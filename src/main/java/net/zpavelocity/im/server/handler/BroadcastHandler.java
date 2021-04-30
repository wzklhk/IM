package net.zpavelocity.im.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.zpavelocity.im.server.session.Channels;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class BroadcastHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " send message: " + msg);
        System.out.println(str);

        for (Channel channel : Channels.getChannelArrayList()) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(str);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

