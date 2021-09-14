/* Copyright (c) 2006 LibJ
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Utility providing implementations of methods missing from the API of
 * {@link Class}.
 */
public final class Classes {
  private static final Logger logger = LoggerFactory.getLogger(Classes.class);

  /**
   * Returns the name of the declaring class of the specified class name.
   * <ul>
   * <li>If the specified class name represents an inner class, the name of the
   * declaring class will be returned.</li>
   * <li>If the specified class name represents a regular class, the specified
   * class name will be returned.
   * </ul>
   * <blockquote>
   * <table>
   * <caption>Examples</caption>
   * <tr><td><b>className</b></td><td><b>returns</b></td></tr>
   * <tr><td>{@code foo.bar.One}</td><td>{@code foo.bar.One}</td></tr>
   * <tr><td>{@code foo.bar.One$Two}</td><td>{@code foo.bar.One}</td></tr>
   * <tr><td>{@code foo.bar.One$Two$Three}</td><td>{@code foo.bar.One$Two}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two$}</td><td>{@code foo.bar.One.$Two$}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two$$Three$$$Four}</td><td>{@code foo.bar.One.$Two$$Three}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two.$$Three$}</td><td>{@code foo.bar.One.$Two.$$Three$}</td></tr>
   * </table>
   * </blockquote>
   *
   * @param className The class name for which to return the name of the
   *          declaring class.
   * @return The name of the declaring class of the specified class name.
   * @throws IllegalArgumentException If {@code className} is null, or if
   *           {@code className} is not a valid <a href=
   *           "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java
   *           Identifier</a>.
   */
  public static String getDeclaringClassName(final String className) {
    if (!Identifiers.isValid(className))
      throw new IllegalArgumentException("Not a valid java identifier: " + className);

    int index = className.length() - 1;
    for (char ch; (index = className.lastIndexOf('$', index - 1)) > 1 && ((ch = className.charAt(index - 1)) == '.' || ch == '$'););
    return index <= 0 ? className : className.substring(0, index);
  }

  /**
   * Returns the name of the root declaring class for the specified class name.
   * <ul>
   * <li>If the specified class name represents an inner class of an inner class
   * of an inner class, the name of the root declaring class will be returned
   * (i.e. the name of the class corresponding to the name of the {@code .java}
   * file in which the inner class is defined).</li>
   * <li>If the specified class name represents a regular class, the specified
   * class name will be returned.
   * </ul>
   * <blockquote>
   * <table>
   * <caption>Examples</caption>
   * <tr><td><b>className</b></td><td><b>returns</b></td></tr>
   * <tr><td>{@code foo.bar.One}</td><td>{@code foo.bar.One}</td></tr>
   * <tr><td>{@code foo.bar.One$Two}</td><td>{@code foo.bar.One}</td></tr>
   * <tr><td>{@code foo.bar.One$Two$Three}</td><td>{@code foo.bar.One}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two$}</td><td>{@code foo.bar.One.$Two$}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two$$Three$$$Four}</td><td>{@code foo.bar.One.$Two}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two.$$Three$}</td><td>{@code foo.bar.One.$Two.$$Three$}</td></tr>
   * </table>
   * </blockquote>
   *
   * @param className The class name for which to return the name of the root
   *          declaring class.
   * @return The name of the root declaring class for the specified class name.
   * @throws IllegalArgumentException If {@code className} is null, or if
   *           {@code className} is not a valid <a href=
   *           "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java
   *           Identifier</a>.
   */
  public static String getRootDeclaringClassName(final String className) {
    if (!Identifiers.isValid(className))
      throw new IllegalArgumentException("Not a valid java identifier: " + className);

    final int limit = className.length() - 1;
    int index = 0;
    while ((index = className.indexOf('$', index + 1)) > 1) {
      final char ch = className.charAt(index - 1);
      if (index == limit)
        return className;

      if (ch != '.' && ch != '$')
        break;
    }

    return index == -1 ? className : className.substring(0, index);
  }

  /**
   * Returns the canonical name of the specified class name, as defined by the Java
   * Language Specification.
   * <blockquote>
   * <table>
   * <caption>Examples</caption>
   * <tr><td><b>className</b></td><td><b>returns</b></td></tr>
   * <tr><td>{@code foo.bar.One}</td><td>{@code foo.bar.One}</td></tr>
   * <tr><td>{@code foo.bar.One$Two}</td><td>{@code foo.bar.One.Two}</td></tr>
   * <tr><td>{@code foo.bar.One$Two$Three}</td><td>{@code foo.bar.One.Two.Three}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two$}</td><td>{@code foo.bar.One.$Two$}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two$$Three$$$Four}</td><td>{@code foo.bar.One.$Two.$Three.$$Four}</td></tr>
   * <tr><td>{@code foo.bar.One.$Two.$$Three$}</td><td>{@code foo.bar.One.$Two.$$Three$}</td></tr>
   * </table>
   * </blockquote>
   *
   * @param className The class name.
   * @return The canonical name of the underlying specified class name.
   * @throws IllegalArgumentException If {@code className} is null, or if
   *           {@code className} is not a valid <a href=
   *           "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java
   *           Identifier</a>.
   * @see <a href=
   *      "https://docs.oracle.com/javase/specs/jls/se7/html/jls-6.html#jls-6.7">6.7.
   *      Fully Qualified Names and Canonical Names</a>
   */
  public static String toCanonicalClassName(final String className) {
    if (!Identifiers.isValid(className))
      throw new IllegalArgumentException("Not a valid java identifier: " + className);

    final StringBuilder builder = new StringBuilder();
    builder.append(className.charAt(0));
    builder.append(className.charAt(1));
    char last = '\0';
    for (int i = 2; i < className.length() - 1; ++i) {
      final char ch = className.charAt(i);
      builder.append(last != '.' && last != '$' && ch == '$' ? '.' : ch);
      last = ch;
    }

    if (className.length() > 2)
      builder.append(className.charAt(className.length() - 1));

    return builder.toString();
  }

  /**
   * Returns the "Composite Name" of the class or interface represented by
   * {@code cls}.
   * <p>
   * The "Composite Name" is the fully qualified name of a class
   * ({@link Class#getName()} with its package name
   * ({@link Class#getPackage()}.getName()) removed.
   * <p>
   * For example:
   * <ol>
   * <li>The "Composite Name" of {@code java.lang.String} is
   * {@link String}.</li>
   * <li>The "Composite Name" of {@code java.lang.Map.Entry} is
   * {@code Map$Entry}.</li>
   * </ol>
   *
   * @param cls The class or interface.
   * @return The "Composite Name" of the class or interface represented by
   *         {@code cls}.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static String getCompositeName(final Class<?> cls) {
    final String pkg = Assertions.assertNotNull(cls).getPackage().getName();
    return pkg.length() == 0 ? cls.getName() : cls.getName().substring(pkg.length() + 1);
  }

  /**
   * Returns the canonical "Composite Name" of the class or interface
   * represented by {@code cls}.
   * <p>
   * The canonical "Composite Name" is the fully qualified name of a class
   * ({@link Class#getCanonicalName()} with its package name
   * ({@link Class#getPackage()}.getName()) removed.
   * <p>
   * For example:
   * <ol>
   * <li>The canonical "Composite Name" of {@code java.lang.String} is
   * {@link String}.</li>
   * <li>The canonical "Composite Name" of {@code java.lang.Map.Entry} is
   * {@code Map.Entry}.</li>
   * </ol>
   *
   * @param cls The class or interface.
   * @return The canonical "Composite Name" of the class or interface
   *         represented by {@code cls}.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static String getCanonicalCompositeName(final Class<?> cls) {
    if (Assertions.assertNotNull(cls).isPrimitive())
      return cls.getCanonicalName();

    final String pkg = cls.getPackage().getName();
    return pkg.length() == 0 ? cls.getCanonicalName() : cls.getCanonicalName().substring(pkg.length() + 1);
  }

  /**
   * Returns the {@link Class} array most accurately reflecting the actual type
   * parameters used in the source code for the generic superclass of the
   * specified {@link Class}, or {@code null} if no generic superclass exist.
   *
   * @param cls The {@link Class}.
   * @return The {@link Class} array most accurately reflecting the actual type
   *         parameters used in the source code for the generic superclass of
   *         the specified {@link Class}, or {@code null} if no generic
   *         superclass exist.
   * @throws GenericSignatureFormatError If the generic class signature does not
   *           conform to the format specified in <cite>The Java&trade; Virtual
   *           Machine Specification</cite>.
   * @throws TypeNotPresentException If the generic superclass refers to a
   *           non-existent type declaration.
   * @throws MalformedParameterizedTypeException If the generic superclass
   *           refers to a parameterized type that cannot be instantiated for
   *           any reason.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static Type[] getSuperclassGenericTypes(final Class<?> cls) {
    return Assertions.assertNotNull(cls).getGenericSuperclass() instanceof ParameterizedType ? ((ParameterizedType)cls.getGenericSuperclass()).getActualTypeArguments() : null;
  }

  private static <T>T visitSuperclass(final Class<?> cls, final Queue<? super Class<?>> queue, final Set<? super Class<?>> visited, final Function<? super Class<?>,T> function) {
    if (cls == null || !visited.add(cls))
      return null;

    T t;
    if (function != null && (t = function.apply(cls)) != null)
      return t;

    queue.add(cls);
    return null;
  }

  /**
   * Traverses and the class hierarchy of the specified {@link Class} in Breadth
   * First Search order, calling the provided {@code function} for each
   * superclass and superinterface in the hierarchy, and returns the first
   * non-null value returned from the {@code function}.
   *
   * @param <T> The type parameter of the object returned by the provided
   *          {@code function}.
   * @param cls The {@link Class}.
   * @param function If not null, the {@link Function} to be called for each
   *          visited superclass and superinterface. If the {@link Function}
   *          returns a non-null value, traversal will terminate, and the method
   *          will return the value returned by the {@code function}.
   * @return The first non-null value returned from the {@code function}.
   * @throws IllegalArgumentException If {@code cls} null.
   */
  public static <T>T walkClassHierarchy(Class<?> cls, final Function<? super Class<?>,T> function) {
    Assertions.assertNotNull(cls);
    final Set<Class<?>> visited = new LinkedHashSet<>();
    final Queue<Class<?>> queue = new LinkedList<>();
    T t;
    if ((t = visitSuperclass(cls, queue, visited, function)) != null)
      return t;

    do {
      if ((t = visitSuperclass(cls.getSuperclass(), queue, visited, function)) != null)
        return t;

      for (final Class<?> superInterface : cls.getInterfaces())
        if ((t = visitSuperclass(superInterface, queue, visited, function)) != null)
          return t;
    }
    while ((cls = queue.poll()) != null);
    return t;
  }

