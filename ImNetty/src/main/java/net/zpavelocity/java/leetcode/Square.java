package net.zpavelocity.java.leetcode;

public class Square {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            boolean b = new Square().isPerfectSquare(i);
            System.out.println("" + i + " " + b);
        }
    }

    public boolean isPerfectSquare(int num) {
        for (int i = 0; i * i <= num; i++) {
            if (i * i == num) {
                return true;
            }
        }
        return false;
    }
}
