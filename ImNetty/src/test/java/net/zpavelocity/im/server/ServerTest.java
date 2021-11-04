package net.zpavelocity.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.zpavelocity.im.server.handler.ConnectHandler;
import net.zpavelocity.im.server.handler.SimpleBroadcastServerHandler;

import java.net.InetSocketAddress;

public class ServerTest {
    private final int PORT;

    public ServerTest(int PORT) {
        this.PORT = PORT;
    }

    public static void main(String[] args) throws Exception {
        int port = 21010;
        ServerTest server = new ServerTest(port);

        server.start();
    }

    public void start() throws Exception {
        ConnectHandler connectHandler = new ConnectHandler();
        SimpleBroadcastServerHandler simpleBroadcastHandler = new SimpleBroadcastServerHandler();

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
                            ch.pipeline().addLast(
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            System.out.println("active1");
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            System.out.println("inactive1");
                                        }
                                    }
                            );
                            ch.pipeline().addLast(
                                    new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            System.out.println("active2");
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                            System.out.println("inactive2");
                                        }
                                    }
                            );
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
