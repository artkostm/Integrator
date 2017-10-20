package com.artkostm.integrator.example.netty

import java.net.InetSocketAddress

import cats.instances.option._
import cats.data.Kleisli
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.{ChannelInboundHandlerAdapter, ChannelInitializer}
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.{NioServerSocketChannel, NioSocketChannel}

object Pipeline extends App {

  val initializer = new ChannelInitializer[SocketChannel] {
    override def initChannel(ch: SocketChannel): Unit = {
      val pipeline = ch.pipeline()
      val injector = for {
        handler1 <- Handler1().local[ServerConfig](_.netty)
      } yield pipeline.addLast("first", handler1)

      injector.run(ServerConfig(NettyConfig("1.0")))
      import scala.collection.JavaConverters._
      println(pipeline.toMap.asScala.map(entr => entr._2))
    }
  }

  val group = new NioEventLoopGroup()
  val bootstrap = new ServerBootstrap()
  bootstrap.group(group)
    .channel(classOf[NioServerSocketChannel])
    .localAddress(new InetSocketAddress(8189))
    .childHandler(initializer)
  val chFuture = bootstrap.bind().sync()
  chFuture.channel().closeFuture().sync()
  group.shutdownGracefully().sync()

}

case class ServerConfig(netty: NettyConfig)
case class NettyConfig(version: String)
class Handler1 extends ChannelInboundHandlerAdapter {

}

object Handler1 extends ConfigInjector[Handler1, NettyConfig] {
  def apply() = Kleisli(_ => Option(new Handler1))
}

trait ConfigInjector[Injectee, Config] {
  def apply(): Kleisli[Option, Config, Injectee]
}
