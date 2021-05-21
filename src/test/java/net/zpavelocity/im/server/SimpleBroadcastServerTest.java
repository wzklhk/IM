package net.zpavelocity.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.zpavelocity.im.server.handler.CommandHandler;
import net.zpavelocity.im.server.handler.ConnectHandler;
import net.zpavelocity.im.server.handler.SimpleBroadcastServerHandler;

import java.net.InetSocketAddress;

public class SimpleBroadcastServerTest {
    private final int PORT;

    public SimpleBroadcastServerTest(int PORT) {
        this.PORT = PORT;
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
                            ch.pipeline().addLast(new CommandHandler());
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(connectHandler);
                            ch.pipeline().addLast(simpleBroadcastHandler);
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
        SimpleBroadcastServerTest server = new SimpleBroadcastServerTest(port);

        server.start();
    }
}
