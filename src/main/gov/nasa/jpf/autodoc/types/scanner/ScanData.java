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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to store scanned lists of files and byte data.
 *
 * @author Carlos Uribe <carlos.albert.uribe@gmail.com>
 */
public class ScanData {

  private List<byte[]> datalist = new ArrayList<byte[]>();
  private List<String> filelist = new ArrayList<String>();

  /**
   * Get the value of datalist
   *
   * @return the value of datalist
   */
  public List<byte[]> getDatalist() {
    return datalist;
  }

  /**
   * Set the value of datalist
   *
   * @param datalist new value of datalist
   */
  public void setDatalist(List<byte[]> datalist) {
    this.datalist = datalist;
  }

  /**
   * Get the value of filelist
   *
   * @return the value of filelist
   */
  public List<String> getFilelist() {
    return filelist;
  }

  /**
   * Set the value of filelist
   *
   * @param filelist new value of filelist
   */
  public void setFilelist(List<String> filelist) {
    this.filelist = filelist;
  }

  public void addInfo(ScanData info) {
    addData(info.getDatalist());
    addFiles(info.getFilelist());
  }

  public void addData(Collection<byte[]> data) {
    datalist.addAll(data);
  }

  public void addFiles(Collection<String> files) {
    filelist.addAll(files);
  }

  @Override
  public String toString() {
    return "ScanData{" + "datalist=" + datalist + ", filelist=" + filelist + '}';
  }
}
