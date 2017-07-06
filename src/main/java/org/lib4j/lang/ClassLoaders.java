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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public final class ClassLoaders {
  private static final Method findLoadedClass;

  static {
    try {
      findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
      findLoadedClass.setAccessible(true);
    }
    catch (final NoSuchMethodException | SecurityException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static boolean isClassLoaded(final ClassLoader classLoader, final String name) {
    if (classLoader == null)
      throw new IllegalArgumentException("classLoader == null");

    try {
      return findLoadedClass.invoke(classLoader, name) != null;
    }
    catch (final InvocationTargetException e) {
      throw new UnsupportedOperationException(e);
    }
    catch (final IllegalAccessException e) {
      throw new SecurityException(e);
    }
  }

  public static URL[] getClassPath() {
    final Collection<URL> urls = new HashSet<URL>();
    urls.addAll(java.util.Arrays.asList(((URLClassLoader)ClassLoader.getSystemClassLoader()).getURLs()));
    urls.addAll(java.util.Arrays.asList(((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs()));
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try {
      // TODO: I don't know why, but when running forked JUnit tests
      // TODO: the classpath is not available by calling the getURLs
      // TODO: method. Instead, it is hidden deep inside the URLClassPath
      final Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
      ucpField.setAccessible(true);
      final Object ucp = ucpField.get(classLoader); // This is a sun.misc.URLClassPath
      final Field lmapField = ucp.getClass().getDeclaredField("lmap");
      lmapField.setAccessible(true);
      @SuppressWarnings("unchecked")
      final Map<String,Object> lmap = (Map<String,Object>)lmapField.get(ucp);
      for (final String key : lmap.keySet())
        urls.add(new URL(key));
    }
    catch (final Exception e) {
      if (classLoader instanceof URLClassLoader)
        urls.addAll(java.util.Arrays.asList(((URLClassLoader)classLoader).getURLs()));
    }

    return urls.toArray(new URL[urls.size()]);
  }

  public static void addURL(final URLClassLoader classLoader, final URL ... urls) {
    for (final URL url : urls) {
      if (Arrays.contains(classLoader.getURLs(), url))
        continue;

      try {
        final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, url);
      }
      catch (final ReflectiveOperationException e) {
        throw new UnsupportedOperationException(e);
      }
    }
  }

  private ClassLoaders() {
  }
}