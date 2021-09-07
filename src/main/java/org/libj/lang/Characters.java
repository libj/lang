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

import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Utility functions for operations pertaining to {@code char} and
 * {@link Character}.
 */
public final class Characters {
  private static final char[] escapableChars = {'"', '\'', '0', '1', '2', '3', '4', '5', '6', '7', '\\', 'b', 'f', 'n', 'r', 't'};
  private static final char[] escapedChars = {'\"', '\'', '\0', '\1', '\2', '\3', '\4', '\5', '\6', '\7', '\\', '\b', '\f', '\n', '\r', '\t'};
  private static final String[] asciiCharToString = new String[128];

  static {
    for (int i = 0; i < 128; ++i)
      asciiCharToString[i] = String.valueOf((char)i);
  }

  public static String toString(final char ch) {
    return ch < 128 ? asciiCharToString[ch] : String.valueOf(ch);
  }

  /**
   * Returns the escaped representation of the specified character.
   * <blockquote>
   * <table>
   * <caption>Escape Sequences</caption>
   * <tr><td><b>Escape Sequence</b></td><td><b>Description</b></td></tr>
   * <tr><td><code>\<b>t</b></code></td><td>Tab</td></tr>
   * <tr><td><code>\<b>b</b></code></td><td>Backspace</td></tr>
   * <tr><td><code>\<b>n</b></code></td><td>Newline</td></tr>
   * <tr><td><code>\<b>r</b></code></td><td>Carriage return</td></tr>
   * <tr><td><code>\<b>f</b></code></td><td>Formfeed</td></tr>
   * <tr><td><code>\<b>'</b></code></td><td>Single quote</td></tr>
   * <tr><td><code>\<b>"</b></code></td><td>Double quote</td></tr>
   * <tr><td><code>\<b>\</b></code></td><td>Backslash</td></tr>
   * <tr><td><code>\<b>0</b></code></td><td>0</td></tr>
   * <tr><td><code>\<b>1</b></code></td><td>1</td></tr>
   * <tr><td><code>\<b>2</b></code></td><td>2</td></tr>
   * <tr><td><code>\<b>3</b></code></td><td>3</td></tr>
   * <tr><td><code>\<b>4</b></code></td><td>4</td></tr>
   * <tr><td><code>\<b>5</b></code></td><td>5</td></tr>
   * <tr><td><code>\<b>6</b></code></td><td>6</td></tr>
   * <tr><td><code>\<b>7</b></code></td><td>7</td></tr>
   * </table>
   * </blockquote>
   *
   * @implSpec This method only supports single-character escape sequences, and
   *           therefore does not handle 2-digit or 3-digit octal escape
   *           sequences, or 4-hex-digit unicode sequences.
   * @param ch The character to escape.
   * @return The escaped character.
   * @throws IllegalArgumentException If the specified character is not escapable.
   * @see <a href=
   *      "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">Escape
   *      Sequences for Character and String Literals</a>
   */
  public static char escape(final char ch) {
    final int i = Arrays.binarySearch(escapableChars, ch);
    if (i < 0)
      throw new IllegalArgumentException(ch + " is not an escapable character");

    return escapedChars[i];
  }

  /**
   * Tests whether the specified character is escapable.
   * <blockquote>
   * <table>
   * <caption>Escape Sequences</caption>
   * <tr><td><b>Escape Sequence</b></td><td><b>Description</b></td></tr>
   * <tr><td><code>\<b>t</b></code></td><td>Tab</td></tr>
   * <tr><td><code>\<b>b</b></code></td><td>Backspace</td></tr>
   * <tr><td><code>\<b>n</b></code></td><td>Newline</td></tr>
   * <tr><td><code>\<b>r</b></code></td><td>Carriage return</td></tr>
   * <tr><td><code>\<b>f</b></code></td><td>Formfeed</td></tr>
   * <tr><td><code>\<b>'</b></code></td><td>Single quote</td></tr>
   * <tr><td><code>\<b>"</b></code></td><td>Double quote</td></tr>
   * <tr><td><code>\<b>\</b></code></td><td>Backslash</td></tr>
   * <tr><td><code>\<b>0</b></code></td><td>0</td></tr>
   * <tr><td><code>\<b>1</b></code></td><td>1</td></tr>
   * <tr><td><code>\<b>2</b></code></td><td>2</td></tr>
   * <tr><td><code>\<b>3</b></code></td><td>3</td></tr>
   * <tr><td><code>\<b>4</b></code></td><td>4</td></tr>
   * <tr><td><code>\<b>5</b></code></td><td>5</td></tr>
   * <tr><td><code>\<b>6</b></code></td><td>6</td></tr>
   * <tr><td><code>\<b>7</b></code></td><td>7</td></tr>
   * </table>
   * </blockquote>
   *
   * @implSpec This method only supports single-character escape sequences, and
   *           therefore does not handle 2-digit or 3-digit octal escape
   *           sequences, or 4-hex-digit unicode sequences.
   * @param ch The character to test.
   * @return {@code true} if the specified character is escapable.
   * @see <a href=
   *      "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">Escape
   *      Sequences for Character and String Literals</a>
   */
  public static boolean isEscapable(final char ch) {
    return Arrays.binarySearch(escapableChars, ch) > -1;
  }

  /**
   * Tests whether the specified character is printable.
   *
   * @param ch The character to test.
   * @return {@code true} if the specified character is printable.
   */
  public static boolean isPrintable(final char ch) {
    final Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
    return block != null && block != Character.UnicodeBlock.SPECIALS && ch != KeyEvent.CHAR_UNDEFINED && !Character.isISOControl(ch);
  }

  /**
   * Converts the provided string to a new character array, or returns
   * {@code null} if {@code str} is null.
   *
   * @param str The string to convert to a new character array.
   * @return A newly allocated character array whose length is the length of the
   *         provided string and whose contents are initialized to contain the
   *         character sequence represented by the provided string, or returns
   *         {@code null} if {@code str} is null.
   */
  public static char[] valueOf(final String str) {
    return str == null ? null : str.toCharArray();
  }

  private Characters() {
  }
}