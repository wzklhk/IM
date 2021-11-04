package net.zpavelocity.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception {
        final int port = 21010;
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        Selector selector = Selector.open();
        // bind
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        // non-blocking
        serverSocketChannel.configureBlocking(false);
        // 把 serverSocketChannel 注册到 selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {

            // 没事件发生
            if (selector.select(1000) == 0) {
                System.out.println("no connect in 1s");
                continue;
            }

            // 获取SelectionKey
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            // 遍历set
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    // 生成SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("A client is connected " + socketChannel.hashCode());

                    // 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    // 关联一个Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println("Client: " + new String(buffer.array()));
                }
                keyIterator.remove();
            }


        }


    }
}
