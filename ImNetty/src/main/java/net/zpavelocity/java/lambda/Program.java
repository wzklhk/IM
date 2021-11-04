package net.zpavelocity.java.lambda;

interface Comparator {
    int compare(int a, int b);
}

public class Program {
    public static void main(String[] args) {
        // Test 1
        Comparator comparator1 = new MyComparator();

        // Test 2
        Comparator comparator2 = new Comparator() {
            @Override
            public int compare(int a, int b) {
                return a - b;
            }
        };

        // Test 3
        Comparator comparator3 = (a, b) -> a - b;
    }
}

class MyComparator implements Comparator {

    @Override
    public int compare(int a, int b) {
        return a - b;
    }
}