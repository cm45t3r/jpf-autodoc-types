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

package gov.nasa.jpf.autodoc.types.parser;

import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.classfile.ClassFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to parse ready targets.
 * 
 * @author Carlos Uribe <carlos.albert.uribe@gmail.com>
 */
public class TargetParser implements Parser {

  private ClassFileParser parser = new ClassFileParser();

  @Override
  public ClassInfo parse(String file) throws ClassFileException {
    parser.parse(file);
    return parser.getClassInfo();
  }

  @Override
  public ClassInfo parse(String file, ClassPath classpath)
          throws ClassFileException, ClassFileNotFoundException {
    parser.parse(file, classpath);
    return parser.getClassInfo();
  }
  
  @Override
  public ClassInfo parse(String file, String[] pathnames)
          throws ClassFileException, ClassFileNotFoundException {
    parser.parse(file, pathnames);
    return parser.getClassInfo();
  }
  
  @Override
  public List<ClassInfo> parse(List<String> filelist)
          throws ClassFileException {
    List<ClassInfo> parsed = new ArrayList<ClassInfo>();

    for (String file : filelist) {
      parsed.add(parse(file));
    }
    return parsed;
  }
  
  @Override
  public List<ClassInfo> parse(List<String> namelist, String[] pathnames)
          throws ClassFileException, ClassFileNotFoundException {
    List<ClassInfo> parsed = new ArrayList<ClassInfo>();

    for (String file : namelist) {
      parsed.add(parse(file, pathnames));
    }
    return parsed;
  }
  
  @Override
  public List<ClassInfo> parseBytes(List<byte[]> datalist) {
    List<ClassInfo> parsed = new ArrayList<ClassInfo>();
    
    for (byte[] data : datalist) {
      try {
        parser.parse(data);
        parsed.add(parser.getClassInfo());
      } catch (ClassFileException ex) {
        //--- TODO: illegal constpool tag exception when parsing some classes.
        //--- Log here
      }
    }
    return parsed;
  }
}
