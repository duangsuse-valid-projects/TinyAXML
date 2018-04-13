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
}
