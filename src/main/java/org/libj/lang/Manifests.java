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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.jar.Manifest;

/**
 * Utility functions for operations pertaining to {@link Manifest}.
 */
public final class Manifests {
  /**
   * Returns the {@link Manifest} associated to the provided {@link Class}, or {@code null} if no such {@link Manifest} exists.
   *
   * @implNote This method is designed to returns a single {@link Manifest} associated to the provided {@link Class} that has been
   *           loaded by its {@link ClassLoader}.
   * @param cls The {@link Class} for which the {@link Manifest} is to be returned.
   * @return The {@link Manifest} associated to the provided {@link Class}, or {@code null} if no such {@link Manifest} exists.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code cls} is null.
   */
  public static Manifest getManifest(final Class<?> cls) throws IOException {
    final CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
    if (codeSource == null)
      return null;

    final String str = codeSource.getLocation().toString();
    final URL url = new URL((str.endsWith(".jar") ? "jar:" + str + "!" : str) + "/META-INF/MANIFEST.MF");
    try (final InputStream in = url.openStream()) {
      return new Manifest(in);
    }
    catch (final FileNotFoundException e) {
      return null;
    }
  }

  /**
   * Returns an array of {@link Manifest}s associated to the provided {@link Class}, or an array of zero members if no such
   * {@link Manifest} exists.
   *
   * @implNote This method is designed to returns all {@link Manifest}s associated to the provided {@link Class} across all paths in
   *           the classpath.
   * @param cls The {@link Class} for which the {@link Manifest}s are to be returned.
   * @return An array of {@link Manifest}s associated to the provided {@link Class}, or an array of zero members if no such
   *         {@link Manifest} exists.
   * @throws IOException If an I/O error has occurred.
   * @throws NullPointerException If {@code cls} is null.
   */
  public static Manifest[] getManifests(final Class<?> cls) throws IOException {
    final String name = cls.getName().replace('.', '/').concat(".class");
    return getManifests(cls.getClassLoader().getResources(name), name.length(), 0);
  }

  private static Manifest[] getManifests(final Enumeration<URL> resources, final int len, final int depth) throws IOException {
    if (!resources.hasMoreElements())
      return new Manifest[depth];

    final String path = resources.nextElement().toString();
    final URL url = new URL(path.substring(0, path.length() - len) + "META-INF/MANIFEST.MF");

    Manifest manifest;
    try (final InputStream in = url.openStream()) {
      manifest = new Manifest(in);
    }
    catch (final FileNotFoundException e) {
      manifest = null;
    }

    final Manifest[] manifests = getManifests(resources, len, manifest != null ? depth + 1 : depth);
    if (manifest != null)
      manifests[depth] = manifest;

    return manifests;
  }

  private Manifests() {
  }
}