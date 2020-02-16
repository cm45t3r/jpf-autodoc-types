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

package gov.nasa.jpf.test.autodoc.types.scanner;

import java.io.PrintWriter;
import gov.nasa.jpf.autodoc.types.scanner.ScanData;
import java.util.List;
import java.util.TreeSet;
import java.util.Set;
import gov.nasa.jpf.autodoc.types.NameUtils;
import java.io.FilenameFilter;
import java.io.File;
import gov.nasa.jpf.autodoc.types.scanner.TargetScanner.ScanType;
import gov.nasa.jpf.autodoc.types.scanner.TargetScanner;
import gov.nasa.jpf.autodoc.types.parser.TargetParser;
import gov.nasa.jpf.autodoc.types.parser.Parser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case for TargetScanner.
 * 
 * @author Carlos Uribe
 */
public class TargetScannerTest {
  
  public TargetScannerTest() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  /**
   * Test of list method, of class TargetScanner.
   */
  @Test
  public void testList() {
    System.out.println("method> list");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    PrintWriter pw = new PrintWriter(System.out, true);
    scanner.list(pw);
  }

  /**
   * Test of scan method, of class TargetScanner.
   */
  @Test
  public void testScan_3args_1() throws Exception {
  }

  /**
   * Test of scan method, of class TargetScanner.
   */
  @Test
  public void testScan_3args_2() throws Exception {
  }

  /**
   * Test of scan method, of class TargetScanner.
   */
  @Test
  public void testScan_String_TargetScannerScanType() throws Exception {
    System.out.println("method> scan(String, ScanType)");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    String target = ".";
    ScanData scanData = scanner.scan(target, ScanType.JARS);
    
    List<byte[]> data = scanData.getDatalist();
    List<String> files = scanData.getFilelist();
    
    System.out.println(" jars_");
    System.out.println("  data count: " + data.size());
    System.out.println("  files count: " + files.size());
    assertFalse(data.isEmpty());
    assertTrue(files.isEmpty());
    
    scanData = scanner.scan(target, ScanType.PATHS);
    
    data = scanData.getDatalist();
    files = scanData.getFilelist();
    
    System.out.println(" paths_");
    System.out.println("  data count: " + data.size());
    System.out.println("  files count: " + files.size());
    assertTrue(data.isEmpty());
    assertFalse(files.isEmpty());
    
    scanData = scanner.scan(target, ScanType.ZIPS);
    
    data = scanData.getDatalist();
    files = scanData.getFilelist();
    
    System.out.println(" zips_");
    System.out.println("  data count: " + data.size());
    System.out.println("  files count: " + files.size());
    assertTrue(data.isEmpty());
    assertTrue(files.isEmpty());
    
    scanData = scanner.scan(target, ScanType.ALL);
    
    data = scanData.getDatalist();
    files = scanData.getFilelist();
    
    System.out.println(" all_");
    System.out.println("  data count: " + data.size());
    System.out.println("  files count: " + files.size());
    assertFalse(data.isEmpty());
    assertFalse(files.isEmpty());
  }

  /**
   * Test of resolve method, of class TargetScanner.
   */
  @Test
  public void testResolve() throws Exception {
    System.out.println("method> resolve");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    Set<String> jars = new TreeSet<String>();
    jars.add("build/jpf-autodoc-types.jar");
    
    List<byte[]> data = scanner.resolve(jars);
    System.out.println("  count: " + data.size());
    assertNotNull(data);
  }

  /**
   * Test of fetchClassFiles method, of class TargetScanner.
   */
  @Test
  public void testFetchClassFiles() {
    System.out.println("method> fetchClassFiles");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    int filecount = 12;
    Set<String> set = scanner.fetchClassFiles("test");
    System.out.println(set);
    assertEquals(filecount, set.size());
  }

  /**
   * Test of fetchZipFiles method, of class TargetScanner.
   */
  @Test
  public void testFetchZipFiles() {
    System.out.println("method> fetchZipFiles");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    int filecount = 0;
    Set<String> set = scanner.fetchZipFiles(".");
    System.out.println(set);
    assertEquals(filecount, set.size());
  }

  /**
   * Test of fetchJarFiles method, of class TargetScanner.
   */
  @Test
  public void testFetchJarFiles() {
    System.out.println("method> fetchJarFiles");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    int filecount = 1;
    Set<String> set = scanner.fetchJarFiles(".");
    System.out.println(set);
    assertEquals(filecount, set.size());
  }

  /**
   * Test of fetch method, of class TargetScanner.
   */
  @Test
  public void testFetch() {
    System.out.println("method> fetch");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    FilenameFilter filter = new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return NameUtils.isFileClassName(name);
      }
    };
    
    int filecount = 12;
    Set<String> set = scanner.fetch("test", filter);
    
    System.out.println(set);
    assertEquals(filecount, set.size());
  }

  /**
   * Test of seek method, of class TargetScanner.
   */
  @Test
  public void testSeek() {
    System.out.println("method> seek");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    File file = new File("test");
    FilenameFilter filter = new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return NameUtils.isFileClassName(name);
      }
    };
    
    int filecount = 12;
    Set<String> set = scanner.seek(file, filter);
    
    System.out.println(set);
    assertEquals(filecount, set.size());
  }

  /**
   * Test of getScanType method, of class TargetScanner.
   */
  @Test
  public void testGetScanType() {
    System.out.println("method> getScanType");
    
    Parser parser = new TargetParser();
    TargetScanner scanner = new TargetScanner(parser);
    
    ScanType type1 = scanner.getScanType(0x1);
    ScanType type2 = scanner.getScanType(0x2);
    ScanType type3 = scanner.getScanType(0x3);
    ScanType type4 = scanner.getScanType(0x4);
    ScanType type5 = scanner.getScanType(0x5);
    ScanType type6 = scanner.getScanType(0x6);
    ScanType type7 = scanner.getScanType(0x7);
    
    System.out.println("  " + type1 + ", " + type2 + ", " + type3 + ", " 
                       + type4 + ", " + type5 + ", " + type6 + ", " + type7);
    
    assertEquals(ScanType.PATHS, type1);
    assertEquals(ScanType.JARS, type2);
    assertEquals(ScanType.PATHS_JARS, type3);
    assertEquals(ScanType.ZIPS, type4);
    assertEquals(ScanType.PATHS_ZIPS, type5);
    assertEquals(ScanType.ARCHIVES, type6);
    assertEquals(ScanType.ALL, type7);
  }
}
