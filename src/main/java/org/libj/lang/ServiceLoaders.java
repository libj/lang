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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * Supplementary convenience functions related to {@link java.util.ServiceLoader} class.
 */
public final class ServiceLoaders {
  private static void fail(final Class<?> service, final String message, final Throwable cause) throws ServiceConfigurationError {
    throw new ServiceConfigurationError(service.getName() + ": " + message, cause);
  }

  private static void fail(final Class<?> service, final String message) throws ServiceConfigurationError {
    throw new ServiceConfigurationError(service.getName() + ": " + message);
  }

  private static void fail(final Class<?> service, final URL resource, final int line, final String message) throws ServiceConfigurationError {
    fail(service, resource + ":" + line + ": " + message);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void add(final Class<?> service, final String providerName, final Consumer consumer, final ClassLoader classLoader) {
    try {
      consumer.accept(Class.forName(providerName, false, classLoader));
    }
    catch (final ClassNotFoundException e) {
      fail(service, "Provider " + providerName + " not found");
    }
    catch (final Throwable t) {
      fail(service, "Provider " + providerName + " could not be instantiated", t);
    }
  }

  /**
   * Parse a single line from the given configuration file, adding the name on the line to set of names if not already seen.
   * Copied from ServiceLoader#parseLine.
   */
  private static <S>int parseLine(final Class<S> service, final URL resource, final BufferedReader reader, final int lineCount, final Consumer<? extends Class<? super S>> consumer, final ClassLoader classLoader) throws IOException {
    String line = reader.readLine();
    if (line == null)
      return -1;

    int ci = line.indexOf('#');
    if (ci >= 0)
      line = line.substring(0, ci);

    line = line.trim();
    final int n = line.length();
    if (n != 0) {
      if (line.indexOf(' ') >= 0 || line.indexOf('\t') >= 0)
        fail(service, resource, lineCount, "Illegal configuration-file syntax");

      int codePoint = line.codePointAt(0);
      if (!Character.isJavaIdentifierStart(codePoint))
        fail(service, resource, lineCount, "Illegal provider-class name: " + line);

      for (int i = Character.charCount(codePoint); i < n; i += Character.charCount(codePoint)) { // [ST]
        codePoint = line.codePointAt(i);
        if (!Character.isJavaIdentifierPart(codePoint) && codePoint != '.')
          fail(service, resource, lineCount, "Illegal provider-class name: " + line);
      }

      add(service, line, consumer, classLoader);
    }

    return lineCount + 1;
  }

  /**
   * Parse the content of the given URL as a provider-configuration file.
   */
  private static <S>void parse(final URL resource, final Class<S> service, final Consumer<? extends Class<? super S>> consumer, final ClassLoader classLoader) {
    try {
      final URLConnection connection = resource.openConnection();
      connection.setUseCaches(false);
      try (InputStream in = connection.getInputStream(); BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
        for (int lc = 1; (lc = parseLine(service, resource, r, lc, consumer, classLoader)) >= 0;); // [ST]
      }
    }
    catch (final IOException e) {
      fail(service, "Error accessing configuration file", e);
    }
  }

  /**
   * Finds the Service Providers for the given {@code service} type from the context class loader, and invokes the specified
   * {@code consumer} for each found provider class.
   *
   * @param <S> The type parameter of the service class.
   * @param service The interface or abstract class representing the service.
   * @param consumer The callback {@link Consumer} to be called for each found provider class.
   * @throws IOException If an I/O error has occurred.
   * @throws ServiceConfigurationError If something goes wrong while loading a service provider.
   * @see ServiceLoader#load(Class)
   */
  public static <S>void load(final Class<S> service, final Consumer<? extends Class<? super S>> consumer) throws IOException {
    load(service, Thread.currentThread().getContextClassLoader(), consumer);
  }

  /**
   * Finds the Service Providers for the given {@code service} type from the provided {@code classLoader}, and invokes the specified
   * {@code consumer} for each found provider class.
   *
   * @param <S> The type parameter of the service class.
   * @param service The interface or abstract class representing the service.
   * @param classLoader The {@link ClassLoader} to be used to load provider-configuration files and provider classes, or
   *          {@code null} if the system class loader (or, failing that, the bootstrap class loader) is to be used.
   * @param consumer The callback {@link Consumer} to be called for each found provider class.
   * @throws IOException If an I/O error has occurred.
   * @throws ServiceConfigurationError If something goes wrong while loading a service provider.
   * @see ServiceLoader#load(Class,ClassLoader)
   */
  public static <S>void load(final Class<S> service, final ClassLoader classLoader, final Consumer<? extends Class<? super S>> consumer) throws IOException {
    final String resourceName = "META-INF/services/" + service.getName();
    final Enumeration<URL> resources = classLoader == null ? ClassLoader.getSystemResources(resourceName) : classLoader.getResources(resourceName);
    while (resources.hasMoreElements())
      parse(resources.nextElement(), service, consumer, classLoader);
  }

  private ServiceLoaders() {
  }
}