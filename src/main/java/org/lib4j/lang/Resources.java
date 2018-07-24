/* Copyright (c) 2006 lib4j
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public final class Resources {
  public static URL getResourceOrFile(final String name) {
    final URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
    if (resource != null)
      return resource;

    final File file = new File(name);
    if (!file.exists())
      return null;

    try {
      return file.toURI().toURL();
    }
    catch (final MalformedURLException e) {
      throw new IllegalArgumentException(name, e);
    }
  }

  public static URL getFileOrResource(final String name) {
    final File file = new File(name);
    if (file.exists()) {
      try {
        return file.toURI().toURL();
      }
      catch (final MalformedURLException e) {
        throw new IllegalArgumentException(name, e);
      }
    }

    return Thread.currentThread().getContextClassLoader().getResource(name);
  }

  private Resources() {
  }
}