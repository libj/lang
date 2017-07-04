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
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class PackageLoader extends ClassLoader {
  private static final Map<ClassLoader,PackageLoader> instances = new HashMap<ClassLoader,PackageLoader>();

  private static BiPredicate<Path,BasicFileAttributes> classPredicate = new BiPredicate<Path,BasicFileAttributes>() {
    @Override
    public boolean test(final Path t, final BasicFileAttributes u) {
      return u.isRegularFile() && t.toString().endsWith(".class");
    }
  };

  /**
   * Returns a PackageLoader that traverses the system classLoader to find
   * packages and load their classes.
   * @return The PackageLoader for the system classLoader
   */
  public static PackageLoader getSystemPackageLoader() {
    return getPackageLoader(ClassLoader.getSystemClassLoader());
  }

  /**
   * Returns a PackageLoader that traverses the provided classLoader to find
   * packages and load their classes.
   * @return The PackageLoader for the provided classLoader
   */
  public static PackageLoader getPackageLoader(final ClassLoader classLoader) {
    if (classLoader == null)
      throw new NullPointerException("classLoader == null");

    PackageLoader packageLoader = instances.get(classLoader);
    if (packageLoader != null)
      return packageLoader;

    synchronized (instances) {
      packageLoader = instances.get(classLoader);
      if (packageLoader != null)
        return packageLoader;

      instances.put(classLoader, packageLoader = new PackageLoader(classLoader));
      return packageLoader;
    }
  }

  private static boolean isClassLoaded(final PackageLoader packageLoader, final String className) {
    return packageLoader != null && (packageLoader.loadedClassNames.contains(className) || isClassLoaded(instances.get(packageLoader.classLoader.getParent()), className));
  }

  private final Set<String> loadedClassNames = new HashSet<String>();
  private final ClassLoader classLoader;

  protected PackageLoader(final ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * This method will call Class.forName() and initialize each final class in a
   * given package and its sub-packages. This method will search for all
   * existing package resources in all elements of the classpath. If the
   * package exists in multiple classpath locations, such as a couple of jar
   * files and a directory, each of the classpath references will be used to
   * load all classes in each resource. This method will search for all
   * classpath entries in all class loaders.
   *
   * @param       pkg        The package.
   *
   * @return      A set of all classes for which Class.forName() was called.
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg) throws PackageNotFoundException {
    return loadPackage(pkg.getName(), true, true);
  }

  /**
   * This method will call Class.forName() class in in a given package and its
   * sub-packages. This method will search for all existing package resources
   * in all elements of the classpath. If the package exists in multiple
   * classpath locations, such as a couple of jar files and a directory, each
   * of the classpath references will be used to load all classes in each
   * resource. This method will search for all classpath entries in all class
   * loaders.
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
    return loadPackage(pkg.getName(), true, initialize);
  }

  /**
   * This method will call Class.forName() and initialize each final class in a
   * given package and its sub-packages. This method will search for all
   * existing package resources in all elements of the classpath. If the
   * package exists in multiple classpath locations, such as a couple of jar
   * files and a directory, each of the classpath references will be used to
   * load all classes in each resource. This method will search for all
   * classpath entries in all class loaders.
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
    return loadPackage(name, true, true);
  }

  /**
   * This method will call Class.forName() class in in a given package. This
   * method will search for all existing package resources in all elements of
   * the classpath. If the package exists in multiple classpath locations, such
   * as a couple of jar files and a directory, each of the classpath references
   * will be used to load all classes in each resource. This method will search
   * for all classpath entries in all class loaders.
   *
   * @param       packageName        The name of the package.
   * @param       classLoader ClassLoader containing the resource, or null for all other ClassLoaders
   * @param       subPackages  Whether subPackages should be loaded
   * @param       initialize  Whether the classes must be initialized
   *
   * @return      A set of all classes for which Class.forName() was called.
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String packageName, final boolean subPackages, final boolean initialize) throws PackageNotFoundException {
    if (packageName == null)
      throw new NullPointerException("name == null");

    if (packageName.length() == 0)
      throw new IllegalArgumentException("name.length() == 0");

    final char firstChar = packageName.charAt(0);
    final Enumeration<Resource> resources;
    try {
      final String location = (firstChar == '/' || firstChar == '.' ? packageName.substring(1) : packageName).replace('.', '/');
      resources = Resources.getResources(location, classLoader);
    }
    catch (final IOException e) {
      throw new ResourceException(e.getMessage(), e);
    }

    if (resources == null)
      throw new PackageNotFoundException(packageName);

    try {
      final Set<Class<?>> classes = new HashSet<Class<?>>();
      while (resources.hasMoreElements()) {
        final Resource resource = resources.nextElement();
        final URL url = resource.getURL();
        final ClassLoader resourceClassLoader = resource.getClassLoader();
        String decodedUrl;
        try {
          decodedUrl = URLDecoder.decode(url.getPath(), "UTF-8");
        }
        catch (final UnsupportedEncodingException e) {
          decodedUrl = url.getPath();
        }

        final File directory = new File(decodedUrl);
        final Set<String> classNames = new HashSet<String>();
        if (directory.exists())
          loadDirectory(classNames, directory, packageName, subPackages);
        else
          loadJar(classNames, url, packageName, subPackages);

        for (final String className : classNames) {
          try {
            classes.add(Class.forName(className, initialize, resourceClassLoader));
          }
          catch (final ClassNotFoundException | NoClassDefFoundError | VerifyError e) {
          }
        }
      }

      return classes;
    }
    catch (final IOException e) {
      throw new PackageNotFoundException(e.getMessage(), e);
    }
  }

  private void loadDirectory(final Set<String> classNames, final File directory, final String packageName, final boolean subPackages) throws IOException {
    final Path path = directory.toPath();
    final Consumer<Path> consumer = new Consumer<Path>() {
      final String packagePrefix = packageName + ".";

      @Override
      public void accept(final Path t) {
        final String classFile = path.relativize(t).toString();
        final String className = packagePrefix + classFile.substring(0, classFile.length() - 6).replace(File.separatorChar, '.');
        if (!isClassLoaded(PackageLoader.this, className))
          classNames.add(className);
      }
    };

    if (subPackages)
      Files.find(path, Integer.MAX_VALUE, classPredicate).forEach(consumer);
    else
      Files.list(path).forEach(consumer);
  }

  private void loadJar(final Set<String> classNames, final URL url, final String packageName, final boolean subPackages) throws PackageNotFoundException {
    final JarURLConnection jarURLConnection;
    final JarFile jarFile;
    try {
      jarURLConnection = (JarURLConnection)url.openConnection();
      jarFile = jarURLConnection.getJarFile();
    }
    catch (final IOException e) {
      throw new PackageNotFoundException(packageName, e);
    }

    final String packagePrefix = packageName + ".";
    final String entryName = jarURLConnection.getEntryName();
    final Enumeration<JarEntry> enumeration = jarFile.entries();
    while (enumeration.hasMoreElements()) {
      final String entry = enumeration.nextElement().getName();
      if (entry.startsWith(entryName) && entry.endsWith(".class")) {
        final String className = (entry.charAt(0) == '/' ? entry.substring(1, entry.length() - 6) : entry.substring(0, entry.length() - 6)).replace('/', '.');
        if (className.startsWith(packagePrefix) && (subPackages || className.indexOf(".", packagePrefix.length() + 1) < 0) && !isClassLoaded(this, className))
          classNames.add(className);
      }
    }
  }
}