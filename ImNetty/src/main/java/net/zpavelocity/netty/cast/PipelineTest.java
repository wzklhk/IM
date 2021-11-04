package net.zpavelocity.netty.cast;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.zpavelocity.im.server.handler.EchoHandler;

import java.net.InetSocketAddress;

public class PipelineTest {
    private final int PORT;

    public PipelineTest(int port) {
        this.PORT = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 21010;
        PipelineTest pipelineTest = new PipelineTest(port);

        pipelineTest.start();


    }

    public void start() throws Exception {
        final EchoHandler ServerHandler = new EchoHandler();
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
                            ch.pipeline().addLast("CIHA1", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("CIHA1");

                                    super.channelRead(ctx, msg);
                                }
                            });
                            ch.pipeline().addLast("CIHA2", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("CIHA2");
                                    super.channelRead(ctx, msg);
                                }
                            });
                            ch.pipeline().addLast("CIHA3", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("CIHA3");
                                    ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server".getBytes()));
                                }
                            });

                            ch.pipeline().addLast("COHA1", new ChannelOutboundHandlerAdapter() {
                                @Override
                                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

                                    System.out.println("COHA1");
                                    super.write(ctx, msg, promise);
                                }
                            });
                            ch.pipeline().addLast("COHA2", new ChannelOutboundHandlerAdapter() {
                                @Override
                                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                    System.out.println("COHA2");
                                    super.write(ctx, msg, promise);
                                }
                            });
                            ch.pipeline().addLast("COHA3", new ChannelOutboundHandlerAdapter() {
                                @Override
                                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                    System.out.println("COHA3");
                                    super.write(ctx, msg, promise);
                                }
                            });
                        }
                    });


            ChannelFuture f = b.bind().sync();
            System.out.println(net.zpavelocity.im.server.Server.class.getName() + " started with " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}


