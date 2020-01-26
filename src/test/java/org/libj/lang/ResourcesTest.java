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

package org.libj.lang;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcesTest {
  private static final Logger logger = LoggerFactory.getLogger(ResourcesTest.class);

  @Test
  public void testListJUnit() throws IOException {
    final AtomicInteger counter = new AtomicInteger();
    Resources.list(ClassLoader.getSystemClassLoader(), "junit/", (u, p, d) -> {
      logger.debug(p);
      counter.getAndIncrement();
      return true;
    });
    assertEquals(5, counter.get());
  }

  @Test
  public void testWalkJUnit() throws IOException {
    final AtomicInteger counter = new AtomicInteger();
    Resources.walk(ClassLoader.getSystemClassLoader(), "junit/", (u, p, d) -> {
      logger.debug(p);
      counter.getAndIncrement();
      return true;
    });
    assertEquals(35, counter.get());
  }

  @Test
  public void testListJUnitRunner() throws IOException {
    final AtomicInteger counter = new AtomicInteger();
    Resources.list(ClassLoader.getSystemClassLoader(), "junit/runner/", (u, p, d) -> {
      logger.debug(p);
      counter.getAndIncrement();
      return true;
    });
    assertEquals(6, counter.get());
  }

  @Test
  public void testWalkJUnitRunner() throws IOException {
    final AtomicInteger counter = new AtomicInteger();
    Resources.walk(ClassLoader.getSystemClassLoader(), "junit/runner/", (u, p, d) -> {
      logger.debug(p);
      counter.getAndIncrement();
      return true;
    });
    assertEquals(6, counter.get());
  }

  @Test
  public void testListLibJRunner() throws IOException {
    final AtomicInteger counter = new AtomicInteger();
    Resources.list(ClassLoader.getSystemClassLoader(), "org/junit/", (u, p, d) -> {
      logger.debug(u + " " + p);
      counter.getAndIncrement();
      return true;
    });
    assertEquals(28, counter.get());
  }

  @Test
  public void testWalkLibJRunner() throws IOException {
    final AtomicInteger counter = new AtomicInteger();
    Resources.walk(ClassLoader.getSystemClassLoader(), "org/junit/", (u, p, d) -> {
      logger.debug(p);
      counter.getAndIncrement();
      return true;
    });
    assertEquals(288, counter.get());
  }
}