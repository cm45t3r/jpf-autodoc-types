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

import java.io.File;

/**
 * Class to check file, archive and project names or normalizeName names.
 * 
 * @author Carlos Uribe
 */
public class NameUtils {

  public static final String CLS_EXT = ".class";
  public static final String JAR_EXT = ".jar";
  public static final String ZIP_EXT = ".zip";
  public static final String WIN_SEP = "\\";
  public static final String UNX_SEP = "/";
  public static final String PCK_SEP = ".";
  public static final String MJI_SEP = "_";
  public static final String MJI_PFX = "JPF_";

  /**
   * Check if filename ends with .class extension and it is not a directory.<p>
   * Example: folder/MyFile.class is a valid name.<br>
   *          MyClass is not a valid name.
   */
  public static boolean isFileClassName(String filename) {
    File check = new File(filename);
    int idx = filename.lastIndexOf(UNX_SEP) + 1;

    if (idx == -1) {
      idx = filename.lastIndexOf(WIN_SEP) + 1;
    }

    return filename.toLowerCase().endsWith(CLS_EXT) && !check.isDirectory()
           && Character.isUpperCase(filename.charAt(idx));
  }

  /**
   * Check if filename ends with .jar extension and it is not a directory.<p>
   * Example: folder/MyArchive.jar is a valid name.<br>
   *          MyArchive is not a valid name.
   */
  public static boolean isJarFilename(String filename) {
    File check = new File(filename);
    return filename.toLowerCase().endsWith(JAR_EXT) && !(check.isDirectory());
  }

  /**
   * Check if filename ends with .zip extension and it is not a directory.<p>
   * Example: folder/MyArchive.zip is a valid name.<br>
   *          MyArchive is not a valid name.
   */
  public static boolean isZipFilename(String filename) {
    File check = new File(filename);
    return filename.toLowerCase().endsWith(ZIP_EXT) && !(check.isDirectory());
  }

  /**
   * Check if package name is lowercase, is separated by dots and classname 
   * starts with capital letter. A class located in default package is invalid.
   * <p>
   * Example: java.lang.Object is a valid name.<br>
   *          MyClass is not a valid name.
   */
  public static boolean isPckClassName(String classname) {
    return classname.length() > 2 && classname.contains(PCK_SEP)
           && !classname.contains(WIN_SEP) && !classname.contains(UNX_SEP)
           && Character.isUpperCase(classname.charAt(
            classname.lastIndexOf(PCK_SEP) + 1));
  }

  /**
   * Check if target contains 'jpf' and '-' but not file separators 
   * ('/' | '\') nor dot.<p>
   * Example: jpf-symbc is a valid name.<br>
   *          eclipse-jpf is a valid name.<br>
   *          myProject is not a valid name.
   */
  public static boolean isJPFProjectName(String project) {
    return project.toLowerCase().contains("jpf") && project.contains("-")
           && !project.contains(PCK_SEP) && !project.contains(WIN_SEP)
           && !project.contains(UNX_SEP);
  }

  /**
   * Check if pathname ends with 'build' and/or file separator ('/' | '\')<p>
   * Example: jpf-core\build is a valid name.<br>
   *          /home/.jpf/jpf-aprop/build/ is a valid name.<br>
   *          build.i is not a valid name.
   */
  public static boolean isBuildFolder(String pathname) {
    return pathname.endsWith("build") || pathname.endsWith("build" + WIN_SEP)
           || pathname.endsWith("build" + UNX_SEP);
  }
  
  public static boolean isNameMangled(String classname) {
    int count = 0;
    for (int i = 0; i < classname.length(); ++i) {
      if (MJI_SEP.charAt(0) == classname.charAt(i)) {
        ++count;
      }
    }
    return classname.contains(MJI_PFX) && count > 1;
  }

  /**
   * Normalize non-standard named package classnames replacing separators
   * for standard package dot separator.
   */
  public static String normalizeName(String classname) {
    return classname.replace(UNX_SEP, PCK_SEP);
  }
  
  public static String normalizePath(String path) {
    if (path.contains(":") && getSeparator().equals(WIN_SEP)) {
      return path.replace(UNX_SEP, WIN_SEP);
    } else {
      return path.replace(WIN_SEP, UNX_SEP);
    }
  }
  
  public static String getSeparator() {
    return System.getProperty("file.separator");
  }
}
