echo off
pushd java
javac -cp "../artifacts/*" ru/ifmo/rain/Zhevtyak/helloUDP/helloUDPClient.java

rem del info\kgeorgiy\java\advanced\implementor\*.class

java -cp ".;../artifacts/WebCrawlerTest.jar;../lib/*" info.kgeorgiy.java.advanced.crawler.Tester easy ru.ifmo.rain.Zhevtyak.webCrawler.WebCrawler %1
popd