  private static boolean visitSuperclass(final Class<?> cls, final Queue<? super Class<?>> queue, final Set<? super Class<?>> visited, final Predicate<? super Class<?>> forEach) {
    if (cls == null || !visited.add(cls))
      return true;

    if (forEach != null && !forEach.test(cls))
      return false;

    queue.add(cls);
    return true;
  }

  /**
   * Traverses and returns the class hierarchy of the specified {@link Class}.
   * This method visits the superclasses and superinterfaces with Breadth First
   * Search.
   *
   * @param cls The {@link Class}.
   * @param forEach If not null, the {@link Predicate} to be called for each
   *          visited superclass and superinterface. If the {@link Predicate}
   *          returns {@code false}, traversal will terminate, and the method
   *          will return the set of classes that had been visited before
   *          termination.
   * @return The class hierarchy of the specified {@link Class}.
   * @throws IllegalArgumentException If {@code cls} null.
   */
  public static Set<Class<?>> getClassHierarchy(Class<?> cls, final Predicate<? super Class<?>> forEach) {
    Assertions.assertNotNull(cls);
    final Set<Class<?>> visited = new LinkedHashSet<>();
    final Queue<Class<?>> queue = new LinkedList<>();
    if (!visitSuperclass(cls, queue, visited, forEach))
      return visited;

    do {
      if (!visitSuperclass(cls.getSuperclass(), queue, visited, forEach))
        return visited;

      for (final Class<?> superInterface : cls.getInterfaces())
        if (!visitSuperclass(superInterface, queue, visited, forEach))
          return visited;
    }
    while ((cls = queue.poll()) != null);
    return visited;
  }

  /**
   * Traverses and returns the class hierarchy of the specified {@link Class}.
   * This method visits the superclasses and superinterfaces with Breadth First
   * Search.
   *
   * @param cls The {@link Class}.
   * @return The class hierarchy of the specified {@link Class}.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static Set<Class<?>> getClassHierarchy(final Class<?> cls) {
    return getClassHierarchy(cls, null);
  }

  /**
   * Finds the specified {@code interfaceType} in the class hierarchy of the
   * provided {@code cls}, and returns an array of {@link Type} objects
   * representing the actual type arguments to the generic type.
   * <p>
   * Note that in some cases, the returned array be empty. This can occur if
   * this type represents a non-parameterized type nested within a parameterized
   * type.
   *
   * @param cls The {@link Class} in which to find the specified
   *          {@code interfaceType}.
   * @param interfaceType The interface {@link Class} to find in the provided
   *          {@code cls}.
   * @return An array of {@link Type} objects representing the actual type
   *         arguments to this type.
   * @throws TypeNotPresentException If any of the actual type arguments refers
   *           to a non-existent type declaration.
   * @throws MalformedParameterizedTypeException If any of the actual type
   *           parameters refer to a parameterized type that cannot be
   *           instantiated for any reason.
   * @throws IllegalArgumentException If {@code interfaceType} or {@code cls} is
   *           null, or if {@code interfaceType} is not a {@link Class} of an
   *           interface type.
   */
  public static Type[] getGenericInterfaceTypeArguments(final Class<?> cls, final Class<?> interfaceType) {
    Assertions.assertNotNull(cls);
    Assertions.assertNotNull(interfaceType);
    if (!interfaceType.isInterface())
      throw new IllegalArgumentException(interfaceType.getName() + " is not an interface type");

    final Type[] types = resolveGenericTypes(cls, null, new HashSet<>(), (c, t) -> c == interfaceType ? t : null);
    return types != null ? types : new Type[0];
  }

  /**
   * Traverses the class hierarchy of the specified {@link Class} in Depth First
   * Search order, invoking the provided {@code function} for each superclasses
   * and superinterface in the class hierarchy with resolved generic type
   * arguments, and returns the first non-null value returned from the
   * {@code function}.
   *
   * @param <T> The type parameter of the object returned by the provided
   *          {@code function}.
   * @param cls The {@link Class} whose superclasses and superinterface are to
   *          be resolved.
   * @param function The {@link BiFunction} to be invoked for each superclasses
   *          and superinterface of the specified {@link Class}, whereby a
   *          return of {@code false} will terminate subsequent traversal of the
   *          class hierarchy.
   * @return The first non-null value returned from the {@code function}.
   * @throws IllegalArgumentException If {@code cls} or {@code function} is null.
   */
  public static <T>T resolveGenericTypes(final Class<?> cls, final BiFunction<Class<?>,Type[],T> function) {
    return resolveGenericTypes(Assertions.assertNotNull(cls), null, new HashSet<>(8), Assertions.assertNotNull(function));
  }

  private static <T>T resolveGenericTypes(final Class<?> cls, final Object[][] args, final Set<Class<?>> visited, final BiFunction<Class<?>,Type[],T> function) {
    final Class<?> superclass = cls.getSuperclass();
    T result;
    if (superclass != null && cls.getGenericSuperclass() instanceof ParameterizedType && (result = resolveGenericTypes(superclass, (ParameterizedType)cls.getGenericSuperclass(), args, visited, function)) != null)
      return result;

    final Class<?>[] superInterfaces = cls.getInterfaces();
    final Type[] genericSuperInterfaces = cls.getGenericInterfaces();
    for (int i = 0; i < genericSuperInterfaces.length; ++i) {
      final Class<?> superInterface = superInterfaces[i];
      if (!visited.add(superInterface))
        continue;

      final Type genericSuperInterface = genericSuperInterfaces[i];
      if (genericSuperInterface instanceof ParameterizedType && (result = resolveGenericTypes(superInterface, (ParameterizedType)genericSuperInterface, args, visited, function)) != null)
        return result;
    }

    return null;
  }

  private static <T>T resolveGenericTypes(final Class<?> superClass, final ParameterizedType superType, final Object[][] args, final Set<Class<?>> visited, final BiFunction<Class<?>,Type[],T> function) {
    final TypeVariable<?>[] typeVariables = superClass.getTypeParameters();
    final Type[] typeArguments = superType.getActualTypeArguments();
    final Object[][] nextArgs = recurseArgs(typeVariables, typeArguments, args, 0, 0);
    T result = function.apply(superClass, typeArguments);
    if (result != null)
      return result;

    result = resolveGenericTypes(superClass, nextArgs, visited, function);
    if (result != null)
      return result;

    return null;
  }

  private static Object[][] recurseArgs(final TypeVariable<?>[] typeVariables, final Type[] typeArguments, final Object[][] args, final int index, final int depth) {
    if (index == typeVariables.length)
      return new Object[depth][2];

    final Object[][] nextArgs;
    if (typeArguments[index] instanceof Class<?>) {
      final Object[] arg = {typeVariables[index].getName(), (Class<?>)typeArguments[index]};
      nextArgs = recurseArgs(typeVariables, typeArguments, args, index + 1, depth + 1);
      nextArgs[depth] = arg;
      return nextArgs;
    }

    final String localName = typeArguments[index].getTypeName();
    if (args != null) {
      for (final Object[] arg : args) {
        if (localName.equals(arg[0])) {
          typeArguments[index] = (Class<?>)arg[1];
          nextArgs = recurseArgs(typeVariables, typeArguments, args, index + 1, depth + 1);
          nextArgs[depth] = new Object[] {typeVariables[index].getTypeName(), typeArguments[index]};
          return nextArgs;
        }
      }
    }

    return recurseArgs(typeVariables, typeArguments, args, index + 1, depth);
  }

  /**
   * Returns the array of generic parameter classes for the return type of the
   * specified method. If the field is not a parameterized type, this method
   * will return an empty array.
   *
   * @param method The {@link Method}
   * @return The array of generic parameter classes for the specified method.
   * @throws IllegalArgumentException If {@code method} is null.
   */
  public static Class<?>[] getGenericParameters(final Method method) {
    return getGenericParameters(Assertions.assertNotNull(method).getGenericReturnType());
  }

  private static final Class<?>[] emptyClasses = {};

  /**
   * Returns the array of generic parameter classes for the specified field. If the
   * field is not a parameterized type, this method will return an empty array.
   *
   * @param field The {@link Field}
   * @return The array of generic parameter classes for the specified field.
   * @throws IllegalArgumentException If {@code field} is null.
   */
  public static Class<?>[] getGenericParameters(final Field field) {
    return getGenericParameters(Assertions.assertNotNull(field).getGenericType());
  }

  public static Class<?>[] getGenericParameters(final Type genericType) {
    if (!(genericType instanceof ParameterizedType))
      return emptyClasses;

    final Type[] types = ((ParameterizedType)genericType).getActualTypeArguments();
    final Class<?>[] classes = new Class[types.length];
    for (int i = 0; i < classes.length; ++i) {
      final Type type = types[i];
      if (type instanceof Class)
        classes[i] = (Class<?>)type;
      else if (type instanceof ParameterizedType)
        classes[i] = (Class<?>)((ParameterizedType)type).getRawType();
      else if (type instanceof TypeVariable)
        classes[i] = (Class<?>)((TypeVariable<?>)type).getBounds()[0];
      else if (type instanceof WildcardType)
        classes[i] = (Class<?>)((WildcardType)type).getUpperBounds()[0];
    }

    return classes;
  }

