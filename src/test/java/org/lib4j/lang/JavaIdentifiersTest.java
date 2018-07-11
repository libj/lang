/* Copyright (c) 2017 lib4j
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

import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

public class JavaIdentifiersTest {
  private static void test(final String expected, final Function<String,String> function, final String value) {
    final String identifier = function.apply(value);
    if (identifier != null && identifier.length() > 0)
      Assert.assertTrue(JavaIdentifiers.isValid(identifier));

    Assert.assertEquals(expected, identifier);
  }

  @Test
  public void testIsValid() {
    try {
      JavaIdentifiers.isValid(null);
      Assert.fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }

    Assert.assertFalse(JavaIdentifiers.isValid(""));
    Assert.assertFalse(JavaIdentifiers.isValid(".A"));
    Assert.assertTrue(JavaIdentifiers.isValid("A.A"));
    Assert.assertTrue(JavaIdentifiers.isValid(JavaIdentifiersTest.class.getName()));
    Assert.assertFalse(JavaIdentifiers.isValid(JavaIdentifiersTest.class.getName(), false));
    Assert.assertTrue(JavaIdentifiers.isValid(JavaIdentifiersTest.class.getSimpleName(), false));
  }

  @Test
  public void testIdentifier() {
    test(null, JavaIdentifiers::toIdentifier, null);
    test("", JavaIdentifiers::toIdentifier, "");
    test("foo$bar", JavaIdentifiers::toIdentifier, "foo$bar");
    test("foo_bar", JavaIdentifiers::toIdentifier, "foo_bar");
    test("foobar", JavaIdentifiers::toIdentifier, "foo-bar");
    test("foobar", JavaIdentifiers::toIdentifier, "foo*bar");
    test("foobar", JavaIdentifiers::toIdentifier, "foo%bar");
    test("foobar", JavaIdentifiers::toIdentifier, "foo bar");
    test("foobar", JavaIdentifiers::toIdentifier, "foo bar");
    test("_2foobar", JavaIdentifiers::toIdentifier, "2foo bar");
    test("_abstract", JavaIdentifiers::toIdentifier, "abstract");
    test("_do", JavaIdentifiers::toIdentifier, "do");
    test("_foo", JavaIdentifiers::toIdentifier, "_foo");
  }

  @Test
  public void testToPackageCase() {
    test(null, JavaIdentifiers::toPackageCase, null);
    test("", JavaIdentifiers::toPackageCase, "");
    test("hyphenated_name", JavaIdentifiers::toPackageCase, "hyphenated-name");
    test("int_", JavaIdentifiers::toPackageCase, "int");
    test("_123name", JavaIdentifiers::toPackageCase, "123name");
  }

  @Test
  public void testToCamelCase() {
    test(null, JavaIdentifiers::toCamelCase, null);
    test("", JavaIdentifiers::toCamelCase, "");
    test("fooBar", JavaIdentifiers::toCamelCase, "_foo_bar");
    test("fooBar", JavaIdentifiers::toCamelCase, "foo_bar");
    test("FOOBarFoO", JavaIdentifiers::toCamelCase, "FOO_bar_foO");
    test("foo", JavaIdentifiers::toCamelCase, "foo_");
    test("FOo", JavaIdentifiers::toCamelCase, "FOo");
    test("FOoBAr", JavaIdentifiers::toCamelCase, "FOo_bAr");
    test("x2FOoBAr", JavaIdentifiers::toCamelCase, "2FOo_bAr");
    test("xabstract", JavaIdentifiers::toCamelCase, "abstract");
    test("xdo", JavaIdentifiers::toCamelCase, "do");
  }

  @Test
  public void testToInstanceCase() {
    test(null, JavaIdentifiers::toInstanceCase, null);
    test("", JavaIdentifiers::toInstanceCase, "");
    test("a", JavaIdentifiers::toInstanceCase, "a");
    test("xml", JavaIdentifiers::toInstanceCase, "XML");
    test("a", JavaIdentifiers::toInstanceCase, "A");
    test("fooBar", JavaIdentifiers::toInstanceCase, "_foo_bar");
    test("fooBar", JavaIdentifiers::toInstanceCase, "foo_bar");
    test("fooBarFoO", JavaIdentifiers::toInstanceCase, "FOO_bar_foO");
    test("foo", JavaIdentifiers::toInstanceCase, "foo_");
    test("fOo", JavaIdentifiers::toInstanceCase, "FOo");
    test("fOoBAr", JavaIdentifiers::toInstanceCase, "FOo_bAr");
    test("_2FOoBAr", JavaIdentifiers::toInstanceCase, "2FOo_bAr");
    test("_abstract", JavaIdentifiers::toInstanceCase, "abstract");
    test("_do", JavaIdentifiers::toInstanceCase, "do");
  }

  @Test
  public void testToClassCase() {
    test(null, JavaIdentifiers::toClassCase, null);
    test("", JavaIdentifiers::toClassCase, "");
    test("FooBar", JavaIdentifiers::toClassCase, "_foo_bar");
    test("FooBar", JavaIdentifiers::toClassCase, "foo_bar");
    test("FOOBarFoO", JavaIdentifiers::toClassCase, "FOO_bar_foO");
    test("Foo", JavaIdentifiers::toClassCase, "foo_");
    test("FOo", JavaIdentifiers::toClassCase, "FOo");
    test("FOoBAr", JavaIdentifiers::toClassCase, "FOo_bAr");
    test("X2FOoBAr", JavaIdentifiers::toClassCase, "2FOo_bAr");
    test("Abstract", JavaIdentifiers::toClassCase, "abstract");
    test("Do", JavaIdentifiers::toClassCase, "do");
  }
}