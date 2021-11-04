package net.zpavelocity.java.set;

import java.util.ArrayList;
import java.util.List;

public class IteratorTest {
    public static void main(String[] args) {
        List<Double> list = new ArrayList<Double>();

        list.add(1.2);
        list.add(2.3);
        list.add(3.3);
        list.add(4.3);
        list.add(5.3);
        list.add(6.3);

        for (Object i : list) {
            System.out.println(i);
        }


    }
}
