echo off
pushd java
javac ru/ifmo/rain/Zhevtyak/students/StudentDB.java
del info\kgeorgiy\java\advanced\student\*.class

java -cp ".;../artifacts/StudentTest.jar;../lib/*" info.kgeorgiy.java.advanced.student.Tester StudentQuery ru.ifmo.rain.Zhevtyak.students.StudentDB %1
popd

