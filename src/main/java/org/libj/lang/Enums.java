/* Copyright (c) 2016 LibJ
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

import java.lang.reflect.Array;
import java.util.List;

/**
 * Utility functions for operations pertaining to {@code enum} and {@link Enum}.
 */
public final class Enums {
  @SuppressWarnings("unchecked")
  private static <T extends Enum<T>>T[] recurseValueOf(final Class<T> type, final int index, final int depth, final String[] names) {
    if (index == names.length)
      return (T[])Array.newInstance(type, depth);

    T value;
    try {
      value = Enum.valueOf(type, names[index]);
    }
    catch (final IllegalArgumentException e) {
      value = null;
    }

    final T[] enums = recurseValueOf(type, index + 1, value != null ? depth + 1 : depth, names);
    if (value != null)
      enums[depth] = value;

    return enums;
  }

  /**
   * Returns an array of type {@code <T>} containing the results of {@link Enum#valueOf(Class,String)} applied to each of the
   * provided {@code names}.
   * <p>
   * All names that do not match a constant in the specified enum class are omitted in the returned array.
   *
   * @implNote This implementation uses a recursive algorithm for optimal performance, and may fail if the provided {@code names}
   *           contains ~8000+ elements.
   * @param <T> The type parameter of the specified {@link Enum}.
   * @param type The class for the type {@code <T>}.
   * @param names The string array of names on which to apply {@link Enum#valueOf(Class,String)}.
   * @return An array of type {@code <T>} containing the results of {@link Enum#valueOf(Class,String)} applied to each of the
   *         provided {@code names}.
   * @throws IllegalArgumentException If the specified class object does not represent an enum type.
   * @throws NullPointerException If {@code type} or {@code names} is null.
   */
  public static <T extends Enum<T>>T[] valueOf(final Class<T> type, final String ... names) {
    return recurseValueOf(type, 0, 0, names);
  }

  /**
   * Returns a {@link List} of type {@code <T>} containing the results of {@link Enum#valueOf(Class,String)} applied to each of the
   * provided {@code names}.
   * <p>
   * All names that do not match a constant in the specified enum class are omitted in the returned array.
   *
   * @implNote This implementation uses a recursive algorithm for optimal performance, and may fail if the provided {@code names}
   *           contains ~8000+ elements.
   * @param <T> The type parameter of the specified {@link Enum}.
   * @param type The class for the type {@code <T>}.
   * @param names The {@link List} of names on which to apply {@link Enum#valueOf(Class,String)}.
   * @return A {@link List} of type {@code <T>} containing the results of {@link Enum#valueOf(Class,String)} applied to each of the
   *         provided {@code names}.
   * @throws NullPointerException If {@code type} or {@code names} is null.
   * @throws IllegalArgumentException If the specified class object does not represent an enum type.
   */
  public static <T extends Enum<T>>T[] valueOf(final Class<T> type, final List<String> names) {
    return valueOf(type, names.toArray(new String[names.size()]));
  }

  /**
   * A utility class that provides functions to encode and decode {@code enum} instances to and from {@code int} or {@code long}
   * values, whereby the bits of the {@code int} or {@code long} values represent the ordinal numbers of the {@code enum} instances.
   */
  public static final class Mask {
    /**
     * Toggles the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to toggle the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be toggled.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>byte toggle(byte mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask ^= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Toggles the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to toggle the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be toggled.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>short toggle(short mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask ^= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Toggles the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to toggle the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be toggled.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>int toggle(int mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask ^= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Toggles the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to toggle the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be toggled.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>long toggle(long mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask ^= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Sets the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to set the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be set.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>byte set(byte mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask |= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Sets the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to set the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be set.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>short set(short mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask |= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Sets the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to set the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be set.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>int set(int mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask |= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Sets the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to set the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be set.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>long set(long mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask |= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Unsets the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to unset the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be unset.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>byte unset(byte mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask &= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Unsets the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to unset the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be unset.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>short unset(short mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask &= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Unsets the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to unset the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be unset.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>int unset(int mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask &= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Unsets the bits in the specified {@code mask} at the ordinal values of the specified {@code enum} instances.
     *
     * @param <E> The type of the {@link Enum}.
     * @param mask The mask in which to unset the bits.
     * @param enums The {@code enum} instances at ordinals of which the bits in {@code mask} will be unset.
     * @return The {@code mask}.
     * @throws NullPointerException If {@code enums} or a member of {@code enums} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>long unset(long mask, final E ... enums) {
      for (int i = 0, i$ = enums.length; i < i$; ++i) // [A]
        mask &= 1 << enums[i].ordinal();

      return mask;
    }

    /**
     * Checks whether the bit position represented by the specified ordinal is set in the specified mask.
     *
     * @param mask The mask in which to check the bit represented by {@code ordinal}.
     * @param ordinal The ordinal representing the bit to check in the {@code mask}.
     * @return {@code true} if the bit position represented by the specified ordinal is set in the specified mask, otherwise
     *         {@code false}.
     */
    public static boolean check(final byte mask, final int ordinal) {
      return (mask & (1 << ordinal)) != 0;
    }

