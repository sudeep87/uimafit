/* 
 Copyright 2009 Regents of the University of Colorado.  
 All rights reserved. 

 Licensed under the Apache License, Version 2.0 (the "License"); 
 you may not use this file except in compliance with the License. 
 You may obtain a copy of the License at 

 http://www.apache.org/licenses/LICENSE-2.0 

 Unless required by applicable law or agreed to in writing, software 
 distributed under the License is distributed on an "AS IS" BASIS, 
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 See the License for the specific language governing permissions and 
 limitations under the License.
 */

package org.uimafit.testing.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.factory.AnnotationFactory;

/**
 * @author Steven Bethard, Philip Ogren
 *
 * @param <TOKEN_TYPE>
 * @param <SENTENCE_TYPE>
 */

public class TokenBuilder <TOKEN_TYPE  extends Annotation, SENTENCE_TYPE extends Annotation> {

	private Class<TOKEN_TYPE> tokenClass;
	private Class<SENTENCE_TYPE> sentenceClass;
	private String posFeatureName;
	private String stemFeatureName;
	
	public TokenBuilder(Class<TOKEN_TYPE> tokenClass, Class<SENTENCE_TYPE> sentenceClass) {
		this.tokenClass = tokenClass;
		this.sentenceClass = sentenceClass;
	}

	public TokenBuilder(Class<TOKEN_TYPE> tokenClass, Class<SENTENCE_TYPE> sentenceClass, String posFeatureName, String stemFeatureName) {
		this.tokenClass = tokenClass;
		this.sentenceClass = sentenceClass;
		this.posFeatureName = posFeatureName;
		this.stemFeatureName = stemFeatureName;
	}

	public void buildTokens(JCas jCas, String text) throws UIMAException {
		if(text == null) {
			throw new IllegalArgumentException("text may not be null.");
		}
		buildTokens(jCas, text, text, null, null);
	}
	
	public void buildTokens(JCas jCas, String text, String tokensString) throws UIMAException {
		if(tokensString == null) {
			throw new IllegalArgumentException("tokensText may not be null.");
		}
		buildTokens(jCas, text, tokensString, null, null);
	}

	public void buildTokens(JCas jCas, String text, String tokensString, String posTagsString) throws UIMAException {
		buildTokens(jCas, text, tokensString, posTagsString, null);
	}

	public void buildTokens(JCas jCas, String text, String tokensString, String posTagsString, String stemsString) throws UIMAException {
		jCas.setDocumentText(text);
		
		if(posTagsString != null && posFeatureName == null) { 
			throw new IllegalArgumentException("posTagsString must be null if TokenBuilder is not initialized with a feature name corresponding to the part-of-speech feature of the token type (assuming your token type has such a feature).");
		}

		if(stemsString != null && stemFeatureName == null) { 
			throw new IllegalArgumentException("stemsString must be null if TokenBuilder is not initialized with a feature name corresponding to the part-of-speech feature of the token type (assuming your token type has such a feature).");
		}

		Feature posFeature = null;
		if (posFeatureName != null) {
//			String fullPosFeatureName = tokenClass.getClass().getName()+":"+posFeatureName;
//			posFeature = jCas.getTypeSystem().getFeatureByFullName(fullPosFeatureName);
			posFeature = jCas.getTypeSystem().getType(tokenClass.getName()).getFeatureByBaseName(posFeatureName);
		}
		Feature stemFeature = null;
		if (stemFeatureName != null) {
			stemFeature = jCas.getTypeSystem().getType(tokenClass.getName()).getFeatureByBaseName(stemFeatureName);
		}

		tokensString = tokensString.replaceAll("\r\n", "\\n");
		String[] sentenceStrings = tokensString.split("\n");
		String[] posTags = posTagsString != null ? posTagsString.split("\\s+") : null;
		String[] stems = stemsString != null ? stemsString.split("\\s+") : null;
		
		int offset = 0;
		int tokenIndex = 0;

		for (String sentenceString : sentenceStrings) {
			String[] tokenStrings = sentenceString.trim().split("\\s+");
			List<Annotation> tokenAnnotations = new ArrayList<Annotation>();
			for (String tokenString : tokenStrings) {
				// move the offset up to the beginning of the token
				while (!text.startsWith(tokenString, offset)) {
					offset++;
					if (offset > text.length()) {
						throw new IllegalArgumentException(String.format("unable to find string %s", tokenString));
					}
				}

				// add the Token
				int start = offset;
				offset = offset + tokenString.length();
				Annotation token = AnnotationFactory.createAnnotation(jCas, start, offset, tokenClass);
				tokenAnnotations.add(token);

				// set the stem and part of speech if present
				if (posTags != null) {
					token.setStringValue(posFeature, posTags[tokenIndex]);
				}
				if (stems != null) {
					token.setStringValue(stemFeature, stems[tokenIndex]);
				}
				tokenIndex++;
			}
			if (tokenAnnotations.size() > 0) {
				int begin = tokenAnnotations.get(0).getBegin();
				int end = tokenAnnotations.get(tokenAnnotations.size() - 1).getEnd();
				AnnotationFactory.createAnnotation(jCas, begin, end, sentenceClass);
			}
		}
		
	}

	
}