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

import java_cup.Main;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper for calling CUP.
 */
public class CupParserGenerator {
    private static final String PACKAGE = "package";
    private static final String CLASS = "class";

    private final Log log;
    private final CupParameters cupParameters;
    private final File cupDefinition;

    private static class CupFileFilter implements DirectoryStream.Filter<Path> {

        @Override
        public boolean accept(Path entry) throws IOException {
            File file = entry.toFile();
            return file.isFile() && file.getName().toLowerCase().endsWith(".cup");
        }
    }

    /**
     * Creates new instance.
     *
     * @param log Maven plug-in logger
     * @param cupParameters Parameters used for CUP parser generation
     */
    public CupParserGenerator(Log log, CupParameters cupParameters) {
        this.log = Objects.requireNonNull(log);
        this.cupParameters = Objects.requireNonNull(cupParameters);
        this.cupDefinition = cupParameters.getCupDefinition();
    }

    /**
     * Generate parser(s) using CUP library.
     *
     * @throws MojoExecutionException When something goes wrong
     */
    public void process() throws MojoExecutionException {
        if (cupDefinition.isDirectory()) {
            parseCupDirectory();
        } else {
            parseCupFile(cupDefinition);
        }
    }

    private void parseCupDirectory() throws MojoExecutionException {
        log.debug("Processing directory: " + cupDefinition.getAbsolutePath());

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(cupDefinition.toPath(), new CupFileFilter())) {
            for (Path cupFile : stream) {
                parseCupFile(cupFile.toFile());
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not process directory " + cupDefinition.getName(), e);
        }
    }

    private void parseCupFile(File cupFile) throws MojoExecutionException {
        log.debug("Processing CUP file " + cupFile.getName());

        String packageName = determinePackageName(cupFile);
        String className = determineClassName(cupFile);

        verifyParameters(cupFile, packageName, className);

        File outputDirectory = cupParameters.getOutputDirectory();
        File outputFile = prepareOutputFile(packageName, className, outputDirectory);

        if (sourceNeedsProcessing(cupFile, outputFile)) {
            List<String> params = cupParameters.prepareCommandLine(cupFile, outputFile);
            try {
                Main.main(params.toArray(new String[0]));
                log.info("  generated " + outputFile);

                String symbolsName = cupParameters.getSymbolsName();
                if (symbolsName != null) {
                    File symFile = new File(outputDirectory, getOutputFilename(packageName, symbolsName));
                    log.info("  generated " + symFile);
                }
            } catch (Exception e) {
                throw new MojoExecutionException("Could not process CUP file " + cupFile.getAbsolutePath(), e);
            }
        }
    }

    private File prepareOutputFile(String packageName, String className, File outputDirectory) {
        File generatedFile = new File(outputDirectory, getOutputFilename(packageName, className));
        if (generatedFile.getParentFile() != null) {
            generatedFile.getParentFile().mkdirs();
        }
        return generatedFile;
    }

    private boolean sourceNeedsProcessing(File cupFile, File generatedFile) {
        long staleMillis = cupParameters.getStaleMillis();
        if (cupFile.lastModified() - generatedFile.lastModified() <= staleMillis) {
            log.info("  " + generatedFile.getName() + " is up to date.");
            log.debug("StaleMillis = " + staleMillis + "ms");
            return false;
        }
        return true;
    }

    private String determinePackageName(File cupFile) throws MojoExecutionException {
        String globalPackageName = cupParameters.getPackageName();
        if (globalPackageName == null) {
            try {
                return readCupEntity(PACKAGE, cupFile);
            } catch (IOException e) {
                throw new MojoExecutionException("Could not read package name from CUP file " + cupFile.getName(), e);
            }
        }
        return globalPackageName;
    }

    private String determineClassName(File cupFile) throws MojoExecutionException {
        String globalClassName = cupParameters.getClassName();
        if (globalClassName == null) {
            try {
                return readCupEntity(CLASS, cupFile);
            } catch (IOException e) {
                throw new MojoExecutionException("Could not read class name from CUP file " + cupFile.getName(), e);
            }
        }
        return globalClassName;
    }


    private String readCupEntity(String entityName, File cupFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(cupFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith(entityName)) {
                    int end = trimmed.indexOf(';');
                    if (end > 0) {
                        return line.substring(entityName.length(), end).trim();
                    }
                }
            }
            return null;
        }
    }

    private String getOutputFilename(String packageName, String className) {
        String packageDirectory = packageName.replace('.', File.separatorChar);
        if (!packageDirectory.isEmpty()) {
            packageDirectory += File.separatorChar;
        }
        return packageDirectory + className + ".java";
    }

    private void verifyParameters(File cupFile, String packageName, String className) throws MojoExecutionException {
        if (cupFile == null) {
            throw new MojoExecutionException(
                    "<cupDefinition> is empty. Please define input file with <cupDefinition>input.cup</cupDefinition>"
            );
        }
        if (!cupFile.isFile()) {
            throw new MojoExecutionException("Input file does not exist: " + cupFile.getAbsolutePath());
        }

        if (packageName == null) {
            throw new MojoExecutionException("Package name is not defined");
        }
        if (className == null) {
            throw new MojoExecutionException("Class name is not defined");
        }
        if (cupParameters.getOutputDirectory() == null) {
            throw new MojoExecutionException("Output directory is not defined");
        }
    }


}
