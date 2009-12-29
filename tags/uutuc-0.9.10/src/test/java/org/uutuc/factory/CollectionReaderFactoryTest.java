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
package org.uutuc.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Progress;
import org.junit.Test;
import org.uutuc.type.Token;
import org.uutuc.util.AnnotationRetrieval;
import org.uutuc.util.JCasIterable;
import org.uutuc.util.SingleFileXReader;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class CollectionReaderFactoryTest {

	@Test
	public void testCreateCollectionReader() throws UIMAException, IOException {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription("org.uutuc.type.TypeSystem");
		CollectionReader reader = CollectionReaderFactory.createCollectionReader(SingleFileXReader.class, 
				typeSystemDescription, SingleFileXReader.PARAM_FILE_NAME, "src/test/resources/data/docs/test.xmi",
				SingleFileXReader.PARAM_XML_SCHEME, SingleFileXReader.XMI);
		
		JCasIterable jCasIterable = new JCasIterable(reader, typeSystemDescription);
		JCas jCas = jCasIterable.next();
		assertNotNull(jCas);
		assertEquals("Me and all my friends are non-conformists.", jCas.getDocumentText());
		Token token = AnnotationRetrieval.get(jCas, Token.class, 2);
		assertEquals("all", token.getCoveredText());
		assertEquals("A", token.getPos());
		assertEquals("all", token.getStem());

		
		reader = CollectionReaderFactory.createCollectionReader("org.uutuc.util.SingleFileXReader", 
				SingleFileXReader.PARAM_FILE_NAME, "src/test/resources/data/docs/test.xmi",
				SingleFileXReader.PARAM_XML_SCHEME, SingleFileXReader.XMI);

		jCasIterable = new JCasIterable(reader, typeSystemDescription);
		jCas = jCasIterable.next();
		assertNotNull(jCas);
		assertEquals("Me and all my friends are non-conformists.", jCas.getDocumentText());
		token = AnnotationRetrieval.get(jCas, Token.class, 9);
		assertEquals(".", token.getCoveredText());
		assertEquals(".", token.getPos());
		assertEquals(".", token.getStem());

		
		reader = CollectionReaderFactory.createCollectionReaderFromPath("src/main/resources/org/uutuc/util/SingleFileXReader.xml", 
				SingleFileXReader.PARAM_FILE_NAME, "src/test/resources/data/docs/test.xmi",
				SingleFileXReader.PARAM_XML_SCHEME, SingleFileXReader.XMI);

		jCasIterable = new JCasIterable(reader, typeSystemDescription);
		jCas = jCasIterable.next();
		assertNotNull(jCas);
		assertEquals("Me and all my friends are non-conformists.", jCas.getDocumentText());
		token = AnnotationRetrieval.get(jCas, Token.class, 4);
		assertEquals("friends", token.getCoveredText());
		assertEquals("F", token.getPos());
		assertEquals("friend", token.getStem());

	}
	
	@Test
	public void testExceptions() throws ResourceInitializationException {
		ResourceInitializationException rie = null;
		try {
			CollectionReaderFactory.createCollectionReader(TestCR.class, null);
		} catch(ResourceInitializationException e) {
			rie = e;
		}
		assertNotNull(rie);

	}
	
	private class TestCR extends CollectionReader_ImplBase {

		private TestCR() {
			
		}
		public void getNext(CAS acas) throws IOException, CollectionException {
			// TODO Auto-generated method stub
			
		}

		public void close() throws IOException {
			// TODO Auto-generated method stub
			
		}

		public Progress[] getProgress() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasNext() throws IOException, CollectionException {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}