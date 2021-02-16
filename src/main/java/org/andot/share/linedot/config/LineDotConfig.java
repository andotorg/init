package org.andot.share.linedot.config;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * netty 服务配置
 * @author andot
 */
public class LineDotConfig {
    private LineDotConfig () {}

    /**
     * 用户同道存储
     */
    private static Map<String, String> userIdMap = new ConcurrentHashMap<>(128);
    private static Map<String, ChannelHandlerContext> getUserChannelMap = new ConcurrentHashMap<>(128);
    private static Map<String, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>(128);

    public static Map<String, String> getUserIdMap() {
        return userIdMap;
    }

    public static Map<String, ChannelHandlerContext> getUserChannelMap() {
        return getUserChannelMap;
    }

    public static Map<String, ChannelHandlerContext> getChannelMap() {
        return channelMap;
    }
}
