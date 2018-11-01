echo off
pushd out\production\java-advanced-2018 
java -cp ".;../../../artifacts/StudentTest.jar;../../../lib/*" info.kgeorgiy.java.advanced.student.Tester StudentQuery ru.ifmo.rain.Zhevtyak.students.StudentDB %1
popd

