//
// Copyright (C) 2014 United States Government as represented by the
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/**
 * Code writer of standard Markdown format.
 *
 * @author Carlos Uribe
 */
public class MarkdownWriter extends TextualWriter implements Writer {

  public MarkdownWriter(String filename) throws FileNotFoundException {
    super(new PrintWriter(new FileOutputStream(filename), true));
  }

  @Override
  public void writeTitle(String appName, String version, String release,
                         String vendor, String details) {
  }

  @Override
  public void writeHeader() {
  }

  @Override
  public void writeFooter() {
  }

  @Override
  public void writeDetailedInfo(CollectedInfo info) {
    writeHeading("Types");

    for (Map.Entry<String, SubtypeInfo> e : info.getSubtypes().entrySet()) {
      writeSubheading(e.getKey());
      writeColoredType(e.getValue().getType());
      detail("location", "`" + e.getValue().getInfo().getLocation() + "`");
      detail("project", "*" + e.getValue().getInfo().getProject() + "*");
      detail("layer", e.getValue().getInfo().getLayer());
      detail("flags", "`" + Integer.toString(e.getValue().getInfo().getFlags()) + "`");
      writeCollection("ancestors", e.getValue().getAncestors());
      writeCollection("interfaces", e.getValue().getInfo().getInterfaces());
      writeCollection("implemented-methods", e.getValue().getSuperMethods());
      writeCollection("own-methods", e.getValue().getInfo().getMethods());
      writeln();
    }

    writeHeading("Model classes");

    for (Map.Entry<String, ModelClassInfo> e : info.getModels().entrySet()) {
      writeSubheading(e.getKey());
      writeColoredType("ModelClass");
      detail("model", "`" + e.getValue().getStdName() + "`");
      detail("location", "`" + e.getValue().getInfo().getLocation() + "`");
      detail("project", "*" + e.getValue().getInfo().getProject() + "*");
      detail("layer", e.getValue().getInfo().getLayer());
      detail("flags", "`" + Integer.toString(e.getValue().getInfo().getFlags()) + "`");
      writeCollection("interfaces", e.getValue().getInfo().getInterfaces());
      writeCollection("modelled-methods", e.getValue().getStdMethods());
      writeCollection("own-methods", e.getValue().getInfo().getMethods());
      writeln();
    }

    writeHeading("Native peers");

    for (Map.Entry<String, NativePeerInfo> e : info.getPeers().entrySet()) {
      writeSubheading(e.getKey());
      writeColoredType("NativePeer");
      detail("model", "`" + e.getValue().getModelName() + "`");
      detail("location", "`" + e.getValue().getInfo().getLocation() + "`");
      detail("project", "*" + e.getValue().getInfo().getProject() + "*");
      detail("layer", e.getValue().getInfo().getLayer());
      detail("flags", "`" + Integer.toString(e.getValue().getInfo().getFlags()) + "`");
      writeCollection("interfaces", e.getValue().getInfo().getInterfaces());
      writeCollection("intercepted-methods", e.getValue().getModelMethods());
      writeCollection("own-methods", e.getValue().getInfo().getMethods());
      writeln();
    }
  }

  public void writeColoredType(String type) {
    if (type.isEmpty() || type == null) {
      return;
    }

    if (type.contains("Listener")) {
      writeln("**type**: <font color=\"red\">" + type + "</font>\n");
    } else if (type.contains("Inst")) {
      writeln("**type**: <font color=\"orange\">" + type + "</font>\n");
    } else if (type.contains("Model")) {
      writeln("**type**: <font color=\"blue\">" + type + "</font>\n");
    } else if (type.contains("Peer")) {
      writeln("**type**: <font color=\"green\">" + type + "</font>\n");
    }
  }

  @Override
  public void writeCollection(String name, Set<String> set) {
    if (set.isEmpty()) {
      return;
    }

    writeln("**" + name + "**");
    indent();
    
    int idx = 0;
    for (String e : set) {
      writeln(idx + ". `" + e + "`");
      idx++;
    }

    unindent();
    writeln();
  }

  @Override
  public void writeMinorSeparator(String id) {
  }
  
  public void writeHeading(String id) {
    pw.println(id);
    writeFittedUnderline("=", id);
  }
  
  public void writeSubheading(String id) {
    pw.println(id);
    writeFittedUnderline("-", id);
  }
  
  public void writeFittedUnderline(String separator, String heading) {
    for (int idx = 0; idx < heading.length(); ++idx) {
      pw.print(separator);
    }
    
    pw.println();
    pw.println();
  }

  @Override
  public void detail(String name, String value) {
    if (!value.isEmpty() && value != null) {
      writeln("**" + name + "**: " + value + "\n");
    }
  }
}
