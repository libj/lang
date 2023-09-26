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

  /**
   * Returns the {@code int} value resulting from the system property indicated by the specified {@code primaryKey}, which if not
   * present is sought by the {@code fallbackKey}, which if not present results in the {@code defaultValue}.
   * <p>
   * First, if there is a security manager, its {@link SecurityManager#checkPropertyAccess(String)} method is called with the key as
   * its argument. This may result in a {@link SecurityException}.
   *
   * @param primaryKey The name of the system property to find first.
   * @param fallbackKey The name of the system property to find if a value for {@code primaryKey} does not exist.
   * @param defaultValue The value to return if a value for {@code primaryKey} nor {@code fallbackKey} exists.
   * @return The value resulting from the system property indicated by the specified {@code primaryKey}, which if not present is
   *         sought by the {@code fallbackKey}, which if not present results in the {@code defaultValue}.
   * @throws SecurityException If a security manager exists and its {@link SecurityManager#checkPermission checkPermission} method
   *           doesn't allow access to the {@code primaryKey} or {@code fallbackKey}.
   * @see System#getProperty(String)
   * @implNote If a value for {@code primaryKey} or {@code fallbackKey} exist, but does not represent a parsable {@code int}, this
   *           method will return {@code defaultValue}.
   */
  public static int getProperty(final String primaryKey, final String fallbackKey, final int defaultValue) throws SecurityException {
    final String value = System.getProperty(primaryKey);
    if (value != null)
      return Numbers.parseInt(value, defaultValue);

    return getProperty(fallbackKey, defaultValue);
  }

  /**
   * Returns the {@code int} value resulting from the system property indicated by the specified {@code key}, which if not present
   * results in the {@code defaultValue}.
   * <p>
   * First, if there is a security manager, its {@link SecurityManager#checkPropertyAccess(String)} method is called with the key as
   * its argument. This may result in a {@link SecurityException}.
   *
   * @param key The name of the system property to find.
   * @param defaultValue The value to return if a value for {@code key} exists.
   * @return The value resulting from the system property indicated by the specified {@code key}, which if not present results in the
   *         {@code defaultValue}.
   * @throws SecurityException If a security manager exists and its {@link SecurityManager#checkPermission checkPermission} method
   *           doesn't allow access to the {@code key}.
   * @see System#getProperty(String)
   * @implNote If a value for {@code key} exist, but does not represent a parsable {@code int}, this method will return
   *           {@code defaultValue}.
   */
  public static int getProperty(final String key, final int defaultValue) {
    final String value = System.getProperty(key);
    if (value != null)
      return Numbers.parseInt(value, defaultValue);

    return defaultValue;
  }

  /**
   * Returns the {@code boolean} value resulting from the system property indicated by the specified {@code primaryKey}, which if not
   * present is sought by the {@code fallbackKey}, which if not present results in the {@code defaultValue}.
   * <p>
   * First, if there is a security manager, its {@link SecurityManager#checkPropertyAccess(String)} method is called with the key as
   * its argument. This may result in a {@link SecurityException}.
   *
   * @param primaryKey The name of the system property to find first.
   * @param fallbackKey The name of the system property to find if a value for {@code primaryKey} does not exist.
   * @param defaultValue The value to return if a value for {@code primaryKey} nor {@code fallbackKey} exists.
   * @return The value resulting from the system property indicated by the specified {@code primaryKey}, which if not present is
   *         sought by the {@code fallbackKey}, which if not present results in the {@code defaultValue}.
   * @throws SecurityException If a security manager exists and its {@link SecurityManager#checkPermission checkPermission} method
   *           doesn't allow access to the {@code primaryKey} or {@code fallbackKey}.
   * @see System#getProperty(String)
   * @implNote If a value for {@code primaryKey} or {@code fallbackKey} exist, but does not represent a parsable {@code boolean}, this
   *           method will return {@code defaultValue}.
   */
  public static boolean getProperty(final String primaryKey, final String fallbackKey, final boolean defaultValue) {
    final String value = System.getProperty(primaryKey);
    if (value != null)
      return Booleans.parseBoolean(value, defaultValue);

    return getProperty(fallbackKey, defaultValue);
  }

  /**
   * Returns the {@code boolean} value resulting from the system property indicated by the specified {@code key}, which if not present
   * results in the {@code defaultValue}.
   * <p>
   * First, if there is a security manager, its {@link SecurityManager#checkPropertyAccess(String)} method is called with the key as
   * its argument. This may result in a {@link SecurityException}.
   *
   * @param key The name of the system property to find.
   * @param defaultValue The value to return if a value for {@code key} exists.
   * @return The value resulting from the system property indicated by the specified {@code key}, which if not present results in the
   *         {@code defaultValue}.
   * @throws SecurityException If a security manager exists and its {@link SecurityManager#checkPermission checkPermission} method
   *           doesn't allow access to the {@code key}.
   * @see System#getProperty(String)
   * @implNote If a value for {@code key} exist, but does not represent a parsable {@code boolean}, this method will return
   *           {@code defaultValue}.
   */
  public static boolean getProperty(final String key, final boolean defaultValue) {
    final String value = System.getProperty(key);
    if (value != null)
      return Booleans.parseBoolean(value, defaultValue);

    return defaultValue;
  }

  /**
   * Returns the {@code String} value resulting from the system property indicated by the specified {@code primaryKey}, which if not
   * present is sought by the {@code fallbackKey}, which if not present results in {@code null}.
   * <p>
   * First, if there is a security manager, its {@link SecurityManager#checkPropertyAccess(String)} method is called with the key as
   * its argument. This may result in a {@link SecurityException}.
   *
   * @param primaryKey The name of the system property to find first.
   * @param fallbackKey The name of the system property to find if a value for {@code primaryKey} does not exist.
   * @return The value resulting from the system property indicated by the specified {@code primaryKey}, which if not present is
   *         sought by the {@code fallbackKey}, which if not present results in {@code null}.
   * @throws SecurityException If a security manager exists and its {@link SecurityManager#checkPermission checkPermission} method
   *           doesn't allow access to the {@code primaryKey} or {@code fallbackKey}.
   * @see System#getProperty(String)
   */
  public static String getProperty(final String primaryKey, final String fallbackKey) {
    final String value = System.getProperty(primaryKey);
    return value != null ? value : System.getProperty(fallbackKey);
  }

  private Systems() {
  }
}