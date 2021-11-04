package net.zpavelocity.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class WriteReadTest {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);
        System.out.println("Bytebuf: " + buffer);
        buffer.writeBytes(new byte[]{1, 2, 3, 4});

        getByteBuf(buffer);
        System.out.println("动作：取数据 ByteBuf" + buffer);

        readByteBuf(buffer);
        System.out.println("动作：读完 ByteBuf" + buffer);

    }

    //读取一个字节
    private static void readByteBuf(ByteBuf buffer) {
        while (buffer.isReadable()) {
            System.out.println("读取一个字节:" + buffer.readByte());
        }
    }


    //读取一个字节，不改变指针
    private static void getByteBuf(ByteBuf buffer) {
        for (int i = 0; i < buffer.readableBytes(); i++) {
            System.out.println("读取一个字节:" + buffer.getByte(i));
        }
    }
}
