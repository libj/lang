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

import static org.libj.lang.Assertions.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A {@link CharSequence} backed by a {@code String} or {@code char[]}.
 */
public abstract class ArrayCharSequence implements CharSequence, Serializable {
  /**
   * Creates a new {@link ArrayCharSequence} with the specified {@code char[]}, with the char sequence range as {@code 0} to
   * {@code buf.length}.
   *
   * @param buf The {@code char[]}.
   * @return A new {@link ArrayCharSequence} with the specified {@code char[]}, with the char sequence range as {@code 0} to
   *         {@code buf.length}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static ArrayCharSequence of(final char[] buf) {
    return new ArrayCharSequenceBuf(buf, 0, buf.length);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified {@code char[]}, with the char sequence range as {@code 0} to
   * {@code count}.
   *
   * @param buf The {@code char[]}.
   * @param count The count.
   * @return A new {@link ArrayCharSequence} with the specified {@code char[]}, with the char sequence range as {@code 0} to
   *         {@code count}.
   * @throws IndexOutOfBoundsException If {@code count} is negative, or {@code buf.length} is less than {@code count}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static ArrayCharSequence of(final char[] buf, final int count) {
    assertBoundsOffsetCount("length", buf.length, "offset", 0, "count", count);
    return new ArrayCharSequenceBuf(buf, 0, count);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified {@code char[]}, with the char sequence range as {@code offset} to
   * {@code count}.
   *
   * @param buf The {@code char[]}.
   * @param offset The offset.
   * @param count The count.
   * @return A new {@link ArrayCharSequence} with the specified {@code char[]}, with the char sequence range as {@code offset} to
   *         {@code count}.
   * @throws IndexOutOfBoundsException If {@code offset} is negative, {@code count} is negative, or {@code buf.length} is less than
   *           {@code offset + count}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public static ArrayCharSequence of(final char[] buf, final int offset, final int count) {
    assertBoundsOffsetCount("length", buf.length, "offset", offset, "count", count);
    return new ArrayCharSequenceBuf(buf, offset, count);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified string, with the char sequence range as {@code 0} to
   * {@code buf.length}.
   *
   * @param str The string.
   * @return A new {@link ArrayCharSequence} with the specified string, with the char sequence range as {@code 0} to
   *         {@code buf.length}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static ArrayCharSequence of(final String str) {
    return new ArrayCharSequenceStr(str, 0, str.length());
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified string, with the char sequence range as {@code 0} to {@code count}.
   *
   * @param str The string.
   * @param count The count.
   * @return A new {@link ArrayCharSequence} with the specified string, with the char sequence range as {@code 0} to {@code count}.
   * @throws IndexOutOfBoundsException If {@code count} is negative, or {@code str.length()} is less than {@code count}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static ArrayCharSequence of(final String str, final int count) {
    assertBoundsOffsetCount("length", str.length(), "offset", 0, "count", count);
    return new ArrayCharSequenceStr(str, 0, count);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified string, with the char sequence range as {@code offset} to
   * {@code count}.
   *
   * @param str The string.
   * @param offset The offset.
   * @param count The count.
   * @return A new {@link ArrayCharSequence} with the specified string, with the char sequence range as {@code offset} to
   *         {@code count}.
   * @throws IndexOutOfBoundsException If {@code offset} is negative, {@code count} is negative, or {@code str.length()} is less than
   *           {@code offset + count}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static ArrayCharSequence of(final String str, final int offset, final int count) {
    assertBoundsOffsetCount("length", str.length(), "offset", offset, "count", count);
    return new ArrayCharSequenceStr(str, offset, count);
  }

  private static class ArrayCharSequenceBuf extends ArrayCharSequence {
    private final char[] buf;

    ArrayCharSequenceBuf(final char[] buf, final int offset, final int count) {
      super(offset, count);
      this.buf = buf;
    }

    @Override
    public char charAt(final int index) {
      return buf[offset + index];
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
      if (start == 0 && end == count)
        return this;

      assertRangeArray(start, end, count);
      return new ArrayCharSequenceBuf(buf, offset + start, end - start);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this)
        return true;

      if (!(obj instanceof ArrayCharSequenceBuf))
        return false;

      final ArrayCharSequenceBuf that = (ArrayCharSequenceBuf)obj;
      for (int i = 0; i < count; ++i) // [N]
        if (buf[offset + i] != that.buf[that.offset + i])
          return false;

      return true;
    }

    @Override
    public int hashCode() {
      int hashCode = 1;
      for (int i = offset, i$ = offset + count; i < i$; ++i) // [N]
        hashCode = 31 * hashCode + buf[i];

      return hashCode;
    }

    @Override
    public String toString() {
      return new String(buf, offset, count);
    }
  }

  private static class ArrayCharSequenceStr extends ArrayCharSequence {
    private final String str;

    ArrayCharSequenceStr(final String str, final int offset, final int count) {
      super(offset, count);
      this.str = Objects.requireNonNull(str);
    }

    @Override
    public char charAt(final int index) {
      return str.charAt(offset + index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
      if (start == 0 && end == count)
        return this;

      assertRangeArray(start, end, count);
      return new ArrayCharSequenceStr(str, offset + start, end - start);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this)
        return true;

      if (!(obj instanceof ArrayCharSequenceStr))
        return false;

      final ArrayCharSequenceStr that = (ArrayCharSequenceStr)obj;
      for (int i = 0; i < count; ++i) // [N]
        if (str.charAt(offset + i) != that.str.charAt(that.offset + i))
          return false;

      return true;
    }

    @Override
    public int hashCode() {
      int hashCode = 1;
      for (int i = offset, i$ = offset + count; i < i$; ++i) // [N]
        hashCode = 31 * hashCode + str.charAt(i);

      return hashCode;
    }

    private String toString;

    @Override
    public String toString() {
      return toString == null ? toString = str.substring(offset, offset + count) : toString;
    }
  }

  final int offset;
  final int count;

  ArrayCharSequence(final int offset, final int count) {
    this.offset = offset;
    this.count = count;
  }

  @Override
  public int length() {
    return count;
  }
}