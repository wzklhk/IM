package net.zpavelocity.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannelReadWrite {
    public static void main(String[] args) throws Exception {
        String filePath1 = "testfile1.txt";
        String filePath2 = "testfile2.txt";

        FileInputStream fileInputStream = new FileInputStream(filePath1);
        FileChannel fileChannel1 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream(filePath2);
        FileChannel fileChannel2 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        while (true) {

            // clean buffer
            byteBuffer.clear();

            int read = fileChannel1.read(byteBuffer);
            if (read == -1) {
                break;
            }

            byteBuffer.flip();
            fileChannel2.write(byteBuffer);


        }

        fileInputStream.close();
        fileOutputStream.close();
    }
}
