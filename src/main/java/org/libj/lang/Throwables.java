/* Copyright (c) 2013 LibJ
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Utility functions for operations pertaining to {@link Throwable}.
 */
public final class Throwables {
  /**
   * Adds the {@code suppressed} exception to the {@code target}.
   * <ol>
   * <li>If {@code suppressed} is null, {@code target} is returned.</li>
   * <li>If {@code target} is null, {@code suppressed} is returned.</li>
   * <li>If neither {@code target} nor {@code suppressed} is null,
   * {@code suppressed} is added to {@code target} as a suppressed exception,
   * and {@code target} is returned.</li>
   * </ol>
   *
   * @param <T> The type parameter of the exception.
   * @param target The target exception.
   * @param suppressed The suppressed exception.
   * @return The exception based on the method's logic, described above.
   */
  public static <T extends Throwable>T addSuppressed(final T target, final T suppressed) {
    if (suppressed == null)
      return target;

    if (target == null)
      return suppressed;

    target.addSuppressed(suppressed);
    return target;
  }

  /**
   * Adds each of the specified {@code suppressed} exceptions to the
   * {@code target}.
   *
   * @param <T> The type parameter of the exception.
   * @param target The target exception.
   * @param suppressed The suppressed exceptions.
   * @return The exception based on the method's logic, described above.
   * @throws IllegalArgumentException If {@code target} or {@code suppressed} is
   *           null.
   */
  public static <T extends Throwable>T addSuppressed(final T target, final Exception[] suppressed) {
    Assertions.assertNotNull(target);
    Assertions.assertNotNull(suppressed);
    return addSuppressed(target, suppressed, 0, suppressed.length);
  }

  /**
   * Adds each of the specified {@code suppressed} exceptions to the
   * {@code target}.
   *
   * @param <T> The type parameter of the exception.
   * @param target The target exception.
   * @param suppressed The suppressed exceptions.
   * @param fromIndex The index (inclusive) of the first element in
   *          {@code suppressed}. If {@code fromIndex > toIndex}, the exceptions
   *          in {@code suppressed} will be traversed in reverse order.
   * @param toIndex The index (exclusive) of the last element in
   *          {@code suppressed}. If {@code toIndex > fromIndex}, the exceptions
   *          in {@code suppressed} will be traversed in regular order.
   * @return The exception based on the method's logic, described above.
   * @throws IllegalArgumentException If {@code target} is null.
   * @throws IndexOutOfBoundsException If {@code off} is negative, {@code len}
   *           is negative, or {@code suppressed.length} is less than
   *           {@code off + len}.
   */
  public static <T extends Throwable>T addSuppressed(final T target, final Exception[] suppressed, final int fromIndex, final int toIndex) {
    Assertions.assertNotNull(target);
    if (fromIndex == toIndex)
      return target;

    if (fromIndex < toIndex)
      for (int i = fromIndex; i < toIndex; ++i)
        target.addSuppressed(suppressed[i]);
    else
      for (int i = fromIndex; i > toIndex; --i)
        target.addSuppressed(suppressed[i]);

    return target;
  }

  /**
   * Adds each of the specified {@code suppressed} exceptions to the
   * {@code target}.
   *
   * @param <T> The type parameter of the exception.
   * @param target The target exception.
   * @param suppressed The suppressed exceptions.
   * @return The exception based on the method's logic, described above.
   * @throws IllegalArgumentException If {@code target} or {@code suppressed} is
   *           null.
   */
  public static <T extends Throwable>T addSuppressed(final T target, final List<Exception> suppressed) {
    Assertions.assertNotNull(target);
    Assertions.assertNotNull(suppressed);
    return addSuppressed(target, suppressed, 0, suppressed.size());
  }

  /**
   * Adds each of the specified {@code suppressed} exceptions to the
   * {@code target}.
   *
   * @param <T> The type parameter of the exception.
   * @param target The target exception.
   * @param suppressed The suppressed exceptions.
   * @param fromIndex The index (inclusive) of the first element in
   *          {@code suppressed}. If {@code fromIndex > toIndex}, the exceptions
   *          in {@code suppressed} will be traversed in reverse order.
   * @param toIndex The index (exclusive) of the last element in
   *          {@code suppressed}. If {@code toIndex > fromIndex}, the exceptions
   *          in {@code suppressed} will be traversed in regular order.
   * @return The exception based on the method's logic, described above.
   * @throws IllegalArgumentException If {@code target} is null.
   * @throws IndexOutOfBoundsException If {@code off} is negative, {@code len}
   *           is negative, or {@code suppressed.length} is less than
   *           {@code off + len}.
   */
  public static <T extends Throwable>T addSuppressed(final T target, final List<Exception> suppressed, final int fromIndex, final int toIndex) {
    Assertions.assertNotNull(target);
    if (fromIndex == toIndex)
      return target;

    if (fromIndex < toIndex)
      for (int i = fromIndex; i < toIndex; ++i)
        target.addSuppressed(suppressed.get(i));
    else
      for (int i = fromIndex; i > toIndex; --i)
        target.addSuppressed(suppressed.get(i));

    return target;
  }

  /**
   * Returns the string representation of the specified {@link Throwable
   * throwable} and its backtrace.
   *
   * @param t The throwable.
   * @return The string representation of the specified {@link Throwable
   *         throwable} and its backtrace.
   * @throws IllegalAnnotationException If {@code t} is null.
   * @see Throwable#printStackTrace(java.io.PrintStream)
   */
  public static String toString(final Throwable t) {
    final StringWriter out = new StringWriter();
    Assertions.assertNotNull(t).printStackTrace(new PrintWriter(out));
    return out.toString();
  }

  /**
   * Copies the cause, stack trace elements, and suppressed exceptions from the
   * first specified {@link Throwable}, to the second.
   *
   * @param <F> The type parameter of the {@code from} {@link Throwable}.
   * @param <T> The type parameter of the {@code to} {@link Throwable}.
   * @param from The {@link Throwable} to copy from.
   * @param to The {@link Throwable} to copy to.
   * @return The {@link Throwable} being copied to.
   * @throws IllegalAnnotationException If {@code from} or {@code to} are null.
   */
  public static <F extends Throwable,T extends F>T copy(final F from, final T to) {
    Assertions.assertNotNull(from);
    Assertions.assertNotNull(to);
    to.initCause(from.getCause());
    to.setStackTrace(from.getStackTrace());
    for (final Throwable suppressed : from.getSuppressed())
      to.addSuppressed(suppressed);

    return to;
  }

  private Throwables() {
  }
}