/* Copyright (c) 2017 LibJ
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

import static org.libj.lang.Assertions.*;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Utility functions for checking or creating valid Java Identifiers.
 *
 * @see <a href= "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">JLS 3.8 Identifiers</a>
 */
public final class Identifiers {
  private static final Pattern unqualifiedJavaIdentifierPattern = Pattern.compile("[a-zA-Z_$][a-zA-Z\\d_$]*");
  private static final Pattern qualifiedJavaIdentifierPattern = Pattern.compile("((" + unqualifiedJavaIdentifierPattern + ")\\.)*" + unqualifiedJavaIdentifierPattern.pattern());

  /**
   * Tests whether the argument {@code identifier} is a valid identifier as defined in the Java Language Specification.
   *
   * @param identifier The identifier.
   * @param qualified Test versus rules of qualified or unqualified identifiers.
   * @return Whether the argument {@code identifier} is a valid identifier.
   * @throws IllegalArgumentException If {@code identifier} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">JLS 3.8 Identifiers</a>
   */
  public static boolean isValid(final String identifier, final boolean qualified) {
    return (qualified ? qualifiedJavaIdentifierPattern : unqualifiedJavaIdentifierPattern).matcher(assertNotNull(identifier)).matches();
  }

  /**
   * Tests whether the argument {@code identifier} is a valid identifier as defined in the Java Language Specification. Calling this
   * method is the equivalent of {@code isValid(identifier, true)}.
   *
   * @param identifier The class name.
   * @return Whether the argument {@code identifier} is a valid identifier.
   * @throws IllegalArgumentException If {@code identifier} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.8">JLS 3.8 Identifiers</a>
   */
  public static boolean isValid(final String identifier) {
    return isValid(identifier, true);
  }

  // NOTE: These arrays are sorted
  private static final String[] reservedWords = {"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while"};
  private static final char[] discardTokens = {'!', '"', '#', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '>', '?', '@', '[', '\\', ']', '^', '_', '`', '{', '|', '}', '~'};

  private static void checkPrefix(final char prefix) {
    if (prefix != '\0' && !Character.isJavaIdentifierStart(prefix))
      throw new IllegalArgumentException("Illegal prefix character: " + prefix);
  }

  private static void checkSubstitutes(final Map<Character,String> substitutes) {
    if (substitutes != null)
      for (final String substitute : substitutes.values()) // [C]
        for (int i = 0, i$ = substitute.length(); i < i$; ++i) // [N]
          if (!Character.isJavaIdentifierPart(substitute.charAt(i)))
            throw new IllegalArgumentException("Substitution \"" + substitute + "\" contains illegal character: " + substitute.charAt(i));
  }

  private static boolean substitute(final StringBuilder builder, final boolean start, final char ch, final char substitute, final Map<Character,String> substitutes, final Function<? super Character,String> function) {
    if (function != null) {
      final String replacement = function.apply(ch);
      if (replacement != null) {
        for (int i = 0, i$ = replacement.length(); i < i$; ++i) // [N]
          if (start && builder.length() == 0 && i == 0 ? !Character.isJavaIdentifierStart(replacement.charAt(i)) : !Character.isJavaIdentifierPart(replacement.charAt(i)))
            throw new IllegalArgumentException("Substitution \"" + replacement + "\" contains illegal " + (start ? "start " : "") + "character: '" + replacement.charAt(i) + "'");

        builder.append(replacement);
      }
    }
    else if (substitutes != null) {
      final String replacement = substitutes.get(ch);
      if (replacement != null)
        builder.append(replacement);
    }
    else if (substitute != '\0') {
      builder.append(substitute);
    }
    else {
      return false;
    }

    return true;
  }

  /**
   * Returns {@code true} if the specified word is a Java reserved word; otherwise, {@code false}.
   *
   * @param word The word to test.
   * @return {@code true} if the specified word is a Java reserved word; otherwise, {@code false}.
   * @throws IllegalArgumentException If {@code word} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.9">3.9. Keywords</a>
   */
  public static boolean isReservedWord(final String word) {
    return Arrays.binarySearch(reservedWords, assertNotNull(word)) >= 0;
  }

