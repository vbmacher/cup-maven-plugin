package edu.tum.cs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CupParameters {
    private boolean symbolsInterface;
    private File cupDefinition;
    private String className;
    private String symbolsName;
    private File outputDirectory;
    private String packageName;
    private boolean dumpGrammar;
    private boolean dumpStates;
    private boolean dumpTables;
    private boolean time;
    private boolean progress;
    private boolean noScanner;
    private boolean noPositions;
    private boolean noSummary;
    private boolean noWarn;
    private boolean compactRed;
    private int expectedConflicts;
    private boolean nontermsToSymbols;
    private String typeArgs;
    private int staleMillis;

    private CupParameters() {

    }

    public static CupParameters loadFrom(CupGenerator cupGenerator) {
        CupParameters parameters = new CupParameters();
        parameters.symbolsInterface = cupGenerator.isSymbolsInterface();
        parameters.cupDefinition = cupGenerator.getCupDefinition();
        parameters.className = cupGenerator.getClassName();
        parameters.symbolsName = cupGenerator.getSymbolsName();
        parameters.outputDirectory = cupGenerator.getOutputDirectory();
        parameters.packageName = cupGenerator.getPackageName();
        parameters.dumpGrammar = cupGenerator.isDumpGrammar();
        parameters.dumpStates = cupGenerator.isDumpStates();
        parameters.dumpTables = cupGenerator.isDumpTables();
        parameters.time = cupGenerator.isTime();
        parameters.progress = cupGenerator.isProgress();
        parameters.noScanner = cupGenerator.isNoScanner();
        parameters.noPositions = cupGenerator.isNoPositions();
        parameters.noSummary = cupGenerator.isNoSummary();
        parameters.noWarn = cupGenerator.isNoWarn();
        parameters.compactRed = cupGenerator.isCompactRed();
        parameters.expectedConflicts = cupGenerator.getExpectedConflicts();
        parameters.nontermsToSymbols = cupGenerator.isNontermsToSymbols();
        parameters.typeArgs = cupGenerator.getTypeArgs();
        parameters.staleMillis = cupGenerator.getStaleMillis();

        return parameters;
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

    public int getStaleMillis() {
        return staleMillis;
    }

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
