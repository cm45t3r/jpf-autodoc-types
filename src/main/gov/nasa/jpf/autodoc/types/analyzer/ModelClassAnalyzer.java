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

import gov.nasa.jpf.autodoc.types.NameUtils;
import gov.nasa.jpf.autodoc.types.info.ModelClassInfo;
import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.autodoc.types.info.CollectedInfo;
import gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException;
import gov.nasa.jpf.classfile.ClassFileException;

/**
 * Class to analyze Model Classes.
 * 
 * @author Carlos Uribe
 */
public class ModelClassAnalyzer extends InfoAnalyzer {

  public static final String TYPE = "ModelClass";
  public static final String LOCATION = "classes";
  
  private ModelClassInfo model = new ModelClassInfo();

  @Override
  public ModelClassInfo getResult() {
    return model;
  }

  @Override
  public void analyze(ClassInfo clsinfo, CollectedInfo info) 
          throws ClassFileNotFoundException {
    model = new ModelClassInfo();
    String mdname = clsinfo.getName();
    ClassInfo std = loadStandard(mdname, info);

    if (std != null) {
      setup(clsinfo);
      model.setStdName(std.getName());
      model.setStdMethods(matchMethods(clsinfo, std));
      found = true;
    } else if (isLocationValid(clsinfo, LOCATION) && std == null) {
      setup(clsinfo);
      model.setStdName("unknown");
      found = true;
    } else {
      found = false;
    }
  }
  
  public ClassInfo loadStandard(String name, CollectedInfo info) {
    try {
      String stdname = NameUtils.normalizeName(name);
      return parser.parse(stdname, classpath);
    } catch (NullPointerException ex) {
    } catch (ClassFileNotFoundException ex) {
    } catch (ClassFileException ex) {
      ex.printStackTrace(System.err);
    }
    return null;
  }
  
  private void setup(ClassInfo modelInfo) {
    model.setType(TYPE);
    model.setInfo(modelInfo);
  }
}
