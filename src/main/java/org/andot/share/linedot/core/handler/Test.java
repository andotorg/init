package org.andot.share.linedot.core.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class Test extends SimpleChannelInboundHandler {

    private static ConcurrentHashMap<String, Channel> channelConcurrentHashMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = "你好".getBytes(StandardCharsets.UTF_8);
        ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
        ctx.channel().writeAndFlush(byteBuf);
        /*ByteBuf in = (ByteBuf) msg;
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        try {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.pipeline().channel().remoteAddress();
            JSONObject jsonObject = JSONObject.parseObject(new String(bytes, "UTF-8"));
            String userId = jsonObject.getString("userId");

            jsonObject.put("userId", ctx.channel().id().asLongText());
            byte[] data = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
            ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
            if (channelConcurrentHashMap.containsKey(userId)) {
                channelConcurrentHashMap.get(userId).writeAndFlush(byteBuf);
                System.out.println(ctx.name() + "--- Port:" + inetSocketAddress.getPort() + " Time:" + jsonObject.toJSONString());
            } else {
                System.err.println("该用户没有登录");
            }

        } finally {
            //ReferenceCountUtil.release(msg);
        }*/
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String userId = ctx.channel().id().asLongText();
        if (!channelConcurrentHashMap.containsKey(userId)) {
            channelConcurrentHashMap.put(userId, ctx.channel());
            System.err.println(userId + "上线了");
        }

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String userId = ctx.channel().id().asLongText();
        if (!channelConcurrentHashMap.containsKey(userId)) {
            channelConcurrentHashMap.remove(userId);
            System.err.println(userId + "离线");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        while (true) {
            byte[] data = "你好channelActive".getBytes(StandardCharsets.UTF_8);
            ByteBuf byteBuf = Unpooled.buffer(data.length).writeBytes(data);
            ctx.channel().writeAndFlush(byteBuf);
        }
    }
}
