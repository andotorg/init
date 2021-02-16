package org.andot.share.linedot.core.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.andot.share.linedot.config.LineDotConfig;
import org.andot.share.linedot.core.dto.LineDotMessage;
import org.andot.share.linedot.util.HttpRequestParamUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket 服务器端处理类
 * @author andot
 */
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            log.warn("暂时没有适配消息");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest (ChannelHandlerContext ctx,
                                    FullHttpRequest fullHttpRequest) {
        // 如果HTTP解码失败，返回HTTP异常
        if (!fullHttpRequest.decoderResult().isSuccess() ||
                !"websocket".equalsIgnoreCase(fullHttpRequest.headers().get("Upgrade"))) {
            sendHttpResponse(ctx, fullHttpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
        }

        // 加入用户存储
        String channelId = ctx.channel().id().asLongText();
        if (!LineDotConfig.getUserIdMap().containsKey(channelId)) {
            LineDotConfig.getUserIdMap().put(channelId, HttpRequestParamUtil.getLineId(fullHttpRequest.uri()));
            LineDotConfig.getUserChannelMap().put(HttpRequestParamUtil.getLineId(fullHttpRequest.uri()), ctx);
            log.info(LineDotConfig.getUserIdMap().get(channelId) + "已经上线");
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://127.0.0.1:8001/ws/conn", null, false);
        handshaker = wsFactory.newHandshaker(fullHttpRequest);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), fullHttpRequest);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否关闭链路指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof  PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not suppoorted", frame.getClass().getName()));
        }
        // 返回应答消息
        String request = ((TextWebSocketFrame)frame).text();
        LineDotMessage data = JSONObject.parseObject(request, LineDotMessage.class);

        String lineId = data.getHeader().getToLineId();

        if (LineDotConfig.getUserChannelMap().containsKey(lineId)) {
            LineDotConfig.getUserChannelMap().get(lineId).channel().writeAndFlush(new TextWebSocketFrame(request));
        }

        log.info(lineId + "消息收到：" + request);
        ctx.channel().write(new TextWebSocketFrame(data.getBody().getContent() + " Welcome to Netty WebSocket Service!"));

    }

    private static void sendHttpResponse (ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // 返回应答给客户端消息
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), StandardCharsets.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        ChannelFuture future = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        if (LineDotConfig.getChannelMap().containsKey(channelId)) {
            LineDotConfig.getChannelMap().put(channelId, ctx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        if (LineDotConfig.getUserIdMap().containsKey(channelId)) {
            log.info(LineDotConfig.getUserIdMap().get(channelId) + "已经离线");
            LineDotConfig.getUserIdMap().remove(channelId);
        }
        if (LineDotConfig.getChannelMap().containsKey(channelId)) {
            LineDotConfig.getChannelMap().remove(channelId);
        }
    }
}