  private static Field getField(final Class<?> cls, final String fieldName, final boolean declared) {
    final Field[] fields = declared ? cls.getDeclaredFields() : cls.getFields();
    for (final Field field : fields)
      if (fieldName.equals(field.getName()))
        return field;

    return null;
  }

  /**
   * Returns a {@link Field} object that reflects the specified public member
   * field of the class or interface represented by {@code cls} (including
   * inherited fields). The {@code name} parameter is a {@link String}
   * specifying the simple name of the desired field.
   * <p>
   * The field to be reflected is determined by the algorithm that follows. Let
   * C be the class or interface represented by this object:
   * <ol>
   * <li>If C declares a public field with the name specified, that is the field
   * to be reflected.</li>
   * <li>If no field was found in step 1 above, this algorithm is applied
   * recursively to each direct superinterface of C. The direct superinterfaces
   * are searched in the order they were declared.</li>
   * <li>If no field was found in steps 1 and 2 above, and C has a superclass S,
   * then this algorithm is invoked recursively upon S. If C has no superclass,
   * then this method returns {@code null}.</li>
   * </ol>
   * <p>
   * If this {@link Class} object represents an array type, then this method
   * does not find the {@code length} field of the array type.
   * <p>
   * This method differentiates itself from {@link Class#getField(String)} by
   * returning {@code null} when a field is not found, instead of throwing
   * {@link NoSuchFieldException}.
   *
   * @param cls The class in which to find the public field.
   * @param name The field name.
   * @return A {@link Field} object that reflects the specified public member
   *         field of the class or interface represented by {@code cls}
   *         (including inherited fields). The {@code name} parameter is a
   *         {@link String} specifying the simple name of the desired field.
   * @throws IllegalArgumentException If {@code cls} or {@code name} is null.
   * @throws SecurityException If a security manager, <i>s</i>, is present and
   *           the caller's class loader is not the same as or an ancestor of
   *           the class loader for the current class and invocation of
   *           {@link SecurityManager#checkPackageAccess s.checkPackageAccess()}
   *           denies access to the package of this class.
   */
  public static Field getField(final Class<?> cls, final String name) {
    return Classes.getField(Assertions.assertNotNull(cls), Assertions.assertNotNull(name), false);
  }

  /**
   * Returns a {@link Field} object that reflects the specified declared member
   * field of the class or interface represented by {@code cls} (excluding
   * inherited fields). The {@code name} parameter is a {@link String}
   * specifying the simple name of the desired field.
   * <p>
   * Declared fields include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If this {@link Class} object represents an array type, then this method
   * does not find the {@code length} field of the array type.
   * <p>
   * This method differentiates itself from
   * {@link Class#getDeclaredField(String)} by returning {@code null} when a
   * field is not found, instead of throwing {@link NoSuchFieldException}.
   *
   * @param cls The class in which to find the declared field.
   * @param name The field name.
   * @return A {@link Field} object that reflects the specified public member
   *         field of the class or interface represented by {@code cls}
   *         (excluding inherited fields). The {@code name} parameter is a
   *         {@link String} specifying the simple name of the desired field.
   * @throws IllegalArgumentException If {@code cls} or {@code name} is null.
   * @throws SecurityException If a security manager, <i>s</i>, is present and
   *           the caller's class loader is not the same as or an ancestor of
   *           the class loader for the current class and invocation of
   *           {@link SecurityManager#checkPackageAccess s.checkPackageAccess()}
   *           denies access to the package of this class.
   */
  public static Field getDeclaredField(final Class<?> cls, final String name) {
    return Classes.getField(Assertions.assertNotNull(cls), Assertions.assertNotNull(name), true);
  }

  /**
   * Returns a {@link Field} object that reflects the specified declared member
   * field of the class or interface represented by {@code cls} (including
   * inherited fields). The {@code name} parameter is a {@link String}
   * specifying the simple name of the desired field.
   * <p>
   * Declared fields include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If this {@link Class} object represents an array type, then this method
   * does not find the {@code length} field of the array type.
   * <p>
   * This method differentiates itself from
   * {@link Class#getDeclaredField(String)} by returning {@code null} when a
   * field is not found, instead of throwing {@link NoSuchFieldException}.
   *
   * @param cls The class in which to find the declared field.
   * @param name The field name.
   * @return A {@link Field} object that reflects the specified public member
   *         field of the class or interface represented by {@code cls}
   *         (including inherited fields). The {@code name} parameter is a
   *         {@link String} specifying the simple name of the desired field.
   * @throws IllegalArgumentException If {@code cls} or {@code name} is null.
   * @throws SecurityException If a security manager, <i>s</i>, is present and
   *           the caller's class loader is not the same as or an ancestor of
   *           the class loader for the current class and invocation of
   *           {@link SecurityManager#checkPackageAccess s.checkPackageAccess()}
   *           denies access to the package of this class.
   */
  public static Field getDeclaredFieldDeep(Class<?> cls, final String name) {
    Assertions.assertNotNull(cls);
    Assertions.assertNotNull(name);
    Field field;
    do
      field = getField(cls, name, true);
    while (field == null && (cls = cls.getSuperclass()) != null);
    return field;
  }

  /**
   * Returns a {@link Constructor} object that reflects the specified public
   * constructor signature of the class represented by {@code cls} (including
   * inherited constructors), or {@code null} if the constructor is not found.
   * <p>
   * The {@code parameterTypes} parameter is an array of {@link Class} objects
   * that identify the constructor's formal parameter types, in declared order.
   * If {@code cls} represents an inner class declared in a non-static context,
   * the formal parameter types include the explicit enclosing instance as the
   * first parameter.
   * <p>
   * This method differentiates itself from
   * {@link Class#getConstructor(Class...)} by returning {@code null} when a
   * method is not found, instead of throwing {@link NoSuchMethodException}.
   *
   * @param <T> The class in which the constructor is declared.
   * @param cls The class in which to find the public constructor.
   * @param parameterTypes The parameter array.
   * @return A {@link Constructor} object that reflects the specified public
   *         constructor signature of the class represented by {@code cls}
   *         (including inherited constructors), or {@code null} if the
   *         constructor is not found.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  @SuppressWarnings("unchecked")
  public static <T>Constructor<T> getConstructor(final Class<T> cls, final Class<?> ... parameterTypes) {
    final Constructor<?>[] constructors = Assertions.assertNotNull(cls).getConstructors();
    for (final Constructor<?> constructor : constructors)
      if (parameterTypes == null || parameterTypes.length == 0 ? constructor.getParameterCount() == 0 : parameterTypes.length == constructor.getParameterCount() && Arrays.equals(constructor.getParameterTypes(), parameterTypes))
        return (Constructor<T>)constructor;

    return null;
  }

  /**
   * Returns a {@link Constructor} object that reflects the specified public
   * constructor signature of the class represented by {@code cls} (including
   * inherited constructors), or {@code null} if the constructor is not found.
   * <p>
   * The {@code parameterTypes} parameter is an array of {@link Class} objects
   * that identify the constructor's compatible parameter types, in declared
   * order.
   * <p>
   * A parameter type {@code p} is compatible with a {@link Class} that is the
   * same or is the superclass of {@code p}.
   * <p>
   * If {@code cls} represents an inner class declared in a non-static context,
   * the formal parameter types include the explicit enclosing instance as the
   * first parameter.
   *
   * @param <T> The class in which the constructor is declared.
   * @param cls The class in which to find the public constructor.
   * @param parameterTypes The parameter array.
   * @return A {@link Constructor} object that reflects the specified public
   *         constructor signature of the class represented by {@code cls}
   *         (including inherited constructors), or {@code null} if the
   *         constructor is not found.
   * @throws IllegalArgumentException If {@code cls} or {@code parameterTypes} is
   *           null.
   */
  @SuppressWarnings("unchecked")
  public static <T>Constructor<T> getCompatibleConstructor(final Class<T> cls, final Class<?> ... parameterTypes) {
    final Constructor<?>[] constructors = Assertions.assertNotNull(cls).getConstructors();
    for (final Constructor<?> constructor : constructors)
      if (isCompatible(constructor.getParameterTypes(), parameterTypes))
        return (Constructor<T>)constructor;

    return null;
  }

  /**
   * Returns a {@link Constructor} object that reflects the specified declared
   * constructor signature of the class represented by {@code cls} (excluding
   * inherited constructors), or {@code null} if the constructor is not found.
   * <p>
   * Declared constructors include public, protected, default (package) access,
   * and private visibility.
   * <p>
   * The {@code parameterTypes} parameter is an array of {@link Class} objects
   * that identify the constructor's formal parameter types, in declared order.
   * If {@code cls} represents an inner class declared in a non-static context,
   * the formal parameter types include the explicit enclosing instance as the
   * first parameter.
   * <p>
   * This method differentiates itself from
   * {@link Class#getDeclaredConstructor(Class...)} by returning {@code null}
   * when a method is not found, instead of throwing
   * {@link NoSuchMethodException}.
   *
   * @param <T> The class in which the constructor is declared.
   * @param cls The class in which to find the declared constructor.
   * @param parameterTypes The parameter array.
   * @return A {@link Constructor} object that reflects the specified declared
   *         constructor signature of the class represented by {@code cls}
   *         (excluding inherited constructors), or {@code null} if the
   *         constructor is not found.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  @SuppressWarnings("unchecked")
  public static <T>Constructor<T> getDeclaredConstructor(final Class<T> cls, final Class<?> ... parameterTypes) {
    final Constructor<?>[] constructors = Assertions.assertNotNull(cls).getDeclaredConstructors();
    for (final Constructor<?> constructor : constructors)
      if (parameterTypes == null || parameterTypes.length == 0 ? constructor.getParameterCount() == 0 : parameterTypes.length == constructor.getParameterCount() && Arrays.equals(constructor.getParameterTypes(), parameterTypes))
        return (Constructor<T>)constructor;

    return null;
  }

  /**
   * Changes the annotation value for {@code key} in {@code annotation} to
   * {@code newValue}, and returns the previous value.
   *
   * @param <T> Type parameter of the value.
   * @param annotation The annotation.
   * @param key The key.
   * @param newValue The new value.
   * @return The previous value assigned to {@code key}.
   * @throws IllegalArgumentException If {@code annotation}, {@code key} or
   *           {@code newValue} is null, or if {@code newValue} does not match
   *           the required type of the value for {@code key}.
   */
  @SuppressWarnings("unchecked")
  public static <T>T setAnnotationValue(final Annotation annotation, final String key, final T newValue) {
    Assertions.assertNotNull(annotation);
    Assertions.assertNotNull(key);
    Assertions.assertNotNull(newValue);
    final Object handler = Proxy.getInvocationHandler(annotation);
    Assertions.assertNotNull(key);
    Assertions.assertNotNull(newValue);
    final Field field;
    final Map<String,Object> memberValues;
    try {
      field = handler.getClass().getDeclaredField("memberValues");
      field.setAccessible(true);
      memberValues = (Map<String,Object>)field.get(handler);
    }
    catch (final IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalStateException(e);
    }

    final T oldValue = Assertions.assertNotNull((T)memberValues.get(key), key + " is not a valid key");
    if (newValue.getClass() != oldValue.getClass())
      throw new IllegalArgumentException(newValue.getClass().getName() + " does not match the required type " + oldValue.getClass().getName());

    memberValues.put(key, newValue);
    return oldValue;
  }

