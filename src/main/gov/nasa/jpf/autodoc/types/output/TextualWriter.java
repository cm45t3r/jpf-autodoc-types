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
import gov.nasa.jpf.autodoc.types.info.ModelClassInfo;
import gov.nasa.jpf.autodoc.types.info.NativePeerInfo;
import gov.nasa.jpf.autodoc.types.info.SubtypeInfo;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author Carlos Uribe
 */
public abstract class TextualWriter {
  
  protected PrintWriter pw;
  protected int indentLevel = 0;

  public TextualWriter(PrintWriter pw) {
    this.pw = pw;
  }
  
  public void disable() {
    pw.close();
  }
  
  public PrintWriter getPrintWriter() {
    return pw;
  }

  public void setPrintWriter(PrintWriter pw) {
    this.pw = pw;
  }

  public void indent() {
    indentLevel++;
  }

  public void unindent() {
    if (indentLevel > 0) {
      indentLevel--;
    }
  }

  public int getIndentLevel() {
    return indentLevel;
  }

  public String getIndent() {
    String ind = new String();
    for (int i = 0; i < indentLevel; ++i) {
      ind += "  ";
    }
    return ind;
  }

  public void putIndent() {
    pw.print(getIndent());
  }

  public void writeTitle(String appName, String version, String release,
                         String vendor, String details) {
    String title = new String();

    if (appName != null) {
      title += appName + " ";
    }

    if (version != null) {
      title += "v" + version + " ";
    }

    if (release != null) {
      title += "(" + release + ") ";
    }

    if (vendor != null) {
      title += "- (C) " + vendor + " ";
    }

    if (details != null) {
      title += details;
    }

    pw.println(title);
  }

  public void writeHeader() {
    writeMajorSeparator("start of process");
    pw.println("analysis started: " + new Date());
  }

  public void writeFooter() {
    writeMajorSeparator("end of process");
    pw.println("analysis finished: " + new Date());
    pw.println();
  }

  public void writeParam(String name, String param) {
    if (!param.isEmpty() && !(param == null)) {
      putIndent();
      pw.println(name + ": " + param);
    }
  }
  
  public void writeInfo(CollectedInfo info) {
    writeMinorSeparator("components");
    
    int i = 0;
    for (Entry<String, SubtypeInfo> e : info.getSubtypes().entrySet()) {
      writeln("[" + i + "] class:\t" + e.getKey());
      indent();
      indent();
      detail("type", e.getValue().getType());
      this.writeCollection("ancestors", e.getValue().getAncestors());
      detail("project", e.getValue().getInfo().getProject());
      detail("flags", Integer.toString(e.getValue().getInfo().getFlags()));
      unindent();
      unindent();
      writeln();
      ++i;
    }
    
    writeMinorSeparator("model classes");
    
    i = 0;
    for (Entry<String, ModelClassInfo> e : info.getModels().entrySet()) {
      writeln("[" + i + "] class:\t" + e.getKey());
      indent();
      indent();
      detail("modelled", e.getValue().getStdName());
      detail("layer", e.getValue().getInfo().getLayer());
      detail("project", e.getValue().getInfo().getProject());
      detail("flags", Integer.toString(e.getValue().getInfo().getFlags()));
      unindent();
      unindent();
      writeln();
      ++i;
    }
    
    writeMinorSeparator("native peers");
    
    i = 0;
    for (Entry<String, NativePeerInfo> e : info.getPeers().entrySet()) {
      writeln("[" + i + "] peer:\t" + e.getKey());
      indent();
      indent();
      detail("model", e.getValue().getModelName());
      detail("layer", e.getValue().getInfo().getLayer());
      detail("project", e.getValue().getInfo().getProject());
      detail("flags", Integer.toString(e.getValue().getInfo().getFlags()));
      unindent();
      unindent();
      writeln();
      ++i;
    }
  }
  
  public void writeDetailedInfo(CollectedInfo info) {
    writeMinorSeparator("components");
    
    int i = 0;
    for (Entry<String, SubtypeInfo> e : info.getSubtypes().entrySet()) {
      writeln("[" + i + "] class:\t" + e.getKey());
      indent();
      indent();
      detail("type", e.getValue().getType());
      writeCollection("ancestors", e.getValue().getAncestors());
      writeCollection("interfaces", e.getValue().getInfo().getInterfaces());
      writeCollection("implemented-methods", e.getValue().getSuperMethods());
      writeCollection("own-methods", e.getValue().getInfo().getMethods());
      detail("location", e.getValue().getInfo().getLocation());
      detail("project", e.getValue().getInfo().getProject());
      detail("layer", e.getValue().getInfo().getLayer());
      detail("flags", Integer.toString(e.getValue().getInfo().getFlags()));
      unindent();
      unindent();
      writeln();
      ++i;
    }
    
    writeMinorSeparator("model classes");
    
    i = 0;
    for (Entry<String, ModelClassInfo> e : info.getModels().entrySet()) {
      writeln("[" + i + "] class:\t" + e.getKey());
      indent();
      indent();
      detail("modelled", e.getValue().getStdName());
      writeCollection("interfaces", e.getValue().getInfo().getInterfaces());
      writeCollection("modelled-methods", e.getValue().getStdMethods());
      writeCollection("own-methods", e.getValue().getInfo().getMethods());
      detail("location", e.getValue().getInfo().getLocation());
      detail("project", e.getValue().getInfo().getProject());
      detail("layer", e.getValue().getInfo().getLayer());
      detail("flags", Integer.toString(e.getValue().getInfo().getFlags()));
      unindent();
      unindent();
      writeln();
      ++i;
    }
    
    writeMinorSeparator("native peers");
    
    i = 0;
    for (Entry<String, NativePeerInfo> e : info.getPeers().entrySet()) {
      writeln("[" + i + "] peer:\t" + e.getKey());
      indent();
      indent();
      detail("model", e.getValue().getModelName());
      writeCollection("interfaces", e.getValue().getInfo().getInterfaces());
      writeCollection("intercepted-methods", e.getValue().getModelMethods());
      writeCollection("own-methods", e.getValue().getInfo().getMethods());
      detail("location", e.getValue().getInfo().getLocation());
      detail("project", e.getValue().getInfo().getProject());
      detail("layer", e.getValue().getInfo().getLayer());
      detail("flags", Integer.toString(e.getValue().getInfo().getFlags()));
      unindent();
      unindent();
      writeln();
      ++i;
    }
  }
  
  public void detail(String name, String value) {
    if (!value.isEmpty() && !(value == null)) {
      writeln(name + ":\t" + value);
    }
  }
  
  public void writeCollection(String name, Set<String> set) {
    if (set.isEmpty()) {
      return;
    }
    
    writeln(name);
    indent();
    
    int i = 0;
    for (String e : set) {
      writeln("[" + i + "] " + e);
      ++i;
    }
    unindent();
  }

  public void writeMajorSeparator(String id) {
    pw.println();
    pw.print("====================================================== ");
    pw.println(id);
  }

  public void writeMinorSeparator(String id) {
    pw.print("------------------------------------------------------ ");
    pw.println(id);
  }

  public void write(Object o) {
    putIndent();
    pw.print(o);
  }

  public void writeln(Object o) {
    putIndent();
    pw.println(o);
  }
  
  public void writeln() {
    putIndent();
    pw.println();
  }

  public void error(String description) {
    pw.println(description);
  }
}
