package org.andot.share.linedot.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 客户端启动器
 * @author andot
 */
@Slf4j
@Component("lineDotClient")
public class LineDotClient {

    @Value("${linedot.server.host}")
    private String HOST = "127.0.0.1";
    @Value("${linedot.server.port}")
    private int PORT = 8000;

    private AttributeKey<String> CLIENT_KEY;

    /**
     * 获取连接
     * @param lineId
     * @return
     */
    public Channel connect (String lineId) {
        CLIENT_KEY = AttributeKey.newInstance(lineId);
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .attr(CLIENT_KEY, lineId)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });
        Channel channel = bootstrap.connect(HOST, PORT).channel();
        log.info("客户端连接成功！");
        return channel;
    }
}
