package net.zpavelocity.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.zpavelocity.im.codec.MessageCodec;
import net.zpavelocity.im.server.handler.SimpleBroadcastServerHandler;
import net.zpavelocity.im.server.handler.SimpleSignInServerHandler;

import java.net.InetSocketAddress;

public class SignInServerTest {
    private final int PORT;

    public SignInServerTest(int PORT) {
        this.PORT = PORT;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(PORT))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new MessageCodec());
                            ch.pipeline().addLast(new SimpleSignInServerHandler());
                            ch.pipeline().addLast(new SimpleBroadcastServerHandler());
                        }
                    });
            ChannelFuture f = b.bind().sync();
            System.out.println("Server started with " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 21010;
        SignInServerTest server = new SignInServerTest(port);

        server.start();
    }
}
