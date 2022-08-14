/* Copyright (c) 2017 Seva Safris, LibJ
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility functions for operations pertaining to {@link BigDecimal}.
 */
public final class BigDecimals {
  private static final ConcurrentHashMap<Serializable,BigDecimal> instances = new ConcurrentHashMap<>();

  private static class BigDecimalInfinity extends BigDecimal {
    private final int signum;
    private final String str;

    private BigDecimalInfinity(final int signum) {
      super(0);
      this.signum = signum;
      this.str = signum < 0 ? "-Infinity" : "Infinity";
    }

    @Override
    public int signum() {
      return signum;
    }

    @Override
    public BigDecimal scaleByPowerOfTen(final int n) {
      return this;
    }

    @Override
    public BigDecimal stripTrailingZeros() {
      return this;
    }

    @Override
    public BigInteger toBigIntegerExact() {
      throw new ArithmeticException();
    }

    @Override
    public String toEngineeringString() {
      return toString();
    }

    @Override
    public String toPlainString() {
      return toString();
    }

    @Override
    public String toString() {
      return str;
    }
  }

  public static final BigDecimal POSITIVE_INFINITY = new BigDecimalInfinity(1);
  public static final BigDecimal NEGATIVE_INFINITY = new BigDecimalInfinity(-1);

  /** The {@link BigDecimal} constant {@code 0}, with a scale of {@code 0}. */
  public static final BigDecimal ZERO = init("0", BigDecimal.ZERO);

  /** The {@link BigDecimal} constant {@code 1}, with a scale of {@code 0}. */
  public static final BigDecimal ONE = init("1", BigDecimal.ONE);

  /** The {@link BigDecimal} constant {@code 2}, with a scale of {@code 0}. */
  public static final BigDecimal TWO = init("2", BigDecimal.valueOf(2L));

  /** The {@link BigDecimal} constant <i><code>e</code></i>, with a scale of {@code 15}. */
  public static final BigDecimal E = init(String.valueOf(Math.E), BigDecimal.valueOf(Math.E));

  /** The {@link BigDecimal} constant <i><code>pi</code></i>, with a scale of {@code 15}. */
  public static final BigDecimal PI = init(String.valueOf(Math.PI), BigDecimal.valueOf(Math.PI));

  /** The {@link BigDecimal} constant {@code log(2)}, with a scale of {@code 15}. */
  public static final BigDecimal LOG_2 = init(String.valueOf(Constants.LOG_2), BigDecimal.valueOf(Constants.LOG_2));

  /** The {@link BigDecimal} constant {@code log(10)}, with a scale of {@code 15}. */
  public static final BigDecimal LOG_10 = init(String.valueOf(Constants.LOG_10), BigDecimal.valueOf(Constants.LOG_10));

  /** The {@link BigDecimal} constant {@code sqrt(2)}, with a scale of {@code 15}. */
  public static final BigDecimal SQRT_2 = init(String.valueOf(Constants.SQRT_2), BigDecimal.valueOf(Constants.SQRT_2));

  private static BigDecimal init(final Serializable key, final BigDecimal val) {
    instances.put(key, val);
    return val;
  }

  /**
   * Returns a canonical representation of the {@link BigDecimal} object representing the specified string value.
   *
   * @param val The value of the desired {@link BigDecimal} instance.
   * @return A canonical representation of the {@link BigDecimal} object representing the specified string value.
   * @throws IllegalArgumentException If {@code val} is null.
   */
  public static BigDecimal intern(final String val) {
    final BigDecimal instance = instances.get(assertNotNull(val));
    return instance != null ? instance : init(val, new BigDecimal(val));
  }

  /**
   * Returns a canonical representation for the {@link BigDecimal} object.
   *
   * @param n The {@link BigDecimal} to intern.
   * @return A {@link BigDecimal} that has the same contents as the specified {@link BigDecimal}, but is guaranteed to be from a
   *         pool of unique instances.
   * @throws IllegalArgumentException If {@code n} is null.
   */
  public static BigDecimal intern(final BigDecimal n) {
    final BigDecimal instance = instances.putIfAbsent(assertNotNull(n).toString(), n);
    return instance != null ? instance : n;
  }

