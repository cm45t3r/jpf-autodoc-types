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

import gov.nasa.jpf.autodoc.types.NameUtils;
import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.classfile.ClassFile;
import gov.nasa.jpf.classfile.ClassFileException;
import gov.nasa.jpf.classfile.ClassFileReaderAdapter;
import java.util.Arrays;

/**
 * Class to extract <code>ClassInfo</code> from Java classfiles.
 * 
 * @author Carlos Uribe
 */
public class ClassFileParser extends ClassFileReaderAdapter {

  private ClassInfo classinfo = new ClassInfo();

  /**
   * Get classfile information collected in parsing process.
   * 
   * @return classfile information.
   */
  public ClassInfo getClassInfo() {
    return classinfo;
  }
  
  public void setLocationEx(String location) {
    classinfo.setLocation(location);
  }
  
  public void setProjectEx(String project) {
    classinfo.setProject(project);
  }

  /**
   * Parse a classfile given the location (path) of the file.
   * 
   * @param path Filepath where the parser can find the
   * <code>.class</code> file.
   * @param project Owner project of the class.
   */
  public void parse(String path) throws ClassFileException {
    classinfo = new ClassInfo();
    classinfo.setLocation(path);
    classinfo.setLayer(inferLayer(path));
    classinfo.setProject(inferProject(path));
    ClassFile cfile = new ClassFile(path);
    cfile.parse(this);
  }

  /**
   * Parse a classfile from a classname and a set of pathnames.
   * 
   * @param classname Named package classname.
   * @param pathnames String array containing pathnames of classpath.
   */
  public void parse(String classname, String[] pathnames)
          throws ClassFileException, ClassFileNotFoundException {
    ClassPath classpath = new ClassPath();
    classpath.addAllPathNames(pathnames);
    parse(classname, classpath);
  }

  /**
   * Parse a classfile from a classname and a non-empty classpath.
   * 
   * @param classname Named package classname.
   * @param classpath 
   * @throws ClassFileException 
   */
  public void parse(String classname, ClassPath classpath)
          throws ClassFileException, ClassFileNotFoundException {
    classinfo = new ClassInfo();
    String clsname = NameUtils.normalizeName(classname);
    String source = classpath.getSource(clsname);
    
    if (source.isEmpty() || source == null) {
      throw new ClassFileNotFoundException("Class not found in classpath: " 
              + clsname);
    }
    classinfo.setLocation(source);
    classinfo.setLayer(inferLayer(clsname, classpath));
    ClassFile cfile = new ClassFile(classpath.getClassData(clsname));
    cfile.parse(this);
  }

  /**
   * Parse classfile from byte data.
   * 
   * @param cfdata Byte array containing classfile data.
   */
  public void parse(byte[] cfdata) throws ClassFileException {
    classinfo = new ClassInfo();
    ClassFile cfile = new ClassFile(cfdata);
    cfile.parse(this);
  }

  /**
   * Get the root container folder of packages from path.
   */
  public String inferLayer(String path) {
    String[] commonLayers = {"main", "peers", "annotations",
                             "classes", "tests", "examples"};
    
    for (String layer : commonLayers) {
      if (path.contains("build/" + layer) || path.contains("build\\" + layer)) {
        return layer;
      }
    }
    return new String();
  }

  /**
   * Get the root container folder of packages from classname and classpath.
   */
  public String inferLayer(String classname, ClassPath classpath) 
          throws ClassFileException {
    String[] commonLayers = {"main", "peers", "annotations",
                             "classes", "tests", "examples"};
    
    String source = classpath.getSource(classname);
    
    for (String layer : commonLayers) {
      if (source.endsWith(layer)) {
        return layer;
      } else if ((layer.equals("annotations") || layer.equals("classes"))
                 && source.endsWith("-" + layer + ".jar")) {
        return layer;
      }
    }

    return new String();
  }
  
  public String inferProject(String path) {
    String sep = NameUtils.getSeparator();
    String name = path.replace(sep, NameUtils.UNX_SEP);
    
    for (String slice : name.split(NameUtils.UNX_SEP)) {
      if (NameUtils.isJPFProjectName(slice)) {
        return slice;
      }
    }
    return new String();
  }

  /**
   * Collect classfile's classname, superclassname and flags. <p>
   * NOTE - This method is automatically called by <code>ClassFile</code>
   * when <code>parse(ClassFileReaderAdapter)</code> method is called.
   */
  @Override
  public void setClass(ClassFile cf, String clsName, String superClsName,
                       int flags, int cpCount) {
    classinfo.setName(clsName);
    classinfo.setSuperName(superClsName);
    classinfo.setFlags(flags);
  }

  /**
   * Collect interfaces implemented by classfile. <p>
   * NOTE - This method is automatically called by <code>ClassFile</code>
   * when <code>parse(ClassFileReaderAdapter)</code> method is called.
   */
  @Override
  public void setInterface(ClassFile cf, int ifcIndex, String ifcName) {
    classinfo.getInterfaces().add(ifcName);
  }

  /**
   * Collect classfile methods. <p>
   * NOTE - This method is automatically called by <code>ClassFile</code>
   * when <code>parse(ClassFileReaderAdapter)</code> method is called.
   */
  @Override
  public void setMethod(ClassFile cf, int methodIndex, int accessFlags,
                        String name, String descriptor) {
    classinfo.getMethods().add(name + descriptor);
  }
}