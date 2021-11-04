package net.zpavelocity.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {

    public static void main(String[] args) throws IOException {
        // A Cached Thread Pool
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(21010);
        System.out.println("Server Start. ");

        while (true) {
            // Listen and wait client connect.
            System.out.println("Listening...");

            Socket socket = serverSocket.accept();
            System.out.println("Accept: A client has connected. ");

            newCachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    handler(socket);
                }
            });
        }
    }

    // A handler to communicate with client
    public static void handler(Socket socket) {
        try {
            System.out.println("A client is connected. ");
            System.out.print("Thread id: " + Thread.currentThread().getId());
            System.out.print(", Thread name: " + Thread.currentThread().getName());
            System.out.print("\n");
            byte[] buffer = new byte[1024];
            InputStream inputStream = socket.getInputStream();
            // Read data from client send.
            while (true) {
                System.out.print("Thread id: " + Thread.currentThread().getId());
                System.out.print(", Thread name: " + Thread.currentThread().getName());
                System.out.print("\n");
                int read = inputStream.read(buffer);
                if (read != -1) {
                    System.out.println(new String(buffer, 0, read));// Output
                } else {
                    break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Close connect with client");
            try {
                socket.close();
            } catch (Exception e) {

            }
        }
    }
}
