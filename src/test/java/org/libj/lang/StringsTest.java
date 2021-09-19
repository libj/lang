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

import static org.junit.Assert.*;
import static org.libj.lang.Strings.Align.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

public class StringsTest {
  private static final String UPPER_CASE = "HELLO WORLD";
  private static final String LOWER_CASE = "hello world";

  private static final Random r = new Random();

  @Test
  public void testIndexOf1() {
    for (int i = 0; i < 10000; ++i) {
      final String str = Strings.getRandomAlphaNumeric(256);
      final String find = Strings.getRandomAlphaNumeric(1);
      final int expected = str.indexOf(find);
      assertEquals(expected, Strings.indexOf(str, find));
      assertEquals(expected, Strings.indexOf(str, find.charAt(0)));
    }
  }

  @Test
  public void testIndexOf2() {
    for (int i = 0; i < 10000; ++i) {
      final String str = Strings.getRandomAlphaNumeric(256);
      final String find = Strings.getRandomAlphaNumeric(1);
      final int from = r.nextInt(str.length());
      final int expected = str.indexOf(find, from);
      assertEquals(expected, Strings.indexOf(str, find, from));
      assertEquals(expected, Strings.indexOf(str, find.charAt(0), from));
    }
  }

  @Test
  public void testEndsWith() {
    for (int i = 0; i < 10000; ++i) {
      final String str = Strings.getRandomAlphaNumeric(256);
      final String find = Strings.getRandomAlphaNumeric(1);
      assertEquals(str.endsWith(find), Strings.endsWith(str, find.charAt(0)));
    }
  }

  @Test
  public void testLastIndexOf() {
    assertEquals(2, Strings.lastIndexOf("abc", 'c'));
    assertEquals(1, Strings.lastIndexOf("abc", 'b'));
    assertEquals(0, Strings.lastIndexOf("abc", 'a'));
    assertEquals(-1, Strings.lastIndexOf("abc", 'x'));
  }

  @Test
  public void testLastIndexOfUnEscaped() {
    assertEquals(1, Strings.lastIndexOfUnEscaped("aa\\a", 'a'));
    assertEquals(4, Strings.lastIndexOfUnEscaped("aa\\\\a", 'a'));
    assertEquals(1, Strings.lastIndexOfUnEscaped("aa\\\\\\a", 'a'));
  }

  @Test
  public void testIndexOfScopeClose() {
    assertEquals(-1, Strings.indexOfScopeClose("{abc}abc", '{', '}'));
    assertEquals(3, Strings.indexOfScopeClose("abc}abc", '{', '}'));
    assertEquals(4, Strings.indexOfScopeClose("{abc}abc", '{', '}', 1));
    assertEquals(9, Strings.indexOfScopeClose("{abc{abc}}abc", '{', '}', 1));
    assertEquals(8, Strings.indexOfScopeClose("abc{abc}}abc", '{', '}'));
    assertEquals(22, Strings.indexOfScopeClose("{country:([a-zA-Z]{2})}{p:/?}{state:(([\\}\\{a-zA-Z]{2})?)}", '{', '}', 1));
    assertEquals(56, Strings.indexOfScopeClose("{country:([a-zA-Z]{2})}{p:/?}{state:(([\\}\\{a-zA-Z]{2})?)}", '{', '}', 34));
  }

