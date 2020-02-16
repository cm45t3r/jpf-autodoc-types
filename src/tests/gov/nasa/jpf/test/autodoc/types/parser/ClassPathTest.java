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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case for ClassPath.
 * 
 * @author Carlos Uribe
 */
public class ClassPathTest {
  
  public ClassPathTest() {
  }

  /**
   * Test of addAllPathNames method, of class ClassPath.
   */
  @Test
  public void testAddAllPathNames() {
    System.out.println("method> addAllPathNames");
    
    String dir = "build" + System.getProperty("file.separator");
    String[] pathnames = new String[]{dir + "jpf-autodoc-types.jar",
                                      dir + "main", dir + "tests"};
    
    ClassPath cp = new ClassPath();
    cp.addAllPathNames(pathnames);
    
    for (String s : cp.getPathNames()) {
      System.out.println("  " + s);
    }
    
    assertArrayEquals(pathnames, cp.getPathNames());
  }

  /**
   * Test of classFound method, of class ClassPath.
   */
  @Test
  public void testClassFound() throws Exception {
    System.out.println("method> classFound");
    
    String classname = "gov.nasa.jpf.autodoc.types.AutoDocTool";
    String badclassname = "gov.nasa.jpf.unexistent.FakeBadClass";
    
    ClassPath cp = new ClassPath();
    cp.addPathName("build/jpf-autodoc-types.jar");
    assertTrue(cp.classFound(classname));
    assertFalse(cp.classFound(badclassname));
  }
  
  /**
   * Test of getSource method, of class ClassPath.
   */
  @Test
  public void testGetSource() throws Exception {
    System.out.println("method> getSource");
    
    String classname = "gov.nasa.jpf.autodoc.types.AutoDocTool";
    
    ClassPath cp = new ClassPath();
    cp.addPathName("lib/jpf.jar");
    cp.addPathName("build/tests");
    cp.addPathName("build/main/");
    
    String source = cp.getSource(classname);
    System.out.println("  source: " + source);
    assertTrue(source.endsWith("main"));
  }
}