    /**
     * Checks whether the bit position represented by the specified ordinal is set in the specified mask.
     *
     * @param mask The mask in which to check the bit represented by {@code ordinal}.
     * @param ordinal The ordinal representing the bit to check in the {@code mask}.
     * @return {@code true} if the bit position represented by the specified ordinal is set in the specified mask, otherwise
     *         {@code false}.
     */
    public static boolean check(final short mask, final int ordinal) {
      return (mask & (1 << ordinal)) != 0;
    }

    /**
     * Checks whether the bit position represented by the specified ordinal is set in the specified mask.
     *
     * @param mask The mask in which to check the bit represented by {@code ordinal}.
     * @param ordinal The ordinal representing the bit to check in the {@code mask}.
     * @return {@code true} if the bit position represented by the specified ordinal is set in the specified mask, otherwise
     *         {@code false}.
     */
    public static boolean check(final int mask, final int ordinal) {
      return (mask & (1 << ordinal)) != 0;
    }

    /**
     * Checks whether the bit position represented by the specified ordinal is set in the specified mask.
     *
     * @param mask The mask in which to check the bit represented by {@code ordinal}.
     * @param ordinal The ordinal representing the bit to check in the {@code mask}.
     * @return {@code true} if the bit position represented by the specified ordinal is set in the specified mask, otherwise
     *         {@code false}.
     */
    public static boolean check(final long mask, final int ordinal) {
      return (mask & (1 << ordinal)) != 0;
    }

    /**
     * Checks whether the bit position represented by the ordinal of the specified {@code enum} is set in the specified mask.
     *
     * @param mask The mask in which to check the bit represented by {@code ordinal}.
     * @param enm The {@code enum} with ordinal representing the bit to check in the {@code mask}.
     * @return {@code true} if the bit position represented by the specified {@code enum} is set in the specified mask, otherwise
     *         {@code false}.
     * @throws NullPointerException If {@link Enum enm} is null.
     */
    public static boolean check(final byte mask, final Enum<?> enm) {
      return check(mask, enm.ordinal());
    }

    /**
     * Checks whether the bit position represented by the ordinal of the specified {@code enum} is set in the specified mask.
     *
     * @param mask The mask in which to check the bit represented by {@code ordinal}.
     * @param enm The {@code enum} with ordinal representing the bit to check in the {@code mask}.
     * @return {@code true} if the bit position represented by the specified {@code enum} is set in the specified mask, otherwise
     *         {@code false}.
     * @throws NullPointerException If {@link Enum enm} is null.
     */
    public static boolean check(final short mask, final Enum<?> enm) {
      return check(mask, enm.ordinal());
    }

    /**
     * Checks whether the bit position represented by the ordinal of the specified {@code enum} is set in the specified mask.
     *
     * @param mask The mask in which to check the bit represented by {@code ordinal}.
     * @param enm The {@code enum} with ordinal representing the bit to check in the {@code mask}.
     * @return {@code true} if the bit position represented by the specified {@code enum} is set in the specified mask, otherwise
     *         {@code false}.
     * @throws NullPointerException If {@link Enum enm} is null.
     */
    public static boolean check(final int mask, final Enum<?> enm) {
      return check(mask, enm.ordinal());
    }

    /**
     * Checks whether the bit position represented by the ordinal of the specified {@code enum} is set in the specified mask.
     *
     * @param mask The mask in which to check the bit represented by {@code ordinal}.
     * @param enm The {@code enum} with ordinal representing the bit to check in the {@code mask}.
     * @return {@code true} if the bit position represented by the specified {@code enum} is set in the specified mask, otherwise
     *         {@code false}.
     * @throws NullPointerException If {@link Enum enm} is null.
     */
    public static boolean check(final long mask, final Enum<?> enm) {
      return check(mask, enm.ordinal());
    }

