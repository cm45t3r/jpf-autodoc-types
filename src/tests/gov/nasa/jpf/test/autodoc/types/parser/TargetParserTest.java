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

package gov.nasa.jpf.test.autodoc.types.parser;

import gov.nasa.jpf.autodoc.types.parser.Parser;
import java.util.List;
import gov.nasa.jpf.autodoc.types.parser.ClassPath;
import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.autodoc.types.parser.TargetParser;
import java.util.ArrayList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Carlos Uribe
 */
public class TargetParserTest {

  public TargetParserTest() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }
  
  /**
   * Test of parse(ArrayList<String>) method, of class TargetParser.
   */
  @Test
  public void testParse_ArrayList_String() throws Exception {
    System.out.println("method> parse(ArrayList<String>)");

    List<String> list = new ArrayList<String>();
    list.add("build/main/gov/nasa/jpf/autodoc/types/AutoDocTool.class");
    list.add("build/main/gov/nasa/jpf/autodoc/types/parser/Parser.class");
    list.add("build/main/gov/nasa/jpf/autodoc/types/parser/TargetParser.class");
    list.add("build/main/gov/nasa/jpf/autodoc/types/parser/ClassFileParser.class");

    Parser parser = new TargetParser();
    List<ClassInfo> classes = parser.parse(list);
    assertFalse(classes.isEmpty());

    int i = 0;
    for (ClassInfo info : classes) {
      System.out.println("  [" + i + "] " + info.getName());
      ++i;
    }
  }
  
  /**
   * Test of parse(String) method, of class TargetParser.
   */
  @Test
  public void testParse_String() throws Exception {
    System.out.println("method> parse(String)");
    
    String clazz = "build/main/gov/nasa/jpf/autodoc/types/AutoDocTool.class";

    TargetParser parser = new TargetParser();
    ClassInfo info = parser.parse(clazz);
    assertEquals("gov/nasa/jpf/autodoc/types/AutoDocTool", info.getName());
    System.out.println("  [" + 0 + "] class: " + info.getName() 
                       + " | flags: " + info.getFlags());
  }

  /**
   * Test of parse(String, String[]) method, of class TargetParser.
   */
  @Test
  public void testParse_String_StringArr() throws Exception {
    System.out.println("method> parse(String, String[])");
    
    String clazz = "gov.nasa.jpf.autodoc.types.AutoDocTool";
    String[] pathnames = new String[]{"build/jpf-autodoc-types.jar"};

    TargetParser parser = new TargetParser();
    ClassInfo info = parser.parse(clazz, pathnames);
    assertEquals("gov/nasa/jpf/autodoc/types/AutoDocTool", info.getName());
    System.out.println("  [" + 0 + "] class: " + info.getName() 
                       + " | flags: " + info.getFlags());
  }

  /**
   * Test of parseBytes method, of class TargetParser.
   */
  @Test
  public void testParseBytes() throws Exception {
    System.out.println("method> parseBytes");
    
    ClassPath cp = new ClassPath();
    cp.addPathName("build/jpf-autodoc-types.jar");

    ArrayList<byte[]> data = new ArrayList<byte[]>();
    data.add(cp.getClassData("gov.nasa.jpf.autodoc.types.AutoDocTool"));
    data.add(cp.getClassData("gov.nasa.jpf.autodoc.types.parser.TargetParser"));
    data.add(cp.getClassData("gov.nasa.jpf.autodoc.types.parser.ClassFileParser"));
    data.add(cp.getClassData("gov.nasa.jpf.autodoc.types.parser.Parser"));

    TargetParser parser = new TargetParser();
    List<ClassInfo> classes = parser.parseBytes(data);
    assertFalse(classes.isEmpty());

    int i = 0;
    for (ClassInfo info : classes) {
      System.out.println("  [" + i + "] " + info.getName());
      ++i;
    }
  }
}
