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

package org.lib4j.lang;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.junit.Assert;
import org.junit.Test;

public class ArraysTest {
  @Test
  public void testTransform() {
    Assert.assertArrayEquals(new String[] {"ONE", "TWO", "THREE"}, Arrays.<String>replaceAll(new UnaryOperator<String>() {
      @Override
      public String apply(final String value) {
        return value.toUpperCase();
      }
    }, new String[] {"one", "two", "three"}));

    Assert.assertArrayEquals(new String[] {}, Arrays.<String>replaceAll(new UnaryOperator<String>() {
      @Override
      public String apply(final String value) {
        return value.toUpperCase();
      }
    }, new String[] {}));
  }

  @Test
  public void testFilter() {
    final String[] expected = new String[] {"ONE", "TWO", "THREE"};
    final String[] filtered = Arrays.filter(new Predicate<String>() {
      @Override
      public boolean test(final String value) {
        return value != null;
      }
    }, new String[] {"ONE", null, "TWO", null, "THREE"});
    Assert.assertArrayEquals(expected, filtered);
  }

  @Test
  public void testConcat() {
    final String[] one = new String[] {"a", "b", "c"};
    final String[] two = new String[] {"d", "e", "f"};
    final String[] concat = Arrays.concat(one, two);
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "e", "f"}, concat);
  }

  @Test
  public void testSubArray() {
    final String[] array = new String[] {"a", "b", "c", "d", "e", "f"};
    Assert.assertArrayEquals(new String[] {"c", "d", "e"}, Arrays.subArray(array, 2, 5));
    Assert.assertArrayEquals(new String[] {"c", "d", "e", "f"}, Arrays.subArray(array, 2));
  }

  @Test
  public void testSplice() {
    final String[] array = new String[] {"a", "b", "c", "d", "e", "f"};

    Assert.assertArrayEquals(new String[] {"c", "d", "e", "f"}, Arrays.splice(array, 0, 2));
    Assert.assertArrayEquals(new String[] {"a", "d", "e", "f"}, Arrays.splice(array, 1, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "e", "f"}, Arrays.splice(array, 2, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "f"}, Arrays.splice(array, 3, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d"}, Arrays.splice(array, 4, 2));
    try {
      Arrays.splice(array, 5, 2);
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }

    try {
      Arrays.splice(array, -2, 3);
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d"}, Arrays.splice(array, -2, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "f"}, Arrays.splice(array, -3, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "e", "f"}, Arrays.splice(array, -4, 2));
    Assert.assertArrayEquals(new String[] {"a", "d", "e", "f"}, Arrays.splice(array, -5, 2));
    Assert.assertArrayEquals(new String[] {"c", "d", "e", "f"}, Arrays.splice(array, -6, 2));
    try {
      Arrays.splice(array, -7, 2);
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }

    Assert.assertArrayEquals(new String[] {}, Arrays.splice(array, 0));
    Assert.assertArrayEquals(new String[] {"a"}, Arrays.splice(array, 1));
    Assert.assertArrayEquals(new String[] {"a", "b"}, Arrays.splice(array, 2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c"}, Arrays.splice(array, 3));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d"}, Arrays.splice(array, 4));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "e"}, Arrays.splice(array, 5));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "e", "f"}, Arrays.splice(array, 6));
    try {
      Arrays.splice(array, 7);
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }

    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "e"}, Arrays.splice(array, -1));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d"}, Arrays.splice(array, -2));
    Assert.assertArrayEquals(new String[] {"a", "b", "c"}, Arrays.splice(array, -3));
    Assert.assertArrayEquals(new String[] {"a", "b"}, Arrays.splice(array, -4));
    Assert.assertArrayEquals(new String[] {"a"}, Arrays.splice(array, -5));
    Assert.assertArrayEquals(new String[] {}, Arrays.splice(array, -6));
    try {
      Arrays.splice(array, -7);
      Assert.fail("Expected NegativeArraySizeException");
    }
    catch (final NegativeArraySizeException e) {
    }

    Assert.assertArrayEquals(new String[] {"x", "y", "z", "c", "d", "e", "f"}, Arrays.splice(array, 0, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "x", "y", "z", "d", "e", "f"}, Arrays.splice(array, 1, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "x", "y", "z", "e", "f"}, Arrays.splice(array, 2, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "x", "y", "z", "f"}, Arrays.splice(array, 3, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "x", "y", "z"}, Arrays.splice(array, 4, 2, "x", "y", "z"));
    try {
      Arrays.splice(array, 5, 2, "x", "y", "z");
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }

    Assert.assertArrayEquals(new String[] {"a", "b", "c", "d", "x", "y", "z"}, Arrays.splice(array, -2, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "c", "x", "y", "z", "f"}, Arrays.splice(array, -3, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "b", "x", "y", "z", "e", "f"}, Arrays.splice(array, -4, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"a", "x", "y", "z", "d", "e", "f"}, Arrays.splice(array, -5, 2, "x", "y", "z"));
    Assert.assertArrayEquals(new String[] {"x", "y", "z", "c", "d", "e", "f"}, Arrays.splice(array, -6, 2, "x", "y", "z"));
    try {
      Arrays.splice(array, -7, 2, "x", "y", "z");
      Assert.fail("Expected ArrayIndexOutOfBoundsException");
    }
    catch (final ArrayIndexOutOfBoundsException e) {
    }
  }
}