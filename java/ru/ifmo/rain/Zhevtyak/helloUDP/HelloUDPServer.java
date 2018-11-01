package ru.ifmo.rain.Zhevtyak.helloUDP;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPServer implements HelloServer {
    private ExecutorService threadPool;
    private DatagramSocket serverSocket;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: <port number> <number of threads>");
        }
        int port = Integer.parseInt(args[0]);
        int threads = Integer.parseInt(args[1]);
        new HelloUDPServer().start(port, threads);
    }

    @Override
    public void start(int port, int numThreads) {
        threadPool = Executors.newFixedThreadPool(numThreads);
        try {
            serverSocket = new DatagramSocket(port);
            final int maxSizeAns = serverSocket.getReceiveBufferSize();
            for (int i = 0; i < numThreads; i++)
                threadPool.submit(() -> {
                    try {
                        byte[] ansMsg = new byte[maxSizeAns];
                        DatagramPacket ans = new DatagramPacket(ansMsg, ansMsg.length);
                        while (!Thread.interrupted()) {
                            serverSocket.receive(ans);
                            String send = "Hello, " + new String(ans.getData(), 0, ans.getLength(), Charset.forName("UTF-8"));
                            byte[] mes = send.getBytes(Charset.forName("UTF-8"));
                            serverSocket.send(new DatagramPacket(mes, mes.length, ans.getSocketAddress()));
                        }
                    } catch (SocketException e) {
                    } catch (IOException e) {
                        System.err.println("Error while sending or receiving");
                    }
                });

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        threadPool.shutdown();
        serverSocket.close();
    }
}