  private interface SuperclassRecurser<M,A> extends Repeat.Recurser<Class<?>,M,A> {
    @Override
    default Class<?> next(final Class<?> container) {
      return container.getSuperclass();
    }
  }

  @FunctionalInterface
  private interface DeclaredFieldRecurser<A> extends SuperclassRecurser<Field,A> {
    @Override
    default Field[] members(final Class<?> container) {
      return container.getDeclaredFields();
    }
  }

  @FunctionalInterface
  private interface DeclaredMethodRecurser<A> extends SuperclassRecurser<Method,A> {
    @Override
    default Method[] members(final Class<?> container) {
      return container.getDeclaredMethods();
    }
  }

  @FunctionalInterface
  private interface DeclaredClassRecurser<A> extends SuperclassRecurser<Class<?>,A> {
    @Override
    default Class<?>[] members(final Class<?> container) {
      return container.getDeclaredClasses();
    }
  }

  private static final Repeat.Recurser<Class<?>,Field,Object> declaredFieldRecurser = (DeclaredFieldRecurser<Object>)(member, arg) -> true;
  private static final Repeat.Recurser<Class<?>,Field,Predicate<Field>> declaredFieldWithPredicateRecurser = (DeclaredFieldRecurser<Predicate<Field>>)(member, arg) -> arg.test(member);
  private static final Repeat.Recurser<Class<?>,Field,Class<? extends Annotation>> declaredFieldWithAnnotationRecurser = (DeclaredFieldRecurser<Class<? extends Annotation>>)(member, arg) -> member.getAnnotation(arg) != null;
  private static final Repeat.Recurser<Class<?>,Method,Object> declaredMethodRecurser = (DeclaredMethodRecurser<Object>)(member, arg) -> true;
  private static final Repeat.Recurser<Class<?>,Method,Predicate<Method>> declaredMethodWithPredicateRecurser = (DeclaredMethodRecurser<Predicate<Method>>)(member, arg) -> arg.test(member);
  private static final Repeat.Recurser<Class<?>,Method,Class<? extends Annotation>> declaredMethodWithAnnotationRecurser = (DeclaredMethodRecurser<Class<? extends Annotation>>)(member, arg) -> member.getAnnotation(arg) != null;
  private static final Repeat.Recurser<Class<?>,Class<?>,Class<? extends Annotation>> classWithAnnotationRecurser = (DeclaredClassRecurser<Class<? extends Annotation>>)(member, arg) -> member.getAnnotation(arg) != null;

  private static final BiPredicate<Field,Class<? extends Annotation>> declaredFieldWithAnnotationFilter = (m, a) -> m.getAnnotation(a) != null;
  private static final BiPredicate<Method,Class<? extends Annotation>> declaredMethodWithAnnotationFilter = (m, a) -> m.getAnnotation(a) != null;
  private static final BiPredicate<Class<?>,Class<? extends Annotation>> classWithAnnotationFilter = (m, a) -> m.getAnnotation(a) != null;

  /**
   * Returns an array of Field objects declared in {@code cls} (including
   * inherited fields).
   * <p>
   * Declared fields include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents a class or interface with no declared fields,
   * then this method returns an array of length 0.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are sorted reflecting the inheritance
   * graph of {@code cls}, whereby inherited fields are first, and member fields
   * are last.
   *
   * @param cls The class in which to find declared fields.
   * @return An array of Field objects declared in {@code cls} (including
   *         inherited fields).
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static Field[] getDeclaredFieldsDeep(final Class<?> cls) {
    return Repeat.Recursive.inverted(Assertions.assertNotNull(cls), cls.getDeclaredFields(), Field.class, declaredFieldRecurser, null);
  }

  /**
   * Returns an array of Field objects declared in {@code cls} (including
   * inherited fields), for which the provided {@link Predicate} returns
   * {@code true}.
   * <p>
   * Declared fields include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents a class or interface with no declared fields,
   * then this method returns an array of length 0.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are sorted reflecting the inheritance
   * graph of {@code cls}, whereby inherited fields are first, and member fields
   * are last.
   *
   * @param cls The class in which to find declared fields.
   * @param predicate The {@link Predicate} used to decide whether the field
   *          should be included in the returned array.
   * @return An array of Field objects declared in {@code cls} (including
   *         inherited fields).
   * @throws IllegalArgumentException If {@code cls} or {@code predicate} is null.
   */
  public static Field[] getDeclaredFieldsDeep(final Class<?> cls, final Predicate<Field> predicate) {
    return Repeat.Recursive.inverted(Assertions.assertNotNull(cls), cls.getDeclaredFields(), Field.class, declaredFieldWithPredicateRecurser, Assertions.assertNotNull(predicate));
  }

