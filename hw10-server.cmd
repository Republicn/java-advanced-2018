echo off
pushd java
javac -cp "../artifacts/*" ru/ifmo/rain/Zhevtyak/helloUDP/HelloUDPServer.java

java -cp ".;../artifacts/HelloUDPTest.jar;../lib/*" info.kgeorgiy.java.advanced.hello.Tester server-i18n ru.ifmo.rain.Zhevtyak.helloUDP.HelloUDPServer %1
popd

