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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Class to read data from .zip and .jar archives.
 * 
 * @author Carlos Uribe
 */
public class ArchiveReader {

  private ZipFile archive;

  /**
   * Load an archive from a file path.
   * 
   * @throws IOException if file was not found in path.
   */
  public void load(String path) throws IOException {
    archive = new ZipFile(path);
  }

  /**
   * Load an archive from a Java <code>File</code>.
   * 
   * @throws ZipException if file is corrupted.
   * @throws IOException if file was not found.
   */
  public void load(File file) throws ZipException, IOException {
    archive = new ZipFile(file);
  }

  /**
   * Read bytes from all packaged files of an archive.
   * 
   * @return a collection of bytes for each file read.
   * @throws IOException if something is wrong with archive.
   * @throws FileNotLoadedException if load has not been executed yet.
   */
  public List<byte[]> readArchive()
          throws IOException, FileNotLoadedException {
    if (archive == null) {
      throw new FileNotLoadedException("Archive must be loaded first.");
    }

    List<byte[]> data = new ArrayList<byte[]>();

    for (Enumeration<? extends ZipEntry> entries = archive.entries();
         entries.hasMoreElements();) {
      ZipEntry entry = (ZipEntry)entries.nextElement();
      data.add(readEntry(archive, entry));
    }
    return data;
  }

  /**
   * Read bytes from an archive of classfiles only. It is useful to get
   * classfiles from a .jar archive.
   * 
   * @return a collection of bytes for each classfile read.
   * @throws IOException if something is wrong with archive.
   * @throws FileNotLoadedException if load has not been executed yet.
   */
  public List<byte[]> readClassFiles()
          throws IOException, FileNotLoadedException {
    if (archive == null) {
      throw new FileNotLoadedException("Archive must be loaded first.");
    }

    List<byte[]> data = new ArrayList<byte[]>();

    for (Enumeration<? extends ZipEntry> entries = archive.entries();
         entries.hasMoreElements();) {
      ZipEntry entry = (ZipEntry)entries.nextElement();

      if (NameUtils.isFileClassName(entry.getName())) {
        data.add(readEntry(archive, entry));
      }
    }
    return data;
  }

  /**
   * Read a single file from an archive.
   * 
   * @throws IOException if something is wrong with archive.
   * @throws FileNotLoadedException if load has not been executed yet.
   */
  public byte[] readInArchive(String filename)
          throws IOException, FileNotLoadedException {
    if (archive == null) {
      throw new FileNotLoadedException("Archive must be loaded first.");
    }

    ZipEntry entry = archive.getEntry(filename);
    return readEntry(archive, entry);
  }

  /**
   * Read bytes from a ZipFile and a ZipEntry. Since JarFile extends ZipFile
   * and JarEntry extends ZipEntry this method works well for both types of
   * archives.
   */
  public byte[] readEntry(ZipFile file, ZipEntry entry) throws IOException {
    InputStream stream = file.getInputStream(entry);
    return read(stream, (int)entry.getSize());
  }

  /**
   * Read bytes from a filepath on disk.
   */
  public byte[] readFile(String path)
          throws FileNotFoundException, IOException {
    File file = new File(path);
    return readFile(file);
  }

  /**
   * Read bytes from a file on disk.
   */
  public byte[] readFile(File file) throws FileNotFoundException, IOException {
    FileInputStream stream = new FileInputStream(file);
    return read(stream, (int)file.length());
  }

  /**
   * Read bytes from an InputStream.<p>
   * NOTE - Size is not bigger than (2^32)-1 bytes to read 
   * single files because classfiles in particular don't exceed 
   * this amount and is limited to default jvm heap space.
   */
  public byte[] read(InputStream stream, int size) throws IOException {
    byte[] data = new byte[size];
    stream.read(data);
    return data;
  }
}
