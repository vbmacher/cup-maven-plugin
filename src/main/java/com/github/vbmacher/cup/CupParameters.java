package com.github.vbmacher.cup;

import java.io.File;
import java.util.List;

/**
 * Parameters used for Java CUP parser.
 */
public interface CupParameters {

    /**
     * Get grammar definition to run the cup parser generator on.
     *
     * <p>
     * By default, a <code>parser.cup</code> file in <code>src/main/cup</code> will be processed.
     * </p>
     *
     * @return CUP grammar file
     */
    File getCupDefinition();

    /**
     * Get parser class name.
     *
     * @return parser class name
     */
    String getClassName();

    /**
     * Get symbol class name.
     *
     * @return symbol class name
     */
    String getSymbolsName();

    /**
     * Get name of the directory into which cup should generate the parser.
     *
     * <p>
     * By default, the parser will be generated into <code>${project.build.directory}/generated-sources/cup</code>
     * </p>
     * @return output directory for generated parser(s)
     */
    File getOutputDirectory();

    /**
     * Get package name.
     *
     * @return package name of generated parser(s)
     */
    String getPackageName();

    /**
     * The granularity in milliseconds of the last modification date for testing
     * whether a source needs regeneration.
     *
     * @return stale milliseconds
     */
    int getStaleMillis();

    /**
     * Prepares command line for execution of CUP.
     *
     * @param cupFile cup file
     * @param generatedFile output parser file
     * @return list of command-line parameters for executing CUP, as they are defined within plugin configuration.
     */
    List<String> prepareCommandLine(File cupFile, File generatedFile);
}
