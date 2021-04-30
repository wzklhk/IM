package net.zpavelocity.im.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    public ServerHandler() {
        super();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " send message: " + in.toString(CharsetUtil.UTF_8));
        System.out.println(str);

        ctx.channel().writeAndFlush(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelReadComplete. ");
        System.out.println(str);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelRegistered. ");
        System.out.println(str);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelUnregistered. ");
        System.out.println(str);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelActive. ");
        System.out.println(str);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelInactive. ");
        System.out.println(str);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " userEventTriggered. ");
        System.out.println(str);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        String str = String.format(
                "[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "]"
                        + " Client " + ctx.channel().remoteAddress()
                        + " channelWritabilityChanged. ");
        System.out.println(str);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
