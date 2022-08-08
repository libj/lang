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

import static org.libj.lang.Assertions.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Utility functions that provide common operations pertaining to {@link Thread}.
 */
public final class Threads {
  /**
   * Prints all threads and the full stack trace of code running on each thread to the standard error stream.
   */
  public static void printThreadTrace() {
    printThreadTrace(System.err);
  }

  /**
   * Prints all threads and the full stack trace of code running on each thread to the specified print writer.
   *
   * @param s {@link PrintWriter} to use for output.
   * @throws IllegalArgumentException If {@code s} is null.
   */
  public static void printThreadTrace(final PrintWriter s) {
    printThreadTrace(assertNotNull(s)::println);
  }

  /**
   * Prints all threads and the full stack trace of code running on each thread to the specified print stream.
   *
   * @param s {@link PrintStream} to use for output.
   * @throws IllegalArgumentException If {@code s} is null.
   */
  public static void printThreadTrace(final PrintStream s) {
    printThreadTrace(assertNotNull(s)::println);
  }

  /**
   * Prints all threads and the full stack trace of code running on each thread to the specified string consumer.
   *
   * @param s {@link Consumer Consumer&lt;String&gt;} to use for output.
   * @throws IllegalAnnotationException If {@code s} is null.
   */
  public static void printThreadTrace(final Consumer<String> s) {
    assertNotNull(s);
    final Map<Thread,StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
    final Map<Long,Thread> tidToThread = new HashMap<>(stackTraces.size());
    for (final Thread thread : stackTraces.keySet()) // [S]
      tidToThread.put(thread.getId(), thread);

    final StringBuilder builder = new StringBuilder();
    final ThreadInfo[] threadInfos = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
    for (int i = 0; i < threadInfos.length; ++i) { // [A]
      if (builder.length() > 0)
        builder.append("\n\n");

      final ThreadInfo threadInfo = threadInfos[i];
      final Thread thread = tidToThread.get(threadInfo.getThreadId());
      if (thread == null)
        continue;

      builder.append('"').append(threadInfo.getThreadName()).append("\" #").append(threadInfo.getThreadId());
      if (thread.isDaemon())
        builder.append(" daemon");

      builder.append(" prio=").append(thread.getPriority());
      builder.append("\n   java.lang.Thread.State: ").append(threadInfo.getThreadState());
      for (final StackTraceElement stackTraceElement : threadInfo.getStackTrace()) // [A]
        builder.append("\n  at ").append(stackTraceElement);
    }

    s.accept(builder.toString());
  }

  private static class ReaperThread extends Thread {
    private static final AtomicLong entrySequence = new AtomicLong(0);

    private static class Entry implements Comparable<Entry> {
      private final long sequence;
      private final Thread thread;
      private final long expireTime;

      private Entry(final Thread thread, final long expireTime) {
        this.sequence = entrySequence.getAndIncrement();
        this.thread = thread;
        this.expireTime = expireTime;
      }

      private boolean isExpired() {
        return System.currentTimeMillis() >= expireTime;
      }

      @Override
      public int compareTo(final Entry o) {
        final int c = Long.compare(expireTime, o.expireTime);
        return c != 0 ? c : Long.compare(sequence, o.sequence);
      }
    }

    private final PriorityBlockingQueue<Entry> queue = new PriorityBlockingQueue<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private ReaperThread() {
      start();
      try {
        lock.lock();
        condition.await();
        lock.unlock();
      }
      catch (final InterruptedException e) {
      }
    }

    private void add(final Thread thread, final long expireTime) {
      final Entry entry = new Entry(thread, expireTime);
      queue.offer(entry);
      final Entry head = queue.peek();
      if (head == entry) {
        lock.lock();
        condition.signal();
        lock.unlock();
      }
    }

    @Override
    public void run() {
      boolean running = false;
      do {
        final Entry entry = queue.poll();
        if (!running) {
          lock.lock();
          condition.signal();
          lock.unlock();
          running = true;
        }

        if (entry == null) {
          try {
            lock.lock();
            condition.await();
            lock.unlock();
          }
          catch (final InterruptedException e) {
          }
        }
        else if (entry.isExpired()) {
          entry.thread.interrupt();
        }
        else {
          queue.offer(entry);
          synchronized (queue) {
            try {
              lock.lock();
              condition.await(System.currentTimeMillis() - entry.expireTime, TimeUnit.MILLISECONDS);
              lock.unlock();
            }
            catch (final InterruptedException e) {
            }
          }
        }
      }
      while (running);
    }
  }

  private static final AtomicReference<ReaperThread> reaper = new AtomicReference<>();

  private static ReaperThread reaper() {
    ReaperThread reaper = Threads.reaper.get();
    if (reaper != null)
      return reaper;

    synchronized (Threads.reaper) {
      reaper = Threads.reaper.get();
      if (reaper != null)
        return reaper;

      reaper = new ReaperThread();
      Threads.reaper.set(reaper);
      return reaper;
    }
  }

  /**
   * Returns a new {@link Runnable} instance that wraps the provided {@code runnable}, and is scheduled to be
   * {@linkplain Thread#interrupt() interrupted} once the provided {@code timeout} of {@link TimeUnit unit} expires.
   *
   * @param runnable The {@link Runnable} to be wrapped.
   * @param timeout The maximum time to wait.
   * @param unit The {@link TimeUnit} of the {@code timeout} argument.
   * @return A new {@link Runnable} instance wrapping the provided {@code runnable} that is scheduled to be
   *         {@linkplain Thread#interrupt() interrupted} once the provided {@code timeout} of {@link TimeUnit unit} expires.
   * @throws IllegalArgumentException If {@code runnable} or {@code unit} is null, or if {@code timeout} is negative.
   */
  public static Runnable interruptAfterTimeout(final Runnable runnable, final long timeout, final TimeUnit unit) {
    assertNotNull(runnable);
    assertNotNegative(timeout);
    assertNotNull(unit);
    return () -> {
      reaper().add(Thread.currentThread(), System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout, unit));
      runnable.run();
    };
  }

  /**
   * Returns a new {@link Callable} instance that wraps the provided {@code callable}, and is scheduled to be
   * {@linkplain Thread#interrupt() interrupted} once the provided {@code timeout} of {@link TimeUnit unit} expires.
   *
   * @param <V> The type parameter of the {@code callable} argument.
   * @param callable The {@link Callable} to be wrapped.
   * @param timeout The maximum time to wait.
   * @param unit The {@link TimeUnit} of the {@code timeout} argument.
   * @return A new {@link Callable} instance wrapping the provided {@code callable} that is scheduled to be
   *         {@linkplain Thread#interrupt() interrupted} once the provided {@code timeout} of {@link TimeUnit unit} expires.
   * @throws IllegalArgumentException If {@code callable} or {@code unit} is null, or if {@code timeout} is negative.
   */
  public static <V>Callable<V> interruptAfterTimeout(final Callable<V> callable, final long timeout, final TimeUnit unit) {
    assertNotNull(callable);
    assertNotNegative(timeout);
    assertNotNull(unit);
    return () -> {
      reaper().add(Thread.currentThread(), System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout, unit));
      return callable.call();
    };
  }

  /**
   * Checks the interrupted flag of the current thread.
   *
   * @implNote If the current thread's interrupt flag is set, this method clears the flag before throwing an
   *           {@link InterruptedException}.
   * @throws InterruptedException If the current thread's interrupt flag is set.
   */
  public static void checkInterrupted() throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException();
  }

  private Threads() {
  }
}