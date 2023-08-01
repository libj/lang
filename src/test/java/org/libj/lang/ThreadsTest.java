/* Copyright (c) 2021 LibJ
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

import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ThreadsTest {
  static {
    Thread.setDefaultUncaughtExceptionHandler((final Thread t, final Throwable e) -> {
      e.printStackTrace();
      System.err.flush();
      System.exit(1);
    });
  }

  @Test
  public void test() {
    Threads.printThreadTrace();
  }

  @Test
  public void testPrintWriter() {
    Threads.printThreadTrace(new PrintWriter(System.err));
  }

  @Test
  public void testPrintStream() {
    Threads.printThreadTrace(System.err);
  }

  @Test
  public void testConsumer() {
    Threads.printThreadTrace(System.err::println);
  }

  public static class Task implements Runnable {
    private final CountDownLatch latch;
    private final long sleepTime;
    private final long timeout;

    public Task(final CountDownLatch latch, final long sleepTime, final long timeout) {
      this.latch = latch;
      this.sleepTime = sleepTime;
      this.timeout = timeout;
    }

    @Override
    public void run() {
      final long ts = System.currentTimeMillis();
      try {
        Thread.sleep(sleepTime);
        final long runtime = System.currentTimeMillis() - ts;
        assertTrue("timeout (" + timeout + ") >= " + "sleepTime (" + sleepTime + ")", timeout >= sleepTime);
        assertTrue("runtime (" + runtime + ") - " + "sleepTime (" + sleepTime + ") < 10", runtime - sleepTime < 20);
      }
      catch (final InterruptedException e) {
        final long runtime = System.currentTimeMillis() - ts;
        assertTrue("timeout (" + timeout + ") <= " + "sleepTime (" + sleepTime + ")", timeout <= sleepTime);
        assertTrue("timeout (" + timeout + ") - " + "runtime (" + runtime + ") < 5", timeout - runtime < 10);
      }
      finally {
        latch.countDown();
      }
    }
  }

  private static final Random r = new Random();
  private static final int numTests = 500;

  public static Runnable newRandomRunnable(final CountDownLatch latch) {
    final long sleepTime = 100 + r.nextInt(500);
    final long delta = 50 + r.nextInt(50);
    final long timeout = r.nextBoolean() ? sleepTime - delta : sleepTime + delta;
    return newRunnable(latch, sleepTime, timeout);
  }

  public static Runnable newRunnable(final CountDownLatch latch, final long sleep, final long timeout) {
    return Threads.interruptAfterTimeout(new Task(latch, sleep, timeout), timeout, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testInterruptAfterTimeout() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(numTests);
    for (int i = 0; i < numTests; ++i) { // [N]
      Thread.sleep(2);
      if (i % 100 == 0)
        System.err.println();

      System.err.print('.');
      System.err.flush();
      new Thread(newRandomRunnable(latch)).start();
    }

    latch.await();
  }
}