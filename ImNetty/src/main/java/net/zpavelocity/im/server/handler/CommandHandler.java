package net.zpavelocity.im.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.zpavelocity.im.server.session.Channels;

import java.util.Scanner;

public class CommandHandler extends ChannelInboundHandlerAdapter {
    public CommandHandler() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String cmd = scanner.nextLine();
                System.out.print(cmd + ": \n");
                if (cmd.equals("exit"))
                    return;
                else if (cmd.equals("list")) {
                    for (Channel channel : Channels.getChannelArrayList()) {
                        System.out.println(channel);
                    }
                } else if (cmd.equals("?")) {
                    System.out.println("all commands");
                } else {
                    System.out.println("Unknown command. Try ? for a list of commands");
                }
            }
        }, "cmd").start();
    }
}
