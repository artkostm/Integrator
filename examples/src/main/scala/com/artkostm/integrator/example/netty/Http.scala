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

@Sharable
class TestRequestHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    msg match {
      case m: HttpRequest =>
        print(m.method().name())
        if (m.method() == HttpMethod.GET) {
          val headers = new DefaultHttpHeaders(true)
          headers.add(HttpHeaderNames.LOCATION, "https://google.com")
          val resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            HttpResponseStatus.MOVED_PERMANENTLY,
            Unpooled.buffer(0),
            headers,
            EmptyHttpHeaders.INSTANCE
          )

          ctx.writeAndFlush(resp)
        }
        println(s" ${m.uri()}")
        println(s"${m.headers().entries().asScala.map(h => h.getKey -> h.getValue)}")
        ctx.fireChannelRead(msg)
      case m: HttpContent if (m.isInstanceOf[LastHttpContent]) => 
        println("=========LAST")
        ctx.fireChannelRead(msg)
      case m: HttpContent => 
         println(s"=========ALERT")
        ctx.fireChannelRead(msg)
//        println(s"Content: ${ByteBufUtil.prettyHexDump(m.content())}")
       
      case m =>
        println(m.getClass)
        ctx.fireChannelRead()
    }
  }

//  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
//    //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE)
//    //ctx.fireChannelReadComplete()
//  }

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
        val headers = new DefaultHttpHeaders(true)
          headers.add(HttpHeaderNames.LOCATION, "https://google.com")
          val resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            HttpResponseStatus.CREATED,
            Unpooled.EMPTY_BUFFER,
            headers,
            EmptyHttpHeaders.INSTANCE
          )
        
          ctx.write(resp, ctx.voidPromise())
          ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE)
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
object HttpDuplex extends ChannelDuplexHandler {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any) {
    println(s"=========DUPLEX(CR): $msg - class ${msg.getClass.getName}")
    ctx.fireChannelRead(msg)
  }
  
  override def write(ctx: ChannelHandlerContext, msg: scala.Any, promise: ChannelPromise) {
    println(s"=========DUPLEX(W): $msg - class ${msg.getClass.getName}, promise - $promise - ${promise.getClass.getName}")
    ctx.writeAndFlush(msg, promise)
  }
}

object ServerApp extends App {
  val group = new NioEventLoopGroup
  val allChannels = new DefaultChannelGroup(group.next())

  def start(): Unit = {
    val testHandler = new TestRequestHandler()
//    val testHandler2 = new TestRequestHandler2()
    try {
      val bootstrap = new ServerBootstrap()
      bootstrap.group(group)
        .channel(classOf[NioServerSocketChannel])
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024, 32 * 1024))
        .localAddress(new InetSocketAddress(8189))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel) = {
            ch.pipeline().addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192))
            ch.pipeline().addLast("encoder", new HttpResponseEncoder())
            ch.pipeline().addLast("decompressor", new HttpContentDecompressor())
            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.TRACE))
            //ch.pipeline().addLast("http-handler", new HttpStreamsServerHandler(Seq[ChannelHandler](testHandler).asJava))
            ch.pipeline().addLast("request-handler", testHandler)
            ch.pipeline().addLast("request-duplex", HttpDuplex)
            ch.pipeline().addLast("request-handler2", TestRequestHandler2)
          }
        })
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

