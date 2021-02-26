package com.abners.nettyrpc.common.coder;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 类JSONDecoder.java的实现描述：T
 *
 * @author baoxing.peng 2021年02月25日 11:02:42
 */
public class JSONDecoder extends LengthFieldBasedFrameDecoder {


    public JSONDecoder() {
        super(65535, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buf = (ByteBuf) super.decode(ctx, in);
        if (buf == null) {
            return null;
        }
        int dataLen = buf.readableBytes();
        byte[] bytes = new byte[dataLen];
        buf.readBytes(bytes);
        Object reads = JSON.parse(bytes);

        return reads;
    }
}
