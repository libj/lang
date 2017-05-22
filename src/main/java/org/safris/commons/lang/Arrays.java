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

package org.safris.commons.lang;

import java.lang.reflect.Array;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class Arrays {
  @SafeVarargs
  public static <T>T[] replaceAll(final UnaryOperator<T> operator, final T ... array) {
    for (int i = 0; i < array.length; i++)
      array[i] = operator.apply(array[i]);

    return array;
  }

  @SafeVarargs
  public static <T>T[] filter(final Predicate<T> precicate, final T ... array) {
    return filter(precicate, 0, 0, array);
  }

  @SuppressWarnings("unchecked")
  private static <T>T[] filter(final Predicate<T> precicate, final int index, final int depth, final T ... array) {
    if (index == array.length)
      return (T[])Array.newInstance(array.getClass().getComponentType(), depth);

    final boolean accept = precicate.test(array[index]);
    final T[] filtered = filter(precicate, index + 1, accept ? depth + 1 : depth, array);
    if (accept)
      filtered[depth] = array[index];

    return filtered;
  }

  @SuppressWarnings("unchecked")
  public static <T>T[] concat(final T[] ... arrays) {
    int length = 0;
    for (final T[] array : arrays)
      length += array.length;

    final T[] concat = (T[])Array.newInstance(arrays[0].getClass().getComponentType(), length);
    for (int i = 0, l = 0; i < arrays.length; l += arrays[i].length, i++)
      System.arraycopy(arrays[i], 0, concat, l, arrays[i].length);

    return concat;
  }

  public static <T>T[] splice(final T[] array, int start) {
    if (start < 0)
      start += array.length;

    return splice(array, start, array.length - start);
  }

  @SuppressWarnings("unchecked")
  public static <T>T[] splice(final T[] array, int start, final int deleteCount) {
    if (start < 0)
      start += array.length;

    final T[] spliced = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount);
    if (start != 0)
      System.arraycopy(array, 0, spliced, 0, start);

    if (start + deleteCount != array.length)
      System.arraycopy(array, start + deleteCount, spliced, start, array.length - start - deleteCount);

    return spliced;
  }

  @SuppressWarnings("unchecked")
  public static <T>T[] splice(final T[] array, int start, final int deleteCount, final T ... items) {
    if (start < 0)
      start += array.length;

    final T[] spliced = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount + items.length);
    if (start != 0)
      System.arraycopy(array, 0, spliced, 0, start);

    if (items.length > 0)
      System.arraycopy(items, 0, spliced, start, items.length);

    if (start + deleteCount != array.length)
      System.arraycopy(array, start + deleteCount, spliced, start + items.length, array.length - start - deleteCount);

    return spliced;
  }

  public static <T>int indexOf(final T[] array, final T obj) {
    for (int i = 0; i < array.length; i++)
      if (obj.equals(array[i]))
        return i;

    return -1;
  }

  public static <T>boolean contains(final T[] array, final T obj) {
    return indexOf(array, obj) >= 0;
  }

  public static String toString(final Object[] array, final char delimiter) {
    if (array == null)
      return null;

    if (array.length == 0)
      return "";

    if (array.length == 1)
      return String.valueOf(array[0]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[0]));
    for (int i = 1; i < array.length; i++)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  public static String toString(final Object[] array, String delimiter) {
    if (array == null)
      return null;

    if (delimiter == null)
      delimiter = "";

    if (array.length == 0)
      return "";

    if (array.length == 1)
      return String.valueOf(array[0]);

    final StringBuilder buffer = new StringBuilder(String.valueOf(array[0]));
    for (int i = 1; i < array.length; i++)
      buffer.append(delimiter).append(String.valueOf(array[i]));

    return buffer.toString();
  }

  public static void fillIncremental(final byte[] array, byte start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  public static void fillIncremental(final char[] array, char start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  public static void fillIncremental(final short[] array, short start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  public static void fillIncremental(final int[] array, int start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  public static void fillIncremental(final long[] array, long start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  public static void fillIncremental(final float[] array, float start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  public static void fillIncremental(final double[] array, double start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  public static boolean[] createRepeat(final boolean ch, final int length) {
    final boolean[] array = new boolean[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static byte[] createRepeat(final byte ch, final int length) {
    final byte[] array = new byte[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static char[] createRepeat(final char ch, final int length) {
    final char[] array = new char[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static double[] createRepeat(final double ch, final int length) {
    final double[] array = new double[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static float[] createRepeat(final float ch, final int length) {
    final float[] array = new float[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static int[] createRepeat(final int ch, final int length) {
    final int[] array = new int[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static long[] createRepeat(final long ch, final int length) {
    final long[] array = new long[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static short[] createRepeat(final short ch, final int length) {
    final short[] array = new short[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static Object[] createRepeat(final Object ch, final int length) {
    final Object[] array = new Object[length];
    java.util.Arrays.fill(array, ch);
    return array;
  }

  public static <T>T[] subArray(final T[] array, final int beginIndex) {
    return subArray(array, beginIndex, array.length);
  }

  @SuppressWarnings("unchecked")
  public static <T>T[] subArray(final T[] array, final int beginIndex, final int endIndex) {
    if (endIndex < beginIndex)
      throw new IllegalArgumentException("endIndex < beginIndex");

    final Class<?> componentType = array.getClass().getComponentType();
    final T[] subArray = (T[])Array.newInstance(componentType, endIndex - beginIndex);
    if (beginIndex == endIndex)
      return subArray;

    System.arraycopy(array, beginIndex, subArray, 0, endIndex - beginIndex);
    return subArray;
  }

  private Arrays() {
  }
}