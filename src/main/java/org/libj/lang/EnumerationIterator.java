package org.libj.lang;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

public class EnumerationIterator<E> implements Iterator<E> {
  private final Enumeration<E> enumeration;

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

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}