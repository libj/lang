/* Copyright (c) 2014 lib4j
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepeatTest {
  private static final Logger logger = LoggerFactory.getLogger(RepeatTest.class);

  private static final Integer[] values1 = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 3, 4, 0, 0, 0, 5, 0, 0, 6, 0, 0, 7, 0, 0, 8};
  private static final Integer[] values2 = {0, 0, 0, 0, 0, 0, 0, 0};
  private static final Repeat.Filter<Integer> filter = new Repeat.Filter<Integer>() {
    @Override
    public boolean accept(final Integer member, final Object ... args) {
      return 1 == member || (3 < member && member < 7) || member == 8;
    }
  };

  private static String[] fieldNames = new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i"};

  public static class A {
    public int a;
    public int b;
    public int c;
  }

  public static class B extends A {
    public int d;
    public int e;
  }

  public static class C extends B {
  }

  public static class D extends C {
  }

  public static class F extends D {
    public int f;
    public int g;
  }

  public static class G extends F {
    public int h;
    public int i;
  }

  public static class H extends G {
  }

  public static Field[] getFieldsDeep(final Class<?> clazz) {
    return Repeat.Recursive.inverted(clazz, clazz.getDeclaredFields(), Field.class, new Repeat.Recurser<Field,Class<?>>() {
      @Override
      public boolean accept(final Field member, final Object ... args) {
        return Modifier.isPublic((member).getModifiers());
      }

      @Override
      public Field[] members(final Class<?> clazz) {
        return clazz.getDeclaredFields();
      }

      @Override
      public Class<?> next(final Class<?> clazz) {
        return clazz.getSuperclass();
      }
    });
  }

  @Test
  public void testDeepRecursive() {
    final Field[] fields = getFieldsDeep(H.class);
    Assert.assertEquals(fieldNames.length, fields.length);
    for (int i = 0; i < fields.length; i++)
      Assert.assertEquals(fieldNames[i], fields[i].getName());
  }

  @Test
  public void testIterative() {
    System.gc();

    Integer[] array = null;
    long start = System.currentTimeMillis();
    long mem = Runtime.getRuntime().freeMemory();
    for (int i = 0; i < 10000000; i++)
      array = Repeat.<Integer>iterative(values1, Integer.class, filter);

    logger.info("iterative: " + (System.currentTimeMillis() - start) + "ms " + (mem - Runtime.getRuntime().freeMemory()) + " bytes");
    Assert.assertArrayEquals(new Integer[] {1, 4, 5, 6, 8}, array);

    array = Repeat.<Integer>iterative(values2, Integer.class, filter);
    Assert.assertEquals(0, array.length);
  }

  @Test
  public void testRecursive() {
    System.gc();

    Integer[] array = null;
    final long start = System.currentTimeMillis();
    final long mem = Runtime.getRuntime().freeMemory();
    for (int i = 0; i < 10000000; i++)
      array = Repeat.Recursive.<Integer>ordered(values1, Integer.class, filter);

    logger.info("recursive: " + (System.currentTimeMillis() - start) + "ms " + (mem - Runtime.getRuntime().freeMemory()) + " bytes");
    Assert.assertArrayEquals(new Integer[] {1, 4, 5, 6, 8}, array);

    array = Repeat.Recursive.<Integer>ordered(values2, Integer.class, filter);
    Assert.assertEquals(0, array.length);
  }
}