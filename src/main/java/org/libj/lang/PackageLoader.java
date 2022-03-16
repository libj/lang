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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link PackageLoader} is a class used to discover and to load classes in a package. Given a package name, the
 * {@link PackageLoader} attempts to locate and to optionally load the classes of the package.
 */
public class PackageLoader {
  private static final Logger logger = LoggerFactory.getLogger(PackageLoader.class);
  private static final ClassLoader bootLoaderProxy = new URLClassLoader(new URL[0], null);
  private static final ConcurrentHashMap<ClassLoader,PackageLoader> instances = new ConcurrentHashMap<>();

  /**
   * Returns a {@link PackageLoader} that uses the system class loader for the discovery of packages and the loading of their
   * classes.
   *
   * @return The {@link PackageLoader} that uses the system class loader for the discovery of packages and the loading of their
   *         classes.
   */
  public static PackageLoader getSystemPackageLoader() {
    return getPackageLoader(ClassLoader.getSystemClassLoader());
  }

  /**
   * Returns a {@link PackageLoader} that uses the context class loader for the discovery of packages and the loading of their
   * classes.
   *
   * @return The {@link PackageLoader} that uses the context class loader for the discovery of packages and the loading of their
   *         classes.
   */
  public static PackageLoader getContextPackageLoader() {
    return getPackageLoader(Thread.currentThread().getContextClassLoader());
  }

  /**
   * Returns a {@link PackageLoader} that uses the specified {@link ClassLoader} for the discovery of packages and the loading of
   * their classes.
   *
   * @param classLoader The {@link ClassLoader} to be used for the discovery of packages and the loading of their classes.
   * @return The {@link PackageLoader} that uses the specified {@code classLoader} for the discovery of packages and the loading of
   *         their classes.
   */
  public static PackageLoader getPackageLoader(final ClassLoader classLoader) {
    PackageLoader packageLoader = instances.get(classLoader);
    if (packageLoader == null)
      instances.put(classLoader, packageLoader = new PackageLoader(classLoader));

    return packageLoader;
  }

  private final ClassLoader classLoader;

