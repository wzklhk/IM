package net.zpavelocity.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannelWrite {
    public static void main(String[] args) throws Exception {
        String data = "Hello World";
        String filePath = "testfile.txt";


        // 输出流到Channel
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);

        // 通过 fileOutputStream 获取对应的 FileChannel
        FileChannel fileChannel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(data.getBytes());

        // flip read and write
        byteBuffer.flip();

        fileChannel.write(byteBuffer);

        fileOutputStream.close();
    }
}
