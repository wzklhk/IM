package net.zpavelocity.im.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

@ChannelHandler.Sharable
public class SimpleReadWriteHandler extends SimpleChannelInboundHandler<ByteBuf> {

    // 连接建立触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String cmd = "";
            while (true) {
                System.out.print("send: ");
                cmd = scanner.nextLine();
                System.out.println(cmd);
                if (cmd.equals("exit")) {
                    ctx.channel().close();
                    return;
                } else {
                    ctx.writeAndFlush(Unpooled.copiedBuffer(cmd, CharsetUtil.UTF_8));
                }
            }
        }).start();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        System.out.println("Client received: " + msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}