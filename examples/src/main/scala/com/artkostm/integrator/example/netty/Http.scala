package com.artkostm.integrator.example.netty

import java.net.InetSocketAddress

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.{ByteBufUtil, PooledByteBufAllocator, Unpooled}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel._
import io.netty.handler.codec.http._
import io.netty.handler.logging.{LogLevel, LoggingHandler}

import scala.collection.JavaConverters._
import io.netty.handler.codec.http.multipart.DiskFileUpload
import akka.stream.scaladsl.Source
import org.reactivestreams.Subscriber
import org.reactivestreams.Publisher
import akka.stream.scaladsl.Sink
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.stream.Supervision
import akka.stream.ActorMaterializerSettings
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.io.File

import io.netty.handler.stream.ChunkedNioFile
import java.io.RandomAccessFile

import com.artkostm.integrator.transport.SslChannelInitializer
import io.netty.util.ReferenceCountUtil
import io.netty.handler.stream.ChunkedFile
import io.netty.handler.ssl.SslHandler

@Sharable
class HttpStaticFileRequestHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    
    val file = new File("/Users/arttsiom.chuiko/git/Integrator/examples/static.pdf")
        val headers = new DefaultHttpHeaders(true)
        headers.add(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaders.Values.CHUNKED)
        headers.add(HttpHeaderNames.CONTENT_TYPE, "application/pdf")
        headers.add(HttpHeaderNames.CONTENT_LENGTH, file.length())
        val resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, headers)
        val raf = new RandomAccessFile(file, "r")
        ctx.write(resp)
        if (ctx.pipeline().get(classOf[SslHandler]) == null) {
          val region = new DefaultFileRegion(raf.getChannel(), 0, file.length())
          ctx.writeAndFlush(region).addListener(ChannelFutureListener.CLOSE)
        } else {
          ctx.writeAndFlush(new HttpChunkedInput(new ChunkedNioFile(raf.getChannel, 0, file.length(), 8192)))
            .addListener(ChannelFutureListener.CLOSE)
        }
        
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}

@Sharable
object TestRequestHandler2 extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    msg match {
      case m: HttpRequest =>
        print(m.method().name())
        println(s" ${m.uri()}")
        
        println(s"${m.headers().entries().asScala.map(h => h.getKey -> h.getValue)}")
      case m: HttpContent if (m.isInstanceOf[LastHttpContent]) => 
//        val headers = new DefaultHttpHeaders(true)
//          headers.add(HttpHeaderNames.LOCATION, "https://google.com")
//          headers.add(HttpHeaderNames.CONTENT_TYPE, "application/pdf")
//          val resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
//            HttpResponseStatus.OK,
//            Unpooled.EMPTY_BUFFER,
//            headers,
//            EmptyHttpHeaders.INSTANCE
//          )
//        
//          ctx.write(resp, ctx.voidPromise())
//          ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE)
      ctx.fireChannelRead(msg)
      case m: HttpContent => 
//        println(s"Content: ${ByteBufUtil.prettyHexDump(m.content())}")
        println(s"=========ALERT2")
        //println(s"Content: ${ByteBufUtil.prettyHexDump(m.content())}")
      case m =>
        println(m.getClass)
        ctx.fireChannelRead()
    }
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    //ctx.fireChannelReadComplete()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}

@Sharable
object HttpDuplex extends ChannelDuplexHandler with Publisher[HttpContent] {
  implicit val system = ActorSystem() 
  val decider: Supervision.Decider = {
    case e: Exception =>
      println(s"Exception handled, recovering stream: ${e.getMessage}, exception: $e")
      Supervision.Restart
    case some => 
      println(s"UNEXPECTED: $some")
      Supervision.Stop
  }
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))
  
  val source = Source.fromPublisher(this)
      .map(_.content)
      .to(Sink.foreach(ByteBufUtil.prettyHexDump(_)))
  val result = source.run
  var subscriber: Subscriber[_ >: HttpContent] = _
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any) {
    println(s"=========DUPLEX(CR): $msg - class ${msg.getClass.getName}")
    if (msg.isInstanceOf[HttpContent]) {
      println(s"BEFORE onNext: $msg")
      subscriber.onNext(msg.asInstanceOf[HttpContent])
    }
    ctx.fireChannelRead(msg)
  }
  
  override def write(ctx: ChannelHandlerContext, msg: scala.Any, promise: ChannelPromise) {
    println(s"=========DUPLEX(W): $msg - class ${msg.getClass.getName}, promise - $promise - ${promise.getClass.getName}")
    ctx.writeAndFlush(msg, promise)
  }
  
  override def subscribe(sub: Subscriber[_ >: HttpContent]): Unit = {
    println(s"Subscriber: $sub")
    println(s"SELF: $this")
    subscriber = sub
  }
}

object ServerApp extends App {
  val group = new NioEventLoopGroup
  val allChannels = new DefaultChannelGroup(group.next())

  def start(): Unit = {
    val testHandler = new HttpStaticFileRequestHandler()
    try {
      val bootstrap = new ServerBootstrap()
      bootstrap.group(group)
        .channel(classOf[NioServerSocketChannel])
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024, 32 * 1024))
        .localAddress(new InetSocketAddress(8189))
        .childHandler(new SslChannelInitializer(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel) = {
//            HttpServerExpectContinueHandler
            ch.pipeline().addLast("compressor", new HttpContentCompressor)
            ch.pipeline().addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192))
            ch.pipeline().addLast("encoder", new HttpResponseEncoder())
            ch.pipeline().addLast("decompressor", new HttpContentDecompressor())
            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.TRACE))
            //ch.pipeline().addLast("http-handler", new HttpStreamsServerHandler(Seq[ChannelHandler](testHandler).asJava))
            ch.pipeline().addLast("request-handler", testHandler)
            //ch.pipeline().addLast("request-duplex", HttpDuplex)
            //ch.pipeline().addLast("request-handler2", TestRequestHandler2)
          }
        }))
      val chFuture = bootstrap.bind().sync()
      chFuture.channel().closeFuture().sync()
    } finally {
      group.shutdownGracefully().sync()
      type а = ChannelDuplexHandler
      type b = DiskFileUpload
    }
  }

  start()
}

