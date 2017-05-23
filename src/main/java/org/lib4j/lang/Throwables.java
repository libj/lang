/* Copyright (c) 2013 lib4j
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

package org.lib4j.lang;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

public final class Throwables {
  public static void set(final Throwable t, final Throwable cause) {
    if (cause == t)
      throw new IllegalArgumentException("Self-causation not permitted");

    try {
      final Field causeField = Throwable.class.getDeclaredField("cause");
      causeField.setAccessible(true);
      causeField.set(t, cause);
    }
    catch (final SecurityException e) {
    }
    catch (final NoSuchFieldException e) {
    }
    catch (final IllegalAccessException e) {
    }
    catch (final IllegalArgumentException e) {
    }
  }

  public static void set(final Throwable t, final String message) {
    try {
      final Field detailMessageField = Throwable.class.getDeclaredField("detailMessage");
      detailMessageField.setAccessible(true);
      detailMessageField.set(t, message);
    }
    catch (final SecurityException e) {
    }
    catch (final NoSuchFieldException e) {
    }
    catch (final IllegalAccessException e) {
    }
    catch (final IllegalArgumentException e) {
    }
  }

  public static void set(final Throwable t, final String message, final Throwable cause) {
    Throwables.set(t, message);
    Throwables.set(t, cause.getCause());
  }

  public static String toString(final Throwable t) {
    if (t == null)
      throw new NullPointerException("t == null");

    final StringWriter stringWriter = new StringWriter();
    final PrintWriter printWriter = new PrintWriter(stringWriter);
    t.printStackTrace(printWriter);
    return stringWriter.toString();
  }

  private Throwables() {
  }
}