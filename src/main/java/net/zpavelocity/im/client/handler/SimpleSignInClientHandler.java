package net.zpavelocity.im.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.zpavelocity.im.message.BroadcastMessage;
import net.zpavelocity.im.message.SignInRequestMessage;
import net.zpavelocity.im.message.SignInResponseMessage;
import net.zpavelocity.im.message.UnicastMessage;

import java.util.Scanner;


public class SimpleSignInClientHandler extends SimpleChannelInboundHandler<SignInResponseMessage> {
    private String username;

    public SimpleSignInClientHandler(String username) {
        this.username = username;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SignInResponseMessage msg) throws Exception {
        System.out.println(msg);
        if (msg.isSignIn()) {
            System.out.println("Succeeded to sign in: " + msg.getReason());
        } else {
            System.out.println("Failed to sign in: " + msg.getReason());
            ctx.channel().close();
            return;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        new Thread(() -> {
            SignInRequestMessage signInRequestMessage = new SignInRequestMessage(this.username);
            System.out.println("Send to sign in: " + signInRequestMessage);
            ctx.writeAndFlush(signInRequestMessage);

            Scanner scanner = new Scanner(System.in);
            String in = "";
            while (true) {
                System.out.print("");
                in = scanner.nextLine();
                System.out.println("input: " + in);
                if (in.equals("exit")) {
                    ctx.channel().close();
                    return;
                } else if (in.equals("send")) {
                    System.out.println("user: ");
                    String toUsername = scanner.nextLine();
                    System.out.println("user: " + toUsername);

                    System.out.println("message: ");
                    String message = scanner.nextLine();
                    System.out.println("message: " + message);

                    ctx.writeAndFlush(new UnicastMessage(message, username, toUsername));
                } else {
                    ctx.writeAndFlush(new BroadcastMessage(in, username));
                }
            }
        }).start();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("inactive");
        return;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
