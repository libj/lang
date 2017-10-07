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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PackageLoader extends ClassLoader {
  private static final Logger logger = LoggerFactory.getLogger(PackageLoader.class);

  private static final Map<Object,PackageLoader> instances = new HashMap<Object,PackageLoader>();

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
   * Returns a PackageLoader that traverses the system and context classLoaders
   * to find packages and load their classes.
   * @return The PackageLoader for the system and context classLoaders
   */
  public static PackageLoader getSystemContextPackageLoader() {
    return getPackageLoader(ClassLoader.getSystemClassLoader(), Thread.currentThread().getContextClassLoader());
  }

  /**
   * Returns a PackageLoader that traverses the provided classLoaders to find
   * packages and load their classes.
   * @return The PackageLoader for the provided classLoaders
   */
  public static PackageLoader getPackageLoader(final Collection<ClassLoader> classLoaders) {
    if (classLoaders.size() == 0)
      throw new IllegalArgumentException("classLoaders.size() == 0");

    return getPackageLoader((Object)classLoaders);
  }

  /**
   * Returns a PackageLoader that traverses the provided classLoaders to find
   * packages and load their classes.
   * @return The PackageLoader for the provided classLoaders
   */
  public static PackageLoader getPackageLoader(final ClassLoader ... classLoaders) {
    if (classLoaders.length == 0)
      throw new IllegalArgumentException("classLoaders.length == 0");

    return getPackageLoader(classLoaders.length == 1 ? classLoaders[0] : classLoaders);
  }

  private static PackageLoader getPackageLoader(final Object key) {
    PackageLoader packageLoader = instances.get(key);
    if (packageLoader != null)
      return packageLoader;

    synchronized (instances) {
      packageLoader = instances.get(key);
      if (packageLoader != null)
        return packageLoader;

      if (key instanceof ClassLoader)
        instances.put(key, packageLoader = new PackageLoader((ClassLoader)key));
      else if (key instanceof ClassLoader[]) {
        final ClassLoader[] classLoaders = (ClassLoader[])key;
        final List<ClassLoader> list = new ArrayList<ClassLoader>(classLoaders.length);
        for (final ClassLoader classLoader : classLoaders)
          list.add(classLoader);

        instances.put(list, packageLoader = new PackageLoader(classLoaders));
      }
      else
        throw new UnsupportedOperationException("Unsupported key type: " + key.getClass().getName());

      return packageLoader;
    }
  }

  private final ClassLoader[] classLoaders;

  private PackageLoader(final ClassLoader ... classLoaders) {
    this.classLoaders = classLoaders;
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
   * @return      Set of all classes called with Class.forName().
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg) throws PackageNotFoundException {
    return PackageLoader.loadPackage(pkg.getName(), true, true, null, classLoaders);
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
   * @param       initialize Predicate to test whether to initialize each Class.
   *
   * @return      Set of all classes called with Class.forName().
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg, final Predicate<Class<?>> initialize) throws PackageNotFoundException {
    return PackageLoader.loadPackage(pkg.getName(), true, false, initialize, classLoaders);
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
   * @return      Set of all classes called with Class.forName().
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final Package pkg, final boolean initialize) throws PackageNotFoundException {
    return PackageLoader.loadPackage(pkg.getName(), true, initialize, null, classLoaders);
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
   * @return      Set of all classes called with Class.forName().
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String name) throws PackageNotFoundException {
    return PackageLoader.loadPackage(name, true, true, null, classLoaders);
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
   * @param       initialize  Predicate to test whether to initialize each Class.
   *
   * @return      Set of all classes called with Class.forName().
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String name, final Predicate<Class<?>> initialize) throws PackageNotFoundException {
    return PackageLoader.loadPackage(name, true, false, initialize, classLoaders);
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
   * @return      Set of all classes called with Class.forName().
   *
   * @exception   PackageNotFoundException    Gets thrown for a package name
   * that cannot be found in any classpath resources.
   */
  public Set<Class<?>> loadPackage(final String packageName, final boolean subPackages, final boolean initialize) throws PackageNotFoundException {
    return PackageLoader.loadPackage(packageName, subPackages, initialize, null, classLoaders);
  }

  private static Set<Class<?>> loadPackage(final String packageName, final boolean subPackages, final boolean initialize, final Predicate<Class<?>> predicate, final ClassLoader ... classLoaders) throws PackageNotFoundException {
    if (packageName == null)
      throw new NullPointerException("name == null");

    if (packageName.length() == 0)
      throw new IllegalArgumentException("packageName.length() == 0");

    final char firstChar = packageName.charAt(0);
    final Enumeration<Resource> resources;
    try {
      final String location = (firstChar == '/' || firstChar == '.' ? packageName.substring(1) : packageName).replace('.', '/');
      resources = Resources.getResources(location, classLoaders);
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
          PackageLoader.loadDirectory(resourceClassLoader, classNames, directory, packageName, subPackages);
        else
          PackageLoader.loadJar(resourceClassLoader, classNames, url, packageName, subPackages);

        for (final String className : classNames) {
          try {
            final Class<?> cls = Class.forName(className, initialize, resourceClassLoader);
            if (!initialize && predicate.test(cls))
              Class.forName(className, true, resourceClassLoader);

            classes.add(cls);
          }
          catch (final ClassNotFoundException | VerifyError e) {
            logger.warn("Loading package: " + packageName, e);
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

  private static void loadDirectory(final ClassLoader classLoader, final Set<String> classNames, final File directory, final String packageName, final boolean subPackages) throws IOException {
    final Path path = directory.toPath();
    final Consumer<Path> consumer = new Consumer<Path>() {
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

  private static void loadJar(final ClassLoader classLoader, final Set<String> classNames, final URL url, final String packageName, final boolean subPackages) throws PackageNotFoundException {
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
        if (className.startsWith(packagePrefix) && (subPackages || className.indexOf(".", packagePrefix.length() + 1) < 0))
          classNames.add(className);
      }
    }
  }
}