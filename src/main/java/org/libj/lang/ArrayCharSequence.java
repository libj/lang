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

import java.io.Serializable;

/**
 * A {@link CharSequence} backed by a {@code String} or {@code char[]}.
 */
public class ArrayCharSequence implements CharSequence, Serializable {
  private static final long serialVersionUID = -3119966124596469581L;

  private final int offset;
  private final int count;

  private char[] buf;
  private String str;

  private int hashCode;
  private boolean hashCodeInited;

  /**
   * Creates a new {@link ArrayCharSequence} with the specified {@code char[]},
   * with the char sequence range as {@code 0} to {@code buf.length}.
   *
   * @param buf The {@code char[]}.
   * @throws IllegalArgumentException If {@code buf} is null.
   */
  public ArrayCharSequence(final char[] buf) {
    this(buf, null, 0, Assertions.assertNotNull(buf).length);
    Assertions.assertBoundsOffsetCount("length", buf.length, "offset", offset, "count", count);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified {@code char[]},
   * with the char sequence range as {@code 0} to {@code count}.
   *
   * @param buf The {@code char[]}.
   * @param count The count.
   * @throws IndexOutOfBoundsException If {@code count} is negative, or
   *           {@code buf.length} is less than {@code count}.
   * @throws IllegalArgumentException If {@code buf} is null.
   */
  public ArrayCharSequence(final char[] buf, final int count) {
    this(buf, 0, count);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified {@code char[]},
   * with the char sequence range as {@code offset} to {@code count}.
   *
   * @param buf The {@code char[]}.
   * @param offset The offset.
   * @param count The count.
   * @throws IndexOutOfBoundsException If {@code offset} is negative,
   *           {@code count} is negative, or {@code buf.length} is less than
   *           {@code offset + count}.
   * @throws IllegalArgumentException If {@code buf} is null.
   */
  public ArrayCharSequence(final char[] buf, final int offset, final int count) {
    this(buf, null, offset, count);
    Assertions.assertBoundsOffsetCount("length", Assertions.assertNotNull(buf).length, "offset", offset, "count", count);
  }

  private ArrayCharSequence(final char[] buf, final String str, final int offset, final int count) {
    this.buf = buf;
    this.str = str;
    this.offset = offset;
    this.count = count;
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified string, with the
   * char sequence range as {@code 0} to {@code buf.length}.
   *
   * @param str The string.
   * @throws IllegalArgumentException If {@code str} is null.
   */
  public ArrayCharSequence(final String str) {
    this(null, str, 0, Assertions.assertNotNull(str).length());
    Assertions.assertBoundsOffsetCount("length", str.length(), "offset", offset, "count", count);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified string, with the
   * char sequence range as {@code 0} to {@code count}.
   *
   * @param str The string.
   * @param count The count.
   * @throws IndexOutOfBoundsException If {@code count} is negative, or
   *           {@code str.length()} is less than {@code count}.
   * @throws IllegalArgumentException If {@code str} is null.
   */
  public ArrayCharSequence(final String str, final int count) {
    this(str, 0, count);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified string, with the
   * char sequence range as {@code offset} to {@code count}.
   *
   * @param str The string.
   * @param offset The offset.
   * @param count The count.
   * @throws IndexOutOfBoundsException If {@code offset} is negative,
   *           {@code count} is negative, or {@code str.length()} is less than
   *           {@code offset + count}.
   * @throws IllegalArgumentException If {@code str} is null.
   */
  public ArrayCharSequence(final String str, final int offset, final int count) {
    this(null, str, offset, count);
    Assertions.assertBoundsOffsetCount("length", Assertions.assertNotNull(str).length(), "offset", offset, "count", count);
  }

  @Override
  public char charAt(final int index) {
    return buf != null ? buf[offset + index] : str.charAt(index);
  }

  @Override
  public int length() {
    return count;
  }

  @Override
  public CharSequence subSequence(final int start, final int end) {
    if (start == 0 && end == count)
      return this;

    Assertions.assertRangeArray(start, end, count);
    return new ArrayCharSequence(buf, str, offset + start, end - start);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof ArrayCharSequence))
      return false;

    final ArrayCharSequence that = (ArrayCharSequence)obj;
    if (buf != null) {
      if (that.buf != null) {
        for (int i = 0; i < count; ++i)
          if (buf[offset + i] != that.buf[that.offset + i])
            return false;
      }
      else {
        for (int i = 0; i < count; ++i)
          if (buf[offset + i] != that.str.charAt(that.offset + i))
            return false;
      }
    }
    else {
      if (that.buf != null) {
        for (int i = 0; i < count; ++i)
          if (str.charAt(offset + i) != that.buf[that.offset + i])
            return false;
      }
      else {
        for (int i = 0; i < count; ++i)
          if (str.charAt(offset + i) != that.str.charAt(that.offset + i))
            return false;
      }
    }

    return true;
  }

  @Override
  public int hashCode() {
    if (hashCodeInited)
      return this.hashCode;

    int hashCode = 1;
    if (buf != null)
      for (int i = offset, len = offset + count; i < len; ++i)
        hashCode = 31 * hashCode + buf[i];
    else
      for (int i = offset, len = offset + count; i < len; ++i)
        hashCode = 31 * hashCode + str.charAt(i);


    hashCodeInited = true;
    return this.hashCode = hashCode;
  }

  @Override
  public String toString() {
    return str == null ? str = new String(buf, offset, count) : str;
  }
}