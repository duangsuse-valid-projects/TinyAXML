package org.duangsuse.tinyaxml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.IllegalArgumentException;

import org.duangsuse.tinyaxml.Main;
import org.junit.Test;

/** Unit test for the {@code Main} class */

public class MainClassTest {
    /** @see Main class constants */
    @Test
    public void constantsConcatsRight() {
        assertEquals(Main.defaultPluginPath, System.getenv("HOME") + "/.config/tinyaxml/");
        assertTrue(Main.helpMessage.contains("TinyAXML"));
        assertFalse(Main.verbose);
    }

    /** @see Main#envOr(String, String) */
    @Test
    public void envOrWorks() {
        if (System.getenv("HOME") == null)
            Main.warn("HOME variable undefined");
        assertEquals("OK", Main.envOr("FOO_TAXML", "OK"));
        assertEquals(System.getenv("HOME"), Main.envOr("HOME", "FAIL"));
    }

    /** @see Main#revAry(Object[], int) */
    @Test
    public void revAryWorks() {
        Object[] ar1 = new Object[] {1, 2, 3};
        Object[] ar2 = new Object[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertEquals(Main.revAry(ar1, 0), 3);
        assertEquals(Main.revAry(ar2, 3), 6);
        assertEquals(Main.revAry(ar2, 5), 4);
    }

    @Test
    public void revAryThrowsRightException() {
        boolean ex = false;
        try {
            Main.revAry(new Object[] {}, 5);
        } catch (IndexOutOfBoundsException ignored) {
            ex = true;
        }
        assertTrue(ex);
    }

    /** @see Main#isFile(String) @see {@link Main#checkFile(File)} */
    @Test
    public void isIsFileAndCheckFileWorks() throws IOException {
        // Runner OS must be GNU/Linux
        assertTrue(Main.isFile(File.createTempFile("axmltest_", ".txt").getPath()));
        assertTrue(Main.checkFile(File.createTempFile("axmltest_", ".ex")));
        assertTrue(Main.isFile("/proc/cmdline"));
        assertFalse(Main.isFile("/"));
        assertFalse(Main.checkFile(new File("/proc/cmdline")));
    }

    /** @see Main#ppAry(Object[]) */
    @Test
    public void ppAryWorks() {
        Object[] ary = {"a", 1, false};
        Object[] result = Main.ppAry(ary);
        assertArrayEquals("ppAry not giving wanted result", new Object[] {"a", "1", "false"}, result);
    }

    /** @see Main#cropAry(byte[], int, int) */
    @Test
    public void cropAryWorks() {
        Integer[] ary0 = {0, 1, 2, 3, 4, 5, 6};
        String[] ary1 = {"A", "B", "C"};
        Character[] ary2 = {'a', 'b'};
        byte[] aryb = new byte[] {1, 2, 3, 4, 5, 6, 7};

        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, Main.cropAry(ary0, 1, 5));
        assertArrayEquals(new String[] {"A", "B"}, Main.cropAry(ary1, 0, 1));
        assertArrayEquals(new Character[] {'a', 'b'}, Main.cropAry(ary2, 0, 1));
        assertArrayEquals(new byte[] {3, 4, 5}, Main.cropAry(aryb, 2, 4)); // byte version works
    }

    @Test(expected=IllegalArgumentException.class)
    public void cropAryThrowsRightException() {
        boolean ex1 = false;
        boolean ex2 = false;
        try {
            try {
                Main.cropAry(new byte[] {}, 5, 1);
            } catch (IllegalArgumentException ign) { ex1 = true; }
            Main.cropAry(new byte[] {}, -1, 1);
        } catch (IllegalArgumentException ign) { ex2 = true; }
        assertTrue(ex1 && ex2);
        Main.cropAry(new byte[] {}, 0, 0);
    }

    /** @see Main#isIn(Object, Object[]) */
    @Test
    public void isInWorks() {
        Object[] ary = new Object[] {1, 2, 3, 4};
        assertFalse(Main.isIn(1, ary) == -1);
        assertTrue(Main.isIn(false, ary) == -1);
        assertEquals(2, Main.isIn(3, ary));
    }

    /** @see Main#int2Word(int) @see Main#word2Int */
    @Test
    public void intWordWordInt() {
        int test_int = 233;
        int test_int2 = 666;
        byte[] test_byte = new byte[] {0, 2, 4, 0};
        assertEquals(test_int, Main.word2Int(Main.int2Word(test_int)));
        assertEquals(test_int2, Main.word2Int(Main.int2Word(test_int2)));
        assertEquals(Main.word2Int(test_byte), Main.word2Int(Main.int2Word(Main.word2Int(test_byte))));
    }

    @Test(expected=IllegalArgumentException.class)
    public void wordIntThrowsRightException() {
        for (int i = 0; i < 5; i++)
            Main.word2Int(new byte[i]);
    }
}
