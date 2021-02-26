package com.abners.nettyrpc.common.coder;

import java.util.List;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * 类JSONEncoder.java的实现描述：TODO 类实现描述
 *
 * @author baoxing.peng 2021年02月25日 11:02:35
 */
public class JSONEncoder extends MessageToMessageEncoder {


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
        ByteBuf bytebuf = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = JSON.toJSONBytes(msg);
        bytebuf.writeInt(bytes.length);
        bytebuf.writeBytes(bytes);

        out.add(bytebuf);

    }

}
