/* Copyright (c) 2008 lib4j
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

public final class ClassLoaders {
  private static URL[] getClassPath(final String property) {
    try {
      final String classPathProperty = System.getProperty(property);
      if (classPathProperty == null)
        return null;

      final String[] parts = classPathProperty.split(File.pathSeparator);
      final URL[] urls = new URL[parts.length];
      for (int i =  0; i < parts.length; i++)
        urls[i] = new File(parts[i]).toURI().toURL();

      return urls;
    }
    catch (final MalformedURLException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public static URL[] getClassPath() {
    final URL[] urls = getClassPath("java.class.path");
    return urls != null ? urls : getClassPath("user.dir");
  }

  public static URL[] getTestClassPath() {
    final URL[] urls = getClassPath("surefire.test.class.path");
    return urls != null ? urls : getClassPath("user.dir");
  }

  private ClassLoaders() {
  }
}