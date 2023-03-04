/* Copyright (c) 2020 LibJ
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

import java.util.UUID;

/**
 * Utility functions for operations pertaining to {@link UUID}.
 */
public final class UUIDs {
  /**
   * Returns a string representation of the specified {@link UUID} with its dashes removed, making it 32 characters in length.
   *
   * @param uuid The {@link UUID}.
   * @return A string representation of the specified {@link UUID} with its dashes removed, making it 32 characters in length.
   * @throws NullPointerException If {@code uuid} is null.
   */
  public static String toString32(final UUID uuid) {
    final String str = uuid.toString();
    return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24, 36);
  }

  private UUIDs() {
  }
}