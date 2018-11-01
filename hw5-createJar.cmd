pushd java
mkdir tmp
javac -d tmp ru/ifmo/rain/Zhevtyak/implementor/Implementor.java
jar cfm ../Implementor.jar ../Manifest.txt -C tmp ru/ifmo/rain/Zhevtyak/implementor -C tmp info/kgeorgiy/java/advanced/implementor/
rmdir /q /s tmp
popd

