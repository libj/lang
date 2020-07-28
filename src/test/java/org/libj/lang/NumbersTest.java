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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;

public class NumbersTest {
  private static final Random random = new Random();
  private static final float epsilon = 0.00000001f;

  public static class CompoundTest {
    private static final Random random = new Random();

    private static byte randomByte() {
      return (byte)random.nextInt();
    }

    private static byte[] randomBytes(final int length) {
      final byte[] bytes = new byte[length];
      for (int i = 0; i < bytes.length; ++i)
        bytes[i] = randomByte();

      return bytes;
    }

    private static short randomShort() {
      return (short)random.nextInt();
    }

    private static short[] randomShorts(final int length) {
      final short[] shorts = new short[length];
      for (int i = 0; i < shorts.length; ++i)
        shorts[i] = randomShort();

      return shorts;
    }

    private static int randomInt() {
      return random.nextInt();
    }

    private static int[] randomInts(final int length) {
      final int[] ints = new int[length];
      for (int i = 0; i < ints.length; ++i)
        ints[i] = randomInt();

      return ints;
    }

    private static float randomFloat() {
      return random.nextFloat();
    }

    private static float[] randomFloats(final int length) {
      final float[] floats = new float[length];
      for (int i = 0; i < floats.length; ++i)
        floats[i] = randomFloat();

      return floats;
    }

    @Test
    public void testLongOfInts() {
      for (int i = 0; i < 10000; ++i) {
        final int[] expected = randomInts(2);
        final long encoded = Numbers.Compound.encode(expected[0], expected[1]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.decodeInt(encoded, j));
      }
    }

    @Test
    public void testLongOfShorts() {
      for (int i = 0; i < 10000; ++i) {
        final short[] expected = randomShorts(4);
        final long encoded1 = Numbers.Compound.encode(expected[0], expected[1], expected[2], expected[3]);
        final long encoded2 = Numbers.Compound.encode(Numbers.Compound.encode(expected[0], expected[1]), Numbers.Compound.encode(expected[2], expected[3]));
        assertEquals(encoded1, encoded2);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.decodeShort(encoded1, j));
      }
    }

