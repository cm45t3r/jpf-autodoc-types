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

import gov.nasa.jpf.autodoc.types.parser.ClassPath;
import gov.nasa.jpf.autodoc.types.parser.ClassFileParser;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case for ClassFileParser.
 * 
 * @author Carlos Uribe
 */
public class ClassFileParserTest {

  public ClassFileParserTest() {
  }

  /**
   * Test of parse(String) method, of class ClassFileParser.
   */
  @Test
  public void testParse_String() throws Exception {
    System.out.println("method> parse(String)");

    ClassFileParser cfparser = new ClassFileParser();
    File testFolder = new File("test");
    
    int i = 0;
    for (String filename : testFolder.list()) {
      cfparser.parse(testFolder.getPath() + File.separator + filename);

      assertFalse(cfparser.getClassInfo().getName().isEmpty());
      System.out.print("  [" + i + "] class: " + cfparser.getClassInfo()
                       .getName());
      //--- No assertion for superClsName. It could be empty.
      assertFalse(cfparser.getClassInfo().getFlags() == -1);
      System.out.println(" | flags: " + cfparser.getClassInfo().getFlags());
      //--- No assertion for interfaces list . It could be empty.
      //--- No assertion for methods list. It could be empty.
      //--- No assertion for layer. It could be empty.
      ++i;
    }
  }

  /**
   * Test of parse(String, String[]) method, of class ClassFileParser.
   */
  @Test
  public void testParse_String_StringArr() throws Exception {
    System.out.println("method> parse(String, String[])");

    ClassFileParser cfparser = new ClassFileParser();
    String classname = "gov.nasa.jpf.autodoc.types.parser.ClassFileParser";
    String[] pathnames = new String[]{"build/main"};

    cfparser.parse(classname, pathnames);

    assertFalse(cfparser.getClassInfo().getName().isEmpty());
    System.out.print("  [0] class: " + cfparser.getClassInfo().getName());
    //--- No assertion for superClsName. It could be empty.
    assertFalse(cfparser.getClassInfo().getFlags() == -1);
    System.out.println(" | flags: " + cfparser.getClassInfo().getFlags());
    //--- No assertion for interfaces list . It could be empty.
    //--- No assertion for methods list. It could be empty.
    //--- No assertion for layer. It could be empty.
  }

  /**
   * Test of parse(String, ClassPath) method, of class ClassFileParser.
   */
  @Test
  public void testParse_String_ClassPath() throws Exception {
    System.out.println("method> parse(String, ClassPath)");

    ClassFileParser cfparser = new ClassFileParser();
    String classname = "gov.nasa.jpf.autodoc.types.parser.ClassFileParser";
    ClassPath classpath = new ClassPath();

    classpath.addPathName("build/main");
    cfparser.parse(classname, classpath);

    assertFalse(cfparser.getClassInfo().getName().isEmpty());
    System.out.print("  [0] class: " + cfparser.getClassInfo().getName());
    //--- No assertion for superClsName. It could be empty.
    assertFalse(cfparser.getClassInfo().getFlags() == -1);
    System.out.println(" | flags: " + cfparser.getClassInfo().getFlags());
    //--- No assertion for interfaces list . It could be empty.
    //--- No assertion for methods list. It could be empty.
    //--- No assertion for layer. It could be empty.
  }

  /**
   * Test of parse(byte[]) method, of class ClassFileParser.
   */
  @Test
  public void testParse_byteArr() throws Exception {
    System.out.println("method> parse(byte[])");

    ClassFileParser cfparser = new ClassFileParser();
    ClassPath cp = new ClassPath();

    cp.addPathName("build/main");
    cfparser.parse(cp.getClassData("gov.nasa.jpf.autodoc.types.parser"
                                   + ".ClassFileParser"));

    assertFalse(cfparser.getClassInfo().getName().isEmpty());
    System.out.print("  [0] class: " + cfparser.getClassInfo().getName());
    //--- No assertion for superClsName. It could be empty.
    assertFalse(cfparser.getClassInfo().getFlags() == -1);
    System.out.println(" | flags: " + cfparser.getClassInfo().getFlags());
    //--- No assertion for interfaces list . It could be empty.
    //--- No assertion for methods list. It could be empty.
    //--- No assertion for layer. It could be empty.
  }
  
  /**
   * Test of inferLayer(String) method, of class ClassFileParser.
   */
  @Test
  public void testInferLayer_String() throws Exception {
    System.out.println("method> inferLayer(String)");

    ClassFileParser cfparser = new ClassFileParser();
    String layer = cfparser.inferLayer("build/main/gov/nasa/jpf/autodoc/types/"
            + "parser/ClassFileParser.class");
    
    assertEquals("main", layer);
    System.out.println("  layer: " + layer);
  }
  
  /**
   * Test of inferLayer(String, ClassPath) method, of class ClassFileParser.
   */
  @Test
  public void testInferLayer_ClassPath() throws Exception {
    System.out.println("method> inferLayer(String, ClassPath)");

    ClassFileParser cfparser = new ClassFileParser();
    ClassPath classpath = new ClassPath();

    String classname = "gov.nasa.jpf.autodoc.types.AutoDocTool";
    
    classpath.addPathName("build/main");
    String layer = cfparser.inferLayer(classname, classpath);
    
    assertEquals("main", layer);
    System.out.println("  layer: " + layer);
  }
}
