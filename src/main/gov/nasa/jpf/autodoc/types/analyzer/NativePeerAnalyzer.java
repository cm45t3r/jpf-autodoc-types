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
import gov.nasa.jpf.autodoc.types.info.NativePeerInfo;
import gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException;
import gov.nasa.jpf.classfile.ClassFileException;
import gov.nasa.jpf.jvm.Types;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class to analyze Native Peer Classes.
 * 
 * @author Carlos Uribe
 */
public class NativePeerAnalyzer extends InfoAnalyzer {

  public static final String TYPE = "NativePeerClass";
  public static final String LOCATION = "peers";
  
  private NativePeerInfo nativepeer = new NativePeerInfo();

  public NativePeerAnalyzer(AnalysisTrigger trigger) {
    this.trigger = trigger;
  }
  
  @Override
  public NativePeerInfo getResult() {
    return nativepeer;
  }

  @Override
  public void analyze(ClassInfo clsinfo, CollectedInfo info)
          throws ClassFileNotFoundException {
    nativepeer = new NativePeerInfo();
    String mangled = clsinfo.getName();
    
    if (NameUtils.isNameMangled(mangled)) {
      String unmangled = unmangleName(mangled);
      ModelClassInfo model = loadModel(unmangled, info);
      
      if (model != null) {
        nativepeer.setModelName(model.getInfo().getName());
        Set<String> umMethods = unmangle(clsinfo.getMethods());
        Set<String> modelMths = model.getInfo().getMethods();
        Set<String> match = matchMethods(umMethods, modelMths);
        nativepeer.setModelMethods(match);
      } else {
        nativepeer.setModelName("unknown");
      }
      setup(clsinfo);
      found = true;
    } else if (isLocationValid(clsinfo, LOCATION)) {
      setup(clsinfo);
      nativepeer.setModelName("unknown");
      found = true;
    } else {
      found = false;
    }
  }

  public ModelClassInfo loadModel(String name, CollectedInfo info) {
    if (info.isModel(name)) {
      return info.getModel(name);
    }
    
    try {
      ClassInfo cls = new ClassInfo();
      
      if (info.isParsed(name)) {
        cls = info.getCls(name);
      } else {
        String mdlname = NameUtils.normalizeName(name);
        cls = parser.parse(mdlname, classpath);
      }
      
      return trigger.analyzeModelClass(cls, info);
    } catch (NullPointerException ex) {
    } catch (ClassFileNotFoundException ex) {
    } catch (ClassFileException ex) {
      ex.printStackTrace(System.err);
    }
    return null;
  }
  
  private void setup(ClassInfo peerInfo) {
    nativepeer.setType(TYPE);
    nativepeer.setInfo(peerInfo);
  }
  
  public String unmangleName(String clsname) {
    if (!NameUtils.isNameMangled(clsname)) {
      return null;
    }

    String name = clsname;

    if (clsname.contains(NameUtils.UNX_SEP)) {
      name = clsname.substring(clsname.lastIndexOf(NameUtils.UNX_SEP) + 1);
    }

    if (name.endsWith("_TODO")) {
      name = name.substring(0, name.indexOf("_TODO"));
    }

    name = name.substring(name.indexOf(NameUtils.MJI_SEP) + 1);
    return name.replace(NameUtils.MJI_SEP, NameUtils.UNX_SEP);
  }
  
  public Set<String> unmangle(Set<String> methods) {
    Set<String> unmangled = new LinkedHashSet<String>();
    
    for (String method : methods) {
      unmangled.add(unmangle(method));
    }
    return unmangled;
  }
  
  public String unmangle(String method) {
    String unmangled = Types.getJNIMethodName(method);
    String sign = Types.getJNISignature(method);
    
    //--- remove MJIEnv parameters
    if (sign != null) {
      int i = sign.indexOf(")") + 1;
      int j = sign.substring(i).indexOf("(") + i;
      sign = sign.substring(0, j);
      unmangled += sign;
    }
    return unmangled;
  }
}
