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
import java.lang.reflect.Field;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Utility providing implementations of methods missing from the API of
 * {@link Class}.
 */
public final class Classes {
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
   * @throws IllegalArgumentException If {@code className} is not a valid
   *           <a href=
   *           "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java
   *           Identifier</a>.
   * @throws NullPointerException If {@code className} is null.
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
   * @throws IllegalArgumentException If {@code className} is not a valid
   *           <a href=
   *           "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java
   *           Identifier</a>.
   * @throws NullPointerException If {@code className} is null.
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
   * @throws IllegalArgumentException If {@code className} is not a valid
   *           <a href=
   *           "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">Java
   *           Identifier</a>.
   * @throws NullPointerException If {@code className} is null.
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
   * Returns the "Compound Name" of the class or interface represented by
   * {@code cls}.
   * <p>
   * The "Compound Name" is the fully qualified name of a class
   * ({@link Class#getName()} with its package name
   * ({@link Class#getPackage()}.getName()) removed.
   * <p>
   * For example:
   * <ol>
   * <li>The "Compound Name" of {@code java.lang.String} is {@link String}.</li>
   * <li>The "Compound Name" of {@code java.lang.Map.Entry} is
   * {@code Map$Entry}.</li>
   * </ol>
   *
   * @param cls The class or interface.
   * @return The "Compound Name" of the class or interface represented by
   *         {@code cls}.
   * @throws NullPointerException If {@code cls} is null.
   */
  public static String getCompoundName(final Class<?> cls) {
    final String pkg = cls.getPackage().getName();
    return pkg.length() == 0 ? cls.getName() : cls.getName().substring(pkg.length() + 1);
  }

  /**
   * Returns the canonical "Compound Name" of the class or interface represented
   * by {@code cls}.
   * <p>
   * The canonical "Compound Name" is the fully qualified name of a class
   * ({@link Class#getCanonicalName()} with its package name
   * ({@link Class#getPackage()}.getName()) removed.
   * <p>
   * For example:
   * <ol>
   * <li>The canonical "Compound Name" of {@code java.lang.String} is
   * {@link String}.</li>
   * <li>The canonical "Compound Name" of {@code java.lang.Map.Entry} is
   * {@code Map.Entry}.</li>
   * </ol>
   *
   * @param cls The class or interface.
   * @return The canonical "Compound Name" of the class or interface represented
   *         by {@code cls}.
   * @throws NullPointerException If {@code cls} is null.
   */
  public static String getCanonicalCompoundName(final Class<?> cls) {
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
   * @throws NullPointerException If {@code cls} is null.
   */
  public static Type[] getSuperclassGenericTypes(final Class<?> cls) {
    return cls.getGenericSuperclass() instanceof ParameterizedType ? ((ParameterizedType)cls.getGenericSuperclass()).getActualTypeArguments() : null;
  }

  /**
   * Traverses and returns the class hierarchy of the specified {@link Class}.
   * This method visits the superclasses and superinterfaces with Breadth First
   * Search.
   *
   * @param cls The {@link Class}.
   * @param forEach The {@link Predicate} called for each visited superclass and
   *          superinterface. If the {@link Predicate} returns {@code false},
   *          traversal will terminate, and the method will return the set of
   *          classes that had been visited before termination.
   * @return The class hierarchy of the specified {@link Class}.
   * @throws NullPointerException If {@code cls} or {@code forEach} is null.
   */
  public static Set<Class<?>> getClassHierarchy(Class<?> cls, final Predicate<? super Class<?>> forEach) {
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

  private static boolean visitSuperclass(final Class<?> cls, final Queue<? super Class<?>> queue, final Set<? super Class<?>> visited, final Predicate<? super Class<?>> forEach) {
    if (cls == null || !visited.add(cls))
      return true;

    if (!forEach.test(cls))
      return false;

    queue.add(cls);
    return true;
  }

  /**
   * Returns the array of generic type classes for the specified field. If the
   * field is not a parameterized type, this method will return an empty array.
   *
   * @param field The {@link Field}
   * @return The array of generic type classes for the specified field.
   * @throws NullPointerException If {@code field} is null.
   */
  public static Class<?>[] getGenericClasses(final Field field) {
    final Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType))
      return new Class[0];

    final Type[] types = ((ParameterizedType)genericType).getActualTypeArguments();
    final Class<?>[] classes = new Class[types.length];
    for (int i = 0; i < classes.length; ++i) {
      if (types[i] instanceof Class)
        classes[i] = (Class<?>)types[i];
      else if (types[i] instanceof ParameterizedType)
        classes[i] = (Class<?>)((ParameterizedType)types[i]).getRawType();
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
   * @throws NullPointerException If {@code cls} or {@code name} is null.
   * @throws SecurityException If a security manager, <i>s</i>, is present and
   *           the caller's class loader is not the same as or an ancestor of
   *           the class loader for the current class and invocation of
   *           {@link SecurityManager#checkPackageAccess s.checkPackageAccess()}
   *           denies access to the package of this class.
   */
  public static Field getField(final Class<?> cls, final String name) {
    return Classes.getField(cls, name, false);
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
   * @throws NullPointerException If {@code cls} or {@code name} is null.
   * @throws SecurityException If a security manager, <i>s</i>, is present and
   *           the caller's class loader is not the same as or an ancestor of
   *           the class loader for the current class and invocation of
   *           {@link SecurityManager#checkPackageAccess s.checkPackageAccess()}
   *           denies access to the package of this class.
   */
  public static Field getDeclaredField(final Class<?> cls, final String name) {
    return Classes.getField(cls, name, true);
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
   * @throws NullPointerException If {@code cls} or {@code name} is null.
   * @throws SecurityException If a security manager, <i>s</i>, is present and
   *           the caller's class loader is not the same as or an ancestor of
   *           the class loader for the current class and invocation of
   *           {@link SecurityManager#checkPackageAccess s.checkPackageAccess()}
   *           denies access to the package of this class.
   */
  public static Field getDeclaredFieldDeep(Class<?> cls, final String name) {
    Field field;
    do
      field = getField(cls, name, true);
    while (field == null && (cls = cls.getSuperclass()) != null);
    return field;
  }

  /**
   * Returns a Constructor object that reflects the specified public constructor
   * of the class or interface represented by {@code cls} (including inherited
   * constructors), or {@code null} if the constructor is not found.
   * <p>
   * The {@code parameterTypes} parameter is an array of Class objects that
   * identify the constructor's formal parameter types, in declared order. If
   * {@code cls} represents an inner class declared in a non-static context, the
   * formal parameter types include the explicit enclosing instance as the first
   * parameter.
   * <p>
   * This method differentiates itself from
   * {@link Class#getConstructor(Class...)} by returning {@code null} when a
   * method is not found, instead of throwing {@link NoSuchMethodException}.
   *
   * @param cls The class in which to find the public constructor.
   * @param parameterTypes The parameter array.
   * @return A Constructor object that reflects the specified public constructor
   *         of the class or interface represented by {@code cls} (including
   *         inherited constructors), or {@code null} if the constructor is not
   *         found.
   * @throws NullPointerException If {@code cls} is null.
   */
  public static Constructor<?> getConstructor(final Class<?> cls, final Class<?> ... parameterTypes) {
    final Constructor<?>[] constructors = cls.getConstructors();
    for (final Constructor<?> constructor : constructors)
      if (Arrays.equals(constructor.getParameterTypes(), parameterTypes))
        return constructor;

    return null;
  }

  /**
   * Returns a Constructor object that reflects the specified declared
   * constructor of the class or interface represented by {@code cls} (excluding
   * inherited constructors), or {@code null} if the constructor is not found.
   * <p>
   * Declared constructors include public, protected, default (package) access,
   * and private visibility.
   * <p>
   * The {@code parameterTypes} parameter is an array of Class objects that
   * identify the constructor's formal parameter types, in declared order. If
   * {@code cls} represents an inner class declared in a non-static context, the
   * formal parameter types include the explicit enclosing instance as the first
   * parameter.
   * <p>
   * This method differentiates itself from
   * {@link Class#getDeclaredConstructor(Class...)} by returning {@code null}
   * when a method is not found, instead of throwing
   * {@link NoSuchMethodException}.
   *
   * @param cls The class in which to find the declared constructor.
   * @param parameterTypes The parameter array.
   * @return A Constructor object that reflects the specified declared
   *         constructor of the class or interface represented by {@code cls}
   *         (excluding inherited constructors), or {@code null} if the
   *         constructor is not found.
   * @throws NullPointerException If {@code cls} is null.
   */
  public static Constructor<?> getDeclaredConstructor(final Class<?> cls, final Class<?> ... parameterTypes) {
    final Constructor<?>[] constructors = cls.getDeclaredConstructors();
    for (final Constructor<?> constructor : constructors)
      if (Arrays.equals(constructor.getParameterTypes(), parameterTypes))
        return constructor;

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
   * @throws IllegalArgumentException If {@code newValue} does not match the
   *           required type of the value for {@code key}.
   * @throws NullPointerException If {@code annotation}, {@code key}, or
   *           {@code newValue} is null.
   */
  @SuppressWarnings("unchecked")
  public static <T>T setAnnotationValue(final Annotation annotation, final String key, final T newValue) {
    final Object handler = Proxy.getInvocationHandler(annotation);
    Objects.requireNonNull(key);
    Objects.requireNonNull(newValue);
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

    final T oldValue = (T)memberValues.get(key);
    if (oldValue == null)
      throw new IllegalArgumentException(key + " is not a valid key");

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
   * @throws NullPointerException If {@code cls} is null.
   */
  public static Field[] getDeclaredFieldsDeep(final Class<?> cls) {
    return Repeat.Recursive.inverted(cls, cls.getDeclaredFields(), Field.class, declaredFieldRecurser, null);
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
   * @throws NullPointerException If {@code cls} or {@code predicate} is null.
   */
  public static Field[] getDeclaredFieldsDeep(final Class<?> cls, final Predicate<Field> predicate) {
    return Repeat.Recursive.inverted(cls, cls.getDeclaredFields(), Field.class, declaredFieldWithPredicateRecurser, Objects.requireNonNull(predicate));
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
   * @throws NullPointerException If {@code cls} or {@code annotationType} is
   *           null.
   */
  public static Field[] getDeclaredFieldsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.ordered(cls.getDeclaredFields(), Field.class, declaredFieldWithAnnotationFilter, Objects.requireNonNull(annotationType));
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
   * @throws NullPointerException If {@code cls} or {@code annotationType} is
   *           null.
   */
  public static Field[] getDeclaredFieldsWithAnnotationDeep(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.inverted(cls, cls.getDeclaredFields(), Field.class, declaredFieldWithAnnotationRecurser, Objects.requireNonNull(annotationType));
  }

  /**
   * Returns a Method object that reflects the specified declared method of the
   * class or interface represented by {@code cls} (excluding inherited
   * methods), or {@code null} if the method is not found.
   * <p>
   * Declared methods include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * The {@code name} parameter is a String that specifies the simple name of
   * the desired method, and the {@code parameterTypes} parameter is an array of
   * Class objects that identify the method's formal parameter types, in
   * declared order. If more than one method with the same parameter types is
   * declared in a class, and one of these methods has a return type that is
   * more specific than any of the others, that method is returned; otherwise
   * one of the methods is chosen arbitrarily. If the name is {@code "<init>"}
   * or {@code "<clinit>"} this method returns {@code null}. If this Class
   * object represents an array type, then this method does not find the clone()
   * method.
   * <p>
   * This method differentiates itself from
   * {@link Class#getDeclaredMethod(String,Class...)} by returning {@code null}
   * when a method is not found, instead of throwing
   * {@link NoSuchMethodException}.
   *
   * @param cls The class in which to find the declared method.
   * @param name The simple name of the method.
   * @param parameterTypes The parameter array.
   * @return A Method object that reflects the specified declared method of the
   *         class or interface represented by {@code cls} (excluding inherited
   *         methods), or {@code null} if the method is not found.
   * @throws NullPointerException If {@code cls} or {@code name} is null.
   */
  public static Method getDeclaredMethod(final Class<?> cls, final String name, final Class<?> ... parameterTypes) {
    final Method[] methods = cls.getDeclaredMethods();
    for (final Method method : methods)
      if (name.equals(method.getName()) && Arrays.equals(method.getParameterTypes(), parameterTypes))
        return method;

    return null;
  }

  /**
   * Returns a Method object that reflects the specified declared method of the
   * class or interface represented by {@code cls} (including inherited
   * methods), or {@code null} if the method is not found.
   * <p>
   * Declared methods include public, protected, default (package) access, and
   * private visibility.
   * <p>
   * The {@code name} parameter is a String that specifies the simple name of
   * the desired method, and the {@code parameterTypes} parameter is an array of
   * Class objects that identify the method's formal parameter types, in
   * declared order. If more than one method with the same parameter types is
   * declared in a class, and one of these methods has a return type that is
   * more specific than any of the others, that method is returned; otherwise
   * one of the methods is chosen arbitrarily. If the name is {@code "<init>"}
   * or {@code "<clinit>"} this method returns {@code null}. If this Class
   * object represents an array type, then this method does not find the clone()
   * method.
   * <p>
   * This method differentiates itself from
   * {@link Class#getDeclaredMethod(String,Class...)} by returning {@code null}
   * when a method is not found, instead of throwing
   * {@link NoSuchMethodException}.
   *
   * @param cls The class in which to find the declared method.
   * @param name The simple name of the method.
   * @param parameterTypes The parameter array.
   * @return A Method object that reflects the specified declared method of the
   *         class or interface represented by {@code cls} (including inherited
   *         methods), or {@code null} if the method is not found.
   * @throws NullPointerException If {@code cls} or {@code name} is null.
   */
  public static Method getDeclaredMethodDeep(Class<?> cls, final String name, final Class<?> ... parameterTypes) {
    Method method;
    do
      method = getDeclaredMethod(cls, name, parameterTypes);
    while (method == null && (cls = cls.getSuperclass()) != null);
    return method;
  }

  /**
   * Returns an array of Method objects declared in {@code cls} (including
   * inherited methods).
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
   * @return An array of Method objects declared in {@code cls} (including
   *         inherited methods).
   * @throws NullPointerException If {@code cls} is null.
   */
  public static Method[] getDeclaredMethodsDeep(final Class<?> cls) {
    return Repeat.Recursive.inverted(cls, cls.getDeclaredMethods(), Method.class, declaredMethodRecurser, null);
  }

  /**
   * Returns an array of Method objects declared in {@code cls} (including
   * inherited methods), for which the provided {@link Predicate} returns
   * {@code true}.
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
   * @return An array of Method objects declared in {@code cls} (including
   *         inherited methods).
   * @throws NullPointerException If {@code cls} or {@code predicate} is null.
   */
  public static Method[] getDeclaredMethodsDeep(final Class<?> cls, final Predicate<Method> predicate) {
    return Repeat.Recursive.inverted(cls, cls.getDeclaredMethods(), Method.class, declaredMethodWithPredicateRecurser, Objects.requireNonNull(predicate));
  }

  /**
   * Returns an array of Method objects declared in {@code cls} (excluding
   * inherited methods) that have an annotation of {@code annotationType}.
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
   * @return An array of Method objects declared in {@code cls} (excluding
   *         inherited methods) that have an annotation of
   *         {@code annotationType}.
   * @throws NullPointerException If {@code cls} or {@code annotationType} is
   *           null.
   */
  public static Method[] getDeclaredMethodsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.ordered(cls.getDeclaredMethods(), Method.class, declaredMethodWithAnnotationFilter, Objects.requireNonNull(annotationType));
  }

  /**
   * Returns an array of Method objects declared in {@code cls} (including
   * inherited methods) that have an annotation of {@code annotationType}.
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
   * @return An array of Method objects declared in {@code cls} (including
   *         inherited methods) that have an annotation of
   *         {@code annotationType}.
   * @throws NullPointerException If {@code cls} or {@code annotationType} is
   *           null.
   */
  public static Method[] getDeclaredMethodsWithAnnotationDeep(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.inverted(cls, cls.getDeclaredMethods(), Method.class, declaredMethodWithAnnotationRecurser, Objects.requireNonNull(annotationType));
  }

  /**
   * Returns an array of Class objects declared in {@code cls} (excluding
   * inherited classes) that have an annotation of {@code annotationType}.
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
   * @return An array of Class objects declared in {@code cls} (excluding
   *         inherited classes) that have an annotation of
   *         {@code annotationType}.
   * @throws NullPointerException If {@code cls} or {@code annotationType} is
   *           null.
   */
  @SuppressWarnings("unchecked")
  public static Class<?>[] getDeclaredClassesWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.ordered(cls.getDeclaredClasses(), (Class<Class<?>>)Class.class.getClass(), classWithAnnotationFilter, Objects.requireNonNull(annotationType));
  }

  /**
   * Returns an array of Class objects declared in {@code cls} (including
   * inherited classes) that have an annotation of {@code annotationType}.
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
   * @return An array of Class objects declared in {@code cls} (including
   *         inherited classes) that have an annotation of
   *         {@code annotationType}.
   * @throws NullPointerException If {@code cls} or {@code annotationType} is
   *           null.
   */
  @SuppressWarnings("unchecked")
  public static Class<?>[] getDeclaredClassesWithAnnotationDeep(final Class<?> cls, final Class<? extends Annotation> annotationType) {
    return Repeat.Recursive.inverted(cls, cls.getDeclaredClasses(), (Class<Class<?>>)Class.class.getClass(), classWithAnnotationRecurser, Objects.requireNonNull(annotationType));
  }

  /**
   * Adds all interfaces extended by the specified {@code iface} interface
   * {@link Class}.
   *
   * @param iface The interface {@link Class}.
   * @param set The set into which all extended interfaces are to be added.
   * @throws NullPointerException If {@code iface} or {@code set} is null.
   */
  private static void recurse(final Class<?> iface, final HashSet<Class<?>> set) {
    if (set.contains(iface))
      return;

    set.add(iface);
    for (final Class<?> extended : iface.getInterfaces())
      recurse(extended, set);
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
   * @throws NullPointerException If {@code cls} is null.
   */
  public static Class<?>[] getAllInterfaces(final Class<?> cls) {
    Class<?> parent = cls;
    Class<?>[] ifaces = null;
    HashSet<Class<?>> set = null;
    do {
      ifaces = parent.getInterfaces();
      if (ifaces.length == 0)
        continue;

      if (set == null)
        set = new HashSet<>(4);

      for (final Class<?> iface : ifaces)
        recurse(iface, set);
    }
    while ((parent = parent.getSuperclass()) != null);
    return set == null ? ifaces : set.toArray(new Class[set.size()]);
  }

  /**
   * Returns the greatest common superclass of the specified array of classes.
   *
   * @param classes The array of classes.
   * @return The greatest common superclass of the specified array of classes.
   * @throws IllegalArgumentException If the number of arguments in the
   *           {@code classes} parameter is 0.
   * @throws NullPointerException If {@code classes}, or a member of
   *           {@code classes} is null.
   */
  public static Class<?> getGreatestCommonSuperclass(final Class<?> ... classes) {
    if (classes.length == 0)
      throw new IllegalArgumentException("Argument length must be greater than 0");

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
   * @throws IllegalArgumentException If the number of arguments in the
   *           {@code objects} parameter is 0.
   * @throws NullPointerException If {@code objects}, or a member of
   *           {@code objects} is null.
   */
  @SafeVarargs
  public static <T>Class<?> getGreatestCommonSuperclass(final T ... objects) {
    if (objects.length == 0)
      throw new IllegalArgumentException("Argument length must be greater than 0");

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
   * @throws IllegalArgumentException If the number of elements in the
   *           {@code objects} collection is 0.
   * @throws NullPointerException If {@code objects}, or an element of
   *           {@code objects} is null.
   */
  public static <T>Class<?> getGreatestCommonSuperclass(final Collection<T> objects) {
    if (objects.size() == 0)
      throw new IllegalArgumentException("Collection size must be greater than 0");

    return getGreatestCommonSuperclass0(objects.toArray());
  }

  private static Class<?> getGreatestCommonSuperclass(Class<?> c1, Class<?> c2) {
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

  private Classes() {
  }
}