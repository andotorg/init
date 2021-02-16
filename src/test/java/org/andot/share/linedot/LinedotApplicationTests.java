package org.andot.share.linedot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.ServerSocket;

@SpringBootTest
class LinedotApplicationTests {

    @Test
    void contextLoads() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.accept();
        } catch (IOException e) {
            System.err.format("连接失败！详细原因：{}", e.getMessage());
        }

    }

}
