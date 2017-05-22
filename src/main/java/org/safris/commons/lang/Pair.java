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

public final class Pair<A,B> {
  public final A a;
  public final B b;

  public Pair(final A a, final B b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Pair))
      return false;

    final Pair<?,?> that = (Pair<?,?>)obj;
    return (a != null ? a.equals(that.a) : that.a == null) && (b != null ? b.equals(that.b) : that.b == null);
  }

  @Override
  public int hashCode() {
    return (a != null ? a.hashCode() : 0) + (b != null ? b.hashCode() : 0);
  }
}