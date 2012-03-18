/*
 * CupGenerator.java
 * 
 * Copyright (c) 2012, Peter Jakubčo <pjakubco@gmail.com>
 *
 * KISS, DRY, YAGNI
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package edu.tum.cs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java_cup.Main;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which generates java files from cup files.
 *
 * @goal generate
 * @requiresProject true
 * @phase generate-sources
 */
public class CupGenerator extends AbstractMojo {

    public static final String DEFAULT_CUP_DIR = "src/main/cup";
    
    /**
     * A flag whether to enable the generation of a backup copy if the generated
     * source file already exists.
     *
     * @parameter default-value="false"
     * @editable
     */
    private boolean backup;
    
    /**
     * A flag whether to output the symbol constant code as an interface rather
     * than as a class.
     *
     * @parameter default-value="true"
     * @editable
     */
    private boolean symbolsInterface;
    
    /**
     * Grammar definition to run the cup parser generator on.
     * 
     * By default, a `parser.cup` file in <code>src/main/cup</code> will be
     * processed.
     *
     * @parameter default-value="parser.cup"
     * @editable
     */
    private java.io.File cupDefinition;
    
    /**
     * Parser class name.
     * 
     * @parameter default-value="parser"
     * @editable
     */
    private String className;
    
    /**
     * Symbol class name.
     * 
     * @parameter default-value="sym"
     * @editable
     */
    private String symbolsName;
    
    /**
     * Name of the directory into which cup should generate the parser.
     *
     * @parameter expression="${project.build.directory}/generated-sources/cup"
     * @editable
     */
    private java.io.File outputDirectory;
    
    /**
     * @parameter expression="${project}"
     * @required
     */
    MavenProject project;
    
    /**
     * Package name.
     * 
     * @parameter
     * @editable
     */
    private String packageName;
    
    /**
     * Produce a human readable dump of the symbols and grammar.
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean dumpGrammar;
    
    /**
     * Produce a dump of parse state machine
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean dumpStates;
    
    /**
     * Produce a dump of the parse tables
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean dumpTables;
    
    /**
     * Print time usage summary
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean time;
    
    /**
     * Print messages to indicate progress of the system
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean progress;
    
    /**
     * Don't refer to java_cup.runtime.Scanner
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean noScanner;
    
    /**
     * Don't propagate the left and right token position values
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean noPositions;
    
    /**
     * Don't print the usual summary of parse states, etc.
     * 
     * @parameter default-value="true"
     * @editable
     */
    private boolean noSummary;
    
    /**
     * Don't warn about useless productions, etc.
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean noWarn;
    
    /**
     * Compact tables by defaulting to most frequent reduce
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean compactRed;
    
    /**
     * Number of conflicts expected/allowed
     * 
     * @parameter default-value="0"
     * @editable
     */
    private int expectedConflicts;
    
    /**
     * Put non terminals in symbol constant class
     * 
     * @parameter default-value="false"
     * @editable
     */
    private boolean nontermsToSymbols;
    
    /**
     * Specify type arguments for parser class
     * 
     * @parameter
     * @editable
     */
    private String typeArgs;
    
    /**
     * The granularity in milliseconds of the last modification date for testing
     * whether a source needs regeneration.
     *
     * @parameter expression="${lastModGranularityMs}"
     * @editable
     */
    private int staleMillis;
        
    public void execute() throws MojoExecutionException, MojoFailureException {
        String projectAbsolutePath = project.getBasedir().getAbsolutePath();
        if (outputDirectory != null && !outputDirectory.isAbsolute()) {
            // Handle relative paths
            outputDirectory = new File(projectAbsolutePath, outputDirectory.getPath());
        }

        project.addCompileSourceRoot(outputDirectory.getPath());
        if (cupDefinition != null) {
            getLog().debug(new StringBuilder().append("Parsing ")
                    .append(" a cup file given in the configuration")
                    .toString());
        } else {
            getLog().debug(new StringBuilder()
                    .append("Using 'parser.cup' file found in (default) ")
                    .append(DEFAULT_CUP_DIR));
            cupDefinition = new File(projectAbsolutePath, new StringBuilder()
                    .append(DEFAULT_CUP_DIR)
                    .append("/parser.cup").toString());
        }
        parseCupDefinition();
    }

