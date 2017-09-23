import java.net.InetSocketAddress

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.{ByteBuf, DefaultByteBufHolder, PooledByteBufAllocator, Unpooled}
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel._
import io.netty.channel.socket.SocketChannel
import io.netty.util.CharsetUtil

@Sharable
class EchoServerHandler extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    val in = msg.asInstanceOf[ByteBuf]
    println(s"server received: ${in.toString(CharsetUtil.UTF_8)}")
    ctx.write(in, ctx.voidPromise())
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
  def start(): Unit = {
    //DefaultByteBufHolder
    Epoll
    val echoHandler = new EchoServerHandler()
    val group = new NioEventLoopGroup()
    try {
      val bootstrap = new ServerBootstrap()
      bootstrap.group(group)
        .channel(classOf[NioServerSocketChannel])
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //java -Dio.netty.allocator.numDirectArenas=32 -Dio.netty.allocator.numHeapArenas=32
        //.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, 8 * 1024)
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

  start()
}
