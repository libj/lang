/* Copyright (c) 2021 LibJ
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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

/**
 * An {@link Iterator} over an {@link Enumeration}.
 *
 * @param <E> The type parameter of the {@link Enumeration}.
 */
public class EnumerationIterator<E> implements Iterator<E> {
  private final Enumeration<E> enumeration;

  /**
   * Creates a new {@link EnumerationIterator} with the provided {@link Enumeration}.
   *
   * @param enumeration The {@link Enumeration} backing this {@link EnumerationIterator}.
   * @throws NullPointerException If {@code enumeration} is null.
   */
  public EnumerationIterator(final Enumeration<E> enumeration) {
    this.enumeration = Objects.requireNonNull(enumeration);
  }

  @Override
  public boolean hasNext() {
    return enumeration.hasMoreElements();
  }

  @Override
  public E next() {
    return enumeration.nextElement();
  }

  /**
   * Throws {@link UnsupportedOperationException}, because removing elements from an {@link Enumeration} is not supported.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}