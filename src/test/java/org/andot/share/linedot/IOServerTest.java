package org.andot.share.linedot;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class IOServerTest {
    public static void main(String[] args) {
        // 服务端接受服务变量
        ServerSocket serverSocket = null;
        // 监听到的socket变量
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(8000);
        } catch (IOException e) {
            System.err.format("连接失败！详细原因：{}", e.getMessage());
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                System.err.format("serverSocket 关闭异常！详细原因：{}", ioException.getMessage());
            }
        }

        try {
            socket = serverSocket.accept();
        } catch (Exception ex) {
            System.err.format("消息接受异常！详细原因：{}", ex.getMessage());
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.format("socket 关闭异常！详细原因：{}", e.getMessage());
            }
        }

        try {
            byte[] data = new byte[128];
            InputStream inputStream = socket.getInputStream();
            while (true) {
                int len;
                while ((len = inputStream.read(data)) != -1) {
                    System.err.println(new String(data, 0, len));
                }
            }
        } catch (IOException e) {
            System.err.format("数据包解析异常！详细原因：{}", e.getMessage());
        }
        System.err.println();
    }
}
