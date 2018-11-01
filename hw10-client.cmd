echo off
pushd java
javac -cp "../artifacts/*" ru/ifmo/rain/Zhevtyak/helloUDP/HelloUDPClient.java

java -cp ".;../artifacts/HelloUDPTest.jar;../lib/*" info.kgeorgiy.java.advanced.hello.Tester client ru.ifmo.rain.Zhevtyak.helloUDP.HelloUDPClient %1
popd

