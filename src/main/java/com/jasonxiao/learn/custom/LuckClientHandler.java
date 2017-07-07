package com.jasonxiao.learn.custom;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class LuckClientHandler extends SimpleChannelInboundHandler<LuckMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LuckMessage message)
            throws Exception {
        System.out.println(message);
    }
}