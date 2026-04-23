package com.example.employeemanagementsystem.service;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class CounterRaceConditionService {

    private final AtomicInteger atomicCounter = new AtomicInteger();

    public void reset() {
        atomicCounter.set(0);
    }

    public void increment() {
        atomicCounter.incrementAndGet();
    }

    public int getValue() {
        return atomicCounter.get();
    }
}
