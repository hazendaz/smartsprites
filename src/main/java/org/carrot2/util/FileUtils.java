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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

/**
 * Various utility methods for working with {@link File}s.
 */
public class FileUtils {

    /**
     * Instantiates a new file utils.
     */
    private FileUtils() {
        // Prevent Instantiation
    }

    /**
     * Creates a new {@link File} from the provided path and attempts to execute {@link File#getCanonicalFile()}. In
     * case of a failure, returns the result of {@link File#getAbsoluteFile()}.
     *
     * @param path
     *            the path
     *
     * @return the canonical or absolute file
     */
    public static File getCanonicalOrAbsoluteFile(String path) {
        File file = Path.of(path).toFile();
        try {
            return file.getCanonicalFile();
        } catch (final IOException e) {
            return file.getAbsoluteFile();
        }
    }

    /**
     * Changes the root directory of a file. For example, file is /a/b/c/d/e and oldRoot is /a/b/c, and newRoot is /x/y,
     * the result will be /x/y/d/e.
     *
     * @param file
     *            the file
     * @param oldRoot
     *            the old root
     * @param newRoot
     *            the new root
     *
     * @return the string
     */
    public static String changeRoot(String file, String oldRoot, String newRoot) {
        // File is assumed to be a subpath of oldRoot, so PathUtils.getRelativeFilePath()
        // shouldn't return null here.
        final String relativePath = PathUtils.getRelativeFilePath(oldRoot, file);
        return FilenameUtils.concat(newRoot, relativePath);
    }

    /**
     * Removes useless segments in relative paths, e.g. replaces <code>../path/../other/file.css</code> with
     * <code>../other/file.css</code>
     *
     * @param path
     *            the path
     * @param separator
     *            the separator
     *
     * @return the string
     */
    public static String canonicalize(String path, String separator) {
        String replaced = path;
        String toReplace = null;
        final String separatorEscaped = Pattern.quote(separator);
        final Pattern pattern = Pattern
                .compile("[^" + separatorEscaped + "\\.]+" + separatorEscaped + "\\.\\." + separatorEscaped + "?");
        while (!replaced.equals(toReplace)) {
            toReplace = replaced;
            replaced = pattern.matcher(toReplace).replaceFirst("");
        }
        return replaced;
    }

    /**
     * Attempts to delete the provided files and throws an {@link IOException} in case {@link File#delete()} returns
     * <code>false</code> for any of them.
     *
     * @param files
     *            the files
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void deleteThrowingExceptions(File... files) throws IOException {
        if (files == null) {
            return;
        }

        final ArrayList<String> undeletedFiles = new ArrayList<>();
        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.exists() && !file.delete()) {
                undeletedFiles.add(file.getPath());
            }
        }

        if (!undeletedFiles.isEmpty()) {
            throw new IOException("Unable to delete files: " + undeletedFiles.toString());
        }
    }

    /**
     * Calls {@link File#mkdirs()} on the provided argument and throws an {@link IOException} if the call returns
     * <code>false</code>.
     *
     * @param dirs
     *            the dirs
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void mkdirsThrowingExceptions(File dirs) throws IOException {
        if (dirs.exists()) {
            return;
        }

        if (!dirs.mkdirs()) {
            throw new IOException("Unable to create directories: " + dirs.getPath());
        }
    }

    /**
     * Returns <code>true</code> if file is contained in the parent directory or any parent of the parent directory.
     *
     * @param file
     *            the file
     * @param parent
     *            the parent
     *
     * @return true, if is file in parent
     */
    public static boolean isFileInParent(File file, File parent) {
        final File fileParent = file.getParentFile();
        if (fileParent == null) {
            return false;
        }

        if (fileParent.equals(parent)) {
            return true;
        }

        return isFileInParent(fileParent, parent);
    }
}
