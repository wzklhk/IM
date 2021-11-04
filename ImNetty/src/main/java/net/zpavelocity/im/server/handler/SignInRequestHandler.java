package net.zpavelocity.im.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.zpavelocity.im.message.SignInRequestMessage;
import net.zpavelocity.im.message.SignInResponseMessage;
import net.zpavelocity.im.server.session.Channels;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class SignInRequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channels.getChannelArrayList().add(ctx.channel());

        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelActive. ");
        System.out.println(str);

        for (Channel channel : Channels.getChannelArrayList()) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(str);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channels.getChannelArrayList().remove(ctx.channel());

        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelInactive. ");
        System.out.println(str);

        for (Channel channel : Channels.getChannelArrayList()) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(str);
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " send message: " + msg);
        System.out.println(str);
        if (null == msg
                || !(msg instanceof SignInRequestMessage)) {
            super.channelRead(ctx, msg);
            return;
        } else {
            SignInRequestMessage signInRequestMessage = (SignInRequestMessage) msg;
            String username = signInRequestMessage.getUsername();
            SignInResponseMessage signInResponseMessage = new SignInResponseMessage(true, "success");
            ctx.writeAndFlush(signInResponseMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
