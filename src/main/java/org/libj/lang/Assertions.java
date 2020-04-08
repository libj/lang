/* Copyright (c) 2019 LibJ
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

package org.libj.lang;

/**
 * Utility methods implementing common assertion operations.
 */
public final class Assertions {
  /**
   * Checks if the given {@code fromIndex} and {@code toIndex} are in range. If
   * not, throws an {@link ArrayIndexOutOfBoundsException} or
   * {@link IllegalArgumentException}.
   *
   * @param fromIndex The from index.
   * @param toIndex The to index.
   * @param length The array length.
   * @throws ArrayIndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @throws IllegalArgumentException If {@code fromIndex > toIndex}.
   */
  public static void assertRangeArray(final int fromIndex, final int toIndex, final int length) {
    if (fromIndex < 0)
      throw new ArrayIndexOutOfBoundsException(fromIndex);

    if (toIndex > length)
      throw new ArrayIndexOutOfBoundsException(toIndex);

    if (fromIndex > toIndex)
      throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
  }

  /**
   * Checks if the given index is in range. If not, throws an
   * {@link ArrayIndexOutOfBoundsException}.
   *
   * @param index The index to check.
   * @param length The size.
   * @throws ArrayIndexOutOfBoundsException If the given index is out of range.
   */
  public static void assertRangeArray(final int index, final int length) {
    if (index < 0 || length <= index)
      throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + length);
  }

  /**
   * Checks if the given index is in range. If not, throws an
   * {@link IndexOutOfBoundsException}.
   *
   * @param index The index to check.
   * @param length The size.
   * @throws IndexOutOfBoundsException If the given index is out of range.
   */
  public static void assertRange(final int index, final int length) {
    if (index < 0 || length <= index)
      throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
  }

  /**
   * Checks if the given {@code fromIndex} and {@code toIndex} are in range. If
   * not, throws an {@link IndexOutOfBoundsException} or
   * {@link IllegalArgumentException}.
   *
   * @param fromIndex The from index.
   * @param toIndex The to index.
   * @param size The size.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @throws IllegalArgumentException If {@code fromIndex > toIndex}.
   */
  public static void assertRange(final int fromIndex, final int toIndex, final int size) {
    if (fromIndex < 0)
      throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);

    if (toIndex > size)
      throw new IndexOutOfBoundsException("toIndex = " + toIndex);

    if (fromIndex > toIndex)
      throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
  }

  /**
   * Checks if the given index is in range. If not, throws an
   * {@link IndexOutOfBoundsException}.
   *
   * @param index The index to check.
   * @param size The size.
   * @param forAdd Whether the range check is for an add operation or not.
   * @throws IndexOutOfBoundsException If the given index is out of range.
   */
  public static void assertRange(final int index, final int size, final boolean forAdd) {
    if (index < 0 || (forAdd ? size < index : size <= index))
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
  }

  /**
   * Checks the given {@code offset} and {@code count} against {@code 0} and
   * {@code length} bounds.
   *
   * @param length The length of the range.
   * @param offset The offset in the range.
   * @param count The count in the range.
   * @param lengthLabel The string label of the "length" parameter.
   * @param offsetLabel The string label of the "offset" parameter.
   * @param countLabel The string label of the "count" parameter.
   * @throws IllegalArgumentException If {@code length} is negative.
   * @throws IndexOutOfBoundsException If {@code offset} is negative,
   *           {@code count} is negative, or {@code length} is less than
   *           {@code offset + count}.
   */
  public static void assertBoundsOffsetCount(final int length, final int offset, final int count, final String lengthLabel, final String offsetLabel, final String countLabel) {
    if (length < 0)
      throw new IllegalArgumentException(lengthLabel + " = " + length);

    if (offset < 0)
      throw new IndexOutOfBoundsException(offsetLabel + " = " + offset);

    if (count < 0)
      throw new IndexOutOfBoundsException(countLabel + " = " + count);

    if (length < offset + count)
      throw new IndexOutOfBoundsException(lengthLabel + " (" + length + ") < " + offsetLabel + " (" + offset + ") + " + countLabel + " (" + count + ")");
  }

  private Assertions() {
  }
}