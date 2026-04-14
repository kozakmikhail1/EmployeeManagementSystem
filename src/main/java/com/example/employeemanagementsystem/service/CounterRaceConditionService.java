package com.example.employeemanagementsystem.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class CounterRaceConditionService {

    private int unsafeCounter;
    private int synchronizedCounter;
    private final AtomicInteger atomicCounter = new AtomicInteger();

    public void resetAll() {
        unsafeCounter = 0;
        synchronizedCounter = 0;
        atomicCounter.set(0);
    }

    public void incrementUnsafe() {
        int current = unsafeCounter;
        Thread.yield();
        unsafeCounter = current + 1;
    }

    public synchronized void incrementSynchronized() {
        synchronizedCounter++;
    }

    public void incrementAtomic() {
        atomicCounter.incrementAndGet();
    }

    public int getUnsafeCounter() {
        return unsafeCounter;
    }

    public int getSynchronizedCounter() {
        return synchronizedCounter;
    }

    public int getAtomicCounter() {
        return atomicCounter.get();
    }
}
