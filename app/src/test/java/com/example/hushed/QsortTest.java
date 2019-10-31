package com.example.hushed;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QsortTest {

    @Test public void basicTest() {
        Random rand = new Random();
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            values.add(rand.nextInt(100000));
        }

        Util.sort(values, (a,b) -> a-b);

        for (int i = 1; i < 1000; i++) {
            // System.out.println("" + (i-1) + ": " + values.get(i-1)
            //      + ", " + i + ": " + values.get(i));

            assertTrue(values.get(i-1) <= values.get(i));
        }
    }

    private static class Thing {
        public final String value;
        public Thing(String v) { value = v; }
    }

    @Test public void genericTest() {
        Util.Func2<Thing, Thing, Integer> compare = (a, b) -> a.value.compareTo(b.value);
        Random rand = new Random();
        List<Thing> list = new ArrayList<>();
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < 1000; i++) {
            // build some random string
            str.append((char) (32 + rand.nextInt(96)));
            for (int k = 0; k < 50; k++) {
                if (rand.nextDouble() < .8) {
                    str.append((char) (32 + rand.nextInt(96)));
                }
            }
            Thing t = new Thing(str.toString());
            list.add(t);
            str.setLength(0);
        }

        Util.sort(list, compare);

        for (int i = 1; i < list.size(); i++) {
            assertTrue(compare.invoke(list.get(i-1), list.get(i)) <= 0);
        }
    }

}
