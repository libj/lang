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

public final class Paths {
  private static final String windowsPath = "([a-zA-Z]:)?(\\\\[a-zA-Z0-9_.-]+)+\\\\?";

  private static boolean isLocalURL(final String path) {
    return path.startsWith("file:");
  }

  private static boolean isLocalUnix(final String path) {
    return path.charAt(0) == '/';
  }

  private static boolean isLocalWindows(final String path) {
    return path.matches(windowsPath);
  }

  public static boolean isLocal(final String path) {
    return isLocalURL(path) || isLocalUnix(path) || isLocalWindows(path);
  }

  public static String getProtocol(final String path) {
    final int i = path.indexOf(":/");
    return i == -1 ? null : path.substring(0, i);
  }

  public static boolean isAbsolute(final String path) {
    return path.charAt(0) == '/' || (Character.isLetter(path.charAt(0)) && path.charAt(1) == ':' && path.charAt(2) == '\\' && Character.isLetter(path.charAt(3)));
  }

  public static String newPath(final String basedir, final String path) {
    if (basedir.length() == 0)
      return path;

    if (path.length() == 0)
      return basedir;

    final char sep = isLocalWindows(basedir) ? '\\' : '/';
    final char lastBasedir = basedir.charAt(basedir.length() - 1);
    final char firstPath = path.charAt(0);
    if (lastBasedir == sep && firstPath == sep)
      return basedir + path.substring(1);

    return basedir + (lastBasedir != sep && firstPath != sep ? sep + path : path);
  }

  public static String canonicalize(String path) {
    if (path.endsWith(".."))
      path = path + "/";

    // This removes all redundant "//" sequences.
    if (path.contains("://"))
      path = path.substring(0, 7) + path.substring(7).replace("//", "/");
    else
      path = path.replace("//", "/");

    path = path.replace("/./", "");
    if (path.endsWith("/."))
      path = path.substring(0, path.length() - 2);

    // Process "/../" correctly. This probably isn't very efficient in
    // the general case, but it's probably not bad most of the time.
    for (int index; (index = path.indexOf("/../")) != -1; ) {
      // Strip of the previous directory - if it exists.
      final int previous = path.lastIndexOf('/', index - 1);
      if (previous != -1)
        path = path.substring(0, previous) + path.substring(index + 3);
      else
        return path.substring(index + 4);
    }

    return path;
  }

  public static String relativePath(final String dir, final String file) {
    final String filePath = Paths.canonicalize(file);
    final String dirPath = Paths.canonicalize(dir);
    return !filePath.startsWith(dirPath) ? filePath : filePath.length() == dirPath.length() ? "" : filePath.substring(dirPath.length() + 1);
  }

  public static String getParent(final String url) {
    final int end = url.charAt(url.length() - 1) == '/' ? url.lastIndexOf('/', url.length() - 2) : url.lastIndexOf('/');
    return end == -1 ? null : url.substring(0, end);
  }

  public static String getCanonicalParent(String url) {
    url = canonicalize(url);
    final int separator = url.lastIndexOf('/');
    return separator < 0 ? null : url.substring(0, separator);
  }

  private static String getName0(final String url) {
    final boolean end = url.charAt(url.length() - 1) == '/';
    final int start = end ? url.lastIndexOf('/', url.length() - 2) : url.lastIndexOf('/');
    return start == -1 ? (end ? url.substring(0, url.length() - 1) : url) : end ? url.substring(start + 1, url.length() - 1) : url.substring(start + 1);
  }

  public static String getName(final String url) {
    return url == null || url.length() == 0 ? url : getName0(url);
  }

  public static String getShortName(String url) {
    if (url == null || url.length() == 0)
      return url;

    url = getName0(url);
    final int index = url.indexOf('.');
    return index == -1 ? url : url.substring(0, index);
  }

  public static boolean equal(final File a, final File b) {
    return a != null ? b != null && Paths.canonicalize(a.getAbsolutePath()).equals(Paths.canonicalize(b.getAbsolutePath())) : b == null;
  }

  private Paths() {
  }
}