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

package org.lib4j.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class Classes {
  private static final Map<Class<?>,Map<String,Field>> classToFields = new ConcurrentHashMap<Class<?>,Map<String,Field>>();

  public static String getDeclaringClassName(final String className) {
    if (!JavaIdentifiers.isValid(className))
      throw new IllegalArgumentException("Not a valid java identifier: " + className);

    int index = className.length() - 1;
    while ((index = className.lastIndexOf('$', index - 1)) > 1) {
      char ch = className.charAt(index - 1);
      if (ch != '.' && ch != '$')
        break;
    }

    return index <= 0 ? className : className.substring(0, index);
  }

  public static String getRootDeclaringClassName(final String className) {
    if (!JavaIdentifiers.isValid(className))
      throw new IllegalArgumentException("Not a valid java identifier: " + className);

    final int limit = className.length() - 1;
    int index = 0;
    while ((index = className.indexOf('$', index + 1)) > 1) {
      char ch = className.charAt(index - 1);
      if (index == limit)
        return className;

      if (ch != '.' && ch != '$')
        break;
    }

    return index == -1 ? className : className.substring(0, index);
  }

  public static String toCanonicalClassName(final String className) {
    if (!JavaIdentifiers.isValid(className))
      throw new IllegalArgumentException("Not a valid java identifier: " + className);

    final StringBuilder builder = new StringBuilder();
    builder.append(className.charAt(0));
    builder.append(className.charAt(1));
    char last = '\0';
    for (int i = 2; i < className.length() - 1; i++) {
      final char ch = className.charAt(i);
      builder.append(last != '.' && last != '$' && ch == '$' ? '.' : ch);
      last = ch;
    }

    builder.append(className.charAt(className.length() - 1));
    return builder.toString();
  }

  public static String getCompoundName(final Class<?> cls) {
    final String pkg = cls.getPackageName();
    return pkg.length() == 0 ? cls.getName() : cls.getName().substring(pkg.length() + 1);
  }

  public static String getCanonicalCompoundName(final Class<?> cls) {
    final String pkg = cls.getPackageName();
    return pkg.length() == 0 ? cls.getCanonicalName() : cls.getCanonicalName().substring(pkg.length() + 1);
  }

  public static Type[] getGenericSuperclasses(final Class<?> cls) {
    return cls.getGenericSuperclass() instanceof ParameterizedType ? ((ParameterizedType)cls.getGenericSuperclass()).getActualTypeArguments() : null;
  }

  private static Field checkAccessField(final Field field, final boolean declared) {
    return field != null && (declared || Modifier.isPublic(field.getModifiers())) ? field : null;
  }

  private static Field getField(final Class<?> cls, final String fieldName, final boolean declared) {
    Map<String,Field> fieldMap;
    if ((fieldMap = classToFields.get(cls)) != null)
      return checkAccessField(fieldMap.get(fieldName), declared);

    final Field[] fields = declared ? cls.getDeclaredFields() : cls.getFields();
    classToFields.put(cls, fieldMap = new HashMap<String,Field>());
    for (final Field field : fields)
      fieldMap.put(field.getName(), field);

    return checkAccessField(fieldMap.get(fieldName), declared);
  }

  public static Field getField(final Class<?> cls, final String fieldName) {
    return Classes.getField(cls, fieldName, false);
  }

  public static Field getDeclaredField(final Class<?> cls, final String fieldName) {
    return Classes.getField(cls, fieldName, true);
  }

  public static Field getFieldDeep(Class<?> clazz, final String fieldName) {
    Field field;
    do
      field = getField(clazz, fieldName, false);
    while (field == null && (clazz = clazz.getSuperclass()) != null);
    return field;
  }

  public static Field getDeclaredFieldDeep(Class<?> clazz, final String name) {
    Field field;
    do
      field = getField(clazz, name, true);
    while (field == null && (clazz = clazz.getSuperclass()) != null);
    return field;
  }

  public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?> ... parameterTypes) {
    try {
      return clazz.getConstructor(parameterTypes);
    }
    catch (final NoSuchMethodException e) {
      return null;
    }
  }

  public static Constructor<?> getDeclaredConstructor(final Class<?> clazz, final Class<?> ... parameterTypes) {
    try {
      return clazz.getDeclaredConstructor(parameterTypes);
    }
    catch (final NoSuchMethodException e) {
      return null;
    }
  }

  /**
   * Changes the annotation value for the given key of the given annotation to newValue and returns
   * the previous value.
   */
  @SuppressWarnings("unchecked")
  public static <T>T setAnnotationValue(final Annotation annotation, final String key, final T newValue) {
    final Object handler = Proxy.getInvocationHandler(annotation);
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

    if (newValue != null && oldValue.getClass() != newValue.getClass())
      throw new IllegalArgumentException(newValue.getClass().getName() + " is not of required type " + oldValue.getClass().getName());

    memberValues.put(key, newValue);
    return oldValue;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Annotation>T getDeclaredAnnotation(final Class<?> clazz, final Class<T> annotationType) {
    for (final Annotation annotation : clazz.getDeclaredAnnotations())
      if (annotation.annotationType() == annotationType)
        return (T)annotation;

    return null;
  }

  private static final Repeat.Recurser<Field,Class<?>> declaredFieldRecurser = new Repeat.Recurser<Field,Class<?>>() {
    @Override
    public boolean accept(final Field member, final Object ... args) {
      return true;
    }

    @Override
    public Field[] members(final Class<?> container) {
      return container.getDeclaredFields();
    }

    @Override
    public Class<?> next(final Class<?> container) {
      return container.getSuperclass();
    }
  };

  private static final Repeat.Recurser<Method,Class<?>> declaredMethodRecurser = new Repeat.Recurser<Method,Class<?>>() {
    @Override
    public boolean accept(final Method member, final Object ... args) {
      return true;
    }

    @Override
    public Method[] members(final Class<?> container) {
      return container.getDeclaredMethods();
    }

    @Override
    public Class<?> next(final Class<?> container) {
      return container.getSuperclass();
    }
  };

  private static final Repeat.Recurser<Method,Class<?>> declaredMethodWithAnnotationRecurser = new Repeat.Recurser<Method,Class<?>>() {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean accept(final Method member, final Object ... args) {
      return args[0] == null || member.getAnnotation((Class)args[0]) != null;
    }

    @Override
    public Method[] members(final Class<?> container) {
      return container.getDeclaredMethods();
    }

    @Override
    public Class<?> next(final Class<?> container) {
      return container.getSuperclass();
    }
  };

  private static final Repeat.Recurser<Field,Class<?>> fieldRecurser = new Repeat.Recurser<Field,Class<?>>() {
    @Override
    public boolean accept(final Field field, final Object ... args) {
      return Modifier.isPublic((field).getModifiers());
    }

    @Override
    public Field[] members(final Class<?> clazz) {
      return clazz.getDeclaredFields();
    }

    @Override
    public Class<?> next(final Class<?> clazz) {
      return clazz.getSuperclass();
    }
  };

  private static final Repeat.Filter<Field> declaredFieldWithAnnotationFilter = new Repeat.Filter<Field>() {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean accept(final Field member, final Object ... args) {
      return args[0] == null || member.getAnnotation((Class)args[0]) != null;
    }
  };

  private static final Repeat.Filter<Method> declaredMethodWithAnnotationFilter = new Repeat.Filter<Method>() {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean accept(final Method member, final Object ... args) {
      return args[0] == null || member.getAnnotation((Class)args[0]) != null;
    }
  };

  private static final Repeat.Filter<Class<?>> classWithAnnotationFilter = new Repeat.Filter<Class<?>>() {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean accept(final Class<?> member, final Object ... args) {
      return args[0] == null || member.getAnnotation((Class)args[0]) != null;
    }
  };

  private static final Repeat.Recurser<Class<?>,Class<?>> classWithAnnotationRecurser = new Repeat.Recurser<Class<?>,Class<?>>() {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean accept(final Class<?> member, final Object ... args) {
      return args[0] == null || member.getAnnotation((Class)args[0]) != null;
    }

    @Override
    public Class<?>[] members(final Class<?> container) {
      return container.getDeclaredClasses();
    }

    @Override
    public Class<?> next(final Class<?> container) {
      return container.getSuperclass();
    }
  };

  /**
   * Find declared Field(s) in the clazz that have an annotation annotationType, executing a comparator callback for content matching.
   *
   * The comparator compareTo method may return: 0 if there is a match, -1 if there if no match, and 1 if there is a match & to return Field result after this
   * match.
   *
   * @param clazz
   * @param annotationType
   * @param comparable
   * @return
   */
  public static <T extends Annotation>Field[] getDeclaredFieldsWithAnnotation(final Class<?> clazz, final Class<T> annotationType) {
    return Repeat.Recursive.<Field>ordered(clazz.getDeclaredFields(), Field.class, declaredFieldWithAnnotationFilter, annotationType);
  }

  public static <T extends Annotation>Field[] getDeclaredFieldsWithAnnotationDeep(final Class<?> clazz, final Class<T> annotationType) {
    return Repeat.Recursive.<Field,Class<?>>inverted(clazz, clazz.getDeclaredFields(), Field.class, declaredFieldRecurser, annotationType);
  }

  /**
   * Find declared Method(s) in the clazz, including methods inherited in all superclasses.
   *
   * The comparator compareTo method may return: 0 if there is a match, -1 if there if no match, and 1 if there is a match & to return Field result after this
   * match.
   *
   * @param clazz
   * @param annotationType
   * @param comparable
   * @return
   */
  public static <T extends Annotation>Method[] getDeclaredMethodsDeep(final Class<?> clazz) {
    return Repeat.Recursive.<Method,Class<?>>inverted(clazz, clazz.getDeclaredMethods(), Method.class, declaredMethodRecurser);
  }

  /**
   * Find declared Method(s) in the clazz that have an annotation annotationType, executing a comparator callback for content matching.
   *
   * The comparator compareTo method may return: 0 if there is a match, -1 if there if no match, and 1 if there is a match & to return Field result after this
   * match.
   *
   * @param clazz
   * @param annotationType
   * @param comparable
   * @return
   */
  public static <T extends Annotation>Method[] getDeclaredMethodsWithAnnotation(final Class<?> clazz, final Class<T> annotationType) {
    return Repeat.Recursive.<Method>ordered(clazz.getDeclaredMethods(), Method.class, declaredMethodWithAnnotationFilter, annotationType);
  }

  public static <T extends Annotation>Method[] getDeclaredMethodsWithAnnotationDeep(final Class<?> clazz, final Class<T> annotationType) {
    return Repeat.Recursive.<Method,Class<?>>inverted(clazz, clazz.getDeclaredMethods(), Method.class, declaredMethodWithAnnotationRecurser, annotationType);
  }

  /**
   * Find declared Class(es) in the clazz that have an annotation annotationType, executing a comparator callback for content matching.
   *
   * The comparator compareTo method may return: 0 if there is a match, -1 if there if no match, and 1 if there is a match & to return Class<?> result after this
   * match.
   *
   * @param clazz
   * @param annotationType
   * @param comparable
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T extends Annotation>Class<?>[] getDeclaredClassesWithAnnotation(final Class<?> clazz, final Class<T> annotationType) {
    return Repeat.Recursive.<Class<?>>ordered(clazz.getDeclaredClasses(), (Class<Class<?>>)Class.class.getClass(), classWithAnnotationFilter, annotationType);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Annotation>Class<?>[] getDeclaredClassesWithAnnotationDeep(Class<?> clazz, final Class<T> annotationType) {
    return Repeat.Recursive.<Class<?>,Class<?>>inverted(clazz, clazz.getDeclaredClasses(), (Class<Class<?>>)Class.class.getClass(), classWithAnnotationRecurser, annotationType);
  }

  public static Field[] getFieldsDeep(final Class<?> clazz) {
    return Repeat.Recursive.ordered(clazz, clazz.getDeclaredFields(), Field.class, fieldRecurser);
  }

  public static Field[] getDeclaredFieldsDeep(final Class<?> clazz) {
    return Repeat.Recursive.<Field,Class<?>>inverted(clazz, clazz.getDeclaredFields(), Field.class, declaredFieldRecurser);
  }

  public static Method getDeclaredMethod(final Class<?> clazz, final String name, final Class<?> ... parameters) {
    if (clazz == null)
      throw new NullPointerException("clazz == null");

    if (name == null)
      throw new NullPointerException("name == null");

    final Method[] methods = clazz.getDeclaredMethods();
    for (final Method method : methods)
      if (method.getName().equals(name) && java.util.Arrays.equals(method.getParameterTypes(), parameters))
        return method;

    return null;
  }

  public static Method getDeclaredMethodDeep(Class<?> clazz, final String name, final Class<?> ... parameters) {
    Method method;
    do
      method = getDeclaredMethod(clazz, name, parameters);
    while (method == null && (clazz = clazz.getSuperclass()) != null);
    return method;
  }

  public static Class<?> getGreatestCommonSuperclass(final Class<?> ... classes) {
    if (classes == null || classes.length == 0)
      return null;

    if (classes.length == 1)
      return classes[0];

    Class<?> gcc = getGreatestCommonSuperclass(classes[0], classes[1]);
    for (int i = 2; i < classes.length && gcc != null; i++)
      gcc = getGreatestCommonSuperclass(gcc, classes[i]);

    return gcc;
  }

  private static final Function<Object,Class<?>> objectClassFunction = o -> o.getClass();

  @SafeVarargs
  private static <T>Class<?> getGreatestCommonSuperclass0(final Function<T,Class<?>> function, final T ... objects) {
    if (objects == null || objects.length == 0)
      return null;

    if (objects.length == 1)
      return function.apply(objects[0]);

    Class<?> gcc = getGreatestCommonSuperclass(function.apply(objects[0]), function.apply(objects[1]));
    for (int i = 2; i < objects.length && gcc != null; i++)
      gcc = getGreatestCommonSuperclass(gcc, function.apply(objects[i]));

    return gcc;
  }

  @SafeVarargs
  public static <T>Class<?> getGreatestCommonSuperclass(final T ... objects) {
    return getGreatestCommonSuperclass0(objectClassFunction, objects);
  }

  public static <T>Class<?> getGreatestCommonSuperclass(final Collection<T> objects) {
    return getGreatestCommonSuperclass0(objectClassFunction, objects.toArray());
  }

  @SafeVarargs
  public static <T>Class<?> getGreatestCommonSuperclass(final Function<T,Class<?>> function, final T ... objects) {
    return getGreatestCommonSuperclass0(function, objects);
  }

  @SuppressWarnings("unchecked")
  public static <T>Class<?> getGreatestCommonSuperclass(final Function<T,Class<?>> function, final Collection<T> objects) {
    return getGreatestCommonSuperclass0((Function<Object,Class<?>>)function, objects.toArray());
  }

  public static Class<?> forName(final String className, final boolean initialize, final Class<?> callerClass) {
    if (className == null || className.length() == 0)
      return null;

    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    try {
      return Class.forName(className, initialize, classLoader);
    }
    catch (final ClassNotFoundException e) {
    }

    classLoader = Thread.currentThread().getContextClassLoader();
    try {
      return Class.forName(className, initialize, classLoader);
    }
    catch (final ClassNotFoundException e) {
    }

    classLoader = callerClass.getClassLoader();
    try {
      return Class.forName(className, initialize, classLoader);
    }
    catch (final ClassNotFoundException e) {
    }

    return null;
  }

  public static Class<?> forName(final String className, final Class<?> callerClass) {
    return Classes.forName(className, false, callerClass);
  }

  private static Class<?> getGreatestCommonSuperclass(Class<?> class1, Class<?> class2) {
    final Class<?> cls = class2;
    do {
      do
        if (class1.isAssignableFrom(class2))
          return class1;
      while ((class2 = class2.getSuperclass()) != null);
      class2 = cls;
    }
    while ((class1 = class1.getSuperclass()) != null);
    return null;
  }

  public static String getStrictName(final Class<?> cls) {
    if (cls.isArray())
      return getStrictName(cls.getComponentType()) + "[]";

    if (cls.isPrimitive()) {
      if (cls == int.class)
        return "int";

      if (cls == long.class)
        return "long";

      if (cls == double.class)
        return "double";

      if (cls == float.class)
        return "float";

      if (cls == boolean.class)
        return "boolean";

      if (cls == byte.class)
        return "byte";

      if (cls == short.class)
        return "short";

      if (cls == char.class)
        return "char";

      if (cls == void.class)
        return "void";

      throw new UnsupportedOperationException("Unknown primitive type: " + cls.getClass());
    }

    return recurseStrictName(cls).toString();
  }

  private static StringBuilder recurseStrictName(final Class<?> cls) {
    return cls.isMemberClass() ? recurseStrictName(cls.getEnclosingClass()).append('.').append(cls.getSimpleName()) : new StringBuilder(cls.getName());
  }

  private static class CallingClass extends SecurityManager {
    @Override
    public Class<?>[] getClassContext() {
      return super.getClassContext();
    }
  }

  public static Class<?>[] getCallingClasses() {
    return Arrays.subArray(new CallingClass().getClassContext(), 3);
  }

  public static <T>T newInstance(final Class<? extends T> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    }
    catch (final IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T>T newInstance(final Constructor<T> consctuctor, final Object ... initargs) {
    try {
      return consctuctor.newInstance(initargs);
    }
    catch (final IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private Classes() {
  }
}