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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public final class Resources {
  public static File getLocationBase(final Class<?> clazz) {
    if (clazz == null)
      return null;

    final Resource resource = getResource(clazz.getName().replace('.', '/') + ".class");
    if (resource == null)
      return null;

    final URL url = resource.getURL();
    String classFile = url.getFile();
    final int colon = classFile.indexOf(':');
    final int bang = classFile.indexOf('!');
    if (bang != -1 && colon != -1)
      classFile = classFile.substring(colon + 1, bang);
    else
      classFile = classFile.substring(0, classFile.length() - clazz.getName().length() - 7);

    return new File(classFile);
  }

  public static Resource getResourceOrFile(final String name) throws MalformedURLException {
    if (name.length() == 0)
      return null;

    final Resource resource = getResource(name);
    if (resource != null)
      return resource;

    final File file = new File(name);
    return file.exists() ? new Resource(file.toURI().toURL(), ClassLoader.getSystemClassLoader()) : null;
  }

  public static Resource getFileOrResource(final String name) throws MalformedURLException {
    if (name.length() == 0)
      return null;

    final File file = new File(name);
    if (file.exists())
      return new Resource(file.toURI().toURL(), ClassLoader.getSystemClassLoader());

    return getResource(name);
  }

  public static Resource getResource(final String name, final ClassLoader classLoader) {
    if (name == null)
      throw new NullPointerException("name == null");

    if (name.length() == 0)
      throw new IllegalArgumentException("name.length() == 0");

    if (classLoader == null)
      throw new NullPointerException("classLoader == null");

    final URL url = classLoader.getResource(name);
    return url != null ? new Resource(url, classLoader) : null;
  }

  public static Resource getResource(final String name) {
    if (name.length() == 0)
      return null;

    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    URL url = classLoader.getResource(name);
    if (url != null)
      return new Resource(url, classLoader);

    classLoader = Thread.currentThread().getContextClassLoader();
    url = classLoader.getResource(name);
    if (url != null)
      return new Resource(url, classLoader);

    /*final Class<?> callerClass = Reflection.getCallerClass();
    url = callerClass.getResource(name);
    if (url != null)
      return new Resource(url, classLoader);*/

    return null;
  }

  public static Enumeration<Resource> getResources(final String name, final ClassLoader classLoader) throws IOException {
    if (name == null)
      throw new NullPointerException("name == null");

    if (name.length() == 0)
      throw new IllegalArgumentException("name.length() == 0");

    if (classLoader == null)
      throw new NullPointerException("classLoader == null");

    final Vector<Resource> resources = new Vector<Resource>(1, 1);
    final Enumeration<URL> urls = classLoader.getResources(name);
    while (urls.hasMoreElements())
      resources.add(new Resource(urls.nextElement(), classLoader));

    return resources.elements();
  }

  public static Enumeration<Resource> getResources(final String name) throws IOException {
    if (name == null)
      throw new NullPointerException("name == null");

    if (name.length() == 0)
      throw new IllegalArgumentException("name.length() == 0");

    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    Enumeration<URL> urls = classLoader.getResources(name);

    final Set<URL> history = new HashSet<URL>();
    final Vector<Resource> resources = new Vector<Resource>(1, 1);
    combineResources(urls, classLoader, history, resources);

    classLoader = Thread.currentThread().getContextClassLoader();
    urls = classLoader.getResources(name);
    combineResources(urls, classLoader, history, resources);

    /*final Class<?> callerClass = Reflection.getCallerClass();
    classLoader = callerClass.getClassLoader();
    urls = classLoader.getResources(name);
    combineResources(urls, classLoader, history, resources);*/

    return resources.elements();
  }

  private static void combineResources(final Enumeration<URL> urls, final ClassLoader classLoader, final Set<URL> history, final Collection<Resource> resources) {
    if (urls == null)
      return;

    while (urls.hasMoreElements()) {
      final URL url = urls.nextElement();
      if (history.contains(url))
        continue;

      history.add(url);
      resources.add(new Resource(url, classLoader));
    }
  }

  private Resources() {
  }
}