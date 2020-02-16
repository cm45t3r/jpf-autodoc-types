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

import gov.nasa.jpf.autodoc.types.FileNotLoadedException;
import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException;
import gov.nasa.jpf.autodoc.types.scanner.TargetScanner.ScanType;
import gov.nasa.jpf.classfile.ClassFileException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Interface to wrap scanners.
 * 
 * @author Carlos Uribe
 */
public interface Scanner {

  public void scan(String[] targets, String[] classpath, ScanType scanType)
          throws IOException, FileNotLoadedException, ClassFileException, 
          InvalidTargetException, ClassFileNotFoundException;

  public void scan(List<String> targets, List<String> classpath,
                   ScanType scanType)
          throws IOException, FileNotLoadedException, ClassFileException, 
          InvalidTargetException, ClassFileNotFoundException;

  public ScanData scan(String target, ScanType type)
          throws IOException, FileNotLoadedException, ClassFileException;

  public Set<ClassInfo> getScanned();

  public void list(PrintWriter pw);

  public Set<String> fetchClassFiles(String target);

  public Set<String> fetchZipFiles(String target);

  public Set<String> fetchJarFiles(String target);

  public Set<String> fetch(String target, FilenameFilter filter);

  public Set<String> seek(File file, FilenameFilter filter);

  public boolean isScanTypeEnabled(ScanType type, ScanType mask);

  public boolean isScanTypeEnabled(int type, int mask);

  public ScanType getScanType(int mask);
}
