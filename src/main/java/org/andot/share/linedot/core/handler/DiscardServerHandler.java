package org.andot.share.linedot.core.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.andot.share.linedot.config.LineDotConfig;
import org.andot.share.linedot.core.dto.LineDotMessage;
import org.andot.share.linedot.core.dto.LineDotMessageBody;
import org.andot.share.linedot.core.dto.LineDotMessageHeader;
import org.andot.share.linedot.util.HttpRequestParamUtil;


/**
 * 服务端处理器
 * @author andot
 */
@Slf4j
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.err.println();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            String lineId = HttpRequestParamUtil.getLineId(fullHttpRequest.uri());
            if (!LineDotConfig.getUserChannelMap().containsKey(lineId)) {
                LineDotConfig.getUserChannelMap().put(lineId, ctx);
            } else {
                log.info("已经登录过了！");
            }
        } else {
            TextWebSocketFrame request = (TextWebSocketFrame) msg;
            log.info("开始接收"+ request.text());
            LineDotMessage lineDotMessage = JSONObject.parseObject(request.text(), LineDotMessage.class);
            System.err.println(lineDotMessage.getBody().getContent());


            String toLineId = lineDotMessage.getHeader().getToLineId();
            if (LineDotConfig.getUserChannelMap().containsKey(toLineId)) {
                LineDotMessage toLineDotMessage = new LineDotMessage();

                LineDotMessageHeader header = new LineDotMessageHeader();
                header.setLineId(toLineId);
                header.setMsgType(1);
                header.setToLineId(lineDotMessage.getHeader().getLineId());
                toLineDotMessage.setHeader(header);

                LineDotMessageBody body = new LineDotMessageBody();
                body.setContent(lineDotMessage.getBody().getContent());
                lineDotMessage.setBody(body);

                lineDotMessage.setFooter(lineDotMessage.getFooter());

                LineDotConfig.getUserChannelMap().get(toLineId).channel().writeAndFlush(
                        new TextWebSocketFrame(JSONObject.toJSONString(toLineDotMessage)));
            } else {
                log.error("离线了！");
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("接收完成");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("接收异常");
        cause.printStackTrace();
        log.error("服务端接收消息异常出现：", cause.getMessage());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LineDotMessage lineDotMessage = new LineDotMessage();
        LineDotMessageBody body = new LineDotMessageBody();
        body.setContent("登录成功！");
        lineDotMessage.setBody(body);
        ctx.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(lineDotMessage)));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LineDotMessage lineDotMessage = new LineDotMessage();
        LineDotMessageBody body = new LineDotMessageBody();
        body.setContent("退出登录成功！");
        lineDotMessage.setBody(body);
        ctx.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(lineDotMessage)));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LineDotMessage lineDotMessage = new LineDotMessage();
        LineDotMessageBody body = new LineDotMessageBody();
        body.setContent("回复中！");
        lineDotMessage.setBody(body);
        ctx.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(lineDotMessage)));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }
}
