package net.zpavelocity.nio;


import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannelRead {
    public static void main(String[] args) throws Exception {
        String data = "Hello World";
        String filePath = "testfile.txt";

        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        // 通过 fileInputStream 获取对应 FileChannel
        FileChannel fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        // 通过 Channel 读入到 Buffer
        fileChannel.read(byteBuffer);

        System.out.println(new String(byteBuffer.array()));

        fileInputStream.close();
    }
}
