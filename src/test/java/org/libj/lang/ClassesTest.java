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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"hiding", "unused"})
public class ClassesTest {
  private static final Logger logger = LoggerFactory.getLogger(ClassesTest.class);

  private final Map<Class<?>[],Class<?>> classes = new HashMap<>();

  @Before
  public void setUp() {
    classes.put(new Class[] {String.class}, String.class);
    classes.put(new Class[] {String.class, Integer.class}, Object.class);
    classes.put(new Class[] {Long.class, Integer.class}, Number.class);
    classes.put(new Class[] {ArrayList.class, LinkedList.class}, AbstractList.class);
    classes.put(new Class[] {HashSet.class, LinkedHashSet.class}, HashSet.class);
    classes.put(new Class[] {FileInputStream.class, ByteArrayInputStream.class, DataInputStream.class, FilterInputStream.class}, InputStream.class);
  }

  @Test
  public void testGreatestCommonClass() throws Exception {
    try {
      Classes.getGreatestCommonSuperclass((Class<?>[])null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    if (classes.size() > 0)
      for (final Map.Entry<Class<?>[],Class<?>> entry : classes.entrySet()) // [S]
        assertSame(entry.getValue(), Classes.getGreatestCommonSuperclass(entry.getKey()));
  }

  protected static class Inn$r {
    protected static class $nner {
      protected static class $nner$ {
      }
    }
  }

  @Test
  public void testGetDeclaringClassName() {
    try {
      Classes.getDeclaringClassName(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Classes.getDeclaringClassName("");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("One", Classes.getDeclaringClassName("One$Two"));
    assertEquals("One", Classes.getDeclaringClassName("One$Two$"));
    assertEquals("$Two", Classes.getDeclaringClassName("$Two"));
    assertEquals("Two$", Classes.getDeclaringClassName("Two$"));
    assertEquals("foo.One", Classes.getDeclaringClassName("foo.One$Two"));
    assertEquals("foo.One", Classes.getDeclaringClassName("foo.One$Two$"));
    assertEquals("foo.bar.One", Classes.getDeclaringClassName("foo.bar.One$Two"));
    assertEquals("foo.bar.One", Classes.getDeclaringClassName("foo.bar.One$Two$"));
    assertEquals("foo.bar.One$Two", Classes.getDeclaringClassName("foo.bar.One$Two$Three"));
    assertEquals("foo.bar.One$Two", Classes.getDeclaringClassName("foo.bar.One$Two$Three$"));

    assertEquals("foo.bar.One.$Two", Classes.getDeclaringClassName("foo.bar.One.$Two"));
    assertEquals("foo.bar.One.$Two$", Classes.getDeclaringClassName("foo.bar.One.$Two$"));
    assertEquals("foo.bar.One.$Two", Classes.getDeclaringClassName("foo.bar.One.$Two$$Three"));
    assertEquals("foo.bar.One.$Two", Classes.getDeclaringClassName("foo.bar.One.$Two$$Three$"));
    // FIXME: This is a problem with Java's inner class naming spec...
    assertEquals("foo.bar.One.$Two$$Three", Classes.getDeclaringClassName("foo.bar.One.$Two$$Three$$$Four"));
    assertEquals("foo.bar.One.$Two$$Three", Classes.getDeclaringClassName("foo.bar.One.$Two$$Three$$$Four$"));
    assertEquals("foo.bar.One.$Two.$$Three", Classes.getDeclaringClassName("foo.bar.One.$Two.$$Three"));
    assertEquals("foo.bar.One.$Two.$$Three$", Classes.getDeclaringClassName("foo.bar.One.$Two.$$Three$"));
  }

  @Test
  public void testGetRootDeclaringClassName() {
    try {
      Classes.getRootDeclaringClassName(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Classes.getRootDeclaringClassName("");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("One", Classes.getRootDeclaringClassName("One$Two"));
    assertEquals("One", Classes.getRootDeclaringClassName("One$Two$"));
    assertEquals("$Two", Classes.getRootDeclaringClassName("$Two"));
    assertEquals("Two$", Classes.getRootDeclaringClassName("Two$"));
    assertEquals("foo.One", Classes.getRootDeclaringClassName("foo.One$Two"));
    assertEquals("foo.One", Classes.getRootDeclaringClassName("foo.One$Two$"));
    assertEquals("foo.bar.One", Classes.getRootDeclaringClassName("foo.bar.One$Two"));
    assertEquals("foo.bar.One", Classes.getRootDeclaringClassName("foo.bar.One$Two$"));
    assertEquals("foo.bar.One", Classes.getRootDeclaringClassName("foo.bar.One$Two$Three"));
    assertEquals("foo.bar.One", Classes.getRootDeclaringClassName("foo.bar.One$Two$Three$"));

    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two"));
    assertEquals("foo.bar.One.$Two$", Classes.getRootDeclaringClassName("foo.bar.One.$Two$"));
    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two$$Three"));
    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two$$Three$"));
    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two$$Three$$$Four"));
    assertEquals("foo.bar.One.$Two", Classes.getRootDeclaringClassName("foo.bar.One.$Two$$Three$$$Four$"));
    assertEquals("foo.bar.One.$Two.$$Three", Classes.getRootDeclaringClassName("foo.bar.One.$Two.$$Three"));
    assertEquals("foo.bar.One.$Two.$$Three$", Classes.getRootDeclaringClassName("foo.bar.One.$Two.$$Three$"));
  }

  @Test
  public void testToCanonicalClassName() {
    try {
      Classes.toCanonicalClassName(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Classes.toCanonicalClassName("");
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("Id", Classes.toCanonicalClassName("Id"));
    assertEquals("Ids", Classes.toCanonicalClassName("Ids"));
    assertEquals("$Two", Classes.toCanonicalClassName("$Two"));
    assertEquals("Two$", Classes.toCanonicalClassName("Two$"));
    assertEquals("One.Two", Classes.toCanonicalClassName("One$Two"));
    assertEquals("One.Two$", Classes.toCanonicalClassName("One$Two$"));
    assertEquals("foo.One.Two", Classes.toCanonicalClassName("foo.One$Two"));
    assertEquals("foo.One.Two$", Classes.toCanonicalClassName("foo.One$Two$"));
    assertEquals("foo.bar.One.Two", Classes.toCanonicalClassName("foo.bar.One$Two"));
    assertEquals("foo.bar.One.Two$", Classes.toCanonicalClassName("foo.bar.One$Two$"));

    assertEquals("foo.bar.One.$Two", Classes.toCanonicalClassName("foo.bar.One.$Two"));
    assertEquals("foo.bar.One.$Two$", Classes.toCanonicalClassName("foo.bar.One.$Two$"));
    // FIXME: This is a problem with Java's inner class naming spec...
    assertEquals("foo.bar.One.$Two.$Three", Classes.toCanonicalClassName("foo.bar.One.$Two$$Three"));
    assertEquals("foo.bar.One.$Two.$Three$", Classes.toCanonicalClassName("foo.bar.One.$Two$$Three$"));
    assertEquals("foo.bar.One.$Two.$$Three", Classes.toCanonicalClassName("foo.bar.One.$Two.$$Three"));
    assertEquals("foo.bar.One.$Two.$$Three$", Classes.toCanonicalClassName("foo.bar.One.$Two.$$Three$"));
  }

  @Test
  public void testGetCompositeName() {
    try {
      Classes.getCompositeName(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("Map$Entry", Classes.getCompositeName(Map.Entry.class));
  }

  @Test
  public void testGetCanonicalCompositeName() {
    try {
      Classes.getCompositeName(null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals("Map.Entry", Classes.getCanonicalCompositeName(Map.Entry.class));
  }

  @SuppressWarnings("rawtypes")
  private static class GetGenericTypesTest {
    private String nonGeneric;
    private Optional rawGeneric;
    private Optional<?> wildGeneric;
    private Optional<String> stringGeneric;
    private Map<List<Integer>,Map<List<Integer>,String>> multiGeneric;
  }

  private static interface V<V> {
  }

  private static interface W<T,U> {
  }

  private static interface X<T,U> extends W<Long,T>, V<Short> {
  }

  private static class Y<U,R> implements X<Integer,U> {
  }

  private static class Z<R> extends Y<Double,R> implements V<Short> {
  }

  private static class ZZ<X> extends Z<Byte> implements V<Short> {
  }

  @Test
  // FIXME: Improve this test
  public void testGetAllGenericInterfaces() {
    final Type[] interfaces = Classes.getAllGenericInterfaces(Z.class);
    assertEquals(3, interfaces.length);
//    assertEquals("[org.libj.lang.ClassesTest$X<java.lang.Integer, U>, org.libj.lang.ClassesTest$W<T>]", Arrays.toString(interfaces));
  }

  @Test
  public void testResolveGenericTypes() {
    final Map<Class<?>,Type[]> classToTypes = new HashMap<>();
    Classes.resolveGenericTypes(ZZ.class, classToTypes::put);
    assertEquals(5, classToTypes.size());
    assertArrayEquals(new Class[] {Byte.class}, classToTypes.get(Z.class));
    assertArrayEquals(new Class[] {Double.class, Byte.class}, classToTypes.get(Y.class));
    assertArrayEquals(new Class[] {Integer.class, Double.class}, classToTypes.get(X.class));
    assertArrayEquals(new Class[] {Long.class, Integer.class}, classToTypes.get(W.class));
    assertArrayEquals(new Class[] {Short.class}, classToTypes.get(V.class));
  }

  @Test
  public void testGetGenericSuperclassTypeArguments() {
    try {
      Classes.getGenericSuperclassTypeArguments(Integer.class, null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Classes.getGenericSuperclassTypeArguments(null, Serializable.class);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Classes.getGenericSuperclassTypeArguments(Integer.class, Serializable.class);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals(0, Classes.getGenericSuperclassTypeArguments(Integer.class, Number.class).length);
    assertEquals(Double.class, Classes.getGenericSuperclassTypeArguments(Z.class, Y.class)[0]);
    assertEquals(Double.class, Classes.getGenericSuperclassTypeArguments(ZZ.class, Y.class)[0]);
    assertArrayEquals(new Class[] {Byte.class}, Classes.getGenericSuperclassTypeArguments(ZZ.class, Z.class));
  }

  @Test
  public void testGetGenericInterfaceTypeArguments() {
    try {
      Classes.getGenericInterfaceTypeArguments(Integer.class, null);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Classes.getGenericInterfaceTypeArguments(null, Serializable.class);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    try {
      Classes.getGenericInterfaceTypeArguments(Integer.class, Integer.class);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertEquals(0, Classes.getGenericInterfaceTypeArguments(Integer.class, Serializable.class).length);
    assertArrayEquals(new Class[] {Integer.class, Double.class}, Classes.getGenericInterfaceTypeArguments(Z.class, X.class));
  }

  @Test
  public void testGetGenericTypes() throws NoSuchFieldException {
    assertArrayEquals(new Class[0], Classes.getGenericParameters(GetGenericTypesTest.class.getDeclaredField("nonGeneric")));
    assertArrayEquals(new Class[0], Classes.getGenericParameters(GetGenericTypesTest.class.getDeclaredField("rawGeneric")));
    assertArrayEquals(new Class[] {Object.class}, Classes.getGenericParameters(GetGenericTypesTest.class.getDeclaredField("wildGeneric")));
    assertArrayEquals(new Class[] {String.class}, Classes.getGenericParameters(GetGenericTypesTest.class.getDeclaredField("stringGeneric")));
    assertArrayEquals(new Class[] {List.class, Map.class}, Classes.getGenericParameters(GetGenericTypesTest.class.getDeclaredField("multiGeneric")));
  }

  @Test
  public void testGetClassHierarchy() {
    try {
      Classes.getClassHierarchy(null, c -> false);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    final Class<?>[] hierarchy = {LinkedList.class, AbstractSequentialList.class, List.class, Deque.class, Cloneable.class, Serializable.class, AbstractList.class, Collection.class, Queue.class, AbstractCollection.class, Iterable.class, Object.class};

    assertEquals(asCollection(new LinkedHashSet<>(), hierarchy, 0, hierarchy.length), Classes.getClassHierarchy(LinkedList.class, null));
    assertEquals(asCollection(new LinkedHashSet<>(), hierarchy, 0, hierarchy.length), Classes.getClassHierarchy(LinkedList.class, c -> true));
    assertEquals(asCollection(new LinkedHashSet<>(), hierarchy, 0, 6), Classes.getClassHierarchy(LinkedList.class, c -> c != Serializable.class));
    assertEquals(asCollection(new LinkedHashSet<>(), hierarchy, 0, 9), Classes.getClassHierarchy(LinkedList.class, c -> c != Queue.class));
  }

  @Test
  public void testWalkClassHierarchy() {
    try {
      Classes.walkClassHierarchy(null, c -> false);
      fail("Expected IllegalArgumentException");
    }
    catch (final IllegalArgumentException e) {
    }

    assertNull(Classes.walkClassHierarchy(LinkedList.class, null));
    assertNull(Classes.walkClassHierarchy(LinkedList.class, c -> c == Integer.class ? "found" : null));
    assertEquals("found", Classes.walkClassHierarchy(LinkedList.class, c -> c == Queue.class ? "found" : null));
    assertEquals("found", Classes.walkClassHierarchy(LinkedList.class, c -> c == Serializable.class ? "found" : null));
  }

  private static <T>Collection<T> asCollection(final Collection<T> c, final T[] a, final int fromIndex, final int toIndex) {
    for (int i = fromIndex; i < toIndex; ++i) // [N]
      c.add(a[i]);

    return c;
  }

  private static final Comparator<Class<?>> comparator = new Comparator<Class<?>>() {
    @Override
    public int compare(final Class<?> o1, final Class<?> o2) {
      return o1.getName().compareTo(o2.getName());
    }
  };

  protected static void testGetAllInterfaces(final Object obj, final Class<?> ... expecteds) {
    final Class<?>[] ifaces = Classes.getAllInterfaces(obj.getClass());
    Arrays.sort(ifaces, comparator);
    Arrays.sort(expecteds, comparator);
    assertEquals(Arrays.toString(ifaces), Arrays.toString(expecteds));
    final List<Class<?>> list = Arrays.asList(ifaces);
    for (final Class<?> expected : expecteds) // [A]
      assertTrue(list.contains(expected));
  }

  public interface A {
    void a();
  }

  public interface B extends A {
    void b();
  }

  public interface C extends B {
  }

  public interface D {
  }

  public static class Bar implements C, D {
    @Override
    public void b() {
    }

    @Override
    public void a() {
    }

    public void withIgnore() {
    }

    public void withoutIgnore() {
    }
  }

  public static class Foo extends Bar implements B {
    @Ignore
    @Override
    public void withIgnore() {
    }
  }

  public static class Kludge extends Foo implements B {
    @Ignore
    @Override
    public void withoutIgnore() {
      super.withoutIgnore();
    }
  }

  public static final Foo foo = new Foo();

  public static final Bar bar = new Bar();

  public static final A a = new A() {
    @Override
    public void a() {
    }
  };

  public static final B b = new B() {
    @Override
    public void a() {
    }

    @Override
    public void b() {
    }
  };

  public static final C c = new C() {
    @Override
    public void a() {
    }

    @Override
    public void b() {
    }
  };

  public static final D d = new D() {};

  @Test
  public void testA() {
    testGetAllInterfaces(new A() {
      @Override
      public void a() {
      }
    }, A.class);
  }

  @Test
  public void testB() {
    testGetAllInterfaces(b, A.class, B.class);
  }

  @Test
  public void testC() {
    testGetAllInterfaces(c, A.class, B.class, C.class);
  }

  @Test
  public void testD() {
    testGetAllInterfaces(d, D.class);
  }

  @Test
  public void testFoo() {
    testGetAllInterfaces(foo, A.class, B.class, C.class, D.class);
  }

  @Test
  public void testBar() {
    testGetAllInterfaces(bar, A.class, B.class, C.class, D.class);
  }

  @Test
  public void testGetDeclaredMethod() {
    assertNotNull(Classes.getDeclaredMethod(ClassesTest.class, "testGetDeclaredMethod"));
    assertNotNull(Classes.getDeclaredMethod(ClassesTest.class, "testGetAllInterfaces", Object.class, Class[].class));
    assertNull(Classes.getDeclaredMethod(ClassesTest.class, "foo"));
  }

  @Test
  public void testGetDeclaredMethodDeep() {
    assertNull(Classes.getDeclaredMethod(Foo.class, "b"));
    assertNotNull(Classes.getDeclaredMethodDeep(Foo.class, "b"));
  }

  @Test
  public void testIsAssignableFrom() {
    assertTrue(Classes.isAssignableFrom(byte.class, Byte.class));
    assertTrue(Classes.isAssignableFrom(long.class, Long.class));
    assertTrue(Classes.isAssignableFrom(Number.class, long.class));
    assertTrue(Classes.isAssignableFrom(Object.class, long.class));
    assertFalse(Classes.isAssignableFrom(Object[].class, long[].class));
    assertFalse(Classes.isAssignableFrom(byte.class, Long.class));
  }

  @Test
  public void testSortDeclarativeOrder() {
    // FIXME: Implement more tests
    final Method[] methods = BootProxyClassLoader.class.getDeclaredMethods();
    Classes.sortDeclarativeOrder(methods);
    final String str = Arrays.toString(methods);
    assertTrue(str, str.startsWith("[public java.lang.Class org.libj.lang.BootProxyClassLoader.findClass(java.lang.String) throws java.lang.ClassNotFoundException, public java.lang.Class org.libj.lang.BootProxyClassLoader.loadClass(java.lang.String,boolean) throws java.lang.ClassNotFoundException, public java.lang.Class org.libj.lang.BootProxyClassLoader.loadClassOrNull(java.lang.String), public java.lang.Class org.libj.lang.BootProxyClassLoader.loadClassOrNull(java.lang.String,boolean)"));
  }

  @Test
  public void testWithIgnore() throws NoSuchMethodException {
    Method withIgnoreMethod = Kludge.class.getMethod("withIgnore");
    assertNotNull(Classes.getAnnotationDeep(withIgnoreMethod, Ignore.class));
    assertTrue(Classes.isAnnotationPresentDeep(withIgnoreMethod, Ignore.class));

    withIgnoreMethod = Foo.class.getMethod("withIgnore");
    assertNotNull(Classes.getAnnotationDeep(withIgnoreMethod, Ignore.class));
    assertTrue(Classes.isAnnotationPresentDeep(withIgnoreMethod, Ignore.class));

    withIgnoreMethod = Bar.class.getMethod("withIgnore");
    assertNull(Classes.getAnnotationDeep(withIgnoreMethod, Ignore.class));
    assertFalse(Classes.isAnnotationPresentDeep(withIgnoreMethod, Ignore.class));
  }

  @Test
  public void testWithoutIgnore() throws NoSuchMethodException {
    Method withIgnoreMethod = Kludge.class.getMethod("withoutIgnore");
    assertNotNull(Classes.getAnnotationDeep(withIgnoreMethod, Ignore.class));
    assertTrue(Classes.isAnnotationPresentDeep(withIgnoreMethod, Ignore.class));

    withIgnoreMethod = Foo.class.getMethod("withoutIgnore");
    assertNull(Classes.getAnnotationDeep(withIgnoreMethod, Ignore.class));
    assertFalse(Classes.isAnnotationPresentDeep(withIgnoreMethod, Ignore.class));

    withIgnoreMethod = Bar.class.getMethod("withoutIgnore");
    assertNull(Classes.getAnnotationDeep(withIgnoreMethod, Ignore.class));
    assertFalse(Classes.isAnnotationPresentDeep(withIgnoreMethod, Ignore.class));
  }
}