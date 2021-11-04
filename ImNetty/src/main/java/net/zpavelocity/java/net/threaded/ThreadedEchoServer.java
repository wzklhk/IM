package net.zpavelocity.java.net.threaded;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ThreadedEchoServer {
    public static void main(String[] args) {
        try (ServerSocket s = new ServerSocket(21010)) {
            int i = 1;

            while (true) {
                Socket incomingSocket = s.accept();
                System.out.println("Spawning " + i);
                Runnable r = new ThreadedEchoHandler(incomingSocket);
                Thread t = new Thread(r);
                t.start();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class ThreadedEchoHandler implements Runnable {
    private Socket incomingSocket;

    public ThreadedEchoHandler(Socket incomingSocket) {
        this.incomingSocket = incomingSocket;
    }

    public void run() {
        try (InputStream inStream = incomingSocket.getInputStream();
             OutputStream outStream = incomingSocket.getOutputStream()) {
            Scanner in = new Scanner(inStream, "UTF-8");
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(outStream, "UTF-8"),
                    true /* autoFlush */);

            out.println("Hello! Enter BYE to exit.");

            // echo client input
            boolean done = false;
            while (!done && in.hasNextLine()) {
                String line = in.nextLine();
                out.println("Echo: " + line);
                if (line.trim().equals("BYE"))
                    done = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
