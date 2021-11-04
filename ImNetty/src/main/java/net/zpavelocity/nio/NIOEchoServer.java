package net.zpavelocity.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOEchoServer {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(21010));
            serverSocketChannel.configureBlocking(false);

            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel == null) {
                    System.out.println();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
