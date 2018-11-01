echo off
pushd java
javac -cp "../artifacts/*" ru/ifmo/rain/Zhevtyak/parallel/IterativeParallelism.java
javac -cp "../artifacts/*" ru/ifmo/rain/Zhevtyak/parallel/ParallelMapperImpl.java

rem del info\kgeorgiy\java\advanced\implementor\*.class

java -cp ".;../artifacts/ParallelMapperTest.jar;../lib/*" info.kgeorgiy.java.advanced.mapper.Tester list ru.ifmo.rain.Zhevtyak.parallel.ParallelMapperImpl,ru.ifmo.rain.Zhevtyak.parallel.IterativeParallelism %1
popd

