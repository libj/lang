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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageLoaderTest {
  private static class TestClassLoader extends ClassLoader {
    public boolean isClassLoaded(final String className) {
      return findLoadedClass(className) != null;
    }
  };

  private static final Logger logger = LoggerFactory.getLogger(PackageLoaderTest.class);
  private static final TestClassLoader classLoader = new TestClassLoader();

  @Test
  public void testPackageLoader() throws PackageNotFoundException {
    final String[] testClasses = {
      "org.junit.PackageLoaderClass1",
      "org.junit.PackageLoaderClass2",
      "org.junit.PackageLoaderClass3",
      "org.junit.runners.Parameterized"
    };

    for (final String testClass : testClasses)
      Assert.assertFalse(testClass, classLoader.isClassLoaded(testClass));

    final Set<Class<?>> loadedClasses = PackageLoader.getPackageLoader(classLoader).loadPackage("org.junit");
    final Set<String> classNames = new HashSet<>();
    for (final Class<?> loadedClass : loadedClasses)
      classNames.add(loadedClass.getName());

    for (final String testClass : testClasses) {
      logger.debug(testClass);
      Assert.assertTrue(testClass, classNames.contains(testClass));
      Assert.assertTrue(testClass, classLoader.isClassLoaded(testClass));
    }

    try {
      PackageLoader.getContextPackageLoader().loadPackage((String)null);
      Assert.fail("Expected NullPointerException");
    }
    catch (final NullPointerException e) {
    }
  }

  @Test
  public void testJar() throws ClassNotFoundException, PackageNotFoundException {
    boolean[] encountered = new boolean[1];
    final Set<Class<?>> loadedClasses = PackageLoader.getContextPackageLoader().loadPackage("org.junit.runner", new Predicate<Class<?>>() {
      @Override
      public boolean test(final Class<?> t) {
        return encountered[0] = "org.junit.runner.FilterFactory".equals(t.getName()) || encountered[0];
      }
    });
    Assert.assertTrue("Should have been loaded by PackageLoader", loadedClasses.contains(Class.forName("org.junit.runner.FilterFactory", false, ClassLoader.getSystemClassLoader())));
    Assert.assertTrue(encountered[0]);
  }
}