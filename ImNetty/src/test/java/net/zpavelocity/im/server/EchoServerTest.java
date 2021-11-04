package net.zpavelocity.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.zpavelocity.im.server.handler.EchoHandler;

import java.net.InetSocketAddress;

public class EchoServerTest {
    private final int PORT;

    public EchoServerTest(int port) {
        this.PORT = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 21010;
        EchoServerTest server = new EchoServerTest(port);

        server.start();
    }

    public void start() throws Exception {
        EchoHandler echoServerHandler = new EchoHandler();

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
                            ch.pipeline().addLast(echoServerHandler);
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
}
