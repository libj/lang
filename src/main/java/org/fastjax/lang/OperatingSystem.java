/* Copyright (c) 2018 FastJAX
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

package org.fastjax.lang;

class Sys {
  public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
}

/**
 * Enum representing the host operating system of the java runtime.
 */
public enum OperatingSystem {
  /**
   * Enum representing the Windows operating system.
   */
  WINDOWS(Sys.OS_NAME.indexOf("win") != -1),
  /**
   * Enum representing the UNIX operating system.
   */
  UNIX(Sys.OS_NAME.indexOf("nix") != -1 || Sys.OS_NAME.indexOf("nux") != -1 || Sys.OS_NAME.indexOf("aix") != -1),
  /**
   * Enum representing the Solaris operating system.
   */
  SOLARIS(Sys.OS_NAME.indexOf("sunos") != -1),
  /**
   * Enum representing the Mac operating system.
   */
  MAC(Sys.OS_NAME.indexOf("mac") != -1);

  /**
   * Returns the {@code OperatingSystem} enum representing the host operating
   * system of the current java process.
   *
   * @return The {@code OperatingSystem} enum representing the host operating
   * system of the current java process.
   */
  public static OperatingSystem get() {
    return current;
  }

  private static OperatingSystem current;

  OperatingSystem(final boolean token) {
    if (token)
      setCurrent();
  }

  private void setCurrent() {
    current = this;
  }

  /**
   * Returns whether this {@code OperatingSystem} represents a Windows operating
   * system.
   *
   * @return Whether this {@code OperatingSystem} represents a Windows operating
   *         system.
   */
  public boolean isWindows() {
    return this == WINDOWS;
  }

  /**
   * Returns whether this {@code OperatingSystem} represents a UNIX operating
   * system.
   *
   * @return Whether this {@code OperatingSystem} represents a UNIX operating
   *         system.
   */
  public boolean isUnix() {
    return this == UNIX;
  }

  /**
   * Returns whether this {@code OperatingSystem} represents a Solaris operating
   * system.
   *
   * @return Whether this {@code OperatingSystem} represents a Solaris operating
   *         system.
   */
  public boolean isSolaris() {
    return this == SOLARIS;
  }

  /**
   * Returns whether this {@code OperatingSystem} represents a Mac operating
   * system.
   *
   * @return Whether this {@code OperatingSystem} represents a Mac operating
   *         system.
   */
  public boolean isMac() {
    return this == MAC;
  }
}