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

public final class PackageNotFoundException extends Exception {
  private static final long serialVersionUID = 4963238462943629433L;

  /**
   * This field holds the exception cause if the
   * PackageNotFoundException(final String s, final Throwable cause) constructor was
   * used to instantiate the object
   * @serial
   */
  private Throwable cause;

  /**
   * Constructs a <code>PackageNotFoundException</code> with no detail message.
   */
  public PackageNotFoundException() {
    super((Throwable)null);  // Disallow initCause
  }

  /**
   * Constructs a <code>PackageNotFoundException</code> with the
   * specified detail message.
   *
   * @param   message   the detail message.
   */
  public PackageNotFoundException(final String message) {
    super(message, null);  //  Disallow initCause
  }

  /**
   * Constructs a <code>PackageNotFoundException</code> with the
   * specified detail message and optional exception that was
   * raised while loading the class.
   *
   * @param message the detail message
   * @param cause the exception that was raised while loading the class
   */
  public PackageNotFoundException(final String message, final Throwable cause) {
    super(message, null);  //  Disallow initCause
    this.cause = cause;
  }

  /**
   * Returns the cause of this exception (final the exception that was raised
   * if an error occurred while attempting to load the class; otherwise
   * <tt>null</tt>).
   *
   * @return  the cause of this exception.
   */
  @Override
  public Throwable getCause() {
    return cause;
  }
}