package ru.ifmo.rain.Zhevtyak.helloUDP;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPClient implements HelloClient {

    public static void main(String[] args) {
        if (args.length < 5) {
            System.err.println("Usage: <host> <port number> <prefix> <number of threads> <requests per thread>");
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String prefix = args[2];
        int threads = Integer.parseInt(args[3]);
        int requests = Integer.parseInt(args[4]);
        new HelloUDPClient().run(host, port, prefix, threads, requests);
    }

    @Override
    public void run(String host, int port, String prefix, int numThreads, int requests) {
        InetSocketAddress serverAddress = new InetSocketAddress(host, port);
        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        final int numRequests = requests;
        //List<Thread> threads = new ArrayList<>(numThreads);
        for (int i = 0; i < numThreads; i++) {
            final int k = i;
            threadPool.submit(() -> {
                //if (!Thread.interrupted()) {
                try (DatagramSocket clientSocket = new DatagramSocket()) {
                    clientSocket.setSoTimeout(300);
                    final int maxSizeAns = clientSocket.getReceiveBufferSize();
                    byte[] ansMsg = new byte[maxSizeAns];
                    DatagramPacket ans = new DatagramPacket(ansMsg, maxSizeAns);
                    for (int j = 0; j < numRequests; j++) {
                        if (!Thread.interrupted()) {
                        String req = prefix + k + "_" + j;
                        byte[] msg = req.getBytes(Charset.forName("UTF-8"));
                        DatagramPacket message = new DatagramPacket(msg, msg.length, serverAddress);
                        while (true) {
                            try {
                                clientSocket.send(message);
                                clientSocket.receive(ans);
                                String strAns = new String(ans.getData(), 0, ans.getLength(), Charset.forName("UTF-8"));
                                if (strAns.contains(req) && !strAns.equals(req)) {
                                    System.out.println("Sent: " + req);
                                    System.out.println("Got: " + strAns);
                                    break;
                                }
                            } catch (SocketTimeoutException e) {
                                //System.err.println("Error while waiting");
                            } catch (IOException e) {
                                System.err.println("Error while sending or receiving");
                            }
                        }
                    }}
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            });
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println("Error while waiting");
        }
    }
}
