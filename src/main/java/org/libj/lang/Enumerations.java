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

import static org.libj.lang.Assertions.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility functions for operations pertaining to {@link Enumeration}.
 */
public final class Enumerations {
  @SuppressWarnings("unchecked")
  private static <E>E[] recurse(final Enumeration<? extends E> enumeration, final E[] dest, final Class<E> componentType, final int depth) {
    if (!enumeration.hasMoreElements())
      return dest != null && dest.length >= depth ? dest : depth == 0 ? null : (E[])Array.newInstance(componentType, depth);

    final E element = enumeration.nextElement();
    final E[] array = recurse(enumeration, dest, componentType, depth + 1);
    array[depth] = element;
    return array;
  }

  @SuppressWarnings("unchecked")
  private static <T,E>E[] recurse(final Enumeration<? extends T> enumeration, final E[] dest, final Class<E> componentType, final Function<T,E> function, final int depth) {
    if (!enumeration.hasMoreElements())
      return dest != null && dest.length >= depth ? dest : depth == 0 ? null : (E[])Array.newInstance(componentType, depth);

    final E element = function.apply(enumeration.nextElement());
    final E[] array = recurse(enumeration, dest, componentType, function, depth + 1);
    array[depth] = element;
    return array;
  }

  /**
   * Returns an array of type {@code <T>} containing the object references in the provided {@link Enumeration}, or {@code null} if
   * the provided {@link Enumeration} was empty.
   *
   * @implNote This implementation uses a recursive algorithm for optimal performance, and may fail if the provided
   *           {@link Enumeration} contains ~8000+ elements.
   * @param <T> The type parameter of the provided {@link Class} and {@link Enumeration}.
   * @param componentType The class for the type {@code <T>}.
   * @param enumeration The {@link Enumeration}.
   * @return An array of type {@code T} containing the object references in the provided {@link Enumeration}, or {@code null} if the
   *         provided {@link Enumeration} was empty.
   * @throws IllegalArgumentException If {@code enumeration} or {@code componentType} is null.
   */
  public static <T>T[] toArray(final Enumeration<? extends T> enumeration, final Class<T> componentType) {
    return recurse(assertNotNull(enumeration), null, assertNotNull(componentType), 0);
  }

  /**
   * Returns an array of type {@code <T>} containing the object references in the provided {@link Enumeration}, or {@code null} if
   * the provided {@link Enumeration} was empty.
   *
   * @implNote This implementation uses a recursive algorithm for optimal performance, and may fail if the provided
   *           {@link Enumeration} contains ~8000+ elements.
   * @param <T> The type parameter of the provided {@link Enumeration}.
   * @param <E> The type parameter of the provided {@link Class} and return array.
   * @param componentType The class for the type {@code <T>}.
   * @param enumeration The {@link Enumeration}.
   * @param function The {@link Function} to translate objects of type {@code <T>} to type {@code <E>}.
   * @return An array of type {@code T} containing the object references in the provided {@link Enumeration}, or {@code null} if the
   *         provided {@link Enumeration} was empty.
   * @throws IllegalArgumentException If {@code enumeration}, {@code componentType} or {@code function} is null.
   */
  public static <T,E>E[] toArray(final Enumeration<? extends T> enumeration, final Class<E> componentType, final Function<T,E> function) {
    return recurse(assertNotNull(enumeration), null, assertNotNull(componentType), function, 0);
  }

  /**
   * Returns an array of type {@code <T>} containing the object references in the provided {@link Enumeration}.
   *
   * @implNote This implementation uses a recursive algorithm for optimal performance, and may fail if the provided
   *           {@link Enumeration} contains ~8000+ elements.
   * @param <T> The type parameter of the provided {@link Class} and {@link Enumeration}.
   * @param enumeration The {@link Enumeration}.
   * @param array The array into which the elements of the provided {@link Enumeration} are to be stored, if it is big enough;
   *          otherwise, a new array of the same runtime type is allocated for this purpose.
   * @return An array of type {@code T} containing the object references in the provided {@link Enumeration}.
   * @throws IllegalArgumentException If {@code enumeration} or {@code array} is null.
   */
  @SuppressWarnings("unchecked")
  public static <T>T[] toArray(final Enumeration<? extends T> enumeration, final T[] array) {
    return recurse(assertNotNull(enumeration), assertNotNull(array), (Class<T>)array.getClass().getComponentType(), 0);
  }

