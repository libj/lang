/* Copyright (c) 2020 LibJ
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

import java.net.URL;
import java.net.URLClassLoader;

/**
 * {@link ClassLoader} that resolves classes and resources via the bootstrap
 * class loader.
 */
public final class BootProxyClassLoader extends URLClassLoader {
  public static final BootProxyClassLoader INSTANCE = new BootProxyClassLoader();

  private BootProxyClassLoader() {
    super(new URL[0], null);
  }

  @Override
  public Class<?> findClass(final String name) throws ClassNotFoundException {
    return super.findClass(name);
  }

  @Override
  public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
    return super.loadClass(name, resolve);
  }

  /**
   * A variation of {@link #loadClass(String)} to load a class with the
   * specified binary name. This method returns {@code null} when the class is
   * not found. Invoking this method is equivalent to invoking
   * {@link #loadClassOrNull(String,boolean) loadClassOrNull(name, false)}.
   *
   * @param name The binary name of the class.
   * @return The resulting {@link Class} object if successfully loaded,
   *         otherwise {@code null}.
   */
  public Class<?> loadClassOrNull(final String name) {
    return loadClassOrNull(name, false);
  }

  /**
   * A variation of {@link #loadClass(String,boolean)} to load a class with the
   * specified binary name. This method returns {@code null} when the class is
   * not found.
   *
   * @param name The binary name of the class.
   * @param resolve If {@code true} then resolve the class.
   * @return The resulting {@link Class} object if successfully loaded,
   *         otherwise {@code null}.
   */
  public Class<?> loadClassOrNull(final String name, final boolean resolve) {
    try {
      return super.loadClass(name, resolve);
    }
    catch (final ClassNotFoundException e) {
      return null;
    }
  }
}