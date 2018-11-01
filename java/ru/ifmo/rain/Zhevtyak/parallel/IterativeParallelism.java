package ru.ifmo.rain.Zhevtyak.parallel;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

public class IterativeParallelism implements ScalarIP, ListIP {

    private ParallelMapper pm = null;

    public IterativeParallelism(ParallelMapper pm) {
        this.pm = pm;
    }

    private <U> List<Stream<? extends U>> split(int threads, List<? extends U> list) {
        List<Stream<? extends U>> res = new ArrayList<>();
        int step = list.size() / threads + 1;
        int rem = list.size() % threads;
        int from = 0;
        for (int i = 0; i < threads; i++) {
            if (i == rem) {
                step--;
            }
            res.add(list.subList(from, from + step).stream());
            from += step;
        }
        return res;
    }

    private <T, R> R mainFunc(int threads,
                              List<? extends T> list,
                              Function<Stream<? extends T>, R> func,
                              Function<? super Stream<R>, R> res) throws InterruptedException {
        threads = Math.min(threads, list.size());
        List<Stream<? extends T>> spl = split(threads, list);
        List<R> ans;
        if (pm != null) {
            ans = pm.map(func, spl);
        } else {
            ArrayList<Thread> myThreads = new ArrayList<>();
            ans = new ArrayList<>(Collections.nCopies(threads, null));
            for (int i = 0; i < threads; i++) {
                final int st = i;
                myThreads.add(new Thread(() -> ans.set(st, func.apply(spl.get(st)))));
                myThreads.get(i).start();
            }
            for (Thread thread : myThreads) {
                thread.join();
            }
        }
        return res.apply(ans.stream());
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return minimum(threads, values, comparator.reversed());
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        Function<Stream<? extends T>, T> min = x -> x.min(comparator).get();
        return mainFunc(threads, values, min, min);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return mainFunc(threads, values, x -> x.allMatch(predicate), x -> x.allMatch(Boolean::booleanValue));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, predicate.negate());
    }

    @Override
    public String join(int threads, List<?> values) throws InterruptedException {
        Function<Stream<?>, String> listJoin = x -> x.map(Object::toString).collect(joining());
        return mainFunc(threads, values, listJoin, listJoin);
    }

    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return mainFunc(threads, values,
                x -> x.filter(predicate).collect(toList()),
                getStreamListFunction());
    }

    private static <T> Function<Stream<List<T>>, List<T>> getStreamListFunction() {
        return x -> x.flatMap(Collection::stream).collect(toList());
    }

    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f) throws InterruptedException {
        return mainFunc(threads, values,
                x -> x.map(f).collect(toList()),
                getStreamListFunction());
    }
}