  protected PackageLoader(final ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * Load and initialize each class in the specified {@link Package} and its sub-packages.
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param pkg The {@link Package}.
   * @return Set of classes discovered, loaded, and initialized.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If {@code pkg} cannot be found in the class loader of this {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code pkg} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public Set<Class<?>> loadPackage(final Package pkg) throws IOException, PackageNotFoundException {
    return loadPackage(assertNotNull(pkg).getName(), true, true);
  }

  /**
   * Load and initialize each class in the specified {@link Package} and its sub-packages.
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param pkg The {@link Package}.
   * @param initialize {@link Predicate} specifying which discovered classes to initialize, or {@code null} to initialize discovered
   *          all classes.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If {@code pkg} cannot be found in the class loader of this {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code pkg} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public void loadPackage(final Package pkg, final Predicate<? super Class<?>> initialize) throws IOException, PackageNotFoundException {
    loadPackage(assertNotNull(pkg).getName(), true, false, initialize, classLoader);
  }

  /**
   * Load and initialize each class in the specified {@link Package} and its sub-packages.
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param pkg The {@link Package}.
   * @param initialize If {@code true}, initialize discovered classes; if {@code false}, do not initialize discovered classes.
   * @return Set of discovered classes, whether they were initialized or not.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If {@code pkg} cannot be found in the class loader of this {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code pkg} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public Set<Class<?>> loadPackage(final Package pkg, final boolean initialize) throws IOException, PackageNotFoundException {
    return loadPackage(assertNotNull(pkg).getName(), true, initialize);
  }

  /**
   * Load and initialize each class in the specified {@link Package}.
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param pkg The {@link Package}.
   * @param includeSubPackages If {@code true}, classes from sub-packages of the package specified by {@code name} will be included
   *          in the returned {@link Set} (regardless of the value of {@code initialize}); if {@code false}, classes of sub-packages
   *          will not be included.
   * @param initialize If {@code true}, initialize discovered classes; if {@code false}, do not initialize discovered classes.
   * @return Set of discovered classes, whether they were initialized or not.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If {@code pkg} cannot be found in the class loader of this {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code pkg} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public Set<Class<?>> loadPackage(final Package pkg, final boolean includeSubPackages, final boolean initialize) throws IOException, PackageNotFoundException {
    return loadPackage(assertNotNull(pkg).getName(), includeSubPackages, initialize);
  }

  /**
   * Load and initialize each class in the package specified by {@code name} (including classes in sub-packages).
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param name The name of the package.
   * @return Set of classes discovered, loaded, and initialized.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If the package specified by {@code name} cannot be found in the class loader of this
   *           {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code name} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public Set<Class<?>> loadPackage(final String name) throws IOException, PackageNotFoundException {
    return loadPackage(name, true, true);
  }

  /**
   * Load and initialize each class in the package specified by {@code name} (including classes in sub-packages).
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param name The name of the package.
   * @param initialize If {@code true}, initialize discovered classes; if {@code false}, do not initialize discovered classes.
   * @return Set of classes discovered, loaded, and initialized.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If the package specified by {@code name} cannot be found in the class loader of this
   *           {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code name} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public Set<Class<?>> loadPackage(final String name, final boolean initialize) throws IOException, PackageNotFoundException {
    return loadPackage(name, true, initialize);
  }

  /**
   * Load and initialize each class in the package specified by {@code name} (including classes in sub-packages).
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param name The name of the package.
   * @param initialize {@link Predicate} specifying which discovered classes to initialize, or {@code null} to initialize discovered
   *          all classes.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If the package specified by {@code name} cannot be found in the class loader of this
   *           {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code name} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public void loadPackage(final String name, final Predicate<? super Class<?>> initialize) throws IOException, PackageNotFoundException {
    loadPackage(name, true, false, initialize, classLoader);
  }

  /**
   * Load and initialize each class in the package specified by {@code name}.
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param name The name of the package.
   * @param includeSubPackages If {@code true}, classes from sub-packages of the package specified by {@code name} will be included
   *          in the returned {@link Set} (regardless of the value of {@code initialize}); if {@code false}, classes of sub-packages
   *          will not be included.
   * @param initialize {@link Predicate} specifying which discovered classes to initialize, or {@code null} to initialize discovered
   *          all classes.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If the package specified by {@code name} cannot be found in the class loader of this
   *           {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code name} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public void loadPackage(final String name, final boolean includeSubPackages, final Predicate<? super Class<?>> initialize) throws IOException, PackageNotFoundException {
    loadPackage(name, includeSubPackages, false, initialize, classLoader);
  }

  /**
   * Load and initialize each class in the package specified by {@code name}.
   * <p>
   * Packages are discovered by searching for path resources in the {@link PackageLoader#classLoader} of this {@link PackageLoader}.
   * If the package exists in multiple locations, such as jar files and/or directories, each of the resource locations will be used
   * to load all classes from each resource.
   *
   * @param name The name of the package.
   * @param includeSubPackages If {@code true}, classes from sub-packages of the package specified by {@code name} will be included
   *          in the returned {@link Set} (regardless of the value of {@code initialize}); if {@code false}, classes of sub-packages
   *          will not be included.
   * @param initialize If {@code true}, initialize discovered classes; if {@code false}, do not initialize discovered classes.
   * @return Set of classes discovered, loaded, and initialized.
   * @throws IOException If an I/O error has occurred.
   * @throws PackageNotFoundException If the package specified by {@code name} cannot be found in the class loader of this
   *           {@link PackageLoader} instance.
   * @throws IllegalArgumentException If {@code name} is null.
   * @see Class#forName(String,boolean,ClassLoader)
   */
  public Set<Class<?>> loadPackage(final String name, final boolean includeSubPackages, final boolean initialize) throws IOException, PackageNotFoundException {
    final Set<Class<?>> classes = new HashSet<>();
    loadPackage(name, includeSubPackages, initialize, (final Class<?> cls) -> {
      classes.add(cls);
      return true;
    }, classLoader);
    return classes;
  }

  private static void loadPackage(final String packageName, final boolean includeSubPackages, final boolean initialize, final Predicate<? super Class<?>> predicate, final ClassLoader classLoader) throws IOException, PackageNotFoundException {
    final ClassLoader loader = classLoader != null ? classLoader : bootLoaderProxy;
    final String resourceName = assertNotNull(packageName).replace('.', '/');
    final Enumeration<URL> resources = loader.getResources(resourceName);
    if (!resources.hasMoreElements())
      throw new PackageNotFoundException(packageName.length() > 0 ? packageName : "<default>");

    Resources.traverse(resources, resourceName, includeSubPackages, (final URL root, final String path, final boolean isDirectory) -> {
      if (isDirectory || !path.endsWith(".class"))
        return true;

      try {
        final String className = path.substring(0, path.length() - 6).replace('/', '.');
        final Class<?> cls = Class.forName(className, initialize, loader);
        if (predicate != null && predicate.test(cls))
          Class.forName(className, true, loader);
      }
      catch (final ClassNotFoundException | VerifyError e) {
        if (logger.isTraceEnabled())
          logger.trace("Problem loading package: " + (packageName.length() > 0 ? packageName : "<default>"), e);
      }
      catch (final NoClassDefFoundError ignored) {
      }

      return true;
    });
  }
}