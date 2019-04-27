# OpenJAX Extensions Lang

> Java API Extension for `java.lang`

[![Build Status](https://travis-ci.org/openjax/ext-lang.png)](https://travis-ci.org/openjax/ext-lang)
[![Coverage Status](https://coveralls.io/repos/github/openjax/ext-lang/badge.svg)](https://coveralls.io/github/openjax/ext-lang)
[![Javadocs](https://www.javadoc.io/badge/org.openjax.ext/lang.svg)](https://www.javadoc.io/doc/org.openjax.ext/lang)
[![Released Version](https://img.shields.io/maven-central/v/org.openjax.ext/lang.svg)](https://mvnrepository.com/artifact/org.openjax.ext/lang)

## Introduction

The OpenJAX Extensions Lang library provides supplementary utilities for classes that belong to `java.lang`, or are considered essential as to justify existence in `java.lang`.

## Classes

* **[AnnotationParameterException](src/main/java/org/openjax/ext/lang/AnnotationParameterException.java)**: Thrown to indicate an exception in a parameter value of an annotation.
* **[IllegalAnnotationException](src/main/java/org/openjax/ext/lang/IllegalAnnotationException.java)**: Thrown to indicate that an illegal annotation was encountered.
* **[OperatingSystem](src/main/java/org/openjax/ext/lang/OperatingSystem.java)**: Enum representing the host operating system of the java runtime.
* **[PackageLoader](src/main/java/org/openjax/ext/lang/PackageLoader.java)**: The `PackageLoader` is a class used to discover and load classes in a package. Given a package name, the `PackageLoader` should attempt to locate and/or load the classes of the package. The `PackageLoader` uses a `ClassLoader`, either specified or default, for the discovery of packages and loading of classes.
* **[PackageNotFoundException](src/main/java/org/openjax/ext/lang/PackageNotFoundException.java)**: Thrown when an application tries to load in a package using `PackageLoader`, but no definition for the specified package could be found.

### PackageLoader

The `PackageLoader` is a class used to discover and load classes in a package. Given a package name, the `PackageLoader` should attempt to locate and/or load the classes of the package. The `PackageLoader` uses a `ClassLoader`, either specified or default, for the discovery of packages and loading of classes.

#### Usage

The following example illustrates how to load the classes in the `org.junit.runner` package, and to initialize classes whose name starts with `org.junit.runner.Filter`.

```java
PackageLoader.getContextPackageLoader().loadPackage("org.junit.runner", new Predicate<Class<?>>() {
  @Override
  public boolean test(Class<?> t) {
    return t.getName().startsWith("org.junit.runner.Filter");
  }
})
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.