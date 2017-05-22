/* Copyright (c) 2015 lib4j
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

package org.safris.commons.lang;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class ObjectsTest {
  private static final Logger logger = LoggerFactory.getLogger(ObjectsTest.class);

  private static enum E {
    a, b, c
  }

  private static class A {
    private final boolean bool = false;
    private transient byte b = 0x55;
    private volatile char c = 'c';
    private short s = (short)35;
    private int i = 439802;
    private long l = 382948392L;
    private float f = 3234.34923f;
    private double d = 38923432432.438498239d;
  }

  private static class B extends A {
    @NotHashable
    @NotEqualable
    private final Object o;

    public B(final String o) {
      this.o = o;
    }

    public B() {
      this("b");
    }
  }

  private static class C extends B {
    private final E e;

    public C(final E e) {
      this.e = e;
    }
  }

  private static class D extends C {
    @Equalable
    @Hashable
    private final Integer i = null;

    public D(final E e) {
      super(e);
    }
  }

  private static class F extends D {
    public F() {
      super(null);
    }
  }

  @Test
  public void testHashCode() {
    final int a = Objects.hashCode(new A());
    final int b = Objects.hashCode(new B());
    final int c = Objects.hashCode(new C(E.a));
    Assert.assertEquals(a, b);
    Assert.assertNotEquals(a, c);
    Assert.assertEquals(Objects.hashCode(new D(E.a)), Objects.hashCode(new D(E.b)));
  }

  @Test
  public void testEquals() {
    Assert.assertTrue(Objects.equals(new A(), new A()));
    Assert.assertTrue(Objects.equals(new B("a"), new B("b")));
    Assert.assertFalse(Objects.equals(new C(E.a), new C(E.b)));
    Assert.assertTrue(Objects.equals(new D(E.a), new D(E.b)));
  }

  @Test
  public void testToStrings() {
    logger.info(Objects.toString(new A()));
    logger.info(Objects.toString(new B()));
    logger.info(Objects.toString(new C(E.a)));
    logger.info(Objects.toString(new D(E.b)));
    logger.info(Objects.toString(new F()));
  }
}