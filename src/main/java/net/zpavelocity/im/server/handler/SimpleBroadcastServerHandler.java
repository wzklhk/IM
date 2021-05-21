package net.zpavelocity.im.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.zpavelocity.im.message.BroadcastMessage;
import net.zpavelocity.im.server.session.Channels;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class SimpleBroadcastServerHandler extends SimpleChannelInboundHandler<BroadcastMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BroadcastMessage msg) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " User " + msg.getFromUser()
                        + " broadcast message: " + msg.getMessage());
        System.out.println(str);

        for (Channel channel : Channels.getChannelArrayList()) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(msg);
            }
        }
    }
}