    private void parseCupDefinition() throws MojoFailureException,
            MojoExecutionException {
        if (cupDefinition.isDirectory()) {
            String extensions[] = {
                "cup"
            };
            getLog().debug((new StringBuilder())
                    .append("Processing parser files found in ")
                    .append(cupDefinition).toString());
            File cupFile;
            for (Iterator fileIterator = FileUtils.iterateFiles(cupDefinition,
                    extensions, true); fileIterator.hasNext();
                    parseCupFile(cupFile)) {
                cupFile = (File) fileIterator.next();
            }
        } else {
            parseCupFile(cupDefinition);
        }
    }

    private void parseCupFile(File cupFile) throws MojoFailureException,
            MojoExecutionException {
        getLog().debug((new StringBuilder()).append("Generationg Java code from ")
                .append(cupFile.getName()).toString());
        if (packageName == null) {
            try {
                packageName = CupAnalyzer.getPackageName(cupFile);
            } catch (FileNotFoundException e1) {
                throw new MojoFailureException(e1.getMessage());
            } catch (IOException e3) {
            }
        }
        checkParameters(cupFile);
        File generatedFile = new File(outputDirectory, 
                CupAnalyzer.getOutputFilename(packageName, className));
        if (generatedFile.getParentFile() != null) {
            generatedFile.getParentFile().mkdirs();
        }
        if (cupFile.lastModified() - generatedFile.lastModified() <= (long) staleMillis) {
            getLog().info((new StringBuilder()).append("  ").append(generatedFile.getName())
                    .append(" is up to date.").toString());
            getLog().debug((new StringBuilder()).append("StaleMillis = ").append(staleMillis)
                    .append("ms").toString());
            return;
        }
        try {
            List<String> params = new ArrayList<String>();
            
            if (packageName != null) {
                params.add("-package");
                params.add(packageName);
            }
            if (className != null) {
                params.add("-parser");
                params.add(className);
            }
            if (symbolsName != null) {
                params.add("-symbols");
                params.add(symbolsName);
            }
            if (symbolsInterface) {
                params.add("-interface");
            }
            if (dumpGrammar) {
                params.add("-dump_grammar");
            }
            if (dumpStates) {
                params.add("-dump_states");
            }
            if (dumpTables) {
                params.add("-dump_tables");
            }
            if (time) {
                params.add("-time");
            }
            if (nontermsToSymbols) {
                params.add("-nonterms");
            }
            if (compactRed) {
                params.add("-compact_red");
            }
            if (noWarn) {
                params.add("-nowarn");
            }
            if (noSummary) {
                params.add("-nosummary");
            }
            if (progress) {
                params.add("-progress");
            }
            if (noPositions) {
                params.add("-nopositions");
            }
            if (noScanner) {
                params.add("-noscanner");
            }
            if (expectedConflicts > 0) {
                params.add("-expect");
                params.add(String.valueOf(expectedConflicts));
            }
            if (typeArgs != null) {
                params.add("-typearg");
                params.add(typeArgs);
            }
            params.add("-destdir");
            File dir = generatedFile.getParentFile();
            
            if (dir != null) {
                params.add(dir.getAbsolutePath());
            } else {
                params.add(outputDirectory.getAbsolutePath());
            }
            params.add(cupFile.getAbsolutePath());
            
            Main.main(params.toArray(new String[0]));
            getLog().info(new StringBuilder().append("  generated ")
                    .append(generatedFile).toString());
            if (symbolsName != null) {
                File symFile = new File(outputDirectory, 
                        CupAnalyzer.getOutputFilename(packageName, symbolsName));
                getLog().info(new StringBuilder().append("  generated ")
                        .append(symFile).toString());
            }
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    private void checkParameters(File cupFile)
            throws MojoExecutionException {
        if (cupFile == null) {
            throw new MojoExecutionException(new StringBuilder()
                    .append("<cupDefinition> is empty. Please")
                    .append(" define input file with <cupDefinition>input.cup</cupDefinition>")
                    .toString());
        }
        if (!cupFile.isFile()) {
            throw new MojoExecutionException(new StringBuilder()
                    .append("Input file does not exist: ").append(cupFile).toString());
        }
    }

}
