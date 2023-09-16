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

import static org.libj.lang.DeclarativeOrderPolicy.*;

import org.junit.Test;

abstract class DeclarativeOrderTest1 extends DeclarativeOrderTest2 {
  @Test
  @Override
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 3)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 4)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 7)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 7)
  public void test2() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest1.class.getName() + ":test2");
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 4)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 7)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 1)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 8)
  public final void test3() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest1.class.getName() + ":test3");
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 5)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 8)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 2)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 9)
  public final void test4() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest1.class.getName() + ":test4");
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 6)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 2)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 5)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 10)
  public void test5() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest1.class.getName() + ":test5");
  }
}