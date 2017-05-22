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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class PathsTest {
  @Test
  public void testIsLocal() {
    // Local UNIX
    Assert.assertTrue(Paths.isLocal("/etc/profile"));
    Assert.assertTrue(Paths.isLocal("//etc/profile"));
    Assert.assertTrue(Paths.isLocal("file:/etc/profile"));
    Assert.assertTrue(Paths.isLocal("file:///etc/profile"));

    // Local Windows
    Assert.assertTrue(Paths.isLocal("/C:/Windows"));
    Assert.assertTrue(Paths.isLocal("file:///C:/Windows"));
    Assert.assertTrue(Paths.isLocal("file:/C:/Windows"));

    // Remote
    Assert.assertFalse(Paths.isLocal("http://www.google.com"));
    Assert.assertFalse(Paths.isLocal("ftp://ftp.google.com"));
  }

  @Test
  public void testNewPath() {
    // UNIX
    Assert.assertEquals("/etc/profile", Paths.newPath("/etc/", "profile"));
    Assert.assertEquals("/etc/profile", Paths.newPath("/etc", "profile"));
    Assert.assertEquals("/etc/profile", Paths.newPath("/etc", "/profile"));
    Assert.assertEquals("/etc/profile", Paths.newPath("/etc/", "/profile"));

    // Windows
    Assert.assertEquals("C:\\Windows\\System32", Paths.newPath("C:\\Windows", "System32"));
    Assert.assertEquals("C:\\Windows\\System32", Paths.newPath("C:\\Windows", "\\System32"));
    Assert.assertEquals("C:\\Windows\\System32", Paths.newPath("C:\\Windows\\", "System32"));
    Assert.assertEquals("C:\\Windows\\System32", Paths.newPath("C:\\Windows\\", "\\System32"));

    // Web
    Assert.assertEquals("http://www.google.com/images", Paths.newPath("http://www.google.com", "images"));
    Assert.assertEquals("http://www.google.com/images", Paths.newPath("http://www.google.com/", "images"));
    Assert.assertEquals("http://www.google.com/images", Paths.newPath("http://www.google.com", "/images"));
    Assert.assertEquals("http://www.google.com/images", Paths.newPath("http://www.google.com/", "/images"));
    Assert.assertEquals("ftp://www.google.com/files", Paths.newPath("ftp://www.google.com", "files"));
  }

  @Test
  public void testGetName() throws Exception {
    final Map<String,String> paths = new HashMap<String,String>();
    paths.put("share", "file:///usr/share/../share");
    paths.put("lib", "file:///usr/share/../share/../lib");
    paths.put("var", "/usr/share/../share/../lib/../../var");
    paths.put("var", "/usr/share/../share/../lib/../../var/");
    paths.put("resolv.conf", "/etc/resolv.conf");
    paths.put("name", "name");

    for (final Map.Entry<String,String> entry : paths.entrySet())
      Assert.assertEquals(entry.getKey(), Paths.getName(entry.getValue()));
  }

  @Test
  public void testGetParent() throws Exception {
    final Map<String,String> urls = new HashMap<String,String>();
    urls.put("file:///usr", "file:///usr/share/../share");
    urls.put("/usr", "/usr/share/../share/..");
    urls.put(null, "arp/../pom.xml");
    urls.put("", "/usr/share/../share/../../");
    urls.put("file:///usr/local", "file:///usr/local/bin/../lib/../bin");

    for (final Map.Entry<String,String> entry : urls.entrySet())
      Assert.assertEquals(entry.getKey(), Paths.getParent(entry.getValue()));
  }
}