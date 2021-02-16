package org.andot.share.linedot;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.andot.share.linedot.util.StringToByteBufUtil;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeServerHandler extends SimpleChannelInboundHandler {

    private String toUserId;
    private String fromUserId;

    public TimeServerHandler (String fromUserId, String toUserId) {
        this.toUserId = toUserId;
        this.fromUserId = fromUserId;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        try {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.pipeline().channel().remoteAddress();
            System.err.println("---------" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "---------");
            JSONObject jsonObject = JSONObject.parseObject(new String(bytes, "UTF-8"));
            if ("0".equals(jsonObject.getString("fromId"))) {
                System.err.println("系统消息: " + jsonObject.getString("msg"));
            } else {
                System.err.println(jsonObject.getString("fromId") + ": " + jsonObject.getString("msg"));
            }

            System.err.println("------------------------------------");

            System.out.println(ctx.name() + "--- Port:" + inetSocketAddress.getPort() + " Time:" + new String(bytes, "UTF-8"));
            System.out.flush();
        } finally {

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        new Thread(()->{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("toId", this.toUserId);
            jsonObject.put("fromId", this.fromUserId);
            jsonObject.put("msg", "Hello " + this.toUserId);
            while (true) {
                ctx.channel().writeAndFlush(StringToByteBufUtil.toByteBuf(jsonObject));
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
