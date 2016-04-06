<h1>Pipelines</h1>

UIMA is a component-based architecture that allows to compose various processing components into a complex processing pipeline. A pipeline typically involves a `CollectionReader` which ingests
documents and `AnalysisEngines` that do the actual processing.

Normally, you would run a pipeline using a UIMA Collection Processing Engine or using UIMA AS.
uimaFIT offers a third alternative that is much simpler to use and well suited for embedding
UIMA pipelines into applications or for writing tests.

As uimaFIT does not supply any readers or processing components, we just assume that we have written three components:

  * TextReader - reads text files from a directory
  * Tokenizer - annotates tokens
  * TokenFrequencyWriter - writes a list of tokens and their frequency to a file

We create descriptors for all components and run them as a pipeline:

```
CollectionReaderDescription reader = CollectionReaderFactory.createDescription(
  TextReader.class, TextReader.PARAM_INPUT, "/home/uimafit/documents");

AnalysisEngineDescription tokenizer = AnalysisEngineFactory.createPrimitiveDescription(
  Tokenizer.class);

AnalysisEngineDescription tokenFrequencyWriter = AnalysisEngineFactory.createPrimitiveDescription(
  TokenFrequencyWriter.class, TokenFrequencyWriter.PARAM_OUTPUT, "counts.txt");

SimplePipeline.runPipeline(reader, tokenizer, writer);
```