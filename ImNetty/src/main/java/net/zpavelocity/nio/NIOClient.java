package net.zpavelocity.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 21010);

        if (!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("connect failed");
            }
        }

        String str = "hello world";
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        // write buffer in channel
        socketChannel.write(buffer);
        System.in.read();


    }
}