  /**
   * Returns an array of Field objects declared in {@code cls} (excluding
   * inherited fields) that have an annotation of {@code annotationType}.
   * <p>
   * Declared fields include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents a class or interface with no declared fields,
   * then this method returns an array of length 0.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are not sorted and are not in any
   * particular order.
   *
   * @param cls The class in which to find declared fields.
   * @param annotationType The type of the annotation to match.
   * @return An array of Field objects declared in {@code cls} (excluding
   *         inherited fields) that have an annotation of
   *         {@code annotationType}.
   * @throws IllegalArgumentException If {@code cls} or {@code annotationType} is
   *           null.
   */
  public static Field[] getDeclaredFieldsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.ordered(Assertions.assertNotNull(cls).getDeclaredFields(), Field.class, declaredFieldWithAnnotationFilter, Assertions.assertNotNull(annotationType));
  }

  /**
   * Returns an array of Field objects declared in {@code cls} (including
   * inherited fields) that have an annotation of {@code annotationType}.
   * <p>
   * Declared fields include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents a class or interface with no declared fields,
   * then this method returns an array of length 0.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are sorted reflecting the inheritance
   * graph of {@code cls}, whereby inherited fields are first, and member fields
   * are last.
   *
   * @param cls The class in which to find declared fields.
   * @param annotationType The type of the annotation to match.
   * @return An array of Field objects declared in {@code cls} (including
   *         inherited fields) that have an annotation of
   *         {@code annotationType}.
   * @throws IllegalArgumentException If {@code cls} or {@code annotationType} is
   *           null.
   */
  public static Field[] getDeclaredFieldsWithAnnotationDeep(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.inverted(Assertions.assertNotNull(cls), cls.getDeclaredFields(), Field.class, declaredFieldWithAnnotationRecurser, Assertions.assertNotNull(annotationType));
  }

  /**
   * Returns a {@link Method} object that reflects the specified public method
   * signature of the class or interface represented by {@code cls} (including
   * inherited methods), or {@code null} if the method is not found.
   * <p>
   * The {@code name} parameter is a {@code String} that specifies the simple
   * name of the desired method, and the {@code parameterTypes} parameter is an
   * array of {@link Class} objects that identify the method's formal parameter
   * types, in declared order. If more than one method with the same parameter
   * types is declared in a class, and one of these methods has a return type
   * that is more specific than any of the others, that method is returned;
   * otherwise one of the methods is chosen arbitrarily. If the name is
   * {@code "<init>"} or {@code "<clinit>"} this method returns {@code null}. If
   * this Class object represents an array type, then this method does not find
   * the {@code clone()} method.
   * <p>
   * This method differentiates itself from
   * {@link Class#getDeclaredMethod(String,Class...)} by returning {@code null}
   * when a method is not found, instead of throwing
   * {@link NoSuchMethodException}.
   *
   * @param cls The class in which to find the declared method.
   * @param name The simple name of the method.
   * @param parameterTypes The parameter array.
   * @return A {@link Method} object that reflects the specified declared method
   *         of the class or interface represented by {@code cls} (excluding
   *         inherited methods), or {@code null} if the method is not found.
   * @throws IllegalArgumentException If {@code cls} or {@code name} is null.
   */
  public static Method getMethod(final Class<?> cls, final String name, final Class<?> ... parameterTypes) {
    for (final Method method : Assertions.assertNotNull(cls).getMethods())
      if (name.equals(method.getName()) && (parameterTypes == null || parameterTypes.length == 0 ? method.getParameterCount() == 0 : parameterTypes.length == method.getParameterCount() && Arrays.equals(method.getParameterTypes(), parameterTypes)))
        return method;

    return null;
  }

  private static boolean isCompatible(final Class<?>[] parameterTypes, final Class<?>[] args) {
    if (parameterTypes.length != args.length)
      return false;

    for (int i = 0, len = parameterTypes.length; i < len; ++i)
      if (args[i] != null && !isAssignableFrom(parameterTypes[i], args[i], true))
        return false;

    return true;
  }

  /**
   * Determines if the specified {@code Object obj} is assignment-compatible
   * with the class or interface represented by {@code target}.
   * <p>
   * This method differentiates itself from {@link Class#isInstance(Object)} by
   * supporting primitive types.
   *
   * @param target The target class.
   * @param obj The object to check.
   * @return Whether the specified {@code Object obj} is assignment-compatible
   *         with the class or interface represented by {@code target}, or
   *         {@code null} if {@code obj} is null.
   * @throws IllegalArgumentException If {@code target} is null.
   */
  public static boolean isInstance(final Class<?> target, final Object obj) {
    return obj != null && isAssignableFrom(Assertions.assertNotNull(target), obj.getClass(), true);
  }

  /**
   * Determines if the class or interface represented by {@code target} is
   * either the same as, or is a superclass or superinterface of, the class or
   * interface represented by the specified {@code cls} parameter.
   * <p>
   * This method differentiates itself from
   * {@link Class#isAssignableFrom(Class)} by wrapping primitive types. This
   * method is also able to properly ascertain whether the {@code cls} class is
   * assignment compatible with the {@code target} class, in case the two
   * classes represent arrays.
   * <p>
   * Calling this method is the equivalent of:
   *
   * <pre>
   * {@code isAssignableFrom(target,cls,true)}
   * </pre>
   *
   * @param target The target class.
   * @param cls The argument class.
   * @return Whether the class or interface represented by {@code target} is
   *         either the same as, or is a superclass or superinterface of, the
   *         class or interface represented by the specified {@code cls}
   *         parameter.
   * @throws IllegalArgumentException If {@code target} or {@code cls} is null.
   */
  public static boolean isAssignableFrom(final Class<?> target, final Class<?> cls) {
    return isAssignableFrom(target, cls, true);
  }

  /**
   * Determines if the class or interface represented by {@code target} is
   * either the same as, or is a superclass or superinterface of, the class or
   * interface represented by the specified {@code cls} parameter.
   * <p>
   * This method differentiates itself from
   * {@link Class#isAssignableFrom(Class)} by conditionally wrapping primitive
   * types, if {@code canWrap == true}. This method is also able to properly
   * ascertain whether the {@code cls} class is assignment compatible with the
   * {@code target} class, in case the two classes represent arrays.
   *
   * @param target The target class.
   * @param cls The argument class.
   * @param canWrap If {@code true}, this method will check compatibility of the
   *          wrapped form of a primitive type.
   * @return Whether the class or interface represented by {@code target} is
   *         either the same as, or is a superclass or superinterface of, the
   *         class or interface represented by the specified {@code cls}
   *         parameter.
   * @throws IllegalArgumentException If {@code target} or {@code cls} is null.
   */
  public static boolean isAssignableFrom(Class<?> target, Class<?> cls, final boolean canWrap) {
    Assertions.assertNotNull(target);
    Assertions.assertNotNull(cls);
    if (target.isArray()) {
      if (!cls.isArray())
        return false;

      return isAssignableFrom(target.getComponentType(), cls.getComponentType(), false);
    }
    else if (cls.isArray()) {
      return false;
    }

    if (target.isPrimitive() && cls.isPrimitive())
      return target == cls;

    if (canWrap) {
      if (target.isPrimitive())
        target = toWrapper(target);

      if (cls.isPrimitive())
        cls = toWrapper(cls);
    }

    return target.isAssignableFrom(cls);
  }

  /**
   * Returns a {@link Method} object that reflects the specified public method
   * signature of the class or interface represented by {@code cls} (including
   * inherited methods), or {@code null} if the method is not found.
   * <p>
   * The {@code name} parameter is a {@code String} that specifies the simple
   * name of the desired method, and the {@code parameterTypes} parameter is an
   * array of {@link Class} objects that identify the method's compatible
   * parameter types, in declared order.
   * <p>
   * A parameter type {@code p} is compatible with a {@link Class} that is the
   * same or is the superclass of {@code p}.
   * <p>
   * If more than one method with the same parameter types is declared in a
   * class, and one of these methods has a return type that is more specific
   * than any of the others, that method is returned; otherwise one of the
   * methods is chosen arbitrarily. If the name is {@code "<init>"} or
   * {@code "<clinit>"} this method returns {@code null}. If this Class object
   * represents an array type, then this method does not find the
   * {@code clone()} method.
   *
   * @param cls The class in which to find the declared method.
   * @param name The simple name of the method.
   * @param parameterTypes The parameter array.
   * @return A {@link Method} object that reflects the specified declared method
   *         of the class or interface represented by {@code cls} (excluding
   *         inherited methods), or {@code null} if the method is not found.
   * @throws IllegalArgumentException If {@code cls}, {@code name} or
   *           {@code parameterTypes} is null.
   */
  public static Method getCompatibleMethod(final Class<?> cls, final String name, final Class<?> ... parameterTypes) {
    Assertions.assertNotNull(cls);
    Assertions.assertNotNull(name);
    Assertions.assertNotNull(parameterTypes);
    for (final Method method : cls.getMethods())
      if (name.equals(method.getName()) && isCompatible(method.getParameterTypes(), parameterTypes))
        return method;

    return null;
  }

  /**
   * Returns a {@link Method} object that reflects the specified declared method
   * of the class or interface represented by {@code cls} (excluding inherited
   * methods), or {@code null} if the method is not found.
   * <p>
   * Declared methods include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * The {@code name} parameter is a {@code String} that specifies the simple
   * name of the desired method, and the {@code parameterTypes} parameter is an
   * array of {@link Class} objects that identify the method's formal parameter
   * types, in declared order. If more than one method with the same parameter
   * types is declared in a class, and one of these methods has a return type
   * that is more specific than any of the others, that method is returned;
   * otherwise one of the methods is chosen arbitrarily. If the name is
   * {@code "<init>"} or {@code "<clinit>"} this method returns {@code null}. If
   * this Class object represents an array type, then this method does not find
   * the {@code clone()} method.
   * <p>
   * This method differentiates itself from
   * {@link Class#getDeclaredMethod(String,Class...)} by returning {@code null}
   * when a method is not found, instead of throwing
   * {@link NoSuchMethodException}.
   *
   * @param cls The class in which to find the declared method.
   * @param name The simple name of the method.
   * @param parameterTypes The parameter array.
   * @return A {@link Method} object that reflects the specified declared method
   *         of the class or interface represented by {@code cls} (excluding
   *         inherited methods), or {@code null} if the method is not found.
   * @throws IllegalArgumentException If {@code cls} or {@code name} is null.
   */
  public static Method getDeclaredMethod(final Class<?> cls, final String name, final Class<?> ... parameterTypes) {
    Assertions.assertNotNull(cls);
    Assertions.assertNotNull(name);
    Assertions.assertNotNull(parameterTypes);
    for (final Method method : cls.getDeclaredMethods())
      if (name.equals(method.getName()) && (parameterTypes == null || parameterTypes.length == 0 ? method.getParameterCount() == 0 : Arrays.equals(method.getParameterTypes(), parameterTypes)))
        return method;

    return null;
  }

  /**
   * Returns a {@link Method} object that reflects the specified declared method
   * of the class or interface represented by {@code cls} (including inherited
   * methods), or {@code null} if the method is not found.
   * <p>
   * Declared methods include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * The {@code name} parameter is a {@code String} that specifies the simple
   * name of the desired method, and the {@code parameterTypes} parameter is an
   * array of {@link Class} objects that identify the method's formal parameter
   * types, in declared order. If more than one method with the same parameter
   * types is declared in a class, and one of these methods has a return type
   * that is more specific than any of the others, that method is returned;
   * otherwise one of the methods is chosen arbitrarily. If the name is
   * {@code "<init>"} or {@code "<clinit>"} this method returns {@code null}. If
   * this Class object represents an array type, then this method does not find
   * the {@code clone()} method.
   *
   * @implNote This method differentiates itself from
   *           {@link Class#getDeclaredMethod(String,Class...)} by returning
   *           {@code null} when a method is not found, instead of throwing
   *           {@link NoSuchMethodException}.
   * @param cls The class in which to find the declared method.
   * @param name The simple name of the method.
   * @param parameterTypes The parameter array.
   * @return A {@link Method} object that reflects the specified declared method
   *         of the class or interface represented by {@code cls} (including
   *         inherited methods), or {@code null} if the method is not found.
   * @throws IllegalArgumentException If {@code cls} or {@code name} is null.
   */
  public static Method getDeclaredMethodDeep(Class<?> cls, final String name, final Class<?> ... parameterTypes) {
    Assertions.assertNotNull(cls);
    Assertions.assertNotNull(name);
    Method method;
    do
      method = getDeclaredMethod(cls, name, parameterTypes);
    while (method == null && (cls = cls.getSuperclass()) != null);
    return method;
  }

  /**
   * Returns an array of {@link Method} objects declared in {@code cls}
   * (including inherited methods).
   * <p>
   * Declared methods include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are sorted reflecting the inheritance
   * graph of {@code cls}, whereby inherited methods are first, and member
   * methods are last.
   *
   * @param cls The class in which to find declared methods.
   * @return An array of {@link Method} objects declared in {@code cls}
   *         (including inherited methods).
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static Method[] getDeclaredMethodsDeep(final Class<?> cls) {
    return Repeat.Recursive.inverted(Assertions.assertNotNull(cls), cls.getDeclaredMethods(), Method.class, declaredMethodRecurser, null);
  }

  /**
   * Returns an array of {@link Method} objects declared in {@code cls}
   * (including inherited methods), for which the provided {@link Predicate}
   * returns {@code true}.
   * <p>
   * Declared methods include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are sorted reflecting the inheritance
   * graph of {@code cls}, whereby inherited methods are first, and member
   * methods are last.
   *
   * @param cls The class in which to find declared methods.
   * @param predicate The {@link Predicate} used to decide whether the method
   *          should be included in the returned array.
   * @return An array of {@link Method} objects declared in {@code cls}
   *         (including inherited methods).
   * @throws IllegalArgumentException If {@code cls} or {@code predicate} is null.
   */
  public static Method[] getDeclaredMethodsDeep(final Class<?> cls, final Predicate<Method> predicate) {
    return Repeat.Recursive.inverted(Assertions.assertNotNull(cls), cls.getDeclaredMethods(), Method.class, declaredMethodWithPredicateRecurser, Assertions.assertNotNull(predicate));
  }

  /**
   * Returns an array of {@link Method} objects declared in {@code cls}
   * (excluding inherited methods) that have an annotation of
   * {@code annotationType}.
   * <p>
   * Declared methods include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents a class or interface with no declared methods,
   * then this method returns an array of length 0.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are not sorted and are not in any
   * particular order.
   *
   * @param cls The class in which to find declared methods.
   * @param annotationType The type of the annotation to match.
   * @return An array of {@link Method} objects declared in {@code cls}
   *         (excluding inherited methods) that have an annotation of
   *         {@code annotationType}.
   * @throws IllegalArgumentException If {@code cls} or {@code annotationType} is
   *           null.
   */
  public static Method[] getDeclaredMethodsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.ordered(Assertions.assertNotNull(cls).getDeclaredMethods(), Method.class, declaredMethodWithAnnotationFilter, Assertions.assertNotNull(annotationType));
  }

  /**
   * Returns an array of {@link Method} objects declared in {@code cls}
   * (including inherited methods) that have an annotation of
   * {@code annotationType}.
   * <p>
   * Declared methods include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are sorted reflecting the inheritance
   * graph of {@code cls}, whereby inherited methods are first, and member
   * methods are last.
   *
   * @param cls The class in which to find declared methods.
   * @param annotationType The type of the annotation to match.
   * @return An array of {@link Method} objects declared in {@code cls}
   *         (including inherited methods) that have an annotation of
   *         {@code annotationType}.
   * @throws IllegalArgumentException If {@code cls} or {@code annotationType} is
   *           null.
   */
  public static Method[] getDeclaredMethodsWithAnnotationDeep(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.inverted(Assertions.assertNotNull(cls), cls.getDeclaredMethods(), Method.class, declaredMethodWithAnnotationRecurser, Assertions.assertNotNull(annotationType));
  }

  /**
   * Returns an array of {@link Class} objects declared in {@code cls}
   * (excluding inherited classes) that have an annotation of
   * {@code annotationType}.
   * <p>
   * Declared classes include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents a class or interface with no declared classes,
   * then this method returns an array of length 0.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are not sorted and are not in any
   * particular order.
   *
   * @param cls The class in which to find declared methods.
   * @param annotationType The type of the annotation to match.
   * @return An array of {@link Class} objects declared in {@code cls}
   *         (excluding inherited classes) that have an annotation of
   *         {@code annotationType}.
   * @throws IllegalArgumentException If {@code cls} or {@code annotationType} is
   *           null.
   */
  @SuppressWarnings("unchecked")
  public static Class<?>[] getDeclaredClassesWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.ordered(Assertions.assertNotNull(cls).getDeclaredClasses(), (Class<Class<?>>)Class.class.getClass(), classWithAnnotationFilter, Assertions.assertNotNull(annotationType));
  }

  /**
   * Returns an array of {@link Class} objects declared in {@code cls}
   * (including inherited classes) that have an annotation of
   * {@code annotationType}.
   * <p>
   * Declared classes include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * If {@code cls} represents an array type, a primitive type, or void, then
   * this method returns an array of length 0.
   * <p>
   * The elements in the returned array are sorted reflecting the inheritance
   * graph of {@code cls}, whereby inherited classes are first, and member
   * classes are last.
   *
   * @param cls The class in which to find declared methods.
   * @param annotationType The type of the annotation to match.
   * @return An array of {@link Class} objects declared in {@code cls}
   *         (including inherited classes) that have an annotation of
   *         {@code annotationType}.
   * @throws IllegalArgumentException If {@code cls} or {@code annotationType} is
   *           null.
   */
  @SuppressWarnings("unchecked")
  public static Class<?>[] getDeclaredClassesWithAnnotationDeep(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.inverted(Assertions.assertNotNull(cls), cls.getDeclaredClasses(), (Class<Class<?>>)Class.class.getClass(), classWithAnnotationRecurser, Assertions.assertNotNull(annotationType));
  }

  /**
   * Returns the annotation for the specified {@code annotationClass} on the
   * provided class if such an annotation is <i>present</i>, else null.
   *
   * @implNote This method differentiates itself from
   *           {@link Class#getAnnotation(Class)} by continuing to look at each
   *           superclass of the provided class if the specified annotation
   *           cannot be found.
   * @param <A> The type of the annotation to query for and return if present.
   * @param cls The {@link Class} on which to look for the specified annotation
   *          type.
   * @param annotationClass The {@link Class} object corresponding to the
   *          annotation type.
   * @return The annotation for the specified annotation type on the provided
   *         class if present, else null.
   * @throws IllegalArgumentException If {@code cls} or {@code annotationClass}
   *           is null.
   */
  public static <A extends Annotation>A getAnnotationDeep(final Class<?> cls, final Class<A> annotationClass) {
    Class<?> parent = Assertions.assertNotNull(cls);
    Assertions.assertNotNull(annotationClass);
    A annotation;
    do {
      annotation = parent.getAnnotation(annotationClass);
      if (annotation != null)
        return annotation;
    }
    while ((parent = parent.getSuperclass()) != null);
    return null;
  }

  /**
   * Returns {@code true} if an annotation for the specified type is
   * <i>present</i> on the provided class, else {@code false}. This method is
   * designed primarily for convenient access to marker annotations.
   * <p>
   * The truth value returned by this method is equivalent to:
   * {@code Classes.getAnnotationDeep(cls,annotationClass) != null}
   * <p>
   * The body of the default method is specified to be the code above.
   * <p>
   *
   *
   * @param cls The {@link Class} on which to look for the specified annotation
   *          type.
   * @param annotationClass The {@link Class} object corresponding to the
   *          annotation type.
   * @return {@code true} if an annotation for the specified annotation type is
   *         present on this element, else {@code false}.
   * @throws IllegalArgumentException If {@code cls} or {@code annotationClass}
   *           is null.
   */
  public static boolean isAnnotationPresentDeep(final Class<?> cls, final Class<? extends Annotation> annotationClass) {
    Class<?> parent = Assertions.assertNotNull(cls);
    Assertions.assertNotNull(annotationClass);
    do {
      if (parent.isAnnotationPresent(annotationClass))
        return true;
    }
    while ((parent = parent.getSuperclass()) != null);
    return false;
  }

  /**
   * Returns all interfaces implemented by the class or interface represented by
   * the specified class. This method differentiates itself from
   * {@link Class#getInterfaces()} by returning <i>all</i> interfaces (full
   * depth and breadth) instead of just the interfaces <i>directly</i>
   * implemented by the class.
   *
   * @param cls The class.
   * @return All interfaces implemented by the class or interface represented by
   *         the specified class.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static Class<?>[] getAllInterfaces(Class<?> cls) {
    Assertions.assertNotNull(cls);
    Class<?>[] thisInterfaces = null;
    LinkedHashSet<Class<?>> allInterfaces = null;
    do {
      thisInterfaces = cls.getInterfaces();
      if (thisInterfaces.length == 0)
        continue;

      if (allInterfaces == null)
        allInterfaces = new LinkedHashSet<>(4);

      for (final Class<?> iface : thisInterfaces)
        getAllInterfaces(iface, allInterfaces);
    }
    while ((cls = cls.getSuperclass()) != null);
    return allInterfaces == null ? thisInterfaces : allInterfaces.toArray(new Class[allInterfaces.size()]);
  }

  /**
   * Adds all interfaces extended by the specified {@code iface} interface
   * {@link Class}.
   *
   * @param iface The interface {@link Class}.
   * @param allInterfaces The set into which all extended interfaces are to be
   *          added.
   * @throws IllegalArgumentException If {@code iface} or {@code allInterfaces} is
   *           null.
   */
  private static void getAllInterfaces(final Class<?> iface, final LinkedHashSet<Class<?>> allInterfaces) {
    Assertions.assertNotNull(iface);
    Assertions.assertNotNull(allInterfaces);
    if (allInterfaces.contains(iface))
      return;

    allInterfaces.add(iface);
    for (final Class<?> extended : iface.getInterfaces())
      getAllInterfaces(extended, allInterfaces);
  }

  /**
   * Returns all generic interfaces implemented by the class or interface
   * represented by the specified class. This method differentiates itself from
   * {@link Class#getGenericInterfaces()} by returning <i>all</i> generic
   * interfaces (full depth and breadth) instead of just the interfaces
   * <i>directly</i> implemented by the class.
   *
   * @param cls The class.
   * @return All generic interfaces implemented by the class or interface
   *         represented by the specified class.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static Type[] getAllGenericInterfaces(Class<?> cls) {
    Assertions.assertNotNull(cls);
    Class<?>[] thisInterfaces = null;
    Type[] thisGenericInterfaces = null;
    LinkedHashSet<Class<?>> allInterfaces = null;
    LinkedHashSet<Type> allGenericInterfaces = null;
    do {
      thisInterfaces = cls.getInterfaces();
      thisGenericInterfaces = cls.getGenericInterfaces();
      if (thisGenericInterfaces.length > 0) {
        if (allGenericInterfaces == null)
          allGenericInterfaces = new LinkedHashSet<>(2);

        Collections.addAll(allGenericInterfaces, thisGenericInterfaces);
      }

      if (thisInterfaces.length == 0)
        continue;

      if (allInterfaces == null) {
        allInterfaces = new LinkedHashSet<>(4);
        if (allGenericInterfaces == null)
          allGenericInterfaces = new LinkedHashSet<>(2);
      }

      for (final Class<?> iface : thisInterfaces)
        getAllGenericInterfaces(iface, allInterfaces, allGenericInterfaces);
    }
    while ((cls = cls.getSuperclass()) != null);
    return allGenericInterfaces == null ? new Type[0] : allGenericInterfaces.toArray(new Type[allGenericInterfaces.size()]);
  }

  /**
   * Adds all generic interfaces extended by the specified {@code iface}
   * interface {@link Class}.
   *
   * @param iface The interface {@link Class}.
   * @param allInterfaces The set into which all extended interfaces are to be
   *          added.
   * @param allGenericInterfaces The set into which all extended generic
   *          interfaces are to be added.
   * @throws IllegalArgumentException If {@code iface}, {@code allInterfaces} or
   *           {@code allGenericInterfaces} is null.
   */
  private static void getAllGenericInterfaces(final Class<?> iface, final LinkedHashSet<Class<?>> allInterfaces, final LinkedHashSet<Type> allGenericInterfaces) {
    Assertions.assertNotNull(iface);
    Assertions.assertNotNull(allInterfaces);
    Assertions.assertNotNull(allGenericInterfaces);
    if (allInterfaces.contains(iface))
      return;

    allInterfaces.add(iface);
    Collections.addAll(allGenericInterfaces, iface.getGenericInterfaces());
    for (final Class<?> extended : iface.getInterfaces())
      getAllGenericInterfaces(extended, allInterfaces, allGenericInterfaces);
  }

  /**
   * Returns the greatest common superclass of the specified array of classes.
   *
   * @param classes The array of classes.
   * @return The greatest common superclass of the specified array of classes.
   * @throws IllegalArgumentException If {@code classes} or a member of
   *           {@code classes} is null, or if the number of arguments in the
   *           {@code classes} parameter is 0.
   */
  public static Class<?> getGreatestCommonSuperclass(final Class<?> ... classes) {
    if (Assertions.assertNotNull(classes).length == 0)
      throw new IllegalArgumentException("Number of arguments must be greater than 0");

    if (classes.length == 1)
      return classes[0];

    Class<?> gcc = getGreatestCommonSuperclass(classes[0], classes[1]);
    for (int i = 2; i < classes.length && gcc != null; ++i)
      gcc = getGreatestCommonSuperclass(gcc, classes[i]);

    return gcc;
  }

  /**
   * Returns the greatest common superclass of the classes of the specified
   * array of objects.
   *
   * @param <T> The type parameter of the specified array of objects.
   * @param objects The array of objects.
   * @return The greatest common superclass of the classes of the specified
   *         array of objects.
   * @throws IllegalArgumentException If {@code objects}, or a member of
   *           {@code objects} is null, or if the number of arguments in the
   *           {@code objects} parameter is 0.
   */
  @SafeVarargs
  public static <T>Class<?> getGreatestCommonSuperclass(final T ... objects) {
    if (Assertions.assertNotNull(objects).length == 0)
      throw new IllegalArgumentException("Number of arguments must be greater than 0");

    return getGreatestCommonSuperclass0(objects);
  }

  /**
   * Returns the greatest common superclass of the classes of the specified
   * {@link Collection} of objects.
   *
   * @param <T> The type parameter of the specified {@link Collection} of
   *          objects.
   * @param objects The array of objects.
   * @return The greatest common superclass of the classes of the specified
   *         {@link Collection} of objects.
   * @throws IllegalArgumentException If {@code objects}, or an element of
   *           {@code objects} is null, or if the number of elements in the
   *           {@code objects} collection is 0.
   */
  public static <T>Class<?> getGreatestCommonSuperclass(final Collection<T> objects) {
    if (Assertions.assertNotNull(objects).size() == 0)
      throw new IllegalArgumentException("Collection size must be greater than 0");

    return getGreatestCommonSuperclass0(objects.toArray());
  }

  private static Class<?> getGreatestCommonSuperclass(Class<?> c1, Class<?> c2) {
    Assertions.assertNotNull(c1);
    Assertions.assertNotNull(c2);
    final Class<?> c0 = c2;
    do {
      do
        if (c1.isAssignableFrom(c2))
          return c1;
      while ((c2 = c2.getSuperclass()) != null);
      c2 = c0;
    }
    while ((c1 = c1.getSuperclass()) != null);
    return null;
  }

  @SafeVarargs
  private static <T>Class<?> getGreatestCommonSuperclass0(final T ... objects) {
    if (objects.length == 1)
      return objects[0].getClass();

    Class<?> gcc = getGreatestCommonSuperclass(objects[0].getClass(), objects[1].getClass());
    for (int i = 2; i < objects.length && gcc != null; ++i)
      gcc = getGreatestCommonSuperclass(gcc, objects[i].getClass());

    return gcc;
  }

  private static class CallingClass extends SecurityManager {
    @Override
    public Class<?>[] getClassContext() {
      return super.getClassContext();
    }
  }

  /**
   * Returns the current execution stack as an array of classes.
   * <p>
   * The length of the array is the number of methods on the execution stack.
   * The element at index {@code 0} is the class of the currently executing
   * method, the element at index {@code 1} is the class of that method's
   * caller, and so on.
   *
   * @return The current execution stack as an array of classes.
   */
  public static Class<?>[] getExecutionStack() {
    final Class<?>[] context = new CallingClass().getClassContext();
    final Class<?>[] executionStack = new Class[context.length - 3];
    for (int i = 3; i < context.length; ++i)
      executionStack[i - 3] = context[i];

    return executionStack;
  }

  /**
   * Returns the boxed {@link Class} for the specified {@code primitiveType}
   * class. If the specified class does not represent a primitive type, the same
   * class is returned.
   *
   * @param primitiveType The {@link Class} representing a primitive type.
   * @return The boxed {@link Class} for the specified {@code primitiveType}
   *         class. If the specified class does not represent a primitive type,
   *         the same class is returned.
   */
  public static Class<?> toWrapper(final Class<?> primitiveType) {
    if (!primitiveType.isPrimitive())
      return primitiveType;

    if (primitiveType == int.class)
      return Integer.class;

    if (primitiveType == long.class)
      return Long.class;

    if (primitiveType == boolean.class)
      return Boolean.class;

    if (primitiveType == byte.class)
      return Byte.class;

    if (primitiveType == char.class)
      return Character.class;

    if (primitiveType == float.class)
      return Float.class;

    if (primitiveType == double.class)
      return Double.class;

    if (primitiveType == short.class)
      return Short.class;

    if (primitiveType == void.class)
      return Void.class;

    throw new UnsupportedOperationException("Unsupported class type: " + primitiveType.getName());
  }

  private static final IdentityHashMap<Class<?>,Executable> classToExecutable = new IdentityHashMap<>();

  /**
   * Creates an instance of the specified class with the provided parameters.
   * <p>
   * The specified type must define either {@code fromString(String)} if the
   * provided parameter object is a {@link String}, {@code valueOf(...)}, or
   * {@code <init>(...)}.
   *
   * @param <T> The type parameter for the instance that is to be created.
   * @param type The class of the instance that is to be created.
   * @param parameters The parameters.
   * @return An instance of the specified class with the value of the provided
   *         string
   * @throws IllegalAccessException If this Constructor object is enforcing Java
   *           language access control and the underlying constructor is
   *           inaccessible.
   * @throws InstantiationException If the class that declares the underlying
   *           constructor represents an abstract class.
   * @throws InvocationTargetException If the underlying constructor throws an
   *           exception.
   * @throws IllegalArgumentException If the specified {@code type} or
   *           {@code parameters} is null, or if the specified string is empty,
   *           or if an instance of the specific class type does not define
   *           {@code <init>(T)}, {@code fromString(String)} if the provided
   *           object is a {@link String}, or {@code valueOf(T)}.
   */
  @SuppressWarnings("unchecked")
  public static <T>T newInstance(Class<T> type, final Object ... parameters) throws IllegalAccessException, InstantiationException, InvocationTargetException {
    Assertions.assertNotNull(type);
    Assertions.assertNotNull(parameters);
    if (type.isPrimitive())
      type = (Class<T>)toWrapper(type);

    final Executable executable = classToExecutable.get(type);
    if (executable != null)
      return (T)(executable instanceof Constructor ? ((Constructor<?>)executable).newInstance(parameters) : ((Method)executable).invoke(null, parameters));

    final Class<?>[] parameterTypes = new Class[parameters.length];
    for (int i = 0, len = parameters.length; i < len; ++i)
      parameterTypes[i] = parameters[i] == null ? null : parameters[i].getClass();

    if (parameterTypes.length == 1 && parameterTypes[0] == String.class) {
      final Method fromString = Classes.getMethod(type, "fromString", parameterTypes);
      if (fromString != null && Modifier.isStatic(fromString.getModifiers())) {
        classToExecutable.put(type, fromString);
        return (T)fromString.invoke(null, parameters);
      }
    }

    final Method valueOf = Classes.getCompatibleMethod(type, "valueOf", parameterTypes);
    if (valueOf != null && Modifier.isStatic(valueOf.getModifiers())) {
      classToExecutable.put(type, valueOf);
      return (T)valueOf.invoke(null, parameters);
    }

    final Constructor<?> constructor = Classes.getCompatibleConstructor(type, parameterTypes);
    if (constructor != null) {
      classToExecutable.put(type, constructor);
      return (T)constructor.newInstance(parameters);
    }

    final String types = Arrays.stream(parameterTypes).map(p -> p.getName()).collect(Collectors.joining(","));
    final StringBuilder message = new StringBuilder();
    message.append(type.getName() + " does not define <init>(" + types + ")");
    if (parameterTypes.length == 1 && parameterTypes[0] == String.class)
      message.append(", valueOf(" + types + "), or fromString(" + types + ")");
    else
      message.append(" or valueOf(" + types + ")");

    throw new IllegalArgumentException(message.toString());
  }

  /**
   * Returns the {@link Class} object associated with the class or interface
   * with the given string name.
   * <p>
   * Invoking this method is equivalent to:
   *
   * <pre>
   *  {@code Classes.forNameOrNull(className, true, currentLoader)}
   * </pre>
   *
   * where {@code currentLoader} denotes the defining class loader of the
   * current class.
   *
   * @param className the fully qualified name of the desired class.
   * @return The {@link Class} object for the class with the specified name, or
   *         {@code null} if the class cannot be located.
   * @exception LinkageError If the linkage fails.
   * @exception ExceptionInInitializerError If the initialization provoked by
   *              this method fails.
   * @see java.lang.Class#forName(String)
   */
  public static Class<?> forNameOrNull(final String className) {
    try {
      return Class.forName(className);
    }
    catch (final ClassNotFoundException e) {
      return null;
    }
  }

  /**
   * Returns the {@link Class} object associated with the class or interface
   * with the given string name, using the given class loader. Given the fully
   * qualified name for a class or interface (in the same format returned by
   * {@code getName}) this method attempts to locate, load, and link the class
   * or interface. The specified class loader is used to load the class or
   * interface. If the parameter {@code loader} is null, the class is loaded
   * through the bootstrap class loader. The class is initialized only if the
   * {@code initialize} parameter is {@code true} and if it has not been
   * initialized earlier.
   *
   * @param name Fully qualified name of the desired class.
   * @param initialize If {@code true} the class will be initialized.
   * @param loader Class loader from which the class must be loaded.
   * @return The {@link Class} object representing the desired class, or
   *         {@code null} if the class cannot be located by the specified class
   *         loader.
   * @exception LinkageError If the linkage fails.
   * @exception ExceptionInInitializerError If the initialization provoked by
   *              this method fails.
   * @exception SecurityException If a security manager is present, and the
   *              {@code loader} is {@code null}, and the caller's class loader
   *              is not {@code null}, and the caller does not have the
   *              {@link RuntimePermission}{@code ("getClassLoader")}.
   * @see java.lang.Class#forName(String,boolean,ClassLoader)
   */
  public static Class<?> forNameOrNull(final String name, final boolean initialize, final ClassLoader loader) {
    try {
      return Class.forName(name, initialize, loader);
    }
    catch (final ClassNotFoundException e) {
      return null;
    }
  }

  private static String getName(final Class<?> cls) {
    return cls.isPrimitive() || cls.isArray() ? cls.getCanonicalName() : cls.getName();
  }

  private static String getSignature(final Method method) {
    final StringBuilder builder = new StringBuilder();
    builder.append(method.getModifiers());
    builder.append(':');
    builder.append(method.getDeclaringClass().getName());
    builder.append('.');
    builder.append(method.getName());
    final int i = builder.length();
    for (final Class<?> parameterType : method.getParameterTypes())
      builder.append(',').append(getName(parameterType));

    if (builder.length() > i)
      builder.setCharAt(i, '(');
    else
      builder.append('(');

    builder.append("):");
    builder.append(getName(method.getReturnType()));
    return builder.toString();
  }

  private static String getSignature(final CtMethod method) throws Exception {
    return method.getModifiers() + ":" + method.getLongName() + ":" + method.getReturnType().getName();
  }

  private static Set<String> errorMethodSigs;
  private static Boolean hasJavaAssist;

  /**
   * Sorts the provided {@link Method} array in order of the declaration of the
   * methods in their defining class. The {@code methods} array is expected to
   * contain methods that belong to:
   * <ul>
   * <li>A single class.</li>
   * <li>Classes in an inheritance hierarchy.</li>
   * </ul>
   * If the {@code methods} array contains {@link Method}s that belong to
   * classes in an inheritance hierarchy, the methods defined in classes at the
   * top of the hierarchy (super-classes) are ordered before those that belong
   * to sub-classes.
   *
   * @implNote This function utilizes bytecode introspection via
   *           <a href="https://www.javassist.org/">Javassist</a> to determine
   *           the declarative order of the provided methods. Javassist is
   *           declared as {@code <scope>provided</scope>} in this Maven module,
   *           and therefore must be provided explicitly. This function will
   *           return {@code false} if Javassist is not present on the system
   *           classpath.
   * @param methods The {@code Method} array to be sorted.
   * @return {@code true} if Javassist is present on the system classpath and
   *         line number information is available in the bytecode of the
   *         {@code methods}, otherwise {@code false}.
   */
  public static boolean sortDeclarativeOrder(final Method[] methods) {
    if (hasJavaAssist == null)
      hasJavaAssist = forNameOrNull("javassist.ClassPool") != null;

    if (!hasJavaAssist)
      return false;

    if (methods.length == 0)
      return true;

    // First, sort the methods based on the class hierarchy
    Arrays.sort(methods, (a, b) -> a.getDeclaringClass() == b.getDeclaringClass() ? 0 : a.getDeclaringClass().isAssignableFrom(b.getDeclaringClass()) ? -1 : 1);

    // Create a map of the method signature to the index of the method in the methods array
    final HashMap<String,Integer> methodSigToIndex = new HashMap<>(methods.length);
    for (int i = 0; i < methods.length; ++i)
      methodSigToIndex.put(getSignature(methods[i]), i);

    // Create a composite set connecting the method to its line number
    final Object[][] methodLineNumbers = new Object[methods.length][2];
    for (int i = 0; i < methods.length; ++i)
      methodLineNumbers[i] = new Object[] {methods[i], null};

    final boolean[] success = {true};
    try {
      final ClassPool pool = ClassPool.getDefault();
      Class<?> cls, last = null;
      for (int i = 0; i < methodLineNumbers.length; ++i, last = cls) {
        cls = methods[i].getDeclaringClass();
        if (cls != last) {
          final CtClass ctClass = pool.get(cls.getName());
          for (final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            final Integer index = methodSigToIndex.get(getSignature(ctMethod));
            if (index != null) {
              final int lineNumber = ctMethod.getMethodInfo().getLineNumber(0);
              success[0] |= lineNumber != -1;
              methodLineNumbers[index][1] = lineNumber;
            }
          }
        }
      }

      Arrays.sort(methodLineNumbers, (a, b) -> {
        final Class<?> clsA = ((Method)a[0]).getDeclaringClass();
        final Class<?> clsB = ((Method)b[0]).getDeclaringClass();
        if (clsA != clsB)
          return clsA.isAssignableFrom(clsB) ? -1 : 1;

        if (a[1] == null) {
          success[0] = false;
          warnNotFound((Method)a[0]);
          return b[1] == null ? 0 : 1;
        }

        if (b[1] == null) {
          success[0] = false;
          warnNotFound((Method)b[0]);
          return a[1] == null ? 0 : -1;
        }

        return Integer.compare((Integer)a[1], (Integer)b[1]);
      });

      for (int i = 0; i < methods.length; ++i)
        methods[i] = (Method)methodLineNumbers[i][0];

      return success[0];
    }
    catch (final Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static void warnNotFound(final Method method) {
    if (!logger.isWarnEnabled())
      return;

    final String signature = getSignature(method);
    if (errorMethodSigs == null)
      errorMethodSigs = new HashSet<>();
    else if (errorMethodSigs.contains(signature))
      return;

    errorMethodSigs.add(signature);
    logger.warn("sortDeclarativeOrder: Could not find " + signature);
  }

  /**
   * Returns the internal name representation of the provided class name. The
   * internal name of a class is its fully qualified name (as returned by
   * {@link Class#getName()}, where {@code '.'} are replaced by {@code '/'}).
   * This method should only be used for an object or array type.
   *
   * @param className The class name for which to return the internal name.
   * @return The internal name representation of the provided class name.
   */
  public static String getInternalName(final String className) {
    return className.replace('.', '/');
  }

  private static final Class<?>[] primitiveClasses = {boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class, void.class};
  private static final String[] primitiveInternalNames = {"Z", "B", "C", "D", "F", "I", "J", "S", "V"};

  /**
   * Returns a string containing the internal names of the given classes,
   * appended sans delimiter.
   *
   * @param classes The classes for which to return a string containing the
   *          internal names.
   * @return A string containing the internal names of the given classes,
   *         appended sans delimiter.
   * @throws IllegalArgumentException If {@code classes} or any member of
   *           {@code classes} is null.
   * @see #getInternalName(Class)
   */
  public static String getInternalName(final Class<?> ... classes) {
    Assertions.assertNotNull(classes);
    final StringBuilder builder = new StringBuilder();
    for (final Class<?> cls : classes)
      builder.append(getInternalName(cls));

    return builder.toString();
  }

  /**
   * Returns the internal name of the given class.
   * <p>
   * The internal name of a primitive type is represented by one character:
   *
   * <pre>
   * {@code B} = {@code byte}
   * {@code C} = {@code char}
   * {@code D} = {@code double}
   * {@code F} = {@code float}
   * {@code I} = {@code int}
   * {@code J} = {@code long}
   * {@code S} = {@code short}
   * {@code Z} = {@code boolean}
   * </pre>
   *
   * The internal name of a class or interface is represented by its fully
   * qualified name, with an 'L' prefix and a ';' suffix. The dots {@code '.'}
   * in the fully qualified class name are replaced by {@code '/'} (for inner
   * classes, the {@code '.'} separating the outer class name from the inner
   * class name is replaced by a {@code '$'}).
   *
   * @param cls The class for which to return the internal name.
   * @return The internal name of the given class.
   * @throws IllegalArgumentException If {@code cls} is null.
   */
  public static String getInternalName(final Class<?> cls) {
    if (Assertions.assertNotNull(cls).isArray())
      return "[" + getInternalName(cls.getComponentType());

    for (int i = 0; i < primitiveClasses.length; ++i)
      if (primitiveClasses[i] == cls)
        return primitiveInternalNames[i];

    return "L" + cls.getName().replace('.', '/') + ";";
  }

  private Classes() {
  }
}