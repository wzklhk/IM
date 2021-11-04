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
import io.netty.util.CharsetUtil;
import net.zpavelocity.im.server.handler.ConnectHandler;
import net.zpavelocity.im.server.handler.SimpleJsonMsgHandler;

import java.net.InetSocketAddress;

public class JsonServerTest {
    private final int PORT;

    public JsonServerTest(int port) {
        this.PORT = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 21010;
        JsonServerTest server = new JsonServerTest(port);

        server.start();

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
//                            ch.pipeline().addLast(new ProtocolFrameDecoder());
                            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new ConnectHandler());
                            ch.pipeline().addLast(new SimpleJsonMsgHandler());

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
