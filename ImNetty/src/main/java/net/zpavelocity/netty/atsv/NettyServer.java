package net.zpavelocity.netty.atsv;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        /* 创建 bossGroup 和 workerGroup 两个线程组
         * bossGroup：处理连接请求
         * workerGroup：处理客户端业务
         *
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务器端启动对象（链式编程）
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128) // 线程队列连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 保持连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 通道测试对象（匿名对象）
                        // 给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler()); // 返回
                        }
                    });

            System.out.println("Server is ready. ");

            // 绑定端口并同步，生成ChannelFuture对象
            ChannelFuture channelFuture = b.bind(21010).sync();

            // 关闭通道（异步）
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
