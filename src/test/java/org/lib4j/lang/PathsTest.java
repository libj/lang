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
  public void testGetName() {
    Assert.assertEquals(null, Paths.getName(null));
    Assert.assertEquals("", Paths.getName(""));
    Assert.assertEquals("share.txt", Paths.getName("file:///usr/share/../share.txt"));
    Assert.assertEquals("lib", Paths.getName("file:///usr/share/../share/../lib"));
    Assert.assertEquals("var.old", Paths.getName("/usr/share/../share/../lib/../../var.old"));
    Assert.assertEquals("var", Paths.getName("/usr/share/../share/../lib/../../var/"));
    Assert.assertEquals("resolv.conf", Paths.getName("/etc/resolv.conf"));
    Assert.assertEquals("name", Paths.getName("name"));
  }

  @Test
  public void testGetShortName() {
    Assert.assertEquals(null, Paths.getShortName(null));
    Assert.assertEquals("", Paths.getShortName(""));
    Assert.assertEquals("share", Paths.getShortName("file:///usr/share/../share.txt"));
    Assert.assertEquals("lib", Paths.getShortName("file:///usr/share/../share/../lib"));
    Assert.assertEquals("var", Paths.getShortName("/usr/share/../share/../lib/../../var.old"));
    Assert.assertEquals("var", Paths.getShortName("/usr/share/../share/../lib/../../var/"));
    Assert.assertEquals("resolv", Paths.getShortName("/etc/resolv.conf"));
    Assert.assertEquals("name", Paths.getShortName("name"));
  }

  @Test
  public void testGetParent() {
    Assert.assertEquals("file:///usr/share/..", Paths.getParent("file:///usr/share/../share"));
    Assert.assertEquals("/usr/share/../share", Paths.getParent("/usr/share/../share/.."));
    Assert.assertEquals("arp/..", Paths.getParent("arp/../pom.xml"));
    Assert.assertEquals("/usr/share/../share/..", Paths.getParent("/usr/share/../share/../../"));
    Assert.assertEquals("file:///usr/local/bin/../lib/..", Paths.getParent("file:///usr/local/bin/../lib/../bin"));
  }

  @Test
  public void testGetCanonicalParent() {
    Assert.assertEquals("file:///usr", Paths.getCanonicalParent("file:///usr/share/../share"));
    Assert.assertEquals("/usr", Paths.getCanonicalParent("/usr/share/../share/.."));
    Assert.assertEquals(null, Paths.getCanonicalParent("arp/../pom.xml"));
    Assert.assertEquals("", Paths.getCanonicalParent("/usr/share/../share/../../"));
    Assert.assertEquals("file:///usr/local", Paths.getCanonicalParent("file:///usr/local/bin/../lib/../bin"));
  }
}