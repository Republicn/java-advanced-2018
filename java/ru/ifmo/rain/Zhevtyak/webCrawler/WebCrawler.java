//package ru.ifmo.rain.Zhevtyak.webCrawler;
//
//import info.kgeorgiy.java.advanced.crawler.*;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.*;
//
//public class WebCrawler implements Crawler{
//
//    private Downloader downloader;
//    private ExecutorService threadPoolExec, threadPoolDownload;
//    private int dp;
//
//    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
//        this.downloader = downloader;
//        threadPoolExec = Executors.newFixedThreadPool(extractors);
//        threadPoolDownload = Executors.newFixedThreadPool(downloaders);
//    }
//
//    @Override
//    public Result download(String url, int depth) {
//        List<String> downloaded = new ArrayList<>();
//        Map<String, IOException> errors = new HashMap<>();
//        try {
//           /* Document doc = threadPoolDownload.submit(
//                try {
//                    downloader.download(url);
//                } catch (IOException e) {
//                    errors.put(url, e);
//                }
//            ).get();*/
//            threadPoolExec.execute(() -> {
//                try {
//                    List<String> links = doc.extractLinks();
//                    for (String link: links) {
//                        if (depth >= 2) {
//                            Result resLink = download(link, depth - 1);
//                            downloaded.addAll(resLink.getDownloaded());
//                            errors.putAll(resLink.getErrors());
//                        }
//                    }
//                    downloaded.add(url);
//                } catch (IOException e) {
//                    errors.put(url, e);
//                }
//            });
//        }  catch (InterruptedException | ExecutionException e) {
//            //e.printStackTrace();
//        }
//        return new Result(downloaded, errors);
//    }
//
//    @Override
//    public void close() {
//        threadPoolDownload.shutdown();
//        threadPoolExec.shutdown();
//    }
//
//    public static void main(String[] args) throws IOException {
//        final String url = args[0];
//        final int depth = Integer.parseInt(args[1]);
//        final int downloaders = args.length > 1 ? Integer.parseInt(args[2]) : 5;
//        final int extractors = args.length > 2 ? Integer.parseInt(args[3]) : 5;
//        final int perHost = args.length > 3 ? Integer.parseInt(args[4]) : 5;
//        try (WebCrawler crawler = new WebCrawler(new CachingDownloader(), downloaders, extractors, perHost)) {
//            crawler.download(url, depth);
//        }
//    }
//}
