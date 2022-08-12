/* Copyright (c) 2014 LibJ
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * An abstraction of operations that execute recursively or iteratively in order to process collections or arrays (containers), and
 * return statically allocated arrays, the sizes of which are not able to be known until evaluation of each member of the specified
 * container.
 */
public final class Repeat {
  /**
   * An interface defining the behavior of recursive execution for the specified types.
   *
   * @param <C> The type of the object containing the array on which repetition is to be performed.
   * @param <M> The type of the member elements in the array.
   * @param <A> The type of the optional argument passed to member elements during recursion.
   */
  public interface Recurser<C,M,A> extends BiPredicate<M,A> {
    /**
     * Returns the array of members in the specified container.
     *
     * @param container The container.
     * @return The array of members in the specified container.
     */
    M[] members(C container);

    /**
     * Returns the container that follows the specified container.
     *
     * @param container The container.
     * @return The container that follows the specified container.
     */
    C next(C container);
  }

  /**
   * An abstract class defining the concept of an algorithm supported by the {@link Repeat} methods.
   */
  private abstract static class Algorithm {
    /**
     * Default implementation of "simple" variation of abstract {@link Algorithm}.
     *
     * @param <M> The type parameter for the member class of {@code array}.
     * @param <A> The type parameter of {@code arg}.
     * @param array The array on which repetition is to be performed.
     * @param type The component type of the {@code array}.
     * @param predicate {@link BiPredicate} to be applied to each member of the {@code array}.
     * @param arg Argument to be passed to {@code predicate}.
     * @return The {@code type}-typed array of {@code predicate}-passed members of {@code array} argument.
     * @throws IllegalArgumentException If {@code array} is not null, and {@code type} or {@code predicate} is null.
     */
    protected <M,A>M[] simple(final M[] array, final Class<M> type, final BiPredicate<? super M,A> predicate, final A arg) {
      if (array == null)
        return null;

      assertNotNull(type, "type == null");
      assertNotNull(predicate, "predicate == null");
      return null;
    }
  }

  /**
   * Algorithms that execute with recursive behavior.
   */
  public static final class Recursive {
    /**
     * Abstract class defining common methods for recursive algorithms.
     */
    private abstract static class RecursiveAlgorithm extends Algorithm {
      @Override
      protected <M,A>M[] simple(final M[] array, final Class<M> type, final BiPredicate<? super M,A> predicate, final A arg) {
        super.simple(array, type, predicate, arg);
        return recurse(array, type, predicate, arg, 0, 0);
      }

      /**
       * Recursive implementation of "contained" variation of {@link Algorithm}.
       *
       * @param <C> The type of the object containing the array on which repetition is to be performed.
       * @param <M> The type of the member elements in the array.
       * @param <A> The type of the optional argument passed to member elements during recursion.
       * @param container The the array on which repetition is to be performed.
       * @param array The array on which recursion is to be performed.
       * @param type The {@link Class} representing the member type {@code <M>}.
       * @param recurser {@link Recurser} defining the recursion logic.
       * @param arg Argument to be passed to {@code predicate}.
       * @return The {@code type}-typed array of {@code recurser}-accepted members of {@code array} argument.
       * @throws IllegalArgumentException If {@code array} is not null, and {@code type} or {@code recurser} is null.
       */
      protected <C,M,A>M[] contained(final C container, final M[] array, final Class<M> type, final Recurser<C,M,A> recurser, final A arg) {
        if (array == null)
          return null;

        assertNotNull(type, "type == null");
        assertNotNull(recurser, "recurser == null");
        return recurse(container, array, type, recurser, arg, 0, 0);
      }

      /**
       * The recursive algorithm that operates on the specified arguments.
       *
       * @param <M> The type of the member elements in the array.
       * @param <A> The type of the optional argument passed to member elements during recursion.
       * @param array The array on which recursion is to be performed.
       * @param type The {@link Class} representing the member type {@code <M>}.
       * @param predicate The {@link BiPredicate} used to test each member in the array whether to be included in the resulting
       *          array.
       * @param arg The optional argument of type {@code <A>} that is passed to member elements during recursion.
       * @param index The starting index of recursion ({@code 0} when initially called).
       * @param depth The starting depth of recursion ({@code 0} when initially called).
       * @return The statically allocated array of type {@code <M>} with the resulting elements.
       */
      protected abstract <M,A>M[] recurse(M[] array, Class<M> type, BiPredicate<? super M,A> predicate, A arg, int index, int depth);

