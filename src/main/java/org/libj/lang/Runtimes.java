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

import java.util.function.Consumer;

/**
 * Utility functions that provide common operations pertaining to
 * {@link Runtime}.
 */
public final class Runtimes {
  /**
   * Registers a new virtual-machine shutdown hook to automatically call
   * {@link AutoCloseable#close()} of the provided {@link AutoCloseable}
   * instance.
   *
   * @param closeable The {@link AutoCloseable} instance to automatically close
   *          when the virtual-machine shuts down.
   * @throws IllegalAnnotationException If {@code closeable} is null.
   */
  public static void closeOnExit(final AutoCloseable closeable) {
    closeOnExit(closeable, null);
  }

  /**
   * Registers a new virtual-machine shutdown hook to automatically call
   * {@link AutoCloseable#close()} of the provided {@link AutoCloseable}
   * instance.
   *
   * @param closeable The {@link AutoCloseable} instance to automatically close
   *          when the virtual-machine shuts down.
   * @param onException The {@link Consumer} to accept an exception that may
   *          occur during the execution of the {@link AutoCloseable#close()} of
   *          the provided {@link AutoCloseable} instance.
   * @throws IllegalAnnotationException If {@code closeable} is null.
   */
  public static void closeOnExit(final AutoCloseable closeable, final Consumer<Exception> onException) {
    Assertions.assertNotNull(closeable);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        closeable.close();
      }
      catch (final Exception e) {
        if (onException != null)
          onException.accept(e);
        else
          throw new RuntimeException(e);
      }
    }));
  }

  private Runtimes() {
  }
}