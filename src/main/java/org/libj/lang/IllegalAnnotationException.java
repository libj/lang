/* Copyright (c) 2017 LibJ
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

import java.lang.annotation.Annotation;

/**
 * Thrown to indicate that an illegal annotation was encountered.
 */
public class IllegalAnnotationException extends RuntimeException {
  private final Annotation annotation;

  /**
   * Creates an {@link IllegalAnnotationException} for the specified annotation.
   *
   * @param annotation The {@link Annotation} that is the subject of this exception.
   */
  public IllegalAnnotationException(final Annotation annotation) {
    super();
    this.annotation = annotation;
  }

  /**
   * Creates an {@link IllegalAnnotationException} for the specified annotation.
   *
   * @param annotation The {@link Annotation} that is the subject of this exception.
   * @param message The detail message.
   */
  public IllegalAnnotationException(final Annotation annotation, final String message) {
    super(message);
    this.annotation = annotation;
  }

  /**
   * Returns the {@link Annotation} that is the subject of this exception.
   *
   * @return The {@link Annotation} that is the subject of this exception.
   */
  public Annotation annotation() {
    return this.annotation;
  }
}