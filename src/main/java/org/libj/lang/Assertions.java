/* Copyright (c) 2019 LibJ
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

import java.util.Collection;

/**
 * Utility methods implementing common assertion operations.
 */
public final class Assertions {
  /**
   * Checks that the specified object reference is not {@code null}. This method
   * is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Arguments.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code obj} if not {@code null}.
   * @throws IllegalArgumentException If {@code obj} is {@code null}
   */
  public static <T>T assertNotNull(final T obj, final String message) {
    if (obj == null)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified object reference is not {@code null}. This method
   * is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Arguments.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code obj} if not {@code null}.
   * @throws IllegalArgumentException If {@code obj} is {@code null}
   */
  public static <T>T assertNotNull(final T obj, final String format, final Object ... args) {
    if (obj == null)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified object reference is not {@code null}. This method
   * is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Arguments.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @return {@code obj} if not {@code null}.
   * @throws IllegalArgumentException If {@code obj} is {@code null}
   */
  public static <T>T assertNotNull(final T obj) {
    return assertNotNull(obj, "null");
  }

  /**
   * Checks that the specified array is not null nor empty. This method is
   * designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Arguments.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code obj} if not {@code null} or empty.
   * @throws IllegalArgumentException If {@code obj} is {@code null} or empty.
   */
  public static <T>T[] assertNotEmpty(final T[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null nor empty. This method is
   * designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Arguments.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code obj} if not {@code null} or empty.
   * @throws IllegalArgumentException If {@code obj} is {@code null} or empty.
   */
  public static <T>T[] assertNotEmpty(final T[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null nor empty. This method is
   * designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Arguments.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not {@code null} or empty.
   * @throws IllegalArgumentException If {@code obj} is {@code null} or empty.
   */
  public static <T>T[] assertNotEmpty(final T[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified collection is not null nor empty. This method is
   * designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Arguments.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the collection reference.
   * @param obj The collection to check for nullity or emptiness.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code obj} if not {@code null} or empty.
   * @throws IllegalArgumentException If {@code obj} is {@code null} or empty.
   */
  public static <T extends Collection<?>>T assertNotEmpty(final T obj, final String message) {
    assertNotNull(obj, message);
    if (obj.size() == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified collection is not null nor empty. This method is
   * designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Arguments.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the collection reference.
   * @param obj The collection to check for nullity or emptiness.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code obj} if not {@code null} or empty.
   * @throws IllegalArgumentException If {@code obj} is {@code null} or empty.
   */
  public static <T extends Collection<?>>T assertNotEmpty(final T obj, final String format, final Object ... args) {
    if (assertNotNull(obj, format).size() == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified collection is not null nor empty. This method is
   * designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Arguments.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the collection reference.
   * @param obj The collection to check for nullity or emptiness.
   * @return {@code obj} if not {@code null} or empty.
   * @throws IllegalArgumentException If {@code obj} is {@code null} or empty.
   */
  public static <T extends Collection<?>>T assertNotEmpty(final T obj) {
    assertNotNull(obj, "null");
    if (obj.size() == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static byte assertPositive(final byte value) {
    if (value <= 0)
      throw new IllegalArgumentException(value + " is not positive");

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static byte assertPositive(final byte value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static byte assertPositive(final byte value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static short assertPositive(final short value) {
    if (value <= 0)
      throw new IllegalArgumentException(value + " is not positive");

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static short assertPositive(final short value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static short assertPositive(final short value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static int assertPositive(final int value) {
    if (value <= 0)
      throw new IllegalArgumentException(value + " is not positive");

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static int assertPositive(final int value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static int assertPositive(final int value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static long assertPositive(final long value) {
    if (value <= 0)
      throw new IllegalArgumentException(value + " is not positive");

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static long assertPositive(final long value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static long assertPositive(final long value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static float assertPositive(final float value) {
    if (value <= 0)
      throw new IllegalArgumentException(value + " is not positive");

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static float assertPositive(final float value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static float assertPositive(final float value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static double assertPositive(final double value) {
    if (value <= 0)
      throw new IllegalArgumentException(value + " is not positive");

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static double assertPositive(final double value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Arguments.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static double assertPositive(final double value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static byte assertNotNegative(final byte value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static byte assertNotNegative(final byte value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static byte assertNotNegative(final byte value) {
    if (value < 0)
      throw new IllegalArgumentException(value + " is negative");

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static short assertNotNegative(final short value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static short assertNotNegative(final short value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static short assertNotNegative(final short value) {
    if (value < 0)
      throw new IllegalArgumentException(value + " is negative");

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static int assertNotNegative(final int value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static int assertNotNegative(final int value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static int assertNotNegative(final int value) {
    if (value < 0)
      throw new IllegalArgumentException(value + " is negative");

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static long assertNotNegative(final long value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static long assertNotNegative(final long value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static long assertNotNegative(final long value) {
    if (value < 0)
      throw new IllegalArgumentException(value + " is negative");

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static float assertNotNegative(final float value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static float assertNotNegative(final float value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static float assertNotNegative(final float value) {
    if (value < 0)
      throw new IllegalArgumentException(value + " is negative");

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the
   *          {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static double assertNotNegative(final double value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message <a href=
   *          "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a>
   *          to be used for the {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format
   *          string to be passed to {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static double assertNotNegative(final double value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed
   * primarily for doing parameter validation in methods and constructors, as
   * demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Arguments.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static double assertNotNegative(final double value) {
    if (value < 0)
      throw new IllegalArgumentException(value + " is negative");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and
   * {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and
   *          {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if
   *           {@code min} is not less than or equal to {@code max}.
   */
  public static byte assertRangeMinMax(final byte value, final byte min, final byte max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and
   * {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and
   *          {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if
   *           {@code min} is not less than or equal to {@code max}.
   */
  public static short assertRangeMinMax(final short value, final short min, final short max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and
   * {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and
   *          {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if
   *           {@code min} is not less than or equal to {@code max}.
   */
  public static int assertRangeMinMax(final int value, final int min, final int max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and
   * {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and
   *          {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if
   *           {@code min} is not less than or equal to {@code max}.
   */
  public static long assertRangeMinMax(final long value, final long min, final long max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and
   * {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and
   *          {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if
   *           {@code min} is not less than or equal to {@code max}.
   */
  public static float assertRangeMinMax(final float value, final float min, final float max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and
   * {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and
   *          {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if
   *           {@code min} is not less than or equal to {@code max}.
   */
  public static double assertRangeMinMax(final double value, final double min, final double max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks if the given {@code offset} and {@code length} are non-negative. If
   * not, throws an {@link ArrayIndexOutOfBoundsException}.
   *
   * @param offset The offset in an array.
   * @param length The length of an array.
   * @throws ArrayIndexOutOfBoundsException If the given {@code offset} or
   *           {@code length} is negative.
   */
  public static void assertOffsetLengthArray(final int offset, final int length) {
    if (offset < 0)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (length < 0)
      throw new ArrayIndexOutOfBoundsException(length);
  }

  /**
   * Checks if the given {@code fromIndex} and {@code toIndex} are in range. If
   * not, throws an {@link ArrayIndexOutOfBoundsException} or
   * {@link IllegalArgumentException}.
   *
   * @param fromIndex The from index.
   * @param toIndex The to index.
   * @param length The array length.
   * @throws ArrayIndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @throws IllegalArgumentException If {@code fromIndex > toIndex}.
   */
  public static void assertRangeArray(final int fromIndex, final int toIndex, final int length) {
    if (fromIndex < 0)
      throw new ArrayIndexOutOfBoundsException(fromIndex);

    if (toIndex > length)
      throw new ArrayIndexOutOfBoundsException(toIndex);

    if (fromIndex > toIndex)
      throw new IllegalArgumentException("fromIndex (" + fromIndex + ") > toIndex (" + toIndex + ")");
  }

  /**
   * Checks if the given index is in range. If not, throws an
   * {@link ArrayIndexOutOfBoundsException}.
   *
   * @param index The index to check.
   * @param length The size.
   * @return The given index.
   * @throws ArrayIndexOutOfBoundsException If the given index is out of range.
   */
  public static int assertRangeArray(final int index, final int length) {
    if (index < 0 || length <= index)
      throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + length);

    return index;
  }

  /**
   * Checks if the given {@code offset} and {@code length} are non-negative. If
   * not, throws an {@link ArrayIndexOutOfBoundsException}.
   *
   * @param offsetLabel The string label of the "offset" parameter.
   * @param offset The offset in an array.
   * @param lengthLabel The string label of the "length" parameter.
   * @param length The length of an array.
   * @throws ArrayIndexOutOfBoundsException If the given {@code offset} or
   *           {@code length} is negative.
   */
  public static void assertOffsetLength(final String offsetLabel, final int offset, final String lengthLabel, final int length) {
    if (offset < 0)
      throw new IndexOutOfBoundsException(offsetLabel + ": " + offset);

    if (length < 0)
      throw new IndexOutOfBoundsException(lengthLabel + ": " + length);
  }

  /**
   * Checks if the given index is in range. If not, throws an
   * {@link IndexOutOfBoundsException}.
   *
   * @param indexLabel The string label of the "index" parameter.
   * @param index The index to check.
   * @param lengthLabel The string label of the "length" parameter.
   * @param length The size.
   * @return The given index.
   * @throws IndexOutOfBoundsException If the given index is out of range.
   */
  public static int assertRange(final String indexLabel, final int index, final String lengthLabel, final int length) {
    if (index < 0 || length <= index)
      throw new IndexOutOfBoundsException(indexLabel + ": " + index + ", " + lengthLabel + ": " + length);

    return index;
  }

  /**
   * Checks if the given {@code fromIndex} and {@code toIndex} are in range. If
   * not, throws an {@link IndexOutOfBoundsException} or
   * {@link IllegalArgumentException}.
   *
   * @param fromIndexLabel The string label of the "fromIndex" parameter.
   * @param fromIndex The from index.
   * @param toIndexLabel The string label of the "toIndex" parameter.
   * @param toIndex The to index.
   * @param sizeLabel The string label of the "size" parameter.
   * @param size The size.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or
   *           {@code toIndex} is out of range.
   * @throws IllegalArgumentException If {@code fromIndex > toIndex}.
   */
  public static void assertRange(final String fromIndexLabel, final int fromIndex, final String toIndexLabel, final int toIndex, final String sizeLabel, final int size) {
    if (fromIndex < 0)
      throw new IndexOutOfBoundsException(fromIndexLabel + ": " + fromIndex);

    if (toIndex > size)
      throw new IndexOutOfBoundsException(toIndexLabel + " (" + toIndex + ") > " + sizeLabel + " (" + size + ")");

    if (fromIndex > toIndex)
      throw new IllegalArgumentException(fromIndexLabel + " (" + fromIndex + ") > " + toIndexLabel + " (" + toIndex + ")");
  }

  /**
   * Checks if the given index is in range. If not, throws an
   * {@link IndexOutOfBoundsException}.
   *
   * @param indexLabel The string label of the "index" parameter.
   * @param index The index to check.
   * @param sizeLabel The string label of the "size" parameter.
   * @param size The size.
   * @param forAdd Whether the range check is for an add operation or not.
   * @return The given index.
   * @throws IndexOutOfBoundsException If the given index is out of range.
   */
  public static int assertRange(final String indexLabel, final int index, final String sizeLabel, final int size, final boolean forAdd) {
    if (index < 0 || (forAdd ? size < index : size <= index))
      throw new IndexOutOfBoundsException(indexLabel + ": " + index + ", " + sizeLabel + ": " + size);

    return index;
  }

  /**
   * Checks the given {@code offset} and {@code count} against {@code 0} and
   * {@code length} bounds.
   *
   * @param lengthLabel The string label of the "length" parameter.
   * @param length The length of the range.
   * @param offsetLabel The string label of the "offset" parameter.
   * @param offset The offset in the range.
   * @param countLabel The string label of the "count" parameter.
   * @param count The count in the range.
   * @throws IllegalArgumentException If {@code length} is negative.
   * @throws IndexOutOfBoundsException If {@code offset} is negative,
   *           {@code count} is negative, or {@code length} is less than
   *           {@code offset + count}.
   */
  public static void assertBoundsOffsetCount(final String lengthLabel, final int length, final String offsetLabel, final int offset, final String countLabel, final int count) {
    if (length < 0)
      throw new IllegalArgumentException(lengthLabel + ": " + length);

    if (offset < 0)
      throw new IndexOutOfBoundsException(offsetLabel + ": " + offset);

    if (count < 0)
      throw new IndexOutOfBoundsException(countLabel + ": " + count);

    if (length < offset + count)
      throw new IndexOutOfBoundsException(lengthLabel + " (" + length + ") < " + offsetLabel + " (" + offset + ") + " + countLabel + " (" + count + ")");
  }

  private Assertions() {
  }
}