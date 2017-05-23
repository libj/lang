/* Copyright (c) 2014 lib4j
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

package org.lib4j.lang;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.lib4j.util.For;

public final class Objects {
  private static final Map<Class<?>,Field[]> blackWhiteListMap = new HashMap<Class<?>,Field[]>();

  private static final For.Filter<Field> nonBlacklistFilter = new For.Filter<Field>() {
    @Override
    public boolean accept(final Field item, final Object ... args) {
      final boolean accept = !item.isSynthetic() && !Modifier.isStatic(item.getModifiers()) && item.getAnnotation(NotEqualable.class) == null;
      if (accept)
        item.setAccessible(true);

      return accept;
    }
  };

  private static final For.Filter<Field> whitelistFilter = new For.Filter<Field>() {
    @Override
    public boolean accept(final Field item, final Object ... args) {
      final boolean accept = !item.isSynthetic() && !Modifier.isStatic(item.getModifiers()) && item.getAnnotation(Equalable.class) != null;
      if (accept)
        item.setAccessible(true);

      return accept;
    }
  };

  public static boolean equals(final Object a, final Object b, final Field field) throws IllegalAccessException {
    if (boolean.class == field.getType())
      return field.getBoolean(a) == field.getBoolean(b);

    if (byte.class == field.getType())
      return field.getByte(a) == field.getByte(b);

    if (char.class == field.getType())
      return field.getChar(a) == field.getChar(b);

    if (short.class == field.getType())
      return field.getShort(a) == field.getShort(b);

    if (int.class == field.getType())
      return field.getInt(a) == field.getInt(b);

    if (long.class == field.getType())
      return field.getLong(a) == field.getLong(b);

    if (float.class == field.getType())
      return field.getFloat(a) == field.getFloat(a);

    if (double.class == field.getType())
      return field.getDouble(a) == field.getDouble(a);

    final Object ao = field.get(a);
    final Object bo = field.get(b);
    return ao != null ? ao.equals(bo) : bo == null;
  }

  public static boolean equals(final Object a, final Object b, final String ... fieldName) {
    if (fieldName == null)
      throw new NullPointerException("fieldName == null");

    if (a == b || fieldName.length == 0)
      return true;

    if (a == null || b == null || !a.getClass().isInstance(b))
      return false;

    boolean equals = false;
    final Class<?> cls = a.getClass();
    Field field;
    try {
      for (final String name : fieldName) {
        field = Classes.getDeclaredField(cls, name);
        if (field == null)
          throw new IllegalArgumentException("Field name \"" + name + "\" not found in calss " + cls.getName());

        equals = equals(a, b, field);
        if (!equals)
          return false;
      }

      return true;
    }
    catch (final IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static boolean equals(final Object a, final Object b) {
    if (a == b)
      return true;

    if (a == null || b == null || a.getClass() != b.getClass())
      return false;

    try {
      boolean equals;
      final Class<?> cls = a.getClass();
      Field[] fields = blackWhiteListMap.get(cls);
      if (fields == null) {
        final Field[] allFields = Classes.getDeclaredFieldsDeep(cls);
        fields = For.<Field>recursiveOrdered(allFields, Field.class, whitelistFilter);
        if (fields.length == 0)
          fields = For.<Field>recursiveOrdered(allFields, Field.class, nonBlacklistFilter);

        blackWhiteListMap.put(cls, fields);
      }

      if (fields != null) {
        for (int i = 0; i < fields.length; i++) {
          equals = equals(a, b, fields[i]);
          if (!equals)
            return false;
        }
      }

      return true;
    }
    catch (final IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static boolean pairsEqual(final Object ... pairs) {
    if (pairs.length == 0)
      throw new IllegalArgumentException("pairs.length == 0");

    if (pairs.length % 2 != 0)
      throw new IllegalArgumentException("pairs.length % 2 != 0");

    for (int i = 0; i < pairs.length; i += 2)
      if (pairs[i] != null ? !pairs[i].equals(pairs[i + 1]) : pairs[i + 1] != null)
        return false;

    return true;
  }

  public static int hashCode(final Object obj, final Field field) throws IllegalAccessException {
    int normal;
    Object member;
    if (boolean.class == field.getType())
      normal = field.getBoolean(obj) ? 1231 : 1237;
    else if (byte.class == field.getType())
      normal = field.getByte(obj);
    else if (char.class == field.getType())
      normal = field.getChar(obj);
    else if (short.class == field.getType())
      normal = field.getShort(obj);
    else if (int.class == field.getType())
      normal = field.getInt(obj);
    else if (long.class == field.getType()) {
      final long value = field.getLong(obj);
      normal = (int)(value ^ (value >>> 32));
    }
    else if (float.class == field.getType())
      normal = Float.floatToIntBits(field.getFloat(obj));
    else if (double.class == field.getType()) {
      final long bits = Double.doubleToLongBits(field.getDouble(obj));
      normal = (int)(bits ^ (bits >>> 32));
    }
    else {
      member = field.get(obj);
      normal = member != null ? member.hashCode() : 0;
    }

    return normal;
  }

  public static int hashCode(final Object obj, final String ... fieldName) {
    if (fieldName == null)
      throw new NullPointerException("fieldName == null");

    if (obj == null || fieldName.length == 0)
      return 0;

    int hashCode = 0;
    final Class<?> cls = obj.getClass();
    Field field;
    try {
      for (final String name : fieldName) {
        field = Classes.getDeclaredField(cls, name);
        if (field == null)
          throw new IllegalArgumentException("Field name \"" + name + "\" not found in calss " + cls.getName());

        hashCode = 31 * hashCode + hashCode(obj, field);
      }
    }
    catch (final IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    }

    return hashCode;
  }

  public static int hashCode(final Object obj) {
    if (obj == null)
      return 0;

    try {
      int hashCode = 0;
      final Class<?> cls = obj.getClass();
      Field[] fields = blackWhiteListMap.get(cls);
      if (fields == null) {
        final Field[] allFields = Classes.getDeclaredFieldsDeep(cls);
        fields = For.<Field>recursiveOrdered(allFields, Field.class, whitelistFilter);
        if (fields.length == 0)
          fields = For.<Field>recursiveOrdered(allFields, Field.class, nonBlacklistFilter);

        blackWhiteListMap.put(cls, fields);
      }

      for (int i = 0; i < fields.length; i++)
        hashCode = 31 * hashCode + hashCode(obj, fields[i]);

      return hashCode;
    }
    catch (final IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static String toString(final Object obj) {
    return obj != null ? toString(obj, 1, new IdentityHashMap<Object,Object>()) : null;
  }

  static int i = 0;

  private static String toString(final Object obj, final int depth, final IdentityHashMap<Object,Object> visited) {
    if (obj == null)
      throw new NullPointerException("obj == null");

    final Field[] fields = Classes.getDeclaredFieldsDeep(obj.getClass());
    final char[] pad = Arrays.createRepeat(' ', depth * 2);
    final char[] pad2 = Arrays.createRepeat(' ', (depth + 1) * 2);
    final StringBuilder builder = new StringBuilder(obj.getClass().getName()).append("@").append(Integer.toHexString(System.identityHashCode(obj))).append(" {\n");
    try {
      for (final Field field : fields) {
        if (field.isSynthetic() || field.getType() == Field.class)
          continue;

        builder.append(pad).append(field.getName()).append(": ");
        field.setAccessible(true);
        if (boolean.class == field.getType())
          builder.append(Boolean.toString(field.getBoolean(obj)));
        else if (byte.class == field.getType())
          builder.append("0x").append(Integer.toHexString(field.getByte(obj)));
        else if (char.class == field.getType())
          builder.append("'").append(String.valueOf(field.getChar(obj))).append("'");
        else if (short.class == field.getType())
          builder.append("(short)").append(String.valueOf(field.getShort(obj)));
        else if (int.class == field.getType())
          builder.append(String.valueOf(field.getInt(obj)));
        else if (long.class == field.getType())
          builder.append(String.valueOf(field.getLong(obj))).append("l");
        else if (float.class == field.getType())
          builder.append(String.valueOf(field.getFloat(obj))).append("f");
        else if (double.class == field.getType())
          builder.append(String.valueOf(field.getDouble(obj))).append("d");
        else {
          final Object object = field.get(obj);
          if (object == obj)
            return "";

          if (object == null)
            builder.append("null");
          else if (String.class == object.getClass())
            builder.append("\"").append(object).append("\"");
          else if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            builder.append("[\n");
            if (length > 0) {
              final StringBuilder arrayString = new StringBuilder();
              for (int i = 0; i < length; i++) {
                final Object member = Array.get(object, i);
                if (!visited.containsKey(member)) {
                  visited.put(member, null);
                  arrayString.append(pad2).append(", ").append(member != null ? toString(member, depth + 1, visited) : "null");
                }
              }

              if (arrayString.length() > 0)
                builder.append(arrayString.substring(2));

              builder.append("\n");
            }

            builder.append(pad).append("]");
          }
          else if (Enum.class.isInstance(object))
            builder.append(field.getType().getSimpleName()).append(".").append(object);
          else if (Class.class == object.getClass())
            builder.append(((Class<?>)object).getName());
          else if (File.class == object.getClass())
            builder.append(((File)object).getAbsolutePath());
          else if (!visited.containsKey(object)) {
            visited.put(object, null);
            builder.append(toString(object, depth + 1, visited));
          }
        }

        builder.append(",\n");
      }

      builder.setLength(builder.length() - 2);
      return builder.append("\n").append(Arrays.createRepeat(' ', (depth - 1) * 2)).append("}").toString();
    }
    catch (final IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  private Objects() {
  }
}