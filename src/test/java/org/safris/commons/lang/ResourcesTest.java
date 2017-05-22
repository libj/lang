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

package org.safris.commons.lang;

import java.io.File;
import java.util.Enumeration;

import org.junit.Assert;
import org.junit.Test;

public class ResourcesTest {
  private static final File JAVA_HOME = new File(System.getProperty("java.home").replace(" ", "%20"));
  private static final File RT_JAR;

  static {
    try {
      if (System.getProperty("os.name").contains("Mac"))
        RT_JAR = new File(JAVA_HOME, "../jre/lib/rt.jar").getCanonicalFile();
      else
        RT_JAR = new File(JAVA_HOME, "lib/rt.jar");
    }
    catch (final Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  @Test
  public void testGetLocationBase() {
    Assert.assertNull(Resources.getLocationBase(null));
    Assert.assertTrue(Resources.getLocationBase(ResourcesTest.class).isDirectory());
    Assert.assertEquals(RT_JAR, Resources.getLocationBase(String.class));
  }

  @Test
  public void testGetResource() throws Exception {
    Assert.assertNull(Resources.getResource(null));
    Assert.assertNull(Resources.getResource(""));
    Assert.assertTrue(Resources.getResource("META-INF").getURL().toString().endsWith(".jar!/META-INF"));
  }

  @Test
  public void testGetResources() throws Exception {
    try {
      Assert.assertNull(Resources.getResources(null));
      Assert.fail("Expected NPE");
    }
    catch (final Exception e) {
    }

    final Enumeration<Resource> resources = Resources.getResources("META-INF");
    boolean found = false;
    while (resources.hasMoreElements()) {
      final Resource resource = resources.nextElement();
      if (!resource.getURL().toString().endsWith("!/META-INF"))
        continue;

      found = true;
      break;
    }

    Assert.assertTrue(found);
  }
}