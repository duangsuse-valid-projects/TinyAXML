package org.duangsuse.tinyaxml;

import static org.junit.Assert.assertArrayEquals;

import org.duangsuse.tinyaxml.Main;
import org.junit.Test;

/** Unit test for the {@code Main} class */

public class MainClassTest {
    @Test
    public void ppAryWorks() {
        Object[] ary = {"a", 1, false};
        Object[] result = Main.ppAry(ary);
        assertArrayEquals("ppAry not giving wanted result", new Object[] {"a", "1", "false"}, result);
    }

    @Test
    public void cropAryWorks() {
        Integer[] ary0 = {0, 1, 2, 3, 4, 5, 6};
        String[] ary1 = {"A", "B", "C"};
        Character[] ary2 = {'a', 'b'};

        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, Main.cropAry(ary0, 1, 5));
        assertArrayEquals(new String[] {"A", "B"}, Main.cropAry(ary1, 0, 1));
        assertArrayEquals(new Character[] {'a', 'b'}, Main.cropAry(ary2, 0, 1));
    }

    @Test
    public void cropAryThrowsRightException() {}
}
