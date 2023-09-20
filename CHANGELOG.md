# Changes by Version

## [v0.8.1-SNAPSHOT](https://github.com/libj/util/compare/32a72d952fe3410214605489c35e536179132d5b..HEAD)

## [v0.8.0](https://github.com/libj/lang/compare/9ef34c960414e289ffe41251662b5c76ea53e192..32a72d952fe3410214605489c35e536179132d5b) (2023-09-20)
* #65 Improve Resources.list() API
* #64 Implement Systems.getProperty(...) convenience methods
* #63 Add ServiceLoaders
* #62 Implement Booleans.valueOf(String,defaultValue)
* #61 Implement Classes.forNamePrimitiveOrNull
* #60 Add Classes.getAnnotationDeep(Method) and Classes.isAnnotationPresentDeep(Method)
* #58 Implement Strings.split(CharSequence,char)
* #57 Refactor Number.Compound -> Number.Composite
* #56 Implement WrappedArrayList
* #55 Implement Threads.interruptAfterTimeout(...)
* #54 Implement Threads.printThreadTrace()
* #53 Numbers.Compound: (float,short,short) and (float,byte,byte,byte,byte) overloads
* #52 Implement Strings.startsWithIgnoreCase(...)
* #51 Add Manifests
* #50 Add Runtimes class
* #49 Implement EnumerationIterator
* #48 Implement Strings.trimStartEnd(String,char,char)
* #47 Implement Enumerations.getSize(Enumeration)
* #46 Support Long and Double in BigDecimals.intern(...)
* #45 Implement Classes.resolveGenericTypes(...)
* #44 Add Classes.getAllGenericInterfaces(Class)
* #43 Support TypeVariable in getGenericParameters(Type)
* #42 Implement Strings.indexOfScopeClose
* #41 Implement BigDecimalInfinity
* #40 Implement Numbers.Unsigned.toUNIT(BigInteger)
* #39 Support Properties class for variables in Strings.derefEL()
* #38 Add Classes.getAnnotationDeep and Classes.isAnnotationPresentDeep
* #37 Transition to GitHub Actions
* #36 Move BigDecimals and BitIntegers from math module
* #35 Add indexOfIgnoreCase(CharSequence,char) and indexOfIgnoreCase(CharSequence,char) in Strings

## [v0.7.5](https://github.com/libj/lang/compare/7e6fffdf58f8a1d6c6dbc7dbce366e1d2c8b56a8..9ef34c960414e289ffe41251662b5c76ea53e192) (2020-05-23)
* Migrate `Assertions`, `Classes`, `Identifiers`, `Repeat`, `Resources` from `org.libj.util`.
* Add `WrapperProxy`.
* Improve javadocs.

## [v0.7.4](https://github.com/libj/lang/compare/c9ac7e7ce3b3f2b068a9012d8e7ce5ca3e146462..7e6fffdf58f8a1d6c6dbc7dbce366e1d2c8b56a8) (2019-07-21)
* Set visibility of `Sys#OS_NAME` to package private.
* Upgrade `org.libj:test:0.6.9` to `0.7.0`.

## [v0.7.3](https://github.com/entinae/pom/compare/56146e2551b62cd8e8b249e390c617c7499cb00c..c9ac7e7ce3b3f2b068a9012d8e7ce5ca3e146462) (2019-05-13)
* Initial public release.