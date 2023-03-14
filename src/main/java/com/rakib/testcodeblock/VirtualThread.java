package com.rakib.testcodeblock;

import java.time.Duration;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThread {
    protected static void virtualThreadDemo() {
        Vector<String> vector = new Vector<>();
        vector.add("rakib1");
        vector.add("rakib2");
        vector.add("rakib3");
        vector.forEach(System.out::println);

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            vector.forEach(s -> executorService.submit(() -> {
                try {
                    sleep(s);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void sleep(String s) throws InterruptedException {
        System.out.println("submit new thread");
        Thread.sleep(Duration.ofSeconds(2L));
        System.out.println("from thread: " + s);
        System.out.println("thread name: " + Thread.currentThread().getName());
        System.out.println("thread is virtual: " + Thread.currentThread().isVirtual());
    }
}
