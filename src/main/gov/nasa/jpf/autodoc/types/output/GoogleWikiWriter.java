//
// Copyright (C) 2013 United States Government as represented by the
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
 * Code writer of wiki for Google Code projects.
 *
 * @author Carlos Uribe
 */
public class GoogleWikiWriter extends TextualWriter implements Writer {

  public GoogleWikiWriter(String filename) throws FileNotFoundException {
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
    writeMinorSeparator("Types");

    for (Map.Entry<String, SubtypeInfo> e : info.getSubtypes().entrySet()) {
      writeln("==" + e.getKey() + "==");
      writeColoredType(e.getValue().getType());
      detail("location", "`" + e.getValue().getInfo().getLocation() + "`");
      detail("project", "_" + e.getValue().getInfo().getProject() + "_");
      detail("layer", e.getValue().getInfo().getLayer());
      detail("flags", "`" + Integer.toString(e.getValue().getInfo().getFlags()) + "`");
      writeCollection("ancestors", e.getValue().getAncestors());
      writeCollection("interfaces", e.getValue().getInfo().getInterfaces());
      writeCollection("implemented-methods", e.getValue().getSuperMethods());
      writeCollection("own-methods", e.getValue().getInfo().getMethods());
      writeln();
    }

    writeMinorSeparator("Model classes");

    for (Map.Entry<String, ModelClassInfo> e : info.getModels().entrySet()) {
      writeln("==" + e.getKey() + "==");
      writeColoredType("ModelClass");
      detail("model", "`" + e.getValue().getStdName() + "`");
      detail("location", "`" + e.getValue().getInfo().getLocation() + "`");
      detail("project", "_" + e.getValue().getInfo().getProject() + "_");
      detail("layer", e.getValue().getInfo().getLayer());
      detail("flags", "`" + Integer.toString(e.getValue().getInfo().getFlags()) + "`");
      writeCollection("interfaces", e.getValue().getInfo().getInterfaces());
      writeCollection("modelled-methods", e.getValue().getStdMethods());
      writeCollection("own-methods", e.getValue().getInfo().getMethods());
      writeln();
    }

    writeMinorSeparator("Native peers");

    for (Map.Entry<String, NativePeerInfo> e : info.getPeers().entrySet()) {
      writeln("==" + e.getKey() + "==");
      writeColoredType("NativePeer");
      detail("model", "`" + e.getValue().getModelName() + "`");
      detail("location", "`" + e.getValue().getInfo().getLocation() + "`");
      detail("project", "_" + e.getValue().getInfo().getProject() + "_");
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
      writeln("*type*: <font color=\"red\"><b>" + type + "</b></font>\n");
    } else if (type.contains("Inst")) {
      writeln("*type*: <font color=\"orange\"><b>" + type + "</b></font>\n");
    } else if (type.contains("Model")) {
      writeln("*type*: <font color=\"blue\"><b>" + type + "</b></font>\n");
    } else if (type.contains("Peer")) {
      writeln("*type*: <font color=\"green\"><b>" + type + "</b></font>\n");
    }
  }

  @Override
  public void writeCollection(String name, Set<String> set) {
    if (set.isEmpty()) {
      return;
    }

    writeln("===" + name + "===");
    indent();

    for (String e : set) {
      writeln("# `" + e + "`");
    }

    unindent();
  }

  @Override
  public void writeMinorSeparator(String id) {
    pw.println("=" + id + "=");
    pw.print("----\n");
  }

  @Override
  public void detail(String name, String value) {
    if (!value.isEmpty() && value != null) {
      writeln("*" + name + "*: " + value + "\n");
    }
  }
}
