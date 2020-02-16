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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class to storeParsed collections of classes, parsed and analyzed.
 *
 * @author Carlos Uribe <carlos.albert.uribe@gmail.com>
 */
public class CollectedInfo {

  private Map<String, ModelClassInfo> models = new TreeMap<String, ModelClassInfo>();
  private Map<String, ClassInfo> parsed = new TreeMap<String, ClassInfo>();
  private Map<String, NativePeerInfo> peers = new TreeMap<String, NativePeerInfo>();
  private Map<String, SubtypeInfo> subtypes = new TreeMap<String, SubtypeInfo>();
  private Map<String, SubtypeInfo> types = new TreeMap<String, SubtypeInfo>();

  public Map<String, ModelClassInfo> getModels() {
    return models;
  }

  public Map<String, ClassInfo> getParsed() {
    return parsed;
  }

  public Map<String, NativePeerInfo> getPeers() {
    return peers;
  }

  public Map<String, SubtypeInfo> getSubtypes() {
    return subtypes;
  }

  public Map<String, SubtypeInfo> getTypes() {
    return types;
  }

  public void setModels(Map<String, ModelClassInfo> models) {
    this.models = models;
  }

  public void setParsed(Map<String, ClassInfo> parsed) {
    this.parsed = parsed;
  }

  public void setPeers(Map<String, NativePeerInfo> peers) {
    this.peers = peers;
  }

  public void setSubtypes(Map<String, SubtypeInfo> subtypes) {
    this.subtypes = subtypes;
  }

  public void setTypes(Map<String, SubtypeInfo> types) {
    this.types = types;
  }

  public void register(ModelClassInfo model) {
    models.put(model.getInfo().getName(), model);
  }

  public void register(ClassInfo parsed) {
    this.parsed.put(parsed.getName(), parsed);
  }

  public void register(NativePeerInfo peer) {
    peers.put(peer.getInfo().getName(), peer);
  }

  public void register(SubtypeInfo subtype) {
    subtypes.put(subtype.getInfo().getName(), subtype);
  }

  public void registerType(SubtypeInfo type) {
    types.put(type.getInfo().getName(), type);
  }

  public void storeParsed(Set<ClassInfo> parsed) {
    for (ClassInfo cls : parsed) {
      this.parsed.put(cls.getName(), cls);
    }
  }

  public void storeTypes(Set<SubtypeInfo> types) {
    for (SubtypeInfo cls : types) {
      this.types.put(cls.getInfo().getName(), cls);
    }
  }

  public boolean isModel(String name) {
    if (name != null) {
      return models.containsKey(name);
    }

    return false;
  }

  public boolean isParsed(String name) {
    if (name != null) {
      return parsed.containsKey(name);
    }

    return false;
  }

  public boolean isPeer(String name) {
    if (name != null) {
      return peers.containsKey(name);
    }

    return false;
  }

  public boolean isSubtype(String name) {
    if (name != null) {
      return subtypes.containsKey(name);
    }

    return false;
  }

  public boolean isType(String name) {
    if (name != null) {
      return types.containsKey(name);
    }

    return false;
  }

  public ClassInfo getCls(String name) {
    return parsed.get(name);
  }

  public ModelClassInfo getModel(String name) {
    return models.get(name);
  }

  public NativePeerInfo getPeer(String name) {
    return peers.get(name);
  }

  public SubtypeInfo getSub(String name) {
    return subtypes.get(name);
  }

  public SubtypeInfo getType(String name) {
    return types.get(name);
  }
}
