uimaFIT 1.4.0 is a feature release. It should is source backwards compatible but not binary backwards compatible with 1.3.1 (see [issue 104](https://code.google.com/p/uimafit/issues/detail?id=104)) and provides new features some of which we introduce next. For a complete list of changes see the [CHANGES](https://uimafit.googlecode.com/svn/tags/uimafit-parent-1.4.0/uimaFIT/CHANGES) file

## Non-standard parameter types support ##

It is now possible to use parameter values other than the basic ones supported by UIMA (String, int, float, boolean) when setting parameter values with the uimaFIT factory methodes. For example, before you needed to write

```
	createPrimitive(MyComponent.class, PARAM_INPUT_FILE, new File("myfile").getPath());
```

now you can write

```
	createPrimitive(MyComponent.class, PARAM_INPUT_FILE, new File("myfile"));
```

In the background, uimaFIT uses the Java PropertyEditor framework and Spring to perform conversions of values unsupported by UIMA to strings. This should work out-of-the-box for types that come with a String-only constructor, for any primitive types, for enums, and for a number of other types (e.g. Locale). Also collections and array of these types are supported.

## Multi-threaded pipeline support ##

uimaFIT now has a very basic support for running multi-threaded UIMA CPE pipelines. The simplest way to use the new feature is the CpePipeline class. Forthcoming versions of uimaFIT are likely to further improve on this feature and offer more configuration options.

## New select methods ##

New select methods have been added:

  * selectAll - select all annotations/feature structures from as JCas/CAS.
  * selectRelative - select a single annotation relative to another annotation.