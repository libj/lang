/* Copyright (c) 2023 LibJ
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
import static org.libj.lang.DeclarativeOrderPolicy.*;

import java.lang.reflect.Method;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class DeclarativeOrderTest2 {
  private static int index = 0;

  void assertIndex(final String sig) throws NoSuchMethodException {
    System.err.println(sig);
    final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    final Method method = getClass().getMethod(stackTraceElements[2].getMethodName());
    final ExpectOrders expectOrders = method.getAnnotation(ExpectOrders.class);
    for (final ExpectOrder expectOrder : expectOrders.value())
      if (expectOrder.policy() == PUBLIC_SUPER_FIRST)
        assertEquals(index + "  " + method.getDeclaringClass().getName() + ":" + method.getName() + " " + expectOrder.order(), expectOrder.order(), index);

    ++index;
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 0)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 5)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 8)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 11)
  public void test0() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest2.class.getName() + ":test0");
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 1)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 9)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 0)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 12)
  public final void test1() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest2.class.getName() + ":test1");
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 2)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 4)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 7)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 13)
  public void test2() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest2.class.getName() + ":test2");
  }
}