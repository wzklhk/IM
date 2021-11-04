package net.zpavelocity.netty.bytebuf;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println(buf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append('a');
        }
        buf.writeBytes(sb.toString().getBytes());
        System.out.println(buf);
    }
}
