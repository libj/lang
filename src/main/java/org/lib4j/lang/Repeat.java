/* Copyright (c) 2014 lib4j
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

package org.lib4j.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class Repeat {
  public static interface Filter<M> {
    public boolean accept(final M member, final Object ... args);
  }

  public static interface Recurser<M,C> extends Filter<M> {
    public M[] members(final C container);
    public C next(final C container);
  }

  private static abstract class Algorithm {
    protected <M>M[] simple(final M[] array, final Class<M> type, final Filter<M> filter, final Object ... args) {
      if (array == null)
        return null;

      if (type == null)
        throw new NullPointerException("type == null");

      if (filter == null)
        throw new NullPointerException("filter == null");

      return null;
    }
  }

  public static final class Recursive {
    private static abstract class RecursiveAlgorithm extends Algorithm {
      @Override
      protected <M>M[] simple(final M[] array, final Class<M> type, final Filter<M> filter, final Object ... args) {
        super.simple(array, type, filter, args);
        return recurse(type, filter, args, array, 0, 0);
      }

      protected <M,C>M[] contained(final C container, final M[] array, final Class<M> type, final Recurser<M,C> recurser, final Object ... args) {
        if (array == null)
          return null;

        if (type == null)
          throw new NullPointerException("type == null");

        if (recurser == null)
          throw new NullPointerException("recurser == null");

        return recurse(container, array, type, recurser, args, 0, 0);
      }

      protected abstract <M>M[] recurse(final Class<M> type, final Filter<M> filter, final Object[] args, final M[] array, int index, final int depth);
      protected abstract <M,C>M[] recurse(final C container, final M[] array, final Class<M> type, final Recurser<M,C> recurser, final Object[] args, int index, final int depth);
    }

    private static final RecursiveAlgorithm recursiveOrdered = new RecursiveAlgorithm() {
      @Override
      @SuppressWarnings("unchecked")
      protected <M>M[] recurse(final Class<M> type, final Filter<M> filter, final Object[] args, final M[] array, int index, final int depth) {
        if (index >= array.length)
          return (M[])Array.newInstance(type, depth);

        M member;
        boolean skip = true;
        while ((skip = !filter.accept(member = array[index++], args)) && index < array.length);
        final M[] result = recurse(type, filter, args, array, index, skip ? depth : depth + 1);
        if (!skip)
          result[depth] = member;

        return result;
      }

      @Override
      @SuppressWarnings("unchecked")
      protected <M,C>M[] recurse(final C container, final M[] array, final Class<M> type, final Recurser<M,C> recurser, final Object[] args, int index, final int depth) {
        if (index >= array.length) {
          final C parent = recurser.next(container);
          return parent == null ? (M[])Array.newInstance(type, depth) : recurse(parent, recurser.members(parent), type, recurser, args, 0, depth);
        }

        M member;
        boolean skip = true;
        while ((skip = !recurser.accept(member = array[index++], args)) && index < array.length);
        final M[] result = recurse(container, array, type, recurser, args, index, skip ? depth : depth + 1);
        if (!skip)
          result[depth] = member;

        return result;
      }
    };

    private static final RecursiveAlgorithm recursiveInverted = new RecursiveAlgorithm() {
      @Override
      @SuppressWarnings("unchecked")
      protected <M>M[] recurse(final Class<M> type, final Filter<M> filter, final Object[] args, final M[] array, int index, final int depth) {
        if (index >= array.length)
          return (M[])Array.newInstance(type, depth);

        M member;
        boolean skip = true;
        while ((skip = !filter.accept(member = array[array.length - ++index], args)) && index < array.length);
        final M[] result = recurse(type, filter, args, array, index, skip ? depth : depth + 1);
        if (!skip)
          result[result.length - 1 - depth] = member;

        return result;
      }

      @Override
      @SuppressWarnings("unchecked")
      protected <M,C>M[] recurse(final C container, final M[] array, final Class<M> type, final Recurser<M,C> recurser, final Object[] args, int index, final int depth) {
        if (index >= array.length) {
          final C parent = recurser.next(container);
          return parent == null ? (M[])Array.newInstance(type, depth) : recurse(parent, recurser.members(parent), type, recurser, args, 0, depth);
        }

        M member;
        boolean skip = true;
        while ((skip = !recurser.accept(member = array[array.length - ++index], args)) && index < array.length);
        final M[] result = recurse(container, array, type, recurser, args, index, skip ? depth : depth + 1);
        if (!skip)
          result[result.length - 1 - depth] = member;

        return result;
      }
    };

    public static <M>M[] ordered(final M[] array, final Class<M> type, final Filter<M> filter, final Object ... args) {
      return recursiveOrdered.simple(array, type, filter, args);
    }

    public static <M,C>M[] ordered(final C container, final M[] array, final Class<M> type, final Recurser<M,C> recurser, final Object ... args) {
      return recursiveOrdered.contained(container, array, type, recurser, args);
    }

    public static <M>M[] inverted(final M[] array, final Class<M> type, final Filter<M> filter, final Object ... args) {
      return recursiveInverted.simple(array, type, filter, args);
    }

    public static <M,C>M[] inverted(final C container, final M[] array, final Class<M> type, final Recurser<M,C> recurser, final Object ... args) {
      return recursiveInverted.contained(container, array, type, recurser, args);
    }

    private Recursive() {
    }
  }

  private static final Algorithm iterative = new Algorithm() {
    @Override
    @SuppressWarnings("unchecked")
    protected <M>M[] simple(final M[] array, final Class<M> type, final Filter<M> filter, final Object ... args) {
      super.simple(array, type, filter, args);
      final List<M> list = new ArrayList<M>(array.length);
      for (int i = 0; i < array.length; i++)
        if (filter.accept(array[i], args))
          list.add(array[i]);

      return list.toArray((M[])Array.newInstance(type, list.size()));
    }
  };

  public static <M>M[] iterative(final M[] array, final Class<M> type, final Filter<M> filter, final Object ... args) {
    return iterative.simple(array, type, filter, args);
  }

  private Repeat() {
  }
}