      /**
       * The recursive algorithm that operates on the specified arguments.
       *
       * @param <C> The type of the object containing the array on which repetition is to be performed.
       * @param <M> The type of the member elements in the array.
       * @param <A> The type of the optional argument passed to member elements during recursion.
       * @param container The container of the array on which repetition is to be performed.
       * @param array The array on which recursion is to be performed.
       * @param type The {@link Class} representing the member type {@code <M>}.
       * @param recurser The {@link Recurser} defining the recursion behavior.
       * @param arg The optional argument of type {@code <A>} that is passed to member elements during recursion.
       * @param index The starting index of recursion ({@code 0} when initially called).
       * @param depth The starting depth of recursion ({@code 0} when initially called).
       * @return The statically allocated array of type {@code <M>} with the resulting elements.
       */
      protected abstract <C,M,A>M[] recurse(C container, M[] array, Class<M> type, Recurser<C,M,A> recurser, A arg, int index, int depth);
    }

    /**
     * A {@link RecursiveAlgorithm} that results in an array of qualifying members stored in the order of traversal.
     */
    private static final RecursiveAlgorithm recursiveOrdered = new RecursiveAlgorithm() {
      @Override
      @SuppressWarnings("unchecked")
      protected <M,A>M[] recurse(final M[] array, final Class<M> type, final BiPredicate<? super M,A> predicate, final A arg, int index, final int depth) {
        if (index >= array.length)
          return (M[])Array.newInstance(type, depth);

        M member;
        boolean skip;
        while ((skip = !predicate.test(member = array[index++], arg)) && index < array.length);
        final M[] result = recurse(array, type, predicate, arg, index, skip ? depth : depth + 1);
        if (!skip)
          result[depth] = member;

        return result;
      }

      @Override
      @SuppressWarnings("unchecked")
      protected <C,M,A>M[] recurse(final C container, final M[] array, final Class<M> type, final Recurser<C,M,A> recurser, final A arg, int index, final int depth) {
        if (index == array.length) {
          final C parent = recurser.next(container);
          return parent == null ? (M[])Array.newInstance(type, depth) : recurse(parent, recurser.members(parent), type, recurser, arg, 0, depth);
        }

        M member;
        boolean skip;
        while ((skip = !recurser.test(member = array[index++], arg)) && index < array.length);
        final M[] result = recurse(container, array, type, recurser, arg, index, skip ? depth : depth + 1);
        if (!skip)
          result[depth] = member;

        return result;
      }
    };

    /**
     * A {@link RecursiveAlgorithm} that results in an array of qualifying members stored in the inverse order of traversal (i.e.
     * the first traversed member is placed in the end of the resulting array, and the last traversed member is first).
     */
    private static final RecursiveAlgorithm recursiveInverted = new RecursiveAlgorithm() {
      @Override
      @SuppressWarnings("unchecked")
      protected <M,A>M[] recurse(final M[] array, final Class<M> type, final BiPredicate<? super M,A> predicate, final A arg, int index, final int depth) {
        if (index == array.length)
          return (M[])Array.newInstance(type, depth);

        M member;
        boolean skip;
        while ((skip = !predicate.test(member = array[array.length - ++index], arg)) && index < array.length);
        final M[] result = recurse(array, type, predicate, arg, index, skip ? depth : depth + 1);
        if (!skip)
          result[result.length - 1 - depth] = member;

        return result;
      }

      @Override
      @SuppressWarnings("unchecked")
      protected <C,M,A>M[] recurse(final C container, final M[] array, final Class<M> type, final Recurser<C,M,A> recurser, final A arg, int index, final int depth) {
        if (index == array.length) {
          final C parent = recurser.next(container);
          return parent == null ? (M[])Array.newInstance(type, depth) : recurse(parent, recurser.members(parent), type, recurser, arg, 0, depth);
        }

        M member;
        boolean skip;
        while ((skip = !recurser.test(member = array[array.length - ++index], arg)) && index < array.length);
        final M[] result = recurse(container, array, type, recurser, arg, index, skip ? depth : depth + 1);
        if (!skip)
          result[result.length - 1 - depth] = member;

        return result;
      }
    };

    /**
     * Executes recursive traversal of the specified arguments, resulting in an array of qualifying members stored in the order of
     * traversal.
     *
     * @param <M> The type parameter for the member class of {@code array}.
     * @param <A> The type parameter of {@code arg}.
     * @param array The array to consume.
     * @param type The component type of the {@code array}.
     * @param predicate {@link BiPredicate} to be applied to each member of the {@code array}.
     * @param arg Argument to be passed to {@code predicate}.
     * @return The {@code type}-typed array of {@code predicate}-passed members of {@code array} argument, stored in the order of
     *         traversal.
     * @throws IllegalArgumentException If {@code array} is not null, and {@code type} or {@code predicate} is null.
     */
    public static <M,A>M[] ordered(final M[] array, final Class<M> type, final BiPredicate<? super M,A> predicate, final A arg) {
      return recursiveOrdered.simple(array, type, predicate, arg);
    }

