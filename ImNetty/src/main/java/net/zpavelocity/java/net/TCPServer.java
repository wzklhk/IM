package net.zpavelocity.java.net;

import java.net.InetAddress;
import java.net.ServerSocket;

public class TCPServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(21010, 100, InetAddress.getByName("localhost"));
        System.out.println("Server Start socket: " + serverSocket);

        serverSocket.close();

    }
}
