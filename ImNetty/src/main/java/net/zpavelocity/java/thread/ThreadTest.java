package net.zpavelocity.java.thread;

public class ThreadTest {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            try {
                print("test1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                print("test2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        t1.start();
        t2.start();
    }


    public static void print(String name) throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            System.out.println(name + ": s" + i);
            Thread.sleep(5);
        }
    }
}