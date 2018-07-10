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
  public static URL getResourceOrFile(final String name) throws MalformedURLException {
    final URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
    if (resource != null)
      return resource;

    final File file = new File(name);
    return file.exists() ? file.toURI().toURL() : null;
  }

  public static URL getFileOrResource(final String name) throws MalformedURLException {
    final File file = new File(name);
    if (file.exists())
      return file.toURI().toURL();

    return Thread.currentThread().getContextClassLoader().getResource(name);
  }

  public static File getLocationBase(final Class<?> clazz) {
    if (clazz == null)
      return null;

    final URL url = Thread.currentThread().getContextClassLoader().getResource(clazz.getName().replace('.', '/') + ".class");
    String classFile = url.getFile();
    final int colon = classFile.indexOf(':');
    final int bang = classFile.indexOf('!');
    if (bang != -1 && colon != -1)
      classFile = classFile.substring(colon + 1, bang);
    else
      classFile = classFile.substring(0, classFile.length() - clazz.getName().length() - 7);

    return new File(classFile);
  }

  // FIXME: This needs to be removed due to Java 9's jrt:/java.base/java/lang....
  public static File[] getLocationBases(final Class<?> ... classes) {
    return getLocationBases(0, 0, classes);
  }

  private static File[] getLocationBases(final int index, final int depth, final Class<?> ... classes) {
    if (index == classes.length)
      return new File[depth];

    final File location = getLocationBase(classes[index]);
    if (location == null)
      return getLocationBases(index + 1, depth, classes);

    final File[] locations = getLocationBases(index + 1, depth + 1, classes);
    locations[depth] = location;
    return locations;
  }

  private Resources() {
  }
}