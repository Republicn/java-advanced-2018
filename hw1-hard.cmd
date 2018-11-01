echo off
pushd java
javac ru/ifmo/rain/Zhevtyak/walk/RecursiveWalk.java
java -cp ".;../artifacts/WalkTest.jar;../lib/*" info.kgeorgiy.java.advanced.walk.Tester RecursiveWalk ru.ifmo.rain.Zhevtyak.walk.RecursiveWalk %1
rem rmdir /q /s __Test__Walk__
popd

