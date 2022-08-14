/* Copyright (c) 2006 LibJ
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

import static org.libj.lang.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utility functions for operations pertaining to resources.
 */
public final class Resources {
  @FunctionalInterface
  public interface ForEachEntry {
    /**
     * Performs this operation on the given arguments.
     *
     * @param root The root location of the resource.
     * @param entry The entry path off of {@code root}.
     * @param isDirectory Whether the entry represents a directory.
     * @return Whether the walk operation should continue.
     */
    boolean test(URL root, String entry, boolean isDirectory);
  }

  private static class Reporter implements Predicate<Path> {
    private final ForEachEntry callback;
    private URL url;
    private Path path;

    private Reporter(final ForEachEntry callback) {
      this.callback = callback;
    }

    private void update(final URL url, final Path path) {
      this.url = url;
      this.path = path;
    }

    @Override
    public boolean test(final Path entry) {
      final boolean isDirectory = entry.toFile().isDirectory();
      final String relativePath = path.relativize(entry).toString();
      return callback.test(url, isDirectory ? relativePath + File.separator : relativePath, isDirectory);
    }
  }

  /**
   * Recursively traverses the tree of resource entries rooted at the provided {@code name}. For each matching entry, the
   * {@link ForEachEntry forEachEntry} predicate is called with the {@code root}, {@code entry}, and {@code isDirectory} values.
   * <p>
   * If {@link ForEachEntry#test(URL,String,boolean)} returns {@code false}, the walk operation is shortcutted.
   * <p>
   * Resource matching behavior is as follows:
   * <ol>
   * <li>If no resource matching {@code name} is found, this method returns immediately.</li>
   * <li>If the resource matching {@code name} represents a file entry, {@link ForEachEntry forEachEntry} is called once for the
   * matching file entry.</li>
   * <li>If the resource matching {@code name} represents a directory entry, {@link ForEachEntry forEachEntry} is called for the
   * directory entry, as well as for each child entry rooted at the entry provided by {@code name}, <b>recursively</b>.</li>
   * </ol>
   *
   * @param classLoader The {@link ClassLoader} in which to search for resource entries.
   * @param name The name of the root resource entry.
   * @param forEachEntry The predicate that is called for each matched resource entry. If
   *          {@link ForEachEntry#test(URL,String,boolean)} returns {@code false}, the walk operation is shortcutted.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If any of the provided arguments is null.
   */
  public static void walk(final ClassLoader classLoader, final String name, final ForEachEntry forEachEntry) throws IOException {
    traverse(assertNotNull(classLoader).getResources(assertNotNull(name)), name, true, assertNotNull(forEachEntry));
  }

  /**
   * Non-recursively traverses the tree of resource entries rooted at the provided {@code name}. For each matching entry, the
   * {@link ForEachEntry forEachEntry} predicate is called with the {@code root}, {@code entry}, and {@code isDirectory} values.
   * <p>
   * If {@link ForEachEntry#test(URL,String,boolean)} returns {@code false}, the walk operation is shortcutted.
   * <p>
   * Resource matching behavior is as follows:
   * <ol>
   * <li>If no resource matching {@code name} is found, this method returns immediately.</li>
   * <li>If the resource matching {@code name} represents a file entry, {@link ForEachEntry forEachEntry} is called once for the
   * matching file entry.</li>
   * <li>If the resource matching {@code name} represents a directory entry, {@link ForEachEntry forEachEntry} is called for the
   * directory entry, as well as for each child entry rooted at the entry provided by {@code name}, <b>non-recursively</b>.</li>
   * </ol>
   *
   * @param classLoader The {@link ClassLoader} in which to search for resource entries.
   * @param name The name of the root resource entry.
   * @param forEachEntry The predicate that is called for each matched resource entry. If
   *          {@link ForEachEntry#test(URL,String,boolean)} returns {@code false}, the walk operation is shortcutted.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If any of the provided arguments is null.
   */
  public static void list(final ClassLoader classLoader, final String name, final ForEachEntry forEachEntry) throws IOException {
    traverse(assertNotNull(classLoader).getResources(assertNotNull(name)), name, false, assertNotNull(forEachEntry));
  }

