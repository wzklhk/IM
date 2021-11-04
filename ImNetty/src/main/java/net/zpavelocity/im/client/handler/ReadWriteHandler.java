package net.zpavelocity.im.client.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

public class ReadWriteHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("send: ");
                String cmd = scanner.nextLine();
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
}
