package net.zpavelocity.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class NIOFileChannelCopyFile {
    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");

        FileChannel sourceChannel = fileInputStream.getChannel();
        FileChannel destChannel = fileOutputStream.getChannel();

        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        sourceChannel.close();
        destChannel.close();
        fileInputStream.close();
        fileOutputStream.close();

    }
}
