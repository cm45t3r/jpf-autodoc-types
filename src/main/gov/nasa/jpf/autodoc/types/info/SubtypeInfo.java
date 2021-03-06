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
 * Class to keep record of analyzed hierarchical information such as 
 * subtypes of Listeners and InstructionFactories.
 * 
 * @author Carlos Uribe <carlos.albert.uribe@gmail.com>
 */
public class SubtypeInfo extends JPFInfo {

  private Set<String> ancestors = new LinkedHashSet<String>();
  private Set<String> superMethods = new LinkedHashSet<String>();

  public Set<String> getAncestors() {
    return ancestors;
  }

  public Set<String> getSuperMethods() {
    return superMethods;
  }

  public void setAncestors(Set<String> ancestors) {
    this.ancestors = ancestors;
  }

  public void setSuperMethods(Set<String> superMethods) {
    this.superMethods = superMethods;
  }
  
  public void addAncestor(String ancestor) {
    ancestors.add(ancestor);
  }
  
  public void addAncestors(Set<String> ancestors) {
    this.ancestors.addAll(ancestors);
  }

  @Override
  public int compareTo(JPFInfo o) {
    return info.compareTo(o.info);
  }
}