    /**
     * Executes recursive traversal of the specified arguments, resulting in an array of qualifying members stored in the order of
     * traversal.
     *
     * @param <C> The type of the object containing the array on which repetition is to be performed.
     * @param <M> The type of the member elements in the array.
     * @param <A> The type of the optional argument passed to member elements during recursion.
     * @param container The container of the array on which repetition is to be performed.
     * @param array The array on which recursion is to be performed.
     * @param type The {@link Class} representing the member type {@code <M>}.
     * @param recurser {@link Recurser} defining the recursion logic.
     * @param arg Argument to be passed to {@code predicate}.
     * @return The {@code type}-typed array of {@code recurser}-accepted members of {@code array} argument, stored in the order of
     *         traversal.
     * @throws IllegalArgumentException If {@code array} is not null, and {@code type} or {@code recurser} is null.
     */
    public static <C,M,A>M[] ordered(final C container, final M[] array, final Class<M> type, final Recurser<C,M,A> recurser, final A arg) {
      return recursiveOrdered.contained(container, array, type, recurser, arg);
    }

    /**
     * Executes recursive traversal of the specified arguments, resulting in an array of qualifying members stored in the inverse
     * order of traversal (i.e. the first traversed member is placed in the end of the resulting array, and the last traversed
     * member is first).
     *
     * @param <M> The type parameter for the member class of {@code array}.
     * @param <A> The type parameter of {@code arg}.
     * @param array The array to consume.
     * @param type The component type of the {@code array}.
     * @param predicate {@link BiPredicate} to be applied to each member of the {@code array}.
     * @param arg Argument to be passed to {@code predicate}.
     * @return The {@code type}-typed array of {@code predicate}-passed members of {@code array} argument, stored in the inverse
     *         order of traversal.
     * @throws IllegalArgumentException If {@code array} is not null, and {@code type} or {@code predicate} is null.
     */
    public static <M,A>M[] inverted(final M[] array, final Class<M> type, final BiPredicate<? super M,A> predicate, final A arg) {
      return recursiveInverted.simple(array, type, predicate, arg);
    }

    /**
     * Executes recursive traversal of the specified arguments, resulting in an array of qualifying members stored in the inverse
     * order of traversal (i.e. the first traversed member is placed in the end of the resulting array, and the last traversed
     * member is first).
     *
     * @param <C> The type of the object containing the array on which repetition is to be performed.
     * @param <M> The type of the member elements in the array.
     * @param <A> The type of the optional argument passed to member elements during recursion.
     * @param container The container of the array on which repetition is to be performed.
     * @param array The array on which recursion is to be performed.
     * @param type The {@link Class} representing the member type {@code <M>}.
     * @param recurser {@link Recurser} defining the recursion logic.
     * @param arg Argument to be passed to {@code predicate}.
     * @return The {@code type}-typed array of {@code recurser}-accepted members of {@code array} argument, stored in the inverse
     *         order of traversal.
     * @throws IllegalArgumentException If {@code array} is not null, and {@code type} or {@code recurser} is null.
     */
    public static <C,M,A>M[] inverted(final C container, final M[] array, final Class<M> type, final Recurser<C,M,A> recurser, final A arg) {
      return recursiveInverted.contained(container, array, type, recurser, arg);
    }

    private Recursive() {
    }
  }

  /**
   * An {@link Algorithm} implementing iterative traversal that results in an array of qualifying members stored in the order of
   * traversal.
   */
  private static final Algorithm iterative = new Algorithm() {
    @Override
    @SuppressWarnings("unchecked")
    protected <M,A>M[] simple(final M[] array, final Class<M> type, final BiPredicate<? super M,A> predicate, final A arg) {
      super.simple(array, type, predicate, arg);
      final List<M> list = new ArrayList<>(array.length);
      for (int i = 0, i$ = array.length; i < i$; ++i) // [A]
        if (predicate.test(array[i], arg))
          list.add(array[i]);

      return list.toArray((M[])Array.newInstance(type, list.size()));
    }
  };

  /**
   * Executes iterative traversal of the specified arguments, resulting in an array of qualifying members stored in the order of
   * traversal.
   *
   * @param <M> The type parameter for the member class of {@code array}.
   * @param <A> The type parameter of {@code arg}.
   * @param array The array on which iteration is to be performed.
   * @param type The component type of the {@code array}.
   * @param predicate {@link BiPredicate} to be applied to each member of the {@code array}.
   * @param arg Argument to be passed to {@code predicate}.
   * @return The {@code type}-typed array of {@code predicate}-passed members of {@code array} argument, stored in the order of
   *         traversal.
   * @throws IllegalArgumentException If {@code array} is not null, and {@code type} or {@code predicate} is null.
   */
  public static <M,A>M[] iterative(final M[] array, final Class<M> type, final BiPredicate<? super M,A> predicate, final A arg) {
    return iterative.simple(array, type, predicate, arg);
  }

  private Repeat() {
  }
}