package org.andot.share.linedot.controller;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.andot.share.linedot.config.LineDotConfig;
import org.andot.share.linedot.core.LineDotClient;
import org.andot.share.linedot.util.StringToByteBufUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 消息发送控制器
 * @author andot
 */
@RestController
@RequestMapping("/notion")
public class SendMessageController {
    @Resource
    private LineDotClient lineDotClient;

    /**
     * 通知消息发送
     * @param lineIds
     * @param msg
     * @return
     */
    @PostMapping("/sendMsg")
    public String sendMsg(@RequestBody List<String> lineIds, String msg) {
        if (lineIds != null && lineIds.size() > 0) {
            if ("0".equals(lineIds.get(0))) {
                // 给所有在线的发消息
                for (Iterator<Map.Entry<String, ChannelHandlerContext>> it = LineDotConfig.getUserChannelMap().entrySet().iterator(); it.hasNext(); ) {
                    it.next().getValue().writeAndFlush(new TextWebSocketFrame(msg));
                }
            } else {
                // 把传入的进行发送消息
                for (String lineId : lineIds) {
                    if (LineDotConfig.getUserChannelMap().containsKey(lineId)) {
                        LineDotConfig.getUserChannelMap().get(lineId).writeAndFlush(new TextWebSocketFrame(msg));
                    } else {
                        return "未上线";
                    }
                }
            }
        }

        return "sucess";
    }

}
