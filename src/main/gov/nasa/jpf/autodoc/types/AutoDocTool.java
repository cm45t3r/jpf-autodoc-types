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

package gov.nasa.jpf.autodoc.types;

import gov.nasa.jpf.autodoc.types.analyzer.AnalysisTrigger;
import gov.nasa.jpf.autodoc.types.info.ClassInfo;
import gov.nasa.jpf.autodoc.types.info.CollectedInfo;
import gov.nasa.jpf.autodoc.types.info.SubtypeInfo;
import gov.nasa.jpf.autodoc.types.output.Writer;
import gov.nasa.jpf.autodoc.types.output.WriterFactory;
import gov.nasa.jpf.autodoc.types.output.XMLReaderWriter;
import gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException;
import gov.nasa.jpf.autodoc.types.parser.Parser;
import gov.nasa.jpf.autodoc.types.parser.TargetParser;
import gov.nasa.jpf.autodoc.types.scanner.InvalidTargetException;
import gov.nasa.jpf.autodoc.types.scanner.ProjectScanner;
import gov.nasa.jpf.autodoc.types.scanner.Scanner;
import gov.nasa.jpf.autodoc.types.scanner.TargetScanner;
import gov.nasa.jpf.classfile.ClassFileException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

/**
 * Automatic Documentation of JPF Components. This class handles command-line 
 * options, configures the tool, starts scans and analyses, collects info and 
 * controls the output.
 * 
 * @author Carlos Uribe [carlos.albert.uribe@gmail.com]
 */
public class AutoDocTool {
  
  /**
   * Command-line options to control {@link AutoDocTool} functionalities.
   */
  public enum Options {
    
    /** Enable analysis of JPF Listeners. */
    LISTENERS     (0x1,     false, "-aL,-listeners,--listeners"),
    /** Enable analysis of JPF Instruction Factories. */
    IFACTORIES    (0x2,     false, "-aI,-ifactories,--ifactories"),
    /** Enable analysis of JPF Model Classes. */
    MODELCLASSES  (0x4,     false, "-aM,-models,--models"),
    /** Enable analysis of JPF Native Peer Classes. */
    NATIVEPEERS   (0x8,     false, "-aP,-peers,--peers"),
    /** Enable analyses of all JPF components. */
    ANALYZE_ALL   (0xF,     false, "-aA,--analyze-all"),
    
    /** Enable scan directories to find classfiles. */
    DIRECTORIES   (0x10,   false, "-sD,-dirs,--dirs"),
    /** Enable scan JAR archives to find classfiles. */
    JAR_FILES     (0x20,   false, "-sJ,-jars,--jars"),
    /** Enable scan ZIP archives to find classfiles. */
    ZIP_FILES     (0x40,   false, "-sZ,-zips,--zips"),
    /** Enable all scan options. */
    SCAN_ALL      (0x70,   false, "-sA,--scan-all"),
    
    /** Verbosity Level 1 for console output. */
    OUT_LEVEL1    (0x100,    false, "-o1,-v"),
    /** Verbosity Level 2 for console output. */
    OUT_LEVEL2    (0x200,    false, "-o2,-vv"),
    /** Allow writing output to a text file. */
    TEXTFILE      (0x400,    true,  "-oT,-text,--text"),
    /** Allow writing output to a xml file. */
    XML           (0x800,    true,  "-oX,-xml,--xml"),
    /** Allow writing output to a wiki file. */
    WIKI          (0x1000,   true,  "-oW,-wiki,--wiki"),
    /** Allow writing output to a wiki file. */
    MARKDOWN      (0x2000,   true,  "-oM,-md,--md"),
    /** Enable all output options with verbosity level 2.*/
    OUTPUT_ALL    (0x3E00,   true,  "-oA,--output-all"),
        
    /** Enable classpath settings. */
    CLASSPATH     (0x10000,  true,  "-cp,-classpath"),
    /** Enable show help screen. */
    HELP          (0x20000,  false, "-h,-?,-help,--help"),
    /** Enable show tool version. */
    VER           (0x40000,  false, "-V,-ver,-version,--version"),
    /** Enable show properties loaded. */
    CONFIG        (0x80000,  false, "-show,--show,-config,--config"),
    
