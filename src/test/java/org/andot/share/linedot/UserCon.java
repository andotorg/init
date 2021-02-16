package org.andot.share.linedot;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

public class UserCon {
    private static ConcurrentHashMap<String, ChannelHandlerContext> contextConcurrentHashMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ChannelHandlerContext> getContextConcurrentHashMap() {
        return contextConcurrentHashMap;
    }

    public static void setContextConcurrentHashMap(ConcurrentHashMap<String, ChannelHandlerContext> contextConcurrentHashMap) {
        UserCon.contextConcurrentHashMap = contextConcurrentHashMap;
    }
}
