package net.zpavelocity.im.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.zpavelocity.im.message.SignInRequestMessage;
import net.zpavelocity.im.message.SignInResponseMessage;
import net.zpavelocity.im.server.session.Channels;
import net.zpavelocity.im.server.session.Users;
import net.zpavelocity.im.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleSignInServerHandler extends SimpleChannelInboundHandler<SignInRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SignInRequestMessage msg) throws Exception {
        String username = msg.getUsername();

        SignInResponseMessage signInResponseMessage;
        if (Users.isExist(username)) {
            signInResponseMessage = new SignInResponseMessage(false, "user is signed in. ");
            ctx.writeAndFlush(signInResponseMessage);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        } else {
            Users.getUserArrayList().add(new User(username, ctx.channel()));
            signInResponseMessage = new SignInResponseMessage(true, "OK");
            ctx.writeAndFlush(signInResponseMessage);
        }

        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " User " + username
                        + " request to sign in: "
                        + signInResponseMessage.isSignIn()
                        + " reason: "
                        + signInResponseMessage.getReason());
        System.out.println(str);
    }

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
        User signOutUser = Users.getUser(ctx.channel());
        Users.getUserArrayList().remove(signOutUser);
        String str1 = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " User " + signOutUser.getUsername()
                        + " is signed out. ");
        System.out.println(str1);

        for (Channel channel : Channels.getChannelArrayList()) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(str1);
            }
        }

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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