    /** Enable debug mode for scan. */
    DEBUG_SCAN    (0x100000,  true,  "-dS,-debug-scan,--debug-scan"),
    /** Enable automated scan output integrity check. */
    CHECK_DSCAN   (0x200000,  true,  "-cS,-check-scan,--check-scan"),
    /** Enable automated xml integrity check. */
    CHECK_DXML    (0x400000,  true,  "-cX,-check-xml,--check-xml");
    
    private final int weight;
    private final boolean hasParam;
    private final String[] options;

    Options(int weight, boolean hasParams, String options) {
      this.weight = weight;
      this.hasParam = hasParams;
      this.options = options.split(",");
    }

    int getParamPosInList(String optlist[]) {
      if (hasParam) {
        int opt = getPosInList(optlist);
        
        if (opt >= 0 && opt < optlist.length - 1) {
          return opt + 1;
        }
      }
      return -1;
    }

    int getPosInList(String optlist[]) {
      for (int idx = 0; idx < optlist.length; ++idx) {
        if (matches(optlist[idx])) {
          return idx;
        }
      }
      return -1;
    }

    boolean matches(String expression) {
      return Arrays.asList(options).contains(expression);
    }
  }
  
  private static List<String> targets = new ArrayList<String>();
  private static List<String> classpath = new ArrayList<String>();
  
  private static long startTime = 0;
  private static long endTime = 0;
  
  private static Writer text;
  private static Writer console;
  private static Scanner scanner;
  private static Parser parser;
  private static final ConfigLoader config = new ConfigLoader();
  private static CollectedInfo info = new CollectedInfo();
  private static final AnalysisTrigger trigger = new AnalysisTrigger();
  
  /**
   * Entry point for the tool.
   * 
   * @param args Command-line arguments for the tool.
   */
  public static void main(String args[]) {
    startTime = System.currentTimeMillis();
    int options = getOptions(args);
    options |= setDefaults(options);

    try {
      System.setErr(new PrintStream("jpfadt.log"));
      console = WriterFactory.createConsole();
      parser = new TargetParser();
      
      configure();
      checkNonRetOpts(args, options);
      checkParams(args, options);
      
      showTitle();
      console.writeHeader();
      showParams();
      showEnabled(options);
      
      startScan(options);
      startAnalyses(options);
      checkOutputOpts(args, options);
      endTime = System.currentTimeMillis();
      statistics();
      console.writeFooter();
      
    } catch (IOException ex) {
      console.error("\n[" + Level.SEVERE + "] I/O error. " + ex);
    } catch (FileNotLoadedException ex) {
      console.error("[" + Level.SEVERE + "] Error trying to load file. " + ex);
    } catch (ClassFileException ex) {
      console.error("[" + Level.WARNING + "] Error parsing file. " + ex);
    } catch (InvalidTargetException ex) {
      console.error("[" + Level.SEVERE + "] Error scanning target. " + ex);
    } catch (MappingException ex) {
      console.error("[" + Level.WARNING + "] Error mapping XML file. " + ex);
    } catch (MarshalException ex) {
      console.error("[" + Level.SEVERE + "] Error marshalling XML file. " + ex);
    } catch (ValidationException ex) {
      console.error("[" + Level.WARNING + "] XML error. " + ex);
    } catch (ClassFileNotFoundException ex) {
      console.error("[" + Level.SEVERE + "] Error parsing file. " + ex);
    }
  }
  
  /**
   * Set default values for command-line options. Default options are overriden 
   * if any {@link Options} is specified.
   * 
   * @param options Bitwise value denoting specified options.
   * @return bitwise value denoting default options enabled.
   */
  public static int setDefaults(int options) {
    int mask = 0;
    
    if ((options & Options.ANALYZE_ALL.weight) == 0) {
      mask |= Options.ANALYZE_ALL.weight; 
    }
    
    if ((options & Options.OUTPUT_ALL.weight) == 0) {
      mask |= Options.XML.weight;
    }
    
    if ((options & Options.SCAN_ALL.weight) == 0) {
      mask |= Options.DIRECTORIES.weight;
    }
    
    return mask;
  }
  
