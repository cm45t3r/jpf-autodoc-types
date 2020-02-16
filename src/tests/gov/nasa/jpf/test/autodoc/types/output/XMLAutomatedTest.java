//
// Copyright (C) 2011 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.test.autodoc.types.output;

import gov.nasa.jpf.autodoc.types.ConfigLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.SAXException;

/**
 * Automated test for XML files comparison.
 */
public class XMLAutomatedTest extends XMLTestCase {

  public XMLAutomatedTest(String name) {
    super(name);
  }

  public void testEqual() throws SAXException, IOException {
    System.out.println("> Automated XML comparison Test.");

    String test = "src/tests/gov/nasa/jpf/test/autodoc/types/output/";
    ConfigLoader cfg = new ConfigLoader();
    cfg.loadConfig("xml-test.properties");

    try {
      String[] prop = cfg.getPropertyVals("files");

      for (String file : prop) {
        File ref = new File(file);
        String reference = load(test + ref.getName());
        System.out.println("reference file: " + test + ref.getName());
        String target = load(file);
        System.out.println("target file: " + file);
        
        String[] ignore = new String[]{"project", "location"};

        reference = ignoreSingleTags(reference, ignore);
        target = ignoreSingleTags(target, ignore);

        Diff diff = new Diff(reference, target);
        assertXMLEqual(diff, true);
      }

    } catch (NullPointerException ex) {
      System.out.println("nothing to do.");
    }
  }

  private String load(String path) throws FileNotFoundException, IOException {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    String line;
    StringBuilder stringBuilder = new StringBuilder();
    String ls = System.getProperty("line.separator");

    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line);
      stringBuilder.append(ls);
    }

    return stringBuilder.toString();
  }

  private String ignoreSingleTags(String XMLText, String[] elementsIDs) {
    String newXML = XMLText;

    for (String id : elementsIDs) {
      String empty = "<" + id + "/>";

      if (newXML.contains(empty)) {
        newXML = newXML.replace(empty, "");
      }

      String single = "<" + id + ">";

      while (newXML.contains(single)) {
        String term = "</" + id + ">";
        int start = newXML.indexOf(single);
        int end = newXML.indexOf(term) + term.length();
        newXML = newXML.replace(newXML.substring(start, end), "");
      }
    }

    return newXML;
  }
}
