package net.zpavelocity.im.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.zpavelocity.im.server.session.Channels;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class ConnectHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channels.getChannelArrayList().add(ctx.channel());

        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelActive. ");
        System.out.println(str);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channels.getChannelArrayList().remove(ctx.channel());

        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelInactive. ");
        System.out.println(str);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
