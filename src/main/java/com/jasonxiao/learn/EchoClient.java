/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.jasonxiao.learn;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.ArrayList;
import java.util.List;


/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
public class EchoClient {
    List<Channel> channels = new ArrayList<Channel>();


    public void connect(int port, String host) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new EchoClientHandler());
                    }
                });

            // 发起异步连接操作
            Channel c = b.connect(host, port).sync().channel();
            channels.add(c);
            // 等待服务端监听端口关闭
            c.closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }


    public void send(String msg) {
        channels.get(0).writeAndFlush(Unpooled.copiedBuffer((msg + "1$_").getBytes()));

    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 8088;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        EchoClient echoClient = new EchoClient();
        echoClient.connect(port, "127.0.0.1");
        echoClient.send("发送一个消息呢");
    }
}
