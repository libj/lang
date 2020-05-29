/* Copyright (c) 2017 LibJ
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

import java.util.Collections;
import java.util.function.Function;

import org.junit.Test;

public class IdentifiersTest {
  private static void test(final String expected, final Function<? super String,String> function, final String value) {
    final String identifier;
    try {
      identifier = function.apply(value);
    }
    catch (final NullPointerException e) {
      if (expected == null)
        return;

      throw e;
    }

    if (identifier != null && identifier.length() > 0)
      assertTrue(expected + " != " + identifier, Identifiers.isValid(identifier));

    assertEquals(expected, identifier);
  }

  @Test
  public void testIsValid() {
    try {
      Identifiers.isValid(null);
      fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    assertFalse(Identifiers.isValid(""));
    assertFalse(Identifiers.isValid(".A"));
    assertTrue(Identifiers.isValid("A.A"));
    assertTrue(Identifiers.isValid(IdentifiersTest.class.getName()));
    assertFalse(Identifiers.isValid(IdentifiersTest.class.getName(), false));
    assertTrue(Identifiers.isValid(IdentifiersTest.class.getSimpleName(), false));
  }

  @Test
  public void testIdentifier() {
    final Function<String,String> function = Identifiers::toIdentifier;
    test("a", function, "a");
    test("XML", function, "XML");
    test("A", function, "A");
    test("HelloWorld", function, "HelloWorld");

    try {
      Identifiers.toIdentifier("@foo", Collections.singletonMap('@', "1!"));
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }
    try {
      Identifiers.toIdentifier("@foo", '\0', c -> c == '@' ? "1!" : null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }
    test("foo", function, "@fo@o");
    test("foo", function, "@foo");
    assertEquals("$foo", Identifiers.toIdentifier("@foo", '_', '$'));
    assertEquals("_1$foo", Identifiers.toIdentifier("@foo", Collections.singletonMap('@', "1$")));
    assertEquals("_1$foo", Identifiers.toIdentifier("@foo", c -> c == '@' ? "1$" : null));
    assertEquals("$1foo", Identifiers.toIdentifier("@foo", Collections.singletonMap('@', "$1")));
    assertEquals("$1foo", Identifiers.toIdentifier("@foo", c -> c == '@' ? "$1" : null));

    test(null, function, null);
    test("", function, "");
    test("foo$bar", function, "foo$bar");
    test("foo_bar", function, "foo_bar");
    test("foobar", function, "foo-bar");
    test("foobar", function, "foo*bar");
    test("foobar", function, "foo%bar");
    test("foobar", function, "foo bar");
    test("foobar", function, "foo bar");
    test("_2foobar", function, "2foo bar");
    test("_abstract", function, "abstract");
    test("_do", function, "do");
    test("_foo", function, "_foo");

    try {
      Identifiers.toIdentifier("@foo", '\0', c -> "1$");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      assertEquals("Substitution \"1$\" contains illegal start character: '1'", e.getMessage());
    }

    try {
      Identifiers.toIdentifier("class", '\0');
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
      assertTrue(e.getMessage().startsWith("Unable to transform"));
    }

    assertEquals("_class", Identifiers.toIdentifier("class", '\0', Collections.singletonMap(null, "_")));
    assertEquals("_class", Identifiers.toIdentifier("class", '\0', c -> c == null ? "_" : null));
  }

  @Test
  public void testUnicode() {
    final char prefix = '\0';
    final Function<Character,String> substitutions = c -> c == null ? "_" : c != '_' ? "_" + Integer.toHexString(c) : "__";
    assertEquals("__", Identifiers.toInstanceCase("_", prefix, substitutions));
    assertEquals("_2e_2a", Identifiers.toInstanceCase(".*", prefix, substitutions));
  }

  @Test
  public void testToPackageCase() {
    final Function<String,String> function = Identifiers::toPackageCase;
    test(null, function, null);
    test("", function, "");
    test("hyphenated_name", function, "hyphenated-name");
    test("int_", function, "int");
    test("_123name", function, "123name");
  }

  @Test
  public void testToCamelCase() {
    final Function<String,String> function = Identifiers::toCamelCase;
    test(null, function, null);
    test("", function, "");
    test("a", function, "a");
    test("XML", function, "XML");
    test("A", function, "A");
    test("elementXPath", function, "elementXPath");
    test("HelloWorld", function, "HelloWorld");
    test("fooBar", function, "_foo_bar");
    test("fooBar", function, "foo_bar");
    test("FooBarFoO", function, "FOO_bar_foO");
    test("foo", function, "foo_");
    test("FOo", function, "FOo");
    test("FOoBAr", function, "FOo_bAr");
    test("x2FOoBAr", function, "2FOo_bAr");
    test("xabstract", function, "abstract");
    test("xdo", function, "do");
  }

  @Test
  public void testToInstanceCase() {
    final Function<String,String> function = Identifiers::toInstanceCase;
    test(null, function, null);
    test("", function, "");
    test("a", function, "a");
    test("xml", function, "XML");
    test("a", function, "A");
    test("elementXPath", function, "elementXPath");
    test("helloWorld", function, "HelloWorld");
    test("fooBar", function, "_foo_bar");
    test("fooBar", function, "foo_bar");
    test("fooBarFoO", function, "FOO_bar_foO");
    test("foo", function, "foo_");
    test("fOo", function, "FOo");
    test("fOoBAr", function, "FOo_bAr");
    test("_2FOoBAr", function, "2FOo_bAr");
    test("_abstract", function, "abstract");
    test("_do", function, "do");
    test("_2e2a", function, "2e_2a");
    test("_2e2a", function, "_2e_2a");
  }

  @Test
  public void testToClassCase() {
    final Function<String,String> function = Identifiers::toClassCase;
    test(null, function, null);
    test("", function, "");
    test("A", function, "a");
    test("Xml", function, "XML");
    test("A", function, "A");
    test("ElementXPath", function, "elementXPath");
    test("HelloWorld", function, "HelloWorld");
    test("FooBar", function, "_foo_bar");
    test("FooBar", function, "foo_bar");
    test("FooBarFoO", function, "FOO_bar_foO");
    test("Foo", function, "foo_");
    test("FOo", function, "FOo");
    test("FOoBAr", function, "FOo_bAr");
    test("X2FOoBAr", function, "2FOo_bAr");
    test("Abstract", function, "abstract");
    test("Do", function, "do");
  }
}