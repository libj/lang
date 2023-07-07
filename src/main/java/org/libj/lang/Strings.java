/* Copyright (c) 2006 LibJ
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

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions that provide common operations pertaining to {@link String} and {@link StringBuilder}.
 */
public final class Strings {
  public static final String[] EMPTY_ARRAY = new String[0];
  private static final char[] alphaNumeric = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
  private static final SecureRandom secureRandom = new SecureRandom();

  private static String getRandom(final SecureRandom secureRandom, final int length, final int start, final int len) {
    if (length == 0)
      return "";

    assertNotNegative(length, () -> "Length must be non-negative: " + length);

    final char[] array = new char[length];
    for (int i = 0; i < length; ++i) // [N]
      array[i] = alphaNumeric[start + secureRandom.nextInt(len)];

    return new String(array);
  }

  /**
   * Returns a randomly constructed alphanumeric string of the specified length.
   *
   * @param secureRandom The {@link SecureRandom} instance for generation of random values.
   * @param len The length of the string to construct.
   * @return A randomly constructed alphanumeric string of the specified length.
   * @throws NullPointerException If {@code secureRandom} is null.
   * @throws IllegalArgumentException If {@code len} is negative.
   */
  public static String getRandomAlphaNumeric(final SecureRandom secureRandom, final int len) {
    return getRandom(secureRandom, len, 0, alphaNumeric.length);
  }

  /**
   * Returns a randomly constructed alphanumeric string of the specified length.
   * <p>
   * This method uses a static {@link SecureRandom} instance for generation of random values.
   *
   * @param len The length of the string to construct.
   * @return A randomly constructed alphanumeric string of the specified length.
   * @throws IllegalArgumentException If {@code len} is negative.
   */
  public static String getRandomAlphaNumeric(final int len) {
    return getRandom(secureRandom, len, 0, alphaNumeric.length);
  }

  /**
   * Returns a randomly constructed alpha string of the specified length.
   *
   * @param secureRandom The {@link SecureRandom} instance for generation of random values.
   * @param len The length of the string to construct.
   * @return A randomly constructed alpha string of the specified length.
   * @throws NullPointerException If {@code secureRandom} is null.
   * @throws IllegalArgumentException If {@code len} is negative.
   */
  public static String getRandomAlpha(final SecureRandom secureRandom, final int len) {
    return getRandom(secureRandom, len, 0, alphaNumeric.length - 10);
  }

  /**
   * Returns a randomly constructed alpha string of the specified length.
   * <p>
   * This method uses a static {@link SecureRandom} instance for generation of random values.
   *
   * @param len The length of the string to construct.
   * @return A randomly constructed alpha string of the specified length.
   * @throws IllegalArgumentException If {@code len} is negative.
   */
  public static String getRandomAlpha(final int len) {
    return getRandom(secureRandom, len, 0, alphaNumeric.length - 10);
  }

  /**
   * Returns a randomly constructed numeric string of the specified length.
   *
   * @param secureRandom The {@link SecureRandom} instance for generation of random values.
   * @param len The length of the string to construct.
   * @return A randomly constructed numeric string of the specified length.
   * @throws NullPointerException If {@code secureRandom} is null.
   * @throws IllegalArgumentException If {@code len} is negative.
   */
  public static String getRandomNumeric(final SecureRandom secureRandom, final int len) {
    return getRandom(secureRandom, len, alphaNumeric.length - 10, 10);
  }

  /**
   * Returns a randomly constructed numeric string of the specified length.
   * <p>
   * This method uses a static {@link SecureRandom} instance for generation of random values.
   *
   * @param len The length of the string to construct.
   * @return A randomly constructed numeric string of the specified length.
   * @throws IllegalArgumentException If {@code len} is negative.
   */
  public static String getRandomNumeric(final int len) {
    return getRandom(secureRandom, len, alphaNumeric.length - 10, 10);
  }

  private static boolean interpolateShallow(final StringBuilder text, final Map<String,String> properties, final String open, final String close) {
    boolean changed = false;
    final int openLen = open.length();
    final int closeLen = close.length();
    for (int start = text.length() - closeLen - 1; (start = text.lastIndexOf(open, start - 1)) > -1;) { // [N]
      final int end = text.indexOf(close, start + openLen);
      if (end < start)
        continue;

      final String key = text.substring(start + openLen, end);
      final String value = properties.get(key);
      if (value != null) {
        text.replace(start, end + closeLen, value);
        changed = true;
      }
    }

    return changed;
  }

  private static String interpolateDeep(final StringBuilder text, final Map<String,String> properties, final String prefix, final String suffix) {
    final int max = properties.size() * properties.size();
    for (int i = 0; interpolateShallow(text, properties, prefix, suffix); ++i) // [N]
      if (i == max)
        throw new IllegalArgumentException("Loop detected");

    return text.toString();
  }

  /**
   * Interpolates all the <i>value</i> strings in the specified {@link Map} by matching {@code prefix + value + suffix}, where
   * <i>value</i> is a <i>key</i> in the {@link Map}, and replacing it with the value from the {@link Map}.
   * <p>
   * This performance of this algorithm is {@code O(n^2)} by nature. If the specified {@link Map} has {@code key=value} entries that
   * result in a loop, this method will throw a {@link IllegalArgumentException}.
   * <p>
   * <blockquote>
   * <b>Example:</b>
   * <p>
   * <table>
   * <caption>Input, with prefix=<code>"${"</code>, and suffix=<code>"}"</code></caption>
   * <tr><td><b>Key</b></td><td><b>Value</b></td></tr>
   * <tr><td>title</td><td>The ${subject} jumps over the ${object}</td></tr>
   * <tr><td>subject</td><td>${adj1} fox</td></tr>
   * <tr><td>object</td><td>${adj2} dog</td></tr>
   * <tr><td>adj1</td><td>quick brown</td></tr>
   * <tr><td>adj2</td><td>lazy</td></tr>
   * </table>
   * <p>
   * <table>
   * <caption>Output</caption>
   * <tr><td><b>Key</b></td><td><b>Value</b></td></tr>
   * <tr><td>title</td><td>The quick brown fox jumps over the lazy dog</td></tr>
   * <tr><td>subject</td><td>quick brown fox</td></tr>
   * <tr><td>object</td><td>lazy dog</td></tr>
   * <tr><td>adj1</td><td>quick brown</td></tr>
   * <tr><td>adj2</td><td>lazy</td></tr>
   * </table>
   * </blockquote>
   *
   * @param properties The map to interpolate.
   * @param prefix String prefixing the key name.
   * @param suffix String suffixing the key name.
   * @return The specified map, with its values interpolated.
   * @see #interpolate(String,Map,String,String)
   * @throws NullPointerException If {@code properties}, {@code prefix}, or {@code suffix} is null.
   * @throws IllegalArgumentException If the specified {@code properties} has {@code key=value} entries that result in a loop.
   */
  public static Map<String,String> interpolate(final Map<String,String> properties, final String prefix, final String suffix) {
    StringBuilder b = null;
    if (properties.size() > 0) {
      for (final Map.Entry<String,String> entry : properties.entrySet()) { // [S]
        final String value = entry.getValue();
        if (value != null) {
          if (b == null)
            b = new StringBuilder(value.length());
          else
            b.setLength(0);

          b.append(value);
          entry.setValue(interpolateDeep(b, properties, prefix, suffix));
        }
      }
    }

    return properties;
  }

  /**
   * Interpolates the specified string by matching {@code prefix + key + suffix} substring and replacing it with the <i>value</i> of
   * the {@code key=value} mapping in the properties {@link Map}.
   * <p>
   * <blockquote>
   * <b>Example:</b>
   * <p>
   * <b>Input</b>: text=<code>The ${subject} jumps over the ${object}</code>,
   * prefix=<code>"${"</code>, suffix=<code>"}"</code>
   * <p>
   * <table>
   * <caption>Properties</caption>
   * <tr><td><b>Key</b></td><td><b>Value</b></td></tr>
   * <tr><td>subject</td><td>quick brown fox</td></tr>
   * <tr><td>object</td><td>lazy dog</td></tr>
   * </table>
   * <p>
   * <b>Output</b>: {@code The quick brown fox jumps over the lazy dog}
   * </blockquote>
   *
   * @param text The string to interpolate.
   * @param properties The map with key=value entries for interpolation.
   * @param prefix String prefixing the key name.
   * @param suffix String suffixing the key name.
   * @return The interpolated string.
   * @see #interpolate(Map,String,String)
   * @throws NullPointerException If {@code text}, {@code properties}, {@code prefix}, or {@code suffix} is null.
   */
  public static String interpolate(final String text, final Map<String,String> properties, final String prefix, final String suffix) {
    return interpolateDeep(new StringBuilder(text), properties, prefix, suffix);
  }

  /**
   * Replaces each {@code target} char in the specified {@link StringBuilder} with the {@code replacement} char.
   *
   * @param builder The {@link StringBuilder}.
   * @param target The char to be replaced.
   * @param replacement The replacement char.
   * @return Whether the specified {@link StringBuilder} was changed as a result of this operation.
   * @throws NullPointerException If {@code builder} is null.
   * @see String#replace(char,char)
   */
  public static boolean replace(final StringBuilder builder, final char target, final char replacement) {
    boolean changed = false;
    for (int i = 0; (i = Strings.indexOf(builder, target, i)) > -1;) { // [X]
      builder.setCharAt(i, replacement);
      changed = true;
    }

    return changed;
  }

  /**
   * Replaces each substring in the specified {@link StringBuilder} that matches the literal target sequence with the specified
   * literal replacement sequence. The replacement proceeds from the beginning of the string to the end, for example, replacing "aa"
   * with "b" in the string "aaa" will result in "ba" rather than "ab".
   *
   * @param builder The {@link StringBuilder}.
   * @param target The sequence of char values to be replaced.
   * @param replacement The replacement sequence of char values.
   * @return Whether the specified {@link StringBuilder} was changed as a result of this operation.
   * @throws NullPointerException If {@code builder}, {@code target}, or {@code replacement} is null.
   * @see String#replace(CharSequence,CharSequence)
   * @throws OutOfMemoryError If the specified parameters result in a {@link StringBuilder} that grows beyond length of
   *           {@link Integer#MAX_VALUE}.
   */
  public static boolean replace(final StringBuilder builder, final CharSequence target, final CharSequence replacement) {
    final String targetString = target.toString();
    final String replaceString = replacement.toString();
    int j = builder.lastIndexOf(targetString);
    if (j < 0)
      return false;

    final int targetLen = targetString.length();
    final int targetLen1 = Math.max(targetLen, 1);

    final int newLengthHint = builder.length() - targetLen + replaceString.length();
    if (newLengthHint < 0)
      throw new OutOfMemoryError();

    do
      builder.replace(j, j + targetLen1, replaceString);
    while ((j = builder.lastIndexOf(targetString, j - targetLen1)) > -1);
    return true;
  }

  /**
   * Replaces each substring of the specified {@link StringBuilder} that matches the given {@code target} sequence with the given
   * {@code replacement} sequence. If a replacement operation results in a {@link StringBuilder} with substrings that match the
   * given {@code target} sequence, each substring will be replaced as well.
   *
   * @param builder The {@link StringBuilder} in which all substrings are to be replaced.
   * @param target The sequence to be replaced.
   * @param replacement The sequence to be substituted for each match.
   * @return Whether the specified {@link StringBuilder} was changed as a result of this operation.
   * @throws OutOfMemoryError If the specified parameters result in a {@link StringBuilder} that grows beyond length of
   *           {@link Integer#MAX_VALUE}, or if the specified parameters result in a {@link StringBuilder} that grows perpetually.
   */
  public static boolean replaceAll(final StringBuilder builder, final CharSequence target, final CharSequence replacement) {
    int i = 0;
    for (; replace(builder, target, replacement); ++i); // [X]
    return i > 0;
  }

