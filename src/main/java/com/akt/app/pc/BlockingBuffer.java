package com.akt.app.pc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingBuffer {
    private int[] buffer;
    private int capacity;
    volatile private int bufferCount;
    private final Lock lock = new ReentrantLock(true);
    private final Condition bufferFull = lock.newCondition();
    private final Condition bufferEmpty = lock.newCondition();

    public BlockingBuffer(int capacity) {
        this.buffer = new int[capacity];
        this.bufferCount = 0;
        this.capacity = capacity;
    }

    public void put(int number) throws InterruptedException {
        lock.lock();
        try {
            while (isFull()) {
                System.out.println(" Buffer is full.");
                bufferFull.await(5,TimeUnit.MILLISECONDS);
            }
            buffer[bufferCount++] = number;
            bufferEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public int get() throws InterruptedException, IllegalAccessException {
        lock.lock();
        try {
            while (isEmpty()) {
                System.out.println(" Buffer is empty.");
                bufferEmpty.await(5, TimeUnit.MILLISECONDS);
            }
            bufferFull.signal();
            return buffer[--bufferCount];
        } finally {
            lock.unlock();
        }
    }

    public int getBufferCount(){
        return bufferCount;
    }

    private boolean isEmpty() {
        return bufferCount == 0;
    }

    private boolean isFull() {
        return bufferCount == capacity;
    }
}
