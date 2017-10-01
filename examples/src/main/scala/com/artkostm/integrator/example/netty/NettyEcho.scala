import java.net.InetSocketAddress

import akka.actor.ActorSystem
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer._
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel._
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpServerUpgradeHandler
import io.netty.util.CharsetUtil

import scala.concurrent.duration.Duration
import scala.concurrent._

@Sharable
class EchoServerHandler(implicit val dispatcher: ExecutionContextExecutor) extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {

    val in = msg.asInstanceOf[ByteBuf]
    println(s"server received: ${in.toString(CharsetUtil.UTF_8)}")
    val promise = Promise[Unit]()
    promise.future onComplete {
      res => println("From future1: " + res)
    }

    promise.future onComplete {
      res => println("From future2: " + res)
    }

    promise.future onComplete {
      res => println("From future3: " + res)
    }
    ctx.write(in).addListener((future: ChannelFuture) => {
      if (future.isSuccess) promise.success()
      else promise.failure(future.cause())
    })


    //ctx.fireChannelRead()
//    Future {
//      val in = msg.asInstanceOf[ByteBuf]
//      ctx.write(in, ctx.voidPromise())
//      s"server received: ${in.toString(CharsetUtil.UTF_8)}"
//    } onComplete {
//      res => println(res)
//    }
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

  override def channelActive(ctx: ChannelHandlerContext): Unit = super.channelActive(ctx)
}

object Server extends App {
  val system = ActorSystem()
  implicit val dispatcher = system.dispatcher
  val group = new NioEventLoopGroup(4, system.dispatcher)
  val allChannels = new DefaultChannelGroup(group.next())


  def start(): Unit = {
    //DefaultByteBufHolder
    //Epoll
    val echoHandler = new EchoServerHandler()

    try {
      val bootstrap = new ServerBootstrap()
      bootstrap.group(group)
        .channel(classOf[NioServerSocketChannel])
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //java -Dio.netty.allocator.numDirectArenas=32 -Dio.netty.allocator.numHeapArenas=32
        .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024, 32 * 1024))
        .localAddress(new InetSocketAddress(8080))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel) = ch.pipeline().addLast(echoHandler)
        })
      val chFuture = bootstrap.bind().sync()
      chFuture.channel().closeFuture().sync()
    } finally {
      group.shutdownGracefully().sync()
    }
  }

  val buf = Unpooled.copiedBuffer("Hello bitch!".getBytes)
  println(ByteBufUtil.prettyHexDump(buf))
  println(new String(ByteBufUtil.decodeHexDump("48656c6c6f20626974636821")))
  //  io.netty.handler.codec.http.HttpResponseStatus.SWITCHING_PROTOCOLS
  //  io.netty.handler.codec.http.HttpServerExpectContinueHandler
  println(Await.result(Future { "Hello!" }, Duration.Inf))
//  HttpServerUpgradeHandler
  start()
}
