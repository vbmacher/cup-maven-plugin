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
package com.github.vbmacher.cup;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which generates java files from cup files.
 *
 * @goal generate
 * @requiresProject true
 * @phase generate-sources
 */
public class GoalGenerate extends AbstractMojo implements CupParameters {
    public static final String DEFAULT_CUP_DIR = "src/main/cup";

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
     * <p/>
     * By default, a `parser.cup` file in <code>src/main/cup</code> will be
     * processed.
     *
     * @parameter
     * @editable
     */
    private File cupDefinition;

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
    private File outputDirectory;

    /**
     * @parameter property="project"
     * @required
     */
    private MavenProject project;

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
     * @parameter property="lastModGranularityMs"
     * @editable
     */
    private int staleMillis;

    /**
     * Executer the "generate" goal.
     *
     * @throws MojoExecutionException if any error occurs during parser generation
     */
    @Override
    public void execute() throws MojoExecutionException {
        String projectAbsolutePath = project.getBasedir().getAbsolutePath();
        if (outputDirectory != null && !outputDirectory.isAbsolute()) {
            // Handle relative paths
            outputDirectory = new File(projectAbsolutePath, outputDirectory.getPath());
        }

        project.addCompileSourceRoot(outputDirectory.getPath());
        if (cupDefinition == null) {
            cupDefinition = new File(projectAbsolutePath, DEFAULT_CUP_DIR + "/parser.cup");
        }

        CupParserGenerator cupParserGenerator = new CupParserGenerator(getLog(), this);
        cupParserGenerator.process();
    }

    @Override
    public File getCupDefinition() {
        return cupDefinition;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getSymbolsName() {
        return symbolsName;
    }

    @Override
    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public int getStaleMillis() {
        return staleMillis;
    }

    @Override
    public List<String> prepareCommandLine(File cupFile, File generatedFile) {
        List<String> params = new ArrayList<>();

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
        return params;
    }

}