  /**
   * Tests if the specified {@link CharSequence} starts with the specified prefix.
   *
   * @param str The {@link CharSequence}.
   * @param prefix The prefix.
   * @return {@code true} if the {@code prefix} character sequence is a prefix of {@code str}; {@code false} otherwise. Note also
   *         that {@code true} will be returned if {@code prefix} is an empty string or is equal to {@code str}.
   * @throws NullPointerException If {@code str} or {@code prefix} is null.
   */
  public static boolean startsWith(final CharSequence str, final CharSequence prefix) {
    final int len = prefix.length();
    if (len == 0)
      return true;

    if (str.length() < len)
      return false;

    for (int i = 0; i < len; ++i) // [N]
      if (str.charAt(i) != prefix.charAt(i))
        return false;

    return true;
  }

  /**
   * Tests if the specified {@link CharSequence} starts with the specified prefix, ignoring case.
   *
   * @param str The {@link CharSequence}.
   * @param prefix The prefix.
   * @return {@code true} if the {@code prefix} character sequence is a prefix of {@code str}, ignoring case; {@code false}
   *         otherwise. Note also that {@code true} will be returned if {@code prefix} is an empty string or is equal to
   *         {@code str}.
   * @throws NullPointerException If {@code str} or {@code prefix} is null.
   */
  public static boolean startsWithIgnoreCase(final CharSequence str, final CharSequence prefix) {
    int len = prefix.length();
    if (len == 0)
      return true;

    if (str.length() < len)
      return false;

    for (int i = 0; i < len; ++i) { // [N]
      final char a = str.charAt(i);
      final char b = prefix.charAt(i);
      if (Character.toUpperCase(a) != Character.toUpperCase(b) && Character.toLowerCase(a) != Character.toLowerCase(b))
        return false;
    }

    return true;
  }

  /**
   * Tests if the specified {@link CharSequence} starts with the specified prefix.
   *
   * @param str The {@link CharSequence}.
   * @param prefix The prefix.
   * @return {@code true} if the {@code prefix} character sequence is a prefix of {@code str}; {@code false} otherwise.
   * @throws NullPointerException If {@code str} is null.
   */
  public static boolean startsWith(final CharSequence str, final char prefix) {
    return str.length() > 0 && str.charAt(0) == prefix;
  }

  /**
   * Tests if the specified {@link CharSequence} starts with the specified prefix, ignoring case.
   *
   * @param str The {@link CharSequence}.
   * @param prefix The prefix.
   * @return {@code true} if the {@code prefix} character sequence is a prefix of {@code str}, ignoring case.; {@code false}
   *         otherwise.
   * @throws NullPointerException If {@code str} is null.
   */
  public static boolean startsWithIgnoreCase(final CharSequence str, final char prefix) {
    final char ch;
    return str.length() > 0 && (Character.toUpperCase(ch = str.charAt(0)) == Character.toUpperCase(prefix) || Character.toLowerCase(ch) == Character.toLowerCase(prefix));
  }

  /**
   * Tests if the specified {@link CharSequence} ends with the specified suffix.
   *
   * @param str The {@link CharSequence}.
   * @param suffix The suffix.
   * @return {@code true} if the {@code suffix} character sequence is a suffix of {@code str}; {@code false} otherwise. Note also
   *         that {@code true} will be returned if {@code suffix} is an empty string or is equal to {@code str}.
   * @throws NullPointerException If {@code str} or {@code suffix} is null.
   */
  public static boolean endsWith(final CharSequence str, final CharSequence suffix) {
    final int len = suffix.length();
    if (len == 0)
      return true;

    if (str.length() < len)
      return false;

    final int offset = str.length() - len;
    for (int i = len - 1; i >= 0; --i) // [N]
      if (str.charAt(offset + i) != suffix.charAt(i))
        return false;

    return true;
  }

  /**
   * Tests if the specified {@link CharSequence} ends with the specified suffix, ignoring case.
   *
   * @param str The {@link CharSequence}.
   * @param suffix The suffix.
   * @return {@code true} if the {@code suffix} character sequence is a suffix of {@code str}, ignoring case; {@code false}
   *         otherwise. Note also that {@code true} will be returned if {@code suffix} is an empty string or is equal to
   *         {@code str}.
   * @throws NullPointerException If {@code str} or {@code suffix} is null.
   */
  public static boolean endsWithIgnoreCase(final CharSequence str, final CharSequence suffix) {
    final int len = suffix.length();
    if (len == 0)
      return true;

    if (str.length() < len)
      return false;

    final int offset = str.length() - len;
    for (int i = len - 1; i >= 0; --i) { // [N]
      final char a = str.charAt(offset + i);
      final char b = suffix.charAt(i);
      if (Character.toUpperCase(a) != Character.toUpperCase(b) && Character.toLowerCase(a) != Character.toLowerCase(b))
        return false;
    }

    return true;
  }

