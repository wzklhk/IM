package net.zpavelocity.java.string;

public class Str2Num {
    public static void main(String[] args) {
        String num = "1,2,3,4,5 ";
        String[] numArray = num.split(",");

        System.out.println(numArray.length);
        for (int i = 0; i < numArray.length; ++i) {
            System.out.println(Double.valueOf(numArray[i]));
        }
    }
}
