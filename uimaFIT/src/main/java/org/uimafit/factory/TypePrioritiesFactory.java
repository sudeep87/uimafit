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

package org.uimafit.factory;

import org.apache.uima.resource.metadata.TypePriorities;
import org.apache.uima.resource.metadata.TypePriorityList;
import org.apache.uima.resource.metadata.impl.TypePriorities_impl;

/**
 * @author Philip Ogren
 */

public class TypePrioritiesFactory {

	public static TypePriorities createTypePriorities(String[] prioritizedTypeNames) {
		TypePriorities typePriorities = new TypePriorities_impl();
		TypePriorityList typePriorityList = typePriorities.addPriorityList();
		for (String  typeName : prioritizedTypeNames) {
			typePriorityList.addType(typeName);	
		}
		return typePriorities;
	}
}