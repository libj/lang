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
 * A {@link CharSequence} backed by a {@code char[]}.
 */
public class ArrayCharSequence implements CharSequence, Serializable {
  private static final long serialVersionUID = -3119966124596469581L;
  private final char[] buf;
  private final int offset;
  private final int count;

  private volatile int hashCode;
  private volatile boolean hashCodeInited;
  private transient volatile String str;

  /**
   * Creates a new {@link ArrayCharSequence} with the specified {@code char[]},
   * with the char sequence range as {@code 0} to {@code buf.length}.
   *
   * @param buf The {@code char[]}.
   * @throws NullPointerException If {@code buf} is null.
   */
  public ArrayCharSequence(final char[] buf) {
    this(buf, 0, buf.length);
  }

  /**
   * Creates a new {@link ArrayCharSequence} with the specified {@code char[]},
   * with the char sequence range as {@code 0} to {@code count}.
   *
   * @param buf The {@code char[]}.
   * @param count The count.
   * @throws IndexOutOfBoundsException If {@code count} is negative, or
   *           {@code buf.length} is less than {@code count}.
   * @throws NullPointerException If {@code buf} is null.
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
   * @throws NullPointerException If {@code buf} is null.
   */
  public ArrayCharSequence(final char[] buf, final int offset, final int count) {
    Assertions.assertBoundsOffsetCount("length", buf.length, "offset", offset, "count", count);
    this.buf = buf;
    this.offset = offset;
    this.count = count;
  }

  @Override
  public char charAt(final int index) {
    return buf[offset + index];
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
    return new ArrayCharSequence(buf, offset + start, end - start);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof ArrayCharSequence))
      return false;

    final ArrayCharSequence that = (ArrayCharSequence)obj;
    for (int i = 0; i < count; ++i)
      if (buf[offset + i] != that.buf[that.offset + i])
        return false;

    return true;
  }

  @Override
  public int hashCode() {
    if (hashCodeInited)
      return this.hashCode;

    int hashCode = 1;
    for (int i = offset, len = offset + count; i < len; ++i)
        hashCode = 31 * hashCode + buf[i];

    hashCodeInited = true;
    return this.hashCode = hashCode;
  }

  @Override
  public String toString() {
    return str == null ? str = new String(this.buf, this.offset, this.count) : str;
  }
}