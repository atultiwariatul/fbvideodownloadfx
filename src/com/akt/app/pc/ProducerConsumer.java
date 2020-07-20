package com.akt.app.pc;

/**
 * This ProducerConsumer will only work well when the Number of Producer and Consumer Threads will be same
 * But if We will have Less Consumers then We will see Producers will keep Producing the messages but there will be no consumers to consume
 * If we will have Less producers then we will see Consumer will keep Polling messages but there is no message to consume
 * Solution should be implemented in package pc2.
 * @author Atul Tiwari
 */
public class ProducerConsumer {
    private final BlockingBuffer blockingBuffer = new BlockingBuffer(10);
    private static final int PRODUCER_THREADS = 5;
    private static final int CONSUMER_THREADS = 5;

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer producerConsumer = new ProducerConsumer();
        Producer[] producers = producerConsumer.startProducers(PRODUCER_THREADS);
        Consumer[] consumers = producerConsumer.startConsumers(CONSUMER_THREADS);
        for (int i = 0; i<PRODUCER_THREADS;i++){
            producers[i].join();
        }
        for (int i = 0; i<CONSUMER_THREADS;i++){
            consumers[i].join();
        }
        System.out.println("\nBuffer Size:"+producerConsumer.blockingBuffer.getBufferCount());
    }


    private Producer[] startProducers(int threads){
        Producer[] producers = new Producer[threads];
        for (int i=0;i<threads;i++){
            producers[i] = new Producer(blockingBuffer,50);
            producers[i].setName("Producer-"+i);
            producers[i].start();
        }
        return producers;
    }

    private Consumer[]  startConsumers(int threads){
        Consumer[] consumers = new Consumer[threads];
        for (int i=0;i<threads;i++){
            consumers[i] = new Consumer(blockingBuffer,50);
            consumers[i].setName("Consumer-"+i);
            consumers[i].start();
        }
        return consumers;
    }

}
