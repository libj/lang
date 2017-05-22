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

import java.net.URL;

public final class Resource {
  private final URL url;
  private final ClassLoader classLoader;

  public Resource(final URL url, final ClassLoader classLoader) {
    this.url = url;
    this.classLoader = classLoader;
  }

  public URL getURL() {
    return url;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;

    if (!(obj instanceof Resource))
      return false;

    final Resource resource = (Resource)obj;
    return url != null ? url.equals(resource.url) && (classLoader != null ? classLoader.equals(resource.classLoader) : resource.classLoader == null) : resource.url == null && (classLoader != null ? classLoader.equals(resource.classLoader) : resource.classLoader == null);
  }

  @Override
  public int hashCode() {
    return url.hashCode() * 3 + classLoader.hashCode();
  }
}