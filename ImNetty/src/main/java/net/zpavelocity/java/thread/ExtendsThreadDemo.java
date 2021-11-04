package net.zpavelocity.java.thread;

public class ExtendsThreadDemo extends Thread {
    private Thread t;
    private String threadName;

    ExtendsThreadDemo(String name) {
        threadName = name;
        System.out.println("Creating " + threadName);
    }

    public static void main(String[] args) {
        ExtendsThreadDemo t1 = new ExtendsThreadDemo("T1");
        t1.start();
        ExtendsThreadDemo t2 = new ExtendsThreadDemo("T2");
        t2.start();
    }

    @Override
    public void run() {
        System.out.println("Running " + threadName);
        try {
            for (int i = 4; i > 0; i--) {
                System.out.println("Thread: " + threadName + ", " + i);
                // 让线程睡眠一会
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " + threadName + " interrupted.");
        }
        System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

}