  /**
   * Returns an array of type {@code <T>} containing the object references in the provided {@link Enumeration}.
   *
   * @implNote This implementation uses a recursive algorithm for optimal performance, and may fail if the provided
   *           {@link Enumeration} contains ~8000+ elements.
   * @param <T> The type parameter of the provided {@link Enumeration}.
   * @param <E> The type parameter of the provided {@link Class} and return array.
   * @param enumeration The {@link Enumeration}.
   * @param function The {@link Function} to translate objects of type {@code <T>} to type {@code <E>}.
   * @param array The array into which the elements of the provided {@link Enumeration} are to be stored, if it is big enough;
   *          otherwise, a new array of the same runtime type is allocated for this purpose.
   * @return An array of type {@code T} containing the object references in the provided {@link Enumeration}.
   * @throws IllegalArgumentException If {@code enumeration}, {@code array} or {@code function} is null.
   */
  @SuppressWarnings("unchecked")
  public static <T,E>E[] toArray(final Enumeration<? extends T> enumeration, final E[] array, final Function<T,E> function) {
    return recurse(assertNotNull(enumeration), assertNotNull(array), (Class<E>)array.getClass().getComponentType(), function, 0);
  }

  /**
   * Returns a {@link List} of type {@code <T>} containing the object references in the specified {@link Enumeration}.
   *
   * @implNote This implementation uses a recursive algorithm for optimal performance, and may fail if the provided
   *           {@link Enumeration} contains ~8000+ elements.
   * @param <T> The type parameter of the specified {@link Class} and {@link Enumeration}.
   * @param componentType The class for the type {@code <T>}.
   * @param enumeration The {@link Enumeration}.
   * @return A {@link List} of type {@code T} containing the object references in the specified {@link Enumeration}.
   * @throws IllegalArgumentException If {@code enumeration} or {@code componentType} is null.
   */
  public static <T>List<T> asList(final Enumeration<? extends T> enumeration, final Class<T> componentType) {
    return Arrays.asList(toArray(enumeration, componentType));
  }

  /**
   * Returns a {@link Stream} containing the object references in the specified {@link Enumeration}.
   *
   * @implNote This implementation traverses provided {@link Enumeration} lazily.
   * @param <T> The type parameter of the specified {@link Class} and {@link Enumeration}.
   * @param enumeration The {@link Enumeration}.
   * @return A {@link Stream} containing the object references in the specified {@link Enumeration}.
   * @throws IllegalArgumentException If {@code enumeration} is null.
   */
  public static <T>Stream<T> asStream(final Enumeration<T> enumeration) {
    assertNotNull(enumeration);
    return StreamSupport.stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
      @Override
      public boolean tryAdvance(final Consumer<? super T> action) {
        if (!enumeration.hasMoreElements())
          return false;

        action.accept(enumeration.nextElement());
        return true;
      }

      @Override
      public void forEachRemaining(final Consumer<? super T> action) {
        while (enumeration.hasMoreElements())
          action.accept(enumeration.nextElement());
      }
    }, false);
  }

  /**
   * Returns the size of the provided {@link Enumeration}.
   *
   * @param enumeration The {@link Enumeration}.
   * @return The size of the provided {@link Enumeration}.
   * @throws IllegalArgumentException If {@code enumeration} is null.
   */
  public static int getSize(final Enumeration<?> enumeration) {
    assertNotNull(enumeration);
    int size = 0;
    for (; enumeration.hasMoreElements(); enumeration.nextElement(), ++size); // [E]
    return size;
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