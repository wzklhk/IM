package net.zpavelocity.netty.cast;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;

public class EmbeddedChannelTest {

    public static void main(String[] args) {
        ChannelInboundHandlerAdapter in1 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println("in1");
                super.channelRead(ctx, msg);
            }
        };
        ChannelInboundHandlerAdapter in2 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                System.out.println("in2");
                super.channelRead(ctx, msg);
            }
        };
        ChannelOutboundHandlerAdapter out1 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                System.out.println("out1");
                super.write(ctx, msg, promise);
            }
        };
        ChannelOutboundHandlerAdapter out2 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                System.out.println("out2");
                super.write(ctx, msg, promise);
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(in1, in2, out1, out2);
        // 模拟入站操作
        channel.writeInbound((ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes())));

        // 模拟出站操作
        channel.writeOutbound((ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes())));

    }
}

