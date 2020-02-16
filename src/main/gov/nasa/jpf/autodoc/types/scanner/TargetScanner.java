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
import gov.nasa.jpf.autodoc.types.NameUtils;
import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException;
import gov.nasa.jpf.autodoc.types.parser.Parser;
import gov.nasa.jpf.classfile.ClassFileException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Process projects, directories, archives and files to gather 
 * classfile information using a classfile parser.
 * 
 * @author Carlos Uribe
 */
public class TargetScanner implements Scanner {

  private Parser parser;
  private Set<ClassInfo> info = new TreeSet<ClassInfo>();

  public enum ScanType {

    PATHS      (0x1),
    JARS       (0x2),
    PATHS_JARS (0x3),
    ZIPS       (0x4),
    PATHS_ZIPS (0x5),
    ARCHIVES   (0x6),
    ALL        (0x7);
    
    int weight;

    ScanType(int weight) {
      this.weight = weight;
    }
  }

  public TargetScanner(Parser parser) {
    this.parser = parser;
  }

  @Override
  public Set<ClassInfo> getScanned() {
    return info;
  }
  
  /**
   * List classname and access flags for debugging purposes.
   */
  @Override
  public void list(PrintWriter pw) {
    pw.println(" -- listing scanned & parsed classes -- ");

    int i = 0;
    for (ClassInfo ci : info) {
      pw.println("  [" + i + "] " + ci.getName() + " | flags: "
                 + ci.getFlags());
      ++i;
    }
  }

  @Override
  public void scan(List<String> targets, List<String> classpath,
                   ScanType scanType)
          throws IOException, FileNotLoadedException, ClassFileException, 
          InvalidTargetException, ClassFileNotFoundException {
    String[] tgs = targets.toArray(new String[targets.size()]);
    String[] cpt = classpath.toArray(new String[classpath.size()]);
    scan(tgs, cpt, scanType);
  }

  @Override
  public void scan(String[] targets, String[] classpath, ScanType scanType)
          throws IOException, FileNotLoadedException, ClassFileException, 
          InvalidTargetException, ClassFileNotFoundException {
    ProjectScanner pScanner = new ProjectScanner(this);
    ArchiveReader aReader = new ArchiveReader();
    ScanData data = new ScanData();
    
    if ((targets == null) || (targets.length == 0)) {
      data = pScanner.scanAll(scanType);
      info.addAll(parser.parse(data.getFilelist()));
      info.addAll(parser.parseBytes(data.getDatalist()));
    } else {
      for (String target : targets) {
        if (NameUtils.isFileClassName(target)) {
          info.add(parser.parse(target));
        } else if (NameUtils.isPckClassName(target)) {
          info.add(parser.parse(target, classpath));
        } else if (NameUtils.isJarFilename(target) 
                   || NameUtils.isZipFilename(target)) {
          aReader.load(target);
          info.addAll(parser.parseBytes(aReader.readClassFiles()));
        } else if (pScanner.checkName(target)) {
          data = pScanner.scan(target, scanType);
          info.addAll(parser.parse(data.getFilelist()));
          info.addAll(parser.parseBytes(data.getDatalist()));
        } else if ((new File(target)).isDirectory()) {
          data = scan(target, scanType);
          info.addAll(parser.parse(data.getFilelist()));
          info.addAll(parser.parseBytes(data.getDatalist()));
        } else {
          throw new InvalidTargetException("Target is invalid. Not found: "
                                           + target);
        }
      }
    }
  }

  /**
   * Get a filtered list of files and data determined by scan type.
   * 
   * @param type Scan type member of <code>Scanner.ScanType</code> enumeration.
   *        For more info about options check ScanType.
   * @return a list of files and data stored in ScanData specified by type.
   */
  @Override
  public ScanData scan(String target, ScanType type)
          throws IOException, FileNotLoadedException, ClassFileException {
    ScanData scanData = new ScanData();

    if (isScanTypeEnabled(type, ScanType.PATHS)) {
      Set<String> files = fetchClassFiles(target);
      scanData.addFiles(files);
    }

    if (isScanTypeEnabled(type, ScanType.JARS)) {
      List<byte[]> data = resolve(fetchJarFiles(target));
      scanData.addData(data);
    }

    if (isScanTypeEnabled(type, ScanType.ZIPS)) {
      List<byte[]> data = resolve(fetchZipFiles(target));
      scanData.addData(data);
    }
    return scanData;
  }

  /**
   * Read byte data from a list of archives.
   */
  public List<byte[]> resolve(Set<String> archives)
          throws IOException, FileNotLoadedException, ClassFileException {
    ArchiveReader aReader = new ArchiveReader();
    List<byte[]> data = new ArrayList<byte[]>();

    for (String file : archives) {
      aReader.load(file);
      data.addAll(aReader.readClassFiles());
    }
    return data;
  }

  /**
   * Get a list of classfiles of a project.
   */
  @Override
  public Set<String> fetchClassFiles(String target) {
    FilenameFilter filter = new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return NameUtils.isFileClassName(name);
      }
    };
    return fetch(target, filter);
  }

  /**
   * Get a list of zip archives from a path.
   */
  @Override
  public Set<String> fetchZipFiles(String target) {
    FilenameFilter filter = new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return NameUtils.isZipFilename(name);
      }
    };
    return fetch(target, filter);
  }

  /**
   * Get a list of jar archives from a path.
   */
  @Override
  public Set<String> fetchJarFiles(String target) {
    FilenameFilter filter = new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return NameUtils.isJarFilename(name) 
               && NameUtils.isBuildFolder(dir.getName());
      }
    };
    return fetch(target, filter);
  }

  /**
   * Get a filtered list of files from a path.
   */
  @Override
  public Set<String> fetch(String target, FilenameFilter filter) {
    Set<String> files = new TreeSet<String>();
    File path = new File(target);
    files.addAll(seek(path, filter));
    return files;
  }

  /**
   * A generic recursive file finder with filename filter. It sorts 
   * file list in alphabetical order and removes repeated elements.
   * 
   * @param file It could be a folder or a file.
   * @param filter Crafted name filter of files. It specifies which files to 
   *        seek.
   * @return a list of files selected by the filename filter.
   */
  @Override
  public Set<String> seek(File file, FilenameFilter filter) {
    Set<String> names = new TreeSet<String>();

    if (file.isDirectory()) {
      for (File dir : file.listFiles()) {
        names.addAll(seek(dir, filter));
      }
    } else {
      if (filter.accept(file.getParentFile(), file.getName())) {
        names.add(file.getPath());
      }
    }
    return names;
  }

  @Override
  public boolean isScanTypeEnabled(ScanType type, ScanType mask) {
    return isScanTypeEnabled(type.weight, mask.weight);
  }

  @Override
  public boolean isScanTypeEnabled(int type, int mask) {
    return (type & mask) != 0;
  }

  @Override
  public ScanType getScanType(int mask) {
    for (ScanType type : ScanType.values()) {
      if (mask == type.weight) {
        return type;
      }
    }
    return ScanType.ALL;
  }
}
