package net.zpavelocity.im.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.zpavelocity.im.server.session.Channels;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channels.getChannelArrayList().add(ctx.channel());
        System.out.println("active");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channels.getChannelArrayList().remove(ctx.channel());
        System.out.println("inactive");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