  /**
   * Tests if the specified {@link CharSequence} ends with the specified suffix.
   *
   * @param str The {@link CharSequence}.
   * @param suffix The suffix.
   * @return {@code true} if the {@code suffix} character sequence is a suffix of {@code str}; {@code false} otherwise. Note also
   *         that {@code true} will be returned if {@code suffix} is an empty string or is equal to {@code str}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static boolean endsWith(final CharSequence str, final char suffix) {
    final int len = str.length();
    return len > 0 && str.charAt(len - 1) == suffix;
  }

  /**
   * Tests if the specified {@link CharSequence} ends with the specified suffix, ignoring case.
   *
   * @param str The {@link CharSequence}.
   * @param suffix The suffix.
   * @return {@code true} if the {@code suffix} character sequence is a prefix of {@code str}, ignoring case.; {@code false}
   *         otherwise.
   * @throws NullPointerException If {@code str} is null.
   */
  public static boolean endsWithIgnoreCase(final CharSequence str, final char suffix) {
    final int len = str.length();
    final char ch;
    return len > 0 && (Character.toUpperCase(ch = str.charAt(len - 1)) == Character.toUpperCase(suffix) || Character.toLowerCase(ch) == Character.toLowerCase(suffix));
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} to "proper case" using case mapping information from the
   * UnicodeData file. "Proper case" is defined as:
   * <p>
   * <blockquote><i>The capitalization of every first letter of every word.</i></blockquote>
   *
   * @param builder The {@link StringBuilder}.
   * @return The specified {@link StringBuilder}, with its characters converted to "proper case".
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toProperCase(final StringBuilder builder) {
    boolean nextUpper = true;
    for (int i = 0, i$ = builder.length(); i < i$; ++i) { // [N]
      final char ch = builder.charAt(i);
      if (Character.isWhitespace(ch)) {
        nextUpper = true;
      }
      else {
        builder.setCharAt(i, nextUpper ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
        nextUpper = false;
      }
    }

    return builder;
  }

  /**
   * Converts the characters in the specified {@link String} to "proper case" using case mapping information from the UnicodeData
   * file. "Proper case" is defined as:
   * <p>
   * <blockquote><i>The capitalization of every first letter of every word.</i></blockquote>
   *
   * @param str The {@link String}.
   * @return The specified {@link String}, with its characters converted to lowercase.
   * @throws NullPointerException If {@code str} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toProperCase(final String str) {
    return toProperCase(new StringBuilder(str));
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} to lowercase using case mapping information from the UnicodeData
   * file.
   *
   * @param builder The {@link StringBuilder}.
   * @return The specified {@link StringBuilder}, with its characters converted to lowercase.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toLowerCase(final StringBuilder builder) {
    for (int i = 0, i$ = builder.length(); i < i$; ++i) // [N]
      builder.setCharAt(i, Character.toLowerCase(builder.charAt(i)));

    return builder;
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} to uppercase using case mapping information from the UnicodeData
   * file.
   *
   * @param builder The {@link StringBuilder}.
   * @return The specified {@link StringBuilder}, with its characters converted to uppercase.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toUpperCase(char)
   */
  public static StringBuilder toUpperCase(final StringBuilder builder) {
    for (int i = 0, i$ = builder.length(); i < i$; ++i) // [N]
      builder.setCharAt(i, Character.toUpperCase(builder.charAt(i)));

    return builder;
  }

  private static StringBuilder changeCase(final StringBuilder builder, final boolean upper, final int beginIndex, final int endIndex) {
    final int length = builder.length();
    if (length == 0)
      return builder;

    if (beginIndex < 0)
      throw new IllegalArgumentException("start index (" + beginIndex + ") must be non-negative");

    if (endIndex < beginIndex)
      throw new IllegalArgumentException("start index (" + beginIndex + ") > end index (" + endIndex + ")");

    if (length < beginIndex)
      throw new IllegalArgumentException("start index (" + beginIndex + ") > string length (" + length + ")");

    if (beginIndex == endIndex)
      return builder;

    for (int i = beginIndex; i < endIndex; ++i) // [N]
      builder.setCharAt(i, upper ? Character.toUpperCase(builder.charAt(i)) : Character.toLowerCase(builder.charAt(i)));

    return builder;
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} spanning the provided index range to lowercase using case
   * mapping information from the UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @param beginIndex The beginning index, inclusive.
   * @param endIndex The ending index, exclusive.
   * @return The specified {@link StringBuilder}, with the characters spanning the index range converted to lowercase.
   * @throws IllegalArgumentException If the {@code beginIndex} is negative, or {@code endIndex} is larger than the length of the
   *              {@link StringBuilder}, or {@code beginIndex} is larger than {@code endIndex}.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toLowerCase(final StringBuilder builder, final int beginIndex, final int endIndex) {
    return changeCase(builder, false, beginIndex, endIndex);
  }

  /**
   * Converts all of the characters in the specified {@link StringBuilder} starting at the provided begin index to lowercase using
   * case mapping information from the UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @param beginIndex The beginning index, inclusive.
   * @return The specified {@link StringBuilder}, with all the characters following the provided begin index converted to lowercase.
   * @throws IllegalArgumentException If the {@code beginIndex} is negative or larger than the length of the
   *              {@link StringBuilder}.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toLowerCase(final StringBuilder builder, final int beginIndex) {
    return changeCase(builder, false, beginIndex, builder.length());
  }

  /**
   * Converts the characters in the specified {@link StringBuilder} spanning the provided index range to uppercase using case
   * mapping information from the UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @param beginIndex The beginning index, inclusive.
   * @param endIndex The ending index, exclusive.
   * @return The specified {@link StringBuilder}, with the characters spanning the index range converted to uppercase.
   * @throws IllegalArgumentException If the {@code beginIndex} is negative, or {@code endIndex} is larger than the length of the
   *              {@link StringBuilder}, or {@code beginIndex} is larger than {@code endIndex}.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toUpperCase(final StringBuilder builder, final int beginIndex, final int endIndex) {
    return changeCase(builder, true, beginIndex, endIndex);
  }

  /**
   * Converts all of the characters in the specified {@link StringBuilder} starting at the provided begin index to uppercase using
   * case mapping information from the UnicodeData file.
   *
   * @param builder The {@link StringBuilder}.
   * @param beginIndex The beginning index, inclusive.
   * @return The specified {@link StringBuilder}, with all the characters following the provided begin index converted to uppercase.
   * @throws IllegalArgumentException If the {@code beginIndex} is negative or larger than the length of the
   *              {@link StringBuilder}.
   * @throws NullPointerException If {@code builder} is null.
   * @see Character#toLowerCase(char)
   */
  public static StringBuilder toUpperCase(final StringBuilder builder, final int beginIndex) {
    return changeCase(builder, true, beginIndex, builder.length());
  }

  /**
   * Returns a padded representation of the specified length for the provided string. If {@code length > str.length()}, preceding
   * characters are filled with spaces ({@code ' '}). If {@code length == str.length()}, the provided string instance is returned.
   * If {@code length < str.length()}, this method throws {@link IllegalArgumentException}.
   * <p>
   * This method is equivalent to calling {@code pad(str, align, length, ' ')}.
   *
   * @param str The string to pad.
   * @param align Alignment to be used for string.
   * @param length The length of the returned, padded string.
   * @return A left-padded representation of the specified length for the provided string.
   * @throws IllegalArgumentException If {@code length} is less than {@code str.length()}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String pad(final String str, final Align align, final int length) {
    return pad(str, align, length, ' ', false);
  }

  /**
   * Returns a padded representation of the specified length for the provided string (with all newlines within the string padded as
   * well). If {@code length > str.length()}, preceding characters are filled with spaces ({@code ' '}). If
   * {@code length == str.length()}, the provided string instance is returned. If {@code length < str.length()}, this method throws
   * {@link IllegalArgumentException}.
   * <p>
   * This method is equivalent to calling {@code padLeft(str, length, ' ')}.
   *
   * @param str The string to pad.
   * @param align Alignment to be used for string.
   * @param length The length of the returned, padded string.
   * @return A left-padded representation of the specified length for the provided string.
   * @throws IllegalArgumentException If {@code length} is less than {@code str.length()}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String padAll(final String str, final Align align, final int length) {
    return padAll(str, align, length, ' ', false);
  }

  /**
   * Returns a padded representation of the specified length for the provided string. If {@code length > str.length()}, preceding
   * characters are filled with spaces ({@code ' '}). If {@code length == str.length()}, the provided string instance is returned.
   * If {@code length < str.length()}, this method throws {@link IllegalArgumentException}.
   * <p>
   * This method is equivalent to calling {@code padLeft(str, length, ' ')}.
   *
   * @param str The string to pad.
   * @param align Alignment to be used for string.
   * @param length The length of the returned, padded string.
   * @param truncate Whether the string should be truncated if it is longer than the provided {@code length}.
   * @return A left-padded representation of the specified length for the provided string.
   * @throws IllegalArgumentException If {@code truncate == false} and {@code length} is less than {@code str.length()}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String pad(final String str, final Align align, final int length, final boolean truncate) {
    return pad(str, align, length, ' ', truncate);
  }

  /**
   * Returns a padded representation of the specified length for the provided string (with all newlines within the string padded as
   * well). If {@code length > str.length()}, preceding characters are filled with spaces ({@code ' '}). If
   * {@code length == str.length()}, the provided string instance is returned. If {@code length < str.length()}, this method throws
   * {@link IllegalArgumentException}.
   * <p>
   * This method is equivalent to calling {@code padLeft(str, length, ' ')}.
   *
   * @param str The string to pad.
   * @param align Alignment to be used for string.
   * @param length The length of the returned, padded string.
   * @param truncate Whether the string should be truncated if it is longer than the provided {@code length}.
   * @return A left-padded representation of the specified length for the provided string.
   * @throws IllegalArgumentException If {@code truncate == false} and {@code length} is less than {@code str.length()}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String padAll(final String str, final Align align, final int length, final boolean truncate) {
    return padAll(str, align, length, ' ', truncate);
  }

  /**
   * Returns a padded representation of the specified length for the provided string. If {@code length > string.length()}, preceding
   * characters are filled with the specified {@code pad} char. If {@code length == string.length()}, the provided string instance
   * is returned. If {@code length < string.length()}, this method throws {@link IllegalArgumentException}.
   *
   * @param str The string to pad.
   * @param align Alignment to be used for string.
   * @param length The length of the returned, padded string.
   * @param pad The padding character.
   * @return A left-padded representation of the specified length for the provided string.
   * @throws IllegalArgumentException If {@code truncate == false} and {@code length} is less than {@code str.length()}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String pad(final String str, final Align align, final int length, final char pad) {
    return pad(str, align, length, pad, false);
  }

  /**
   * Returns a padded representation of the specified length for the provided string (with all newlines within the string padded as
   * well). If {@code length > string.length()}, preceding characters are filled with the specified {@code pad} char. If
   * {@code length == string.length()}, the provided string instance is returned. If {@code length < string.length()}, this method
   * throws {@link IllegalArgumentException}.
   *
   * @param str The string to pad.
   * @param align Alignment to be used for string.
   * @param length The length of the returned, padded string.
   * @param pad The padding character.
   * @return A left-padded representation of the specified length for the provided string.
   * @throws IllegalArgumentException If {@code length} is less than {@code str.length()}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String padAll(final String str, final Align align, final int length, final char pad) {
    return padAll(str, align, length, pad, false);
  }

  /**
   * Returns a padded representation of the specified length for the provided string. If {@code length > string.length()}, preceding
   * characters are filled with the specified {@code pad} char. If {@code length == string.length()}, the provided string instance
   * is returned. If {@code length < string.length()}, this method throws {@link IllegalArgumentException}.
   *
   * @param str The string to pad.
   * @param align Alignment to be used for string.
   * @param length The length of the returned, padded string.
   * @param pad The padding character.
   * @param truncate Whether the string should be truncated if it is longer than the provided {@code length}.
   * @return A left-padded representation of the specified length for the provided string.
   * @throws IllegalArgumentException If {@code truncate == false} and {@code length} is less than {@code str.length()}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String pad(final String str, final Align align, final int length, final char pad, final boolean truncate) {
    final int lenPrint = lengthPrintable(str);
    if (length == lenPrint)
      return str;

    if (length >= lenPrint) {
      final int len = str.length();
      final char[] chars = new char[length + len - lenPrint];
      align.pad(chars, str, len, pad);
      return new String(chars);
    }

    if (truncate)
      return align == Align.LEFT ? str.substring(0, indexPrintable(str, length)) : str.substring(indexPrintable(str, str.length() - length));

    throw new IllegalArgumentException("length (" + length + ") must be greater or equal to printable string length (" + lenPrint + ")");
  }

  /**
   * Returns a padded representation of the specified length for the provided string (with all newlines within the string padded as
   * well). If {@code length > string.length()}, preceding characters are filled with the specified {@code pad} char. If
   * {@code length == string.length()}, the provided string instance is returned. If {@code length < string.length()}, this method
   * throws {@link IllegalArgumentException}.
   *
   * @param str The string to pad.
   * @param align Alignment to be used for string.
   * @param length The length of the returned, padded string.
   * @param pad The padding character.
   * @param truncate Whether the string should be truncated if it is longer than the provided {@code length}.
   * @return A left-padded representation of the specified length for the provided string.
   * @throws IllegalArgumentException If {@code truncate == false} and {@code length} is less than {@code str.length()}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String padAll(final String str, final Align align, final int length, final char pad, final boolean truncate) {
    final StringBuilder b = new StringBuilder();
    final String[] lines = str.split("[\n\r]");
    for (int i = 0, i$ = lines.length; i < i$; ++i) { // [N]
      if (i > 0)
        b.append('\n');

      b.append(pad(lines[i], align, length, pad, truncate));
    }

    return b.toString();
  }

  public static int lengthPrintable(final CharSequence str) {
    return countPrintable(str, -1);
  }

  public static int indexPrintable(final CharSequence str, final int index) {
    return countPrintable(str, index);
  }

  private static int countPrintable(final CharSequence str, final int index) {
    final int i$ = str.length();
    int start = 0;
    char ch, last = '\0';
    boolean esc = false;
    int i = 0;
    for (; i < i$; ++i, last = ch) { // [N]
      ch = str.charAt(i);
      if (esc) {
        esc = ch != 'm';
        continue;
      }

      if ((esc = (ch == '[' && last == '\033')) || !Characters.isPrintable(ch))
        continue;

      if (start++ == index)
        break;
    }

    return index == -1 ? start : i;
  }

  public enum Align {
    LEFT {
      @Override
      void pad(final char[] chars, final CharSequence seq, final int len, final char pad) {
        Arrays.fill(chars, len, chars.length, pad);
        for (int i = 0; i < len; ++i) // [N]
          chars[i] = seq.charAt(i);
      }
    },
    CENTER {
      @Override
      void pad(final char[] chars, final CharSequence seq, final int len, final char pad) {
        final int offset = (chars.length - len) / 2;
        Arrays.fill(chars, 0, offset, pad);
        int i = 0;
        for (; i < len; ++i) // [N]
          chars[i + offset] = seq.charAt(i);

        i += offset;
        for (; i < chars.length; ++i) // [N]
          chars[i] = pad;
      }
    },
    RIGHT {
      @Override
      void pad(final char[] chars, final CharSequence seq, final int len, final char pad) {
        final int offset = chars.length - len;
        Arrays.fill(chars, 0, offset, pad);
        for (int i = 0; i < len; ++i) // [N]
          chars[i + offset] = seq.charAt(i);
      }
    };

    abstract void pad(char[] chars, CharSequence seq, int len, char pad);
  }

  /**
   * Returns the hexadecimal representation of the specified value up to the provided digits. If the number of digits is less than
   * the full length of the hexadecimal representation, the extra most significant digits are truncated. If the number of digits is
   * less than the full length of the hexadecimal representation, the resultant string is left-padded with zeros ({@code '0'}).
   *
   * @param value The value to convert to hexadecimal representation.
   * @param digits The number of digits to return, least significant digits first.
   * @return The hexadecimal representation of the specified value up to the provided digits
   */
  static String hex(long value, final int digits) {
    final boolean negative = value < 0;
    if (negative)
      value = -value;

    String hex = Long.toString(value & ((1L << 4 * digits) - 1), 16);
    if (hex.length() < digits)
      hex = pad(hex, Align.RIGHT, digits, '0');

    return negative ? "-" + hex : hex;
  }

  /**
   * Returns the UTF-8 literal hexadecimal encoding of the specified {@code char}.
   *
   * @param ch The {@code char} to encode.
   * @return The UTF-8 literal hexadecimal encoding of the specified {@code char}.
   */
  public static String toUTF8Literal(final char ch) {
    return "\\x" + hex(ch, 2);
  }

  /**
   * Returns the string of UTF-8 literal hexadecimal encodings of characters of the specified {@link CharSequence}.
   *
   * @param str The {@link CharSequence} to encode.
   * @return The string of UTF-8 literal hexadecimal encodings of characters of the specified {@link CharSequence}.
   */
  public static String toUTF8Literal(final CharSequence str) {
    final int length = str.length();
    final StringBuilder b = new StringBuilder(length * 4);
    for (int i = 0; i < length; ++i) // [N]
      b.append(toUTF8Literal(str.charAt(i)));

    return b.toString();
  }

  /**
   * Returns a base 26 representation of {@code n} in alphabetical digits. The {@code 0}th string is {@code "a"}, and the
   * {@code 25}th is {@code "z"}. For {@code n} between {@code 26} and {@code 51}, the resulting string is 2 characters in length,
   * and starts with {@code 'a'}. For {@code n} between {@code 52} and {@code 77}, the resulting string is 2 characters in length,
   * and starts with {@code 'b'}. In effect, this method
   *
   * @param n The decimal value to convert into a base 26 representation of {@code n} in alphabetical digits.
   * @return A base 26 representation of {@code n} in alphabetical digits.
   */
  public static String getAlpha(final int n) {
    final int scale;
    return n < '{' - 'a' ? String.valueOf((char)('a' + n)) : getAlpha((scale = n / ('{' - 'a')) - 1) + (char)('a' + n - scale * ('{' - 'a'));
  }

  /**
   * Returns the prefix string that is shared amongst all members for the specified {@link String} array.
   *
   * @param strings The {@link String} array in which to find a common prefix.
   * @return The prefix string that is shared amongst all members for the specified {@link String} array.
   * @throws NullPointerException If any member of {@code strings} is null.
   */
  public static String getCommonPrefix(final String ... strings) {
    final int len;
    if (strings == null || (len = strings.length) == 0)
      return null;

    if (len == 1)
      return strings[0];

    final String string0 = strings[0];
    for (int i = 0, i$ = string0.length(); i < i$; ++i) // [N]
      for (int j = 1, j$ = len; j < j$; ++j) // [N]
        if (i == strings[j].length() || string0.charAt(i) != strings[j].charAt(i))
          return string0.substring(0, i);

    return string0;
  }

  /**
   * Returns the prefix string that is shared amongst all members for the specified {@link Collection}.
   *
   * @param strings The {@link Collection} of strings in which to find a common prefix.
   * @return The prefix string that is shared amongst all members for the specified {@link Collection}.
   * @throws NullPointerException If {@code strings} or any member of {@code strings} is null.
   */
  public static String getCommonPrefix(final Collection<String> strings) {
    final int len;
    if (strings == null || (len = strings.size()) == 0)
      return null;

    Iterator<String> iterator = strings.iterator();
    if (len == 1)
      return iterator.next();

    final String string0 = iterator.next();
    for (int i = 0, i$ = string0.length(); i < i$; ++i) { // [N]
      if (i > 0) {
        iterator = strings.iterator();
        iterator.next();
      }

      while (iterator.hasNext()) {
        final String next = iterator.next();
        if (i == next.length() || string0.charAt(i) != next.charAt(i))
          return string0.substring(0, i);
      }
    }

    return string0;
  }

  /**
   * Returns a representation of the specified string that is able to be contained in a {@link String} literal in Java.
   *
   * @param str The string to transform.
   * @return A representation of the specified string that is able to be contained in a {@link String} literal in Java.
   */
  public static String escapeForJava(final String str) {
    return str == null ? null : str.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  /**
   * Returns a string consisting of a specific number of concatenated repetitions of an input character. For example,
   * {@code Strings.repeat('a', 3)} returns the string {@code "aaa"}.
   *
   * @param ch The {@code char} to repeat.
   * @param count A nonnegative number of times to repeat the specified {@code char}.
   * @return A string containing the specified {@code char} repeated {@code count} times; an empty string if {@code count == 0}.
   * @throws IllegalArgumentException If {@code count < 0}.
   * @throws ArrayIndexOutOfBoundsException If {@code str.length() * count > Integer.MAX_VALUE}.
   */
  public static String repeat(final char ch, final int count) {
    assertNotNegative(count, () -> "count (" + count + ") must be greater than or equal to 0");

    if (count == 0)
      return "";

    final char[] chars = new char[count];
    Arrays.fill(chars, ch);
    return new String(chars);
  }

  /**
   * Returns a string consisting of a specific number of concatenated repetitions of an input string. For example,
   * {@code Strings.repeat("ha", 3)} returns the string {@code "hahaha"}.
   *
   * @param str Any non-null string.
   * @param count A nonnegative number of times to repeat the specified string.
   * @return A string containing the specified {@code str} repeated {@code count} times; an empty string if {@code count == 0}; the
   *         {@code str} if {@code count == 1}.
   * @throws ArrayIndexOutOfBoundsException If {@code str.length() * count > Integer.MAX_VALUE}.
   * @throws IllegalArgumentException If {@code count < 0}.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String repeat(final String str, final int count) {
    assertNotNegative(count, () -> "count (" + count + ") must be greater than or equal to 0");

    final int length = str.length();
    if (count == 0 || length == 0)
      return "";

    if (count == 1)
      return str;

    final long longSize = (long)length * count;
    final int size = (int)longSize;
    if (size != longSize)
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + longSize);

    final char[] chars = new char[size];
    str.getChars(0, length, chars, 0);
    int n = length;
    for (; n < size - n; n <<= 1) // [N]
      System.arraycopy(chars, 0, chars, n, n);

    System.arraycopy(chars, 0, chars, n, size - n);
    return new String(chars);
  }

  /**
   * Encodes the specified {@link String} into a sequence of bytes using the named charset, storing the result into a new byte
   * array.
   * <p>
   * This method differentiates itself from {@link String#getBytes(String)} by throwing the unchecked {@link RuntimeException}
   * instead of the checked {@link UnsupportedEncodingException} if the named charset is not supported.
   *
   * @param str The string to encode.
   * @param charsetName The name of a supported {@linkplain java.nio.charset.Charset charset}.
   * @return The resultant byte array.
   * @throws RuntimeException If the named charset is not supported.
   * @throws NullPointerException If {@code str} or {@code charsetName} is null.
   * @see String#getBytes(String)
   */
  public static byte[] getBytes(final String str, final String charsetName) {
    try {
      return str.getBytes(charsetName);
    }
    catch (final UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the provided string with any leading and trailing characters matching the provided {@code char} removed.
   *
   * @param str The string to be trimmed.
   * @param ch The {@code char} to remove from the front and back of the provided string.
   * @return The provided string with any leading and trailing characters matching the provided {@code char} removed.
   */
  public static String trim(final String str, final char ch) {
    if (str == null)
      return null;

    int i = -1;
    final int length = str.length();
    while (++i < length && str.charAt(i) == ch);
    if (i == length)
      return "";

    int j = length;
    while (j > i && str.charAt(--j) == ch);
    return i == 0 && j == length - 1 ? str : str.substring(i, j + 1);
  }

  /**
   * Returns the provided string with the provided {@code start} and {@code end} characters removed from the start and end of the
   * string, respectfully. If the provided string does not start with {@code start} or end with {@code end}, the provided string is
   * returned unchanged.
   *
   * @param str The string to be trimmed.
   * @param start The {@code char} to remove as the starting character of the string.
   * @param end The {@code char} to remove as the ending character of the string.
   * @return The provided string with the provided {@code start} and {@code end} characters removed from the start and end of the
   *         string, respectfully. If the provided string does not start with {@code start} or end with {@code end}, the provided
   *         string is returned unchanged.
   */
  public static String trimStartEnd(final String str, final char start, final char end) {
    if (str == null)
      return null;

    final int length = str.length();
    if (length > 1 && str.charAt(0) == start && str.charAt(length - 1) == end)
      return str.substring(1, length - 1);

    return str;
  }

  /**
   * Returns the index within the provided {@link CharSequence} of the last occurrence of the specified character. The
   * {@code CharSequence} is searched backwards starting at the last character. If no such character occurs in this string, then
   * {@code -1} is returned.
   *
   * @param str The {@link CharSequence}.
   * @param ch A character (Unicode code point).
   * @return The index of the last occurrence of the character in the provided {@link CharSequence}, or {@code -1} if the character
   *         does not occur.
   */
  public static int lastIndexOf(final CharSequence str, final char ch) {
    return lastIndexOf0(str, ch, str.length() - 1);
  }

  /**
   * Returns the index within the provided {@link CharSequence} of the last occurrence of the specified character, searching
   * backward starting at the specified index. If no such character occurs in this string at or before position {@code fromIndex},
   * then {@code -1} is returned.
   * <p>
   * All indices are specified in {@code char} values (Unicode code units).
   *
   * @param str The {@link CharSequence}.
   * @param ch A character (Unicode code point).
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the provided {@link CharSequence}, it has the same effect as if it were equal
   *          to one less than the length of this string: this entire string may be searched. If it is negative, it has the same
   *          effect as if it were -1: -1 is returned.
   * @return The index of the last occurrence of the character in the character sequence represented by this object that is less
   *         than or equal to {@code fromIndex}, or {@code -1} if the character does not occur before that point.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int lastIndexOf(final CharSequence str, final char ch, final int fromIndex) {
    return lastIndexOf0(str, ch, fromIndex);
  }

  private static int lastIndexOf0(final CharSequence str, final char ch, final int fromIndex) {
    for (int i = Math.min(fromIndex, str.length() - 1); i >= 0; --i) // [N]
      if (str.charAt(i) == ch)
        return i;

    return -1;
  }

  /**
   * Returns the index within the provided {@link CharSequence} of the last occurrence of the specified substring. The
   * {@code CharSequence} is searched backwards starting at the last character. If no such substring occurs in this string, then
   * {@code -1} is returned.
   *
   * @param str The {@link CharSequence}.
   * @param substr A substring.
   * @return The index of the last occurrence of the substring in the provided {@link CharSequence}, or {@code -1} if the substring
   *         does not occur.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int lastIndexOf(final CharSequence str, final CharSequence substr) {
    return lastIndexOf0(str, substr, str.length() - 1);
  }

  /**
   * Returns the index within the provided {@link CharSequence} of the last occurrence of the specified substring, searching
   * backward starting at the specified index. If no such substring occurs in this string at or before position {@code fromIndex},
   * then {@code -1} is returned.
   * <p>
   * All indices are specified in {@code char} values (Unicode code units).
   *
   * @param str The {@link CharSequence}.
   * @param substr A substring.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the provided {@link CharSequence}, it has the same effect as if it were equal
   *          to one less than the length of this string: this entire string may be searched. If it is negative, it has the same
   *          effect as if it were -1: -1 is returned.
   * @return The index of the last occurrence of the substring in the {@link CharSequence} represented by this object that is less
   *         than or equal to {@code fromIndex}, or {@code -1} if the substring does not occur before that point.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int lastIndexOf(final CharSequence str, final CharSequence substr, final int fromIndex) {
    return lastIndexOf0(str, substr, fromIndex);
  }

  private static int lastIndexOf0(final CharSequence str, final CharSequence substr, final int fromIndex) {
    final int substrLen = substr.length();
    for (int i = Math.min(fromIndex, str.length() - 1); i >= 0; --i) // [N]
      if (regionMatches0(str, false, i, substr, 0, substrLen))
        return i;

    return -1;
  }

  /**
   * Returns the index within the specified {@link CharSequence} of the first occurrence of the provided character that is not
   * escaped, starting the search at the specified index.
   *
   * @param str The {@link CharSequence}.
   * @param ch The character to find.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified {@link CharSequence} of the first occurrence of the provided character that is not
   *         escaped, starting the search at the specified index, or {@code -1} if the unescaped character is not found.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfUnEscaped(final CharSequence str, final char ch, final int fromIndex) {
    boolean escaped = false;
    for (int i = Math.max(fromIndex, 0), i$ = str.length(); i < i$; ++i) { // [N]
      final char c = str.charAt(i);
      if (escaped) {
        if (c == '\\' && ch == '\\')
          return i;

        escaped = false;
      }
      else if (c == '\\')
        escaped = true;
      else if (c == ch)
        return i;
    }

    return -1;
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided character that is not escaped, starting
   * the search at the specified index.
   *
   * @param str The string.
   * @param ch The character to find.
   * @return The index within the specified string of the first occurrence of the provided character that is not escaped, starting
   *         the search at the specified index, or {@code -1} if the unescaped character is not found.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfUnEscaped(final CharSequence str, final char ch) {
    return indexOfUnEscaped(str, ch, 0);
  }

  /**
   * Returns the index within the specified {@link CharSequence} of the first occurrence of the provided substring that is not
   * escaped, starting the search at the specified index.
   *
   * @param str The {@link CharSequence}.
   * @param substr The substring to find.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified {@link CharSequence} of the first occurrence of the provided substring that is not
   *         escaped, starting the search at the specified index, or {@code -1} if the unescaped substring is not found.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOfUnEscaped(final CharSequence str, final CharSequence substr, final int fromIndex) {
    final int substrLen = substr.length();
    final boolean substrIsBackslash = substrLen == 1 && substr.charAt(0) == '\\';
    boolean escaped = false;
    for (int i = Math.max(fromIndex, 0), i$ = str.length(); i < i$; ++i) { // [N]
      final char c = str.charAt(i);
      if (escaped) {
        if (c == '\\' && substrIsBackslash)
          return i;

        escaped = false;
      }
      else if (c == '\\')
        escaped = true;
      else if (regionMatches0(str, false, i, substr, 0, substrLen))
        return i;
    }

    return -1;
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided substring that is not escaped, starting
   * the search at the specified index.
   *
   * @param str The string.
   * @param substr The substring to find.
   * @return The index within the specified string of the first occurrence of the provided substring that is not escaped, starting
   *         the search at the specified index, or {@code -1} if the unescaped substring is not found.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOfUnEscaped(final CharSequence str, final CharSequence substr) {
    return indexOfUnEscaped(str, substr, 0);
  }

  /**
   * Returns the index within the specified {@link CharSequence} of the last occurrence of the provided character that is not
   * escaped, searching backward starting at the specified index.
   *
   * @param str The {@link CharSequence}.
   * @param ch The character to find.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified {@link CharSequence} of the last occurrence of the provided character that is not
   *         escaped, searching backward starting at the specified index, or {@code -1} if the unescaped character is not found.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int lastIndexOfUnEscaped(final CharSequence str, final char ch, int fromIndex) {
    do {
      final int i = lastIndexOf(str, ch, fromIndex);
      int count = 0;
      for (int j = i - 1; j >= 0; --j) // [N]
        if (str.charAt(j) == '\\')
          ++count;
        else
          break;

      if (count % 2 == 0)
        return i;

      fromIndex = i - 1;
    }
    while (fromIndex > 0);
    return -1;
  }

  /**
   * Returns the index within the specified string of the last occurrence of the provided character that is not escaped.
   *
   * @param str The string.
   * @param ch The character to find.
   * @return The index within the specified string of the last occurrence of the provided character that is not escaped, or
   *         {@code -1} if the unescaped character is not found.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int lastIndexOfUnEscaped(final CharSequence str, final char ch) {
    return lastIndexOfUnEscaped(str, ch, str.length() - 1);
  }

  /**
   * Returns the index within the specified {@link CharSequence} of the last occurrence of the provided substring that is not
   * escaped, searching backward starting at the specified index.
   *
   * @param str The {@link CharSequence}.
   * @param substr The substring to find.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified {@link CharSequence} of the last occurrence of the provided substring that is not
   *         escaped, searching backward starting at the specified index, or {@code -1} if the unescaped substring is not found.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int lastIndexOfUnEscaped(final CharSequence str, final CharSequence substr, int fromIndex) {
    do {
      final int i = lastIndexOf(str, substr, fromIndex);
      int count = 0;
      for (int j = i - 1; j >= 0; --j) // [N]
        if (str.charAt(j) == '\\')
          ++count;
        else
          break;

      if (count % 2 == 0)
        return i;

      fromIndex = i - 1;
    }
    while (fromIndex > 0);
    return -1;
  }

  /**
   * Returns the index within the specified string of the last occurrence of the provided substring that is not escaped.
   *
   * @param str The string.
   * @param substr The substring to find.
   * @return The index within the specified string of the last occurrence of the provided substring that is not escaped, or
   *         {@code -1} if the unescaped character is not found.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int lastIndexOfUnEscaped(final CharSequence str, final CharSequence substr) {
    return lastIndexOfUnEscaped(str, substr, str.length() - 1);
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided character that is not within a quoted
   * section of the string, starting the search at the specified index. A quoted section of a string starts with a double-quote
   * character ({@code '"'}) and ends with a double-quote character or the end of the string.
   *
   * @param str The string.
   * @param ch The character to find.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified string of the first occurrence of the provided character that is not within a quoted
   *         section of the string, or {@code -1} if the character is not found in an unquoted section.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfUnQuoted(final CharSequence str, final char ch, final int fromIndex) {
    boolean escaped = false;
    boolean quoted = false;
    for (int i = Math.max(fromIndex, 0), i$ = str.length(); i < i$; ++i) { // [N]
      final char c = str.charAt(i);
      if (escaped)
        escaped = false;
      else if (c == '\\')
        escaped = true;
      else if (c == ch && !quoted)
        return i;
      else if (c == '"')
        quoted = !quoted;
    }

    return -1;
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided character that is not within a quoted
   * section of the string. A quoted section of a string starts with a double-quote character ({@code '"'}) and ends with a
   * double-quote character or the end of the string.
   *
   * @param str The string.
   * @param ch The character to find.
   * @return The index within the specified string of the first occurrence of the provided character that is not within a quoted
   *         section of the string, or {@code -1} if the character is not found in an unquoted section.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfUnQuoted(final CharSequence str, final char ch) {
    return indexOfUnQuoted(str, ch, 0);
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided substring that is not within a quoted
   * section of the string, starting the search at the specified index. A quoted section of a string starts with a double-quote
   * character ({@code '"'}) and ends with a double-quote character or the end of the string.
   *
   * @param str The string.
   * @param substr The substring to find.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified string of the first occurrence of the provided substring that is not within a quoted
   *         section of the string, or {@code -1} if the substring is not found in an unquoted section.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOfUnQuoted(final CharSequence str, final CharSequence substr, final int fromIndex) {
    final int substrLen = substr.length();
    boolean escaped = false;
    boolean quoted = false;
    for (int i = Math.max(fromIndex, 0), i$ = str.length(); i < i$; ++i) { // [N]
      final char c = str.charAt(i);
      if (escaped)
        escaped = false;
      else if (c == '\\')
        escaped = true;
      else if (!quoted && regionMatches0(str, false, i, substr, 0, substrLen))
        return i;
      else if (c == '"')
        quoted = !quoted;
    }

    return -1;
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided substring that is not within a quoted
   * section of the string. A quoted section of a string starts with a double-quote character ({@code '"'}) and ends with a
   * double-quote character or the end of the string.
   *
   * @param str The string.
   * @param substr The substring to find.
   * @return The index within the specified string of the first occurrence of the provided substring that is not within a quoted
   *         section of the string, or {@code -1} if the substring is not found in an unquoted section.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOfUnQuoted(final CharSequence str, final CharSequence substr) {
    return indexOfUnQuoted(str, substr, 0);
  }

  /**
   * Returns the index within the specified string of the last occurrence of the provided character that is not within a quoted
   * section of the string, searching backward starting at the specified index. A quoted section of a string ends with a
   * double-quote character ({@code '"'}) and starts with a double-quote character or the start of the string.
   *
   * @param str The string.
   * @param ch The character to find.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified string of the first occurrence of the provided character that is not within a quoted
   *         section of the string, or {@code -1} if the character is not found in an unquoted section.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int lastIndexOfUnQuoted(final CharSequence str, final char ch, final int fromIndex) {
    boolean esacped = false;
    boolean quoted = false;
    char n = '\0';
    for (int end = str.length() - 1, i = Math.min(fromIndex, end); i >= 0; --i) { // [N]
      final char c = str.charAt(i);
      if (c == '\\')
        esacped = true;
      else if (esacped)
        esacped = false;
      else if (n == ch && !quoted)
        return i + 1;
      else if (n == '"')
        quoted = !quoted;

      n = c;
    }

    return n == ch ? 0 : -1;
  }

  /**
   * Returns the index within the specified string of the last occurrence of the provided character that is not within a quoted
   * section of the string. A quoted section of a string ends with a double-quote character ({@code '"'}) and starts with a
   * double-quote character or the start of the string.
   *
   * @param str The string.
   * @param ch The character to find.
   * @return The index within the specified string of the first occurrence of the provided character that is not within a quoted
   *         section of the string, or {@code -1} if the character is not found in an unquoted section.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int lastIndexOfUnQuoted(final CharSequence str, final char ch) {
    return lastIndexOfUnQuoted(str, ch, str.length());
  }

  /**
   * Returns the index within the specified string of the last occurrence of the provided substring that is not within a quoted
   * section of the string, searching backward starting at the specified index. A quoted section of a string ends with a
   * double-quote character ({@code '"'}) and starts with a double-quote character or the start of the string.
   *
   * @param str The string.
   * @param substr The substring to find.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified string of the first occurrence of the provided substring that is not within a quoted
   *         section of the string, or {@code -1} if the substring is not found in an unquoted section.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int lastIndexOfUnQuoted(final CharSequence str, final CharSequence substr, final int fromIndex) {
    final int substrLen = substr.length();
    boolean esacped = false;
    boolean quoted = false;
    char n = '\0';
    for (int end = str.length() - 1, i = Math.min(fromIndex, end); i >= 0; --i) { // [N]
      final char c = str.charAt(i);
      if (c == '\\')
        esacped = true;
      else if (esacped)
        esacped = false;
      else if (!quoted && regionMatches0(str, false, i, substr, 0, substrLen))
        return i + 1;
      else if (n == '"')
        quoted = !quoted;

      n = c;
    }

    return substrLen == 1 && substr.charAt(0) == n ? 0 : -1;
  }

  /**
   * Returns the index within the specified string of the last occurrence of the provided substring that is not within a quoted
   * section of the string. A quoted section of a string ends with a double-quote character ({@code '"'}) and starts with a
   * double-quote character or the start of the string.
   *
   * @param str The string.
   * @param substr The substring to find.
   * @return The index within the specified string of the first occurrence of the provided substring that is not within a quoted
   *         section of the string, or {@code -1} if the substring is not found in an unquoted section.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int lastIndexOfUnQuoted(final CharSequence str, final CharSequence substr) {
    return lastIndexOfUnQuoted(str, substr, str.length());
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided character that is not within an enclosed
   * section of the string, starting the search at the specified index. An enclosed section of a string starts with the provided
   * {@code open} character and ends with the provided {@code close} character or the end of the string.
   *
   * @param str The string.
   * @param ch The character to find.
   * @param open The {@code char} indicating the start of an enclosure.
   * @param close The {@code char} indicating the end of an enclosure.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified string of the first occurrence of the provided character that is not within an enclosed
   *         section of the string, or {@code -1} if the character is not found in an unenclosed section.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfUnEnclosed(final CharSequence str, final char ch, final char open, final char close, final int fromIndex) {
    boolean escaped = false;
    boolean enclosed = false;
    for (int i = Math.max(fromIndex, 0), i$ = str.length(); i < i$; ++i) { // [N]
      final char c = str.charAt(i);
      if (escaped)
        escaped = false;
      else if (c == '\\')
        escaped = true;
      else if (enclosed)
        enclosed = c != close;
      else if (c == ch)
        return i;
      else
        enclosed = c == open;
    }

    return -1;
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided character that is not within an enclosed
   * section of the string. An enclosed section of a string starts with the provided {@code open} character and ends with the
   * provided {@code close} character or the end of the string.
   *
   * @param str The string.
   * @param ch The character to find.
   * @param open The {@code char} indicating the start of an enclosure.
   * @param close The {@code char} indicating the end of an enclosure.
   * @return The index within the specified string of the first occurrence of the provided character that is not within an enclosed
   *         section of the string, or {@code -1} if the character is not found in an unenclosed section.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfUnEnclosed(final CharSequence str, final char ch, final char open, final char close) {
    return indexOfUnEnclosed(str, ch, open, close, 0);
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided substring that is not within an enclosed
   * section of the string, starting the search at the specified index. An enclosed section of a string starts with the provided
   * {@code open} character and ends with the provided {@code close} character or the end of the string.
   *
   * @param str The string.
   * @param substr The substring to find.
   * @param open The {@code char} indicating the start of an enclosure.
   * @param close The {@code char} indicating the end of an enclosure.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified string of the first occurrence of the provided substring that is not within an enclosed
   *         section of the string, or {@code -1} if the substring is not found in an unenclosed section.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOfUnEnclosed(final CharSequence str, final CharSequence substr, final char open, final char close, final int fromIndex) {
    boolean escaped = false;
    boolean enclosed = false;
    final int substrLen = substr.length();
    for (int i = Math.max(fromIndex, 0), i$ = str.length(); i < i$; ++i) { // [N]
      final char c = str.charAt(i);
      if (escaped)
        escaped = false;
      else if (c == '\\')
        escaped = true;
      else if (enclosed)
        enclosed = c != close;
      else if (regionMatches0(str, false, i, substr, 0, substrLen))
        return i;
      else
        enclosed = c == open;
    }

    return -1;
  }

  /**
   * Returns the index within the specified string of the first occurrence of the provided substring that is not within an enclosed
   * section of the string. An enclosed section of a string starts with the provided {@code open} character and ends with the
   * provided {@code close} character or the end of the string.
   *
   * @param str The string.
   * @param substr The substring to find.
   * @param open The {@code char} indicating the start of an enclosure.
   * @param close The {@code char} indicating the end of an enclosure.
   * @return The index within the specified string of the first occurrence of the provided substring that is not within an enclosed
   *         section of the string, or {@code -1} if the substring is not found in an unenclosed section.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOfUnEnclosed(final CharSequence str, final CharSequence substr, final char open, final char close) {
    return indexOfUnEnclosed(str, substr, open, close, 0);
  }

  /**
   * Returns the index within the specified string of the provided {@code close} {@code char} corresponding to the matching scope of
   * the given {@code open} {@code char}, starting the search at the specified {@code fromIndex}.
   * <p>
   * As the specified string is traversed, this method keeps track of the depth of scope, whereby "scope" is defined by a matching
   * {@code open} {@code char} followed by a {@code close} {@code char}, within which space no other unmatched {@code open} or
   * {@code close} {@code char} can exist.
   *
   * @implNote The leading {@code open} {@code char} corresponding to the ending {@code close} {@code char} that is sought is
   *           expected to not be present, or is on an index that is less than {@code fromIndex}.
   * @param str The string.
   * @param open The {@code char} indicating a scope open.
   * @param close The {@code char} indicating a scope close.
   * @param fromIndex The index to start the search from. There is no restriction on the value of {@code fromIndex}. If it is
   *          greater than or equal to the length of the string, it has the same effect as if it were equal to one less than the
   *          length of the string: the entire string may be searched. If it is negative, it has the same effect as if it were
   *          {@code -1}: {@code -1} is returned.
   * @return The index within the specified string of the provided {@code close} {@code char} corresponding to the matching scope of
   *         the given {@code open} {@code char}, starting the search at the specified {@code fromIndex}, or {@code -1} if the close
   *         scope is not found.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfScopeClose(final CharSequence str, final char open, final char close, final int fromIndex) {
    boolean escaped = false;
    for (int i = Math.max(fromIndex, 0), i$ = str.length(), scope = 1; i < i$; ++i) { // [N]
      final char c = str.charAt(i);
      if (escaped)
        escaped = false;
      else if (c == '\\')
        escaped = true;
      else if (c == open)
        ++scope;
      else if (c == close && --scope == 0)
        return i;
    }

    return -1;
  }

  /**
   * Returns the index within the specified string of the provided {@code close} {@code char} corresponding to the matching scope of
   * the given {@code open} {@code char}, starting the search at the specified {@code fromIndex}.
   * <p>
   * As the specified string is traversed, this method keeps track of the depth of scope, whereby "scope" is defined by a matching
   * {@code open} {@code char} followed by a {@code close} {@code char}, within which space no other unmatched {@code open} or
   * {@code close} {@code char} can exist.
   *
   * @implNote The leading {@code open} {@code char} corresponding to the ending {@code close} {@code char} that is sought is
   *           expected to not be present.
   * @param str The string.
   * @param open The {@code char} indicating a scope open.
   * @param close The {@code char} indicating a scope close.
   * @return The index within the specified string of the provided {@code close} {@code char} corresponding to the matching scope of
   *         the given {@code open} {@code char}, starting the search at the specified {@code fromIndex}, or {@code -1} if the close
   *         scope is not found.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfScopeClose(final CharSequence str, final char open, final char close) {
    return indexOfScopeClose(str, open, close, 0);
  }

  /**
   * Truncates the specified string to the provided maximum length, adding ellipses ({@code "..."}) if the string is longer than
   * maximum length.
   * <p>
   * Special conditions:
   * <ul>
   * <li>If {@code maxLength < 3}, this method throws an {@link IllegalArgumentException}.</li>
   * <li>If {@code maxLength == 3}, this method returns {@code "..."}.</li>
   * <li>If {@code maxLength >= string.length()}, this method returns {@code str}.</li>
   * <li>If {@code maxLength < string.length()}, this method returns: <blockquote>{@code str.substring(0, maxLength - 3) + "..."}
   * </blockquote></li>
   * </ul>
   *
   * @param str The string to truncate.
   * @param maxLength The max length of the resulting string (must be {@code >= 3}).
   * @return The truncated string.
   * @throws IllegalArgumentException If the provided length is less than 3.
   */
  public static String truncate(final String str, final int maxLength) {
    return truncate(str, maxLength, true);
  }

  /**
   * Truncates the specified string to the provided maximum length, conditionally adding ellipses ({@code "..."}) if the string is
   * longer than maximum length and {@code withEllipsis == true}.
   * <p>
   * Special conditions if {@code withEllipsis == true}:
   * <ul>
   * <li>If {@code maxLength < 3}, this method throws an {@link IllegalArgumentException}.</li>
   * <li>If {@code maxLength == 3}, this method returns {@code "..."}.</li>
   * <li>If {@code maxLength >= string.length()}, this method returns {@code str}.</li>
   * <li>If {@code maxLength < string.length()}, this method returns: <blockquote>{@code str.substring(0, maxLength - 3) + "..."}
   * </blockquote></li>
   * </ul>
   *
   * @param str The string to truncate.
   * @param maxLength The max length of the resulting string (must be {@code >= 3}).
   * @param withEllipsis Whether ellipses should be added in case the specified string is truncated.
   * @return The truncated string.
   * @throws IllegalArgumentException If {@code withEllipsis == true} and the provided length is less than 3.
   */
  public static String truncate(String str, final int maxLength, final boolean withEllipsis) {
    if (str == null)
      str = "null";

    if (!withEllipsis)
      return str.length() <= maxLength ? str : str.substring(0, maxLength);

    if (maxLength < 3)
      throw new IllegalArgumentException("length (" + maxLength + ") must be >= 3 for ellipses (\"...\")");

    return maxLength == 3 ? "..." : str.length() > maxLength ? str.substring(0, maxLength - 3).concat("...") : str;
  }

  /**
   * Flips the capitalization of the first character of the specified string. If the string is in ALLCAPS from the second character
   * to the end, this method returns the original string. This method is reversible, as in: if the resulting string is used as the
   * input, the original input string will be returned.
   * <p>
   * <blockquote> <b>Example:</b>
   * <p>
   * <table>
   * <caption>Example</caption>
   * <tr>
   * <td><b>Input</b></td>
   * <td><b>Output</b></td>
   * </tr>
   * <tr>
   * <td>foo</td>
   * <td>Foo</td>
   * </tr>
   * <tr>
   * <td>fooBar</td>
   * <td>FooBar</td>
   * </tr>
   * <tr>
   * <td>BAR</td>
   * <td>BAR</td>
   * </tr>
   * <tr>
   * <td>fOO</td>
   * <td>fOO</td>
   * </tr>
   * <tr>
   * <td>baR</td>
   * <td>BaR</td>
   * </tr>
   * <tr>
   * <td>FooBar</td>
   * <td>fooBar</td>
   * </tr>
   * </table>
   * </blockquote>
   *
   * @param str The string.
   * @return The specified string with its first character's capitalization flipped, as per the described rules.
   * @throws NullPointerException If {@code str} is null.
   */
  public static String flipFirstCap(final String str) {
    final int length = str.length();
    if (length == 0)
      return str;

    boolean hasLower = false;
    boolean hasUpper = false;
    for (int i = 1; i < length; ++i) { // [N]
      hasLower = hasLower || Character.isLowerCase(str.charAt(i));
      hasUpper = hasUpper || Character.isUpperCase(str.charAt(i));
      if (hasLower && hasUpper)
        break;
    }

    // If the string is ALLUPPER or aLLUPPER then don't modify it
    if (hasUpper && !hasLower)
      return str;

    final char ch = str.charAt(0);
    return (Character.isLowerCase(ch) ? Character.toUpperCase(ch) : Character.toLowerCase(ch)) + str.substring(1);
  }

  @SuppressWarnings("rawtypes")
  private static void appendElVar(final StringBuilder b, final StringBuilder v, final Map vs) {
    final String name = v.toString();
    final Object value = vs.get(name);
    if (value != null)
      b.append(value);
    else
      b.append('$').append('{').append(name).append('}');

    v.setLength(0);
  }

  private static void appendElNoMatch(final StringBuilder b, final StringBuilder v, final char close) {
    b.append('$').append('{');
    if (v.length() > 0) {
      b.append(v);
      v.setLength(0);
    }

    if (close != '\0')
      b.append(close);
  }

  /**
   * Dereferences all Expression Language-encoded names, such as <code>${foo}</code> or <code>${bar}</code>, in the specified string
   * with values in the specified properties.
   * <p>
   * Names encoded in Expression Language follow the same rules as
   * <a href= "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java Identifiers</a>.
   *
   * @param s The string in which EL-encoded names are to be dereferenced.
   * @param vars The properties of name to value pairs.
   * @return The specified string with EL-encoded names replaced with their mapped values. If a name is missing from the specified
   *         properties, or if a name does not conform to the rules of
   *         <a href= "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java Identifiers</a>, or if the
   *         Expression Language encoding is malformed, it will remain in the string as-is.
   * @throws NullPointerException If {@code s} or {@code vars} is null.
   */
  public static String derefEL(final String s, final Properties vars) {
    return derefEL(vars, s);
  }

  /**
   * Dereferences all Expression Language-encoded names, such as <code>${foo}</code> or <code>${bar}</code>, in the specified string
   * with values in the specified map.
   * <p>
   * Names encoded in Expression Language follow the same rules as
   * <a href= "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java Identifiers</a>.
   *
   * @param s The string in which EL-encoded names are to be dereferenced.
   * @param nameToValue The map of name to value pairs.
   * @return The specified string with EL-encoded names replaced with their mapped values. If a name is missing from the specified
   *         map, or if a name does not conform to the rules of
   *         <a href= "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java Identifiers</a>, or if the
   *         Expression Language encoding is malformed, it will remain in the string as-is.
   * @throws NullPointerException If {@code s} or {@code vars} is null.
   */
  public static String derefEL(final String s, final Map<String,String> nameToValue) {
    return derefEL(nameToValue, s);
  }

  @SuppressWarnings("rawtypes")
  private static String derefEL(final Map vs, final String s) {
    if (s.length() < 4)
      return s;

    final StringBuilder b = new StringBuilder();
    final StringBuilder v = new StringBuilder();
    boolean escape = false;
    final int i$ = s.length();
    for (int i = 0; i < i$; ++i) { // [N]
      char ch = s.charAt(i);
      if (ch == '\\') {
        if (v.length() > 0) {
          b.append('$').append('{').append(v);
          v.setLength(0);
        }

        if (!(escape = !escape))
          b.append(ch);
      }
      else if (!escape) {
        if (ch == '$') {
          if (v.length() > 0) {
            appendElVar(b, v, vs);
          }

          if (++i == i$) {
            b.append('$');
          }
          else {
            ch = s.charAt(i);
            if (ch != '{') {
              v.setLength(0);
              b.append('$');
              if (ch != '\\')
                b.append(ch);
            }
            else if (++i == i$) {
              appendElNoMatch(b, v, '\0');
            }
            else {
              ch = s.charAt(i);
              if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_' || ch == '.')
                v.append(ch);
              else
                appendElNoMatch(b, v, ch);
            }
          }
        }
        else if (v.length() > 0) {
          if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9' || ch == '_' || ch == '.') {
            v.append(ch);
          }
          else if (ch != '}') {
            appendElNoMatch(b, v, ch);
          }
          else {
            appendElVar(b, v, vs);
            if (ch != '}')
              b.append(ch);
          }
        }
        else {
          b.append(ch);
        }
      }
      else {
        if (v.length() > 0)
          appendElVar(b, v, vs);

        b.append(ch);
        escape = false;
      }
    }

    if (v.length() > 0)
      appendElNoMatch(b, v, '\0');

    return b.toString();
  }

  private static void appendEvVar(final StringBuilder builder, final StringBuilder v, final Map<String,String> vs) {
    final String variable = vs.get(v.toString());
    if (variable != null)
      builder.append(variable);

    v.setLength(0);
  }

  /**
   * Dereferences all POSIX-compliant Environment Variable names, such as <code>$FOO</code> or <code>${BAR}</code>, in the specified
   * string with values in the specified map.
   * <p>
   * Names encoded in POSIX format follow the rules defined in the POSIX standard on shells
   * <a href= "http://pubs.opengroup.org/onlinepubs/9699919799/utilities/V3_chap02.html#tag_18_10_02">IEEE Std 1003.1-2017</a>.
   *
   * @param s The string in which POSIX-compliant names are to be dereferenced.
   * @param vs The map of name to value pairs.
   * @return The specified string with POSIX-compliant names replaced with their mapped values. If a name is missing from the
   *         specified map, it will remain in the string as-is.
   * @throws ParseException If the encoding of the environment variable name is malformed.
   * @throws NullPointerException If {@code s} or {@code vars} is null.
   */
  public static String derefEV(final String s, final Map<String,String> vs) throws ParseException {
    if (s.length() < 2)
      return s;

    final StringBuilder b = new StringBuilder();
    final StringBuilder v = new StringBuilder();
    boolean escape = false;
    boolean bracket = false;
    final int i$ = s.length();
    for (int i = 0; i < i$; ++i) { // [N]
      char ch = s.charAt(i);
      if (ch == '\\') {
        if (v.length() > 0)
          appendEvVar(b, v, vs);

        if (!(escape = !escape))
          b.append(ch);
      }
      else if (!escape) {
        if (ch == '$') {
          if (v.length() > 0)
            appendEvVar(b, v, vs);

          if (++i == i$) {
            b.append('$');
          }
          else {
            ch = s.charAt(i);
            if (ch == '$')
              throw new ParseException("$$: not supported", i);

            if (ch == '{') {
              bracket = true;
              if (++i == i$)
                throw new ParseException("${: bad substitution", i);

              ch = s.charAt(i);
            }

            if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '_') {
              v.append(ch);
            }
            else if (!bracket) {
              b.append('$');
              if (ch != '\\')
                b.append(ch);
            }
            else {
              throw new ParseException("${" + ch + ": bad substitution", i);
            }
          }
        }
        else if (v.length() > 0) {
          if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9' || ch == '_') {
            v.append(ch);
          }
          else if (bracket && ch != '}') {
            throw new ParseException("${" + v + ch + ": bad substitution", i);
          }
          else {
            appendEvVar(b, v, vs);
            if (!bracket || ch != '}')
              b.append(ch);
          }
        }
        else {
          b.append(ch);
        }
      }
      else {
        if (v.length() > 0)
          appendEvVar(b, v, vs);

        b.append(ch);
        escape = false;
      }
    }

    if (v.length() > 0) {
      if (bracket)
        throw new ParseException("${" + v + ": bad substitution", i$);

      appendEvVar(b, v, vs);
    }

    return b.toString();
  }

  /**
   * Tests if the specified string is fully comprised of whitespace characters.
   *
   * @param str The {@link String} to test.
   * @return {@code true} if the specified string is fully comprised of whitespace characters. This method returns {@code true} for
   *         empty strings and {@code false} for null.
   */
  public static boolean isWhitespace(final CharSequence str) {
    if (str == null)
      return false;

    for (int i = 0, i$ = str.length(); i < i$; ++i) // [N]
      if (!Character.isWhitespace(str.charAt(i)))
        return false;

    return true;
  }

  /**
   * Determines whether all characters in the specified {@link CharSequence} are in the general category type of
   * {@link Character#LOWERCASE_LETTER}.
   *
   * @param str The {@link CharSequence}.
   * @return {@code true} if all characters in the specified {@link CharSequence} are lower-case characters; otherwise
   *         {@code false}.
   * @throws NullPointerException If {@code str} is null.
   * @throws IllegalArgumentException If {@code str} is empty.
   */
  public static boolean isLowerCase(final CharSequence str) {
    final int length = str.length();
    assertPositive(length, "Empty");
    for (int i = 0; i < length; ++i) // [N]
      if (!Character.isLowerCase(str.charAt(i)))
        return false;

    return true;
  }

  /**
   * Determines whether all characters in the specified {@link CharSequence} are in the general category type of
   * {@link Character#UPPERCASE_LETTER}.
   *
   * @param str The {@link CharSequence}.
   * @return {@code true} if all characters in the specified {@link CharSequence} are upper-case characters; otherwise
   *         {@code false}.
   * @throws NullPointerException If {@code str} is null.
   * @throws IllegalArgumentException If {@code str} is empty.
   */
  public static boolean isUpperCase(final CharSequence str) {
    final int length = str.length();
    assertPositive(length, "Empty");
    for (int i = 0; i < length; ++i) // [N]
      if (!Character.isUpperCase(str.charAt(i)))
        return false;

    return true;
  }

  /**
   * Returns a {@code long}-valued hash code for the specified {@link CharSequence}, using the same algorithm as in
   * {@link String#hashCode()}.
   *
   * @param str The {@link CharSequence}.
   * @return The {@code long}-valued hash code value for the specified {@link CharSequence}.
   * @see String#hashCode()
   */
  public static long hash(final CharSequence str) {
    if (str == null)
      return 0;

    long hash = 0;
    for (int i = 0, i$ = str.length(); i < i$; ++i) // [N]
      hash = 31 * hash + str.charAt(i);

    return hash;
  }

  /**
   * Indents the specified string with the provided number of spaces. This method prepends the specified number of space characters
   * {@code ' '} before each new-line character {@code '\n'}.
   *
   * @param str The {@link String}.
   * @param spaces The number of spaces to indent.
   * @return A {@link StringBuilder} instance with the indented string.
   * @throws NullPointerException If {@code str} is null.
   * @throws IllegalArgumentException If the number of spaces is negative.
   */
  public static StringBuilder indent(final String str, final int spaces) {
    return indent(new StringBuilder(str), spaces);
  }

  /**
   * Indents the specified {@link StringBuilder} with the provided number of spaces. This method prepends the specified number of
   * space characters {@code ' '} before each new-line character {@code '\n'}.
   *
   * @param str The {@link StringBuilder}.
   * @param spaces The number of spaces to indent.
   * @return The specified {@link StringBuilder} instance, indented.
   * @throws NullPointerException If {@code str} is null.
   * @throws IllegalArgumentException If the number of spaces is negative.
   */
  public static StringBuilder indent(final StringBuilder str, final int spaces) {
    if (spaces == 0)
      return str;

    final String indent = repeat(' ', spaces);
    Strings.replace(str, "\n\n", "\7\n");

    for (int i = str.length(); i > 0 && (i = Strings.lastIndexOf(str, '\n', i - 1)) > -1;) // [X]
      str.insert(i + 1, indent);

    Strings.replace(str, '\7', '\n');
    return str;
  }

  /**
   * Tests if two {@link CharSequence} regions are equal.
   *
   * @param str The {@code CharSequence} to be processed.
   * @param ignoreCase If {@code true}, ignore case when comparing characters.
   * @param strOffset The starting offset of the subregion in {@code str}.
   * @param substr The {@code CharSequence} to be searched.
   * @param substrOffset The starting offset of the subregion in {@code substr}.
   * @param len The number of characters to compare.
   * @return {@code true} if the specified subregion of {@code str} matches the specified subregion of {@code substr}; {@code false}
   *         otherwise. Whether the matching is exact or case insensitive depends on the {@code ignoreCase} argument.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static boolean regionMatches(final CharSequence str, final boolean ignoreCase, final int strOffset, final CharSequence substr, final int substrOffset, int len) {
    return regionMatches0(str, ignoreCase, strOffset, substr, substrOffset, len);
  }

  private static boolean regionMatches0(final CharSequence str, final boolean ignoreCase, final int strOffset, final CharSequence substr, final int substrOffset, int len) {
    if (str instanceof String && substr instanceof String)
      return ((String)str).regionMatches(ignoreCase, strOffset, (String)substr, substrOffset, len);

    if (substrOffset < 0 || strOffset < 0 || strOffset > (long)str.length() - len || substrOffset > (long)substr.length() - len)
      return false;

    for (int i1 = strOffset, i2 = substrOffset; len-- > 0; ++i1, ++i2) { // [N]
      final char c1 = str.charAt(i1);
      final char c2 = substr.charAt(i2);

      if (c1 == c2)
        continue;

      if (!ignoreCase)
        return false;

      if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2))
        return false;
    }

    return true;
  }

  /**
   * Returns the index within the specified {@link CharSequence str} of the first occurrence of the specified {@code char}.
   * <p>
   * The returned index is the smallest value {@code k} for which:
   *
   * <pre>
   * {@code
   * this.startsWith(str, k)
   * }
   * </pre>
   *
   * If no such value of {@code k} exists, then {@code -1} is returned.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param ch The {@code char} to search for.
   * @return The index of the first occurrence of the specified {@code char}, or {@code -1} if there is no such occurrence.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOf(final CharSequence str, final char ch) {
    return indexOf(str, ch, 0);
  }

  /**
   * Returns the index within the specified {@link CharSequence str} of the first occurrence of the specified {@code char}.
   * <p>
   * The returned index is the smallest value {@code k} for which:
   *
   * <pre>
   * {@code
   * this.startsWith(str, k)
   * }
   * </pre>
   *
   * If no such value of {@code k} exists, then {@code -1} is returned.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param ch The {@code char} to search for.
   * @param fromIndex The index from which to start the search.
   * @return The index of the first occurrence of the specified {@code char}, or {@code -1} if there is no such occurrence.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOf(final CharSequence str, final char ch, int fromIndex) {
    if (fromIndex < 0)
      fromIndex = 0;

    final int length = str.length();
    if (fromIndex > length)
      return -1;

    for (int i = fromIndex; i < length; ++i) // [N]
      if (str.charAt(i) == ch)
        return i;

    return -1;
  }

  /**
   * Returns the index within the specified {@link CharSequence str} of the first occurrence of the specified {@link CharSequence
   * substr}.
   * <p>
   * The returned index is the smallest value {@code k} for which:
   *
   * <pre>
   * {@code
   * this.startsWith(str, k)
   * }
   * </pre>
   *
   * If no such value of {@code k} exists, then {@code -1} is returned.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param substr The {@link CharSequence} to search for.
   * @return The index of the first occurrence of the specified substring, or {@code -1} if there is no such occurrence.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOf(final CharSequence str, final CharSequence substr) {
    return indexOf(str, substr, 0);
  }

  /**
   * Returns the index within the specified {@link CharSequence str} of the first occurrence of the specified {@link CharSequence
   * substr}.
   * <p>
   * The returned index is the smallest value {@code k} for which:
   *
   * <pre>
   * {@code
   * this.startsWith(str, k)
   * }
   * </pre>
   *
   * If no such value of {@code k} exists, then {@code -1} is returned.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param substr The {@link CharSequence} to search for.
   * @param fromIndex The index from which to start the search.
   * @return The index of the first occurrence of the specified substring, or {@code -1} if there is no such occurrence.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOf(final CharSequence str, final CharSequence substr, int fromIndex) {
    if (fromIndex < 0)
      fromIndex = 0;

    final int subStrLength = substr.length();
    final int i$ = str.length() - subStrLength + 1;
    if (fromIndex > i$)
      return -1;

    if (subStrLength == 0)
      return fromIndex;

    for (int i = fromIndex; i < i$; ++i) // [N]
      if (regionMatches0(str, false, i, substr, 0, subStrLength))
        return i;

    return -1;
  }

  /**
   * Returns the index within the specified {@link CharSequence str} of the first occurrence of the specified {@code char} (ignoring
   * case).
   * <p>
   * The returned index is the smallest value {@code k} for which:
   *
   * <pre>
   * {@code
   * this.startsWith(str, k)
   * }
   * </pre>
   *
   * If no such value of {@code k} exists, then {@code -1} is returned.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param ch The {@code char} to search for.
   * @return The index of the first occurrence of the specified {@code char}, or {@code -1} if there is no such occurrence (ignoring
   *         case).
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfIgnoreCase(final CharSequence str, final char ch) {
    return indexOfIgnoreCase(str, ch, 0);
  }

  /**
   * Returns the index within the specified {@link CharSequence str} of the first occurrence of the specified {@code char} (ignoring
   * case).
   * <p>
   * The returned index is the smallest value {@code k} for which:
   *
   * <pre>
   * {@code
   * this.startsWith(str, k)
   * }
   * </pre>
   *
   * If no such value of {@code k} exists, then {@code -1} is returned.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param ch The {@code char} to search for.
   * @param fromIndex The index from which to start the search.
   * @return The index of the first occurrence of the specified {@code char} (ignoring case), or {@code -1} if there is no such
   *         occurrence.
   * @throws NullPointerException If {@code str} is null.
   */
  public static int indexOfIgnoreCase(final CharSequence str, char ch, int fromIndex) {
    if (fromIndex < 0)
      fromIndex = 0;

    final int length = str.length();
    if (fromIndex > length)
      return -1;

    final char uCh, lCh;
    if (Character.isUpperCase(ch)) {
      uCh = ch;
      lCh = Character.toLowerCase(ch);
    }
    else {
      uCh = Character.toUpperCase(ch);
      lCh = ch;
    }

    for (int i = fromIndex; i < length; ++i) { // [N]
      ch = str.charAt(i);
      if (ch == uCh || ch == lCh)
        return i;
    }

    return -1;
  }

  /**
   * Returns the index within the specified {@link CharSequence str} of the first occurrence of the specified {@link CharSequence
   * substr} (ignoring case).
   * <p>
   * The returned index is the smallest value {@code k} for which:
   *
   * <pre>
   * {@code
   * this.startsWith(str, k)
   * }
   * </pre>
   *
   * If no such value of {@code k} exists, then {@code -1} is returned.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param substr The {@link CharSequence} to search for.
   * @return The index of the first occurrence of the specified substring (ignoring case), or {@code -1} if there is no such
   *         occurrence.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOfIgnoreCase(final CharSequence str, final CharSequence substr) {
    return indexOfIgnoreCase(str, substr, 0);
  }

  /**
   * Returns the index within the specified {@link CharSequence str} of the first occurrence of the specified {@link CharSequence
   * substr} (ignoring case).
   * <p>
   * The returned index is the smallest value {@code k} for which:
   *
   * <pre>
   * {@code
   * this.startsWith(str, k)
   * }
   * </pre>
   *
   * If no such value of {@code k} exists, then {@code -1} is returned.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param substr The {@link CharSequence} to search for.
   * @param fromIndex The index from which to start the search.
   * @return The index of the first occurrence of the specified substring (ignoring case), or {@code -1} if there is no such
   *         occurrence.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static int indexOfIgnoreCase(final CharSequence str, final CharSequence substr, int fromIndex) {
    if (fromIndex < 0)
      fromIndex = 0;

    final int subStrLength = substr.length();
    final int i$ = str.length() - subStrLength + 1;
    if (fromIndex > i$)
      return -1;

    if (subStrLength == 0)
      return fromIndex;

    for (int i = fromIndex; i < i$; ++i) // [N]
      if (regionMatches0(str, true, i, substr, 0, subStrLength))
        return i;

    return -1;
  }

  /**
   * Returns {@code true} if and only if the specified {@link CharSequence str} contains the specified {@code char}.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param ch The {@code char} to search for.
   * @return true If this string contains {@code ch}, false otherwise.
   * @throws NullPointerException If {@code str} is null.
   */
  public static boolean containsIgnoreCase(final CharSequence str, final char ch) {
    return indexOfIgnoreCase(str, ch) > -1;
  }

  /**
   * Returns {@code true} if and only if the specified {@link CharSequence str} contains the specified {@link CharSequence substr}.
   *
   * @param str The {@link CharSequence} in which to search.
   * @param substr The {@link CharSequence} to search for.
   * @return true If this string contains {@code substr}, false otherwise.
   * @throws NullPointerException If {@code str} or {@code substr} is null.
   */
  public static boolean containsIgnoreCase(final CharSequence str, final CharSequence substr) {
    return indexOfIgnoreCase(str, substr) > -1;
  }

  /**
   * Returns a {@link UUID} representation of the specified string, or {@code null} if the string cannot be converted to a
   * {@link UUID}.
   *
   * @param str The string to convert to a {@link UUID}.
   * @return A {@link UUID} representation of the specified string, or {@code null} if the string cannot be converted to a
   *         {@link UUID}.
   */
  public static UUID toUuidOrNull(final String str) {
    if (str == null)
      return null;

    if (str.length() != 36)
      return null;

    for (int i = 0, ch; i < 36; ++i) { // [N]
      ch = str.charAt(i);
      if (i == 8 || i == 13 || i == 18 || i == 23) {
        if (ch != '-')
          return null;

        continue;
      }

      if (!('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f' || 'A' <= ch && ch <= 'F'))
        return null;
    }

    return UUID.fromString(str);
  }

  private static final ConcurrentHashMap<String,String> interns = new ConcurrentHashMap<>();

  /**
   * Returns a canonical representation for the string object.
   * <p>
   * This method differentiates itself from {@link String#intern()} by maintaining a {@link ConcurrentHashMap} of strings that
   * outperforms the native {@link String#intern()} implementation for large maps.
   *
   * @param str The string to intern.
   * @return A string that has the same contents as the specified string, but is guaranteed to be from a pool of unique strings.
   */
  public static String intern(final String str) {
    if (str == null)
      return null;

    final String intern = interns.putIfAbsent(str, str);
    return intern != null ? intern : str;
  }

  private static String[] split(final CharSequence str, final int i$, final char ch, final int empties, final StringBuilder b, int index, final int depth) {
    final String[] parts;
    final char c = str.charAt(index);
    if (c != ch) {
      b.append(c);
      if (++index == i$) {
        if (index != i$ || b.length() > 0) {
          final String part = b.toString();
          parts = new String[depth + 1];
          parts[depth] = part;
        }
        else {
          parts = new String[depth - empties];
        }
      }
      else {
        parts = split(str, i$, ch, empties, b, index, depth);
      }
    }
    else {
      if (++index != i$ || b.length() > 0) {
        final String part = b.toString();
        if (index == i$)
          parts = new String[depth + 1];
        else
          parts = split(str, i$, ch, part.length() == 0 ? empties + 1 : 0, new StringBuilder(), index, depth + 1);

        parts[Math.min(parts.length - 1, depth)] = part;
      }
      else {
        if (index == i$)
          parts = new String[depth - empties];
        else
          parts = split(str, i$, ch, empties, new StringBuilder(), index, depth);
      }
    }

    return parts;
  }

  /**
   * Splits the provided {@link CharSequence str} around matches of the given {@code char} literal.
   *
   * @param str The {@link CharSequence} to split.
   * @param ch The {@code char} literal to match.
   * @return The array of strings computed by splitting this string around matches of the given regular expression.
   * @throws NullPointerException If {@code str} is null.
   * @see String#split(String)
   */
  public static String[] split(final CharSequence str, final char ch) {
    final int i$ = str.length();
    return i$ == 0 ? EMPTY_ARRAY : split(str, i$, ch, 0, new StringBuilder(), 0, 0);
  }

  /**
   * Compares the two provided {@link CharSequence} objects for equality. The result is {@code
   * true} if and only if the two objects are null, or the two objects represent the same sequence of characters.
   *
   * @param a The first {@link CharSequence}.
   * @param b The second {@link CharSequence}.
   * @return {@code true} if and only if the two objects are null, or the objects represent the same sequence of characters.
   * @see String#compareTo(String)
   * @see String#compareToIgnoreCase(String)
   * @see String#equals(Object)
   * @see String#equalsIgnoreCase(String)
   */
  public static boolean equals(final CharSequence a, final CharSequence b) {
    if (a == b)
      return true;

    if (a == null || b == null)
      return false;

    final int len = a.length();
    if (len != b.length())
      return false;

    for (int i = 0; i < len; ++i) // [A]
      if (a.charAt(i) != b.charAt(i))
        return false;

    return true;
  }

  /**
   * Compares the two provided {@link CharSequence} objects for equality, ignoring case. The result is {@code
   * true} if and only if the two objects are null, or the two objects represent the same sequence of characters, ignoring case.
   *
   * @param a The first {@link CharSequence}.
   * @param b The second {@link CharSequence}.
   * @return {@code true} if and only if the two objects are null, or the objects represent the same sequence of characters,
   *         ignoring case.
   * @see String#compareTo(String)
   * @see String#compareToIgnoreCase(String)
   * @see String#equals(Object)
   * @see String#equalsIgnoreCase(String)
   */
  public static boolean equalsIgnoreCase(final CharSequence a, final CharSequence b) {
    if (a == b)
      return true;

    if (a == null || b == null)
      return false;

    final int len = a.length();
    if (len != b.length())
      return false;

    for (int i = 0; i < len; ++i) // [A]
      if (Character.toLowerCase(a.charAt(i)) != Character.toLowerCase(b.charAt(i)))
        return false;

    return true;
  }

  private static final Pattern replacePattern = Pattern.compile("^/((([^/])|(\\\\/))+)/((([^/])|(\\\\/))+)/$");

  /**
   * Returns the resulting string after the invocation of the provided {@code searchReplaceRegex} on the specified {@code str}, or
   * the original {@code str} if {@code rename} is null. The RegEx pattern provided in {@code searchReplaceRegex} must be in the
   * form:
   *
   * <pre>
   * {@code /<search>/<replace>/}
   * </pre>
   *
   * @param str The string on which to invoke the {@code searchReplaceRegex}.
   * @param searchReplaceRegex The {@code /search/replace} RegEx pattern to invoke on {@code str}.
   * @return the resulting string after the invocation of the provided {@code searchReplaceRegex} on the specified {@code str}, or
   *         the original {@code str} if {@code rename} is null.
   * @throws IllegalArgumentException If {@code rename} is malformed.
   * @throws NullPointerException If {@code path} is null.
   * @implNote This implementation supports Replacement Text Case Conversion via {@code \\l}, {@code \\u}, {@code \\L}, and
   *           {@code \\U} operators.
   */
  public static String searchReplace(String str, final String searchReplaceRegex) {
    if (searchReplaceRegex == null)
      return str;

    final Matcher matcher = replacePattern.matcher(searchReplaceRegex);
    if (!matcher.matches())
      throw new IllegalArgumentException(searchReplaceRegex + ": must be in the form: /<search>/<replace>/");

    final String regex = matcher.group(1);
    final String replacement = matcher.group(5).replaceAll("((\\\\[lLuU])(\\$((\\d+)|(\\{[^}]+\\})))+)", "\\\\$1\\\\\\\\$2");
    str = str.replaceFirst(regex, replacement);

    final StringBuilder b = new StringBuilder();
    boolean escaped = false;
    Boolean upperOne = null;
    boolean upperAll = false;
    Boolean lowerOne = null;
    boolean lowerAll = false;
    for (int i = 0, i$ = str.length(); i < i$; ++i) { // [ST]
      final char ch = str.charAt(i);
      if (escaped) {
        escaped = false;

        if (ch == 'u') {
          upperOne = upperOne != null ? null : Boolean.TRUE;
          continue;
        }

        if (ch == 'U') {
          upperAll = !upperAll;
          continue;
        }

        if (ch == 'l') {
          lowerOne = lowerOne != null ? null : Boolean.TRUE;
          continue;
        }

        if (ch == 'L') {
          lowerAll = !lowerAll;
          continue;
        }
      }
      else if (ch == '\\') {
        escaped = true;
        continue;
      }
      else if (upperOne != null) {
        if (upperOne)
          b.append(Character.toUpperCase(ch));
        else
          b.append(ch);

        upperOne = Boolean.FALSE;
        continue;
      }
      else if (upperAll) {
        b.append(Character.toUpperCase(ch));
        continue;
      }
      else if (lowerOne != null) {
        if (lowerOne)
          b.append(Character.toLowerCase(ch));
        else
          b.append(ch);

        lowerOne = Boolean.FALSE;
        continue;
      }
      else if (lowerAll) {
        b.append(Character.toLowerCase(ch));
        continue;
      }

      b.append(ch);
    }

    return b.toString();
  }

  private Strings() {
  }
}