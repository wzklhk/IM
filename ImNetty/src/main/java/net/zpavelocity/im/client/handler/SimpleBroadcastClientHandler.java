package net.zpavelocity.im.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.zpavelocity.im.message.BroadcastMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleBroadcastClientHandler extends SimpleChannelInboundHandler<BroadcastMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BroadcastMessage msg) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " User " + msg.getFromUser()
                        + " broadcast message: " + msg.getMessage());
        System.out.println(str);
    }
}
