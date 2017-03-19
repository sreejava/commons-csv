/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CSVRecordTest {

    private enum EnumFixture { UNKNOWN_COLUMN }

    private String[] values;
    private CSVRecord record, recordWithHeader;
    private Map<String, Integer> header;

    @Before
    public void setUp() throws Exception {
        values = new String[] { "A", "B", "C" };
        record = new CSVRecord(values, null, null, 0, -1);
        header = new HashMap<String, Integer>();
        header.put("first", Integer.valueOf(0));
        header.put("second", Integer.valueOf(1));
        header.put("third", Integer.valueOf(2));
        recordWithHeader = new CSVRecord(values, header, null, 0, -1);
    }

    @Test
    public void testGetInt() {
        System.out.println("testGetInt");

        assertEquals(values[0], record.get(0));
        assertEquals(values[1], record.get(1));
        assertEquals(values[2], record.get(2));
        System.out.println("testGetInt");
    }

    @Test
    public void testGetString() {
    	  System.out.println("testGetString");
        assertEquals(values[0], recordWithHeader.get("first"));
        assertEquals(values[1], recordWithHeader.get("second"));
        assertEquals(values[2], recordWithHeader.get("third"));
        System.out.println("testGetString");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStringInconsistentRecord() {
    	 System.out.println("testGetStringInconsistentRecord");
        header.put("fourth", Integer.valueOf(4));
        recordWithHeader.get("fourth");
        System.out.println("testGetStringInconsistentRecord");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetStringNoHeader() {
    	 System.out.println("testGetStringNoHeader");
        record.get("first");
        System.out.println("testGetStringNoHeader");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUnmappedEnum() {
    	 System.out.println("testGetUnmappedEnum");
        assertNull(recordWithHeader.get(EnumFixture.UNKNOWN_COLUMN));
        System.out.println("testGetUnmappedEnum");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUnmappedName() {
    	System.out.println("testGetUnmappedName");
        assertNull(recordWithHeader.get("fourth"));
        System.out.println("testGetUnmappedName");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetUnmappedNegativeInt() {
    	  System.out.println("testGetUnmappedNegativeInt");
        assertNull(recordWithHeader.get(Integer.MIN_VALUE));
        System.out.println("testGetUnmappedNegativeInt");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetUnmappedPositiveInt() {
    	System.out.println("testGetUnmappedPositiveInt");
        assertNull(recordWithHeader.get(Integer.MAX_VALUE));
        System.out.println("testGetUnmappedPositiveInt");
    }

    @Test
    public void testIsConsistent() {
    	 System.out.println("testIsConsistent");
        assertTrue(record.isConsistent());
        assertTrue(recordWithHeader.isConsistent());

        header.put("fourth", Integer.valueOf(4));
        assertFalse(recordWithHeader.isConsistent());
        System.out.println("testIsConsistent");
    }

    @Test
    public void testIsMapped() {
    	 System.out.println("testIsMapped");
        assertFalse(record.isMapped("first"));
        assertTrue(recordWithHeader.isMapped("first"));
        assertFalse(recordWithHeader.isMapped("fourth"));
        System.out.println("testIsMapped");
    }

    @Test
    public void testIsSet() {
    	System.out.println("testIsSet");
        assertFalse(record.isSet("first"));
        assertTrue(recordWithHeader.isSet("first"));
        assertFalse(recordWithHeader.isSet("fourth"));
        System.out.println("testIsSet");
    }

    @Test
    public void testIterator() {
    	System.out.println("testIterator begin");
        int i = 0;
        for (final String value : record) {
            assertEquals(values[i], value);
            i++;
        }
        System.out.println("testIterator end");
    }

    @Test
    public void testPutInMap() {
    	System.out.println("testPutInMap");
        final Map<String, String> map = new ConcurrentHashMap<String, String>();
        this.recordWithHeader.putIn(map);
        this.validateMap(map, false);
        // Test that we can compile with assigment to the same map as the param.
        final TreeMap<String, String> map2 = recordWithHeader.putIn(new TreeMap<String, String>());
        this.validateMap(map2, false);
    	System.out.println("testPutInMap");

    }

    @Ignore
    @Test
    public void testRemoveAndAddColumns() throws IOException {
    	System.out.println("testRemoveAndAddColumns");
        // do:
        final CSVPrinter printer = new CSVPrinter(new StringBuilder(), CSVFormat.DEFAULT);
        final Map<String, String> map = recordWithHeader.toMap();
        map.remove("OldColumn");
        map.put("ZColumn", "NewValue");
        // check:
        final ArrayList<String> list = new ArrayList<String>(map.values());
        Collections.sort(list);
        printer.printRecord(list);
        Assert.assertEquals("A,B,C,NewValue" + CSVFormat.DEFAULT.getRecordSeparator(), printer.getOut().toString());
        printer.close();
    	System.out.println("testRemoveAndAddColumns");

    }

    @Test
    public void testToMap() {
    	System.out.println("testToMap");
        final Map<String, String> map = this.recordWithHeader.toMap();
        this.validateMap(map, true);
        System.out.println("testToMap");
    }

    @Ignore
    @Test
    public void testToMapWithShortRecord() throws Exception {
    	System.out.println("testToMapWithShortRecord");
       final CSVParser parser =  CSVParser.parse("a,b", CSVFormat.DEFAULT.withHeader("A", "B", "C"));
       final CSVRecord shortRec = parser.iterator().next();
       shortRec.toMap();
       System.out.println("testToMapWithShortRecord");
    }

    @Ignore
    @Test
    public void testToMapWithNoHeader() throws Exception {
    	System.out.println("testToMapWithNoHeader");
       final CSVParser parser =  CSVParser.parse("a,b", CSVFormat.newFormat(','));
       final CSVRecord shortRec = parser.iterator().next();
       final Map<String, String> map = shortRec.toMap();
       assertNotNull("Map is not null.", map);
       assertTrue("Map is empty.", map.isEmpty());
   	System.out.println("testToMapWithNoHeader");

    }

    private void validateMap(final Map<String, String> map, final boolean allowsNulls) {
    	System.out.println("validateMap");
        assertTrue(map.containsKey("first"));
        assertTrue(map.containsKey("second"));
        assertTrue(map.containsKey("third"));
        assertFalse(map.containsKey("fourth"));
        if (allowsNulls) {
            assertFalse(map.containsKey(null));
        }
        assertEquals("A", map.get("first"));
        assertEquals("B", map.get("second"));
        assertEquals("C", map.get("third"));
        assertEquals(null, map.get("fourth"));
    	System.out.println("validateMap");
    }

}
