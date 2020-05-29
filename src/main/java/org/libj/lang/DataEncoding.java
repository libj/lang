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

import java.io.Serializable;

/**
 * Abstraction of a data encoding.
 *
 * @param <D> The type parameter representing the data class.
 * @param <E> The type parameter representing the encoding class.
 */
public abstract class DataEncoding<D extends Serializable,E extends Serializable> implements Serializable {
  private static final long serialVersionUID = -8326150018177745673L;

  protected volatile D data;
  protected volatile E encoded;

  /**
   * Creates a new {@link DataEncoding}.
   *
   * @param data The unencoded value.
   * @param encoded The encoded value.
   */
  public DataEncoding(final D data, final E encoded) {
    this.data = data;
    this.encoded = encoded;
  }

  /**
   * Returns the unencoded value.
   *
   * @return The unencoded value.
   */
  public abstract D getData();

  /**
   * Returns the encoded value.
   *
   * @return The encoded value.
   */
  public abstract E getEncoded();
}