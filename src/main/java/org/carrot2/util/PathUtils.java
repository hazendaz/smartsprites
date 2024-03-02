/*
 * SmartSprites Project
 *
 * Copyright (C) 2007-2009, Stanisław Osiński.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of  source code must  retain the above  copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following  disclaimer in  the documentation  and/or
 *   other materials provided with the distribution.
 *
 * - Neither the name of the SmartSprites Project nor the names of its contributors
 *   may  be used  to endorse  or  promote  products derived   from  this  software
 *   without specific prior written permission.
 *
 * - We kindly request that you include in the end-user documentation provided with
 *   the redistribution and/or in the software itself an acknowledgement equivalent
 *   to  the  following: "This product includes software developed by the SmartSprites
 *   Project."
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  AND
 * ANY EXPRESS OR  IMPLIED WARRANTIES, INCLUDING,  BUT NOT LIMITED  TO, THE IMPLIED
 * WARRANTIES  OF  MERCHANTABILITY  AND  FITNESS  FOR  A  PARTICULAR  PURPOSE   ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE  FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL,  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL  DAMAGES
 * (INCLUDING, BUT  NOT LIMITED  TO, PROCUREMENT  OF SUBSTITUTE  GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS;  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  ON
 * ANY  THEORY  OF  LIABILITY,  WHETHER  IN  CONTRACT,  STRICT  LIABILITY,  OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE)  ARISING IN ANY WAY  OUT OF THE USE  OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.carrot2.util;

import com.google.common.base.Strings;

import java.io.File;
import java.util.StringTokenizer;

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

        // normalise the path delimiters
        String fromPath = new File(oldPath).getPath();
        String toPath = new File(newPath).getPath();

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

        if (toPath.startsWith(":", 1) && !fromPath.startsWith(":", 1)
                || !toPath.startsWith(":", 1) && fromPath.startsWith(":", 1)) {
            // one has a drive path element and the other doesnt, no relative path.
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
        // use tokenizer to traverse paths and for lazy checking
        StringTokenizer toTokenizer = new StringTokenizer(toPath, String.valueOf(separatorChar));
        StringTokenizer fromTokenizer = new StringTokenizer(fromPath, String.valueOf(separatorChar));

        int count = 0;

        // walk along the to path looking for divergence from the from path
        while (toTokenizer.hasMoreTokens() && fromTokenizer.hasMoreTokens()) {
            if (separatorChar == '\\') {
                if (!fromTokenizer.nextToken().equalsIgnoreCase(toTokenizer.nextToken())) {
                    break;
                }
            } else if (!fromTokenizer.nextToken().equals(toTokenizer.nextToken())) {
                break;
            }

            count++;
        }

        // Reinitialize the tokenizers to count positions to retrieve the
        // gobbled token

        toTokenizer = new StringTokenizer(toPath, String.valueOf(separatorChar));
        fromTokenizer = new StringTokenizer(fromPath, String.valueOf(separatorChar));

        while (count-- > 0) {
            fromTokenizer.nextToken();
            toTokenizer.nextToken();
        }

        StringBuilder relativePath = new StringBuilder();

        // add back refs for the rest of from location.
        while (fromTokenizer.hasMoreTokens()) {
            fromTokenizer.nextToken();

            relativePath.append("..");

            if (fromTokenizer.hasMoreTokens()) {
                relativePath.append(separatorChar);
            }
        }

        if (relativePath.length() != 0 && toTokenizer.hasMoreTokens()) {
            relativePath.append(separatorChar);
        }

        // add fwd fills for whatevers left of newPath.
        while (toTokenizer.hasMoreTokens()) {
            relativePath.append(toTokenizer.nextToken());

            if (toTokenizer.hasMoreTokens()) {
                relativePath.append(separatorChar);
            }
        }
        return relativePath.toString();
    }
}
