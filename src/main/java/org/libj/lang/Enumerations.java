/* Copyright (c) 2016 LibJ
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility functions for operations pertaining to {@link Enumeration}.
 */
public final class Enumerations {
  @SuppressWarnings("unchecked")
  private static <T>T[] recurse(final Class<T> componentType, final Enumeration<? extends T> enumeration, final int depth) {
    if (!enumeration.hasMoreElements())
      return (T[])Array.newInstance(componentType, depth);

    final T element = enumeration.nextElement();
    final T[] array = recurse(componentType, enumeration, depth + 1);
    array[depth] = element;
    return array;
  }

  /**
   * Returns an array of type {@code <T>} containing the object references in
   * the specified {@link Enumeration}.
   * <p>
   * <b>Note:</b> This implementation uses a recursive algorithm for optimal
   * performance, and may fail if the specified {@link Enumeration} contains
   * ~8000+ elements.
   *
   * @param <T> The type parameter of the specified {@link Class} and
   *          {@link Enumeration}.
   * @param componentType The class for the type {@code <T>}.
   * @param enumeration The {@link Enumeration}.
   * @return An array of type {@code T} containing the object references in the
   *         specified {@link Enumeration}.
   */
  public static <T>T[] toArray(final Class<T> componentType, final Enumeration<? extends T> enumeration) {
    return recurse(componentType, enumeration, 0);
  }

  /**
   * Returns a {@link List} of type {@code <T>} containing the object references
   * in the specified {@link Enumeration}.
   * <p>
   * <b>Note:</b> This implementation uses a recursive algorithm for optimal
   * performance, and may fail if the specified {@link Enumeration} contains
   * ~8000+ elements.
   *
   * @param <T> The type parameter of the specified {@link Class} and
   *          {@link Enumeration}.
   * @param componentType The class for the type {@code <T>}.
   * @param enumeration The {@link Enumeration}.
   * @return A {@link List} of type {@code T} containing the object references
   *         in the specified {@link Enumeration}.
   */
  public static <T>List<T> toList(final Class<T> componentType, final Enumeration<? extends T> enumeration) {
    return Arrays.asList(toArray(componentType, enumeration));
  }

  /**
   * Returns an {@link Enumeration} containing only the specified object.
   *
   * @param <T> The type of the object in the {@link Enumeration}.
   * @param o The sole object to be stored in the returned set.
   * @return An {@link Enumeration} containing only the specified object.
   */
  public static <T>Enumeration<T> singleton(final T o) {
    return new Enumeration<T>() {
      private boolean hasNext = true;

      @Override
      public boolean hasMoreElements() {
        return hasNext;
      }

      @Override
      public T nextElement() {
        hasNext = false;
        return o;
      }
    };
  }

  private Enumerations() {
  }
}