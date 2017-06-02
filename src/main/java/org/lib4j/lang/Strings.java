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

import java.text.ParseException;
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
      builder.append("\n").append(Strings.interpolateLine(tokenizer.nextToken(), properties, "{{", "}}"));

    return builder.length() == 0 ? "" : builder.substring(1);
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

  public static String toInstanceCase(String string) {
    if (string == null)
      return null;

    if (string.length() == 1)
      return string.toLowerCase();

    string = toCamelCase(string);
    int index = 0;
    final char[] chars = string.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      index = i;
      if (('0' <= chars[i] && chars[i] <= '9') || ('a' <= chars[i] && chars[i] <= 'z'))
        break;
    }

    if (index == 1)
      return string.substring(0, 1).toLowerCase() + string.substring(1, string.length());

    if (index == string.length() - 1)
      return string.toLowerCase();

    if (index > 1)
      return string.substring(0, index - 1).toLowerCase() + string.substring(index - 1, string.length());

    return string;
  }

  public static String toTitleCase(String string) {
    if (string == null)
      return null;

    string = toCamelCase(string);

    // make sure that the fully qualified names are not changed
    if (string.indexOf(".") != -1)
      return string;

    return string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
  }

  // NOTE: This array is sorted
  private static final char[] discardTokens = new char[] {'!', '"', '#', '%', '&', '\'', '(', ')', '*', ',', '-', '.', '.', '/', ':', ';', '<', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'};

  public static String toCamelCase(final String string) {
    if (string == null)
      return null;

    final StringBuilder builder = new StringBuilder();
    final char[] chars = string.toCharArray();
    boolean capNext = false;
    for (int i = 0; i < chars.length; i++) {
      final int index = java.util.Arrays.binarySearch(discardTokens, chars[i]);
      if (index >= 0) {
        capNext = true;
      }
      else if (capNext) {
        builder.append(Character.toUpperCase(chars[i]));
        capNext = false;
      }
      else {
        builder.append(chars[i]);
      }
    }

    return builder.toString();
  }

  public static String toClassCase(final String string) {
    return string == null ? null : string.length() != 0 ? Character.toUpperCase(string.charAt(0)) + toCamelCase(string).substring(1) : string;
  }

  // FIXME: This means that there can be name collisions!
  public static String toJavaCase(final String string) {
    return string.replace('-', '_').replace('.', '_').replace("#", "");
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

  private Strings() {
  }
}