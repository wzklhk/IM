package net.zpavelocity.im.client.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import net.zpavelocity.im.message.JsonMsg;

@ChannelHandler.Sharable
public class SimpleJsonMsgHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        JsonMsg jsonMsg = new JsonMsg(1, 1 + "test");
        System.out.println(jsonMsg);
        String cmd = jsonMsg.convertToJson();
        ctx.writeAndFlush(Unpooled.copiedBuffer(cmd, CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

    }
}
