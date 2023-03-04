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
   * Checks that the specified object reference is null. This method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Assertions.assertNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if null.
   * @throws IllegalArgumentException If {@code obj} is not null.
   */
  public static <T>T assertNull(final T obj, final String message) {
    if (obj != null)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified object reference is null. This method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Assertions.assertNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if null.
   * @throws IllegalArgumentException If {@code obj} is not null.
   */
  public static <T>T assertNull(final T obj, final String format, final Object ... args) {
    if (obj != null)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified object reference is null. This method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Assertions.assertNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @return {@code obj} if null.
   * @throws IllegalArgumentException If {@code obj} is not null.
   */
  public static <T>T assertNull(final T obj) {
    return assertNull(obj, "not null");
  }

  /**
   * Checks that the specified object reference is not null. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Assertions.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null.
   * @throws IllegalArgumentException If {@code obj} is null.
   */
  public static <T>T assertNotNull(final T obj, final String message) {
    if (obj == null)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified object reference is not null. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Assertions.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null.
   * @throws IllegalArgumentException If {@code obj} is null.
   */
  public static <T>T assertNotNull(final T obj, final String format, final Object ... args) {
    if (obj == null)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified object reference is not null. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar bar) {
   *   this.bar = Assertions.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The object reference to check for nullity.
   * @return {@code obj} if not null.
   * @throws IllegalArgumentException If {@code obj} is null.
   */
  public static <T>T assertNotNull(final T obj) {
    return assertNotNull(obj, "null");
  }

  /**
   * Checks that the specified array reference is not null, and that none of its member references is null. This method is designed
   * primarily for doing parameter validation in methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar[] bar) {
   *   this.bar = Assertions.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The component type of the reference.
   * @param obj The array reference to check for nullity.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null.
   * @throws IllegalArgumentException If {@code obj} is null.
   */
  public static <T>T[] assertNotNullArray(final T[] obj, final String message) {
    if (obj == null)
      throw new IllegalArgumentException(message);

    for (int i = 0, i$ = obj.length; i < i$; ++i) // [A]
      if (obj[i] == null)
        throw new IllegalArgumentException(message + " at index " + i);

    return obj;
  }

  /**
   * Checks that the specified array reference is not null, and that none of its member references is null. This method is designed
   * primarily for doing parameter validation in methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar[] bar) {
   *   this.bar = Assertions.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The component type of the reference.
   * @param obj The array reference to check for nullity.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null.
   * @throws IllegalArgumentException If {@code obj} is null.
   */
  public static <T>T[] assertNotNullArray(final T[] obj, final String format, final Object ... args) {
    if (obj == null)
      throw new IllegalArgumentException(String.format(format, args));

    for (int i = 0, i$ = obj.length; i < i$; ++i) // [A]
      if (obj[i] == null)
        throw new IllegalArgumentException(String.format(format, args) + " at index " + i);

    return obj;
  }

  /**
   * Checks that the specified array reference is not null, and that none of its member references is null. This method is designed
   * primarily for doing parameter validation in methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar[] bar) {
   *   this.bar = Assertions.assertNonNull(bar);
   * }
   * </pre>
   *
   * @param <T> The component type of the reference.
   * @param obj The array reference to check for nullity.
   * @return {@code obj} if not null.
   * @throws IllegalArgumentException If {@code obj} is null.
   */
  public static <T>T[] assertNotNullArray(final T[] obj) {
    return assertNotNullArray(obj, "null");
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(boolean ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static boolean[] assertNotEmpty(final boolean[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(boolean ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Assertions.referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static boolean[] assertNotEmpty(final boolean[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(boolean ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static boolean[] assertNotEmpty(final boolean[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static byte[] assertNotEmpty(final byte[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Assertions.referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static byte[] assertNotEmpty(final byte[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static byte[] assertNotEmpty(final byte[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(char ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static char[] assertNotEmpty(final char[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(char ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Assertions.referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static char[] assertNotEmpty(final char[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(char ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static char[] assertNotEmpty(final char[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static short[] assertNotEmpty(final short[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Assertions.referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static short[] assertNotEmpty(final short[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static short[] assertNotEmpty(final short[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static int[] assertNotEmpty(final int[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Assertions.referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static int[] assertNotEmpty(final int[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static int[] assertNotEmpty(final int[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static long[] assertNotEmpty(final long[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Assertions.referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static long[] assertNotEmpty(final long[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static long[] assertNotEmpty(final long[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static float[] assertNotEmpty(final float[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Assertions.referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static float[] assertNotEmpty(final float[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static float[] assertNotEmpty(final float[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static double[] assertNotEmpty(final double[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Assertions.referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static double[] assertNotEmpty(final double[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static double[] assertNotEmpty(final double[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The array to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static <T>T[] assertNotEmpty(final T[] obj, final String message) {
    assertNotNull(obj, message);
    if (obj.length == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The array to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static <T>T[] assertNotEmpty(final T[] obj, final String format, final Object ... args) {
    assertNotNull(obj, format, args);
    if (obj.length == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified array is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the reference.
   * @param obj The array to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static <T>T[] assertNotEmpty(final T[] obj) {
    assertNotNull(obj, "null");
    if (obj.length == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified collection is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the collection reference.
   * @param obj The collection to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static <T extends Collection<?>>T assertNotEmpty(final T obj, final String message) {
    assertNotNull(obj, message);
    if (obj.size() == 0)
      throw new IllegalArgumentException(message);

    return obj;
  }

  /**
   * Checks that the specified collection is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the collection reference.
   * @param obj The collection to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static <T extends Collection<?>>T assertNotEmpty(final T obj, final String format, final Object ... args) {
    if (assertNotNull(obj, format, args).size() == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return obj;
  }

  /**
   * Checks that the specified collection is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(Bar ... bars) {
   *   this.bar = Assertions.assertNonEmpty(bars);
   * }
   * </pre>
   *
   * @param <T> The type of the collection reference.
   * @param obj The collection to check for nullity or emptiness.
   * @return {@code obj} if not null or empty.
   * @throws IllegalArgumentException If {@code obj} is null or empty.
   */
  public static <T extends Collection<?>>T assertNotEmpty(final T obj) {
    assertNotNull(obj, "null");
    if (obj.size() == 0)
      throw new IllegalArgumentException("empty");

    return obj;
  }

  /**
   * Checks that the specified string is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(CharSequence str) {
   *   this.bar = Assertions.assertNonEmpty(str);
   * }
   * </pre>
   *
   * @param str The string to check for nullity or emptiness.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code str} if not null or empty.
   * @throws IllegalArgumentException If {@code str} is null or empty.
   */
  public static CharSequence assertNotEmpty(final CharSequence str, final String message) {
    assertNotNull(str, message);
    if (str.length() == 0)
      throw new IllegalArgumentException(message);

    return str;
  }

  /**
   * Checks that the specified string is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(CharSequence str) {
   *   this.bar = Assertions.assertNonEmpty(str);
   * }
   * </pre>
   *
   * @param str The string to check for nullity or emptiness.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code str} if not null or empty.
   * @throws IllegalArgumentException If {@code str} is null or empty.
   */
  public static CharSequence assertNotEmpty(final CharSequence str, final String format, final Object ... args) {
    if (assertNotNull(str, format, args).length() == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return str;
  }

  /**
   * Checks that the specified string is not null or empty. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(CharSequence str) {
   *   this.bar = Assertions.assertNonEmpty(str);
   * }
   * </pre>
   *
   * @param str The string to check for nullity or emptiness.
   * @return {@code str} if not null or empty.
   * @throws IllegalArgumentException If {@code str} is null or empty.
   */
  public static CharSequence assertNotEmpty(final CharSequence str) {
    assertNotNull(str, "null");
    if (str.length() == 0)
      throw new IllegalArgumentException("empty");

    return str;
  }

  /**
   * Checks that the provided value is finite. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertFinite(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if finite.
   * @throws IllegalArgumentException If {@code value} represents infinity or {@code NaN}.
   */
  public static float assertFinite(final float value, final String message) {
    if (!Float.isFinite(value))
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is finite. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertFinite(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if finite.
   * @throws IllegalArgumentException If {@code value} is represents infinity or {@code NaN}.
   */
  public static float assertFinite(final float value, final String format, final Object ... args) {
    if (!Float.isFinite(value))
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is finite. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertFinite(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite.
   * @return {@code value} if finite.
   * @throws IllegalArgumentException If {@code value} is represents infinity or {@code NaN}.
   */
  public static float assertFinite(final float value) {
    if (!Float.isFinite(value))
      throw new IllegalArgumentException(value + " is not finite");

    return value;
  }

  /**
   * Checks that the provided value is finite. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertFinite(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if finite.
   * @throws IllegalArgumentException If {@code value} represents infinity or {@code NaN}.
   */
  public static double assertFinite(final double value, final String message) {
    if (!Double.isFinite(value))
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is finite. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertFinite(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if finite.
   * @throws IllegalArgumentException If {@code value} is represents infinity or {@code NaN}.
   */
  public static double assertFinite(final double value, final String format, final Object ... args) {
    if (!Double.isFinite(value))
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is finite. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertFinite(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite.
   * @return {@code value} if finite.
   * @throws IllegalArgumentException If {@code value} is represents infinity or {@code NaN}.
   */
  public static double assertFinite(final double value) {
    if (!Double.isFinite(value))
      throw new IllegalArgumentException(value + " is not finite");

    return value;
  }


  /**
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertPositive(bar);
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
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static byte assertPositive(final byte value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static byte assertPositive(final byte value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertPositive(bar);
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
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static short assertPositive(final short value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static short assertPositive(final short value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertPositive(bar);
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
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static int assertPositive(final int value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static int assertPositive(final int value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertPositive(bar);
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
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static long assertPositive(final long value, final String message) {
    if (value <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as positive.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not positive.
   */
  public static long assertPositive(final long value, final String format, final Object ... args) {
    if (value <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is finite and positive. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and positive.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not finite or positive.
   */
  public static float assertPositive(final float value) {
    if (assertFinite(value) <= 0)
      throw new IllegalArgumentException(value + " is not positive");

    return value;
  }

  /**
   * Checks that the provided value is finite and positive. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and positive.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not finite or positive.
   */
  public static float assertPositive(final float value, final String message) {
    if (assertFinite(value) <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is finite and positive. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and positive.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not finite or positive.
   */
  public static float assertPositive(final float value, final String format, final Object ... args) {
    if (assertFinite(value) <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is finite and positive. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and positive.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not finite or positive.
   */
  public static double assertPositive(final double value) {
    if (assertFinite(value) <= 0)
      throw new IllegalArgumentException(value + " is not positive");

    return value;
  }

  /**
   * Checks that the provided value is finite and positive. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and positive.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not finite or positive.
   */
  public static double assertPositive(final double value, final String message) {
    if (assertFinite(value) <= 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is finite and positive. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertPositive(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and positive.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if positive.
   * @throws IllegalArgumentException If {@code value} is not finite or positive.
   */
  public static double assertPositive(final double value, final String format, final Object ... args) {
    if (assertFinite(value) <= 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static byte assertNotNegative(final byte value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static byte assertNotNegative(final byte value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
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
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static short assertNotNegative(final short value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static short assertNotNegative(final short value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
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
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static int assertNotNegative(final int value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static int assertNotNegative(final int value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
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
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static long assertNotNegative(final long value, final String message) {
    if (value < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not negative.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is negative.
   */
  public static long assertNotNegative(final long value, final String format, final Object ... args) {
    if (value < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not negative. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
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
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static byte assertNotZero(final byte value, final String message) {
    if (value == 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static byte assertNotZero(final byte value, final String format, final Object ... args) {
    if (value == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(byte bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static byte assertNotZero(final byte value) {
    if (value == 0)
      throw new IllegalArgumentException(value + " is zero");

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static short assertNotZero(final short value, final String message) {
    if (value == 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static short assertNotZero(final short value, final String format, final Object ... args) {
    if (value == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(short bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static short assertNotZero(final short value) {
    if (value == 0)
      throw new IllegalArgumentException(value + " is zero");

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static int assertNotZero(final int value, final String message) {
    if (value == 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static int assertNotZero(final int value, final String format, final Object ... args) {
    if (value == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(int bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static int assertNotZero(final int value) {
    if (value == 0)
      throw new IllegalArgumentException(value + " is zero");

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static long assertNotZero(final long value, final String message) {
    if (value == 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static long assertNotZero(final long value, final String format, final Object ... args) {
    if (value == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(long bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static long assertNotZero(final long value) {
    if (value == 0)
      throw new IllegalArgumentException(value + " is zero");

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static float assertNotZero(final float value, final String message) {
    if (value == 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static float assertNotZero(final float value, final String format, final Object ... args) {
    if (value == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static float assertNotZero(final float value) {
    if (value == 0)
      throw new IllegalArgumentException(value + " is zero");

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static double assertNotZero(final double value, final String message) {
    if (value == 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static double assertNotZero(final double value, final String format, final Object ... args) {
    if (value == 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is not zero. This method is designed primarily for doing parameter validation in methods and
   * constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertNotZero(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as not zero.
   * @return {@code value} if not zero.
   * @throws IllegalArgumentException If {@code value} is zero.
   */
  public static double assertNotZero(final double value) {
    if (value == 0)
      throw new IllegalArgumentException(value + " is zero");

    return value;
  }

  /**
   * Checks that the provided value is finite and not negative. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and not negative.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is not finite or is negative.
   */
  public static float assertNotNegative(final float value, final String message) {
    if (assertFinite(value) < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is finite and not negative. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and not negative.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is not finite or is negative.
   */
  public static float assertNotNegative(final float value, final String format, final Object ... args) {
    if (assertFinite(value) < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is finite and not negative. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(float bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and not negative.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is not finite or is negative.
   */
  public static float assertNotNegative(final float value) {
    if (assertFinite(value) < 0)
      throw new IllegalArgumentException(value + " is negative");

    return value;
  }
  /**
   * Checks that the provided value is finite and not negative. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and not negative.
   * @param message The detail message to be used for the {@link IllegalArgumentException}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is not finite or is negative.
   */
  public static double assertNotNegative(final double value, final String message) {
    if (assertFinite(value) < 0)
      throw new IllegalArgumentException(message);

    return value;
  }

  /**
   * Checks that the provided value is finite and not negative. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and not negative.
   * @param format The detail message
   *          <a href= "https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format</a> to be used for the
   *          {@link IllegalArgumentException}.
   * @param args Arguments referenced by the format specifiers in the format string to be passed to
   *          {@link String#format(String,Object...)}.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is not finite or is negative.
   */
  public static double assertNotNegative(final double value, final String format, final Object ... args) {
    if (assertFinite(value) < 0)
      throw new IllegalArgumentException(String.format(format, args));

    return value;
  }

  /**
   * Checks that the provided value is finite and not negative. This method is designed primarily for doing parameter validation in
   * methods and constructors, as demonstrated below:
   *
   * <pre>
   * public Foo(double bar) {
   *   this.bar = Assertions.assertNotNegative(bar);
   * }
   * </pre>
   *
   * @param value The value to assert as finite and not negative.
   * @return {@code value} if not negative.
   * @throws IllegalArgumentException If {@code value} is not finite or is negative.
   */
  public static double assertNotNegative(final double value) {
    if (assertFinite(value) < 0)
      throw new IllegalArgumentException(value + " is negative");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if {@code min} is not less than or equal to {@code max}.
   */
  public static byte assertRangeMinMax(final byte value, final byte min, final byte max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if {@code min} is not less than or equal to {@code max}.
   */
  public static short assertRangeMinMax(final short value, final short min, final short max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if {@code min} is not less than or equal to {@code max}.
   */
  public static int assertRangeMinMax(final int value, final int min, final int max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if {@code min} is not less than or equal to {@code max}.
   */
  public static long assertRangeMinMax(final long value, final long min, final long max) {
    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if {@code min} is not less than or equal to {@code max},
   *           or if any of the provided values are not finite.
   */
  public static float assertRangeMinMax(final float value, final float min, final float max) {
    assertFinite(value);
    assertFinite(min);
    assertFinite(max);

    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks the given {@code value} is between {@code min} (inclusive) and {@code max} (inclusive).
   *
   * @param value The value to assert as between {@code min} (inclusive) and {@code max} (inclusive).
   * @param min The minimum bound of the range (inclusive).
   * @param max The maximum bound of the range (inclusive).
   * @return {@code value} if it is in range.
   * @throws IllegalArgumentException If {@code value} is not in range, or if {@code min} is not less than or equal to {@code max},
   *           or if any of the provided values are not finite.
   */
  public static double assertRangeMinMax(final double value, final double min, final double max) {
    assertFinite(value);
    assertFinite(min);
    assertFinite(max);

    if (max < min)
      throw new IllegalArgumentException("Illegal bounds: min (" + min + ") is not less than or equal to max (" + max + ")");

    if (value < min || max < value)
      throw new IllegalArgumentException(value + " is not in range: [" + min + "," + max + ")");

    return value;
  }

  /**
   * Checks if the given {@code offset} and {@code length} are non-negative. If not, throws an
   * {@link ArrayIndexOutOfBoundsException}.
   *
   * @param offset The offset in an array.
   * @param length The length of an array.
   * @throws ArrayIndexOutOfBoundsException If the given {@code offset} or {@code length} is negative.
   */
  public static void assertOffsetLengthArray(final int offset, final int length) {
    if (offset < 0)
      throw new ArrayIndexOutOfBoundsException(offset);

    if (length < 0)
      throw new ArrayIndexOutOfBoundsException(length);
  }

  /**
   * Checks if the given {@code fromIndex} and {@code toIndex} are in range. If not, throws an
   * {@link ArrayIndexOutOfBoundsException} or {@link IllegalArgumentException}.
   *
   * @param fromIndex The from index.
   * @param toIndex The to index.
   * @param length The array length.
   * @throws ArrayIndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
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
   * Checks if the given index is in range. If not, throws an {@link ArrayIndexOutOfBoundsException}.
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
   * Checks if the given {@code offset} and {@code length} are non-negative. If not, throws an
   * {@link ArrayIndexOutOfBoundsException}.
   *
   * @param offsetLabel The string label of the "offset" parameter.
   * @param offset The offset in an array.
   * @param lengthLabel The string label of the "length" parameter.
   * @param length The length of an array.
   * @throws ArrayIndexOutOfBoundsException If the given {@code offset} or {@code length} is negative.
   */
  public static void assertOffsetLength(final String offsetLabel, final int offset, final String lengthLabel, final int length) {
    if (offset < 0)
      throw new IndexOutOfBoundsException(offsetLabel + ": " + offset);

    if (length < 0)
      throw new IndexOutOfBoundsException(lengthLabel + ": " + length);
  }

  /**
   * Checks if the given index is in range. If not, throws an {@link IndexOutOfBoundsException}.
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
   * Checks if the given {@code fromIndex} and {@code toIndex} are in range. If not, throws an {@link IndexOutOfBoundsException} or
   * {@link IllegalArgumentException}.
   *
   * @param fromIndexLabel The string label of the "fromIndex" parameter.
   * @param fromIndex The from index.
   * @param toIndexLabel The string label of the "toIndex" parameter.
   * @param toIndex The to index.
   * @param sizeLabel The string label of the "size" parameter.
   * @param size The size.
   * @throws IndexOutOfBoundsException If the given {@code fromIndex} or {@code toIndex} is out of range.
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
   * Checks if the given index is in range. If not, throws an {@link IndexOutOfBoundsException}.
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
   * Checks the given {@code offset} and {@code count} against {@code 0} and {@code length} bounds.
   *
   * @param lengthLabel The string label of the "length" parameter.
   * @param length The length of the range.
   * @param offsetLabel The string label of the "offset" parameter.
   * @param offset The offset in the range.
   * @param countLabel The string label of the "count" parameter.
   * @param count The count in the range.
   * @throws IllegalArgumentException If {@code length} is negative.
   * @throws IndexOutOfBoundsException If {@code offset} is negative, {@code count} is negative, or {@code length} is less than
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