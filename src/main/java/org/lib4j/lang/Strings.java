/* Copyright (c) 2006 lib4j
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

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.text.BadLocationException;

public final class Strings {
  private static final char[] alpha = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
  private static final char[] alphaNumeric = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

  private static String getRandomString(final int length, final boolean alphanumeric) {
    if (length < 0)
      throw new IllegalArgumentException("length = " + length);

    if (length == 0)
      return "";

    final char[] chars;
    if (alphanumeric)
      chars = alphaNumeric;
    else
      chars = alpha;

    final char[] array = new char[length];
    for (int i = 0; i < length; i++)
      array[i] = chars[(int)(Math.random() * chars.length)];

    return new String(array);
  }

  public static String getRandomAlphaNumericString(final int length) {
    return getRandomString(length, true);
  }

  public static String getRandomAlphaString(final int length) {
    return getRandomString(length, false);
  }

  private static String interpolateLine(final String line, final Map<String,String> properties, final int index, final String open, final String close) throws BadLocationException, ParseException {
    if (line == null)
      return null;

    int start = line.indexOf(open, index);
    if (start < 0)
      return line;

    int end = line.indexOf(close, start + open.length());
    if (end < 0)
      throw new ParseException(line, start);

    final String key = line.substring(start + open.length(), end);
    final String value = properties.get(key);
    if (value == null)
      throw new BadLocationException(key, start);

    final String interpolated = interpolateLine(line, properties, end + close.length(), open, close);
    return interpolated == null ? null : interpolated.substring(0, start) + value + interpolated.substring(end + close.length());
  }

  private static String interpolateLine(String line, final Map<String,String> properties, final String open, final String close) throws BadLocationException, ParseException {
    final int max = properties.size() * properties.size();
    int i = 0;
    while (true) {
      final String interpolated = interpolateLine(line, properties, 0, open, close);
      if (line != null ? line.equals(interpolated) : interpolated == null)
        return line;

      if (++i == max) {
        if (!line.equals(interpolated))
          throw new IllegalArgumentException("Loop detected.");

        return interpolated;
      }

      line = interpolated;
    }
  }

  public static Map<String,String> interpolate(final Map<String,String> properties, final String open, final String close) throws BadLocationException, ParseException {
    for (final Map.Entry<String,String> entry : properties.entrySet())
      entry.setValue(Strings.interpolateLine(entry.getValue(), properties, open, close));

    return properties;
  }

  public static String interpolate(final String text, final Map<String,String> properties, final String open, final String close) throws BadLocationException, ParseException {
    final StringTokenizer tokenizer = new StringTokenizer(text, "\r\n");
    final StringBuilder builder = new StringBuilder();
    while (tokenizer.hasMoreTokens())
      builder.append('\n').append(Strings.interpolateLine(tokenizer.nextToken(), properties, open, close));

    return builder.length() == 0 ? "" : builder.substring(1);
  }

  public static StringBuilder toLowerCase(final StringBuilder builder) {
    for (int i = 0; i < builder.length(); i++)
      builder.setCharAt(i, Character.toLowerCase(builder.charAt(i)));

    return builder;
  }

  public static StringBuilder toUppereCase(final StringBuilder builder) {
    for (int i = 0; i < builder.length(); i++)
      builder.setCharAt(i, Character.toUpperCase(builder.charAt(i)));

    return builder;
  }

  private static String changeCase(final String string, final boolean upper, final int beginIndex, final int endIndex) {
    if (string == null || string.length() == 0)
      return string;

    if (beginIndex > endIndex)
      throw new IllegalArgumentException("start {" + beginIndex + "} > end {" + endIndex + "}");

    if (string.length() < beginIndex)
      throw new StringIndexOutOfBoundsException("start index {" + beginIndex + "} > string length {" + string.length() + "}");

    if (endIndex < 0)
      throw new StringIndexOutOfBoundsException("end index {" + endIndex + "} < 0");

    if (beginIndex == endIndex)
      return string;

    if (beginIndex == 0) {
      final String caseString = string.substring(beginIndex, endIndex).toLowerCase();
      final String endString = string.substring(endIndex);
      return upper ? caseString.toUpperCase() + endString : caseString.toLowerCase() + endString;
    }

    if (endIndex == string.length()) {
      final String beginString = string.substring(0, beginIndex);
      final String caseString = string.substring(beginIndex, endIndex).toLowerCase();
      return upper ? beginString + caseString.toUpperCase() : beginString + caseString.toLowerCase();
    }

    final String beginString = string.substring(0, beginIndex);
    final String caseString = string.substring(beginIndex, endIndex).toLowerCase();
    final String endString = string.substring(endIndex);
    return upper ? beginString + caseString.toUpperCase() + endString : beginString + caseString.toLowerCase() + endString;
  }

  public static String toLowerCase(final String string, final int beginIndex, final int endIndex) {
    return changeCase(string, false, beginIndex, endIndex);
  }

  public static String toLowerCase(final String string, final int beginIndex) {
    return changeCase(string, false, beginIndex, string.length());
  }

  public static String toUpperCase(final String string, final int beginIndex, final int endIndex) {
    return changeCase(string, true, beginIndex, endIndex);
  }

  public static String toUpperCase(final String string, final int beginIndex) {
    return changeCase(string, true, beginIndex, string.length());
  }

  public static String getRandomString(final int length) {
    return getRandomString(length, false);
  }

  public static String padFixed(final String string, final int length, final boolean right) {
    if (length - string.length() < 0)
      return string;

    final char[] chars = new char[length - string.length()];
    java.util.Arrays.fill(chars, ' ');
    return right ? string + String.valueOf(chars) : String.valueOf(chars) + string;
  }

  private static String hex(long i, final int places) {
    if (i == Long.MIN_VALUE)
      return "-8000000000000000";

    boolean negative = i < 0;
    if (negative)
      i = -i;

    String result = Long.toString(i, 16).toUpperCase();
    if (result.length() < places)
      result = "0000000000000000".substring(result.length(), places) + result;

    return negative ? '-' + result : result;
  }

  public static String toUTF8Literal(final char ch) {
    return "\\x" + hex(ch, 2);
  }

  public static String toUTF8Literal(final String string) {
    final StringBuilder buffer = new StringBuilder(string.length() * 4);
    for (int i = 0; i < string.length(); i++) {
      char ch = string.charAt(i);
      buffer.append(toUTF8Literal(ch));
    }

    return buffer.toString();
  }

  public static String getAlpha(final int number) {
    int scale;
    return number < '{' - 'a' ? String.valueOf((char)('a' + number)) : getAlpha((scale = number / ('{' - 'a')) - 1) + String.valueOf((char)('a' + number - scale * ('{' - 'a')));
  }

  @SafeVarargs
  public static String getCommonPrefix(final String ... strings) {
    if (strings == null || strings.length == 0)
      return null;

    if (strings.length == 1)
      return strings[0];

    for (int c = 0; c < strings[0].length(); c++)
      for (int i = 1; i < strings.length; i++)
        if (c == strings[i].length() || strings[0].charAt(c) != strings[i].charAt(c))
          return strings[0].substring(0, c);

    return strings[0];
  }

  public static String getCommonPrefix(final Collection<String> strings) {
    if (strings == null || strings.size() == 0)
      return null;

    Iterator<String> iterator = strings.iterator();
    if (strings.size() == 1)
      return iterator.next();

    final String string0 = iterator.next();
    for (int c = 0; c < string0.length(); c++) {
      if (c > 0) {
        iterator = strings.iterator();
        iterator.next();
      }

      while (iterator.hasNext()) {
        final String next = iterator.next();
        if (c == next.length() || string0.charAt(c) != next.charAt(c))
          return string0.substring(0, c);
      }
    }

    return string0;
  }

  public static String escapeForJava(final String string) {
    return string == null ? null : string.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  public static String printColumns(final String ... columns) {
    // Split input strings into columns and rows
    final String[][] strings = new String[columns.length][];
    int maxLines = 0;
    for (int i = 0; i < columns.length; i++) {
      strings[i] = columns[i] == null ? null : columns[i].split("\n");
      if (strings[i] != null && strings[i].length > maxLines)
        maxLines = strings[i].length;
    }

    // Store an array of column widths
    final int[] widths = new int[columns.length];
    // calculate column widths
    for (int i = 0; i < columns.length; i++) {
      int maxWidth = 0;
      if (strings[i] != null) {
        for (int j = 0; j < strings[i].length; j++)
          if (strings[i][j].length() > maxWidth)
            maxWidth = strings[i][j].length();
      }
      else if (maxWidth < 4) {
        maxWidth = 4;
      }

      widths[i] = maxWidth + 1;
    }

    // Print the lines
    final StringBuilder builder = new StringBuilder();
    for (int j = 0; j < maxLines; j++) {
      builder.append('\n');
      for (int i = 0; i < strings.length; i++) {
        final String line = strings[i] == null ? "null" : j < strings[i].length ? strings[i][j] : "";
        builder.append(String.format("%-" + widths[i] + "s", line));
      }
    }

    return builder.length() == 0 ? "null" : builder.substring(1);
  }

  /**
   * Returns a string consisting of a specific number of concatenated
   * repetitions of an input string. For example,
   * <code>Strings.repeat("ha", 3)</code> returns the string
   * <code>"hahaha"</code>.
   *
   * @param string Any non-null string
   * @param count A nonnegative number of times to repeat the string
   * @return A string containing <code>string</code> repeated
   *     <code>count</code> times; an empty string if <code>count == 0</code>;
   *     the <code>string</code> if <code>count == 1</code>
   * @throws NullPointerException If <code>string == null</code>
   * @throws IllegalArgumentException If <code>count &lt; 0 </code>
   * @throws ArrayIndexOutOfBoundsException If <code>string.length() * count &gt; Integer.MAX_VALUE</code>
   */
  public static String repeat(final String string, final int count) {
    if (string == null)
      throw new IllegalArgumentException("string == null");

    if (count < 0)
      throw new IllegalArgumentException("count < 0");

    if (count == 0 || string.length() == 0)
      return "";

    if (count == 1)
      return string;

    final int length = string.length();
    final long longSize = (long)length * (long)count;
    final int size = (int)longSize;
    if (size != longSize)
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);

    final char[] chars = new char[size];
    string.getChars(0, length, chars, 0);
    int n;
    for (n = length; n < size - n; n <<= 1)
      System.arraycopy(chars, 0, chars, n, n);

    System.arraycopy(chars, 0, chars, n, size - n);
    return new String(chars);
  }

  public static byte[] getBytes(final String value, final String enc) {
    try {
      return value.getBytes(enc);
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static String trim(final String string, char ch) {
    if (string == null)
      return null;

    int i = 0;
    for (; i < string.length() && string.charAt(i) == ch; i++);
    if (i == string.length())
      return "";

    int j = string.length() - 1;
    for (; j > i + 1 && string.charAt(j) == ch; --j);
    return i == 0 && j == string.length() - 1 ? string : string.substring(i, j + 1);
  }

  private Strings() {
  }
}