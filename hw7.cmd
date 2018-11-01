echo off
pushd java
javac -cp "../artifacts/IterativeParallelismTest.jar" ru/ifmo/rain/Zhevtyak/parallel/IterativeParallelism.java
rem del info\kgeorgiy\java\advanced\implementor\*.class

java -cp ".;../artifacts/IterativeParallelismTest.jar;../lib/*" info.kgeorgiy.java.advanced.concurrent.Tester list ru.ifmo.rain.Zhevtyak.parallel.IterativeParallelism %1
popd