  /**
   * Traverses the tree of resource entries rooted at the provided {@code name}. For each matching entry, the {@link ForEachEntry
   * forEachEntry} predicate is called with the {@code root}, {@code entry}, and {@code isDirectory} values.
   * <p>
   * If {@link ForEachEntry#test(URL,String,boolean)} returns {@code false}, the walk operation is shortcutted.
   * <p>
   * Resource matching behavior is as follows:
   * <ol>
   * <li>If no resource matching {@code name} is found, this method returns immediately.</li>
   * <li>If the resource matching {@code name} represents a file entry, {@link ForEachEntry forEachEntry} is called once for the
   * matching file entry.</li>
   * <li>If the resource matching {@code name} represents a directory entry, {@link ForEachEntry forEachEntry} is called for the
   * directory entry, as well as for each child entry rooted at the entry provided by {@code name}, <b>recursively</b>.</li>
   * </ol>
   *
   * @param resources The {@link Enumeration} of resource URLs provided by {@link ClassLoader#getResources(String)}.
   * @param name The name of the root resource entry.
   * @param recursive Whether traversal should be recursive or not.
   * @param forEachEntry The predicate that is called for each matched resource entry. If
   *          {@link ForEachEntry#test(URL,String,boolean)} returns {@code false}, the walk operation is shortcutted.
   * @throws IOException If an I/O error has occurred.
   * @throws IllegalArgumentException If any of the provided arguments is null.
   */
  public static void traverse(final Enumeration<URL> resources, final String name, final boolean recursive, final ForEachEntry forEachEntry) throws IOException {
    assertNotNull(resources);
    assertNotNull(name);
    assertNotNull(forEachEntry);
    if (!resources.hasMoreElements())
      return;

    Reporter reporter = null;
    do {
      final URL url = resources.nextElement();
      if ("file".equals(url.getProtocol())) {
        String decodedUrl;
        try {
          decodedUrl = URLDecoder.decode(url.getPath(), "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
          decodedUrl = url.getPath();
        }

        final File file = new File(decodedUrl);
        final Path path = file.toPath();
        if (reporter == null)
          reporter = new Reporter(forEachEntry);

        reporter.update(url, new File(decodedUrl.substring(0, decodedUrl.length() - name.length())).toPath());

        if (file.isFile()) {
          reporter.test(path);
        }
        else if (recursive) {
          Files.walk(path).allMatch(reporter);
        }
        else {
          if (name.length() == 0 && !forEachEntry.test(url, "/", true))
            return;

          Files.list(path).allMatch(reporter);
        }
      }
      else if ("jar".equals(url.getProtocol())) {
        final JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
        jarURLConnection.setUseCaches(false);
        try (final JarFile jarFile = jarURLConnection.getJarFile()) {
          final String rootName = jarURLConnection.getEntryName();
          if (name.length() == 0)
            forEachEntry.test(url, "/", true);

          final Enumeration<JarEntry> enumeration = jarFile.entries();
          while (enumeration.hasMoreElements()) {
            final JarEntry entry = enumeration.nextElement();
            final String entryName = entry.getName();
            final int prefix = entryName.startsWith(rootName) ? rootName.length() : entryName.startsWith(name) ? name.length() : 0;
            if (prefix == 0)
              continue;

            final int s;
            if (recursive || (s = entryName.indexOf('/', prefix + 1)) == -1 || s == entryName.length() - 1)
              forEachEntry.test(url, entryName, entry.isDirectory());
          }
        }
      }
      else {
        throw new UnsupportedOperationException("Unsupported URL protocol: " + url);
      }
    }
    while (resources.hasMoreElements());
  }

  /**
   * Returns an {@link URL} to a resource by the specified name, or, if not
   * found, to a file by the same name (in the current working directory).
   *
   * @param name The name of the resource.
   * @return An {@link URL} to a resource by the specified name, or, if not
   *         found, to a file by the same name.
   * @throws IllegalArgumentException If {@code name} is null.
   */
  public static URL getResourceOrFile(final String name) {
    final URL resource = Thread.currentThread().getContextClassLoader().getResource(assertNotNull(name));
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

  /**
   * Returns an {@link URL} to a file by the specified name (in the current working directory), or, if not found, to a resource by
   * the same name.
   *
   * @param name The name of the resource.
   * @return An {@link URL} to a file by the specified name, or, if not found, to a resource by the same name.
   * @throws IllegalArgumentException If {@code name} is null.
   */
  public static URL getFileOrResource(final String name) {
    final File file = new File(assertNotNull(name));
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