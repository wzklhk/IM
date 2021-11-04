package net.zpavelocity.im.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.zpavelocity.im.message.UnicastMessage;
import net.zpavelocity.im.server.session.Users;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleUnicastServerHandler extends SimpleChannelInboundHandler<UnicastMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UnicastMessage msg) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " User " + msg.getFromUser()
                        + " unicast to " + msg.getToUser()
                        + " message: " + msg.getMessage());
        System.out.println(str);

        Channel userChannel = Users.getUser(msg.getToUser()).getUserChannel();
        userChannel.writeAndFlush(msg);
    }
}
