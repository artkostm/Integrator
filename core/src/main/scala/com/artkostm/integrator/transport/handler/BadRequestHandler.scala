package com.artkostm.integrator.transport.handler

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.HttpMessage
import io.netty.util.ReferenceCountUtil

@Sharable
private[transport] object BadRequestHandler extends SimpleChannelInboundHandler[HttpMessage] {

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpMessage): Unit =
    if (!msg.decoderResult().isFailure) ctx.fireChannelRead(msg)
    else {
      ReferenceCountUtil.release(msg)
      handleException(ctx, msg.decoderResult().cause())
    }

  private[this] def handleException(ctx: ChannelHandlerContext, throwable: Throwable): Unit = {
    val response = exceptionToResponse(throwable)

  }

  def exceptionToResponse(throwable: Throwable) = ???
}