  /**
   * Load configuration files and prepare JPF components for analyses.
   */
  static void configure() throws IOException, ClassFileException, 
                                              ClassFileNotFoundException {
    config.loadConfig("config.properties");
    
    String[] pathnames = config.getPropertyVals("SubtypeAnalyzer.classpath");
    
    for (String key : config.getLeafs()) {
      ClassInfo cls = parser.parse(config.getProperty(key), pathnames);
      SubtypeInfo type = new SubtypeInfo();
      type.setInfo(cls);
      type.setType(config.getType(key));
      info.register(cls);
      info.registerType(type);
    }
    
    config.loadConfig("build.properties");
  }

  /**
   * Check those {@link Options} that show something and halt.
   */
  static void checkNonRetOpts(String[] args, int options) {
    if (isOptionEnabled(options, Options.HELP.weight)) {
      showUsage();
      System.exit(0);
    }

    if (isOptionEnabled(options, Options.VER.weight)) {
      showVersion();
      System.exit(0);
    }

    if (isOptionEnabled(options, Options.CONFIG.weight)) {
      showTitle();
      config.list(console.getPrintWriter());
      System.exit(0);
    }
  }

  /**
   * Parse and check classpath if enabled and parse targets.
   * 
   * @param args Command line arguments.
   * @param options Bitwise value denoting enabled options.
   */
  static void checkParams(String[] args, int options) {
    if (isOptionEnabled(options, Options.CLASSPATH.weight)) {
      classpath = parseClassPath(args);
    }

    targets = parseTargets(args);

    if (!isInputCoherent(targets, classpath)) {
      showUsage();
      System.exit(0);
    }
  }
  
  /**
   * Show current enabled options.
   * 
   * @param options Bitwise value denoting enabled options.
   */
  public static void showEnabled(int options) {
    String en = new String();
    
    for (Options op : Options.values()) {
      if (isOptionEnabled(options, op.weight)) {
        if (((op.weight << 1) & op.weight) == 0) {
          en += op.name().toLowerCase() + ", ";
        }
      }
    }
    en = en.isEmpty() ? en : en.substring(0, en.length() - 2);
    console.writeParam("enabled", en);
  }

  /**
   * Extract classpath names from command-line arguments.
   * 
   * @param args Command line arguments.
   * @return a list of pathnames.
   */
  public static List<String> parseClassPath(String args[]) {
    int idx = Options.CLASSPATH.getParamPosInList(args);
    List<String> pnames = new ArrayList<String>();

    if (idx != -1) {
      String sep = System.getProperty("path.separator");
      pnames.addAll(Arrays.asList(args[idx].split(sep)));
    }
    return pnames;
  }

  /**
   * Extract targets from command-line arguments.
   * 
   * @param args Command line arguments.
   * @return a list of valid targets.
   */
  public static List<String> parseTargets(String args[]) {
    List<String> tgs = new LinkedList<String>();
    tgs.addAll(Arrays.asList(args));
    int i, j;
    
    for (Options opt : Options.values()) {
      String[] remain = tgs.toArray(new String[tgs.size()]);
      i = opt.getPosInList(remain);
      j = opt.getParamPosInList(remain);
      
      if (i >= 0 && tgs.size() > 0) {
        tgs.remove(i); 
        
        if (j > 0 && tgs.size() > 0) {
          tgs.remove(j - 1);
        }
      }
    }
    return tgs;
  }

  /**
   * Extract specified option argument from command-line args.
   * 
   * @param args Command line arguments.
   * @param op Value of {@link Options}.
   * @return option argument.
   */
  public static String getOptionArg(String[] args, Options op) {
    int idx = op.getParamPosInList(args);
    
    if (args.length >= idx && idx >= 0) {
      if (!args[idx].startsWith("-")) {
        return args[idx];
      }
    }
    return new String();
  }

  /**
   * Parse options from command-line arguments.
   * 
   * @param args Command line arguments.
   * @return bitwise value denoting enabled options.
   */
  public static int getOptions(String args[]) {
    int mask = 0;
    for (String arg : args) {
      for (Options op : Options.values()) {
        if (op.matches(arg)) {
          mask |= op.weight;
        }
      }
    }
    return mask;
  }

