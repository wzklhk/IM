package net.zpavelocity.netty.cast;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class HelloServer {
    public static void main(String[] args) {
        new ServerBootstrap()  // 启动器，负责组装netty组件，启动服务器
                .group(new NioEventLoopGroup())  // group组件
                .channel(NioServerSocketChannel.class)  // 选择服务器 ServerSocketChannel 实现
                .childHandler(  // boss 负责处理连接， worker(child) 负责处理读写
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // 添加具体handler
                                ch.pipeline().addLast(new StringDecoder());  //
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                .bind(21010);
    }
}
