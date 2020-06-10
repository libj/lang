/* Copyright (c) 2020 LibJ
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

/**
 * Functions implementing common operations on {@code Boolean} references and
 * {@code boolean} values.
 */
public final class Booleans {
  /**
   * Parses the string argument as a boolean. The {@link Boolean} returned is
   * null if the string argument is null, and the value {@code true} if the
   * string argument is equal, ignoring case, to the string {@code "true"}.
   * <ul>
   * <li>{@code Boolean.parseBoolean("True")} returns {@code true}.</li>
   * <li>{@code Boolean.parseBoolean("yes")} returns {@code false}.</li>
   * </ul>
   *
   * @param s The {@link String} containing the boolean representation to be
   *          parsed.
   * @return The {@link Boolean} represented by the string argument.
   * @see Boolean#parseBoolean(String)
   */
  public static Boolean parseBoolean(final String s) {
    return s == null ? null : Boolean.parseBoolean(s);
  }

  public static Boolean parseBoolean(final Integer i) {
    return i == null ? null : i == 0 ? Boolean.FALSE : Boolean.TRUE;
  }

  public static int toInt(final boolean b) {
    return b ? 1 : 0;
  }

  public static double toDouble(final boolean b) {
    return b ? 1 : 0;
  }

  public static Integer toInteger(final Boolean b) {
    return b == null ? null : b ? 1 : 0;
  }

  private Booleans() {
  }
}