package net.zpavelocity.java.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class EchoServer {
    public static void main(String[] args) throws Exception {
        // establish server socket
        ServerSocket s = new ServerSocket(21010);
        // wait for client connection
        Socket incoming = s.accept();
        InputStream inStream = incoming.getInputStream();
        OutputStream outStream = incoming.getOutputStream();

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
    }
}

