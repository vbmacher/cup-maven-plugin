# Java CUP Maven plug-in
[![Build Status](https://github.com/vbmacher/cup-maven-plugin/actions/workflows/ant.yml/badge.svg)](https://github.com/vbmacher/cup-maven-plugin/actions/workflows/ant.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.vbmacher/cup-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vbmacher/cup-maven-plugin)
[![License: GPL v2](https://img.shields.io/badge/License-GPL_v2-blue.svg)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)

This project includes three artifacts:

- `cup-maven-plugin`; a Maven plug-in which wraps [Java CUP](http://www2.cs.tum.edu/projects/cup/) parser
generator.
- `java-cup`; repackaged `java-cup-11b.jar` as Maven artifact
- `java-cup-runtime`; repackaged `java-cup-11b-runtime.jar` as Maven artifact

NOTE: This is not an "official" plug-in, it was developed without any communication with original
      CUP authors.

## Copyright

Java CUP Maven plug-in + repackaged jars projects: (c) Copyright 2012-2022, Peter Jakubčo

"CUP Parser Generator": Copyright 1996-2015 by Scott Hudson, Frank Flannery, C. Scott Ananian, Michael Petter

## License

- Java CUP Maven plug-in: GPL v2.
- Repackaged jars: GPL-Compatible. See http://www2.cs.tum.edu/projects/cup/licence.html for more details.
- CUP Parser Generator": GPL-Compatible. See http://www2.cs.tum.edu/projects/cup/licence.html for more details.

## Usage

To use this plugin, you will have to tell Maven to execute the plugin at some point during the build process. 
To do so, add the following to the plugins-section of your `pom.xml`.

```
<plugin>
  <groupId>com.github.vbmacher</groupId>
  <artifactId>cup-maven-plugin</artifactId>
  <version>11b-20160615-2</version>
  <executions>
    <execution>
      <goals>
        <goal>generate</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <className>MyParserClassName</className>
    <symbolsName>MySymbolsClassName</symbolsName>
  </configuration>
</plugin>
```

By default the plugin is called during the generate-sources phase of the
[build lifecycle](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

## Run-time library

CUP-generated parsers need a runtime-library to run. There are no (reliable) third party bundles
for latest CUP (version 11b), so I have repackaged original jars into Maven bundles.

You can find the run-time library and also the engine in the subdirectories inside this repository.

The run-time library version of Java CUP library must correspond to version which the plug-in is
using for parser generation.

Currently, the plug-in uses [CUP](http://www2.cs.tum.edu/projects/cup/) version `11b-20160615`.

In order to use it, you must add a dependency to your `pom.xml`:

```
<dependency>
  <groupId>com.github.vbmacher</groupId>
  <artifactId>java-cup-runtime</artifactId>
  <version>11b-20160615-2</version>
</dependency>
```

NOTE: There might exist [several libraries](https://search.maven.org/search?q=cup) out there
      that should provide the CUP runtime library. You are not bound with this specific
      dependency, but you must take care about the CUP version.

## Parameters in configuration

The plug-in accepts many input parameters. Each parameter is passed inside the `<configuration>` element.
Most of the parameters are just forwarded to the original CUP parser generator.

Plugin-specific parameters:

* `<backup>false</backup>` - A flag whether to enable the generation of a backup copy, if the generated source file already exists. By default it is set
  to false.
* `<cupDefinition>parser.cup</cupDefinition>` - Grammar definition to run the cup parser generator on. By default, a `parser.cup` file in
  `src/main/cup` will be processed.
* `<outputDirectory>${project.build.directory}/generated-sources/cup</outputDirectory>` - Name of the directory into which cup should generate
   the parser.
* `<staleMillis>${lastModGranularityMs}</staleMillis>` - The granularity in milliseconds of the last modification date for testing
  whether a source needs regeneration.

CUP-specific parameters:

* `<className>parser</className>` - Parser class name.
* `<symbolsName>sym</symbolsName>` - Symbol class name.
* `<packageName></packageName>` - Package name. By default, package name is empty.
* `<symbolsInterface>true</symbolsInterface>` - A flag whether to output the symbol constant code as an interface rather
                                                than as a class.
* `<typeArgs></typeArgs>` - Specify type arguments for parser class. By default, it is empty.
* `<nontermsToSymbols>false</nontermsToSymbols>` - Put non-terminals in symbol constant class.
* `<expectedConflicts>0</expectedConflicts>` - Number of conflicts expected/allowed.
* `<compactRed>false</compactRed>` - Compact tables by defaulting to most frequent reduce.
* `<noWarn>false</noWarn>` - Don't warn about useless productions, etc.
* `<noSummary>true</noSummary>` - Don't print the usual summary of parse states, etc.
* `<progress>false</progress>` - Print messages to indicate progress of the system.
* `<dumpGrammar>false</dumpGrammar>` - Produce a human-readable dump of the symbols and grammar.
* `<dumpStates>false</dumpStates>` - Produce a dump of parse state machine.
* `<dumpTables>false</dumpTables>` - Produce a dump of the parse tables.
* `<time>false</time>` - Print time usage summary.
* `<debug>false</debug>` - Produces voluminous internal debugging information about the system as it runs.
* `<noPositions>false</noPositions>` - Don't propagate the left and right token position values.
* `<noScanner>false</noScanner>` - Don't refer to `java_cup.runtime.Scanner`.
* `<locations>false</locations>` - Makes CUP generate xleft/xright handles for accessing Location objects for symbol start/end inside actions.
* `<xmlActions>false</xmlActions>` - Makes CUP generate generic actions that produce XMLElement-objects for any symbol, that is labeled by the CUP spec author.
* `<genericLabels>false</genericLabels>` - This option goes one step further then `<xmlActions/>` by producing the full parse
                                           tree as XMLElement-tree.

## Contributing

This project is not under active development. Time-to-time it is updated to wrap newest CUP, but checking for new version is not done often and regularly.
The best how you can contribute is to file issues if something is wrong, or just notify the author about newest CUP version if you need it.

Ofcourse, if you find some bug and know how to fix it, pull requests are welcome.
