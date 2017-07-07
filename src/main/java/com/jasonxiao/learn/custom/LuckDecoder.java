package com.jasonxiao.learn.custom;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;


public class LuckDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 可读取字节的大小,
        // int i = in.readableBytes();
        // 获取协议的版本
        int version = in.readInt();
        // 获取消息长度
        int contentLength = in.readInt();
        // 获取SessionId
        byte[] sessionByte = new byte[36];
        in.readBytes(sessionByte);
        String sessionId = new String(sessionByte);

        // 从第index序号开始读取数据到byte直接数组中
        // byte[] session = new byte[36];
        // in.getBytes(8, session);
        // String sessionId1 = new String(session);

        // 组装协议头
        LuckHeader header = new LuckHeader(version, contentLength, sessionId);

        // 读取消息内容
        byte[] content = new byte[contentLength];
        in.readBytes(content);

        LuckMessage message = new LuckMessage(header, new String(content));

        out.add(message);
    }
}
