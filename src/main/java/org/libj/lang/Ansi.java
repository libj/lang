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

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

/**
 * Class for utilities related to ANSI standards.
 */
public final class Ansi {
  /**
   * Enum of ANSI color codes.
   */
  public enum Color implements Function<String,String> {
    BLACK(0),
    BLUE(4),
    CYAN(6),
    DEFAULT(9),
    GREEN(2),
    MAGENTA(5),
    RED(1),
    WHITE(7),
    YELLOW(3);

    private final String lowerCase;
    private final String code;

    Color(final int code) {
      this.lowerCase = name().toLowerCase();
      this.code = String.valueOf(code);
    }

    /**
     * Applies this {@code Color} to the specified string.
     */
    @Override
    public String apply(final String t) {
      return Ansi.apply0(t, Intensity.DEFAULT, this);
    }

    private static Comparator<Object> comparator = new Comparator<Object>() {
      @Override
      public int compare(final Object o1, final Object o2) {
        final String n1 = o1 instanceof Color ? ((Color)o1).lowerCase : (String)o1;
        final String n2 = o2 instanceof Color ? ((Color)o2).lowerCase : (String)o2;
        return n1.compareTo(n2);
      }
    };

    public static Color fromString(final String str) {
      final Color[] values = values();
      final int index = Arrays.binarySearch(values, str.toLowerCase(), comparator);
      return index < 0 ? null : values[index];
    }
  }

  /**
   * Enum of ANSI intensity codes.
   */
  public enum Intensity implements Function<String,String> {
    BOLD(3, 1),
    DEFAULT(3, 0),
    FAINT(3, 2),
    INTENSE(9, 0),
    ITALIC(3, 3),
    UNDERLINE(3, 4);

    private final String lowerCase;
    private final String group;
    private final String strength;

    Intensity(final int group, final int strength) {
      this.lowerCase = name().toLowerCase();
      this.group = String.valueOf(group);
      this.strength = String.valueOf(strength);
    }

    /**
     * Applies this {@link Intensity} to the specified string.
     */
    @Override
    public String apply(final String t) {
      return Ansi.apply0(t, this, Color.DEFAULT);
    }

    private static Comparator<Object> comparator = new Comparator<Object>() {
      @Override
      public int compare(final Object o1, final Object o2) {
        final String n1 = o1 instanceof Intensity ? ((Intensity)o1).lowerCase : (String)o1;
        final String n2 = o2 instanceof Intensity ? ((Intensity)o2).lowerCase : (String)o2;
        return n1.compareTo(n2);
      }
    };

    public static Intensity fromString(final String str) {
      final Intensity[] values = values();
      final int index = Arrays.binarySearch(values, str.toLowerCase(), comparator);
      return index < 0 ? null : values[index];
    }
  }

  private static final String ENCODE_START = "\033[";
  private static final String ENCODE_END = "m";
  private static final String RESET = "0;3" + Color.DEFAULT.code;

  /**
   * Applies the specified {@code intensity} to the provided {@code str}.
   *
   * @param str The string to which the specified {@code intensity} is to be
   *          applied.
   * @param intensity The {@link Intensity}.
   * @return A new string with the specified {@code intensity} applied to the
   *         provided {@code str}.
   */
  public static String apply(final String str, final Intensity intensity) {
    return intensity == null || intensity == Intensity.DEFAULT ? str : apply0(str, intensity, Color.DEFAULT);
  }

  /**
   * Applies the specified {@code color} to the provided {@code str}.
   *
   * @param str The string to which the specified {@code color} is to be
   *          applied.
   * @param color The {@link Color}.
   * @return A new string with the specified {@code color} applied to the
   *         provided {@code str}.
   */
  public static String apply(final String str, final Color color) {
    return color == null || color == Color.DEFAULT ? str : apply0(str, Intensity.DEFAULT, color);
  }

  /**
   * Applies the specified {@code intensity} and {@code color} to the provided
   * {@code str}. If {@code intensity} or {@code color} is null, the default is
   * applied instead.
   *
   * @param str The string to which the specified {@code intensity} and
   *          {@code color} are to be applied.
   * @param intensity The {@link Intensity}.
   * @param color The {@link Color}.
   * @return A new string with the specified {@code intensity} and {@code color}
   *         applied to the provided {@code str}.
   */
  public static String apply(final String str, Intensity intensity, Color color) {
    if (intensity == null) {
      if (color == null)
        return str;

      intensity = Intensity.DEFAULT;
    }
    else if (color == null) {
      color = Color.DEFAULT;
    }

    return apply0(str, intensity, color);
  }

  private static String apply0(final String str, Intensity intensity, Color color) {
    final StringBuilder builder = new StringBuilder();
    builder.append(ENCODE_START);
    builder.append(intensity.strength).append(';').append(intensity.group).append(color.code);
    builder.append(ENCODE_END);
    builder.append(str);
    builder.append(ENCODE_START);
    builder.append(RESET);
    builder.append(ENCODE_END);
    return builder.toString();
  }

  private Ansi() {
  }
}