    /**
     * Returns an array of instances of the specified {@code enum} whose ordinals are set in the specified mask.
     *
     * @param <E> The type parameter representing the {@code enum} class.
     * @param values The array of {@code enum} values returned by the {@code MyEnum.values()} method, where {@code MyEnum} is the
     *          {@code enum} of interest.
     * @param mask The mask with bits representing ordinal values of the specified {@code enum}.
     * @return An array of instances of the specified {@code enum} values whose ordinals are set in the specified mask.
     * @throws ArrayIndexOutOfBoundsException If the mask defines an ordinal that is out of bounds of the values array of the
     *           {@code enum} of interest.
     * @throws NullPointerException If {@code values} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>E[] toArray(final byte mask, final E ... values) {
      return toArray(values, mask, Byte.SIZE, 0, 0);
    }

    /**
     * Returns an array of instances of the specified {@code enum} whose ordinals are set in the specified mask.
     *
     * @param <E> The type parameter representing the {@code enum} class.
     * @param values The array of {@code enum} values returned by the {@code MyEnum.values()} method, where {@code MyEnum} is the
     *          {@code enum} of interest.
     * @param mask The mask with bits representing ordinal values of the specified {@code enum}.
     * @return An array of instances of the specified {@code enum} values whose ordinals are set in the specified mask.
     * @throws ArrayIndexOutOfBoundsException If the mask defines an ordinal that is out of bounds of the values array of the
     *           {@code enum} of interest.
     * @throws NullPointerException If {@code values} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>E[] toArray(final short mask, final E ... values) {
      return toArray(values, mask, Short.SIZE, 0, 0);
    }

    /**
     * Returns an array of instances of the specified {@code enum} whose ordinals are set in the specified mask.
     *
     * @param <E> The type parameter representing the {@code enum} class.
     * @param values The array of {@code enum} values returned by the {@code MyEnum.values()} method, where {@code MyEnum} is the
     *          {@code enum} of interest.
     * @param mask The mask with bits representing ordinal values of the specified {@code enum}.
     * @return An array of instances of the specified {@code enum} values whose ordinals are set in the specified mask.
     * @throws ArrayIndexOutOfBoundsException If the mask defines an ordinal that is out of bounds of the values array of the
     *           {@code enum} of interest.
     * @throws NullPointerException If {@code values} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>E[] toArray(final int mask, final E ... values) {
      return toArray(values, mask, Integer.SIZE, 0, 0);
    }

    /**
     * Returns an array of instances of the specified {@code enum} whose ordinals are set in the specified mask.
     *
     * @param <E> The type parameter representing the {@code enum} class.
     * @param values The array of {@code enum} values returned by the {@code MyEnum.values()} method, where {@code MyEnum} is the
     *          {@code enum} of interest.
     * @param mask The mask with bits representing ordinal values of the specified {@code enum}.
     * @return An array of instances of the specified {@code enum} values whose ordinals are set in the specified mask.
     * @throws ArrayIndexOutOfBoundsException If the mask defines an ordinal that is out of bounds of the values array of the
     *           {@code enum} of interest.
     * @throws NullPointerException If {@code values} is null.
     */
    @SafeVarargs
    public static <E extends Enum<?>>E[] toArray(final long mask, final E ... values) {
      return toArray(values, mask, 0, 0);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<?>>E[] toArray(final E[] values, final int mask, final int size, final int index, final int depth) {
      for (int i = index; i < size; ++i) { // [N]
        if ((mask & (1 << i)) != 0) {
          final E enm = values[i];
          final E[] array = toArray(values, mask, size, index + 1, depth + 1);
          array[depth] = enm;
          return array;
        }
      }

      return (E[])Array.newInstance(values.getClass().getComponentType(), depth);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<?>>E[] toArray(final E[] values, final long mask, final int index, final int depth) {
      for (int i = index; i < Long.SIZE; ++i) { // [N]
        if ((mask & (1 << i)) != 0) {
          final E enm = values[i];
          final E[] array = toArray(values, mask, index + 1, depth + 1);
          array[depth] = enm;
          return array;
        }
      }

      return (E[])Array.newInstance(values.getClass().getComponentType(), depth);
    }

    private Mask() {
    }
  }

  private Enums() {
  }
}