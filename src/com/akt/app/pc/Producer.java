package com.akt.app.pc;

import java.util.Random;

class Producer extends Thread {
    private final BlockingBuffer pc;
    private final int produceElements;

    public Producer(BlockingBuffer sharedObject, int produceElements) {
        this.pc = sharedObject;
        this.produceElements = produceElements;
    }

    @Override
    public void run() {
        try {
            for (int i=0;i<produceElements;i++) {
                int number = new Random().nextInt(100);
                System.out.printf("\nThread %s Produced Random number %d:",Thread.currentThread().getName(),number);
                pc.put(number);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

