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

package gov.nasa.jpf.autodoc.types.info;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class to keep record of common elements of a class, generally extracted from
 * a parsing process to a classfile.
 *
 * @author Carlos Uribe <carlos.albert.uribe@gmail.com>
 */
public class ClassInfo implements Comparable<ClassInfo> {

  private int flags = 0;
  private String name = new String();
  private String superName = new String();
  private Set<String> methods = new LinkedHashSet<String>();
  private Set<String> interfaces = new LinkedHashSet<String>();
  private String location = new String();
  private String project = new String();
  private String layer = new String();

  /**
   * Get flags.
   *
   * @return access permissions to and properties of this class. If any refer to
   * Java(TM)
   * <a
   * href=http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#74353>
   * access_flags</a> at JVM specs.
   */
  public int getFlags() {
    return flags;
  }

  public Set<String> getInterfaces() {
    return interfaces;
  }

  public String getLocation() {
    return location;
  }

  public String getProject() {
    return project;
  }

  public String getLayer() {
    return layer;
  }

  public Set<String> getMethods() {
    return methods;
  }

  public String getName() {
    return name;
  }

  public String getSuperName() {
    return superName;
  }

  /**
   * Set flags.
   * 
   * @param flags permission flags. If any refer to Java(TM)
   * <a
   * href=http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#74353>
   * access_flags</a> at JVM specs.
   */
  public void setFlags(int flags) {
    this.flags = flags;
  }

  public void setInterfaces(Set<String> interfaces) {
    this.interfaces = interfaces;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public void setLayer(String layer) {
    this.layer = layer;
  }

  public void setMethods(Set<String> methods) {
    this.methods = methods;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSuperName(String superName) {
    this.superName = superName;
  }

  @Override
  public int compareTo(ClassInfo o) {
    return name.compareTo(o.name);
  }

  @Override
  public String toString() {
    return "ClassInfo{" + "flags=" + flags + ", name=" + name + ", superName=" + superName + ", methods=" + methods + ", interfaces=" + interfaces + ", location=" + location + ", project=" + project + ", layer=" + layer + '}';
  }
}
