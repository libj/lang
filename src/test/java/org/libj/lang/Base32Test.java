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

import static org.junit.Assert.*;

import org.junit.Test;

public class Base32Test {
  String encode(final byte[] data, final boolean padding, final boolean uppercase) {
    return Base32.encode(data, padding, uppercase);
  }

  byte[] decode(final String base32, final boolean padding) {
    return Base32.decode(base32, padding);
  }

  private void test(final boolean padding, final boolean uppercase) {
    for (int i = 0; i < 200; ++i) { // [N]
      final byte[] arg = Strings.getRandomAlphaNumeric((int)(Math.random() * i * i)).getBytes();
      final String encoded = encode(arg, padding, uppercase);
      final byte[] decoded = decode(encoded, padding);
      assertArrayEquals(arg, decoded);
    }
  }

  @Test
  public void test() {
    test(true, true);
    test(true, false);
    test(false, true);
    test(false, false);
  }
}