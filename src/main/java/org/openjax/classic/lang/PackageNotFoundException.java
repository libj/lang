/* Copyright (c) 2006 OpenJAX
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

package org.openjax.classic.lang;

/**
 * Thrown when an application tries to load in a package using
 * {@link PackageLoader}, but no definition for the specified package could be
 * found.
 */
public final class PackageNotFoundException extends ReflectiveOperationException {
  private static final long serialVersionUID = 4963238462943629433L;

  /**
   * Creates a {@code PackageNotFoundException} with no detail message.
   */
  public PackageNotFoundException() {
    super();
  }

  /**
   * Creates a {@code PackageNotFoundException} with the specified detail
   * message.
   *
   * @param message The detail message.
   */
  public PackageNotFoundException(final String message) {
    super(message);
  }

  /**
   * Constructs a {@code PackageNotFoundException} with the specified exception
   * that was the cause of this exception.
   *
   * @param cause The exception that was the cause of this exception.
   */
  public PackageNotFoundException(final Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a {@code PackageNotFoundException} with the specified detail
   * message and exception that was the cause of this exception.
   *
   * @param message The detail message.
   * @param cause The exception that was the cause of this exception.
   */
  public PackageNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }
}