/* Copyright (c) 2022 LibJ
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

/**
 * Utility functions that provide common operations pertaining to {@link System}.
 */
public final class Systems {
  /**
   * Returns {@code true} if there exists a system property specified by the {@code key} that is set to anything other than
   * {@code "false"}, otherwise {@code false}.
   *
   * @param key The name of the system property.
   * @return {@code true} if there exists a system property specified by the {@code key} that is set to anything other than
   *         {@code "false"}, otherwise {@code false}.
   */
  public static boolean hasProperty(final String key) {
    final String prop = System.getProperty(key);
    return prop != null && !prop.equals("false");
  }

  private Systems() {
  }
}