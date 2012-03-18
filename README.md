Java cup Maven plug-in
======================

This project represents a plug-in acting as a wapper for [Java cup](http://www2.cs.tum.edu/projects/cup/) LR parser. I couldn't find any Maven 3 plug-in for this parser, so I created one.
To use it, put following structure into `plugins` element inside your POM file:

```
<plugin>
  <groupId>edu.tum.cs</groupId>
  <artifactId>cup-maven-plugin</artifactId>
  <version>1.0.0</version>
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

Parameters in configuration
---------------------------

The plug-in accepts many input parameters. Each parameter is passed inside `<configuration>` element. Most of the parameters are just forwarded to Java cup parser. List of available parameters follows:

* `<backup>false</backup>` - A flag whether to enable the generation of a backup copy, if the generated source file already exists. Defaultly is set
  to false.
* `<symbolsInterface>true</symbolsInterface>` - A flag whether to output the symbol constant code as an interface rather than as a class. Defaultly
  is set to true.
* `<cupDefinition>parser.cup</cupDefinition>` - Grammar definition to run the cup parser generator on. By default, a `parser.cup` file in
  `src/main/cup` will be processed.
* `<className>parser</className>` - Parser class name. Defaultly is set to `parser`.
* `<symbolsName>sym</symbolsName>` - Symbol class name. Defaultly is set to `sym`.
* `<outputDirectory>${project.build.directory}/generated-sources/cup</outputDirectory>` - Name of the directory into which cup should generate
   the parser.
* `<packageName></packageName>` - Package name. By default, package name is empty.
* `<dumpGrammar>false</dumpGrammar>` - Produce a human readable dump of the symbols and grammar. Defaultly is set to false.
* `<dumpStates>false</dumpStates>` - Produce a dump of parse state machine. Defaultly is set to false.
* `<dumpTables>false</dumpTables>` - Produce a dump of the parse tables. Defaultly is set to false.
* `<time>false</time>` - Print time usage summary. Defaultly is set to false.
* `<progress>false</progress>` - Print messages to indicate progress of the system. Defaultly is set to false.
* `<noScanner>false</noScanner>` - Don't refer to `java_cup.runtime.Scanner`. Defaultly is set to false.
* `<noPositions>false</noPositions>` - Don't propagate the left and right token position values. Defaultly is set to false.
* `<noSummary>true</noSummary>` - Don't print the usual summary of parse states, etc. Defaultly is set to true.
* `<noWarn>false</noWarn>` - Don't warn about useless productions, etc. Defaultly is set to false.
* `<compactRed>false</compactRed>` - Compact tables by defaulting to most frequent reduce. Defaultly is set to false.
* `<expectedConflicts>0</expectedConflicts>` - Number of conflicts expected/allowed. Defaultly it is 0.
* `<nontermsToSymbols>false</nontermsToSymbols>` - Put non terminals in symbol constant class. Defaultly is set to false.
* `<typeArgs></typeArgs>` - Specify type arguments for parser class. By default, it is empty.
* `<staleMillis>${lastModGranularityMs}</staleMillis>` - The granularity in milliseconds of the last modification date for testing
  whether a source needs regeneration.

