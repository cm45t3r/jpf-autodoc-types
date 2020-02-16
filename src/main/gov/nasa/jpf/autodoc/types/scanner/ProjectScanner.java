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

package gov.nasa.jpf.autodoc.types.scanner;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.autodoc.types.FileNotLoadedException;
import gov.nasa.jpf.autodoc.types.NameUtils;
import gov.nasa.jpf.autodoc.types.scanner.TargetScanner.ScanType;
import gov.nasa.jpf.classfile.ClassFileException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class to get lists of files from jpf registered projects.
 * 
 * @author Carlos Uribe
 */
public class ProjectScanner {

  private Config config;
  private Scanner scanner;

  public ProjectScanner(Scanner scanner) {
    //--- It's used the Config constructor with empty args to load the whole
    //    JPF config stack because the string constructor is not available.
    config = new Config(new String[]{});
    this.scanner = scanner;
  }

  /**
   * Traverse all jpf projects registered in site.properties and 
   * get a filtered list of its files.
   * 
   * @param type Scan type member of <code>Scanner.ScanType</code> enumeration.
   *        For more info about options check <a href="ScanType.html>ScanType
   *        </a>.
   * @return a list of files in all projects specified by type.
   */
  public ScanData scanAll(ScanType type)
          throws IOException, FileNotLoadedException, ClassFileException {
    ScanData info = new ScanData();

    for (String project : getAllNames()) {
      info.addInfo(scan(project, type));
    }
    return info;
  }
  
  public Set<String> fetchAllFiles(FilenameFilter filter) {
    Set<String> paths = new TreeSet();
    
    for (String project : getAllNames()) {
      String path = getPath(project);
      paths.addAll(scanner.fetch(path, filter));
    }
    return paths;
  }
  
  /**
   * Get a list of all project names registered in site.properties 
   * in alphabetical order.
   */
  public Set<String> getAllNames() {
    Set<String> projects = new TreeSet<String>();
    
    for (Enumeration<?> entries = config.propertyNames();
         entries.hasMoreElements();) {
      String name = entries.nextElement().toString();

      if (checkName(name)) {
        projects.add(name);
      }
    }
    return projects;
  }

  /**
   * Get a filtered list of project files.
   * 
   * @param type Scan type member of <code>Scanner.ScanType</code> enumeration.
   *        For more info about options check <a href="ScanType.html>ScanType
   *        </a>.
   * @return a list of files in project specified by type.
   */
  public ScanData scan(String project, ScanType scanType)
          throws IOException, FileNotLoadedException, ClassFileException {
    if (checkName(project)) {
      return scanner.scan(getPath(project), scanType);
    }
    return new ScanData();
  }

  /**
   * Check if project name is valid and is in site.properties.
   */
  public boolean checkName(String project) {
    return NameUtils.isJPFProjectName(project) && isInSite(project);
  }

  /**
   * Check if project is registered in site.properties allowing 
   * to get project information.
   */
  public boolean isInSite(String project) {
    return config.hasValue(project);
  }
  
  /**
   * Get the project path by checking the JPF config property.
   */
  public String getPath(String project) {
    return config.getString(project);
  }
}
