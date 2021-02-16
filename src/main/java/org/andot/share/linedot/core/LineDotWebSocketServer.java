package org.andot.share.linedot.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.andot.share.linedot.core.handler.DiscardServerHandler;
import org.andot.share.linedot.core.handler.WebSocketServerHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * netty 服务端
 * @author andot
 */
@Slf4j
@Component("lineDotWebSocketServer")
public class LineDotWebSocketServer {

    @Value("${linedot.server.port:8000}")
    private int PORT;
    @Value("${linedot.reconnect.count:3}")
    private int RECONNECT_COUNT;
    private int CURRENT_RECONNECT_COUNT = 0;
    @Value("${linedot.client.path:/ws/conn}")
    private String webSocketPath;

    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * 启动服务器
     */
    public void startup () {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            log.info("服务端子服务连接 ");
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("http-codec", new HttpServerCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                            pipeline.addLast("logger", new LoggingHandler(LogLevel.INFO));
                            pipeline.addLast("handler", new WebSocketServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
