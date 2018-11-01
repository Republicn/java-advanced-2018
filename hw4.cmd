echo off
pushd java
javac -cp "../artifacts/ImplementorTest.jar" ru/ifmo/rain/Zhevtyak/implementor/Implementor.java
rem del info\kgeorgiy\java\advanced\implementor\*.class

"%JAVA_HOME%\bin\java" -cp ".;../artifacts/ImplementorTest.jar;%JAVA_HOME%/lib/tools.jar;../lib/*" info.kgeorgiy.java.advanced.implementor.Tester interface ru.ifmo.rain.Zhevtyak.implementor.Implementor %1
popd

