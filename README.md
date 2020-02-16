# JPF AUTODOC TYPES

Automatic Documentation of JPF Types.


## Introduction

`jpf-autodoc-types` is an extension of [Java PathFinder (JPF)](https://github.com/javapathfinder/jpf-core). It extracts structural information related to type hierarchy in jpf projects. Specifically, this project extracts information about Listeners, InstructionFactories, Native Peers, and classes that model library classes. The information is recorded in popular text formats.


## Requirements

`jpf-autodoc-types` needs Java JRE to run and JDK to build. Java 6, 7 and 8 are supported.

Navigate to root folder in order to build, run and execute tests:

**Unix**

    user@host:~/projects_folder/jpf-autodoc-types$

**Windows**

    drive:\user_folder\projects_folder\jpf-autodoc-types>


## Building

This tool uses an Apache Ant script to build. Some ant binaries and shell scripts are provided if you prefer not installing ant. Binaries can be found in `tools` folder and shell scripts in `bin` folder. Run `ant` command without args or specify the `build` target:

    ant
    ant build
    
    ./bin/ant          (if no ant installed)
    ./bin/ant build


## Running

Run the `jpfadt` script from the `bin` folder or use the `java -jar` command:

    ./bin/jpfadt [<options>] {<target>}
    java -jar build/jpf-autodoc-types.jar [<options>] {<target>}

See [usage](#usage) for further details.


## Testing

Run **ant** target `test`:

    ant test
    ./bin/ant test     (if no ant installed)

The output is recorded in text files for each test case in `build/tests`.


## Usage

``` bash
jpfadt [<options>] {<target specification>}

<target specification> :: 
  classfile : java classfile path or filename. |
  classname : named package classname. A valid classpath must be specified. |
  archive : file with jar or zip extensions, containing class files or nested archive files containing other class files. |
  folder : a directory containing class files. |
  jpf-project-name : name of a jpf project (extension) registered in site.properties.

  NOTE: If no target specified, it scans all projects registered in site.properties. 
        You can mix different targets to get particular results.
        e.g. jpfadt jpf-core build/.../Config.class build/jpf.jar

<options> :: 
  [<classpath>] {<scan>} {<analysis>} {<output>} [<misc>]

<classpath> :: 
  -cp <path> : short classpath specification. |
  -classpath <path> : long classpath specification.

<path> :: 
  (Unix-like/Mac OS X)
  pathname0:pathname1:...:pathnameN : A : separated list of JAR archives or directories to search for class files. |
  
  (Windows)
  pathname0;pathname1;...;pathnameN : A ; separated list of JAR archives or directories to search for class files.

<scan> :: 
  -sD | -dirs | --dirs | --folders : scan into directories only. (default mode) |
  -sJ | -jars | --jars : scan into JAR archives only. |
  -sZ | -zips | --zips : scan into ZIP archives only. |
  -sA | --scan-all : scan directories and archives.

  NOTE: If no scan type specified, it runs default mode.
        You can mix options to get particular results.
        e.g. jpfadt -sJ --zips

<analysis> :: 
  -aL | -listeners | --listeners : analyze Listeners only. |
  -aI | -ifactories | --ifactories : analyze Instruction Factories only. |
  -aM | -models | --models : analyze Model Classes only. |
  -aP | -peers | --peers : analyze Native Peer classes only. |
  -aA | --analyze-all : analyze all components. (default mode)

  NOTE: If no analysis type specified, it runs default mode. You can mix  
        options to get particular results.
        e.g. jpfadt --listeners -aM -aP

<output> :: 
  -o1 | -v : show output with verbosity level 1. | 
  -o2 | -vv : show output with verbosity level 2. | 
 (-oT | -text | --text) [<file>] : write output to a plain text file. | 
 (-oX | -xml | --xml) [<file>] : write output to a xml file. (default mode) | 
 (-oW | -wiki | --wiki) [<file>] : write output to a wiki file for Google Code Projects. (deprecated) | 
 (-oM | -md | --md) [<file>] : write output to a markdown file. | 
  -oA | --output-all : write output in all formats.

  NOTE: If no output is specified, it runs default mode.
        You can mix options to get particular results.
        e.g. jpfadt -v -oX -oT

<file> :: output file path or filename.

<misc> :: 
  -h | -? | -help | --help : show help screen. | 
  -V | -ver | -version | --version : show build properties including version. | 
  -show | --show | -config | --config: show jpfadt config properties.
```

### Examples

Analyze classes in test folder:

    ./bin/jpfadt test

Analyze jpf projects:

    ./bin/jpfadt jpf-core

    ./bin/jpfadt jpf-aprop jpf-numeric jpf-shell

Empty target (analyze all projects in site.properties):

    ./bin/jpfadt

Analyze directories:

    ./bin/jpfadt /home/user/.jpf/jpf-concurrent/build

    ./bin/jpfadt ../jpf-actor/build ../jpf-delayed /home/user/.jpf/jpf-core/bu
                   ild/classes

Analyze classfiles:

    ./bin/jpfadt ../jpf-aprop/build/main/gov/nasa/jpf/aprop/listener/ArgChecker.class

    ./bin/jpfadt ../jpf-awt/build/classes/java/awt/Component.class

Using classpath (wildcard expansion supported!):

    ./bin/jpfadt -cp ~/.jpf/jpf-aprop/build/jpf-aprop.jar gov.nasa.jpf.aprop.listener.ConstChecker

    ./bin/jpfadt -cp ../jpf-core/build/jpf.jar:../jpf-aprop/build/main gov.nasa.jpf.listener.AssertionProperty gov.nasa.jpf.aprop.listener.ConstChecker gov.nasa.jpf.aprop.listener.NonnullChecker

    ./bin/jpfadt -cp '../jpf-core/build/*' gov.nasa.jpf.AnnotationProxyBase

**NOTE:** use single quotations to prevent shell path wildcard expansion.

Analyze jar archives:

    ./bin/jpfadt ../jpf-core/build/jpf.jar

    ./bin/jpfadt ../jpf-aprop/build/jpf-aprop.jar ../jpf-core/build/jpf.jar

Analyze zip archives:

    ./bin/jpfadt ../jpf-core/build/jpf-core.zip

Specify scan mode:

    ./bin/jpfadt ../jpf-core/build --jars --zips

    ./bin/jpfadt jpf-aprop ../jpf-shell -sA

    ./bin/jpfadt -sD ../jpf-awt jpf-shell

Specify analysis mode:

    ./bin/jpfadt -aL -aI jpf-core jpf-aprop

    ./bin/jpfadt jpf-numeric --peers jpf-racefinder

Specify output mode:

    ./bin/jpfadt -oX jpf.xml -oT results.txt jpf-core jpf-aprop

    ./bin/jpfadt -oX -o1


Happy Documenting!