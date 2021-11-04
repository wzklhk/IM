package net.zpavelocity.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import net.zpavelocity.im.server.handler.ConnectHandler;
import net.zpavelocity.im.server.handler.SimpleBroadcastServerHandler;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

public class HTTPTest {
    private final int PORT;

    public HTTPTest(int PORT) {
        this.PORT = PORT;
    }

    public static void main(String[] args) throws Exception {
        int port = 21010;
        HTTPTest server = new HTTPTest(port);

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
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                    System.out.println(msg);
                                    System.out.println(msg.uri());
                                    System.out.println(msg.headers());

                                    DefaultFullHttpResponse response =
                                            new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);

                                    byte[] bytes = "<h1>response<h1>".getBytes();
                                    response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                    response.content().writeBytes(bytes);

                                    ctx.writeAndFlush(response);
                                }
                            });

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
