/* Copyright (c) 2006 lib4j
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.lib4j.lang;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lib4j.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassesTest {
  private static final Logger logger = LoggerFactory.getLogger(ClassesTest.class);

  private final Map<Class<?>[],Class<?>> classes = new HashMap<Class<?>[],Class<?>>();

  @Before
  public void setUp() {
    classes.put(new Class[] {String.class}, String.class);
    classes.put(new Class[] {String.class, Integer.class}, Object.class);
    classes.put(new Class[] {Long.class, Integer.class}, Number.class);
    classes.put(new Class[] {ArrayList.class, LinkedList.class}, AbstractList.class);
    classes.put(new Class[] {HashSet.class, LinkedHashSet.class}, HashSet.class);
    classes.put(new Class[] {FileInputStream.class, ByteArrayInputStream.class, DataInputStream.class, FilterInputStream.class}, InputStream.class);
  }

  @Test
  public void testGreatestCommonClass() throws Exception {
    for (final Map.Entry<Class<?>[],Class<?>> entry : classes.entrySet())
      Assert.assertEquals(Classes.getGreatestCommonSuperclass(entry.getKey()), entry.getValue());

    Assert.assertNull(Classes.getGreatestCommonSuperclass((Class<?>[])null));
  }

  @Test
  public void testGetCallingClasses() {
    final Class<?>[] classes = Classes.getCallingClasses();
    logger.info(Arrays.toString(classes, ", "));
  }

  protected static class Inn$r {
    protected static class $nner {
      protected static class $nner$ {
      }
    }
  }

  @Test
  public void testStrictGetName() throws Exception {
    Assert.assertEquals("java.lang.String", Classes.getStrictName(String.class));
    Assert.assertEquals(ClassesTest.class.getName() + ".Inn$r.$nner.$nner$", Classes.getStrictName(Inn$r.$nner.$nner$.class));
  }
}