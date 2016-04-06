uimaFIT 1.3.1 is a minor feature release. It should be binary backwards compatible with 1.3.0 and provides new features some of which we introduce next. For a complete list of changes see the [CHANGES](https://uimafit.googlecode.com/svn/tags/uimafit-parent-1.3.1/uimaFIT/CHANGES) file

## Nested external resources ##

Resources can now be injected into other resources.

```
AnalysisEngineDescription descriptor = AnalysisEngineFactory.createPrimitiveDescription(
  TestAnalysisEngine2.class,
  TestAnalysisEngine2.PARAM_RESOURCE, createExternalResourceDescription(
    TestExternalResource2.class,
    TestExternalResource2.PARAMETER, "value",
    TestExternalResource2.PARAM_RESOURCE, createExternalResourceDescription(
      TestExternalResource.class,
      TestExternalResource.PARAMETER, "value")));
```

Nested resources are only initialized if they are used in a pipeline which contains at least one component that calls  `ConfigurationParameterInitializer.initialize()`. Any component extending uimaFIT's component base classes qualifies. If you use nested resources in a pipeline without any uimaFIT-aware components, you can just add uimaFIT's `NoopAnnotator` to the pipeline.