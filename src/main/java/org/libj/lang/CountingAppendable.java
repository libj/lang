/* Copyright (c) 2023 Seva Safris, LibJ
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

import java.io.IOException;

/**
 * An {@link CountingAppendable} contains some other {@link Appendable}, to which it delegates its method calls to keep track of the
 * count of appended {@code char}s.
 */
public class CountingAppendable implements Appendable {
  protected final Appendable target;
  private long count;

  /**
   * Creates a {@link CountingAppendable} with the provided target {@link Appendable}.
   *
   * @param target The {@link Appendable}.
   * @throws IllegalArgumentException If {@code target} is null.
   */
  public CountingAppendable(final Appendable target) {
    this.target = assertNotNull(target);
  }

  /**
   * Returns the count of appended {@code char}s.
   *
   * @return The count of appended {@code char}s.
   */
  public long getCount() {
    return count;
  }

  @Override
  public Appendable append(final CharSequence csq) throws IOException {
    count += csq.length();
    target.append(csq);
    return this;
  }

  @Override
  public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
    count += end - start;
    target.append(csq, start, end);
    return this;
  }

  @Override
  public Appendable append(final char c) throws IOException {
    ++count;
    target.append(c);
    return this;
  }

  @Override
  public int hashCode() {
    return target.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    return target.equals(obj);
  }

  @Override
  public String toString() {
    return target.toString();
  }
}