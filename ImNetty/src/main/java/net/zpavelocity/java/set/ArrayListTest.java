package net.zpavelocity.java.set;

import java.util.ArrayList;
import java.util.List;

public class ArrayListTest {
    public static void main(String[] args) {
        List list = new ArrayList();

        list.add("jack");
        System.out.println(list);
        System.out.println(list.size());
        list.add(10);
        System.out.println(list);
        System.out.println(list.size());
        list.add(true);
        System.out.println(list);
        System.out.println(list.size());
    }
}
