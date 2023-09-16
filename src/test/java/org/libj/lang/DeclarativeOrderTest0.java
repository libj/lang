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

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class DeclarativeOrderTest0 extends DeclarativeOrderTest1 {
  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 7)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 0)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 3)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 0)
  public void test6() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest0.class.getName() + ":test6");
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 8)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 1)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 4)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 1)
  public void test7() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest0.class.getName() + ":test7");
  }

  @Test
  @Override
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 9)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 2)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 5)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 2)
  public void test5() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest0.class.getName() + ":test5");
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 10)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 3)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 6)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 3)
  public void test8() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest0.class.getName() + ":test8");
  }

  @Test
  @Override
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 11)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 4)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 7)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 4)
  public void test2() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest0.class.getName() + ":test2");
  }

  @Test
  @Override
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 12)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 5)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 8)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 5)
  public void test0() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest0.class.getName() + ":test0");
  }

  @Test
  @ExpectOrder(policy = DECLARED_SUPER_FIRST, order = 13)
  @ExpectOrder(policy = PUBLIC_SUPER_LAST, order = 6)
  @ExpectOrder(policy = PUBLIC_SUPER_FIRST, order = 9)
  @ExpectOrder(policy = DECLARED_SUPER_LAST, order = 6)
  public void test9() throws NoSuchMethodException {
    assertIndex(DeclarativeOrderTest0.class.getName() + ":test9");
  }
}