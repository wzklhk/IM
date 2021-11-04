package net.zpavelocity.netty.cast;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class EventLoopTest {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(2);
        /*EventLoopGroup group = new NioEventLoopGroup();*/
        EventLoop next;
        for (int i = 0; i < 10; i++) {
            next = group.next();
            System.out.print(String.valueOf(i) + next);
            System.out.println();
        }
    }
}