  @Test
  public void testGetRandomAlphaString() {
    try {
      Strings.getRandomAlpha(-1);
      fail("Expecting an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    for (int len = 0; len < 100; ++len) {
      final String random = Strings.getRandomAlpha(len);
      assertEquals(random.length(), len);
      assertTrue(random, random.matches("^[a-zA-Z]*$"));
    }
  }

  @Test
  public void testGetRandomAlphaNumericString() {
    try {
      Strings.getRandomAlphaNumeric(-1);
      fail("Expecting an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    for (int len = 0; len < 100; ++len) {
      final String random = Strings.getRandomAlphaNumeric(len);
      assertEquals(random.length(), len);
      assertTrue(random, random.matches("^[0-9a-zA-Z]*$"));
    }
  }

  @Test
  public void testGetRandomNumericString() {
    try {
      Strings.getRandomNumeric(-1);
      fail("Expecting an IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    for (int len = 0; len < 100; ++len) {
      final String random = Strings.getRandomNumeric(len);
      assertEquals(random.length(), len);
      assertTrue(random, random.matches("^[0-9]*$"));
    }
  }

  private static void assertInterpolate(final String expected, final String test, final Map<String,String> properties, final String open, final String close) {
    final String actual = Strings.interpolate(test, properties, open, close);
    assertEquals(expected, actual);
  }

  @Test
  public void testInterpolate() {
    final String open = "{{";
    final String close = "}}";
    final Map<String,String> properties = new HashMap<>();
    properties.put("prop1", "prop1");
    properties.put("prop2", "prop2");
    properties.put("prop3", "prop3");
    properties.put("prop4", "{{prop2}}");
    properties.put("prop5", "{{prop4}} plus {{prop3}}");
    properties.put("prop6", "{{prop5}} plus {{prop6}}");

    assertInterpolate("Bla bla prop1 with prop2 and prop3", "Bla bla {{prop1}} with {{prop2}} and {{prop3}}", properties, open, close);
    assertInterpolate("Bla bla prop2 with prop3 and prop2", "Bla bla {{prop2}} with {{prop3}} and {{prop4}}", properties, open, close);
    assertInterpolate("Bla bla prop3 with prop2 and prop2 plus prop3", "Bla bla {{prop3}} with {{prop4}} and {{prop5}}", properties, open, close);

    try {
      Strings.interpolate("Bla bla {{prop4}} with {{prop5}} and {{prop6}}", properties, "{{", "}}");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      if (!"Loop detected".equals(e.getMessage()))
        throw e;
    }

    try {
      Strings.interpolate(properties, "{{", "}}");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      if (!"Loop detected".equals(e.getMessage()))
        throw e;
    }

    properties.remove("prop6");
    Strings.interpolate(properties, "{{", "}}");
    assertEquals("prop2 plus prop3", properties.get("prop5"));
  }

  private static String testReplace(final StringBuilder builder, final CharSequence target, final CharSequence replacement, final boolean expectReplace) {
    assertEquals(expectReplace, Strings.replace(builder, target, replacement));
    return builder.toString();
  }

  @Test
  public void testReplace() throws Exception {
    assertEquals("xde", testReplace(new StringBuilder("abcde"), "abc", "x", true));
    assertEquals("axde", testReplace(new StringBuilder("abcde"), "bc", "x", true));
    assertEquals("abxe", testReplace(new StringBuilder("abcde"), "cd", "x", true));
    assertEquals("abcx", testReplace(new StringBuilder("abcde"), "de", "x", true));
    assertEquals("xxxxx", testReplace(new StringBuilder("aaaaa"), "a", "x", true));
    assertEquals("xxx", testReplace(new StringBuilder("ababab"), "ab", "x", true));
    assertEquals("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", testReplace(new StringBuilder("ababab"), "ab", "xxxxxxxxxx", true));
  }

  @Test
  public void testChangeCase() throws Exception {
    try {
      Strings.toLowerCase(null, 0, 1);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.toLowerCase(new StringBuilder(UPPER_CASE), 10, 4);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.toLowerCase(new StringBuilder(UPPER_CASE), 12, 13);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.toLowerCase(new StringBuilder(UPPER_CASE), -1, 1);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.toLowerCase(new StringBuilder(UPPER_CASE), -2, -1);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.toLowerCase(new StringBuilder(UPPER_CASE), 1, 12);
      fail("Expected StringIndexOutOfBoundsException");
    }
    catch (final Exception e) {
    }

    assertEquals("", Strings.toLowerCase(new StringBuilder(), 0, 0).toString());
    assertEquals(UPPER_CASE, Strings.toLowerCase(new StringBuilder(UPPER_CASE), 0, 0).toString());
    assertEquals("hELLO WORLD", Strings.toLowerCase(new StringBuilder(UPPER_CASE), 0, 1).toString());
    assertEquals("HeLLO WORLD", Strings.toLowerCase(new StringBuilder(UPPER_CASE), 1, 2).toString());
    assertEquals("HelLO WORLD", Strings.toLowerCase(new StringBuilder(UPPER_CASE), 1, 3).toString());
    assertEquals("HELLO WORLd", Strings.toLowerCase(new StringBuilder(UPPER_CASE), 10, 11).toString());
    assertEquals("HELLO WORld", Strings.toLowerCase(new StringBuilder(UPPER_CASE), 9, 11).toString());
    assertEquals("HELLO WOrld", Strings.toLowerCase(new StringBuilder(UPPER_CASE), 8).toString());
    assertEquals("HELLO world", Strings.toLowerCase(new StringBuilder(UPPER_CASE), 6).toString());

    assertEquals("", Strings.toUpperCase(new StringBuilder(), 0, 0).toString());
    assertEquals(LOWER_CASE, Strings.toLowerCase(new StringBuilder(LOWER_CASE), 0, 0).toString());
    assertEquals("Hello world", Strings.toUpperCase(new StringBuilder(LOWER_CASE), 0, 1).toString());
    assertEquals("hEllo world", Strings.toUpperCase(new StringBuilder(LOWER_CASE), 1, 2).toString());
    assertEquals("hELlo world", Strings.toUpperCase(new StringBuilder(LOWER_CASE), 1, 3).toString());
    assertEquals("hello worlD", Strings.toUpperCase(new StringBuilder(LOWER_CASE), 10, 11).toString());
    assertEquals("hello worLD", Strings.toUpperCase(new StringBuilder(LOWER_CASE), 9, 11).toString());
    assertEquals("hello woRLD", Strings.toUpperCase(new StringBuilder(LOWER_CASE), 8).toString());
    assertEquals("hello WORLD", Strings.toUpperCase(new StringBuilder(LOWER_CASE), 6).toString());
  }

  @Test
  public void testToProperCase() {
    try {
      Strings.toProperCase((String)null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.toProperCase((StringBuilder)null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("", Strings.toProperCase("").toString());
    assertEquals(" ", Strings.toProperCase(" ").toString());
    assertEquals("Hello World", Strings.toProperCase("hello world").toString());
    assertEquals(" Hello World ", Strings.toProperCase(" hello world ").toString());
  }

  @Test
  public void testPad() {
    try {
      Strings.pad(null, LEFT, 0);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.pad(null, RIGHT, 0);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.pad(" ", LEFT, 0);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.pad(" ", RIGHT, 0);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals(" ", Strings.pad(" ", RIGHT, 1));
    assertEquals(" A", Strings.pad("A", RIGHT, 2));
    assertEquals("  A", Strings.pad("A", RIGHT, 3));
    assertEquals("xxxA", Strings.pad("A", RIGHT, 4, 'x'));

    assertEquals(" ", Strings.pad(" ", LEFT, 1));
    assertEquals("A ", Strings.pad("A", LEFT, 2));
    assertEquals("A  ", Strings.pad("A", LEFT, 3));
    assertEquals("Axxx", Strings.pad("A", LEFT, 4, 'x'));

    assertEquals("1234", Strings.pad("12345", LEFT, 4, ' ', true));
    assertEquals("2345", Strings.pad("12345", RIGHT, 4, ' ', true));

    assertEquals("  A\n  A\n  A", Strings.padAll("A\nA\nA", RIGHT, 3));
    assertEquals("A  \nA  \nA  ", Strings.padAll("A\nA\nA", LEFT, 3));
  }

  @Test
  public void testGetAlpha() {
    assertEquals("a", Strings.getAlpha(0));
    assertEquals("aa", Strings.getAlpha(26));
    assertEquals("aaa", Strings.getAlpha(26 * 26 + 26));
    assertEquals("aaaa", Strings.getAlpha(26 * 26 * 26 + 26 * 26 + 26));

    assertEquals("f", Strings.getAlpha(5));
    assertEquals("z", Strings.getAlpha(25));

    assertEquals("ac", Strings.getAlpha(28));
    assertEquals("za", Strings.getAlpha(676));
  }

  @Test
  public void testHex() {
    final long value = 0xabcdef1234567L;
    assertEquals("7", Strings.hex(value, 1));
    assertEquals("67", Strings.hex(value, 2));
    assertEquals("1234567", Strings.hex(value, 7));
    assertEquals("-1234567", Strings.hex(-value, 7));
    assertEquals("00abcdef1234567", Strings.hex(value, 15));
    assertEquals("-00abcdef1234567", Strings.hex(-value, 15));
  }

  @Test
  public void testToUTF8Literal() {
    assertEquals("\\x00", Strings.toUTF8Literal('\0'));
    assertEquals("\\x61", Strings.toUTF8Literal('a'));
    assertEquals("\\x65", Strings.toUTF8Literal('e'));
    assertEquals("\\x7b", Strings.toUTF8Literal('{'));
    assertEquals("\\x0a", Strings.toUTF8Literal('\n'));
    assertEquals("\\x00\\x61\\x65\\x7b\\x0a", Strings.toUTF8Literal("\0ae{\n"));
  }

  @Test
  public void testGetCommonPrefix() {
    assertNull(Strings.getCommonPrefix((String[])null));
    assertNull(Strings.getCommonPrefix((String)null));
    try {
      Strings.getCommonPrefix(null, null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("a", Strings.getCommonPrefix("a"));
    assertEquals("a", Strings.getCommonPrefix(Arrays.asList("a")));

    assertEquals("", Strings.getCommonPrefix("", "b"));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("", "b")));

    assertEquals("", Strings.getCommonPrefix("a", ""));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("a", "")));

    assertEquals("", Strings.getCommonPrefix("a", "b"));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("a", "b")));

    assertEquals("", Strings.getCommonPrefix("aa", "b"));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("aa", "b")));

    assertEquals("", Strings.getCommonPrefix("a", "bb"));
    assertEquals("", Strings.getCommonPrefix(Arrays.asList("a", "bb")));

    assertEquals("a", Strings.getCommonPrefix("aa", "ab"));
    assertEquals("a", Strings.getCommonPrefix(Arrays.asList("aa", "ab")));

    assertEquals("a", Strings.getCommonPrefix("aaa", "ab"));
    assertEquals("a", Strings.getCommonPrefix(Arrays.asList("aaa", "ab")));

    assertEquals("a", Strings.getCommonPrefix("aa", "abb"));
    assertEquals("a", Strings.getCommonPrefix(Arrays.asList("aa", "abb")));

    assertEquals("aa", Strings.getCommonPrefix("aaa", "aab"));
    assertEquals("aa", Strings.getCommonPrefix(Arrays.asList("aaa", "aab")));

    assertEquals("aa", Strings.getCommonPrefix("aaaa", "aab"));
    assertEquals("aa", Strings.getCommonPrefix(Arrays.asList("aaaa", "aab")));

    assertEquals("aa", Strings.getCommonPrefix("aaa", "aabb"));
    assertEquals("aa", Strings.getCommonPrefix(Arrays.asList("aaa", "aabb")));
  }

  @Test
  public void testRepeat() {
    try {
      Strings.repeat(null, 10);
      fail("Expected a IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.repeat("", -1);
      fail("Expected a IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("a", Strings.repeat("a", 1));
    assertEquals("aa", Strings.repeat("a", 2));
    assertEquals("abab", Strings.repeat("ab", 2));
    assertEquals("ab ab ab ", Strings.repeat("ab ", 3));

    try {
      Strings.repeat("abcdefghijklmnopqrstuvwxyz", 353892843);
      fail("Expected an ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }
  }

  @Test
  public void testTrim() {
    assertNull(Strings.trim(null, '\0'));
    assertEquals("foo", Strings.trim("foo", 'x'));
    assertEquals("a", Strings.trim("xa", 'x'));
    assertEquals("a", Strings.trim("xxa", 'x'));
    assertEquals("", Strings.trim("x", 'x'));
    assertEquals("", Strings.trim("xx", 'x'));
    assertEquals("a", Strings.trim("ax", 'x'));
    assertEquals("a", Strings.trim("axx", 'x'));
    assertEquals("string", Strings.trim("xstring", 'x'));
    assertEquals("string", Strings.trim("stringx", 'x'));
    assertEquals("string", Strings.trim("xstringx", 'x'));
    assertEquals("string", Strings.trim("xxstringxx", 'x'));
    assertEquals("string", Strings.trim("xxxstringxxx", 'x'));
    assertEquals("st", Strings.trim("xxxstxxx", 'x'));
    assertEquals("s", Strings.trim("xxxsxxx", 'x'));
    assertEquals("string", Strings.trim("\0string\0", '\0'));
  }

  @Test
  public void testIndexOfUnEscaped() {
    try {
      Strings.indexOfUnEscaped(null, '\0');
      fail("Expected a IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    final String testString = "random a b c \\d d e f d \\d \\\\d \\\\\\d \\\\\\\\d";
    assertEquals(3, Strings.indexOfUnEscaped(testString, 'd'));
    assertEquals(16, Strings.indexOfUnEscaped(testString, 'd', 4));
    assertEquals(22, Strings.indexOfUnEscaped(testString, 'd', 17));
    assertEquals(29, Strings.indexOfUnEscaped(testString, 'd', 23));
    assertEquals(40, Strings.indexOfUnEscaped(testString, 'd', 30));
    assertEquals(-1, Strings.indexOfUnEscaped(testString, 'd', 41));

    assertEquals(28, Strings.indexOfUnEscaped(testString, '\\'));
    assertEquals(32, Strings.indexOfUnEscaped(testString, '\\', 29));
    assertEquals(37, Strings.indexOfUnEscaped(testString, '\\', 33));
    assertEquals(39, Strings.indexOfUnEscaped(testString, '\\', 38));
  }

  @Test
  public void testIndexOfUnQuoted() {
    try {
      Strings.indexOfUnQuoted(null, '\0');
      fail("Expected a IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    final String testString = "random 'x' \"quoted \\'x\\' \\\"t\\\" \\\\\"s\\\\\"\" te'\\''xts";
    assertEquals(testString, -1, Strings.indexOfUnQuoted(testString, '1'));
    assertEquals(testString, 0, Strings.indexOfUnQuoted(testString, 'r'));
    assertEquals(testString, 4, Strings.indexOfUnQuoted(testString, 'o'));
    assertEquals(testString, -1, Strings.indexOfUnQuoted(testString, 'o', 5));
    assertEquals(testString, -1, Strings.indexOfUnQuoted(testString, 'q'));
    assertEquals(testString, 41, Strings.indexOfUnQuoted(testString, 'e'));
    assertEquals(testString, 8, Strings.indexOfUnQuoted(testString, 'x'));
    assertEquals(testString, 34, Strings.indexOfUnQuoted(testString, 's'));

    final String doubleQuote = "\"The \\\"meaning\\\" of life\"";
    assertEquals(doubleQuote.length() - 1, Strings.indexOfUnQuoted(doubleQuote, '"', 1));

    final String singleQuote = "'The \\'meaning\\' of life'";
    assertEquals(singleQuote.length() - 1, Strings.indexOfUnQuoted(singleQuote, '\'', 1));
  }

  @Test
  public void testLastIndexOfUnQuoted() {
    try {
      Strings.indexOfUnQuoted(null, '\0');
      fail("Expected a IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    final String testString = "ran'\\''dom 'n' \"quoted \\'n\\' \\\"d\\\" \\\\\"s\\\\\"\" texts";
    assertEquals(-1, Strings.lastIndexOfUnQuoted(testString, '1'));
    assertEquals(0, Strings.lastIndexOfUnQuoted(testString, 'r'));
    assertEquals(-1, Strings.lastIndexOfUnQuoted(testString, 'q'));
    assertEquals(12, Strings.lastIndexOfUnQuoted(testString, 'n'));
    assertEquals(7, Strings.lastIndexOfUnQuoted(testString, 'd'));
    assertEquals(8, Strings.lastIndexOfUnQuoted(testString, 'o'));
    assertEquals(8, Strings.lastIndexOfUnQuoted(testString, 'o', 9));

    final String doubleQuote = "\"The \\\"meaning\\\" of life\"";
    assertEquals(0, Strings.lastIndexOfUnQuoted(doubleQuote, '"', doubleQuote.length() - 2));

    final String singleQuote = "'The \\'meaning\\' of life'";
    assertEquals(0, Strings.lastIndexOfUnQuoted(singleQuote, '\'', singleQuote.length() - 2));
  }

  @Test
  public void testTruncate() {
    try {
      Strings.truncate("", -1);
      fail("Expected a IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("", Strings.truncate("", 4));
    assertEquals("a", Strings.truncate("a", 4));
    assertEquals("aa", Strings.truncate("aa", 4));
    assertEquals("aaa", Strings.truncate("aaa", 4));
    assertEquals("aaaa", Strings.truncate("aaaa", 4));
    assertEquals("aaaaa", Strings.truncate("aaaaa", 5));
    assertEquals("aa...", Strings.truncate("aaaaaa", 5));
    assertEquals("aaa...", Strings.truncate("aaaaaaa", 6));
    assertEquals("...", Strings.truncate("aaaa", 3));
  }

  private static void assertFlip(final String expected, final String test) {
    final String actual = Strings.flipFirstCap(test);
    assertEquals(expected, actual);
    assertEquals(test, Strings.flipFirstCap(actual));
  }

  @Test
  public void testFlipFirstCap() {
    try {
      Strings.flipFirstCap(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertFlip("Foo", "foo");
    assertFlip("FooBar", "fooBar");
    assertFlip("BAR", "BAR");
    assertFlip("fOO", "fOO");
    assertFlip("BaR", "baR");
    assertFlip("FooBar", "fooBar");
  }

  private static void assertEL(final Map<String,String> variables, final String test, final String match) {
    assertEquals(match, Strings.derefEL(test, variables));
  }

  @Test
  public void testDerefEL() {
    final Map<String,String> variables = new HashMap<>();
    variables.put("left", "LEFT");
    variables.put("right", "RIGHT");
    variables.put("middle", "MIDDLE");

    try {
      Strings.derefEL(null, variables);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.derefEL("foo ${bar}", (Map<String,String>)null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEL(variables, "this string has a token on the $right", "this string has a token on the $right");
    assertEL(variables, "this string has a token on the ${right}", "this string has a token on the RIGHT");
    assertEL(variables, "${left} token here", "LEFT token here");
    assertEL(variables, "something in the ${middle} of this string", "something in the MIDDLE of this string");
    assertEL(variables, "something in the ${left} ${middle} ${right} of this string", "something in the LEFT MIDDLE RIGHT of this string");
    assertEL(variables, "something in the ${left}${middle}${right} of this string", "something in the LEFTMIDDLERIGHT of this string");

    assertEL(variables, "$", "$");
    assertEL(variables, "$$", "$$");
    assertEL(variables, " ${} ", " ${} ");
    assertEL(variables, "a ${} b", "a ${} b");
    assertEL(variables, "a $\\{} b", "a ${} b");
    assertEL(variables, "a $\\{left} b", "a ${left} b");
    assertEL(variables, "a $\\$ b", "a $$ b");
    assertEL(variables, "a $\\$\\$ b", "a $$$ b");
    assertEL(variables, "$left} token here", "$left} token here");
    assertEL(variables, "\\$${left}} token here", "$LEFT} token here");
    assertEL(variables, "\\{${left}} token here", "{LEFT} token here");
    assertEL(variables, "${left}\\ token here", "LEFT token here");
    assertEL(variables, "${le ft} token here", "${le ft} token here");
    assertEL(variables, "${left}\\\\ token here", "LEFT\\ token here");
    assertEL(variables, "${left}\\} token here", "LEFT} token here");
    assertEL(variables, "${left}\\T token here", "LEFTT token here");
    assertEL(variables, "${left\\} token here", "${left} token here");
    assertEL(variables, "${{left}} token here", "${{left}} token here");
    assertEL(variables, "${left token here", "${left token here");
    assertEL(variables, "this string has a token on the ${right", "this string has a token on the ${right");
    assertEL(variables, "this string has a token on the ${", "this string has a token on the ${");
  }

  private static void assertEV(final Map<String,String> variables, final String test, final String match) throws ParseException {
    assertEquals(match, Strings.derefEV(test, variables));
  }

  @Test
  public void testDerefEV() throws ParseException {
    final Map<String,String> variables = new HashMap<>();
    variables.put("LEFT", "left");
    variables.put("RIGHT", "right");
    variables.put("MIDDLE", "middle");

    try {
      Strings.derefEV(null, variables);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.derefEV("foo ${bar}", null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEV(variables, "this string has a token on the $RIGHT", "this string has a token on the right");
    assertEV(variables, "this string has a token on the ${RIGHT}", "this string has a token on the right");
    assertEV(variables, "$LEFT token here", "left token here");
    assertEV(variables, "${LEFT} token here", "left token here");
    assertEV(variables, "something in the $MIDDLE of this string", "something in the middle of this string");
    assertEV(variables, "something in the ${MIDDLE} of this string", "something in the middle of this string");
    assertEV(variables, "something in the $LEFT $MIDDLE $RIGHT of this string", "something in the left middle right of this string");
    assertEV(variables, "something in the ${LEFT} ${MIDDLE} ${RIGHT} of this string", "something in the left middle right of this string");
    assertEV(variables, "something in the ${LEFT}${MIDDLE}${RIGHT} of this string", "something in the leftmiddleright of this string");

    assertEV(variables, "$", "$");
    assertEV(variables, " $ ", " $ ");
    assertEV(variables, "a $ b", "a $ b");
    assertEV(variables, "a $\\$ b", "a $$ b");
    assertEV(variables, "a $\\$\\$ b", "a $$$ b");
    assertEV(variables, "$LEFT} token here", "left} token here");
    assertEV(variables, "\\$$LEFT} token here", "$left} token here");
    assertEV(variables, "\\{$LEFT} token here", "{left} token here");
    assertEV(variables, "$LEFT\\ token here", "left token here");
    assertEV(variables, "$LEFT\\\\ token here", "left\\ token here");
    assertEV(variables, "$LEFT\\} token here", "left} token here");
    assertEV(variables, "$LEFT\\T token here", "leftT token here");

    try {
      assertEV(variables, "${LEFT token here", "left token here");
      fail("Expected a ParseException");
    }
    catch (final ParseException e) {
      if (!"${LEFT : bad substitution".equals(e.getMessage()))
        throw e;
    }

    try {
      assertEV(variables, "this string has a token on the ${RIGHT", "left token here");
      fail("Expected a ParseException");
    }
    catch (final ParseException e) {
      if (!"${RIGHT: bad substitution".equals(e.getMessage()))
        throw e;
    }

    try {
      assertEV(variables, "this string has a token on the ${", "left token here");
      fail("Expected a ParseException");
    }
    catch (final ParseException e) {
      if (!"${: bad substitution".equals(e.getMessage()))
        throw e;
    }

    try {
      assertEV(variables, "${{LEFT}} token here", "left token here");
      fail("Expected a ParseException");
    }
    catch (final ParseException e) {
      if (!"${{: bad substitution".equals(e.getMessage()))
        throw e;
    }

    try {
      Strings.derefEV("expect an $$ here", variables);
      fail("Expected a ParseException");
    }
    catch (final ParseException e) {
      if (!"$$: not supported".equals(e.getMessage()))
        throw e;
    }
  }

  private static StringBuilder s(final String str) {
    return str == null ? null : new StringBuilder(str);
  }

  @Test
  public void testRegionMatches() {
    assertTrue(Strings.regionMatches(s("abc"), false, 0, s("abc"), 0, 3));
    assertTrue(Strings.regionMatches(s("abc"), true, 0, s("ABC"), 0, 3));

    assertTrue(Strings.regionMatches(s("abc"), false, 1, s("bc"), 0, 2));
    assertTrue(Strings.regionMatches(s("abc"), true, 1, s("BC"), 0, 2));

    assertFalse(Strings.regionMatches(s("abc"), false, 0, s("bc"), 0, 2));
    assertFalse(Strings.regionMatches(s("abc"), true, 0, s("BC"), 0, 2));
  }

  @Test
  public void testContainsIgnoreCaseChar() {
    try {
      Strings.containsIgnoreCase(null, '\0');
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertFalse(Strings.containsIgnoreCase("", '\0'));
    assertFalse(Strings.containsIgnoreCase("foo", '\0'));
    assertTrue(Strings.containsIgnoreCase("hElXLo", "X"));
    assertTrue(Strings.containsIgnoreCase("hElXLo", "x"));
  }

  @Test
  public void testContainsIgnoreCaseCharSequence() {
    try {
      Strings.containsIgnoreCase(null, "");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Strings.containsIgnoreCase("", null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertTrue(Strings.containsIgnoreCase("", ""));
    assertTrue(Strings.containsIgnoreCase("hElLo", "hello"));
  }

  @Test
  public void testIntern() {
    final String a = "hello world 1";
    final String b = "hello world 2";
    assertSame(a, Strings.intern(a));
    assertSame(b, Strings.intern(b));

    for (int i = 0; i < 100; ++i) {
      assertSame(a, Strings.intern(new String("hello world 1")));
      assertSame(b, Strings.intern(new String("hello world 2")));
    }
  }

  @Test
  public void testSplit() {
    for (int i = 0; i < 1000000; ++i) {
      String str = Strings.getRandomAlphaNumeric(5);
      final String ch = Strings.getRandomAlphaNumeric(1);
      final char c = ch.charAt(0);
      final String[] expected = str.split(ch);
      final String[] actual = Strings.split(str, c);
      if (!Arrays.equals(expected, actual)) {
        System.err.println(ch + " " + str + " -> " + Arrays.asList(expected) + " " + Arrays.asList(actual));
        Strings.split(str, c);
        assertArrayEquals(ch + " " + str + " -> " + Arrays.asList(expected) + " " + Arrays.asList(actual), expected, actual);
      }
    }
  }
}