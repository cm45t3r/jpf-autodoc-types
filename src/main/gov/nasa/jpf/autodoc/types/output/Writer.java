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

package gov.nasa.jpf.autodoc.types.output;

import gov.nasa.jpf.autodoc.types.info.CollectedInfo;
import java.io.PrintWriter;

/**
 * Interface to wrap text and stdout writers.
 * 
 * @author Carlos Uribe
 */
public interface Writer {

  public PrintWriter getPrintWriter();
  
  public void disable();

  public void setPrintWriter(PrintWriter pw);

  public void indent();

  public void unindent();

  public int getIndentLevel();

  public void putIndent();

  public void writeTitle(String appName, String version, String release,
                         String vendor, String details);

  public void writeHeader();

  public void writeFooter();

  public void writeParam(String name, String param);
  
  public void writeInfo(CollectedInfo info);
  
  public void writeDetailedInfo(CollectedInfo info);
  
  public void detail(String name, String value);

  public void writeMajorSeparator(String id);

  public void writeMinorSeparator(String id);

  public void write(Object o);

  public void writeln(Object o);
  
  public void writeln();

  public void error(String description);
}
