/* Copyright (c) 2017 lib4j
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

import java.lang.annotation.Annotation;

/**
 * Thrown to indicate an exception in a parameter value of an annotation.
 */
public class AnnotationParameterException extends RuntimeException {
  private static final long serialVersionUID = -5403129096514741253L;
  private final Annotation annotation;

  public AnnotationParameterException(final Annotation annotation) {
    super();
    this.annotation = annotation;
  }

  public AnnotationParameterException(final Annotation annotation, final String message) {
    super(message);
    this.annotation = annotation;
  }

  public Annotation annotation() {
    return this.annotation;
  }
}