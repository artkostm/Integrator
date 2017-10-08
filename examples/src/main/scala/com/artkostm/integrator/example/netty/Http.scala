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

import io.netty.handler.stream.{ChunkedFile, ChunkedNioFile, ChunkedWriteHandler}
import java.io.RandomAccessFile

import com.artkostm.integrator.transport.SslChannelInitializer
import io.netty.util.ReferenceCountUtil
import io.netty.handler.ssl.SslHandler

import scala.util.control.NonFatal

@Sharable
class HttpStaticFileRequestHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    val file = new File("/Users/arttsiom.chuiko/git/Integrator/examples/static.pdf")
    val headers = new DefaultHttpHeaders(true)
    headers.add(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED)
    headers.add(HttpHeaderNames.CONTENT_TYPE, "application/pdf")
    headers.add("Date", System.currentTimeMillis)
    headers.add("Last-Modified", System.currentTimeMillis)
    val resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, headers)
    val raf = new RandomAccessFile(file, "r")
    if (msg.isInstanceOf[HttpRequest]) {
      val req = msg.asInstanceOf[HttpRequest]
      val range = req.headers.get(HttpHeaderNames.RANGE)

      println(s"RANGE: $range")

      val (offset, length) = HttpDuplex.getRangeFromRequest(range) match {
        case None =>
          (0L, raf.length)  // 0L is for avoiding "type mismatch" compile error

        case Some((startIndex, endIndex)) =>
          val endIndex2 = if (endIndex >= 0) endIndex else raf.length - 1
          resp.setStatus(HttpResponseStatus.PARTIAL_CONTENT)
          resp.headers.set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES)
          resp.headers.set(HttpHeaderNames.CONTENT_RANGE, "bytes " + startIndex + "-" + endIndex2 + "/" + raf.length)
          (startIndex, endIndex2 - startIndex + 1)
      }

      HttpUtil.setContentLength(resp, length)

      ctx.write(resp)
      if (ctx.pipeline().get(classOf[SslHandler]) == null) {
        val region = new DefaultFileRegion(raf.getChannel(), 0, file.length())
        ctx.write(region)
      } else {
        ctx.write(new HttpChunkedInput(new ChunkedNioFile(raf.getChannel, offset, length, 8192))).addListener(new ChannelFutureListener {
          def operationComplete(f: ChannelFuture) { raf.close() }
        })
      }
      val future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT, ctx.voidPromise())
      if (msg.isInstanceOf[HttpRequest] && !HttpUtil.isKeepAlive(msg.asInstanceOf[HttpRequest])) {
        future.addListener(ChannelFutureListener.CLOSE)
      }
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

  private[netty] def getRangeFromRequest(spec: String): Option[(Long, Long)] = {
    try {
      if (spec == null) {
        None
      } else {
        if (spec.length <= 6) {
          None
        } else {
          val range = spec.substring(6)  // Skip "bytes="
          val se    = range.split('-')
          if (se.length == 2) {
            val s = se(0).toLong
            val e = se(1).toLong
            Some((s, e))
          } else if (se.length != 1) {
            None
          } else {
            val s = se(0).toLong
            val e = -1L
            Some((s, e))
          }
        }
      }
    } catch {
      case NonFatal(e) =>
        println("Unsupported Range spec: " + spec)
        None
    }
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
            ch.pipeline().addLast(new ChunkedWriteHandler())
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

