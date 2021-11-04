package net.zpavelocity.nio;

import java.nio.IntBuffer;

public class BufferTest {
    public static void main(String[] args) {
        // Create a buffer, size is 5
        IntBuffer intBuffer = IntBuffer.allocate(5);

        // Storage data in buffer
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(2 * i);
        }

        // Flip read and write
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }

    }
}
