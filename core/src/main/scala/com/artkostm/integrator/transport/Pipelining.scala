package com.artkostm.integrator.transport

import io.netty.handler.codec.http.HttpRequest
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.handler.codec.http.HttpUtil
import io.netty.channel.ChannelFutureListener

object Pipelining {
  def keepAlivePipelining(request: HttpRequest, channel: Channel, 
      channelFuture: ChannelFuture) {
    if (HttpUtil.isKeepAlive(request)) channelFuture.addListener(
        new ChannelFutureListener {
          def operationComplete(future: ChannelFuture) { resumeReading(channel) }
        })
    else channelFuture.addListener(ChannelFutureListener.CLOSE)
  }
  
  def resumeReading(channel: Channel) {
    // We don't have to call channel.read() because setAutoRead also calls
    // channel.read() if not reading
    channel.config.setAutoRead(true)
  }
  
  def pauseReading(channel: Channel) {
    channel.config.setAutoRead(false)
  }
}