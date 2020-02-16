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

package gov.nasa.jpf.test.autodoc.types;

import java.io.PrintWriter;
import java.io.FileInputStream;
import gov.nasa.jpf.autodoc.types.ConfigLoader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case for ConfigLoader
 * 
 * @author Carlos Uribe
 */
public class ConfigLoaderTest {
  
  public ConfigLoaderTest() {
  }
  
  /**
   * Test of loadConfig method, of class ConfigLoader.
   */
  @Test
  public void testLoadConfig() throws Exception {
    System.out.println("method> loadConfig");
    
    String filepath = "config.properties";
    
    ConfigLoader cl = new ConfigLoader();
    cl.loadConfig(filepath);
    assertFalse(cl.isEmpty());
  }
  
  /**
   * Test of expandTerms method, of class ConfigLoader.
   */
  @Test
  public void testExpandTerms() throws Exception {
    System.out.println("method> expandTerms");
    
    String filepath = "config.properties";
    ConfigLoader cl = new ConfigLoader();
    cl.load(new FileInputStream(filepath));
    
    cl.expandTerms();
    cl.list(new PrintWriter(System.out, true));
  }
  
  /**
   * Test of isExpandable, of class ConfigLoader.
   */
  @Test
  public void testIsExpandable() throws Exception {
    System.out.println("method> isExpandable");
    
    String expression = "${expandable}";
    String badexpression = "$[nonexpandable}";
    
    ConfigLoader cl = new ConfigLoader();
    assertTrue(cl.isExpandable(expression));
    assertFalse(cl.isExpandable(badexpression));
  }
  
  /**
   * Test of isLeaf, of class ConfigLoader.
   */
  @Test
  public void testIsLeaf() throws Exception {
    System.out.println("method> isLeaf");
    
    ConfigLoader cl = new ConfigLoader();
    cl.setProperty("property", "value");
    cl.setProperty("property.isleaf", "true");
    cl.setProperty("noleaf", "value");
    cl.setProperty("noleaf.isleaf", "false");
    cl.setProperty("singleprop", "value");
    
    assertTrue(cl.isLeaf("property"));
    assertFalse(cl.isLeaf("noleaf"));
    assertFalse(cl.isLeaf("singleprop"));
  }
  
  /**
   * Test of getType, of class ConfigLoader.
   */
  @Test
  public void testGetType() throws Exception {
    System.out.println("method> getType");
    
    String key = "PropertyListenerAdapter";
    String expType = "Listener";
    String filepath = "config.properties";
    
    ConfigLoader cl = new ConfigLoader();
    cl.load(new FileInputStream(filepath));
    assertEquals(expType, cl.getType(key));
  }
}
