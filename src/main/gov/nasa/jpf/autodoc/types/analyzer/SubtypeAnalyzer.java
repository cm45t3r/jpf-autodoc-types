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

import gov.nasa.jpf.autodoc.types.info.SubtypeInfo;
import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.autodoc.types.info.CollectedInfo;
import gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException;
import gov.nasa.jpf.classfile.ClassFileException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class to analyze hierarchy relationships between classes and components.
 * 
 * @author Carlos Uribe
 */
public class SubtypeAnalyzer extends InfoAnalyzer {

  private SubtypeInfo subtype = new SubtypeInfo();
  private Set<String> buffer = new LinkedHashSet<String>();
  
  public SubtypeAnalyzer(AnalysisTrigger trigger) {
    this.trigger = trigger;
  }
  
  @Override
  public SubtypeInfo getResult() {
    return subtype;
  }
  
  @Override
  public void analyze(ClassInfo clsinfo, CollectedInfo info)
          throws ClassFileNotFoundException {
    subtype = new SubtypeInfo();
    String parent = clsinfo.getSuperName();
    buffer.add(parent);
    SubtypeInfo type = loadType(parent, info);
    
    if (type != null) {
      subtype.setInfo(clsinfo);
      subtype.setType(type.getType());
      subtype.addAncestors(buffer);
      subtype.addAncestors(type.getAncestors());
      subtype.setSuperMethods(matchMethods(subtype, type));
      found = true;
    } else {
      found = false;
    }
    buffer.clear();
  }

  public SubtypeInfo loadType(String name, CollectedInfo info) {
    if (info.isSubtype(name)) {
      return info.getSub(name);
    }
    
    if (info.isType(name)) {
      return info.getType(name);
    }
    
    try {
      ClassInfo cls = new ClassInfo();
      
      if (info.isParsed(name)) {
        cls = info.getCls(name);
      } else {
        cls = parser.parse(name, classpath);
      }
      return trigger.analyzeSubtype(cls, info);
    } catch (NullPointerException ex) {
    } catch (ClassFileNotFoundException ex) {
    } catch (ClassFileException ex) {
      ex.printStackTrace(System.err);
    }
    return null;
  }
}
