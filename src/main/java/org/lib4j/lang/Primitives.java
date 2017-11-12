/* Copyright (c) 2013 lib4j
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

public final class Primitives {
  public static Object cast(final Object primitive, final Class<?> cls) {
    if (primitive == null)
      return null;

    if (cls.isInstance(primitive))
      return primitive;

    if (!cls.isPrimitive())
      throw new IllegalArgumentException("cls is not of a primitive type");

    if (primitive instanceof Number) {
      if (Byte.class.isAssignableFrom(cls))
        return Byte.valueOf(((Number)primitive).byteValue());

      if (Short.class.isAssignableFrom(cls))
        return Short.valueOf(((Number)primitive).shortValue());

      if (Integer.class.isAssignableFrom(cls))
        return Integer.valueOf(((Number)primitive).intValue());

      if (Long.class.isAssignableFrom(cls))
        return Long.valueOf(((Number)primitive).longValue());

      if (Float.class.isAssignableFrom(cls))
        return Float.valueOf(((Number)primitive).floatValue());

      if (Double.class.isAssignableFrom(cls))
        return Double.valueOf(((Number)primitive).doubleValue());

      if (Boolean.class.isAssignableFrom(cls))
        return Boolean.valueOf(((Number)primitive).intValue() != 0);

      if (Character.class.isAssignableFrom(cls))
        return Character.valueOf((char)((Number)primitive).intValue());
    }
    else if (primitive instanceof Boolean) {
      if (Byte.class.isAssignableFrom(cls))
        return Byte.valueOf((byte)((Boolean)primitive ? 1 : 0));

      if (Short.class.isAssignableFrom(cls))
        return Short.valueOf((short)((Boolean)primitive ? 1 : 0));

      if (Integer.class.isAssignableFrom(cls))
        return Integer.valueOf((Boolean)primitive ? 1 : 0);

      if (Long.class.isAssignableFrom(cls))
        return Long.valueOf((Boolean)primitive ? 1 : 0);

      if (Float.class.isAssignableFrom(cls))
        return Float.valueOf((Boolean)primitive ? 1 : 0);

      if (Double.class.isAssignableFrom(cls))
        return Double.valueOf((Boolean)primitive ? 1 : 0);

      if (Character.class.isAssignableFrom(cls))
        return Character.valueOf((char)((Boolean)primitive ? 1 : 0));
    }
    else if (primitive instanceof Character) {
      if (Byte.class.isAssignableFrom(cls))
        return Byte.valueOf((byte)((Character)primitive).charValue());

      if (Short.class.isAssignableFrom(cls))
        return Short.valueOf((short)((Character)primitive).charValue());

      if (Integer.class.isAssignableFrom(cls))
        return Integer.valueOf(((Character)primitive).charValue());

      if (Long.class.isAssignableFrom(cls))
        return Long.valueOf(((Character)primitive).charValue());

      if (Float.class.isAssignableFrom(cls))
        return Float.valueOf(((Character)primitive).charValue());

      if (Double.class.isAssignableFrom(cls))
        return Double.valueOf(((Character)primitive).charValue());

      if (Boolean.class.isAssignableFrom(cls))
        return Boolean.valueOf(((Character)primitive).charValue() != 0);
    }

    throw new IllegalArgumentException("Unknown cast from " + primitive.getClass().getName() + " to " + cls.getName());
  }

  private Primitives() {
  }
}