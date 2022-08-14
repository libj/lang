/* Copyright (c) 2018 LibJ
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

final class Sys {
  static final String OS_NAME = System.getProperty("os.name").toLowerCase();

  private Sys() {
  }
}

/**
 * Enum representing the host operating system of the java runtime.
 */
public enum OperatingSystem {
  /** Enum representing the Windows operating system. */
  WINDOWS(Sys.OS_NAME.contains("win")),
  /** Enum representing the UNIX operating system. */
  UNIX(Sys.OS_NAME.contains("nix") || Sys.OS_NAME.contains("nux") || Sys.OS_NAME.contains("aix")),
  /** Enum representing the Solaris operating system. */
  SOLARIS(Sys.OS_NAME.contains("sunos")),
  /** Enum representing the Mac operating system. */
  MAC(Sys.OS_NAME.contains("mac"));

  /**
   * Returns the {@link OperatingSystem} enum representing the host operating system of the current java process.
   *
   * @return The {@link OperatingSystem} enum representing the host operating system of the current java process.
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
   * States whether this instance represents a Windows operating system.
   *
   * @return {@code true} if this instance represents a Windows operating system; {@code false} otherwise.
   */
  public boolean isWindows() {
    return this == WINDOWS;
  }

  /**
   * States whether this instance represents a UNIX operating system.
   *
   * @return {@code true} if this instance represents a UNIX operating system; {@code false} otherwise.
   */
  public boolean isUnix() {
    return this == UNIX;
  }

  /**
   * States whether this instance represents a Solaris operating system.
   *
   * @return {@code true} if this instance represents a Solaris operating system; {@code false} otherwise.
   */
  public boolean isSolaris() {
    return this == SOLARIS;
  }

  /**
   * States whether this instance represents a Mac operating system.
   *
   * @return {@code true} if this instance represents a Mac operating system; {@code false} otherwise.
   */
  public boolean isMac() {
    return this == MAC;
  }
}