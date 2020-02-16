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

package gov.nasa.jpf.autodoc.types.parser;

import gov.nasa.jpf.classfile.ClassFileException;
import gov.nasa.jpf.util.FileUtils;
import java.io.PrintWriter;

/**
 * Class that wraps <code>gov.nasa.jpf.classfile.ClassPath</code> extending
 * some of its functionalities.
 * 
 * @author Carlos Uribe
 */
public class ClassPath extends gov.nasa.jpf.classfile.ClassPath {

  /**
   * Add a single pathname to classpath's names collection. It automatically 
   * expands Java 6 classpath wildcards.
   * 
   * @param pathname String containing single classpath element.
   */
  @Override
  public void addPathName(String pathname) {
    addAllPathNames(pathname.split(System.getProperty("path.separator")));
  }

  /**
   * Add all pathnames to classpath's names collection. It automatically 
   * expands Java 6 classpath wildcards.
   * 
   * @param pathnames String array containing all classpath elements.
   */
  public void addAllPathNames(String[] pathnames) {
    String[] expanded = FileUtils.expandWildcards(pathnames);
    for (String cp : expanded) {
      super.addPathName(cp);
    }
  }
  
  public String getSource(String classname) throws ClassFileException {
    for (String pathname : getPathNames()) {
      ClassPath check = new ClassPath();
      check.addPathName(pathname);
      
      if (check.classFound(classname)) {
        return pathname;
      }
    }
    return new String();
  }
  
  /**
   * Indicate whether a class could be found or not in this classpath.
   * 
   * @param classname Named package classname of the class.
   * @return <code>true</code> if classdata is not <code>null</code>.
   * @throws ClassFileException if an error ocurrs when requesting class data.
   */
  public boolean classFound(String classname) throws ClassFileException {
    return super.getClassData(classname) != null;
  }

  /**
   * Print all path names added to the classpath. This method is useful 
   * for debugging operations.
   * 
   * @param pw reference to a <code>PrintWriter</code> to print the code.
   */
  public void printPathNames(PrintWriter pw) {
    pw.println(" -- listing pathnames -- ");
    for (String pn : getPathNames()) {
      pw.println(pn);
    }
  }
}
