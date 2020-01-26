/* Copyright (c) 2020 LibJ
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

package org.libj.lang.reflect;

import static org.junit.Assert.*;
import static org.libj.lang.ClassesTest.*;

import org.junit.Test;
import org.libj.lang.ClassesTest.A;
import org.libj.lang.ClassesTest.B;

@SuppressWarnings("all")
public class WrapperProxyTest {
  @Test
  public void testFoo() {
    final B proxyB = WrapperProxy.wrap(foo, b);
    assertTrue(proxyB instanceof A);
    proxyB.a();
    proxyB.b();

    final A proxyA = WrapperProxy.wrap(foo, a);
    assertTrue(proxyA instanceof B);
    proxyA.a();
    ((B)proxyA).b();
  }
}