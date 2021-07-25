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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility functions that provide common operations pertaining to
 * {@link Thread}.
 */
public final class Threads {
  /**
   * Prints all threads and the full stack trace of code running on each thread
   * to the standard error stream.
   */
  public static void printThreadTrace() {
    printThreadTrace(System.err);
  }

  /**
   * Prints all threads and the full stack trace of code running on each thread
   * to the specified print writer.
   *
   * @param s {@link PrintWriter} to use for output.
   */
  public static void printThreadTrace(final PrintWriter s) {
    printThreadTrace(s::println);
  }

  /**
   * Prints all threads and the full stack trace of code running on each thread
   * to the specified print stream.
   *
   * @param s {@link PrintStream} to use for output.
   */
  public static void printThreadTrace(final PrintStream s) {
    printThreadTrace(s::println);
  }

  /**
   * Prints all threads and the full stack trace of code running on each thread
   * to the specified string consumer.
   *
   * @param s {@link Consumer Consumer&lt;String&gt;} to use for output.
   * @throws NullPointerException If {@code s} is null.
   */
  public static void printThreadTrace(final Consumer<String> s) {
    final Map<Thread,StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
    final Map<Long,Thread> tidToThread = new HashMap<>(stackTraces.size());
    for (final Thread thread : stackTraces.keySet())
      tidToThread.put(thread.getId(), thread);

    final StringBuilder builder = new StringBuilder();
    final ThreadInfo[] threadInfos = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
    for (int i = 0; i < threadInfos.length; ++i) {
      if (i > 0)
        builder.append("\n\n");

      final ThreadInfo threadInfo = threadInfos[i];
      builder.append('"').append(threadInfo.getThreadName()).append("\" #").append(threadInfo.getThreadId());
      final Thread thread = tidToThread.get(threadInfo.getThreadId());
      if (thread.isDaemon())
        builder.append(" daemon");

      builder.append(" prio=").append(thread.getPriority());
      builder.append("\n   java.lang.Thread.State: ").append(threadInfo.getThreadState());
      for (final StackTraceElement stackTraceElement : threadInfo.getStackTrace())
        builder.append("\n  at ").append(stackTraceElement);
    }

    s.accept(builder.toString());
  }

  private Threads() {
  }
}