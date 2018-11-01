echo off
pushd java
javac ru/ifmo/rain/Zhevtyak/implementor/Implementor.java
del info\kgeorgiy\java\advanced\implementor\*.class

"%JAVA_HOME%\bin\java" -cp ".;../artifacts/JarImplementorTest.jar;%JAVA_HOME%/lib/tools.jar;../lib/*" info.kgeorgiy.java.advanced.implementor.Tester jar-interface ru.ifmo.rain.Zhevtyak.implementor.Implementor %1
popd

