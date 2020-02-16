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

package gov.nasa.jpf.autodoc.types.analyzer;

import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.autodoc.types.info.CollectedInfo;
import gov.nasa.jpf.autodoc.types.info.JPFInfo;
import gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException;
import gov.nasa.jpf.autodoc.types.parser.ClassPath;
import gov.nasa.jpf.autodoc.types.parser.Parser;
import gov.nasa.jpf.autodoc.types.parser.TargetParser;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class that serves as an abstraction for analyzers.
 * 
 * @author Carlos Uribe
 */
public abstract class InfoAnalyzer {

  protected boolean found = false;
  protected AnalysisTrigger trigger;
  protected ClassPath classpath = new ClassPath();
  protected Parser parser = new TargetParser();

  /**
   * Check if analyzed <code>ClassInfo</code> matches the component and could
   * be obtained extension mechanism information.
   * 
   * @return true if <code>ClassInfo</code> is a component.
   */
  public boolean wasFound() {
    return found;
  }
  
  public void addPathName(String pathname) {
    classpath.addPathName(pathname);
  }
  
  public void addPathNames(String[] pathnames) {
    classpath.addAllPathNames(pathnames);
  }
  
  public void setClassPath(ClassPath classpath) {
    this.classpath = classpath;
  }
  
  public boolean isLocationValid(ClassInfo classInfo, String location) {
    return classInfo.getLayer().equals(location);
  }
  
  /**
   * Analyze <code>classinfo</code> and found the associated component to it.
   */
  public abstract void analyze(ClassInfo clsinfo, CollectedInfo info)
          throws ClassFileNotFoundException;

  /**
   * Get analysis result.
   */
  public abstract JPFInfo getResult();
  
  public Set<String> matchMethods(JPFInfo cls0, JPFInfo cls1) {
    return matchMethods(cls0.getInfo(), cls1.getInfo());
  }
  
  /**
   * Find methods with same name and descriptor from two classes in order
   * to detect method implementations of one class to another.
   * 
   * @param cls0 Parsed classinfo 0.
   * @param cls1 Parsed classinfo 1.
   * @return a list of coincident methods.
   */
  public Set<String> matchMethods(ClassInfo cls0, ClassInfo cls1) {
    return matchMethods(cls0.getMethods(), cls1.getMethods());
  }

  /**
   * Find methods with same name and descriptor from two classes in order
   * to detect method implementations of one class to another.
   */
  public Set<String> matchMethods(Set<String> meths0, Set<String> meths1) {
    Set<String> same = new LinkedHashSet<String>();

    for (String m0 : meths0) {
      for (String m1 : meths1) {
        if (m0.equals(m1) && !same.contains(m0)) {
          same.add(m0);
        }
      }
    }
    return same;
  }
}
