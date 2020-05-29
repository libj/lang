/* Copyright (c) 2016 LibJ
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

import java.util.Enumeration;

import org.junit.Test;

public class EnumerationTest {
  private final Enumeration<Integer> enumeration = new Enumeration<Integer>() {
    private int position;

    @Override
    public boolean hasMoreElements() {
      return position < 10;
    }

    @Override
    public Integer nextElement() {
      return position++;
    }
  };

  @Test
  public void testToArray() {
    final Integer[] array = Enumerations.toArray(Integer.class, enumeration);
    assertArrayEquals(new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
  }
}