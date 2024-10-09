/* Copyright (c) 2019 LibJ
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

import static org.junit.Assert.*;

import java.util.Random;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

public class ArrayCharSequenceTest {
  private static final Function<String,ArrayCharSequence> buf = (final String s) -> ArrayCharSequence.of(s.toCharArray());
  private static final Function<String,ArrayCharSequence> str = (final String s) -> ArrayCharSequence.of(s);
  private static final Random r = new Random();

  private static void assertEquals(final CharSequence expected, final CharSequence actual) {
    for (int i = 0, i$ = expected.length(); i < i$; ++i) // [N]
      Assert.assertEquals(i + "\n" + expected + "\n" + actual, expected.charAt(i), actual.charAt(i));
  }

  private static void test(final Function<String,ArrayCharSequence> f) {
    final int len = Math.abs(r.nextInt() % 1024);
    final String expected = Strings.getRandomAlphaNumeric(len);
    final ArrayCharSequence actual = f.apply(expected);
    assertEquals(expected, actual);

    final CharSequence subExpected = expected.subSequence(len / 4, len / 2);
    final CharSequence subActual = actual.subSequence(len / 4, len / 2);
    assertEquals(subExpected, subActual);
  }

  @Test
  public void testNull() {
    try {
      ArrayCharSequence.of((String)null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    try {
      ArrayCharSequence.of((char[])null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }
  }

  @Test
  public void testOffset() {
    try {
      ArrayCharSequence.of("012", 5);
      fail("Expected IndexOutOfBoundsException");
    }
    catch (final IndexOutOfBoundsException e) {
    }

    try {
      ArrayCharSequence.of(new char[] {0, 1, 2}, 7);
      fail("Expected IndexOutOfBoundsException");
    }
    catch (final IndexOutOfBoundsException e) {
    }
  }

  @Test
  public void testLength() {
    try {
      ArrayCharSequence.of("012", 1, 7);
      fail("Expected IndexOutOfBoundsException");
    }
    catch (final IndexOutOfBoundsException e) {
    }

    try {
      ArrayCharSequence.of(new char[] {0, 1, 2}, 2, 9);
      fail("Expected IndexOutOfBoundsException");
    }
    catch (final IndexOutOfBoundsException e) {
    }
  }

  @Test
  public void test() {
    for (int i = 0; i < 1000; ++i) { // [N]
      test(buf);
      test(str);
    }
  }
}