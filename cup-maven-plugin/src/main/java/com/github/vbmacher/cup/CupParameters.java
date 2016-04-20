/*
 * Copyright (c) 2012-2016, Peter Jakubčo
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
