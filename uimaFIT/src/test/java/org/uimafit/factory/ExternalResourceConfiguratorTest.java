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
*/

package org.uimafit.factory;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDependency;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.junit.Test;
import org.uimafit.factory.ExternalResourceConfigurator;
import org.uimafit.factory.testAes.ParameterizedAE2;

import static org.uimafit.factory.AnalysisEngineFactory.*;
import static org.uimafit.factory.ExternalResourceConfigurator.*;
import static org.uimafit.factory.TypeSystemDescriptionFactory.*;

/**
 * Test the {@link ExternalResourceConfigurator}.
 *
 * @author Richard Eckart de Castilho
 */
public class ExternalResourceConfiguratorTest
{
	@Test
	public void testAnalyze() throws Exception
	{
		Map<String, ExternalResourceDependency> deps = getResourceDeclarations(ParameterizedAE2.class);

		verify(deps);
	}

	@Test
	public void testDescriptor() throws Exception
	{
		TypeSystemDescription tsd = createTypeSystemDescription(new Class<?>[0]);
		AnalysisEngineDescription desc = createPrimitiveDescription(ParameterizedAE2.class, tsd);

		Map<String, ExternalResourceDependency> deps = new HashMap<String, ExternalResourceDependency>();
		for (ExternalResourceDependency dep : desc.getExternalResourceDependencies()) {
			deps.put(dep.getKey(), dep);
		}

		verify(deps);
	}

	private void verify(Map<String, ExternalResourceDependency> deps)
	{
		assertEquals(3, deps.size());

		String key = ParameterizedAE2.DummyResource.class.getName();
		String api = ParameterizedAE2.DummyResource.class.getName();
		ExternalResourceDependency d = deps.get(key);
		assertEquals(key, d.getKey());
		assertEquals(api, d.getInterfaceName());
		assertEquals(false, d.isOptional());

		key = ParameterizedAE2.RES_OTHER;
		d = deps.get(key);
		assertEquals(key, d.getKey());
		assertEquals(api, d.getInterfaceName());
		assertEquals(false, d.isOptional());

		key = ParameterizedAE2.RES_OPTIONAL;
		d = deps.get(key);
		assertEquals(key, d.getKey());
		assertEquals(api, d.getInterfaceName());
		assertEquals(true, d.isOptional());
	}
}