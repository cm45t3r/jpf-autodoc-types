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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;

/**
 * Class to marshal and unmarshal XML code handling component information.
 * This class uses a Castor marshaller/unmarshaller to write/read XML.
 * 
 * @author Carlos Uribe [carlos.albert.uribe@gmail.com]
 */
public class XMLReaderWriter {

  /** Default XML mapping file. */
  public static final String DEF_MAP_FILE = "mapping.xml";
  /** Default XML file. */
  public static final String DEF_XML_FILE = "jpf-types.xml";
  
  private String file = new String();
  private Mapping mapping = new Mapping();
  private XMLContext context = new XMLContext();

  /**
   * Get the current XML file to marshal or unmarshal.
   */
  public String getFile() {
    return file;
  }
  
  /**
   * This method should be called before marshalling or unmarshalling.
   */
  public void setup() throws IOException, MappingException {
    mapping.loadMapping(DEF_MAP_FILE);
    context.addMapping(mapping);
  }
  
  /**
   * Marshal XML code to a file. This task is the opposite of Unmarshal.
   * 
   * @param info Collected information of components.
   * @param xmlFile Path to a XML file to write in.
   */
  public void marshal(CollectedInfo info, String xmlFile) throws IOException, 
         MarshalException, ValidationException {
    if (xmlFile == null || xmlFile.isEmpty()) {
      file = DEF_XML_FILE;
    } else {
      file = xmlFile;
    }
    
    FileWriter writer = new FileWriter(file);
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty("org.exolab.castor.indent", "true");
    marshaller.setWriter(writer);
    marshaller.marshal(info);
  }
  
  /**
   * Unmarshal XML code from a file. This task is the opposite of Marshal.
   * 
   * @param xmlFile Path to a XML file to read from.
   * @return Collected information of components.
   */
  public CollectedInfo unmarshal(String xmlFile) throws IOException, 
         MappingException, MarshalException, ValidationException {
    if (xmlFile == null || xmlFile.isEmpty()) {
      file = DEF_XML_FILE;
    } else {
      file = xmlFile;
    }
    
    FileReader reader = new FileReader(file);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    unmarshaller.setClass(CollectedInfo.class);
    return (CollectedInfo)unmarshaller.unmarshal(reader);
  }
}
