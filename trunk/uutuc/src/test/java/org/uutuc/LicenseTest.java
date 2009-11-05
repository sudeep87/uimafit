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
package org.uutuc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.uima.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.uutuc.util.io.Files;

/**
 * @author Steven Bethard, Philip Ogren
 */

public class LicenseTest {

	@Test
	public void testLicenseStatedInSource() throws Exception {
		test(new File("src/main/java"));
	}

	@Test
	public void testLicenseStatedInTestSource() throws Exception {
		test(new File("src/test/java"));

	}

	private void test(File directory) throws IOException {
		List<String> filesMissingLicense = new ArrayList<String>();

		Iterable<File> files = Files.getFiles(directory, new String[] { ".java" });

		for (File file : files) {
			if (file.getParentFile().getName().equals("type") || file.getName().equals("Files.java")) continue;

			String fileText = FileUtils.file2String(file);

			if (fileText.indexOf("Copyright") == -1 ||
					fileText.indexOf("Licensed under the Apache License, Version 2.0") == -1
					|| fileText.indexOf("@author") == -1) {
				filesMissingLicense.add(file.getPath());
			}
		}

		
		if (filesMissingLicense.size() > 0) {
			String message = String.format("%d source file missing license or author attribution: ",
					filesMissingLicense.size());
			System.err.println(message);
			Collections.sort(filesMissingLicense);
			for (String path : filesMissingLicense) {
				System.err.println(path);
			}
			Assert.fail(message);
		}
	}


}