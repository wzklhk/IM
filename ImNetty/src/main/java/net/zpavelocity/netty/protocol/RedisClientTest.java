package net.zpavelocity.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class RedisClientTest {
    public static void main(String[] args) {
        final byte[] LINE = {13, 10};
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(worker)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("192.168.0.111", 6379))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf = ctx.alloc().buffer();
                                    buf.writeBytes("*3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("set".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$5".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("hello".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$5".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("world".getBytes());
                                    buf.writeBytes(LINE);

                                    ctx.writeAndFlush(buf);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf) msg;
                                    System.out.println(buf.toString(Charset.defaultCharset()));
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    ctx.channel().close();
                                }
                            });
                        }
                    });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();

        } catch (Exception e) {

        }

    }
}
