# LibJ Lang

[![Build Status](https://travis-ci.org/libj/lang.svg?1)](https://travis-ci.org/libj/lang)
[![Coverage Status](https://coveralls.io/repos/github/libj/lang/badge.svg?1)](https://coveralls.io/github/libj/lang)
[![Javadocs](https://www.javadoc.io/badge/org.libj/lang.svg?1)](https://www.javadoc.io/doc/org.libj/lang)
[![Released Version](https://img.shields.io/maven-central/v/org.libj/lang.svg?1)](https://mvnrepository.com/artifact/org.libj/lang)

## Introduction

The LibJ Lang library provides supplementary utilities for classes that belong to `java.lang`, or are considered essential as to justify existence in `java.lang`.

## Classes

* **[AnnotationParameterException](src/main/java/org.libj/lang/AnnotationParameterException.java)**: Thrown to indicate an exception in a parameter value of an annotation.
* **[IllegalAnnotationException](src/main/java/org.libj/lang/IllegalAnnotationException.java)**: Thrown to indicate that an illegal annotation was encountered.
* **[OperatingSystem](src/main/java/org.libj/lang/OperatingSystem.java)**: Enum representing the host operating system of the java runtime.
* **[PackageLoader](src/main/java/org.libj/lang/PackageLoader.java)**: The `PackageLoader` is a class used to discover and load classes in a package. Given a package name, the `PackageLoader` should attempt to locate and/or load the classes of the package. The `PackageLoader` uses a `ClassLoader`, either specified or default, for the discovery of packages and loading of classes.
* **[PackageNotFoundException](src/main/java/org.libj/lang/PackageNotFoundException.java)**: Thrown when an application tries to load in a package using `PackageLoader`, but no definition for the specified package could be found.

### PackageLoader

The `PackageLoader` is a class used to discover and load classes in a package. Given a package name, the `PackageLoader` should attempt to locate and/or load the classes of the package. The `PackageLoader` uses a `ClassLoader`, either specified or default, for the discovery of packages and loading of classes.

#### Usage

The following example illustrates how to load the classes in the `org.junit.runner` package, and to initialize classes whose name starts with `org.junit.runner.Filter`.

```java
PackageLoader.getContextPackageLoader().loadPackage("org.junit.runner", c -> c.getName().startsWith("org.junit.runner.Filter"));
```

## Contributing

Pull requests are welcome. For major changes, please [open an issue](../../issues) first to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.