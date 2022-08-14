/* Copyright (c) 2018 Seva Safris, LibJ
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

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility functions for operations pertaining to {@link BigInteger}.
 */
public final class BigIntegers {
  private static final ConcurrentHashMap<String,BigInteger> instances = new ConcurrentHashMap<>();

  /** The {@link BigInteger} constant zero ({@code 0}). */
  public static final BigInteger ZERO = init("0", BigInteger.ZERO);

  /** The {@link BigInteger} constant one ({@code 1}). */
  public static final BigInteger ONE = init("1", BigInteger.ONE);

  /** The {@link BigInteger} constant two ({@code 2}). */
  public static final BigInteger TWO = init("2", BigInteger.valueOf(2));

  /** The {@link BigInteger} constant ten ({@code 10}). */
  public static final BigInteger TEN = init("10", BigInteger.TEN);

  private static BigInteger init(final String str, final BigInteger val) {
    instances.put(str, val);
    return val;
  }

  /**
   * Returns a canonical representation of the {@link BigInteger} object representing the specified string value.
   *
   * @param val The value of the desired {@link BigInteger} instance.
   * @return A canonical representation of the {@link BigInteger} object representing the specified string value.
   * @throws IllegalArgumentException If {@code val} is null.
   */
  public static BigInteger intern(final String val) {
    final BigInteger intern = instances.get(assertNotNull(val));
    return intern != null ? intern : init(val, new BigInteger(val));
  }

  /**
   * Returns a canonical representation for the {@link BigInteger} object.
   *
   * @param n The {@link BigInteger} to intern.
   * @return A {@link BigInteger} that has the same contents as the specified {@link BigInteger}, but is guaranteed to be from a
   *         pool of unique instances.
   * @throws IllegalArgumentException If {@code n} is null.
   */
  public static BigInteger intern(final BigInteger n) {
    final BigInteger instance = instances.putIfAbsent(assertNotNull(n).toString(), n);
    return instance != null ? instance : n;
  }

  /**
   * Return a {@link BigInteger} equal to the unsigned value of the argument.
   *
   * @param signum The sign of the {@link BigInteger} to be returned.
   * @param mag The unsigned magnitude of the {@link BigInteger} to be returned.
   * @return A {@link BigInteger} equal to the unsigned value of the argument.
   * @throws NumberFormatException If {@code signum} is not -1, 0 or 1, or if {@code signum == 0 && mag != 0}.
   */
  public static BigInteger valueOf(final int signum, final int mag) {
    final long signed = Integer.toUnsignedLong(mag);
    return BigInteger.valueOf(signum < 0 ? -signed : signed);
  }

  /**
   * Return a {@link BigInteger} equal to the unsigned value of the argument.
   *
   * @param signum The sign of the {@link BigInteger} to be returned.
   * @param mag The unsigned magnitude of the {@link BigInteger} to be returned.
   * @return A {@link BigInteger} equal to the unsigned value of the argument.
   * @throws NumberFormatException If {@code signum} is not -1, 0 or 1, or if {@code signum == 0 && mag != 0}.
   */
  public static BigInteger valueOf(final int signum, final long mag) {
    if (signum < -1 || signum > 1)
      throw new NumberFormatException("Invalid signum value");

    if (signum == 0) {
      if (mag != 0)
        throw new NumberFormatException("signum-magnitude mismatch");

      return BigInteger.ZERO;
    }

    if (mag >= 0L)
      return BigInteger.valueOf(signum == -1 ? -mag : mag);

    final long upper = signum * Integer.toUnsignedLong((int)(mag >>> 32));
    final long lower = signum * Integer.toUnsignedLong((int)mag);
    return BigInteger.valueOf(upper).shiftLeft(32).add(BigInteger.valueOf(lower));
  }

  /**
   * Return a {@link BigInteger} equal to the unsigned value of the argument.
   *
   * @param signum The sign of the {@link BigInteger} to be returned.
   * @param mag The unsigned magnitude of the {@link BigInteger} to be returned.
   * @return A {@link BigInteger} equal to the unsigned value of the argument.
   * @throws NumberFormatException If {@code signum} is not -1, 0 or 1, or if {@code signum == 0 && mag != 0}.
   */
  public static BigInteger valueOf(final int signum, final byte[] mag) {
    return new BigInteger(signum, mag);
  }

  private BigIntegers() {
  }
}