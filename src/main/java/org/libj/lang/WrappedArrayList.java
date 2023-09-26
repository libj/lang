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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A list that wraps a java array. This class differentiates itself from the implementation returned by
 * {@link Arrays#asList(Object...)} by allowing subclassing.
 *
 * @param <E> The type of elements in this list.
 */
public class WrappedArrayList<E> extends ArrayList<E> {
  /**
   * The empty {@link WrappedArrayList} (immutable). This list is serializable.
   */
  @SuppressWarnings("rawtypes")
  public static final WrappedArrayList EMPTY_LIST = new WrappedArrayList<>();

  protected final E[] elementData;
  private final int size;

  /**
   * Creates a new {@link WrappedArrayList} that wraps the provided {@code objs} array.
   *
   * @param objs The array to wrap.
   * @throws NullPointerException If {@code objs} is null.
   */
  @SafeVarargs
  public WrappedArrayList(final E ... objs) {
    elementData = Objects.requireNonNull(objs);
    size = objs.length;
  }

  @Override
  public int size() {
    return elementData.length;
  }

  @Override
  public Object[] toArray() {
    return elementData.clone();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(final T[] a) {
    final int size = this.size;
    if (a.length < size)
      return Arrays.copyOf(this.elementData, size, (Class<? extends T[]>)a.getClass());

    if (size > 0)
      System.arraycopy(this.elementData, 0, a, 0, size);

    if (a.length > size)
      a[size] = null;

    return a;
  }

  @Override
  public E get(final int index) {
    return elementData[index];
  }

  @Override
  public E set(final int index, final E element) {
    final E oldValue = elementData[index];
    elementData[index] = element;
    return oldValue;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public int indexOf(final Object o) {
    final E[] a = this.elementData;
    if (o == null) {
      for (int i = 0, i$ = a.length; i < i$; ++i) // [A]
        if (a[i] == null)
          return i;
    }
    else {
      for (int i = 0, i$ = a.length; i < i$; ++i) // [A]
        if (o.equals(a[i]))
          return i;
    }

    return -1;
  }

  @Override
  public boolean contains(final Object o) {
    return indexOf(o) != -1;
  }

  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(elementData, Spliterator.ORDERED);
  }

  @Override
  public void forEach(final Consumer<? super E> action) {
    for (final E e : elementData) // [A]
      action.accept(e);
  }

  @Override
  public void replaceAll(final UnaryOperator<E> operator) {
    final E[] a = this.elementData;
    for (int i = 0, i$ = a.length; i < i$; ++i) // [A]
      a[i] = operator.apply(a[i]);
  }

  @Override
  public void sort(final Comparator<? super E> c) {
    Arrays.sort(elementData, c);
  }

  @Override
  public WrappedArrayList<E> clone() {
    return new WrappedArrayList<>(elementData.clone());
  }

  @Override
  public boolean equals(final Object obj) {
    return obj == this || obj instanceof List && equalsRange((List<?>)obj, 0, size);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(elementData);
  }

  @Override
  public String toString() {
    return Arrays.toString(elementData);
  }

  @Override
  public void trimToSize() {
  }

  @Override
  public void ensureCapacity(final int minCapacity) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(final E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(final int index, final E element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(final int index, final Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public E remove(final int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void removeRange(final int fromIndex, final int toIndex) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeIf(final Predicate<? super E> filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<E> iterator() {
    return new Itr();
  }

  @Override
  public ListIterator<E> listIterator() {
    return new ListItr(0);
  }

  @Override
  public ListIterator<E> listIterator(final int index) {
    assertRange("index", index, "size", size, true);
    return new ListItr(index);
  }

  /**
   * An optimized version of AbstractList.Itr
   */
  private class Itr implements Iterator<E> {
    int cursor; // index of next element to return
    int lastRet = -1; // index of last element returned; -1 if no such

    // prevent creating a synthetic constructor
    Itr() {
    }

    @Override
    public boolean hasNext() {
      return cursor != size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E next() {
      final int i = cursor;
      if (i >= size)
        throw new NoSuchElementException();

      final Object[] elementData = WrappedArrayList.this.elementData;
      cursor = i + 1;
      return (E)elementData[lastRet = i];
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEachRemaining(final Consumer<? super E> action) {
      Objects.requireNonNull(action);
      final int size = WrappedArrayList.this.size;
      int i = cursor;
      if (i < size) {
        final Object[] es = elementData;
        for (; i < size; ++i) // [A]
          action.accept((E)es[i]);

        // update once at end to reduce heap write traffic
        cursor = i;
        lastRet = i - 1;
      }
    }
  }

  private class ListItr extends Itr implements ListIterator<E> {
    ListItr(int index) {
      super();
      cursor = index;
    }

    @Override
    public boolean hasPrevious() {
      return cursor != 0;
    }

    @Override
    public int nextIndex() {
      return cursor;
    }

    @Override
    public int previousIndex() {
      return cursor - 1;
    }

    @Override
    public E previous() {
      final int i = cursor - 1;
      if (i < 0)
        throw new NoSuchElementException();

      final E[] elementData = WrappedArrayList.this.elementData;
      cursor = i;
      return elementData[lastRet = i];

    }

    @Override
    public void set(final E e) {
      if (lastRet < 0)
        throw new IllegalStateException();

      WrappedArrayList.this.set(lastRet, e);
    }

    @Override
    public void add(final E e) {
      final int i = cursor;
      WrappedArrayList.this.add(i, e);
      cursor = i + 1;
    }
  }

  @Override
  public List<E> subList(final int fromIndex, final int toIndex) {
    assertRange("fromIndex", fromIndex, "toIndex", toIndex, "size", size);
    return new SubList<>(this, fromIndex, toIndex);
  }

  private int indexOfRange(final Object o, final int start, final int end) {
    final Object[] es = elementData;
    if (o == null) {
      for (int i = start; i < end; ++i) // [A]
        if (es[i] == null)
          return i;
    }
    else {
      for (int i = start; i < end; ++i) // [A]
        if (o.equals(es[i]))
          return i;
    }

    return -1;
  }

  private int lastIndexOfRange(final Object o, final int start, final int end) {
    final Object[] es = elementData;
    if (o == null) {
      for (int i = end - 1; i >= start; --i) // [A]
        if (es[i] == null)
          return i;
    }
    else {
      for (int i = end - 1; i >= start; --i) // [A]
        if (o.equals(es[i]))
          return i;
    }

    return -1;
  }

  private boolean equalsRange(final List<?> other, int from, final int to) {
    final int o$ = other.size();
    if (o$ == 0)
      return from == to;

    if (from == to)
      return false;

    final Object[] es = elementData;
    if (other instanceof RandomAccess) {
      int o = 0;
      do // [RA]
        if (o == o$ || !Objects.equals(es[from], other.get(o++)))
          return false;
      while (++from < to);
      return o == o$;
    }

    final Iterator<?> oit = other.iterator();
    do // [I]
      if (!oit.hasNext() || !Objects.equals(es[from++], oit.next()))
        return false;
    while (oit.hasNext());
    return true;
  }

  private int hashCodeRange(final int from, final int to) {
    final Object[] es = elementData;

    int hashCode = 1;
    for (int i = from; i < to; ++i) { // [A]
      final Object e = es[i];
      hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
    }

    return hashCode;
  }

  private static class SubList<E> extends AbstractList<E> implements RandomAccess {
    private final WrappedArrayList<E> root;
    private final int offset;
    private int size;

    /**
     * Constructs a sublist of an arbitrary ArrayList.
     */
    private SubList(final WrappedArrayList<E> root, final int fromIndex, final int toIndex) {
      this.root = root;
      this.offset = fromIndex;
      this.size = toIndex - fromIndex;
    }

    /**
     * Constructs a sublist of another SubList.
     */
    private SubList(final SubList<E> parent, final int fromIndex, final int toIndex) {
      this.root = parent.root;
      this.offset = parent.offset + fromIndex;
      this.size = toIndex - fromIndex;
      this.modCount = parent.modCount;
    }

    @Override
    public E set(final int index, final E element) {
      throw new UnsupportedOperationException();
    }

    @Override
    public E get(final int index) {
      assertRange("index", index, "size", size);
      return root.elementData[offset + index];
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public void add(final int index, final E element) {
      throw new UnsupportedOperationException();
    }

    @Override
    public E remove(final int index) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(final UnaryOperator<E> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
      return Arrays.copyOfRange(root.elementData, offset, offset + size);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(final T[] a) {
      final int size = this.size;
      if (a.length < size)
        return (T[])Arrays.copyOfRange(root.elementData, offset, offset + size, a.getClass());

      System.arraycopy(root.elementData, offset, a, 0, size);
      if (a.length > size)
        a[size] = null;

      return a;
    }

    @Override
    public boolean equals(final Object o) {
      return o == this || o instanceof List && root.equalsRange((List<?>)o, offset, offset + size);
    }

    @Override
    public int hashCode() {
      return root.hashCodeRange(offset, offset + size);
    }

    @Override
    public int indexOf(final Object o) {
      final int index = root.indexOfRange(o, offset, offset + size);
      return index >= 0 ? index - offset : -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
      final int index = root.lastIndexOfRange(o, offset, offset + size);
      return index >= 0 ? index - offset : -1;
    }

    @Override
    public boolean contains(final Object o) {
      return indexOf(o) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
      return listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
      assertRange("index", index, "size", size, true);
      return new ListIterator<E>() {
        int cursor = index;
        int lastRet = -1;
        int expectedModCount = SubList.this.modCount;

        @Override
        public boolean hasNext() {
          return cursor != SubList.this.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
          final int i = cursor;
          if (i >= SubList.this.size)
            throw new NoSuchElementException();

          final Object[] elementData = root.elementData;
          cursor = i + 1;
          return (E)elementData[offset + (lastRet = i)];
        }

        @Override
        public boolean hasPrevious() {
          return cursor != 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E previous() {
          final int i = cursor - 1;
          if (i < 0)
            throw new NoSuchElementException();

          final Object[] elementData = root.elementData;
          cursor = i;
          return (E)elementData[offset + (lastRet = i)];
        }

        @Override
        @SuppressWarnings("unchecked")
        public void forEachRemaining(final Consumer<? super E> action) {
          Objects.requireNonNull(action);
          final int size = SubList.this.size;
          int i = cursor;
          if (i < size) {
            final Object[] es = root.elementData;
            for (; i < size && root.modCount == expectedModCount; ++i) // [A]
              action.accept((E)es[offset + i]);

            // update once at end to reduce heap write traffic
            cursor = i;
            lastRet = i - 1;
          }
        }

        @Override
        public int nextIndex() {
          return cursor;
        }

        @Override
        public int previousIndex() {
          return cursor - 1;
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void set(final E e) {
          if (lastRet < 0)
            throw new IllegalStateException();

          root.set(offset + lastRet, e);
        }

        @Override
        public void add(final E e) {
          throw new UnsupportedOperationException();
        }
      };
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
      assertRange("fromIndex", fromIndex, "toIndex", toIndex, "size", size);
      return new SubList<>(this, fromIndex, toIndex);
    }

    @Override
    public Spliterator<E> spliterator() {
      // ArrayListSpliterator not used here due to late-binding
      return new Spliterator<E>() {
        private int index = offset; // current index, final modified on advance/split
        private int fence = -1; // -1 until used; then one past last index
        private int expectedModCount; // initialized when fence set

        private int getFence() { // initialize fence to size on first use
          int hi = fence; // (final a specialized variant appears in method forEach)
          if (hi < 0) {
            expectedModCount = modCount;
            hi = fence = offset + size;
          }

          return hi;
        }

        @Override
        public WrappedArrayList<E>.ArrayListSpliterator trySplit() {
          final int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
          // ArrayListSpliterator can be used here as the source is already bound
          return (lo >= mid) ? null : root.new ArrayListSpliterator(lo, index = mid, expectedModCount); // divide range in half unless too small
        }

        @Override
        public boolean tryAdvance(final Consumer<? super E> action) {
          Objects.requireNonNull(action);
          final int hi = getFence();
          final int i = index;
          if (i < hi) {
            index = i + 1;
            action.accept(root.elementData[i]);
            return true;
          }

          return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void forEachRemaining(final Consumer<? super E> action) {
          Objects.requireNonNull(action);
          final int mc;
          final WrappedArrayList<E> lst = root;
          final Object[] a = lst.elementData;
          int i, hi = fence; // hoist accesses and checks from loop
          if (hi < 0) {
            mc = modCount;
            hi = offset + size;
          }
          else {
            mc = expectedModCount;
          }

          if ((i = index) >= 0 && (index = hi) <= a.length) {
            for (; i < hi; ++i) // [A]
              action.accept((E)a[i]);

            if (lst.modCount == mc)
              return;
          }
        }

        @Override
        public long estimateSize() {
          return getFence() - index;
        }

        @Override
        public int characteristics() {
          return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
      };
    }
  }

  /** Index-based split-by-two, lazily initialized Spliterator */
  final class ArrayListSpliterator implements Spliterator<E> {
    private int index; // current index, modified on advance/split
    private int fence; // -1 until used; then one past last index
    private int expectedModCount; // initialized when fence set

    /** Creates new spliterator covering the given range. */
    private ArrayListSpliterator(final int origin, final int fence, final int expectedModCount) {
      this.index = origin;
      this.fence = fence;
      this.expectedModCount = expectedModCount;
    }

    private int getFence() { // initialize fence to size on first use
      int hi = fence; // (a specialized variant appears in method forEach)
      if (hi < 0) {
        expectedModCount = modCount;
        hi = fence = size;
      }

      return hi;
    }

    @Override
    public ArrayListSpliterator trySplit() {
      final int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
      return (lo >= mid) ? null : // divide range in half unless too small
          new ArrayListSpliterator(lo, index = mid, expectedModCount);
    }

    @Override
    public boolean tryAdvance(final Consumer<? super E> action) {
      if (action == null)
        throw new NullPointerException();

      final int hi = getFence();
      final int i = index;
      if (i >= hi)
        return false;

      index = i + 1;
      action.accept(elementData[i]);
      return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEachRemaining(final Consumer<? super E> action) {
      if (action == null)
        throw new NullPointerException();

      final int mc; // hoist accesses and checks from loop
      final Object[] a = elementData;
      int hi = fence, i;
      if (hi < 0) {
        mc = modCount;
        hi = size;
      }
      else {
        mc = expectedModCount;
      }

      if ((i = index) >= 0 && (index = hi) <= a.length) {
        for (; i < hi; ++i) // [A]
          action.accept((E)a[i]);

        if (modCount == mc)
          return;
      }
    }

    @Override
    public long estimateSize() {
      return getFence() - index;
    }

    @Override
    public int characteristics() {
      return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
    }
  }
}