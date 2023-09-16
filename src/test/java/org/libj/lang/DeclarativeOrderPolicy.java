/* Copyright (c) 2023 LibJ
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
import java.lang.reflect.Method;

public enum DeclarativeOrderPolicy {
  DECLARED_SUPER_FIRST(false, true),
  DECLARED_SUPER_LAST(false, false),
  PUBLIC_SUPER_FIRST(true, true),
  PUBLIC_SUPER_LAST(true, false);

  private final boolean publicOnly;
  private final boolean superFirst;

  private DeclarativeOrderPolicy(final boolean publicOnly, final boolean superFirst) {
    this.publicOnly = publicOnly;
    this.superFirst = superFirst;
  }

  public boolean isPublicOnly() {
    return publicOnly;
  }

  public boolean isSuperFirst() {
    return superFirst;
  }

  public Method[] getMethods(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return publicOnly ? Classes.getMethodsWithAnnotation(cls, annotationType) : Classes.getDeclaredMethodsWithAnnotationDeep(cls, annotationType);
  }
}