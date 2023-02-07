/* Copyright (c) 2008 LibJ
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
import static org.libj.lang.Numbers.Unsigned.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;
import java.util.function.Function;

import org.junit.Test;

public class NumbersTest {
  private static final Random random = new Random();
  private static final float epsilon = 0.00000001f;

  public static class UnsignedTest {
    private static final int numTests = 1000000;

    @Test
    public void testToUINT8() {
      for (int i = 0; i < numTests; ++i) { // [N]
        final short signed = (short)(Math.abs((short)random.nextInt()) & 0xff);
        final byte unsigned = toUINT8(signed);
        assertEquals(signed, (short)Byte.toUnsignedInt(unsigned));
      }
    }

    @Test
    public void testToUINT16() {
      for (int i = 0; i < numTests; ++i) { // [N]
        final int signed = Math.abs(random.nextInt()) & 0xffff;
        final short unsigned = toUINT16(signed);
        assertEquals(signed, Short.toUnsignedInt(unsigned));
      }
    }

    @Test
    public void testToUINT32() {
      for (int i = 0; i < numTests; ++i) { // [N]
        final long signed = Math.abs(random.nextLong()) & 0xffffffffL;
        final int unsigned = toUINT32(signed);
        assertEquals(signed, Integer.toUnsignedLong(unsigned));
      }
    }

    @Test
    public void testToUINT64() {
      for (int i = 0; i < numTests; ++i) { // [N]
        final BigInteger signed = BigIntegers.valueOf(1, random.nextLong());
        final long unsigned = toUINT64(signed);
        assertEquals(signed, toUnsignedBigInteger(unsigned));
      }
    }

    @Test
    public void testToUINT() {
      for (int i = 0; i < numTests; ++i) { // [N]
        final BigInteger signed = new BigInteger(Strings.getRandomNumeric(Math.abs(random.nextInt()) % 100 + 1));
        final byte[] unsigned = toUINT(signed);
        assertEquals(signed, toUnsignedBigInteger(unsigned));
      }
    }
  }

  public static class CompositeTest {
    private static final Random random = new Random();

    private static byte randomByte() {
      return (byte)random.nextInt();
    }

    private static byte[] randomBytes(final int length) {
      final byte[] bytes = new byte[length];
      for (int i = 0, i$ = bytes.length; i < i$; ++i) // [A]
        bytes[i] = randomByte();

      return bytes;
    }

    private static short randomShort() {
      return (short)random.nextInt();
    }

    private static short[] randomShorts(final int length) {
      final short[] shorts = new short[length];
      for (int i = 0, i$ = shorts.length; i < i$; ++i) // [A]
        shorts[i] = randomShort();

      return shorts;
    }

    private static int randomInt() {
      return random.nextInt();
    }

    private static int[] randomInts(final int length) {
      final int[] ints = new int[length];
      for (int i = 0, i$ = ints.length; i < i$; ++i) // [A]
        ints[i] = randomInt();

      return ints;
    }

    private static float randomFloat() {
      return random.nextFloat();
    }

    private static float[] randomFloats(final int length) {
      final float[] floats = new float[length];
      for (int i = 0, i$ = floats.length; i < i$; ++i) // [A]
        floats[i] = randomFloat();

      return floats;
    }

    @Test
    public void testLongOfInts() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final int[] expected = randomInts(2);
        final long encoded = Numbers.Composite.encode(expected[0], expected[1]);
        for (int j = 0, j$ = expected.length; j < j$; ++j) // [A]
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Composite.decodeInt(encoded, j));
      }
    }

    @Test
    public void testLongOfShorts() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final short[] expected = randomShorts(4);
        final long encoded1 = Numbers.Composite.encode(expected[0], expected[1], expected[2], expected[3]);
        final long encoded2 = Numbers.Composite.encode(Numbers.Composite.encode(expected[0], expected[1]), Numbers.Composite.encode(expected[2], expected[3]));
        assertEquals(encoded1, encoded2);
        for (int j = 0, j$ = expected.length; j < j$; ++j) // [A]
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Composite.decodeShort(encoded1, j));
      }
    }

    @Test
    public void testLongOfBytes() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final byte[] expected = randomBytes(8);
        final long encoded1 = Numbers.Composite.encode(expected[0], expected[1], expected[2], expected[3], expected[4], expected[5], expected[6], expected[7]);
        final long encoded2 = Numbers.Composite.encode(Numbers.Composite.encode(expected[0], expected[1], expected[2], expected[3]), Numbers.Composite.encode(expected[4], expected[5], expected[6], expected[7]));
        final long encoded3 = Numbers.Composite.encode(Numbers.Composite.encode(expected[0], expected[1]), Numbers.Composite.encode(expected[2], expected[3]), Numbers.Composite.encode(expected[4], expected[5]), Numbers.Composite.encode(expected[6], expected[7]));
        assertEquals(encoded1, encoded2);
        assertEquals(encoded2, encoded3);
        for (int j = 0, j$ = expected.length; j < j$; ++j) // [A]
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Composite.decodeByte(encoded1, j));
      }
    }

    @Test
    public void testLongOfIntFloat() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final int expectedInt = randomInt();
        final float expectedFloat = randomFloat();
        final long encoded = Numbers.Composite.encode(expectedInt, expectedFloat);
        assertEquals("Index: 0, Value: " + expectedInt, expectedInt, Numbers.Composite.decodeInt(encoded, 0));
        assertEquals("Index: 1, Value: " + expectedFloat, expectedFloat, Numbers.Composite.decodeFloat(encoded, 1), epsilon);
      }
    }

    @Test
    public void testLongOfFloats() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final float[] expected = randomFloats(2);
        final long encoded = Numbers.Composite.encode(expected[0], expected[1]);
        for (int j = 0, j$ = expected.length; j < j$; ++j) // [A]
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Composite.decodeFloat(encoded, j), epsilon);
      }
    }

    @Test
    public void testLongOfFloatInt() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final float expectedFloat = randomFloat();
        final int expectedInt = randomInt();
        final long encoded = Numbers.Composite.encode(expectedFloat, expectedInt);
        assertEquals("Index: 0, Value: " + expectedFloat, expectedFloat, Numbers.Composite.decodeFloat(encoded, 0), epsilon);
        assertEquals("Index: 1, Value: " + expectedInt, expectedInt, Numbers.Composite.decodeInt(encoded, 1));
      }
    }

    @Test
    public void testIntOfShorts() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final short[] expected = randomShorts(2);
        final int encoded = Numbers.Composite.encode(expected[0], expected[1]);
        for (int j = 0, j$ = expected.length; j < j$; ++j) // [A]
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Composite.decodeShort(encoded, j));
      }
    }

    @Test
    public void testIntOfBytes() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final byte[] expected = randomBytes(4);
        final int encoded = Numbers.Composite.encode(expected[0], expected[1], expected[2], expected[3]);
        for (int j = 0, j$ = expected.length; j < j$; ++j) // [A]
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Composite.decodeByte(encoded, j));
      }
    }

    @Test
    public void testShortOfBytes() {
      for (int i = 0; i < 10000; ++i) { // [N]
        final byte[] expected = randomBytes(2);
        final short encoded = Numbers.Composite.encode(expected[0], expected[1]);
        for (int j = 0, j$ = expected.length; j < j$; ++j) // [A]
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Composite.decodeByte(encoded, j));
      }
    }
  }

  @Test
  public void testParseNumberByteMin() {
    final Byte v = Byte.MIN_VALUE;
    String s = v.toString();
    Number t = Numbers.parseNumber(s);
    assertEquals(v, t);
    s = s.substring(0, s.length() - 1) + '9';
    t = Numbers.parseNumber(s);
    assertEquals(Short.valueOf(s), t);
  }

  @Test
  public void testParseNumberByteMax() {
    final Byte v = Byte.MAX_VALUE;
    String s = v.toString();
    Number t = Numbers.parseNumber(s);
    assertEquals(v, t);
    s = s.substring(0, s.length() - 1) + '8';
    t = Numbers.parseNumber(s);
    assertEquals(Short.valueOf(s), t);
    s = "+" + s;
    t = Numbers.parseNumber(s);
    assertEquals(Short.valueOf(s), t);
  }

  @Test
  public void testParseNumberShortMin() {
    final Short v = Short.MIN_VALUE;
    String s = v.toString();
    Number t = Numbers.parseNumber(s);
    assertEquals(v, t);
    s = s.substring(0, s.length() - 1) + '9';
    t = Numbers.parseNumber(s);
    assertEquals(Integer.valueOf(s), t);
  }

  @Test
  public void testParseNumberShortMax() {
    final Short v = Short.MAX_VALUE;
    String s = v.toString();
    Number t = Numbers.parseNumber(s);
    assertEquals(v, t);
    s = s.substring(0, s.length() - 1) + '8';
    t = Numbers.parseNumber(s);
    assertEquals(Integer.valueOf(s), t);
    s = "+" + s;
    t = Numbers.parseNumber(s);
    assertEquals(Integer.valueOf(s), t);
  }

  @Test
  public void testParseNumberIntMin() {
    final Integer v = Integer.MIN_VALUE;
    String s = v.toString();
    Number t = Numbers.parseNumber(s);
    assertEquals(v, t);
    s = s.substring(0, s.length() - 1) + '9';
    t = Numbers.parseNumber(s);
    assertEquals(Long.valueOf(s), t);
  }

  @Test
  public void testParseNumberIntMax() {
    final Integer v = Integer.MAX_VALUE;
    String s = v.toString();
    Number t = Numbers.parseNumber(s);
    assertEquals(v, t);
    s = s.substring(0, s.length() - 1) + '8';
    t = Numbers.parseNumber(s);
    assertEquals(Long.valueOf(s), t);
    s = "+" + s;
    t = Numbers.parseNumber(s);
    assertEquals(Long.valueOf(s), t);
  }

  @Test
  public void testParseNumberLongMin() {
    final Long v = Long.MIN_VALUE;
    String s = v.toString();
    Number t = Numbers.parseNumber(s);
    assertEquals(v, t);
    s = s.substring(0, s.length() - 1) + '9';
    t = Numbers.parseNumber(s);
    assertEquals(new BigInteger(s), t);
  }

  @Test
  public void testParseNumberLongMax() {
    final Long v = Long.MAX_VALUE;
    String s = v.toString();
    Number t = Numbers.parseNumber(s);
    assertEquals(v, t);
    s = s.substring(0, s.length() - 1) + '8';
    t = Numbers.parseNumber(s);
    assertEquals(new BigInteger(s), t);
    s = "+" + s;
    t = Numbers.parseNumber(s);
    assertEquals(new BigInteger(s), t);
  }

  @Test
  public void testIsNumber() {
    assertTrue(Numbers.isNumber("0"));
    assertTrue(Numbers.isNumber(" 299792458"));
    assertTrue(Numbers.isNumber("-299792458 "));
    assertTrue(Numbers.isNumber(" 3.14159265"));
    assertTrue(Numbers.isNumber("-3.14159265"));
    assertTrue(Numbers.isNumber("6.022E23 "));
    assertTrue(Numbers.isNumber(" -6.022E23"));
    assertTrue(Numbers.isNumber(" 6.626068E-34"));
    assertTrue(Numbers.isNumber("-6.626068E-34 "));
    assertTrue(Numbers.isNumber("-6.626068E-34 24/49"));
    assertTrue(Numbers.isNumber("-6.6991202137503775E+18"));
    assertTrue(Numbers.isNumber("-6.6991202137503775E-18"));
    assertTrue(Numbers.isNumber("3/5"));

    assertFalse(Numbers.isNumber(null));
    assertFalse(Numbers.isNumber(""));
    assertFalse(Numbers.isNumber(" "));
    assertFalse(Numbers.isNumber("not number"));
    assertFalse(Numbers.isNumber("3.14.15"));
    assertFalse(Numbers.isNumber("29-97924"));
    assertFalse(Numbers.isNumber("-29-97924"));
    assertFalse(Numbers.isNumber("2 997924"));
    assertFalse(Numbers.isNumber("-29-979.24"));
    assertFalse(Numbers.isNumber("-2.9-979.24"));
    assertFalse(Numbers.isNumber("6.022 E23 "));
    assertFalse(Numbers.isNumber(" -6.022E 23"));
    assertFalse(Numbers.isNumber("-6.626068E--34 "));
    assertFalse(Numbers.isNumber("-6.626068E-3-4 "));
    assertFalse(Numbers.isNumber("-6.626068E-3.4"));
    assertFalse(Numbers.isNumber("-6.626E068E-34"));
  }

  @Test
  public void testToString() {
    assertEquals("0.00833333333333", Numbers.toString(0.008333333333330742, 14));
    assertEquals("0.00833333333334", Numbers.toString(0.008333333333339323, 14));
    assertEquals("0.008333333333", Numbers.toString(0.008333333333000000, 14));
  }

  @Test
  public void testPrecision() {
    assertEquals(3, Numbers.precision(349));
    assertEquals(1, Numbers.precision(3));
    assertEquals(5, Numbers.precision(34329));
    assertEquals(10, Numbers.precision(Integer.MIN_VALUE));
    assertEquals(1, Numbers.precision(-1));
    assertEquals(5, Numbers.precision(-13423));
    assertEquals(12, Numbers.precision(349349349349L));
    assertEquals(19, Numbers.precision(BigInteger.valueOf(4389429384493848239L)));
    assertEquals(19, Numbers.precision(BigInteger.valueOf(-4389429384493848239L)));
    assertEquals(19, Numbers.precision(Long.MIN_VALUE));
    assertEquals(19, Numbers.precision(new BigDecimal("-4389429384.493848239")));
  }

  private static final byte countTrailingZeroes(final String str) {
    byte count = 0;
    for (final int i$ = str.length(); count < i$ && str.charAt(i$ - 1 - count) == '0'; ++count); // [$]
    return count;
  }

  @Test
  public void testTrailingZeroesByte() {
    for (int i = 0; i < 100000; ++i) { // [N]
      final byte n = i == 0 ? 0 : (byte)random.nextInt();
      final String str = String.valueOf(n);
      assertEquals(str, countTrailingZeroes(str), Numbers.trailingZeroes(n));
    }
  }

  @Test
  public void testTrailingZeroesShort() {
    for (int i = 0; i < 100000; ++i) { // [N]
      final short n = i == 0 ? 0 : (short)random.nextInt();
      final String str = String.valueOf(n);
      assertEquals(str, countTrailingZeroes(str), Numbers.trailingZeroes(n));
    }
  }

  @Test
  public void testTrailingZeroesInt() {
    for (int i = 0; i < 100000; ++i) { // [N]
      final int n = i == 0 ? 0 : random.nextInt();
      final String str = String.valueOf(n);
      assertEquals(str, countTrailingZeroes(str), Numbers.trailingZeroes(n));
    }
  }

  @Test
  public void testTrailingZeroesLong() {
    for (int i = 0; i < 100000; ++i) { // [N]
      final long n = i == 0 ? 0 : random.nextLong();
      final String str = String.valueOf(n);
      assertEquals(str, countTrailingZeroes(str), Numbers.trailingZeroes(n));
    }
  }

  @Test
  public void testToBigDecimal() {
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal((short)1));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1L));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1f));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(1d));
    assertEquals(BigDecimal.ONE, Numbers.toBigDecimal(BigInteger.ONE));
  }

  @Test
  public void testStripTrailingZeros() {
    assertNull(Numbers.stripTrailingZeros(null));
    assertEquals("5.4", Numbers.stripTrailingZeros("5.4000"));
    assertEquals("500", Numbers.stripTrailingZeros("500"));
    assertEquals("0500", Numbers.stripTrailingZeros("0500"));
    assertEquals("0.1", Numbers.stripTrailingZeros("0.100"));
    assertEquals("0", Numbers.stripTrailingZeros("0.000"));
    assertEquals("1", Numbers.stripTrailingZeros("1.000"));
    assertEquals("xxx", Numbers.stripTrailingZeros("xxx"));
    assertEquals("xxx00", Numbers.stripTrailingZeros("xxx00"));
  }

  @Test
  public void testAverage() {
    assertEquals(BigDecimal.valueOf(3), Numbers.average(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO));
    assertEquals(BigDecimal.valueOf(3), Numbers.average(BigInteger.ONE, BigInteger.ONE, BigInteger.TEN, BigInteger.ZERO));
    assertEquals(3d, Numbers.average((byte)1, (byte)1, (byte)10, (byte)0), 0.000000001);
    assertEquals(3d, Numbers.average((short)1, (short)1, (short)10, (short)0), 0.000000001);
    assertEquals(3d, Numbers.average(1, 1, 10, 0), 0.000000001);
    assertEquals(3d, Numbers.average(1L, 1L, 10L, 0L), 0.000000001);
    assertEquals(3d, Numbers.average((byte)1, 1L, 10, 0d), 0.000000001);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Number>void testParse(final Function<? super String,T> function, final T ... numbers) {
    for (final T number : numbers) { // [A]
      final String str = String.valueOf(number);
      assertEquals(number, function.apply(str));
    }
  }

  @FunctionalInterface
  public interface ObjIntFunction<T,R> {
    R apply(T t, int i);
  }

  @SuppressWarnings("unchecked")
  private static <T extends Number>void testParseRadix(final ObjIntFunction<String,T> function, final int radix, final T ... numbers) {
    for (final T number : numbers) { // [A]
      final String str = Long.toString(number.longValue(), radix);
      assertEquals(number, function.apply(str, radix));
    }
  }

  @Test
  public void testParseInteger() {
    assertNull(Numbers.parseInteger(null));
    assertNull(Numbers.parseInteger(""));
    assertNull(Numbers.parseInteger("-"));
    assertNull(Numbers.parseInteger(":"));
    assertNull(Numbers.parseInteger("/"));
    assertNull(Numbers.parseInteger(":"));

    testParse(Numbers::parseInteger, -323, 3923, -7932, 38229, -732938, 83928384, -382983985);

    for (byte r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r) // [N]
      testParseRadix(Numbers::parseInteger, r, -323, 3923, -7932, 38229, -732938, 83928384, -382983985);

    for (int i = 0; i < 1000; ++i) { // [N]
      for (int r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r) { // [N]
        final int random = (int)(Integer.MIN_VALUE * Math.random() + Integer.MAX_VALUE * Math.random());
        assertEquals(random, Integer.parseInt(Integer.toString(random, r), r));
        assertEquals(random, (int)Numbers.parseInteger(Integer.toString(random, r), r));
      }
    }
  }

  @Test
  public void testParseLong() {
    assertNull(Numbers.parseLong(null));
    assertNull(Numbers.parseLong(""));
    assertNull(Numbers.parseLong("-"));
    assertNull(Numbers.parseLong(":"));
    assertNull(Numbers.parseLong("/"));
    assertNull(Numbers.parseLong(":"));

    testParse(Numbers::parseLong, 323L, -3923L, 7932L, -38229L, 732938L, -83928384L, 382983985L, -8434893285L, 38434893285L, -938434893285L, 1938434893285L, -21938434893285L, 921938434893285L, -9921938434893285L, 79921938434893285L, -279921938434893285L, 8279921938434893285L);

    for (byte r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r) // [N]
      testParseRadix(Numbers::parseLong, r, 323L, -3923L, 7932L, -38229L, 732938L, -83928384L, 382983985L, -8434893285L, 38434893285L, -938434893285L, 1938434893285L, -21938434893285L, 921938434893285L, -9921938434893285L, 79921938434893285L, -279921938434893285L, 8279921938434893285L);

    for (int i = 0; i < 1000; ++i) { // [N]
      for (int r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r) { // [N]
        final long random = (long)(Long.MIN_VALUE * Math.random() + Long.MAX_VALUE * Math.random());
        assertEquals(random, Long.parseLong(Long.toString(random, r), r));
        assertEquals(random, (long)Numbers.parseLong(Long.toString(random, r), r));
      }
    }
  }

  private static void testIsDigit(final int radix) {
    for (int r = 0; r < radix; ++r) { // [N]
      if (r < 10) {
        assertTrue(String.valueOf(r), Numbers.isDigit((char)('0' + r), radix));
      }
      else {
        assertTrue(String.valueOf(r), Numbers.isDigit((char)('a' + r - 10), radix));
        assertTrue(String.valueOf(r), Numbers.isDigit((char)('A' + r - 10), radix));
      }
    }

    assertFalse("-1", Numbers.isDigit((char)('0' - 1), radix));
    if (radix <= 10) {
      assertFalse(String.valueOf(radix), Numbers.isDigit((char)('0' + radix), radix));
    }
    else {
      assertFalse(String.valueOf(radix), Numbers.isDigit((char)('a' + radix - 10), radix));
      // NOTE: Can only check a max of radix=31, because at 32, the ASCII table
      // NOTE: crosses form lower-case latin characters to upper case
      if (radix < 32)
        assertFalse(String.valueOf(radix), Numbers.isDigit((char)('A' + radix - 10), radix));
    }
  }

  @Test
  public void testIsDigit() {
    for (int r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r) // [N]
      testIsDigit(r);
  }

  private static void testPrecisionByte(final byte n) {
    final String str = String.valueOf(n);
    assertEquals(str, n < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testPrecisionByte() {
    testPrecisionByte(Byte.MIN_VALUE);
    testPrecisionByte(Byte.MAX_VALUE);
    for (int i = 0; i < 1000000; ++i) { // [N]
      final byte n = (byte)random.nextInt();
      testPrecisionByte(n);
    }
  }

  private static void testPrecisionShort(final short n) {
    final String str = String.valueOf(n);
    assertEquals(str, n < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testPrecisionShort() {
    testPrecisionShort(Short.MIN_VALUE);
    testPrecisionShort(Short.MAX_VALUE);
    for (int i = 0; i < 1000000; ++i) { // [N]
      final short n = (short)random.nextInt();
      testPrecisionShort(n);
    }
  }

  private static void testPrecisionInt(final int n) {
    final String str = String.valueOf(n);
    assertEquals(str, n < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testPrecisionInt() {
    testPrecisionInt(Integer.MIN_VALUE);
    testPrecisionInt(Integer.MAX_VALUE);
    for (int i = 0; i < 1000000; ++i) { // [N]
      final int n = random.nextInt();
      testPrecisionInt(n);
    }
  }

  private static void testPrecisionLong(final long n) {
    final String str = String.valueOf(n);
    assertEquals(str, n < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testPrecisionLong() {
    testPrecisionLong(Long.MIN_VALUE);
    testPrecisionLong(Long.MAX_VALUE);
    for (int i = 0; i < 1000000; ++i) { // [N]
      final long n = random.nextLong();
      testPrecisionLong(n);
    }
  }

  private static void testPrecisionBigInteger(final BigInteger n) {
    final String str = String.valueOf(n);
    assertEquals(str, n.signum() < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testPrecisionBigInteger() {
    for (int i = 0; i < 1000000; ++i) { // [N]
      final BigInteger n = BigInteger.valueOf(random.nextLong());
      testPrecisionBigInteger(n);
    }
  }

  private static void testPrecisionBigDecimal(final BigDecimal n) {
    final String str = String.valueOf(n.stripTrailingZeros());
    int e = str.indexOf('e');
    if (e < 0)
      e = str.indexOf('E');

    if (e < 0)
      e = str.length();

    int i$ = n.signum() < 0 ? e - 1 : e;
    if (n.scale() > 0)
      --i$;

    for (int i = n.signum() < 0 ? 1 : 0; i$ >= 0; ++i) { // [N]
      final char ch = str.charAt(i);
      if (ch == '0') {
        --i$;
        continue;
      }

      if (ch == '.')
        continue;

      break;
    }

    assertEquals(str, i$, Numbers.precision(n));
  }

  @Test
  public void testPrecisionBigDecimal() {
    for (int i = 0; i < 1000000; ++i) { // [N]
      final BigDecimal n = BigDecimal.valueOf(random.nextDouble());
      testPrecisionBigDecimal(n);
    }
  }

  @Test
  public void testCast() {
    for (int i = 0; i < 1000000; ++i) { // [N]
      final long n = random.nextLong();
      assertEquals(Byte.valueOf((byte)(n % 0xFF - 0xFF >> 1)), Numbers.cast((byte)(n % 0xFF - 0xFF >> 1), Byte.class));
      assertEquals(Short.valueOf((short)(n % 0xFFFF - 0xFFFF >> 1)), Numbers.cast((short)(n % 0xFFFF - 0xFFFF >> 1), Short.class));
      assertEquals(Integer.valueOf((int)(n % 0xFFFFFFFF - 0xFFFFFFFF >> 1)), Numbers.cast((int)(n % 0xFFFFFFFF - 0xFFFFFFFF >> 1), Integer.class));
      assertEquals(Long.valueOf(n), Numbers.cast(n, Long.class));
      assertEquals(BigInteger.valueOf(n), Numbers.cast(n, BigInteger.class));
      assertEquals(BigDecimal.valueOf(n), Numbers.cast(n, BigDecimal.class));
    }
  }

  @Test
  public void testSignumByte() {
    byte j;
    for (int i = 9; i < 1000000; ++i) { // [N]
      j = (byte)random.nextInt();
      assertEquals(j < 0 ? -1 : j == 0 ? 0 : 1, Numbers.signum(j));
    }
  }

  @Test
  public void testSignumShort() {
    short j;
    for (int i = 9; i < 1000000; ++i) { // [N]
      j = (short)random.nextInt();
      assertEquals(j < 0 ? -1 : j == 0 ? 0 : 1, Numbers.signum(j));
    }
  }

  @Test
  public void testSignumInt() {
    for (int i = 9, j; i < 1000000; ++i) { // [N]
      j = random.nextInt();
      assertEquals(j < 0 ? -1 : j == 0 ? 0 : 1, Numbers.signum(j));
    }
  }

  @Test
  public void testSignumLong() {
    for (long i = 9, j; i < 1000000; ++i) { // [N]
      j = random.nextLong();
      assertEquals(j < 0 ? -1 : j == 0 ? 0 : 1, Numbers.signum(j));
    }
  }

  @Test
  public void testSignumFloat() {
    float j;
    for (int i = 9; i < 1000000; ++i) { // [N]
      j = random.nextFloat();
      assertEquals(j < 0 ? -1 : j == 0 ? 0 : 1, Numbers.signum(j));
    }
  }

  @Test
  public void testSignumDouble() {
    double j;
    for (int i = 9; i < 1000000; ++i) { // [N]
      j = random.nextDouble();
      assertEquals(j < 0 ? -1 : j == 0 ? 0 : 1, Numbers.signum(j));
    }
  }

  private static Number makeNumber(final int m, final int signum) {
    if (m == 0)
      return signum == 0 ? 0 : signum * (random.nextInt(Integer.MAX_VALUE - 1) + 1);
    else if (m == 1)
      return signum == 0 ? 0L : signum * (long)(random.nextInt(Integer.MAX_VALUE - 1) + 1);
    else if (m == 2)
      return signum == 0 ? BigInteger.ZERO : BigInteger.valueOf(signum * (random.nextInt(Integer.MAX_VALUE - 1) + 1));
    else if (m == 3)
      return signum == 0 ? BigDecimal.ZERO : BigDecimal.valueOf((random.nextDouble() + 1) * signum);
    else if (m == 4)
      return signum == 0 ? 0d : (random.nextDouble() + 1) * signum;
    else if (m == 5)
      return signum == 0 ? 0f : (random.nextFloat() + 1) * signum;
    else if (m == 6)
      return signum == 0 ? (short)0 : (short)(signum * (random.nextInt(Short.MAX_VALUE - 1) + 1));
    else if (m == 7)
      return signum == 0 ? (byte)0 : (byte)(signum * (random.nextInt(Byte.MAX_VALUE - 1) + 1));
    else
      throw new IllegalStateException();
  }

  @Test
  public void testSignumNumber() {
    for (int i = 0; i < 1000000; ++i) { // [N]
      final int m = i % 8;
      Number n = makeNumber(m, 1);
      assertEquals(n.getClass().getSimpleName() + " " + n.toString(), 1, Numbers.signum(n));
      n = makeNumber(m, -1);
      assertEquals(n.getClass().getSimpleName() + " " + n.toString(), -1, Numbers.signum(n));
      n = makeNumber(m, 0);
      assertEquals(n.getClass().getSimpleName() + " " + n.toString(), 0, Numbers.signum(n));
    }
  }

  @Test
  public void testIsPowerOf2Int() {
    final double log2 = Math.log(2);
    for (int i = 0, x; i < 1000000; ++i) { // [N]
      x = random.nextInt();
      final boolean expected = Math.ceil(Math.log(x) / log2) == Math.floor(Math.log(x) / log2);
      final boolean actual = Numbers.isPowerOf2(x);
      assertEquals("" + x, expected, actual);
    }
  }

  @Test
  public void testIsPowerOf2Long() {
    final double log2 = Math.log(2);
    for (long i = 0, x; i < 1000000; ++i) { // [N]
      x = random.nextLong();
      final boolean expected = Math.ceil(Math.log(x) / log2) == Math.floor(Math.log(x) / log2);
      final boolean actual = Numbers.isPowerOf2(x);
      assertEquals("" + x, expected, actual);
    }
  }
}