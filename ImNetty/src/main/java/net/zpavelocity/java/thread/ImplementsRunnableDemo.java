package net.zpavelocity.java.thread;

/*
 * 通过实现 Runnable 接口来创建线程
 *
 */

public class ImplementsRunnableDemo implements Runnable {
    private Thread t;
    private String threadName;

    public ImplementsRunnableDemo(String threadName) {
        this.threadName = threadName;
        System.out.println("Creating " + threadName);
    }

    public static void main(String[] args) {
        ImplementsRunnableDemo r1 = new ImplementsRunnableDemo("T1");
        r1.start();
        ImplementsRunnableDemo r2 = new ImplementsRunnableDemo("T2");
        r2.start();
    }

    @Override
    public void run() {
        System.out.println("Running " + threadName);
        try {
            for (int i = 5; i > 0; i--) {
                System.out.println("Thread: " + threadName + ", " + i);

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Thread " + threadName + " exiting. ");
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}


