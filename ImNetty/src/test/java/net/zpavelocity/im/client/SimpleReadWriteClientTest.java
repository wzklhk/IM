package net.zpavelocity.im.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.zpavelocity.im.client.handler.SimpleReadWriteHandler;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SimpleReadWriteClientTest {
    private final String USERNAME;
    private final String HOST;
    private final int PORT;

    public SimpleReadWriteClientTest(String USERNAME, String HOST, int PORT) {
        this.USERNAME = USERNAME;
        this.HOST = HOST;
        this.PORT = PORT;
    }

    public static void main(String[] args) throws Exception {
        String username = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // 以当前时间为用户名
        System.out.println(username);
        String host = "localhost";
        int port = 21010;
        new SimpleReadWriteClientTest(username, host, port).start();

    }

    public void start() throws Exception {
        final SimpleReadWriteHandler simpleReadWriteHandler = new SimpleReadWriteHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Scanner scanner = new Scanner(System.in);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(HOST, PORT))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(simpleReadWriteHandler);
                        }
                    });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }
}

