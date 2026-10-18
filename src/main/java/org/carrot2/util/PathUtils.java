/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.util;

import com.google.common.base.Strings;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * This class defines utilities methods helping to determine path-related information such as relative paths.
 * <p>
 * The original code comes from <b>org.codehaus.plexus.util.PathTool</b>.
 *
 * @author Ibrahim Chaehoi
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 */
public class PathUtils {

    /**
     * Instantiates a new path utils.
     */
    private PathUtils() {
        // Prevent Instantiation
    }

    /**
     * This method can calculate the relative path between two pathes on a file system.
     *
     * <pre>
     * PathUtils.getRelativeFilePath( null, null )                                   = ""
     * PathUtils.getRelativeFilePath( null, "/usr/local/java/bin" )                  = ""
     * PathUtils.getRelativeFilePath( "/usr/local", null )                           = ""
     * PathUtils.getRelativeFilePath( "/usr/local", "/usr/local/java/bin" )          = "java/bin"
     * PathUtils.getRelativeFilePath( "/usr/local", "/usr/local/java/bin/" )         = "java/bin"
     * PathUtils.getRelativeFilePath( "/usr/local/java/bin", "/usr/local/" )         = "../.."
     * PathUtils.getRelativeFilePath( "/usr/local/", "/usr/local/java/bin/java.sh" ) = "java/bin/java.sh"
     * PathUtils.getRelativeFilePath( "/usr/local/java/bin/java.sh", "/usr/local/" ) = "../../.."
     * PathUtils.getRelativeFilePath( "/usr/local/", "/bin" )                        = "../../bin"
     * PathUtils.getRelativeFilePath( "/bin", "/usr/local/" )                        = "../usr/local"
     * </pre>
     *
     * Note: On Windows based system, the <code>/</code> character should be replaced by <code>\</code> character.
     *
     * @param oldPath
     *            the old path
     * @param newPath
     *            the new path
     *
     * @return a relative file path from <code>oldPath</code>.
     */
    public static final String getRelativeFilePath(final String oldPath, final String newPath) {
        if (Strings.isNullOrEmpty(oldPath) || Strings.isNullOrEmpty(newPath)) {
            return "";
        }

        // Normalize the path delimiters
        String fromPath = Path.of(oldPath).toString();
        String toPath = Path.of(newPath).toString();

        // strip any leading slashes if its a windows path
        if (toPath.matches("^\\[a-zA-Z]:")) {
            toPath = toPath.substring(1);
        }
        if (fromPath.matches("^\\[a-zA-Z]:")) {
            fromPath = fromPath.substring(1);
        }

        // lowercase windows drive letters.
        if (fromPath.startsWith(":", 1)) {
            fromPath = Character.toLowerCase(fromPath.charAt(0)) + fromPath.substring(1);
        }
        if (toPath.startsWith(":", 1)) {
            toPath = Character.toLowerCase(toPath.charAt(0)) + toPath.substring(1);
        }

        // check for the presence of windows drives. No relative way of
        // traversing from one to the other.
        if (toPath.startsWith(":", 1) && fromPath.startsWith(":", 1)
                && !toPath.substring(0, 1).equals(fromPath.substring(0, 1))) {
            // they both have drive path element but they dont match, no relative path
            return null;
        }

        if ((toPath.startsWith(":", 1) && !fromPath.startsWith(":", 1))
                || (!toPath.startsWith(":", 1) && fromPath.startsWith(":", 1))) {
            // one has a drive path element and the other doesn't, no relative path.
            return null;
        }

        String resultPath = buildRelativePath(toPath, fromPath, File.separatorChar);

        if (newPath.endsWith(File.separator) && !resultPath.endsWith(File.separator)) {
            return resultPath + File.separator;
        }

        return resultPath;
    }

    // ----------------------------------------------------------------------
    // Private methods
    // ----------------------------------------------------------------------

    /**
     * Builds the relative path.
     *
     * @param toPath
     *            the to path
     * @param fromPath
     *            the from path
     * @param separatorChar
     *            the separator char
     *
     * @return the string
     */
    private static String buildRelativePath(String toPath, String fromPath, final char separatorChar) {
        String separator = String.valueOf(separatorChar);
        String[] toTokens = Arrays.stream(toPath.split(Pattern.quote(separator))).filter(token -> !token.isEmpty())
                .toArray(String[]::new);
        String[] fromTokens = Arrays.stream(fromPath.split(Pattern.quote(separator))).filter(token -> !token.isEmpty())
                .toArray(String[]::new);

        int count = 0;

        // Walk along the to path looking for divergence from the from path.
        while (count < toTokens.length && count < fromTokens.length) {
            boolean matches = separatorChar == '\\' ? fromTokens[count].equalsIgnoreCase(toTokens[count])
                    : fromTokens[count].equals(toTokens[count]);

            if (!matches) {
                break;
            }

            count++;
        }

        StringBuilder relativePath = new StringBuilder();

        // Add back references for the rest of the from location.
        for (int i = count; i < fromTokens.length; i++) {
            relativePath.append("..");

            if (i < fromTokens.length - 1) {
                relativePath.append(separatorChar);
            }
        }

        if (relativePath.length() != 0 && count < toTokens.length) {
            relativePath.append(separatorChar);
        }

        // Add forward components for whatever remains of the to path.
        for (int i = count; i < toTokens.length; i++) {
            relativePath.append(toTokens[i]);

            if (i < toTokens.length - 1) {
                relativePath.append(separatorChar);
            }
        }

        return relativePath.toString();
    }
}
