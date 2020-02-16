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
import gov.nasa.jpf.autodoc.types.info.ModelClassInfo;
import gov.nasa.jpf.autodoc.types.info.NativePeerInfo;
import gov.nasa.jpf.autodoc.types.info.SubtypeInfo;
import gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException;

/**
 * Class that starts analyses.
 * 
 * @author Carlos Uribe <carlos.albert.uribe@gmail.com>
 */
public class AnalysisTrigger {

  private SubtypeAnalyzer typelyzer = new SubtypeAnalyzer(this);
  private ModelClassAnalyzer modelyzer = new ModelClassAnalyzer();
  private NativePeerAnalyzer nativelyzer = new NativePeerAnalyzer(this);

  public enum AnalysisType {

    LISTENERS    (0x1),
    IFACTORIES   (0x2),
    SUBTYPES     (0x3),
    MODELCLASSES (0x4),
    NATIVEPEERS  (0x8),
    ALL          (0xF);
    
    int weight;

    AnalysisType(int weight) {
      this.weight = weight;
    }
  }

  public void addModelPaths(String[] pathnames) {
    modelyzer.addPathNames(pathnames);
  }

  public void addPeerPaths(String[] pathnames) {
    nativelyzer.addPathNames(pathnames);
  }

  public void addTypePaths(String[] pathnames) {
    typelyzer.addPathNames(pathnames);
  }

  public void addModelPath(String pathname) {
    modelyzer.addPathName(pathname);
  }

  public CollectedInfo startAnalyses(CollectedInfo info, AnalysisType[] types)
          throws ClassFileNotFoundException {
    int mask = 0;
    for (AnalysisType type : types) {
      mask |= type.weight;
    }
    return startAnalyses(info, mask);
  }

  public CollectedInfo startAnalyses(CollectedInfo info, int type)
          throws ClassFileNotFoundException {
    CollectedInfo newInfo = info;

    for (ClassInfo cls : newInfo.getParsed().values()) {
      if (isAnalysisEnabled(type, AnalysisType.SUBTYPES.weight)) {
        SubtypeInfo subtype = analyzeSubtype(cls, info);
        
        if (subtype != null) {
          if (subtype.getType().equals("InstructionFactory")) {
            if (isAnalysisEnabled(type, AnalysisType.IFACTORIES.weight)) {
              newInfo.register(subtype);
            }
          } else if (subtype.getType().equals("Listener")) {
            if (isAnalysisEnabled(type, AnalysisType.LISTENERS.weight)) {
              newInfo.register(subtype);
            }
            //-- add more else-if for each type whether you want to filter 
            // another subtype case.
          } else {
            newInfo.register(subtype);
          }
        }
      }
      
      if (isAnalysisEnabled(type, AnalysisType.MODELCLASSES.weight)) {
        ModelClassInfo model = analyzeModelClass(cls, info);

        if (model != null) {
          newInfo.register(model);
        }
      }

      if (isAnalysisEnabled(type, AnalysisType.NATIVEPEERS.weight)) {
        NativePeerInfo peer = analyzeNativePeer(cls, info);

        if (peer != null) {
          newInfo.register(peer);
        }
      }
    }
    return newInfo;
  }

  public ModelClassInfo analyzeModelClass(ClassInfo cls, CollectedInfo info)
          throws ClassFileNotFoundException {
    modelyzer.analyze(cls, info);

    if (modelyzer.wasFound()) {
      return modelyzer.getResult();
    }
    return null;
  }

  public NativePeerInfo analyzeNativePeer(ClassInfo cls, CollectedInfo info)
          throws ClassFileNotFoundException {
    nativelyzer.analyze(cls, info);

    if (nativelyzer.wasFound()) {
      return nativelyzer.getResult();
    }
    return null;
  }

  public SubtypeInfo analyzeSubtype(ClassInfo cls, CollectedInfo info)
          throws ClassFileNotFoundException {
    typelyzer.analyze(cls, info);

    if (typelyzer.wasFound()) {
      return typelyzer.getResult();
    }
    return null;
  }

  public boolean isAnalysisEnabled(int type, int mask) {
    return (type & mask) != 0;
  }

  public AnalysisType getAnalysisType(int mask) {
    for (AnalysisType type : AnalysisType.values()) {
      if (mask == type.weight) {
        return type;
      }
    }
    return AnalysisType.ALL;
  }
}