  /**
   * Returns a canonical representation of the {@link BigDecimal} object representing the specified {@code long} value.
   *
   * @param val The value of the desired {@link BigDecimal} instance.
   * @return A canonical representation of the {@link BigDecimal} object representing the specified {@code long} value.
   */
  public static BigDecimal intern(final long val) {
    final BigDecimal instance = instances.get(val);
    return instance != null ? instance : init(val, new BigDecimal(val));
  }

  /**
   * Returns a canonical representation of the {@link BigDecimal} object representing the specified {@link Long} value.
   *
   * @param val The value of the desired {@link BigDecimal} instance.
   * @return A canonical representation of the {@link BigDecimal} object representing the specified {@link Long} value.
   * @throws IllegalArgumentException If {@code val} is null.
   */
  public static BigDecimal intern(final Long val) {
    final BigDecimal instance = instances.get(assertNotNull(val));
    return instance != null ? instance : init(val, new BigDecimal(val));
  }

  /**
   * Returns a canonical representation of the {@link BigDecimal} object representing the specified {@code double} value.
   *
   * @param val The value of the desired {@link BigDecimal} instance.
   * @return A canonical representation of the {@link BigDecimal} object representing the specified {@code double} value.
   */
  public static BigDecimal intern(final double val) {
    final BigDecimal instance = instances.get(val);
    return instance != null ? instance : init(val, new BigDecimal(val));
  }

  /**
   * Returns a canonical representation of the {@link BigDecimal} object representing the specified {@link Double} value.
   *
   * @param val The value of the desired {@link BigDecimal} instance.
   * @return A canonical representation of the {@link BigDecimal} object representing the specified {@link Double} value.
   * @throws IllegalArgumentException If {@code val} is null.
   */
  public static BigDecimal intern(final Double val) {
    final BigDecimal instance = instances.get(assertNotNull(val));
    return instance != null ? instance : init(val, new BigDecimal(val));
  }

  /**
   * Returns a {@link BigDecimal} whose scale is the specified value, and whose unscaled value is determined by multiplying or
   * dividing the provided {@link BigDecimal}'s unscaled value by the appropriate power of ten to maintain its overall value.
   * <p>
   * This method differentiates itself from {@link BigDecimal#setScale(int,RoundingMode)} in the way the given {@link RoundingMode}
   * is applied. For {@code newScale} values that require rounding, unlike in {@link BigDecimal#setScale(int,RoundingMode)}, this
   * method first scales the provided {@link BigDecimal} to {@code newScale + 1}, rounding down. It thereafter performs scales the
   * resulting {@link BigDecimal} to the specified {@code newScale} with the given {@link RoundingMode}.
   * <p>
   * This behavior ensures that only the last decimal value affects the rounding, so as to align in behavior with most other
   * analogous algorithms.
   *
   * @param v The {@link BigDecimal}.
   * @param newScale Scale of the {@code BigDecimal} value to be returned.
   * @param rm The {@link RoundingMode}.
   * @return A {@link BigDecimal} whose scale is the specified value, and whose unscaled value is determined by multiplying or
   *         dividing the provided {@link BigDecimal}'s unscaled value by the appropriate power of ten to maintain its overall
   *         value.
   * @throws ArithmeticException If {@code rm} is {@link RoundingMode#UNNECESSARY} and the specified scaling operation would require
   *           rounding.
   * @throws IllegalArgumentException If {@code v} or {@code rm} is null.
   */
  public static BigDecimal setScale(BigDecimal v, final int newScale, final RoundingMode rm) {
    assertNotNull(v);
    assertNotNull(rm);
    if (v.scale() <= newScale + 1)
      return v.setScale(newScale, rm);

    if (rm != RoundingMode.UNNECESSARY)
      v = v.setScale(newScale + 1, RoundingMode.DOWN);

    return v.setScale(newScale, rm);
  }

  private BigDecimals() {
  }
}