    @Test
    public void testLongOfBytes() {
      for (int i = 0; i < 10000; ++i) {
        final byte[] expected = randomBytes(8);
        final long encoded1 = Numbers.Compound.encode(expected[0], expected[1], expected[2], expected[3], expected[4], expected[5], expected[6], expected[7]);
        final long encoded2 = Numbers.Compound.encode(Numbers.Compound.encode(expected[0], expected[1], expected[2], expected[3]), Numbers.Compound.encode(expected[4], expected[5], expected[6], expected[7]));
        final long encoded3 = Numbers.Compound.encode(Numbers.Compound.encode(expected[0], expected[1]), Numbers.Compound.encode(expected[2], expected[3]), Numbers.Compound.encode(expected[4], expected[5]), Numbers.Compound.encode(expected[6], expected[7]));
        assertEquals(encoded1, encoded2);
        assertEquals(encoded2, encoded3);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.decodeByte(encoded1, j));
      }
    }

    @Test
    public void testLongOfIntFloat() {
      for (int i = 0; i < 10000; ++i) {
        final int expectedInt = randomInt();
        final float expectedFloat = randomFloat();
        final long encoded = Numbers.Compound.encode(expectedInt, expectedFloat);
        assertEquals("Index: 0, Value: " + expectedInt, expectedInt, Numbers.Compound.decodeInt(encoded, 0));
        assertEquals("Index: 1, Value: " + expectedFloat, expectedFloat, Numbers.Compound.decodeFloat(encoded, 1), epsilon);
      }
    }

    @Test
    public void testLongOfFloats() {
      for (int i = 0; i < 10000; ++i) {
        final float[] expected = randomFloats(2);
        final long encoded = Numbers.Compound.encode(expected[0], expected[1]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.decodeFloat(encoded, j), epsilon);
      }
    }

    @Test
    public void testLongOfFloatInt() {
      for (int i = 0; i < 10000; ++i) {
        final float expectedFloat = randomFloat();
        final int expectedInt = randomInt();
        final long encoded = Numbers.Compound.encode(expectedFloat, expectedInt);
        assertEquals("Index: 0, Value: " + expectedFloat, expectedFloat, Numbers.Compound.decodeFloat(encoded, 0), epsilon);
        assertEquals("Index: 1, Value: " + expectedInt, expectedInt, Numbers.Compound.decodeInt(encoded, 1));
      }
    }

    @Test
    public void testIntOfShorts() {
      for (int i = 0; i < 10000; ++i) {
        final short[] expected = randomShorts(2);
        final int encoded = Numbers.Compound.encode(expected[0], expected[1]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.decodeShort(encoded, j));
      }
    }

    @Test
    public void testIntOfBytes() {
      for (int i = 0; i < 10000; ++i) {
        final byte[] expected = randomBytes(4);
        final int encoded = Numbers.Compound.encode(expected[0], expected[1], expected[2], expected[3]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.decodeByte(encoded, j));
      }
    }

    @Test
    public void testShortOfBytes() {
      for (int i = 0; i < 10000; ++i) {
        final byte[] expected = randomBytes(2);
        final short encoded = Numbers.Compound.encode(expected[0], expected[1]);
        for (int j = 0; j < expected.length; ++j)
          assertEquals("Index: " + j + ", Value: " + expected[j], expected[j], Numbers.Compound.decodeByte(encoded, j));
      }
    }
  }

  @Test
  public void testParseNumber() {
    assertEquals(2.5, Numbers.parseNumber("2 1/2"), 0);
    assertEquals(2.75, Numbers.parseNumber("2 3/4"), 0);

    assertEquals(0, Numbers.parseNumber("0"), 0);
    assertEquals(299792458, Numbers.parseNumber(" 299792458"), 0);
    assertEquals(-299792458, Numbers.parseNumber("-299792458 "), 0);
    assertEquals(3.14159265, Numbers.parseNumber(" 3.14159265"), 0);
    assertEquals(-3.14159265, Numbers.parseNumber("-3.14159265"), 0);
    assertEquals(6.022E23, Numbers.parseNumber("6.022E23 "), 0);
    assertEquals(-6.022E23, Numbers.parseNumber(" -6.022E23"), 0);
    assertEquals(6.626068E-34, Numbers.parseNumber(" 6.626068E-34"), 0);
    assertEquals(-6.626068E-34, Numbers.parseNumber("-6.626068E-34 "), 0);

    assertEquals(Double.NaN, Numbers.parseNumber(null), 0);
    assertEquals(Double.NaN, Numbers.parseNumber(""), 0);
    assertEquals(Double.NaN, Numbers.parseNumber(" "), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("not number"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("3.14.15"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("29-97924"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-29-97924"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("2 997924"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-29-979.24"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-2.9-979.24"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("6.022 E23 "), 0);
    assertEquals(Double.NaN, Numbers.parseNumber(" -6.022E 23"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E--34 "), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E-3-4 "), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-6.626068E-3.4"), 0);
    assertEquals(Double.NaN, Numbers.parseNumber("-6.626E068E-34"), 0);
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
  public void testUnsignedByte() {
    final byte value = Byte.MAX_VALUE;
    assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedShort() {
    final short value = Short.MAX_VALUE;
    assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedInt() {
    final int value = Integer.MAX_VALUE;
    assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedLong() {
    final long value = Long.MAX_VALUE;
    assertEquals(BigInteger.valueOf(value), Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  @Test
  public void testUnsignedBigInteger() {
    final BigInteger value = new BigInteger("18446744073709551615");
    assertEquals(value, Numbers.Unsigned.toSigned(Numbers.Unsigned.toUnsigned(value)));
  }

  private static final Class<?>[] numberTypes = new Class<?>[] {Byte.class, Short.class, Integer.class, Float.class, Double.class, Long.class};

  @Test
  @SuppressWarnings("unchecked")
  public void testValueOf() {
    for (int i = 0; i < numberTypes.length; ++i) {
      for (int j = 0; j < numberTypes.length; ++j) {
        final Class<? extends Number> from = (Class<? extends Number>)numberTypes[i];
        final Class<? extends Number> to = (Class<? extends Number>)numberTypes[j];
        final Number value = Numbers.valueOf(111, from);
        assertEquals(value, Numbers.valueOf(Numbers.valueOf(value, to), from));
      }
    }
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
    for (final int len = str.length(); count < len && str.charAt(len - 1 - count) == '0'; ++count);
    return count;
  }

  @Test
  public void testTrailingZeroesByte() {
    for (int i = 0; i < 100000; ++i) {
      final byte n = i == 0 ? 0 : (byte)random.nextInt();
      final String str = String.valueOf(n);
      assertEquals(str, countTrailingZeroes(str), Numbers.trailingZeroes(n));
    }
  }

  @Test
  public void testTrailingZeroesShort() {
    for (int i = 0; i < 100000; ++i) {
      final short n = i == 0 ? 0 : (short)random.nextInt();
      final String str = String.valueOf(n);
      assertEquals(str, countTrailingZeroes(str), Numbers.trailingZeroes(n));
    }
  }

  @Test
  public void testTrailingZeroesInt() {
    for (int i = 0; i < 100000; ++i) {
      final int n = i == 0 ? 0 : random.nextInt();
      final String str = String.valueOf(n);
      assertEquals(str, countTrailingZeroes(str), Numbers.trailingZeroes(n));
    }
  }

  @Test
  public void testTrailingZeroesLong() {
    for (int i = 0; i < 100000; ++i) {
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
    for (final T number : numbers) {
      final String str = String.valueOf(number);
      assertEquals(number, function.apply(str));
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Number>void testParseRadix(final BiFunction<? super String,? super Integer,T> function, final int radix, final T ... numbers) {
    for (final T number : numbers) {
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

    for (byte r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r)
      testParseRadix(Numbers::parseInteger, r, -323, 3923, -7932, 38229, -732938, 83928384, -382983985);

    for (int i = 0; i < 1000; ++i) {
      for (int r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r) {
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

    for (byte r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r)
      testParseRadix(Numbers::parseLong, r, 323L, -3923L, 7932L, -38229L, 732938L, -83928384L, 382983985L, -8434893285L, 38434893285L, -938434893285L, 1938434893285L, -21938434893285L, 921938434893285L, -9921938434893285L, 79921938434893285L, -279921938434893285L, 8279921938434893285L);

    for (int i = 0; i < 1000; ++i) {
      for (int r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r) {
        final long random = (long)(Long.MIN_VALUE * Math.random() + Long.MAX_VALUE * Math.random());
        assertEquals(random, Long.parseLong(Long.toString(random, r), r));
        assertEquals(random, (long)Numbers.parseLong(Long.toString(random, r), r));
      }
    }
  }

  private static void testIsDigit(final int radix) {
    for (int r = 0; r < radix; ++r) {
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
    for (int r = Character.MIN_RADIX; r <= Character.MAX_RADIX; ++r)
      testIsDigit(r);
  }

  private static void testDigitsByte(final byte n) {
    final String str = String.valueOf(n);
    assertEquals(str, n < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testDigitsByte() {
    testDigitsByte(Byte.MIN_VALUE);
    testDigitsByte(Byte.MAX_VALUE);
    for (int i = 0; i < 1000000; ++i) {
      final byte n = (byte)random.nextInt();
      testDigitsByte(n);
    }
  }

  private static void testDigitsShort(final short n) {
    final String str = String.valueOf(n);
    assertEquals(str, n < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testDigitsShort() {
    testDigitsShort(Short.MIN_VALUE);
    testDigitsShort(Short.MAX_VALUE);
    for (int i = 0; i < 1000000; ++i) {
      final short n = (short)random.nextInt();
      testDigitsShort(n);
    }
  }

  private static void testDigitsInt(final int n) {
    final String str = String.valueOf(n);
    assertEquals(str, n < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testDigitsInt() {
    testDigitsInt(Integer.MIN_VALUE);
    testDigitsInt(Integer.MAX_VALUE);
    for (int i = 0; i < 1000000; ++i) {
      final int n = random.nextInt();
      testDigitsInt(n);
    }
  }

  private static void testDigitsLong(final long n) {
    final String str = String.valueOf(n);
    assertEquals(str, n < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testDigitsLong() {
    testDigitsLong(Long.MIN_VALUE);
    testDigitsLong(Long.MAX_VALUE);
    for (int i = 0; i < 1000000; ++i) {
      final long n = random.nextLong();
      testDigitsLong(n);
    }
  }

  private static void testDigitsBigInteger(final BigInteger n) {
    final String str = String.valueOf(n);
    assertEquals(str, n.signum() < 0 ? str.length() - 1 : str.length(), Numbers.precision(n));
  }

  @Test
  public void testDigitsBigInteger() {
    for (int i = 0; i < 1000000; ++i) {
      final BigInteger n = BigInteger.valueOf(random.nextLong());
      testDigitsBigInteger(n);
    }
  }

  private static void testDigitsBigDecimal(final BigDecimal n) {
    final String str = String.valueOf(n.stripTrailingZeros());
    int e = str.indexOf('e');
    if (e < 0)
      e = str.indexOf('E');

    if (e < 0)
      e = str.length();

    int len = n.signum() < 0 ? e - 1 : e;
    if (n.scale() > 0)
      --len;

    for (int i = n.signum() < 0 ? 1 : 0; len >= 0; ++i) {
      final char ch = str.charAt(i);
      if (ch == '0') {
        --len;
        continue;
      }

      if (ch == '.')
        continue;

      break;
    }

    assertEquals(str, len, Numbers.precision(n));
  }

  @Test
  public void testDigitsBigDecimal() {
    for (int i = 0; i < 1000000; ++i) {
      final BigDecimal n = BigDecimal.valueOf(random.nextDouble());
      testDigitsBigDecimal(n);
    }
  }

  @Test
  public void testCast() {
    for (int i = 0; i < 1000000; ++i) {
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
  public void testSignumInt() {
    for (int i = 9, j; i < 1000000; ++i) {
      j = random.nextInt();
      assertEquals(j < 0 ? -1 : j == 0 ? 0 : 1, Numbers.signum(j));
    }
  }

  @Test
  public void testSignumLong() {
    for (long i = 9, j; i < 1000000; ++i) {
      j = random.nextLong();
      assertEquals(j < 0 ? -1 : j == 0 ? 0 : 1, Numbers.signum(j));
    }
  }
}