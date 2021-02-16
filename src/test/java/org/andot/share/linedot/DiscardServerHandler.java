package org.andot.share.linedot;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.andot.share.linedot.util.StringToByteBufUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;


public class DiscardServerHandler extends SimpleChannelInboundHandler {

    static ConcurrentHashMap<String, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        try {
            String dataStr = new String(bytes, "UTF-8");
            JSONObject data = JSONObject.parseObject(dataStr);
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.pipeline().channel().remoteAddress();
            System.out.println(ctx.name() + "--- Port:" + inetSocketAddress.getPort() + " Time:" + data);


            String fromId = data.getString("fromId");
            if (!channelMap.containsKey(fromId)) {
                channelMap.put(fromId, ctx);
            }

            String id = data.getString("toId");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromId", "0");
            if (channelMap.containsKey(id)) {
                channelMap.get(id).channel().writeAndFlush(StringToByteBufUtil.toByteBuf(data));
                jsonObject.put("msg", "发送成功！");
            } else {
                jsonObject.put("msg", "发送失败，好友未登录！");
            }
            ctx.channel().writeAndFlush(StringToByteBufUtil.toByteBuf(jsonObject));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
