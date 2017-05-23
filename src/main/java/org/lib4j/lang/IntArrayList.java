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

public class IntArrayList {
  private int[] array;

  private int size;

  /**
   * Creates a list with a capacity of 5
   */
  public IntArrayList() {
    this(5);
  }

  /**
   * Creates a list with a set capacity
   *
   * @param size
   *          The initial capacity of the list
   */
  public IntArrayList(final int size) {
    array = new int[size];
  }

  /**
   * Creates a list with a set of values
   *
   * @param values
   *          The values for the list
   */
  public IntArrayList(final int ... values) {
    array = values;
    size = values.length;
  }

  /**
   * @return The number of elements in the list
   */
  public int size() {
    return size;
  }

  /**
   * @return Whether this list is empty of elements
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Clears this list, setting its size to 0
   */
  public void clear() {
    size = 0;
  }

  /**
   * Gets the value in the list at the given index
   *
   * @param index
   *          The index of the value to get
   * @return The value at the given index
   */
  public int get(final int index) {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException(index);

    return array[index];
  }

  /**
   * Adds a value to the end of this list
   *
   * @param value
   *          The value to add to the list
   */
  public void add(final int value) {
    ensureCapacity(size + 1);
    array[size++] = value;
  }

  /**
   * Adds a value to this list at the given index
   *
   * @param index
   *          The index to add the value at
   * @param value
   *          The value to add to the list
   */
  public void add(final int index, final int value) {
    if (index < 0 || index > size)
      throw new ArrayIndexOutOfBoundsException(index);

    ensureCapacity(size + 1);
    for (int i = size; i > index; i--)
      array[i] = array[i - 1];

    array[index] = value;
    size++;
  }

  /**
   * Adds an array of values to the end of this list
   *
   * @param value
   *          The values to add
   */
  public void addAll(final int ... value) {
    ensureCapacity(size + value.length);
    for (int i = 0; i < value.length; i++)
      array[size + i] = value[i];

    size += value.length;
  }

  /**
   * Adds a list of values to the end of this list
   *
   * @param list
   *          The list of values to add
   */
  public void addAll(final IntArrayList list) {
    ensureCapacity(size + list.size);
    for (int i = 0; i < list.size; i++)
      array[size + i] = list.array[i];

    size += list.size;
  }

  /**
   * Replaces a value in this list with another value
   *
   * @param index
   *          The index of the value to replace
   * @param value
   *          The value to replace the old value with
   * @return The old value at the given index
   */
  public int set(final int index, final int value) {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException(index);

    final int oldValue = array[index];
    array[index] = value;
    return oldValue;
  }

  /**
   * Removes a value from this list
   *
   * @param index
   *          The index of the value to remove
   * @return The value that was removed
   */
  public int remove(final int index) {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException(index);

    int ret = array[index];
    for (int i = index; i < size - 1; i++)
      array[i] = array[i + 1];

    size--;
    return ret;
  }

  /**
   * Removes a value from this list
   *
   * @param value
   *          The value to remove
   * @return Whether the value was found and removed
   */
  public boolean removeValue(final int value) {
    for (int i = 0; i < size; i++) {
      if (array[i] == value) {
        remove(i);
        return true;
      }
    }

    return false;
  }

  /**
   * Removes all instances of the given value from this list
   *
   * @param value
   *          The value to remove
   * @return The number of times the value was removed
   */
  public int removeAll(final int value) {
    int ret = 0;
    for (int i = 0; i < size; i++) {
      if (array[i] == value) {
        remove(i);
        i--;
        ret++;
      }
    }

    return ret;
  }

  /**
   * Determines if this list contains a given value
   *
   * @param value
   *          The value to find
   * @return Whether this list contains the given value
   */
  public boolean contains(final int value) {
    return indexOf(value) >= 0;
  }

  /**
   * Counts the number of times a value is represented in this list
   *
   * @param value
   *          The value to count
   * @return The number of times the value appears in this list
   */
  public int instanceCount(final int value) {
    int ret = 0;
    for (int i = 0; i < size; i++)
      if (array[i] == value)
        ret++;

    return ret;
  }

  /**
   * Returns the index of the first occurrence of the specified value
   * in this list, or -1 if this list does not contain the value.
   * More formally, returns the lowest index <tt>i</tt> such that
   * <tt>o == get(i)</tt>, or -1 if there is no such index.
   */
  public int indexOf(final int value) {
    for (int i = 0; i < size; i++)
      if (array[i] == value)
        return i;

    return -1;
  }

  /**
   * Returns the index of the last occurrence of the specified value
   * in this list, or -1 if this list does not contain the value.
   * More formally, returns the highest index <tt>i</tt> such that
   * <tt>o == get(i)</tt>, or -1 if there is no such index.
   */
  public int lastIndexOf(final int value) {
    for (int i = size - 1; i >= 0; i--)
      if (array[i] == value)
        return i;

    return -1;
  }

  /**
   * @return The list of values currently in this list
   */
  public int[] toArray() {
    final int[] ret = new int[size];
    System.arraycopy(array, 0, ret, 0, size);
    return ret;
  }

  /**
   * Similar to {@link #toArray()}, but creates an array of {@link Integer} wrappers
   *
   * @return The list of values currently in this list
   */
  public Integer[] toObjectArray() {
    final Integer[] objectArray = new Integer[size];
    for (int i = 0; i < objectArray.length; i++)
      objectArray[i] = new Integer(array[i]);

    return objectArray;
  }

  /**
   * Trims this list so that it wastes no space and its capacity is equal to its size
   */
  public void trimToSize() {
    if (array.length == size)
      return;

    final int[] oldArray = array;
    array = new int[size];
    System.arraycopy(oldArray, 0, array, 0, size);
  }

  /**
   * Ensures that this list's capacity is at list the given value
   *
   * @param minCapacity
   *          The minimum capacity for the list
   */
  public void ensureCapacity(final int minCapacity) {
    if (minCapacity > array.length) {
      final int[] oldData = array;
      final int newCapacity = Math.max((array.length * 3) / 2 + 1, minCapacity);
      array = new int[newCapacity];
      System.arraycopy(oldData, 0, array, 0, size);
    }
  }
}