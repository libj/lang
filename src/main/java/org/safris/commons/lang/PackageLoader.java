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

package org.safris.commons.lang;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.safris.commons.util.Collections;

/**
 * This utility final class is for loading classes in a package.
 */
public abstract class PackageLoader extends ClassLoader {
  private static final PackageLoader instance = new PackageLoader() {};
  private static final Map<String,Set<Class<?>>> loadedPackageToClasses = new HashMap<String,Set<Class<?>>>();
  private static final Map<String,Set<String>> loadedPackageToClassNames = new HashMap<String,Set<String>>();

  private static final FileFilter classFileFilter = new FileFilter() {
    @Override
    public boolean accept(final File pathname) {
      return pathname.getName().endsWith(".class") || pathname.isDirectory();
    }
  };

  private static void loadDirectory(final List<String> entries, final File directory, final String name) {
    final File[] files = directory.listFiles(classFileFilter);
    for (final File file : files) {
      if (file.isDirectory())
        loadDirectory(entries, file, name);
      else
        entries.add(name + "." + file.getName().substring(0, file.getName().length() - 6).replace('/', '.'));
    }
  }

  public static PackageLoader getSystemPackageLoader() {
    return instance;
  }

  protected PackageLoader() {
  }

  /**
   * This method will call Class.forName() and initialize each final class in a
   * given package. This method will search for all existing package resources
   * in all elements of the classpath. If the package exists in multiple
   * classpath locations, such as a couple of jar files and a directory, each
   * of the classpath references will be used to load all classes in each
   * resource. This method will search for all classpath entries in all class
   * loaders.
   *
   * @param       pkg        The package.
   *
   * @return      A set of all classes for which Class.forName() was called.
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg) throws PackageNotFoundException {
    return loadPackage(pkg.getName());
  }

  /**
   * This method will call Class.forName() class in in a given package. This
   * method will search for all existing package resources in all elements of
   * the classpath. If the package exists in multiple classpath locations, such
   * as a couple of jar files and a directory, each of the classpath references
   * will be used to load all classes in each resource. This method will search
   * for all classpath entries in all class loaders.
   *
   * @param       pkg        The package.
   * @param       initialize Whether the classes must be initialized
   *
   * @return      A set of all classes for which Class.forName() was called.
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg, final boolean initialize) throws PackageNotFoundException {
    return loadPackage(pkg.getName(), initialize);
  }

  /**
   * This method will call Class.forName() and initialize each final class in a
   * given package. This method will search for all existing package resources
   * in all elements of the classpath. If the package exists in multiple
   * classpath locations, such as a couple of jar files and a directory, each
   * of the classpath references will be used to load all classes in each
   * resource. This method will search for all classpath entries in all class
   * loaders.
   *
   * @param       name       The name of the package.
   * @param       initialize Whether the classes must be initialized
   *
   * @return      A set of all classes for which Class.forName() was called.
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String name) throws PackageNotFoundException {
    return loadPackage(name, true);
  }

  /**
   * This method will call Class.forName() class in in a given package. This
   * method will search for all existing package resources in all elements of
   * the classpath. If the package exists in multiple classpath locations, such
   * as a couple of jar files and a directory, each of the classpath references
   * will be used to load all classes in each resource. This method will search
   * for all classpath entries in all class loaders.
   *
   * @param       name        The name of the package.
   * @param       classLoader ClassLoader containing the resource, or null for all other ClassLoaders
   * @param       initialize  Whether the classes must be initialized
   *
   * @return      A set of all classes for which Class.forName() was called.
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String name, final boolean initialize) throws PackageNotFoundException {
    if (name == null)
      throw new NullPointerException("name == null");

    if (name.length() == 0)
      throw new IllegalArgumentException("name.length() == 0");

    final char firstChar = name.charAt(0);
    final Enumeration<Resource> resources;
    try {
      resources = Resources.getResources((firstChar == '/' || firstChar == '.' ? name.substring(1) : name).replace('.', '/'));
    }
    catch (final IOException e) {
      throw new ResourceException(e.getMessage(), e);
    }

    if (resources == null)
      throw new PackageNotFoundException(name);

    final Set<Class<?>> classes = new HashSet<Class<?>>();
    while (resources.hasMoreElements()) {
      final Resource resource = resources.nextElement();
      final URL url = resource.getURL();
      final ClassLoader resourceClassLoader = resource.getClassLoader();
      synchronized (resourceClassLoader) {
        String decodedUrl;
        try {
          decodedUrl = URLDecoder.decode(url.getPath(), "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
          decodedUrl = url.getPath();
        }

        final File directory = new File(decodedUrl);
        final List<String> entries = new ArrayList<String>();
        if (directory.exists()) {
          loadDirectory(entries, directory, name);
        }
        else {
          final JarURLConnection jarURLConnection;
          final JarFile jarFile;
          try {
            jarURLConnection = (JarURLConnection)url.openConnection();
            jarFile = jarURLConnection.getJarFile();
          }
          catch (final IOException e) {
            throw new PackageNotFoundException(name, e);
          }

          final String entryName = jarURLConnection.getEntryName();
          final Enumeration<JarEntry> enumeration = jarFile.entries();
          while (enumeration.hasMoreElements()) {
            final String entry = enumeration.nextElement().getName();
            if (entry.startsWith(entryName) && entry.endsWith(".class"))
              entries.add((entry.charAt(0) == '/' ? entry.substring(1, entry.length() - 6) : entry.substring(0, entry.length() - 6)).replace('/', '.'));
          }
        }

        if (entries.size() == 0)
          continue;

        String lastPackage = null;
        Set<Class<?>> packageClasses = null;
        Set<String> packageClassNames = null;
        boolean packageLoaded = false;
        Collections.sort(entries);
        for (final String entry : entries) {
          final String subPackage = entry.substring(0, entry.lastIndexOf('.'));
          if (!subPackage.equals(lastPackage)) {
            lastPackage = subPackage;
            if (packageClasses != null)
              classes.addAll(packageClasses);

            packageClasses = loadedPackageToClasses.get(subPackage);
            if (packageLoaded = (packageClasses != null)) {
              packageClassNames = loadedPackageToClassNames.get(subPackage);
            }
            else {
              loadedPackageToClasses.put(subPackage, packageClasses = new HashSet<Class<?>>());
              loadedPackageToClassNames.put(subPackage, packageClassNames = new HashSet<String>());
            }
          }

          if (!packageLoaded || !packageClassNames.contains(entry)) {
            try {
              packageClasses.add(Class.forName(entry, initialize, resourceClassLoader));
            }
            catch (final ClassNotFoundException | NoClassDefFoundError e) {
            }
            packageClassNames.add(entry);
          }
        }

        classes.addAll(packageClasses);
      }
    }

    return classes;
  }
}