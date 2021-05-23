package net.zpavelocity.im.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.zpavelocity.im.client.handler.SimpleBroadcastClientHandler;
import net.zpavelocity.im.client.handler.SimpleSignInClientHandler;
import net.zpavelocity.im.client.handler.SimpleUnicastClientHandler;
import net.zpavelocity.im.codec.MessageCodec;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private final String USERNAME;
    private final String HOST;
    private final int PORT;

    public Client(String USERNAME, String HOST, int PORT) {
        this.USERNAME = USERNAME;
        this.HOST = HOST;
        this.PORT = PORT;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(HOST, PORT))
                    .handler(new ChannelInitializer<SocketChannel>() {
                                 @Override
                                 protected void initChannel(SocketChannel ch) throws Exception {
                                     ch.pipeline().addLast(new MessageCodec());
                                     ch.pipeline().addLast(new SimpleSignInClientHandler(USERNAME));
                                     ch.pipeline().addLast(new SimpleUnicastClientHandler());
                                     ch.pipeline().addLast(new SimpleBroadcastClientHandler());
                                 }
                             }
                    );

            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        String username = new SimpleDateFormat("HHmmss").format(new Date()); // 以当前时间为用户名
//        username = "username";
        System.out.println("username is " + username);
        String host = "localhost";
        int port = 21010;
        new Client(username, host, port).start();

    }
}
