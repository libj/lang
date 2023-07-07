/* Copyright (c) 2018 LibJ
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Utility functions for operations pertaining to {@link Annotation}.
 */
public final class Annotations {
  /**
   * Returns a map of name-value pairs representing the attributes in the specified annotation.
   *
   * @param annotation The {@link Annotation} whose attributes to get.
   * @return A map of name-value pairs representing the attributes in the specified annotation.
   * @throws NullPointerException If {@code annotation} is null.
   */
  public static Map<String,Object> getAttributes(final Annotation annotation) {
    return getAttributes(annotation, false);
  }

  /**
   * Returns a map of name-value pairs representing the attributes in the specified annotation.
   *
   * @param annotation The {@link Annotation} whose attributes to get.
   * @param removeDefaults Whether fields whose values are equal to the {@code annotation}'s defaults should be omitted.
   * @return A map of name-value pairs representing the attributes in the specified annotation.
   * @throws NullPointerException If {@code annotation} is null.
   */
  public static Map<String,Object> getAttributes(final Annotation annotation, final boolean removeDefaults) {
    final Class<? extends Annotation> annotationType = annotation.annotationType();
    final HashMap<String,Object> attributes = new HashMap<>();
    try {
      for (final Method method : annotationType.getDeclaredMethods()) { // [A]
        final Object value = method.invoke(annotation);
        if (!removeDefaults || !Objects.equals(method.getDefaultValue(), value))
          attributes.put(method.getName(), value);
      }

      return attributes;
    }
    catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    catch (final InvocationTargetException e) {
      final Throwable cause = e.getCause();
      if (cause instanceof RuntimeException)
        throw (RuntimeException)cause;

      throw new RuntimeException(cause);
    }
  }

  /**
   * Returns a {@link #toString()} representation of {@code annotation}, with its property names sorted alphabetically.
   * <p>
   * This method is equivalent to calling:
   *
   * <pre>
   * {@code
   * toSortedString(annotation, null)
   * }
   * </pre>
   *
   * @param annotation The {@link Annotation}.
   * @return A {@link #toString()} representation of {@code annotation}, with its property names sorted by {@code comparator}.
   * @throws NullPointerException If {@code annotation} is null.
   */
  public static String toSortedString(final Annotation annotation) {
    return toSortedString(annotation, null, false);
  }

  /**
   * Returns a {@link #toString()} representation of {@code annotation}, with its property names sorted alphabetically.
   * <p>
   * This method is equivalent to calling:
   *
   * <pre>
   * {@code
   * toSortedString(annotation, null)
   * }
   * </pre>
   *
   * @param annotation The {@link Annotation}.
   * @param removeDefaults Whether fields whose values are equal to the {@code annotation}'s defaults should be omitted.
   * @return A {@link #toString()} representation of {@code annotation}, with its property names sorted by {@code comparator}.
   * @throws NullPointerException If {@code annotation} is null.
   */
  public static String toSortedString(final Annotation annotation, final boolean removeDefaults) {
    return toSortedString(annotation, null, removeDefaults);
  }

  /**
   * Returns a {@link #toString()} representation of {@code annotation}, with its property names sorted by {@code comparator}.
   *
   * @param annotation The {@link Annotation}.
   * @param comparator The {@link Comparator}.
   * @return A {@link #toString()} representation of {@code annotation}, with its property names sorted by {@code comparator}.
   * @throws NullPointerException If {@code annotation} is null.
   */
  public static String toSortedString(final Annotation annotation, final Comparator<String> comparator) {
    return toSortedString(annotation, comparator, false);
  }

  /**
   * Returns a {@link #toString()} representation of {@code annotation}, with its property names sorted by {@code comparator}. If
   * {@code comparator} is null, sorting will be done according to the natural ordering of the keys.
   *
   * @param annotation The {@link Annotation}.
   * @param comparator The {@link Comparator}.
   * @param removeDefaults Whether fields whose values are equal to the {@code annotation}'s defaults should be omitted.
   * @return A {@link #toString()} representation of {@code annotation}, with its property names sorted by {@code comparator}.
   * @throws NullPointerException If {@code annotation} is null.
   */
  public static String toSortedString(final Annotation annotation, final Comparator<String> comparator, final boolean removeDefaults) {
    final String str = annotation.toString();
    if (str.indexOf('(') < 0)
      return str;

    final TreeMap<String,Object> attributes = comparator != null ? new TreeMap<>(comparator) : new TreeMap<>();
    attributes.putAll(getAttributes(annotation, removeDefaults));

    final StringBuilder b = new StringBuilder("@").append(annotation.annotationType().getName()).append('(');
    if (attributes.size() > 0) {
      final Iterator<Map.Entry<String,Object>> iterator = attributes.entrySet().iterator();
      for (int i = 0; iterator.hasNext(); ++i) { // [I]
        if (i > 0)
          b.append(", ");

        final Map.Entry<String,Object> entry = iterator.next();
        b.append(entry.getKey()).append('=');
        appendValue(b, comparator, entry.getValue());
      }
    }

    return b.append(')').toString();
  }

  private static void appendValue(final StringBuilder b, final Comparator<String> c, final Object v) {
    if (v instanceof String) {
      b.append('"').append(v).append('"');
    }
    else if (v instanceof Class) {
      b.append(((Class<?>)v).getName()).append(".class");
    }
    else {
      final Class<? extends Object> cls = v.getClass();
      if (cls.isArray()) {
        b.append('{');
        final Class<?> componentType = cls.getComponentType();
        if (componentType == byte.class) {
          final byte[] a = (byte[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            b.append(a[i]);
          }
        }
        else if (componentType == char.class) {
          final char[] a = (char[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            b.append(a[i]);
          }
        }
        else if (componentType == short.class) {
          final short[] a = (short[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            b.append(a[i]);
          }
        }
        else if (componentType == int.class) {
          final int[] a = (int[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            b.append(a[i]);
          }
        }
        else if (componentType == long.class) {
          final long[] a = (long[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            b.append(a[i]);
          }
        }
        else if (componentType == double.class) {
          final double[] a = (double[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            b.append(a[i]);
          }
        }
        else if (componentType == float.class) {
          final float[] a = (float[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            b.append(a[i]);
          }
        }
        else if (componentType == boolean.class) {
          final boolean[] a = (boolean[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            b.append(a[i]);
          }
        }
        else {
          final Object[] a = (Object[])v;
          for (int i = 0, i$ = a.length; i < i$; ++i) { // [A]
            if (i > 0)
              b.append(", ");

            appendValue(b, c, a[i]);
          }
        }

        b.append('}');
      }
      else if (v instanceof Annotation) {
        b.append(toSortedString((Annotation)v, c, true));
      }
      else {
        b.append(v);
      }
    }
  }

  private Annotations() {
  }
}