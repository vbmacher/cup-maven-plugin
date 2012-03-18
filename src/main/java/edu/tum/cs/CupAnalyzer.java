/*
 * CupAnalyzer.java
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

import java.io.*;

/**
 *
 * @author vbmacher
 */
public class CupAnalyzer {

    public static String getPackageName(File cupFile)
            throws FileNotFoundException, IOException {
        LineNumberReader reader = new LineNumberReader(new FileReader(cupFile));
        String packageName = null;
        do {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            int index = line.indexOf("package");
            if (index >= 0) {
                index += 7;
                int end = line.indexOf(';', index);
                if (end >= index) {
                    packageName = (line.substring(index, end)).trim();
                    break;
                }
            }
        } while (true);
        return packageName;
    }
    
    public static String getOutputFilename(String packageName, String className) {
        String packageDir = "";
        if (packageName != null) {
            packageDir = new StringBuilder().append(packageDir)
                    .append(packageName.replace('.', File.separatorChar)).toString();
        }
        if (packageDir.length() > 0) {
            packageDir = new StringBuilder().append(packageDir)
                    .append(File.separatorChar).toString();
        }
        return new StringBuilder().append(packageDir).append(className)
                .append(".java").toString();
    }

}
