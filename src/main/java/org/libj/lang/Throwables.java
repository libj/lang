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
   * Returns the string representation of the specified {@link Throwable
   * throwable} and its backtrace.
   *
   * @param t The throwable.
   * @return The string representation of the specified {@link Throwable
   *         throwable} and its backtrace.
   * @throws NullPointerException If the specified {@link Throwable throwable}
   *           is null.
   * @see Throwable#printStackTrace(java.io.PrintStream)
   */
  public static String toString(final Throwable t) {
    final StringWriter out = new StringWriter();
    t.printStackTrace(new PrintWriter(out));
    return out.toString();
  }

  /**
   * Copies the cause, stack trace elements, and suppressed exceptions from the
   * first specified {@link Throwable}, to the second.
   *
   * @param <T> The type parameter of the {@link Throwable}.
   * @param from The {@link Throwable} to copy from.
   * @param to The {@link Throwable} to copy to.
   * @return The {@link Throwable} being copied to.
   * @throws NullPointerException If {@code from} or {@code to} are null.
   */
  public static <T extends Throwable>T copy(final T from, final T to) {
    to.initCause(from.getCause());
    to.setStackTrace(from.getStackTrace());
    if (from.getSuppressed() != null)
      for (final Throwable suppressed : from.getSuppressed())
        to.addSuppressed(suppressed);

    return to;
  }

  private Throwables() {
  }
}