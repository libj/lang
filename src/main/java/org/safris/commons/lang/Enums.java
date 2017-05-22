/* Copyright (c) 2016 lib4j
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

package org.safris.commons.lang;

import java.lang.reflect.Array;
import java.util.List;

public final class Enums {
  @SuppressWarnings("unchecked")
  private static <T extends Enum<T>>T[] recurseValueOf(final Class<T> enumClass, final int index, final int depth, final String ... names) {
    if (index == names.length)
      return (T[])Array.newInstance(enumClass, depth);

    T value;
    try {
      value = Enum.valueOf(enumClass, names[index]);
    }
    catch (final IllegalArgumentException e) {
      value = null;
    }

    final T[] enums = recurseValueOf(enumClass, index + 1, value != null ? depth + 1 : depth, names);
    if (value != null)
      enums[depth] = value;

    return enums;
  }

  public static <T extends Enum<T>>T[] valueOf(final Class<T> enumClass, final String ... names) {
    return recurseValueOf(enumClass, 0, 0, names);
  }

  public static <T extends Enum<T>>T[] valueOf(final Class<T> enumClass, final List<String> names) {
    return valueOf(enumClass, names.toArray(new String[names.size()]));
  }

  private Enums() {
  }
}