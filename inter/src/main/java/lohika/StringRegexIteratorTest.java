package lohika;


import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringRegexIteratorTest {
    @Test
    public void simpleTest(){
        assertTrue(true);
        StringContainter containter = new StringContainter("abdfo45ijaoghawjeo45rjoaijsdgkj45ashidgoa45jisdf", "\\d{2}");
        Iterator iterator = containter.iterator();

        assertEquals("abdfo", iterator.next());
        assertEquals("45ijaoghawjeo", iterator.next());
        assertEquals("45rjoaijsdgkj", iterator.next());
        assertEquals("45ashidgoa", iterator.next());
        assertEquals("45jisdf", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testExceptionWhenNoMoreElements(){
        StringContainter containter = new StringContainter("abdfo45ijaoghawjeo", "\\d{2}");
        Iterator iterator = containter.iterator();

        assertEquals("abdfo", iterator.next());
        assertEquals("45ijaoghawjeo", iterator.next());
        iterator.next();

    }
}
