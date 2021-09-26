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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * Utility functions for operations pertaining to {@link Object}.
 */
public final class ObjectUtil {
  /**
   * Returns the input argument.
   *
   * @param <T> The type of the input and output object.
   * @param obj The object to return.
   * @return The input argument.
   * @see java.util.function.Function#identity()
   */
  public static <T>T identity(final T obj) {
    return obj;
  }

  /**
   * Returns the class name of object {@code obj}, concatenated with '@', and
   * the hexadecimal representation of its identity hash code.
   *
   * @param obj The object.
   * @return The class name of object {@code obj}, concatenated with '@', and
   *         the hexadecimal representation of its identity hash code.
   * @throws IllegalArgumentException If {@code obj} is null.
   * @see System#identityHashCode(Object)
   */
  public static String identityString(final Object obj) {
    return obj == null ? "null" : obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
  }

  /**
   * Returns the simple class name of object {@code obj}, concatenated with '@',
   * and the hexadecimal representation of its identity hash code.
   *
   * @param obj The object.
   * @return The simple class name of object {@code obj}, concatenated with '@',
   *         and the hexadecimal representation of its identity hash code.
   * @see System#identityHashCode(Object)
   */
  public static String simpleIdentityString(final Object obj) {
    return obj == null ? "null" : obj.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(obj));
  }

  private static final HashMap<Class<?>,Method> classToCloneMethod = new HashMap<>();

  /**
   * Returns a clone of the specified object that implements the
   * {@link Cloneable} interface.
   *
   * @param <T> The type of the specified object.
   * @param obj The object to be cloned.
   * @return A clone of the specified object that implements the
   *         {@link Cloneable} interface.
   * @throws IllegalArgumentException If {@code obj} is null.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Cloneable>T clone(final T obj) {
    final Class<?> cls = assertNotNull(obj).getClass();
    Method cloneMethod = classToCloneMethod.get(cls);
    if (cloneMethod == null) {
      try {
        classToCloneMethod.put(cls, cloneMethod = obj.getClass().getDeclaredMethod("clone"));
      }
      catch (final NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    try {
      cloneMethod.setAccessible(true);
      final T clone = (T)cloneMethod.invoke(obj);
      cloneMethod.setAccessible(false);
      return clone;
    }
    catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    catch (final InvocationTargetException e) {
      if (e.getCause() instanceof RuntimeException)
        throw (RuntimeException)e.getCause();

      throw new RuntimeException(e.getCause());
    }
  }

  /**
   * Returns {@code true} if the arguments are equal to each other and
   * {@code false} otherwise. Consequently, if both arguments are {@code null},
   * {@code true} is returned and if exactly one argument is {@code null},
   * {@code false} is returned. Otherwise, equality is determined by using the
   * {@link Object#equals equals} method of the first argument.
   * <p>
   * This method differentiates itself from
   * {@link Objects#equals(Object,Object)} by recursively invoking this same
   * method when evaluating equality of arrays and {@link Iterable}s. This
   * effectively merges the functionality of
   * {@link Objects#equals(Object,Object)} and
   * {@link Arrays#equals(Object[],Object[])} into one method.
   *
   * @param o1 An object to be equated to {@code o2}.
   * @param o2 An object to be equated to {@code o1}.
   * @return {@code true} if the arguments are equal to each other and
   *         {@code false} otherwise.
   * @see Objects#equals(Object,Object)
   * @see Arrays#equals(Object[],Object[])
   */
  public static boolean equals(final Object o1, final Object o2) {
    if (o1 == o2)
      return true;

    if (o1 == null)
      return o2 == null;

    if (o2 == null)
      return false;

    final Class<?> c1 = o1.getClass();
    final Class<?> c2 = o2.getClass();
    if (c1 == Optional.class)
      return c2 == Optional.class && equals(((Optional<?>)o1).orElse(null), ((Optional<?>)o2).orElse(null));

    if (o1 instanceof Iterable) {
      if (!(o2 instanceof Iterable))
        return false;

      final Iterator<?> i1 = ((Iterable<?>)o1).iterator();
      final Iterator<?> i2 = ((Iterable<?>)o2).iterator();
      while (i1.hasNext() && i2.hasNext())
        if (!ObjectUtil.equals(i1.next(), i2.next()))
          return false;

      return !i1.hasNext() && !i2.hasNext();
    }

    if (!c1.isArray())
      return !c2.isArray() && Objects.equals(o1, o2);

    final Class<?> t1 = c1.getComponentType();
    final Class<?> t2 = c2.getComponentType();
    if (t1.isPrimitive() || t2.isPrimitive()) {
      if (t1 != t2)
        return false;

      if (t1 == boolean.class)
        return Arrays.equals((boolean[])o1, (boolean[])o2);

      if (t1 == byte.class)
        return Arrays.equals((byte[])o1, (byte[])o2);

      if (t1 == char.class)
        return Arrays.equals((char[])o1, (char[])o2);

      if (t1 == short.class)
        return Arrays.equals((short[])o1, (short[])o2);

      if (t1 == int.class)
        return Arrays.equals((int[])o1, (int[])o2);

      if (t1 == long.class)
        return Arrays.equals((long[])o1, (long[])o2);

      if (t1 == float.class)
        return Arrays.equals((float[])o1, (float[])o2);

      if (t1 == double.class)
        return Arrays.equals((double[])o1, (double[])o2);
    }

    final Object[] a1 = (Object[])o1;
    final Object[] a2 = (Object[])o2;
    final int length = a1.length;
    if (a2.length != length)
      return false;

    for (int i = 0; i < length; i++)
      if (!ObjectUtil.equals(a1[i], a2[i]))
        return false;

    return true;
  }

  /**
   * Returns the hash code of a non-{@code null} argument and 0 for a
   * {@code null} argument.
   * <p>
   * This method differentiates itself from {@link Objects#hashCode(Object)} by
   * recursively invoking this same method when computing the hash code of
   * arrays and {@link Iterable}s. This effectively merges the functionality of
   * {@link Objects#hashCode(Object)} and {@link Arrays#hashCode(Object[])} into
   * one method.
   *
   * @param obj An object
   * @return The hash code of a non-{@code null} argument and 0 for a
   *         {@code null} argument.
   * @see Objects#hashCode(Object)
   * @see Arrays#hashCode(Object[])
   */
  public static int hashCode(final Object obj) {
    if (obj == null)
      return 0;

    final Class<?> cls = obj.getClass();
    if (cls == Optional.class)
      return hashCode(((Optional<?>)obj).orElse(null));

    if (Iterable.class.isAssignableFrom(cls)) {
      final Iterator<?> i = ((Iterable<?>)obj).iterator();
      int result = 1;
      while (i.hasNext()) {
        final Object element = i.next();
        result = 31 * result + (element == null ? 0 : ObjectUtil.hashCode(element));
      }

      return result;
    }

    if (!cls.isArray())
      return obj.hashCode();

    final Class<?> type = cls.getComponentType();
    if (type == boolean.class)
      return Arrays.hashCode((boolean[])obj);

    if (type == byte.class)
      return Arrays.hashCode((byte[])obj);

    if (type == char.class)
      return Arrays.hashCode((char[])obj);

    if (type == short.class)
      return Arrays.hashCode((short[])obj);

    if (type == int.class)
      return Arrays.hashCode((int[])obj);

    if (type == long.class)
      return Arrays.hashCode((long[])obj);

    if (type == float.class)
      return Arrays.hashCode((float[])obj);

    if (type == double.class)
      return Arrays.hashCode((double[])obj);

    int result = 1;
    for (final Object element : (Object[])obj)
      result = 31 * result + (element == null ? 0 : ObjectUtil.hashCode(element));

    return result;
  }

  /**
   * Returns the result of calling {@code toString} for a non-{@code
   * null} argument and {@code "null"} for a {@code null} argument.
   * <p>
   * This method differentiates itself from {@link Objects#toString(Object)} by
   * detecting array arguments and invoking the relevant
   * {@link Arrays#toString(Object[])} method. This effectively merges the
   * functionality of {@link Objects#toString(Object)} and
   * {@link Arrays#toString(Object[])} into one method.
   *
   * @param obj An object.
   * @return The result of calling {@code toString} for a non-{@code
   * null} argument and {@code "null"} for a {@code null} argument.
   * @see Objects#toString(Object)
   * @see Arrays#toString(Object[])
   */
  public static String toString(final Object obj) {
    if (obj == null)
      return "null";

    final Class<?> cls = obj.getClass();
    if (!cls.isArray())
      return obj.toString();

    final Class<?> type = cls.getComponentType();
    if (type == boolean.class)
      return Arrays.toString((boolean[])obj);

    if (type == byte.class)
      return Arrays.toString((byte[])obj);

    if (type == char.class)
      return Arrays.toString((char[])obj);

    if (type == short.class)
      return Arrays.toString((short[])obj);

    if (type == int.class)
      return Arrays.toString((int[])obj);

    if (type == long.class)
      return Arrays.toString((long[])obj);

    if (type == float.class)
      return Arrays.toString((float[])obj);

    if (type == double.class)
      return Arrays.toString((double[])obj);

    final StringBuilder builder = new StringBuilder();
    builder.append('[');
    final Object[] array = (Object[])obj;
    for (int i = 0; i < array.length; ++i) {
      if (i > 0)
        builder.append(", ");

      builder.append(toString(array[i]));
    }

    builder.append(']');
    return builder.toString();
  }

  private ObjectUtil() {
  }
}