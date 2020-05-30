package com.akt.app.pc;

class Consumer extends Thread {
    private final BlockingBuffer pc;
    private final int consumeElements;

    public Consumer(BlockingBuffer sharedObject, int consumeElements) {
        this.pc = sharedObject;
        this.consumeElements = consumeElements;
    }

    @Override
    public void run() {
        try {
            for (int i = 0;i<consumeElements;i++) {
                System.out.printf("\n Thread %s Consumed %d from Buffer.",Thread.currentThread().getName(), pc.get());
            }
        } catch (InterruptedException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
