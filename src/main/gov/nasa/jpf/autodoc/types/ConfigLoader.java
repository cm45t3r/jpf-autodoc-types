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

package gov.nasa.jpf.autodoc.types;

import gov.nasa.jpf.Config;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Class to load config files to setup AutoDocTool.
 * 
 * @author Carlos Uribe
 */
public class ConfigLoader extends Properties {

  /**
   * Load the config file, recursiveExpand property implicit terms 
   * and configureComponents.
   * 
   * @param filepath String containing the path to the config file.
   * @throws IOException if the file was not found or ocurred an I/O error.
   * @throws AutoDocException if parsing of some entry fails.
   */
  public void loadConfig(String filepath) throws IOException {
    load(new FileInputStream(filepath));
    expandTerms();
  }

  /**
   * Expand all implicit expandable terms between '${' and '}'.
   */
  public void expandTerms() {
    for (Enumeration<?> e = propertyNames(); e.hasMoreElements();) {
      String key = e.nextElement().toString();
      String value = getProperty(key);
      setProperty(key, recursiveExpand(value));
    }
  }

  /**
   * Get the expanded equivalent of <code>unexpanded</code> values found 
   * as a System property, JPF Property or any described in config file. 
   * If needed, do a recursive process expanding on top levels.
   * 
   * @param unexpanded Property that matches \$\{.*\} regex.
   * @return expanded property or original if not found.
   */
  protected String recursiveExpand(String unexpanded) {
    String expanded = unexpanded;
    String[] slices = unexpanded.split(",");

    for (String slc : slices) {
      if (isExpandable(slc)) {
        String name = slc.trim().substring(slc.indexOf("${") + 2,
                                           slc.indexOf("}"));
        String prop = getProperty(name);
        Config jc = new Config(new String[]{});
        String jpf = jc.getString(name);
        String sys = System.getProperty(name);

        if (prop != null) {
          if (isExpandable(prop)) {
            expanded = recursiveExpand(prop);
          } else {
            expanded = prop;
          }
        } else if (jpf != null) {
          expanded = jpf;
        } else if (sys != null) {
          expanded = sys;
        } else {
          expanded = slc;
        }
      }
    }
    return expanded;
  }

  /**
   * Check if <code>expression</code> matches \$\{.*\} regex in a simple way.
   * 
   * @param expression Value of property to be checked.
   * @return true if <code>expression</code> matches.
   */
  public boolean isExpandable(String expression) {
    return expression.contains("${") && expression.contains("}");
  }
  
  public List<String> getLeafs() {
    List<String> leafs = new ArrayList<String>();
    
    for (Enumeration<?> e = propertyNames(); e.hasMoreElements();) {
      String key = (String)e.nextElement();
      
      if (isLeaf(key)) {
        leafs.add(key);
      }
    }
    return leafs;
  }
  
  /**
   * Check if a property is a leaf of one of the JPF component hierarchy tree.
   * 
   * @param key String describing the root property.
   * @return true if <code>.isleaf</code> subproperty value is true.
   */
  public boolean isLeaf(String key) {
    String val = getProperty(key + ".isleaf");
    if (val == null) {
      return false;
    }
    return val.equals("true");
  }

  /**
   * Get value of subproperty type of given key.
   * 
   * @param key String indicating the property key.
   * @return type of component.
   */
  public String getType(String key) {
    return getProperty(key + ".type");
  }
  
  public String[] getPropertyVals(String key) {
    return getProperty(key).split(",");
  }
}
