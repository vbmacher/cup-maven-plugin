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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;

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
     * @parameter expression="${project}"
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
     * @parameter expression="${lastModGranularityMs}"
     * @editable
     */
    private int staleMillis;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String projectAbsolutePath = project.getBasedir().getAbsolutePath();
        if (outputDirectory != null && !outputDirectory.isAbsolute()) {
            // Handle relative paths
            outputDirectory = new File(projectAbsolutePath, outputDirectory.getPath());
        }

        project.addCompileSourceRoot(outputDirectory.getPath());
        if (cupDefinition == null) {
            cupDefinition = new File(projectAbsolutePath, DEFAULT_CUP_DIR + "/parser.cup");
        }

        CupParameters cupParameters = CupParameters.loadFrom(this);
        CupAnalyzer cupAnalyzer = new CupAnalyzer(getLog(), cupParameters);

        cupAnalyzer.process();
    }

    public boolean isSymbolsInterface() {
        return symbolsInterface;
    }

    public File getCupDefinition() {
        return cupDefinition;
    }

    public String getClassName() {
        return className;
    }

    public String getSymbolsName() {
        return symbolsName;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isDumpGrammar() {
        return dumpGrammar;
    }

    public boolean isDumpStates() {
        return dumpStates;
    }

    public boolean isDumpTables() {
        return dumpTables;
    }

    public boolean isTime() {
        return time;
    }

    public boolean isProgress() {
        return progress;
    }

    public boolean isNoScanner() {
        return noScanner;
    }

    public boolean isNoPositions() {
        return noPositions;
    }

    public boolean isNoSummary() {
        return noSummary;
    }

    public boolean isNoWarn() {
        return noWarn;
    }

    public boolean isCompactRed() {
        return compactRed;
    }

    public int getExpectedConflicts() {
        return expectedConflicts;
    }

    public boolean isNontermsToSymbols() {
        return nontermsToSymbols;
    }

    public String getTypeArgs() {
        return typeArgs;
    }

    public int getStaleMillis() {
        return staleMillis;
    }
}
