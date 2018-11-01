echo off
pushd java
javac ru/ifmo/rain/Zhevtyak/arrayset/ArraySet.java
java -cp ".;../artifacts/ArraySetTest.jar;../lib/*" info.kgeorgiy.java.advanced.arrayset.Tester NavigableSet ru.ifmo.rain.Zhevtyak.arrayset.ArraySet %1
rem rmdir /q /s __Test__Walk__
popd

