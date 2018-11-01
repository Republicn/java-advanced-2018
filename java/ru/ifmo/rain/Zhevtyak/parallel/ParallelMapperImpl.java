package ru.ifmo.rain.Zhevtyak.parallel;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    //public int numThreads;
    private List<Thread> threadPool;
    private final Queue<Runnable> queue = new ArrayDeque<>();

    public ParallelMapperImpl(int thread) {
        threadPool = new ArrayList<>();
        for (int i = 0; i < thread; i++) {
            threadPool.add(new Thread(() -> {
                try {
                    Runnable task;
                    while (!Thread.interrupted()) {
                        synchronized (queue) {
                            while (queue.isEmpty()) {
                                queue.wait();
                            }
                            task = queue.poll();
                        }
                        task.run();
                    }
                } catch (InterruptedException e) {
                //    System.err.println("Error while waiting");
                }
            }));
        }
        threadPool.forEach(Thread::start);
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        int n = args.size();
        final Counter counter = new Counter();
        final List<R> result = new ArrayList<>(Collections.nCopies(n, null));
        for (int i = 0; i < n; i++) {
            final int id = i;
            final T arg = args.get(i);
            synchronized (queue) {
                queue.add(() -> {
                    result.set(id, f.apply(arg));
                    synchronized (counter) {
                        if (++counter.count == n) {
                            counter.notify();
                        }
                    }
                });
                queue.notify();
            }
        }
        synchronized (counter) {
            while (counter.count < n) {
                counter.wait();
            }
        }
        return result;
    }

    private static class Counter {
        private int count = 0;
    }

    @Override
    public void close() {
        threadPool.forEach(Thread::interrupt);
        for (Thread t : threadPool) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Error while joining");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ParallelMapper mapper = new ParallelMapperImpl(3);
        System.out.println(mapper.map(i -> {
            System.out.println(args[i]);
            return i;
        }, Arrays.asList(1, 2, 3, 4)));
        mapper.close();
    }
}
