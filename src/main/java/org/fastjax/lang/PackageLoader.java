/* Copyright (c) 2006 FastJAX
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

package org.fastjax.lang;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.fastjax.lang.PackageLoader;
import org.fastjax.lang.PackageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PackageLoader extends ClassLoader {
  private static final Logger logger = LoggerFactory.getLogger(PackageLoader.class);

  private static final Map<ClassLoader,PackageLoader> instances = new HashMap<>();

  private static BiPredicate<Path,BasicFileAttributes> classPredicate = new BiPredicate<>() {
    @Override
    public boolean test(final Path t, final BasicFileAttributes u) {
      return u.isRegularFile() && t.toString().endsWith(".class");
    }
  };

  /**
   * Returns a <code>PackageLoader</code> that traverses the system classLoader
   * to find packages and load their classes.
   *
   * @return The <code>PackageLoader</code> for the system classLoader
   */
  public static PackageLoader getSystemPackageLoader() {
    return getPackageLoader(ClassLoader.getSystemClassLoader());
  }

  /**
   * Returns a <code>PackageLoader</code> that traverses the context classLoader
   * to find packages and load their classes.
   *
   * @return The <code>PackageLoader</code> for the system and context classLoaders
   */
  public static PackageLoader getContextPackageLoader() {
    return getPackageLoader(Thread.currentThread().getContextClassLoader());
  }

  public static PackageLoader getPackageLoader(final ClassLoader classLoader) {
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

  private final ClassLoader classLoader;

  private PackageLoader(final ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * This method will call <code>Class.forName()</code> and initialize each
   * final class in a given package and its sub-packages. This method will
   * search for all existing package resources in all elements of the
   * classpath. If the package exists in multiple classpath locations, such
   * as a couple of jar files and a directory, each of the classpath
   * references will be used to load all classes in each resource.
   *
   * @param pkg The package.
   * @return Set of all classes called with {@code Class#forName(String)}
   * @exception PackageNotFoundException Thrown when a package name
   *              cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg) throws PackageNotFoundException {
    return PackageLoader.loadPackage(pkg.getName(), true, true, null, classLoader);
  }

  /**
   * This method will call <code>Class.forName()</code> and initialize each
   * final class in a given package and its sub-packages. This method will
   * search for all existing package resources in all elements of the
   * classpath. If the package exists in multiple classpath locations, such as
   * a couple of jar files and a directory, each of the classpath references
   * will be used to load all classes in each resource.
   *
   * @param pkg The package.
   * @param filter Filter which classes will be initialized and returned.
   * @return Set of all classes called with <code>Class.forName()</code>.
   * @exception PackageNotFoundException Thrown when a package name
   *              cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg, final Predicate<Class<?>> filter) throws PackageNotFoundException {
    return PackageLoader.loadPackage(pkg.getName(), true, false, filter, classLoader);
  }

  /**
   * This method will call <code>Class.forName()</code> class in a given
   * package and its sub-packages. This method will search for all existing
   * package resources in all elements of the classpath. If the package exists
   * in multiple classpath locations, such as a couple of jar files and a
   * directory, each of the classpath references will be used to load all
   * classes in each resource. This method will search for all classpath
   * entries in all class loaders.
   *
   * @param pkg The package.
   * @param initialize Whether the classes must be initialized
   * @return Set of all classes called with <code>Class.forName()</code>.
   * @exception PackageNotFoundException Thrown when a package name
   *              that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg, final boolean initialize) throws PackageNotFoundException {
    return PackageLoader.loadPackage(pkg.getName(), true, initialize, null, classLoader);
  }

  /**
   * This method will call <code>Class.forName()</code> and initialize each
   * final class in a given package and its sub-packages. This method will
   * search for all existing package resources in all elements of the
   * classpath. If the package exists in multiple classpath locations, such
   * as a couple of jar files and a directory, each of the classpath references
   * will be used to load all classes in each resource.
   *
   * @param name The name of the package.
   * @param initialize Whether the classes must be initialized
   * @return Set of all classes called with <code>Class.forName()</code>.
   * @exception PackageNotFoundException Thrown when a package name
   *              cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String name) throws PackageNotFoundException {
    return PackageLoader.loadPackage(name, true, true, null, classLoader);
  }

  /**
   * This method will call <code>Class.forName()</code> and initialize each
   * final class in a given package and its sub-packages. This method will
   * search for all existing package resources in all elements of the
   * classpath. If the package exists in multiple classpath locations, such as
   * a couple of jar files and a directory, each of the classpath references
   * will be used to load all classes in each resource.
   *
   * @param name The name of the package.
   * @param initialize Predicate to test whether to initialize each Class.
   * @return Set of all classes called with <code>Class.forName()</code>.
   * @exception PackageNotFoundException Thrown when a package name
   *              that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String name, final Predicate<Class<?>> initialize) throws PackageNotFoundException {
    return PackageLoader.loadPackage(name, true, false, initialize, classLoader);
  }

  /**
   * This method will call <code>Class.forName()</code> class in in a given
   * package. This method will search for all existing package resources in all
   * elements of the classpath. If the package exists in multiple classpath
   * locations, such as a couple of jar files and a directory, each of the
   * classpath references will be used to load all classes in each resource.
   *
   * @param packageName The name of the package.
   * @param classLoader ClassLoader containing the resource, or null for all other ClassLoaders
   * @param subPackages Whether subPackages should be loaded
   * @param initialize Whether the classes must be initialized
   * @return Set of all classes called with <code>Class.forName()</code>.
   * @exception PackageNotFoundException Thrown when a package name
   *              cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String packageName, final boolean subPackages, final boolean initialize) throws PackageNotFoundException {
    return PackageLoader.loadPackage(packageName, subPackages, initialize, null, classLoader);
  }

  private static Set<Class<?>> loadPackage(final String packageName, final boolean subPackages, final boolean initialize, final Predicate<Class<?>> filter, final ClassLoader classLoader) throws PackageNotFoundException {
    if (packageName.length() == 0)
      throw new IllegalArgumentException("packageName.length() == 0");

    try {
      final String location = packageName.replace('.', '/');
      final Enumeration<URL> urls = classLoader.getResources(location);
      final ArrayList<URL> resources = new ArrayList<>(1);
      while (urls.hasMoreElements())
        resources.add(urls.nextElement());

      if (resources.size() == 0)
        throw new PackageNotFoundException(packageName);

      final Set<Class<?>> classes = new HashSet<>();

      // Reverse the order of resources, because the resources from the classLoader's parent,
      // and its parent, and its parent... are listed first -- thus, if the resource belongs to
      // the classLoader, it is guaranteed to always be the last element in the list
      for (int i = resources.size() - 1; i >= 0; --i) {
        final URL url = resources.get(i);
        final Set<String> classNames = new HashSet<>();
        if ("file".equals(url.getProtocol())) {
          String decodedUrl;
          try {
            decodedUrl = URLDecoder.decode(url.getPath(), "UTF-8");
          }
          catch (final UnsupportedEncodingException e) {
            decodedUrl = url.getPath();
          }

          PackageLoader.loadDirectory(classNames, new File(decodedUrl), packageName, subPackages);
        }
        else if ("jar".equals(url.getProtocol())) {
          PackageLoader.loadJar(classNames, url, packageName, subPackages);
        }
        else {
          throw new UnsupportedOperationException("Unsupported protocol in URL: " + url.toExternalForm());
        }

        for (final String className : classNames) {
          try {
            final Class<?> cls = Class.forName(className, initialize, classLoader);
            boolean add = filter == null;
            if (!initialize && !add && (add = filter.test(cls)))
              Class.forName(className, true, classLoader);

            if (add)
              classes.add(cls);
          }
          catch (final ClassNotFoundException | VerifyError e) {
            logger.trace("Problem loading package: " + packageName, e);
          }
          catch (final NoClassDefFoundError e) {
          }
        }
      }

      return classes;
    }
    catch (final IOException e) {
      throw new PackageNotFoundException(e.getMessage(), e);
    }
  }

  private static void loadDirectory(final Set<String> classNames, final File directory, final String packageName, final boolean subPackages) throws IOException {
    final Path path = directory.toPath();
    final Consumer<Path> consumer = new Consumer<>() {
      final String packagePrefix = packageName + ".";

      @Override
      public void accept(final Path t) {
        final String classFile = path.relativize(t).toString();
        final String className = packagePrefix + classFile.substring(0, classFile.length() - 6).replace(File.separatorChar, '.');
        classNames.add(className);
      }
    };

    if (subPackages)
      Files.find(path, Integer.MAX_VALUE, classPredicate).forEach(consumer);
    else
      Files.list(path).forEach(consumer);
  }

  private static void loadJar(final Set<String> classNames, final URL url, final String packageName, final boolean subPackages) throws PackageNotFoundException {
    final JarURLConnection jarURLConnection;
    final JarFile jarFile;
    try {
      jarURLConnection = (JarURLConnection)url.openConnection();
      jarURLConnection.setUseCaches(false);
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
        if (className.startsWith(packagePrefix) && (subPackages || className.indexOf(".", packagePrefix.length() + 1) < 0))
          classNames.add(className);
      }
    }
  }
}