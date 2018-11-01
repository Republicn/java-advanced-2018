package ru.ifmo.rain.Zhevtyak.parallel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Нина on 27.03.2018.
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        System.out.println(new IterativeParallelism().maximum(6, list, Integer::compare ));
    }
}
