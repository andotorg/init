package org.andot.share.linedot.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.andot.share.linedot.core.handler.DiscardServerHandler;
import org.andot.share.linedot.core.handler.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * netty 服务端
 * @author andot
 */
@Slf4j
@Component("lineDotServer")
public class LineDotServer {
    private static final AttributeKey<Object> SERVER_NAME_KEY = AttributeKey.newInstance("linedot_server");
    private static final String SERVER_NAME_VALUE = "linedot_netty_server";
    public static final AttributeKey<Object> CLIENT_KEY = AttributeKey.newInstance("linedot_client");
    public static final String CLIENT_VALUE = "linedot_netty_client";

    @Value("${linedot.server.port:8000}")
    private int PORT;
    @Value("${linedot.reconnect.count:3}")
    private int RECONNECT_COUNT;
    private int CURRENT_RECONNECT_COUNT = 0;
    @Value("${linedot.client.path:/ws/conn}")
    private String webSocketPath;
    private static final String WEBSOCKET_PROTOCOL = "WebSocket";

    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * 启动服务器
     */
    public void startup () {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<ServerSocketChannel>() {
                    @Override
                    protected void initChannel(ServerSocketChannel ch) throws Exception {
                        log.info("服务端服务启动中！");
                        log.info("服务端服务连接 " + ch.attr(SERVER_NAME_KEY).get());
                    }
                })
                .attr(SERVER_NAME_KEY, SERVER_NAME_VALUE)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childAttr(CLIENT_KEY, CLIENT_VALUE)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        log.info("服务端子服务连接 " +  ch.attr(CLIENT_KEY).get());
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("httpCodec", new HttpServerCodec());
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        pipeline.addLast(new WebSocketServerProtocolHandler("/conn"));
                        pipeline.addLast(new DiscardServerHandler());
                    }
                });

        //bind(serverBootstrap, PORT);
        try {
            ChannelFuture future = serverBootstrap.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    /**
     * 端口绑定
     * @param serverBootstrap
     * @param port
     */
    public void bind (ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener((Future<? super Void> future) -> {
            if (future.isSuccess()) {
                log.info("端口 [ " + port + " ] LineDot服务启动成功！");
            } else {
                log.info("端口 [ " + port + " ] LineDot服务启动失败！");
                if (CURRENT_RECONNECT_COUNT < RECONNECT_COUNT) {
                    log.info("现在开始重试端口 [ " + (port+1)+ " ] 进行LineDot服务启动...");
                    CURRENT_RECONNECT_COUNT++;
                    bind(serverBootstrap, port+1);
                } else {
                    log.info("服务端启动重试已经超过 [ " + RECONNECT_COUNT + " ] 次，LineDot服务启动失败！");
                }
            }
        });
    }

}
