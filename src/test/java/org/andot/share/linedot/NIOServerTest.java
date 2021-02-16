package org.andot.share.linedot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NIOServerTest {
    public static void main(String[] args) {
        Selector selector = null;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(8000));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if (selector.select(1) > 0) {
                    Set<SelectionKey> selectorKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterable = selectorKeys.iterator();
                    while (keyIterable.hasNext()) {
                        SelectionKey selectionKey = keyIterable.next();
                        if (selectionKey.isReadable()) {
                            try {
                                SocketChannel socketChannel = ((SocketChannel)selectionKey.channel());
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                socketChannel.read(byteBuffer);
                                byteBuffer.flip();
                                System.err.println(Charset.defaultCharset().newDecoder().decode(byteBuffer));
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                            } finally {
                                keyIterable.remove();
                                selectionKey.interestOps(SelectionKey.OP_READ);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
