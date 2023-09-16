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

import java.lang.reflect.Method;

import org.junit.Test;

public class DeclarativeOrderTest {
  private static void test(final DeclarativeOrderPolicy declarativeOrderPolicy) throws ClassNotFoundException {
    final Method[] methods = declarativeOrderPolicy.getMethods(DeclarativeOrderTest0.class, Test.class);
    assertTrue(Classes.sortDeclarativeOrder(methods, declarativeOrderPolicy.isSuperFirst()));
    for (int i = 0, i$ = methods.length; i < i$; ++i) {
      final Method method = methods[i];
      final ExpectOrders expectOrders = method.getAnnotation(ExpectOrders.class);
      for (final ExpectOrder expectOrder : expectOrders.value())
        if (expectOrder.policy() == declarativeOrderPolicy)
          assertEquals(i + "  " + method.getDeclaringClass().getName() + ":" + method.getName() + " " + expectOrder.order(), expectOrder.order(), i);
    }
  }

  @Test
  public void test() throws ClassNotFoundException {
    test(DeclarativeOrderPolicy.DECLARED_SUPER_LAST);
  }

  @Test
  public void testSuperFirstDeclared() throws ClassNotFoundException {
    test(DeclarativeOrderPolicy.DECLARED_SUPER_FIRST);
  }

  @Test
  public void testSuperFirstRespectOverride() throws ClassNotFoundException {
    test(DeclarativeOrderPolicy.PUBLIC_SUPER_FIRST);
  }

  @Test
  public void testSuperLastRespectOverride() throws ClassNotFoundException {
    test(DeclarativeOrderPolicy.PUBLIC_SUPER_LAST);
  }
}