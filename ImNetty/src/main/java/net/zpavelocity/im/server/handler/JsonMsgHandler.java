package net.zpavelocity.im.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.zpavelocity.im.message.JsonMsg;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonMsgHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " send message: " + msg);
        System.out.println(str);
        String json = (String) msg;
        JsonMsg jsonMsg = JsonMsg.parseFromJson(json);
        System.out.println(jsonMsg);
    }
}
