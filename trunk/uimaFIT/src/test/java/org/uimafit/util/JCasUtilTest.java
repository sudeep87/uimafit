/*
 Copyright 2009
 Ubiquitous Knowledge Processing (UKP) Lab
 Technische Universitaet Darmstadt
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

 getCoveredAnnotations() contains code adapted from the UIMA Subiterator class.
*/
package org.uimafit.util;

import static org.junit.Assert.*;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static org.uimafit.util.JCasUtil.exists;
import static org.uimafit.util.JCasUtil.getType;
import static org.uimafit.util.JCasUtil.isCovered;
import static org.uimafit.util.JCasUtil.select;
import static org.uimafit.util.JCasUtil.selectCovered;
import static org.uimafit.util.JCasUtil.selectFollowing;
import static org.uimafit.util.JCasUtil.selectPreceding;
import static org.uimafit.util.JCasUtil.selectSingle;
import static org.uimafit.util.JCasUtil.toText;
import static java.util.Arrays.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Test;
import org.uimafit.ComponentTestBase;
import org.uimafit.type.Sentence;
import org.uimafit.type.Token;

/**
 * Test cases for {@link JCasUtil}.
 *
 * @author Richard Eckart de Castilho
 * @author Torsten Zesch
 */
public class JCasUtilTest
	extends ComponentTestBase
{
	/**
	 * Test Tokens (Stems + Lemmas) overlapping with each other.
	 */
	@Test
	public void test1() throws Exception {
		add(jCas, 3, 16);
		add(jCas, 37, 61);
		add(jCas, 49, 75);
		add(jCas, 54, 58);
		add(jCas, 66, 84);

		for (Token t : JCasUtil.iterate(jCas, Token.class)) {
			List<Sentence> stem1 = getCoveredAnnotationsNaive(jCas, Sentence.class, t);
			List<Sentence> stem2 = JCasUtil.selectCovered(jCas, Sentence.class, t);
			check(jCas, t, stem1, stem2);
		}
	}

	/**
	 * Test what happens if there is actually nothing overlapping with the Token.
	 */
	@Test
	public void test2() throws Exception {
		new Sentence(jCas, 3, 31).addToIndexes();
		new Sentence(jCas, 21, 21).addToIndexes();
		new Sentence(jCas, 24, 44).addToIndexes();
		new Sentence(jCas, 30, 45).addToIndexes();
		new Sentence(jCas, 32, 43).addToIndexes();
		new Sentence(jCas, 47, 61).addToIndexes();
		new Sentence(jCas, 48, 77).addToIndexes();
		new Sentence(jCas, 65, 82).addToIndexes();
		new Sentence(jCas, 68, 80).addToIndexes();
		new Sentence(jCas, 72, 65).addToIndexes();

		new Token(jCas, 73, 96).addToIndexes();

		for (Token t : JCasUtil.iterate(jCas, Token.class)) {
			List<Sentence> stem1 = getCoveredAnnotationsNaive(jCas, Sentence.class, t);
			List<Sentence> stem2 = JCasUtil.selectCovered(jCas, Sentence.class, t);
			check(jCas, t, stem1, stem2);
		}
	}

	@Test
	public void testIteration() throws Exception {
		Random rnd = new Random();

		final int ITERATIONS = 10;

		for (int i = 0; i < ITERATIONS; i++) {
			CAS cas = jCas.getCas();
			List<Type> types = new ArrayList<Type>();
			types.add(cas.getTypeSystem().getType(Token.class.getName()));
			types.add(cas.getTypeSystem().getType(Sentence.class.getName()));

			// Shuffle the types
			for (int n = 0; n < 10; n++) {
				Type t = types.remove(rnd.nextInt(types.size()));
				types.add(t);
			}

			// Randomly generate annotations
			for (int n = 0; n < (10 * i) ; n++) {
				for (Type t : types) {
					int begin = rnd.nextInt(100);
					int end = begin+rnd.nextInt(30);
					cas.addFsToIndexes(cas.createAnnotation(t, begin, end));
				}
			}

			JCas jcas = cas.getJCas();
			long t1 = 0;
			long t2 = 0;
			for (Token t : JCasUtil.iterate(jcas, Token.class)) {
				long ti = System.currentTimeMillis();
				List<Sentence> stem1 = getCoveredAnnotationsNaive(jcas, Sentence.class, t);
				t1 += System.currentTimeMillis() - ti;

				ti = System.currentTimeMillis();
				List<Sentence> stem2 = selectCovered(jcas, Sentence.class, t);
				t2 += System.currentTimeMillis() - ti;

				check(jcas, t, stem1, stem2);
			}
			System.out.format("Speed up: n:%d o:%d d:%d (%.2f)\n", t1, t2, t1-t2, (double) t1 / (double) t2);
		}
	}

	@SuppressWarnings("unused")
	private void print(Collection<? extends Annotation> annos) {
		for (Annotation a : annos) {
			System.out.println(a.getClass().getSimpleName()+" "+a.getBegin()+" "+a.getEnd());
		}
	}

	private Token add(JCas jcas, int begin, int end) {
		Token t = new Token(jcas, begin, end);
		t.addToIndexes();
		new Sentence(jcas, begin, end).addToIndexes();
		return t;
	}

	private void check(JCas jcas, Token t, Collection<? extends Annotation> a1,
			Collection<? extends Annotation> a2) {
//		List<Annotation> annos = new ArrayList<Annotation>();
//		FSIterator fs = jcas.getAnnotationIndex().iterator();
//		while (fs.hasNext()) {
//			annos.add((Annotation) fs.next());
//		}
//
//		System.out.println("--- Index");
//		print(annos);
//		System.out.println("--- Container");
//		print(Collections.singleton(t));
//		System.out.println("--- Naive");
//		print(a1);
//		System.out.println("--- Optimized");
//		print(a2);
		assertEquals("Container: ["+t.getBegin()+".."+t.getEnd()+"]", a1, a2);
	}

	/**
	 * The version by Torsten slightly optimized by Richard using the cut-off condition found in the
	 * UIMA subiterator class.
	 */
	@SuppressWarnings( { "unchecked" })
	private static <T extends Annotation> List<T> getCoveredAnnotationsNaive(
			JCas aJCas, Class<? extends Annotation> aType, Annotation aContainer)
	{
		int begin = aContainer.getBegin();
		int end = aContainer.getEnd();
		Type t = aJCas.getTypeSystem().getType(aType.getName());

		List<T> list = new ArrayList<T>();
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex(t).iterator();
		while (iter.hasNext()) {
			T a = (T) iter.next();
			if (a.getBegin() >= begin && a.getEnd() <= end) {
				list.add(a);
			}
			// If the start of the current annotation is past the end parameter,
		    // we're done.
		    if (a.getBegin() > end) {
		        break;
		    }
		}
		return list;
	}

	@Test
	public void testIterator() throws Exception {
		String text = "Rot wood cheeses dew?";
		tokenBuilder.buildTokens(jCas, text);

		Iterator<Token> tokens = JCasUtil.iterator(jCas, Token.class);
		assertTrue(tokens.hasNext());
		assertEquals("Rot", tokens.next().getCoveredText());
		assertTrue(tokens.hasNext());
		assertEquals("wood", tokens.next().getCoveredText());
		assertTrue(tokens.hasNext());
		assertEquals("cheeses", tokens.next().getCoveredText());
		assertTrue(tokens.hasNext());
		assertEquals("dew?", tokens.next().getCoveredText());
		assertFalse(tokens.hasNext());
	}

	@Test
	public void testGet() throws UIMAException {
		String text = "Rot wood cheeses dew?";
		tokenBuilder.buildTokens(jCas, text);

		Token lastToken = JCasUtil.selectByIndex(jCas, Token.class, -1);
		assertEquals("dew?", lastToken.getCoveredText());

		Token firstToken = JCasUtil.selectByIndex(jCas, Token.class, 0);
		assertEquals("Rot", firstToken.getCoveredText());

		lastToken = JCasUtil.selectByIndex(jCas, Token.class, 3);
		assertEquals("dew?", lastToken.getCoveredText());

		firstToken = JCasUtil.selectByIndex(jCas, Token.class, -4);
		assertEquals("Rot", firstToken.getCoveredText());

		Token oobToken = JCasUtil.selectByIndex(jCas, Token.class, -5);
		assertNull(oobToken);

		oobToken = JCasUtil.selectByIndex(jCas, Token.class, 4);
		assertNull(oobToken);

	}

	@Test
	public void testToText() throws UIMAException {
		String text = "Rot wood cheeses dew?";
		tokenBuilder.buildTokens(jCas, text);
		assertEquals(asList(text.split(" ")), toText(select(jCas, Token.class)));
	}

	@Test
	public void testSelectFollowingPreceding() throws UIMAException {
		String text = "one two three";
		tokenBuilder.buildTokens(jCas, text);
		List<Token> token = new ArrayList<Token>(select(jCas, Token.class));

		assertEquals(token.get(0).getCoveredText(),
				selectPreceding(jCas, Token.class, token.get(1), 1).get(0).getCoveredText());
		assertEquals(token.get(2).getCoveredText(),
				selectFollowing(jCas, Token.class, token.get(1), 1).get(0).getCoveredText());
	}

	@Test
	public void testExists() throws UIMAException {
		JCas jcas = CasCreationUtils.createCas(createTypeSystemDescription(), null, null).getJCas();

		assertFalse(exists(jcas, Token.class));

		new Token(jcas, 0, 1).addToIndexes();

		assertTrue(exists(jcas, Token.class));
	}

	@Test
	public void testSelectSingle() throws UIMAException {
		JCas jcas = CasCreationUtils.createCas(createTypeSystemDescription(), null, null).getJCas();

		try {
			selectSingle(jcas, Token.class);
			fail("Found annotation that has not yet been created");
		}
		catch (IllegalArgumentException e) {
			// OK
		}

		new Token(jcas, 0, 1).addToIndexes();

		selectSingle(jcas, Token.class);

		new Token(jcas, 1, 2).addToIndexes();

		try {
			selectSingle(jcas, Token.class);
			fail("selectSingle must fail if there is more than one annotation of the type");
		}
		catch (IllegalArgumentException e) {
			// OK
		}
	}

	@Test
	public void testSelectIsCovered() throws UIMAException {
		String text = "Will you come home today ? \n No , tomorrow !";
		tokenBuilder.buildTokens(jCas, text);

		List<Sentence> sentences = new ArrayList<Sentence>(select(jCas, Sentence.class));
		List<Token> tokens = new ArrayList<Token>(select(jCas, Token.class));

		assertEquals(6, selectCovered(Token.class, sentences.get(0)).size());
		assertEquals(4, selectCovered(Token.class, sentences.get(1)).size());

		assertTrue(isCovered(jCas, sentences.get(0), Token.class));
		tokens.get(0).removeFromIndexes();
		tokens.get(1).removeFromIndexes();
		tokens.get(2).removeFromIndexes();
		tokens.get(3).removeFromIndexes();
		tokens.get(4).removeFromIndexes();
		tokens.get(5).removeFromIndexes();
		assertFalse(isCovered(jCas, sentences.get(0), Token.class));
	}

	@Test
	public void testGetInternalUimaType() {
		Type t = getType(jCas, Annotation.class);
		assertNotNull(t);
	}
}