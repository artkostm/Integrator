package com.artkostm.integrator.transport

import io.netty.handler.ssl.SslProvider
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.ssl.SslHandler
import io.netty.handler.ssl.SslContextBuilder
import java.io.File

object SslChannelInitializer {
  lazy val context = {
    val provider = ??? //read from config
    SslContextBuilder.forServer(new File("certChainFile"), new File("keyFile")).sslProvider(provider).build()
  }
}

@Sharable
class SslChannelInitializer(nonSslInitializer: ChannelInitializer[SocketChannel]) extends ChannelInitializer[SocketChannel] {
  import SslChannelInitializer._
  override def initChannel(ch: SocketChannel): Unit = {
    val p = ch.pipeline()
    p.addLast(classOf[SslHandler].getName, context.newHandler(ch.alloc()))
    p.addLast(nonSslInitializer)
    
    //here you need to remove all handlers which can't be used with SSL
  }
}