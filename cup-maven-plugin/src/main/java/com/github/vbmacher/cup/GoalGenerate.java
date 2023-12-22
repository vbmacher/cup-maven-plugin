/*
 * Copyright (c) 2012-2022, Peter Jakubčo
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
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which generates java files from cup files.
 */
@SuppressWarnings("unused")
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GoalGenerate extends AbstractMojo implements CupParameters {
    public static final String DEFAULT_CUP_DIR = "src/main/cup";

    /**
     * A flag whether to output the symbol constant code as an interface rather
     * than as a class.
     */
    @Parameter(defaultValue = "true")
    private boolean symbolsInterface;

    /**
     * Grammar definition to run the cup parser generator on.
     * <p/>
     * By default, a `parser.cup` file in <code>src/main/cup</code> will be
     * processed.
     */
    @Parameter
    private File cupDefinition;

    /**
     * Parser class name.
     */
    @Parameter(defaultValue = "parser")
    private String className;

    /**
     * Symbol class name.
     */
    @Parameter(defaultValue = "sym")
    private String symbolsName;

    /**
     * Name of the directory into which cup should generate the parser.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/cup")
    private File outputDirectory;

    /**
     * Maven project
     */
    @Parameter(property = "project", readonly = true, required = true)
    private MavenProject project;

    /**
     * Package name.
     */
    @Parameter
    private String packageName;

    /**
     * Produce a human-readable dump of the symbols and grammar.
     */
    @Parameter(defaultValue = "false")
    private boolean dumpGrammar;

    /**
     * Produce a dump of parse state machine
     */
    @Parameter(defaultValue = "false")
    private boolean dumpStates;

    /**
     * Produce a dump of the parse tables
     */
    @Parameter(defaultValue = "false")
    private boolean dumpTables;

    /**
     * Print time usage summary
     */
    @Parameter(defaultValue = "false")
    private boolean time;

    /**
     * Print messages to indicate progress of the system
     */
    @Parameter(defaultValue = "false")
    private boolean progress;

    /**
     * Don't refer to java_cup.runtime.Scanner
     */
    @Parameter(defaultValue = "false")
    private boolean noScanner;

    /**
     * Don't propagate the left and right token position values
     */
    @Parameter(defaultValue = "false")
    private boolean noPositions;

    /**
     * Don't print the usual summary of parse states, etc.
     */
    @Parameter(defaultValue = "true")
    private boolean noSummary;

    /**
     * Don't warn about useless productions, etc.
     */
    @Parameter(defaultValue = "false")
    private boolean noWarn;

    /**
     * Compact tables by defaulting to most frequent reduce
     */
    @Parameter(defaultValue = "false")
    private boolean compactRed;

    /**
     * Number of conflicts expected/allowed
     */
    @Parameter(defaultValue = "0")
    private int expectedConflicts;

    /**
     * Put non-terminals in symbol constant class
     */
    @Parameter(defaultValue = "false")
    private boolean nontermsToSymbols;

    /**
     * Specify type arguments for parser class
     */
    @Parameter
    private String typeArgs;

    /**
     * The granularity in milliseconds of the last modification date for testing
     * whether a source needs regeneration.
     */
    @Parameter(property = "lastModGranularityMs")
    private int staleMillis;

    /**
     * Makes CUP generate xleft/xright handles for accessing Location objects for symbol start/end inside actions.
     */
    @Parameter(defaultValue = "false")
    private boolean locations;

    /**
     * Makes CUP generate generic actions that produce XMLElement-objects for any symbol, that is labeled by the CUP spec author.
     */
    @Parameter(defaultValue = "false")
    private boolean xmlActions;

    /**
     * This option goes one step further then `<xmlActions/>` by producing the full parse tree as XMLElement-tree.
     */
    @Parameter(defaultValue = "false")
    private boolean genericLabels;

    /**
     * Executes the "generate" goal.
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
        if (locations) {
            params.add("-locations");
        }
        if (xmlActions) {
            params.add("-xmlactions");
        }
        if (genericLabels) {
            params.add("-genericlabels");
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