  private static String transformNotReserved(final char prefix, final char suffix, final Map<Character,String> substitutes, final Function<Character,String> function, final StringBuilder builder) {
    final String word = builder.toString();
    if (!isReservedWord(word))
      return word;

    if (suffix != '\0')
      return builder.append(suffix).toString();

    if (prefix != '\0')
      return builder.insert(0, prefix).toString();

    final String pre = substitutes != null ? substitutes.get(null) : function != null ? function.apply(null) : null;
    if (pre != null)
      return pre + word;

    throw new IllegalArgumentException("Unable to transform reserved word due to unspecified prefix, suffix, nor 'null' substitution rule: " + word);
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the
   * character is substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions. This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', substitutes, null, toIdentifier0(string, prefix, substitute, substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with an illegal character are prepended with {@code _}.
   * Strings that are Java Reserved Words are prepended with {@code _}. All other illegal characters are substituted with the string
   * value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final Map<Character,String> substitutes) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = '_';
    return transformNotReserved(prefix, '\0', substitutes, null, toIdentifier0(string, prefix, '\0', substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the
   * illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', substitutes, null, toIdentifier0(string, prefix, '\0', substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitutes Function to dereference illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix, final Function<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, substitutes, toIdentifier0(string, prefix, '\0', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @return The string transformed to a valid Java Identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix, final char substitute) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, null, toIdentifier0(string, prefix, substitute, null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @return The string transformed to a valid Java Identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final char prefix) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, null, toIdentifier0(string, prefix, '\0', null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with an illegal character are prepended with {@code _}.
   * Strings that are Java Reserved Words are prepended with {@code _}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @param substitutes Function to dereference illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string, final Function<Character,String> substitutes) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = '_';
    return transformNotReserved(prefix, '\0', null, substitutes, toIdentifier0(string, prefix, '\0', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier. Strings that start with an illegal character are prepended with {@code _}.
   * Strings that are Java Reserved Words are prepended with {@code _}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toIdentifier(final String string) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = '_';
    return transformNotReserved(prefix, '\0', null, null, toIdentifier0(string, prefix, '\0', null, null));
  }

  private static StringBuilder toIdentifier0(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes, final Function<? super Character,String> function) {
    checkPrefix(prefix);
    checkSubstitutes(substitutes);
    final StringBuilder builder = new StringBuilder(string.length());
    if (string.length() == 0)
      return builder;

    int i$ = string.length();
    int i = 0;
    char ch = '\0';
    for (; i < i$; ++i) { // [N]
      ch = string.charAt(i);
      if (Character.isJavaIdentifierStart(ch)) {
        builder.append(ch);
        break;
      }

      if (Character.isJavaIdentifierPart(ch) && prefix != '\0') {
        builder.append(prefix).append(ch);
        break;
      }

      if (substitute(builder, prefix == '\0', ch, substitute, substitutes, function)) {
        break;
      }
    }

    if (!Character.isJavaIdentifierStart(ch) && (builder.length() == 0 || !Character.isJavaIdentifierStart(builder.charAt(0)))) {
      if (prefix != '\0')
        builder.insert(0, "_");
      else
        throw new IllegalArgumentException("Unspecified prefix or substitution for illegal start character: " + ch);
    }

    for (++i; i < i$; ++i) { // [N]
      ch = string.charAt(i);
      if (Character.isJavaIdentifierPart(ch)) {
        builder.append(ch);
        continue;
      }

      substitute(builder, i == 0, ch, substitute, substitutes, function);
    }

    return builder;
  }

  /**
   * Transforms a string into a valid Java Identifier that meets suggested package name guidelines. Strings that are Java Reserved
   * Words are prepended with {@code prefix}. Strings that start with an illegal character are prepended with {@code _}. All other
   * illegal characters are substituted {@code _}. If the domain name contains a hyphen, or any other special character not allowed
   * in an identifier, convert it into an underscore. If any of the resulting package name components are keywords, append an
   * underscore to them. If any of the resulting package name components start with a digit, or any other character that is not
   * allowed as an initial character of an identifier, have an underscore prefixed to the component.
   *
   * @param string The input string.
   * @param substitutes Function to dereference illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier that meets suggested package name guidelines.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   * @see <a href= "https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html">Package Names</a>
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-6.html#d5e8089">Unique Package Names</a>
   */
  public static String toPackageCase(final String string, final Function<Character,String> substitutes) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = '_';
    return transformNotReserved(prefix, '_', null, substitutes, toPackageCase0(string, prefix, '_', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier that meets suggested package name guidelines. Strings that are Java Reserved
   * Words are prepended with {@code prefix}. Strings that start with an illegal character are prepended with {@code _}. All other
   * illegal characters are substituted {@code _}. If the domain name contains a hyphen, or any other special character not allowed
   * in an identifier, convert it into an underscore. If any of the resulting package name components are keywords, append an
   * underscore to them. If any of the resulting package name components start with a digit, or any other character that is not
   * allowed as an initial character of an identifier, have an underscore prefixed to the component.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier that meets suggested package name guidelines.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   * @see <a href= "https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html">Package Names</a>
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-6.html#d5e8089">Unique Package Names</a>
   */
  public static String toPackageCase(final String string) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = '_';
    return transformNotReserved(prefix, '_', null, null, toPackageCase0(string, prefix, '_', null, null));
  }

  private static StringBuilder toPackageCase0(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes, final Function<? super Character,String> function) {
    return Strings.toLowerCase(toIdentifier0(string, prefix, substitute, substitutes, function));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the
   * character is substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions. This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in camelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', substitutes, null, toCamelCase0(string, prefix, substitute, substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the
   * illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions. This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in camelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', substitutes, null, toCamelCase0(string, prefix, '\0', substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @return The string transformed to a valid Java Identifier in camelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix, final char substitute) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, null, toCamelCase0(string, prefix, substitute, null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitutes Function to dereference illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in camelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix, final Function<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, substitutes, toCamelCase0(string, prefix, '\0', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings that start with an illegal character are prepended with
   * {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @return The string transformed to a valid Java Identifier in camelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final char prefix) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, null, toCamelCase0(string, prefix, '\0', null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings that start with an illegal character are prepended with
   * {@code x}. Strings that are Java Reserved Words are prepended with {@code x}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @param substitutes Function to dereference illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in camelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final Function<Character,String> substitutes) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = 'x';
    return transformNotReserved(prefix, '\0', null, substitutes, toCamelCase0(string, prefix, '\0', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings that start with an illegal character are prepended with
   * {@code x}. Strings that are Java Reserved Words are prepended with {@code x}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier in camelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = 'x';
    return transformNotReserved(prefix, '\0', null, null, toCamelCase0(string, prefix, '\0', null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in camelCase. Strings that start with an illegal character are prepended with
   * {@code x}. Strings that are Java Reserved Words are prepended with {@code x}. All other illegal characters are substituted with
   * the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the illegal character is
   * omitted.
   *
   * @param string The input string.
   * @param substitutes The mapping of illegal characters to their substitutions. This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in camelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toCamelCase(final String string, final Map<Character,String> substitutes) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = 'x';
    return transformNotReserved(prefix, '\0', substitutes, null, toCamelCase0(string, prefix, '\0', substitutes, null));
  }

  private static StringBuilder toCamelCase0(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes, final Function<? super Character,String> function) {
    checkPrefix(prefix);
    checkSubstitutes(substitutes);
    final StringBuilder builder = new StringBuilder(string.length());
    if (string.length() == 0)
      return builder;

    int startUpper = -1;
    boolean capNext = false;
    for (int i = 0, i$ = string.length(); i < i$; ++i) { // [N]
      char ch = string.charAt(i);
      if (builder.length() == 0 && !Character.isJavaIdentifierStart(ch)) {
        if (prefix != '\0')
          builder.append(prefix);
        else if (substitute(builder, i == 0, ch, substitute, substitutes, function))
          ++i;
        else
          throw new IllegalArgumentException("Unspecified prefix or substitution for illegal start character: " + ch);
      }

      ch = string.charAt(i);
      if (startUpper == -1 && Character.isUpperCase(ch))
        startUpper = i;
      else if (startUpper != -1 && Character.isLowerCase(ch))
        startUpper = -1;

      final int index = Arrays.binarySearch(discardTokens, ch);
      if (index >= 0) {
        if (startUpper != -1) {
          for (int j = startUpper + 1; j < i; ++j) // [N]
            builder.setCharAt(j, Character.toLowerCase(builder.charAt(j)));

          startUpper = -1;
        }

        capNext = i != 0;
        substitute(builder, i == 0, ch, substitute, substitutes, function);
      }
      else if (capNext) {
        builder.append(Character.toUpperCase(ch));
        capNext = false;
      }
      else {
        builder.append(ch);
      }
    }

    return builder;
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the
   * character is substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions. This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', substitutes, null, toInstanceCase0(string, prefix, substitute, substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the
   * illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', substitutes, null, toInstanceCase0(string, prefix, '\0', substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the
   * illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitutes Function to dereference illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix, final Function<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, substitutes, toInstanceCase0(string, prefix, '\0', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix, final char substitute) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, null, toInstanceCase0(string, prefix, substitute, null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. Strings that are Java Reserved Words are prepended with {@code prefix}. All other illegal characters are
   * omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final char prefix) {
    return assertNotNull(string).length() == 0 ? string : transformNotReserved(prefix, '\0', null, null, toInstanceCase0(string, prefix, '\0', null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase. Strings that start with an illegal character are prepended
   * with {@code _}. Strings that are Java Reserved Words are prepended with {@code _}. All other illegal characters are substituted
   * with the string value mapped to the key of the character in {@code substitutes}. If the mapping is missing, the illegal
   * character is omitted.
   *
   * @param string The input string.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final Map<Character,String> substitutes) {
    if (string.length() == 0)
      return string;

    final char prefix = '_';
    return transformNotReserved(prefix, '\0', substitutes, null, toInstanceCase0(string, prefix, '\0', substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase. Strings that start with an illegal character are prepended
   * with {@code _}. Strings that are Java Reserved Words are prepended with {@code _}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @param substitutes Function to dereference illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string, final Function<Character,String> substitutes) {
    if (string.length() == 0)
      return string;

    final char prefix = '_';
    return transformNotReserved(prefix, '\0', null, substitutes, toInstanceCase0(string, prefix, '\0', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier in lower-camelCase. Strings that start with an illegal character are prepended
   * with {@code _}. Strings that are Java Reserved Words are prepended with {@code _}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier in lower-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toInstanceCase(final String string) {
    if (assertNotNull(string).length() == 0)
      return string;

    final char prefix = '_';
    return transformNotReserved(prefix, '\0', null, null, toInstanceCase0(string, prefix, '\0', null, null));
  }

  /**
   * Transforms a string into a legal Java [c]amelCase identifier, guaranteeing the beginning string of upper-case characters (until
   * the last) are changed to lower case. All illegal characters are removed, and used to determine the location of change of case.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions. This mapping overrides the default substitution.
   * @return The string transformed to a legal Java [c]amelCase identifier.
   * @throws IllegalArgumentException If {@code string} is null.
   */
  private static StringBuilder toInstanceCase0(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes, final Function<? super Character,String> function) {
    final StringBuilder builder = toCamelCase0(string, prefix, substitute, substitutes, function);
    final int i$ = builder.length();
    if (i$ == 0)
      return builder;

    if (i$ == 1) {
      if (Character.isUpperCase(builder.charAt(0)))
        builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));

      return builder;
    }

    int i;
    for (i = 0; i < i$; ++i) // [N]
      if (!Character.isUpperCase(builder.charAt(i)))
        break;

    if (i == 0)
      return builder;

    if (i == i$)
      return Strings.toLowerCase(builder);

    if (i == 1) {
      builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
      return builder;
    }

    for (int j = 0; j < i - 1; ++j) // [N]
      builder.setCharAt(j, Character.toLowerCase(builder.charAt(j)));

    return builder;
  }

  private static String toClassCase(final StringBuilder builder) {
    if (Strings.isUpperCase(builder))
      return Strings.toLowerCase(builder, 1, builder.length()).toString();

    if (!Character.isUpperCase(builder.charAt(0)))
      builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));

    return builder.toString();
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. All other illegal characters are substituted with the string value mapped to the key of the character in
   * {@code substitutes}. If the mapping is missing, the character is substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @param substitutes The mapping of illegal characters to their substitutions. This mapping overrides the default substitution.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix, final char substitute, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : toClassCase(toCamelCase0(string, prefix, substitute, substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. All other illegal characters are substituted with the string value mapped to the key of the character in
   * {@code substitutes}. If the mapping is missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : toClassCase(toCamelCase0(string, prefix, '\0', substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. All other illegal characters are substituted with the string value mapped to the key of the character in
   * {@code substitutes}. If the mapping is missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix, final Function<? super Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : toClassCase(toCamelCase0(string, prefix, '\0', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. All other illegal characters are substituted with the {@code substitute} char.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @param substitute The default substitution for illegal characters.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix, final char substitute) {
    return assertNotNull(string).length() == 0 ? string : toClassCase(toCamelCase0(string, prefix, substitute, null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase. Strings that start with an illegal character are prepended
   * with {@code prefix}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @param prefix The character that will be prepended to the string if the first character is not valid.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final char prefix) {
    return assertNotNull(string).length() == 0 ? string : toClassCase(toCamelCase0(string, prefix, '\0', null, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase. Strings that start with an illegal character are prepended
   * with {@code X}. All other illegal characters are substituted with the string value mapped to the key of the character in
   * {@code substitutes}. If the mapping is missing, the illegal character is omitted.
   *
   * @param string The input string.
   * @param substitutes The mapping of illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final Map<Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : toClassCase(toCamelCase0(string, 'X', '\0', substitutes, null));
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase. Strings that start with an illegal character are prepended
   * with {@code X}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @param substitutes Function to dereference illegal characters to their substitutions.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string, final Function<? super Character,String> substitutes) {
    return assertNotNull(string).length() == 0 ? string : toClassCase(toCamelCase0(string, 'X', '\0', null, substitutes));
  }

  /**
   * Transforms a string into a valid Java Identifier in Title-CamelCase. Strings that start with an illegal character are prepended
   * with {@code X}. All other illegal characters are omitted.
   *
   * @param string The input string.
   * @return The string transformed to a valid Java Identifier in Title-CamelCase.
   * @throws IllegalArgumentException If {@code string} is null.
   * @see <a href= "https://docs.oracle.com/javase/specs/jls/se9/html/jls-3.html#jls-3.8">Java Identifiers</a>
   */
  public static String toClassCase(final String string) {
    return assertNotNull(string).length() == 0 ? string : toClassCase(toCamelCase0(string, 'X', '\0', null, null));
  }

  private Identifiers() {
  }
}