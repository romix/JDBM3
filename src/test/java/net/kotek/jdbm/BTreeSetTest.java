/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.kotek.jdbm;


import java.io.Serializable;
import java.util.*;

/**
 * Tests for TreeSet which comes with JDBM. Original code comes from Apache Harmony,
 * Modified by Jan Kotek for use in JDBM
 */
public class BTreeSetTest extends TestCaseWithTestFile {

    private DB db;

    public static class ReversedIntegerComparator implements Comparator, Serializable {
        public int compare(Object o1, Object o2) {
            return -(((Integer) o1).compareTo((Integer) o2));
        }

        public boolean equals(Object o1, Object o2) {
            return ((Integer) o1).compareTo((Integer) o2) == 0;
        }


    }

    SortedSet<Object> ts;

    Object objArray[] = new Object[1000];

    /**
     * @tests java.util.TreeSet#TreeSet()
     */
    public void test_Constructor() {
        // Test for method java.util.TreeSet()
        assertTrue("Did not construct correct TreeSet", db.createTreeSet("test").isEmpty());
    }


    /**
     * @tests java.util.TreeSet#TreeSet(java.util.Comparator)
     */
    public void test_ConstructorLjava_util_Comparator() {
        // Test for method java.util.TreeSet(java.util.Comparator)
        SortedSet myTreeSet = db.createTreeSet("test", new ReversedIntegerComparator(), null);
        assertTrue("Did not construct correct TreeSet", myTreeSet.isEmpty());
        myTreeSet.add(new Integer(1));
        myTreeSet.add(new Integer(2));
        assertTrue(
                "Answered incorrect first element--did not use custom comparator ",
                myTreeSet.first().equals(new Integer(2)));
        assertTrue(
                "Answered incorrect last element--did not use custom comparator ",
                myTreeSet.last().equals(new Integer(1)));
    }

    /**
     * @tests java.util.TreeSet#TreeSet(java.util.SortedSet)
     */
    public void test_ConstructorLjava_util_SortedSet() {
        // Test for method java.util.TreeSet(java.util.SortedSet)
        ReversedIntegerComparator comp = new ReversedIntegerComparator();
        SortedSet myTreeSet = db.createTreeSet("test", comp, null);
        for (int i = 0; i < objArray.length; i++)
            myTreeSet.add(objArray[i]);
        SortedSet anotherTreeSet = db.getTreeSet("test");
        anotherTreeSet.addAll(myTreeSet);
        assertTrue("TreeSet is not correct size",
                anotherTreeSet.size() == objArray.length);
        for (int counter = 0; counter < objArray.length; counter++)
            assertTrue("TreeSet does not contain correct elements",
                    anotherTreeSet.contains(objArray[counter]));
        assertEquals("TreeSet does not answer correct comparator", anotherTreeSet
                .comparator().getClass(),comp.getClass());
        assertEquals("TreeSet does not use comparator",
                anotherTreeSet.first() , objArray[objArray.length - 1]);
    }

    /**
     * @tests java.util.TreeSet#add(java.lang.Object)
     */
    public void test_addLjava_lang_Object() {
        // Test for method boolean java.util.TreeSet.add(java.lang.Object)
        ts.add(new Integer(-8));
        assertTrue("Failed to add Object", ts.contains(new Integer(-8)));
        ts.add(objArray[0]);
        assertTrue("Added existing element", ts.size() == objArray.length + 1);

    }

    /**
     * @tests java.util.TreeSet#addAll(java.util.Collection)
     */
    public void test_addAllLjava_util_Collection() {
        // Test for method boolean
        // java.util.TreeSet.addAll(java.util.Collection)
        SortedSet s = db.createTreeSet("test");
        s.addAll(ts);
        assertTrue("Incorrect size after add", s.size() == ts.size());
        Iterator i = ts.iterator();
        while (i.hasNext())
            assertTrue("Returned incorrect set", s.contains(i.next()));

    }

    /**
     * @tests java.util.TreeSet#clear()
     */
    public void test_clear() {
        // Test for method void java.util.TreeSet.clear()
        ts.clear();
        assertEquals("Returned non-zero size after clear", 0, ts.size());
        assertTrue("Found element in cleared set", !ts.contains(objArray[0]));
    }

    /**
     * @tests java.util.TreeSet#comparator()
     */
    public void test_comparator() {
        // Test for method java.util.Comparator java.util.TreeSet.comparator()
        ReversedIntegerComparator comp = new ReversedIntegerComparator();
        SortedSet myTreeSet = db.createTreeSet("test", comp, null);
        assertTrue("Answered incorrect comparator",
                myTreeSet.comparator() == comp);
    }

    /**
     * @tests java.util.TreeSet#contains(java.lang.Object)
     */
    public void test_containsLjava_lang_Object() {
        // Test for method boolean java.util.TreeSet.contains(java.lang.Object)
        assertTrue("Returned false for valid Object", ts
                .contains(objArray[objArray.length / 2]));
        assertTrue("Returned true for invalid Object", !ts
                .contains(new Integer(-9)));
    }

