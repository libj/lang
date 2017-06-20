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

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class Arrays {
  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static <T extends Comparable<T>>int binaryClosestSearch(final T[] a, final T key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static <T extends Comparable<T>>int binaryClosestSearch(final T[] a, final int from, final int to, final T key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static <T>int binaryClosestSearch(final T[] a, final T key, final Comparator<T> comparator) {
    return binaryClosestSearch0(a, 0, a.length, key, comparator);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static <T>int binaryClosestSearch(final T[] a, final int from, final int to, final T key, final Comparator<T> comparator) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key, comparator);
  }

  /**
   * Checks that {@code fromIndex} and {@code toIndex} are in
   * the range and throws an exception if they aren't.
   */
  private static void rangeCheck(final int arrayLength, final int fromIndex, final int toIndex) {
    if (fromIndex > toIndex)
      throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");

    if (fromIndex < 0)
      throw new ArrayIndexOutOfBoundsException(fromIndex);

    if (toIndex > arrayLength)
      throw new ArrayIndexOutOfBoundsException(toIndex);
  }

  private static <T extends Comparable<T>>int binaryClosestSearch0(final T[] a, final int from, final int to, final T key) {
    if (to == 0)
      return 0;

    int first = 0;
    int upto = to;
    int mid = -1;
    while (first < upto) {
      mid = (first + upto) / 2;    // Compute mid point.
      final int comparison = key.compareTo(a[mid]);
      if (comparison < 0)
        upto = mid;        // repeat search in bottom half.
      else if (comparison > 0)
        first = mid + 1;      // Repeat search in top half.
      else
        return mid;
    }

    return first == to - 1 && key.compareTo(a[first]) > 0 ? first + 1 : (first + upto) / 2;
  }

  private static <T>int binaryClosestSearch0(final T[] a, final int from, final int to, final T key, final Comparator<T> comparator) {
    if (to == 0)
      return 0;

    int first = 0;
    int upto = to;
    int mid = -1;
    while (first < upto) {
      mid = (first + upto) / 2;    // Compute mid point.
      final int comparison = comparator.compare(key, a[mid]);
      if (comparison < 0)
        upto = mid;        // repeat search in bottom half.
      else if (comparison > 0)
        first = mid + 1;      // Repeat search in top half.
      else
        return mid;
    }

    return first == to - 1 && comparator.compare(key, a[first]) > 0 ? first + 1 : (first + upto) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final byte[] a, final byte key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final byte[] a, final int from, final int to, final byte key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final byte[] a, final int from, final int to, final byte key) {
    if (to == 0)
      return 0;

    int first = 0;
    int upto = to;
    int mid = -1;
    while (first < upto) {
      mid = (first + upto) / 2;    // Compute mid point.
      if (key < a[mid])
        upto = mid;        // repeat search in bottom half.
      else if (key > a[mid])
        first = mid + 1;      // Repeat search in top half.
      else
        return mid;
    }

    return first == to - 1 && key > a[first] ? first + 1 : (first + upto) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final short[] a, final short key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final short[] a, final int from, final int to, final short key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final short[] a, final int from, final int to, final short key) {
    if (to == 0)
      return 0;

    int first = 0;
    int upto = to;
    int mid = -1;
    while (first < upto) {
      mid = (first + upto) / 2;    // Compute mid point.
      if (key < a[mid])
        upto = mid;        // repeat search in bottom half.
      else if (key > a[mid])
        first = mid + 1;      // Repeat search in top half.
      else
        return mid;
    }

    return first == to - 1 && key > a[first] ? first + 1 : (first + upto) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final int[] a, final int key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final int[] a, final int from, final int to, final int key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final int[] a, final int from, final int to, final int key) {
    if (to == 0)
      return 0;

    int first = 0;
    int upto = to;
    int mid = -1;
    while (first < upto) {
      mid = (first + upto) / 2;    // Compute mid point.
      if (key < a[mid])
        upto = mid;        // repeat search in bottom half.
      else if (key > a[mid])
        first = mid + 1;      // Repeat search in top half.
      else
        return mid;
    }

    return first == to - 1 && key > a[first] ? first + 1 : (first + upto) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final float[] a, final float key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final float[] a, final int from, final int to, final float key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final float[] a, final int from, final int to, final float key) {
    rangeCheck(a.length, from, to);

    if (to == 0)
      return 0;

    int first = 0;
    int upto = to;
    int mid = -1;
    while (first < upto) {
      mid = (first + upto) / 2;    // Compute mid point.
      if (key < a[mid])
        upto = mid;        // repeat search in bottom half.
      else if (key > a[mid])
        first = mid + 1;      // Repeat search in top half.
      else
        return mid;
    }

    return first == to - 1 && key > a[first] ? first + 1 : (first + upto) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final double[] a, final double key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final double[] a, final int from, final int to, final double key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final double[] a, final int from, final int to, final double key) {
    rangeCheck(a.length, from, to);

    if (to == 0)
      return 0;

    int first = 0;
    int upto = to;
    int mid = -1;
    while (first < upto) {
      mid = (first + upto) / 2;    // Compute mid point.
      if (key < a[mid])
        upto = mid;        // repeat search in bottom half.
      else if (key > a[mid])
        first = mid + 1;      // Repeat search in top half.
      else
        return mid;
    }

    return first == to - 1 && key > a[first] ? first + 1 : (first + upto) / 2;
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final long[] a, final long key) {
    return binaryClosestSearch0(a, 0, a.length, key);
  }

  /**
   * Find the index of the sorted array whose value most closely matches
   * the value provided.
   *
   * @param a The sorted array.
   * @param from The starting index of the sorted array to search from.
   * @param to The ending index of the sorted array to search to.
   * @param key The value to match.
   *
   * @return The closest index of the sorted array matching the desired value.
   */
  public static int binaryClosestSearch(final long[] a, final int from, final int to, final long key) {
    rangeCheck(a.length, from, to);
    return binaryClosestSearch0(a, from, to, key);
  }

  private static int binaryClosestSearch0(final long[] a, final int from, final int to, final long key) {
    rangeCheck(a.length, from, to);

    if (to == 0)
      return 0;

    int first = 0;
    int upto = to;
    int mid = -1;
    while (first < upto) {
      mid = (first + upto) / 2;    // Compute mid point.
      if (key < a[mid])
        upto = mid;        // repeat search in bottom half.
      else if (key > a[mid])
        first = mid + 1;      // Repeat search in top half.
      else
        return mid;
    }

    return first == to - 1 && key > a[first] ? first + 1 : (first + upto) / 2;
  }

  /**
   * Replace all members of the supplied array with the supplied <code>UnaryOperator</code>.
   *
   * @param operator The <code>UnaryOperator</code> that defines the replacement operation.
   * @param array The array whose members are to be replaced.
   * @return The the original array instance with its members replaced by the operator.
   */
  @SafeVarargs
  public static <T>T[] replaceAll(final UnaryOperator<T> operator, final T ... array) {
    for (int i = 0; i < array.length; i++)
      array[i] = operator.apply(array[i]);

    return array;
  }

  /**
   * Filter the supplied array with the supplied <code>Predicate</code>. This method
   * recursive walks the members of the array to attain highest runtime and memory
   * performance.
   *
   * @param precicate The <code>Predicate</code> that defines the filter.
   * @param array The array whose members are to be filtered.
   * @return A new array instance with members that pass the filter.
   */
  @SafeVarargs
  public static <T>T[] filter(final Predicate<T> precicate, final T ... array) {
    return filter0(precicate, 0, 0, array);
  }

  @SuppressWarnings("unchecked")
  private static <T>T[] filter0(final Predicate<T> precicate, final int index, final int depth, final T ... array) {
    if (index == array.length)
      return (T[])Array.newInstance(array.getClass().getComponentType(), depth);

    final boolean accept = precicate.test(array[index]);
    final T[] filtered = filter0(precicate, index + 1, accept ? depth + 1 : depth, array);
    if (accept)
      filtered[depth] = array[index];

    return filtered;
  }

  /**
   * Concatenate the supplied arrays of a common type into a single array.
   *
   * @param arrays The arrays to be concatenated.
   * @return The concatenated array.
   */
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

  /**
   * Create a new array by removing existing elements from the supplied array.
   *
   * @param array The array to be spliced.
   * @param start Index at the greater of which to remove all elements in the
   * array (with origin 0). If negative, index will be set to its calculated
   * value from the end of the array (with origin 1).
   * @return A new array with elements removed from the supplied array.
   */
  public static <T>T[] splice(final T[] array, int start) {
    if (start < 0)
      start += array.length;

    return splice(array, start, array.length - start);
  }

  /**
   * Create a new array by removing existing elements from the supplied array.
   *
   * @param array The array to be spliced.
   * @param start Index at which to begin changing the array (with origin 0).
   * If negative, index will be set to its calculated value from the end of the
   * array (with origin 1).
   * @param deleteCount An integer indicating the number of array elements to
   * remove. If deleteCount is 0, no elements are removed, but a new reference
   * to the array is returned.
   * @return A new array with elements removed from the supplied array.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] splice(final T[] array, int start, final int deleteCount) {
    if (deleteCount == 0)
      return array.clone();

    if (start < 0)
      start += array.length;

    final T[] spliced = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount);
    if (start != 0)
      System.arraycopy(array, 0, spliced, 0, start);

    if (start + deleteCount != array.length)
      System.arraycopy(array, start + deleteCount, spliced, start, array.length - start - deleteCount);

    return spliced;
  }

  /**
   * Create a new array by removing existing elements from the supplied array.
   *
   * @param array The array to be spliced.
   * @param start Index at which to begin changing the array (with origin 0).
   * If negative, index will be set to its calculated value from the end of the
   * array (with origin 1).
   * @param deleteCount An integer indicating the number of array elements to
   * remove. If deleteCount is 0, no elements are removed, but a new reference
   * to the array is returned.
   * @param items The elements to add to the array, beginning at the start
   * index.
   * @return A new array with elements removed from the supplied array.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] splice(final T[] array, int start, final int deleteCount, final T ... items) {
    if (items.length == 0)
      return splice(array, start, deleteCount);

    if (start < 0)
      start += array.length;

    final T[] spliced = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount + items.length);
    if (start != 0)
      System.arraycopy(array, 0, spliced, 0, start);

    System.arraycopy(items, 0, spliced, start, items.length);

    if (start + deleteCount != array.length)
      System.arraycopy(array, start + deleteCount, spliced, start + items.length, array.length - start - deleteCount);

    return spliced;
  }

  /**
   * Find the index of an object in an array.
   *
   * @param array The array to search.
   * @param obj The object to locate.
   * @return The index of the object if it is found, or -1 otherwise.
   */
  public static <T>int indexOf(final T[] array, final T obj) {
    for (int i = 0; i < array.length; i++)
      if (obj.equals(array[i]))
        return i;

    return -1;
  }

  /**
   * Check for the existence of an object in an array.
   *
   * @param array The array to search.
   * @param obj The object to locate.
   * @return <code>true</code> if the object exists, <code>false</code> otherwise.
   */
  public static <T>boolean contains(final T[] array, final T obj) {
    return indexOf(array, obj) >= 0;
  }

  /**
   * Create a <code>String</code> representation of the array by calling each
   * member's <code>toString()</code> method, delimited by the supplied delimiter
   * <code>char</code>.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited <code>toString()</code> representation of
   * the array.
   */
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

  /**
   * Create a <code>String</code> representation of the array by calling each
   * member's <code>toString()</code> method, delimited by the supplied delimiter
   * <code>char</code>.
   *
   * @param array The array.
   * @param delimiter The delimiter.
   * @return The delimiter delimited <code>toString()</code> representation of
   * the array.
   */
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

  /**
   * Fill the supplied array with +1 incremental values starting at the
   * supplied <code>start</code> value from array[0], to
   * array[array.length - 1].
   *
   * @param array The target array.
   * @param start The starting value.
   */
  public static void fillIncremental(final byte[] array, byte start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  /**
   * Fill the supplied array with +1 incremental values starting at the
   * supplied <code>start</code> value from array[0], to
   * array[array.length - 1].
   *
   * @param array The target array.
   * @param start The starting value.
   */
  public static void fillIncremental(final char[] array, char start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  /**
   * Fill the supplied array with +1 incremental values starting at the
   * supplied <code>start</code> value from array[0], to
   * array[array.length - 1].
   *
   * @param array The target array.
   * @param start The starting value.
   */
  public static void fillIncremental(final short[] array, short start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  /**
   * Fill the supplied array with +1 incremental values starting at the
   * supplied <code>start</code> value from array[0], to
   * array[array.length - 1].
   *
   * @param array The target array.
   * @param start The starting value.
   */
  public static void fillIncremental(final int[] array, int start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  /**
   * Fill the supplied array with +1 incremental values starting at the
   * supplied <code>start</code> value from array[0], to
   * array[array.length - 1].
   *
   * @param array The target array.
   * @param start The starting value.
   */
  public static void fillIncremental(final long[] array, long start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  /**
   * Fill the supplied array with +1 incremental values starting at the
   * supplied <code>start</code> value from array[0], to
   * array[array.length - 1].
   *
   * @param array The target array.
   * @param start The starting value.
   */
  public static void fillIncremental(final float[] array, float start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  /**
   * Fill the supplied array with +1 incremental values starting at the
   * supplied <code>start</code> value from array[0], to
   * array[array.length - 1].
   *
   * @param array The target array.
   * @param start The starting value.
   */
  public static void fillIncremental(final double[] array, double start) {
    for (int i = 0; i < array.length; i++)
      array[i] = start++;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  public static boolean[] createRepeat(final boolean value, final int length) {
    final boolean[] array = new boolean[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  public static byte[] createRepeat(final byte value, final int length) {
    final byte[] array = new byte[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  public static char[] createRepeat(final char value, final int length) {
    final char[] array = new char[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  public static double[] createRepeat(final double value, final int length) {
    final double[] array = new double[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  public static float[] createRepeat(final float value, final int length) {
    final float[] array = new float[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  public static int[] createRepeat(final int value, final int length) {
    final int[] array = new int[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  public static long[] createRepeat(final long value, final int length) {
    final long[] array = new long[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  public static short[] createRepeat(final short value, final int length) {
    final short[] array = new short[length];
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Create a new array by repeating the supplied <code>value</code> by the
   * supplied <code>length</code> number of times.
   *
   * @param value The value to repeat.
   * @param length The number of times to repeat the value.
   * @return A new array with <code>length</code> number of repeated
   * <code>value</code> members.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] createRepeat(final T value, final int length) {
    final T[] array = (T[])Array.newInstance(value.getClass(), length);
    java.util.Arrays.fill(array, value);
    return array;
  }

  /**
   * Returns an array that is the subArray of the supplied array. Calling this
   * method is the equivalent of calling
   * Arrays.subArray(array, beginIndex, array.length).
   *
   * @param array The specified <code>array</code>.
   * @param beginIndex The index to become the start of the new array.
   * @return The subArray of the specified <code>array</code>.
   */
  public static <T>T[] subArray(final T[] array, final int beginIndex) {
    return subArray(array, beginIndex, array.length);
  }

  /**
   * Returns an array that is the subArray of the supplied array.
   *
   * @param array The specified <code>array</code>.
   * @param beginIndex The index to become the start of the new array.
   * @param endIndex The index to become the end of the new array.
   * @return The subArray of the specified <code>array</code>.
   */
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