  /**
   * Check against a bitwise mask if an option is enabled.
   * @param option
   * @param mask
   * @return 
   */
  public static boolean isOptionEnabled(int option, int mask) {
    return (mask & option) != 0;
  }

  /**
   * Named package classes must have an associated classpath. If not, this 
   * method returns false.
   * 
   * @param tgs A list of targets.
   * @param cp A list of classpath pathnames.
   * @return true if classnames are associated with a classpath.
   */
  public static boolean isInputCoherent(List<String> tgs, List<String> cp) {
    boolean clsfiles = false;
    boolean pcknames = false;

    for (String t : tgs) {
      if (NameUtils.isFileClassName(t)) {
        clsfiles = true;
      }

      if (NameUtils.isPckClassName(t)) {
        pcknames = true;
      }
    }

    if (pcknames && cp.isEmpty()) {
      return false;
    }

    return clsfiles || pcknames || cp.isEmpty();
  }

  /**
   * Scan registered targets and store results.
   * 
   * @param options Bitwise value denoting scan options.
   * @throws java.io.IOException
   * @throws gov.nasa.jpf.autodoc.types.FileNotLoadedException
   * @throws gov.nasa.jpf.classfile.ClassFileException
   * @throws gov.nasa.jpf.autodoc.types.scanner.InvalidTargetException
   * @throws gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException
   */
  protected static void startScan(int options)
          throws IOException, FileNotLoadedException, ClassFileException, 
          InvalidTargetException, ClassFileNotFoundException {
    scanner = new TargetScanner(parser);
    
    int opts = (options & 0xF0) >> 0x4;
    
    scanner.scan(targets, classpath, scanner.getScanType(opts));
    info.storeParsed(scanner.getScanned());
  }

