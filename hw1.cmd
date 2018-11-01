echo off
pushd java
javac ru/ifmo/rain/Zhevtyak/walk/Walk.java
java -cp ".;../artifacts/WalkTest.jar;../lib/*" info.kgeorgiy.java.advanced.walk.Tester Walk ru.ifmo.rain.Zhevtyak.walk.Walk %1
rmdir /q /s __Test__Walk__
popd

