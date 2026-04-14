package com.example.employeemanagementsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class CounterRaceConditionServiceTest {

    @Test
    void unsafeCounterShowsRaceConditionWith50PlusThreads() throws InterruptedException {
        CounterRaceConditionService counterService = new CounterRaceConditionService();
        int threads = 64;
        int incrementsPerThread = 2000;
        int expected = threads * incrementsPerThread;

        runConcurrentIncrements(threads, incrementsPerThread, counterService::incrementUnsafe);

        assertTrue(counterService.getUnsafeCounter() < expected);
    }

    @Test
    void synchronizedCounterAvoidsRaceConditionWith50PlusThreads() throws InterruptedException {
        CounterRaceConditionService counterService = new CounterRaceConditionService();
        int threads = 64;
        int incrementsPerThread = 2000;
        int expected = threads * incrementsPerThread;

        runConcurrentIncrements(threads, incrementsPerThread, counterService::incrementSynchronized);

        assertEquals(expected, counterService.getSynchronizedCounter());
    }

    @Test
    void atomicCounterAvoidsRaceConditionWith50PlusThreads() throws InterruptedException {
        CounterRaceConditionService counterService = new CounterRaceConditionService();
        int threads = 64;
        int incrementsPerThread = 2000;
        int expected = threads * incrementsPerThread;

        runConcurrentIncrements(threads, incrementsPerThread, counterService::incrementAtomic);

        assertEquals(expected, counterService.getAtomicCounter());
    }

    private void runConcurrentIncrements(int threads, int incrementsPerThread, Runnable incrementAction)
            throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                await(startLatch);
                for (int j = 0; j < incrementsPerThread; j++) {
                    incrementAction.run();
                }
                doneLatch.countDown();
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception.getMessage(), exception);
        }
    }
}
