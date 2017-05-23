/* Copyright (c) 2012 lib4j
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

import org.junit.Assert;
import org.junit.Test;

public class IntArrayListTest {
  @Test
  public void test() {
    final IntArrayList list = new IntArrayList();
    Assert.assertTrue(list.isEmpty());
    list.add(1);
    list.add(2);
    list.add(3);
    list.add(4);
    list.add(5);
    Assert.assertEquals(1, list.get(0));
    Assert.assertEquals(2, list.get(1));
    Assert.assertEquals(3, list.get(2));
    Assert.assertEquals(4, list.get(3));
    Assert.assertEquals(5, list.get(4));

    try {
      list.get(5);
      Assert.fail("Expected IndexOutOfBoundsException");
    }
    catch (final IndexOutOfBoundsException e) {
    }

    list.addAll(6, 7, 8, 9, 10);
    Assert.assertEquals(6, list.get(5));
    Assert.assertEquals(7, list.get(6));
    Assert.assertEquals(8, list.get(7));
    Assert.assertEquals(9, list.get(8));
    Assert.assertEquals(10, list.get(9));

    list.set(0, 7);
    Assert.assertEquals(7, list.get(0));

    Assert.assertEquals(0, list.indexOf(7));
    Assert.assertEquals(6, list.lastIndexOf(7));

    list.add(3, 99);
    Assert.assertArrayEquals(new int[] {7, 2, 3, 99, 4, 5, 6, 7, 8, 9, 10}, list.toArray());
  }
}