    /**
     * @tests java.util.TreeSet#first()
     */
    public void test_first() {

        // Test for method java.lang.Object java.util.TreeSet.first()
        assertEquals("Returned incorrect first element",
                ts.first(), objArray[0]);
    }

    /**
     * @tests java.util.TreeSet#headSet(java.lang.Object)
     */
    public void test_headSetLjava_lang_Object() {
        // Test for method java.util.SortedSet
        // java.util.TreeSet.headSet(java.lang.Object)
        Set s = ts.headSet(new Integer(100));
        assertEquals("Returned set of incorrect size", 100, s.size());
        for (int i = 0; i < 100; i++)
            assertTrue("Returned incorrect set", s.contains(objArray[i]));
    }

    /**
     * @tests java.util.TreeSet#isEmpty()
     */
    public void test_isEmpty() {
        // Test for method boolean java.util.TreeSet.isEmpty()
        assertTrue("Empty set returned false", db.createTreeSet("test").isEmpty());
        assertTrue("Non-Empty returned true", !ts.isEmpty());
    }

    /**
     * @tests java.util.TreeSet#iterator()
     */
    public void test_iterator() {
        // Test for method java.util.Iterator java.util.TreeSet.iterator()
        SortedSet s = db.createTreeSet("test");
        s.addAll(ts);
        Iterator i = ts.iterator();
        Set as = new HashSet(Arrays.asList(objArray));
        while (i.hasNext())
            as.remove(i.next());
        assertEquals("Returned incorrect iterator", 0, as.size());

    }

    /**
     * @tests java.util.TreeSet#last()
     */
    public void test_last() {
        // Test for method java.lang.Object java.util.TreeSet.last()
        assertEquals("Returned incorrect last element",
                ts.last(),objArray[objArray.length - 1]);
    }

    /**
     * @tests java.util.TreeSet#remove(java.lang.Object)
     */
    public void test_removeLjava_lang_Object() {
        // Test for method boolean java.util.TreeSet.remove(java.lang.Object)
        ts.remove(objArray[0]);
        assertTrue("Failed to remove object", !ts.contains(objArray[0]));
        assertTrue("Failed to change size after remove",
                ts.size() == objArray.length - 1);
    }

    /**
     * @tests java.util.TreeSet#size()
     */
    public void test_size() {
        // Test for method int java.util.TreeSet.size()
        assertTrue("Returned incorrect size", ts.size() == objArray.length);
    }

    /**
     * @tests java.util.TreeSet#subSet(java.lang.Object, java.lang.Object)
     */
    public void test_subSetLjava_lang_ObjectLjava_lang_Object() {
        // Test for method java.util.SortedSet
        // java.util.TreeSet.subSet(java.lang.Object, java.lang.Object)
        final int startPos = objArray.length / 4;
        final int endPos = 3 * objArray.length / 4;
        SortedSet aSubSet = ts.subSet(objArray[startPos], objArray[endPos]);
        assertTrue("Subset has wrong number of elements",
                aSubSet.size() == (endPos - startPos));
        for (int counter = startPos; counter < endPos; counter++)
            assertTrue("Subset does not contain all the elements it should",
                    aSubSet.contains(objArray[counter]));

        int result;
        try {
            ts.subSet(objArray[3], objArray[0]);
            result = 0;
        } catch (IllegalArgumentException e) {
            result = 1;
        }
        assertEquals("end less than start should throw", 1, result);
    }

    /**
     * @tests java.util.TreeSet#tailSet(java.lang.Object)
     */
    public void test_tailSetLjava_lang_Object() {
        // Test for method java.util.SortedSet
        // java.util.TreeSet.tailSet(java.lang.Object)
        Set s = ts.tailSet(new Integer(900));
        assertEquals("Returned set of incorrect size", 100, s.size());
        for (int i = 900; i < objArray.length; i++)
            assertTrue("Returned incorrect set", s.contains(objArray[i]));
    }

    /**
     * Tests equals() method.
     * Tests that no ClassCastException will be thrown in all cases.
     * Regression test for HARMONY-1639.
     */
    public void test_equals() throws Exception {
        // comparing TreeSets with different object types
        Set s1 = db.createTreeSet("test1");
        Set s2 = db.createTreeSet("test2");
        s1.add("key1");
        s1.add("key2");
        s2.add(new Integer(1));
        s2.add(new Integer(2));
        assertFalse("Sets should not be equal 1", s1.equals(s2));
        assertFalse("Sets should not be equal 2", s2.equals(s1));

        // comparing TreeSet with HashSet
        s1 = db.createTreeSet("test");
        s2 = new HashSet();
        s1.add("key");
        s2.add(new Object());
        assertFalse("Sets should not be equal 3", s1.equals(s2));
        assertFalse("Sets should not be equal 4", s2.equals(s1));


    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    public void setUp() throws Exception {
        super.setUp();
        db = newDBNoCache();
        ts = db.createTreeSet("testBTreeSet");
        for (int i = 0; i < objArray.length; i++) {
            Object x = objArray[i] = new Integer(i);
            ts.add(x);
        }
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    public void tearDown() throws Exception {
        db.close();
        super.tearDown();
    }
}