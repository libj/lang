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

import static org.libj.lang.Assertions.*;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A list that wraps a java array. This class differentiates itself from the
 * implementation returned by {@link Arrays#asList(Object...)} by allowing
 * subclassing.
 *
 * @param <E> The type of elements in this list.
 */
public class WrappedArrayList<E> extends AbstractList<E> implements RandomAccess, Serializable, Cloneable {
  protected final E[] array;

  /**
   * Creates a new {@link WrappedArrayList} that wraps the provided {@code objs}
   * array.
   *
   * @param objs The array to wrap.
   * @throws IllegalArgumentException If {@code array} is null.
   */
  @SafeVarargs
  public WrappedArrayList(final E ... objs) {
    array = assertNotNull(objs);
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  public Object[] toArray() {
    return array.clone();
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException If {@code action} is null.
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T>T[] toArray(final T[] a) {
    final int size = size();
    if (assertNotNull(a).length < size)
      return Arrays.copyOf(this.array, size, (Class<? extends T[]>)a.getClass());

    System.arraycopy(this.array, 0, a, 0, size);
    if (a.length > size)
      a[size] = null;

    return a;
  }

  @Override
  public E get(final int index) {
    return array[index];
  }

  @Override
  public E set(final int index, final E element) {
    final E oldValue = array[index];
    array[index] = element;
    return oldValue;
  }

  @Override
  public int indexOf(final Object o) {
    final E[] a = this.array;
    if (o == null) {
      for (int i = 0; i < a.length; ++i)
        if (a[i] == null)
          return i;
    }
    else {
      for (int i = 0; i < a.length; ++i)
        if (o.equals(a[i]))
          return i;
    }

    return -1;
  }

  @Override
  @SuppressWarnings("unlikely-arg-type")
  public boolean contains(final Object o) {
    return indexOf(o) != -1;
  }

  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(array, Spliterator.ORDERED);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException If {@code action} is null.
   */
  @Override
  public void forEach(final Consumer<? super E> action) {
    assertNotNull(action);
    for (final E e : array)
      action.accept(e);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException If {@code operator} is null.
   */
  @Override
  public void replaceAll(final UnaryOperator<E> operator) {
    assertNotNull(operator);
    final E[] a = this.array;
    for (int i = 0; i < a.length; ++i)
      a[i] = operator.apply(a[i]);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException If {@code c} is null.
   */
  @Override
  public void sort(final Comparator<? super E> c) {
    Arrays.sort(assertNotNull(array), c);
  }

  @Override
  public WrappedArrayList<E> clone() {
    return new WrappedArrayList<>(array.clone());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof WrappedArrayList))
      return false;

    final WrappedArrayList<?> that = (WrappedArrayList<?>)obj;
    return Arrays.equals(array, that.array);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(array);
  }

  @Override
  public String toString() {
    return Arrays.toString(array);
  }
}