  /**
   * Prepare classpath for analyzers, start analyses and store results.
   * 
   * @param options Bitwise value denoting analyses options.
   * @throws gov.nasa.jpf.autodoc.types.parser.ClassFileNotFoundException
   */
  protected static void startAnalyses(int options) 
          throws ClassFileNotFoundException {
    trigger.addModelPath(System.getProperty("sun.boot.class.path"));
    
    trigger.addTypePaths(config.getPropertyVals(
            "SubtypeAnalyzer.classpath"));
    trigger.addPeerPaths(config.getPropertyVals(
            "NativePeerAnalyzer.classpath"));
    trigger.addModelPaths(config.getPropertyVals(
            "ModelClassAnalyzer.classpath"));
    
    ProjectScanner pscanner = new ProjectScanner(scanner);
    FilenameFilter filter = new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return NameUtils.isJarFilename(name) && name.endsWith("-classes.jar")
               && NameUtils.isBuildFolder(dir.getName());
      }
    };
    
    Set<String> jars = pscanner.fetchAllFiles(filter);
    trigger.addPeerPaths(jars.toArray(new String[jars.size()]));
    
    int opts = options & 0xF;
    
    info = trigger.startAnalyses(info, opts);
  }

  /**
   * Check output options and call output writers to show collected info.
   * 
   * @param args Command line arguments.
   * @param options Bitwise value denoting output options.
   * @throws java.io.IOException
   * @throws org.exolab.castor.mapping.MappingException
   * @throws org.exolab.castor.xml.MarshalException
   * @throws org.exolab.castor.xml.ValidationException
   */
  public static void checkOutputOpts(String[] args, int options) 
         throws IOException, MappingException, MarshalException, 
         ValidationException {
    String debug = new String();
    
    if (isOptionEnabled(options, Options.DEBUG_SCAN.weight)) {
      debug = getOptionArg(args, Options.DEBUG_SCAN);
      
      if (debug.isEmpty()) {
        debug = "debug-scan.txt";
      }
      
      text = WriterFactory.createFileWriter(debug);
      scanner.list(text.getPrintWriter());
      
      if (isOptionEnabled(options, Options.OUT_LEVEL1.weight) 
          || isOptionEnabled(options, Options.OUT_LEVEL2.weight)) {
        console.writeMajorSeparator("scan");
        scanner.list(console.getPrintWriter());
      }
    }
    
    if (isOptionEnabled(options, Options.OUT_LEVEL1.weight)) {
      console.writeMajorSeparator("analysis result");
      console.writeInfo(info);
    } else if (isOptionEnabled(options, Options.OUT_LEVEL2.weight)) {
      console.writeMajorSeparator("analysis result");
      console.writeDetailedInfo(info);
    }
    
    console.writeMajorSeparator("status");
    console.writeParam("writing debug file", debug);
    
    if (isOptionEnabled(options, Options.TEXTFILE.weight)) {
      String file = getOptionArg(args, Options.TEXTFILE);
      
      if (file.isEmpty()) {
        file = "jpf-types.txt";
      }
      
      text = WriterFactory.createFileWriter(file);
      text.writeDetailedInfo(info);
      console.writeParam("writing text to", file);
    }

    if (isOptionEnabled(options, Options.XML.weight)) {
      String arg = getOptionArg(args, Options.XML);
      XMLReaderWriter xml = new XMLReaderWriter();
      
      xml.setup();
      xml.marshal(info, arg);
      console.writeParam("writing xml to", xml.getFile());
    }
    
    if (isOptionEnabled(options, Options.WIKI.weight)) {
      String file = getOptionArg(args, Options.WIKI);
      
      if (file.isEmpty()) {
        file = "jpf-types.wiki";
      }
      
      text = WriterFactory.createWikiWriter(file);
      text.writeDetailedInfo(info);
      console.writeParam("writing wiki to", file);
    }
    
    if (isOptionEnabled(options, Options.MARKDOWN.weight)) {
      String file = getOptionArg(args, Options.MARKDOWN);
      
      if (file.isEmpty()) {
        file = "jpf-types.md";
      }
      
      text = WriterFactory.createMarkdownWriter(file);
      text.writeDetailedInfo(info);
      console.writeParam("writing markdown to", file);
    }
    
    console.write("done!");
  }
  
  /**
   * Show some data such as analyzed classes count
   */
  public static void statistics() {
    String secs = String.format("%.3g", (float)((endTime - startTime)) / 1000);
    int np = info.getParsed().size();
    int ns = info.getSubtypes().size();
    int nm = info.getModels().size();
    int nn = info.getPeers().size();
    int total = ns + nm + nn;
    
    console.writeMajorSeparator("statistics");
    console.detail("total elapsed time", secs + " sec");
    console.detail("analyzed subtypes", Integer.toString(ns));
    console.detail("analyzed models", Integer.toString(nm));
    console.detail("analyzed nat peers", Integer.toString(nn));
    console.detail("total components", Integer.toString(total));
    console.detail("total parsed classes", Integer.toString(np));
    console.detail("other parsed classes", Integer.toString(np - total));
  }

  /**
   * Show targets and classpath if exists.
   */
  public static void showParams() {
    if (targets.isEmpty()) {
      console.writeParam("targets", "all projects");
    } else {
      console.writeParam("targets", targets.toString());
    }
    
    if (!classpath.isEmpty()) {
      console.writeParam("classpath", classpath.toString());
    }
  }
  
  /**
   * Show application title.
   */
  public static void showTitle() {
    showVersion();
  }

  /**
   * Load build properties and show it.
   */
  public static void showVersion() {
    console.writeTitle(config.getProperty("app.name"),
                       config.getProperty("version"),
                       config.getProperty("release"),
                       config.getProperty("vendor"), null);
  }

  /**
   * Show help screen.
   */
  public static void showUsage() {
    console.writeln("Usage: jpfadt [<options>] {<target specification>}\n");
    console.writeln("<target specification> :: ");
    console.writeln("  classfile : path or filename of a java class file. | ");
    console.writeln("  classname : named package classname. It should be "
                    + "together with classpath ");
    console.writeln("              specification. | ");
    console.writeln("  archive : file with .jar/.zip extension containing class"
                    + " files or nested ");
    console.writeln("            archive files with more class files. | ");
    console.writeln("  folder : directory where to look for class files. | ");
    console.writeln("  jpf-project-name : name of a jpf project (extension) "
                    + "registered in ");
    console.writeln("                     site.properties config file.\n");
    console.writeln("  NOTE: If no target specified, it processes all projects "
                    + "in site.properties. ");
    console.writeln("        You can mix different kinds of targets to get "
                    + "particular results.");
    console.writeln("        e.g. jpfadt jpf-core build/.../Config.class "
                    + "build/jpf.jar\n");
    console.writeln("<options> :: ");
    console.writeln("  [<classpath>] {<scan>} {<analysis>} {<output>} [<misc>]"
                    + "\n");
    console.writeln("<classpath> :: ");
    console.writeln("  -cp <path> : short classpath specification. | ");
    console.writeln("  -classpath <path> : long classpath specification.\n");
    console.writeln("<path> :: ");
    console.writeln("  (Unix-like/Mac OS X)");
    console.writeln("  pathname0:pathname1:...:pathnameN : A : separated list "
                    + "of JAR archives or ");
    console.writeln("                                      directories to "
                    + "search for class files. | ");
    console.writeln("  (Windows)");
    console.writeln("  pathname0;pathname1;...;pathnameN : A ; separated list "
                    + "of JAR archives or ");
    console.writeln("                                      directories to "
                    + "search for class files.\n");
    console.writeln("<scan> :: ");
    console.writeln("  -sD | -dirs | --dirs | --folders : scan into directories"
                    + " only. (default mode) | ");
    console.writeln("  -sJ | -jars | --jars : scan into JAR archives only. | ");
    console.writeln("  -sZ | -zips | --zips : scan into ZIP archives only. | ");
    console.writeln("  -sA | --scan-all : scan directories and archives.\n");
    console.writeln("  NOTE: If no scan type specified, it runs default mode. "
                    + "You can mix ");
    console.writeln("        options to get particular results.");
    console.writeln("        e.g. jpfadt -sJ --zips\n");
    console.writeln("<analysis> :: ");
    console.writeln("  -aL | -listeners | --listeners : analyze Listeners "
                    + "only. | ");
    console.writeln("  -aI | -ifactories | --ifactories : analyze Instruction "
                    + "Factories only. | ");
    console.writeln("  -aM | -models | --models : analyze Model Classes only."
                    + " | ");
    console.writeln("  -aP | -peers | --peers : analyze Native Peer classes "
                    + "only. | ");
    console.writeln("  -aA | --analyze-all : analyze all components. (default "
                    + "mode)\n");
    console.writeln("  NOTE: If no analysis type specified, it runs default "
                    + "mode. You can mix ");
    console.writeln("        options to get particular results.");
    console.writeln("        e.g. jpfadt --listeners -aM -aP\n");
    console.writeln("<output> :: ");
    console.writeln(" (-oT | -text | --text) [<file>] : dump output to a plain "
                    + "text file. | ");
    console.writeln("  -o1 | -v : show output in the system console with "
                    + "verbosity level 1. | ");
    console.writeln("  -o2 | -vv : show output in the system console with "
                    + "verbosity level 2. |");
    console.writeln(" (-oX | -xml | --xml) [<file>] : dump output to a xml "
                    + "file. (default mode) | ");
    console.writeln(" (-oW | -wiki | --wiki) [<file>] : dump output to a wiki "
                    + "file for Google Code Projects. | ");
    console.writeln("  -oA | --output-all : output all formats with verbosity level 2.\n");
    console.writeln("  NOTE: If no output type specified, it runs default mode."
                    + " You can mix ");
    console.writeln("        options to get particular results.");
    console.writeln("        e.g. jpfadt -v -oX -oT\n");
    console.writeln("<file> ::");
    console.writeln("  textfile : if --text option is enabled, it's needed to"
                    + " specify a text ");
    console.writeln("             filename. | ");
    console.writeln("  xmlfile : if --xml option is enabled, it's needed to "
                    + "specify a xml ");
    console.writeln("            filename.\n");
    console.writeln("<misc> :: ");
    console.writeln("  -h | -? | -help | --help : show this help screen. | ");
    console.writeln("  -V | -ver | -version | --version : show build properties"
                    + " including version. | ");
    console.writeln("  -show | --show | -config | --config: show jpfadt "
                    + "config properties.");
  }
}
