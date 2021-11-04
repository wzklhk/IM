package net.zpavelocity.im.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.zpavelocity.im.message.JsonMsg;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class SimpleJsonMsgHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " send message: " + msg);
        System.out.println(str);
        JsonMsg jsonMsg = JsonMsg.parseFromJson(msg);
        System.out.println(jsonMsg);
    }
}
