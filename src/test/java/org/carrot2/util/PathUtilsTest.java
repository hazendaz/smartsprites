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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link PathUtils}.
 */
class PathUtilsTest {

    /** OS-specific path separator for expected result construction. */
    private static final String SEP = File.separator;

    /**
     * Returns empty string when old path is null.
     */
    @Test
    void returnsEmptyWhenOldPathIsNull() {
        assertEquals("", PathUtils.getRelativeFilePath(null, Path.of("/usr/local/java").toString()));
    }

    /**
     * Returns empty string when new path is null.
     */
    @Test
    void returnsEmptyWhenNewPathIsNull() {
        assertEquals("", PathUtils.getRelativeFilePath(Path.of("/usr/local").toString(), null));
    }

    /**
     * Returns empty string when both paths are null.
     */
    @Test
    void returnsEmptyWhenBothPathsAreNull() {
        assertEquals("", PathUtils.getRelativeFilePath(null, null));
    }

    /**
     * Returns empty string when old path is empty.
     */
    @Test
    void returnsEmptyWhenOldPathIsEmpty() {
        assertEquals("", PathUtils.getRelativeFilePath("", Path.of("/usr/local").toString()));
    }

    /**
     * Returns empty string when new path is empty.
     */
    @Test
    void returnsEmptyWhenNewPathIsEmpty() {
        assertEquals("", PathUtils.getRelativeFilePath(Path.of("/usr/local").toString(), ""));
    }

    /**
     * Child path relative to parent.
     */
    @Test
    void childPathRelativeToParent() {
        assertEquals("java" + SEP + "bin", PathUtils.getRelativeFilePath(Path.of("/usr/local").toString(),
                Path.of("/usr/local/java/bin").toString()));
    }

    /**
     * Child path with trailing separator relative to parent.
     */
    @Test
    void childPathWithTrailingSlashRelativeToParent() {
        // Append the OS separator manually since Path.of() strips trailing separators
        assertEquals("java" + SEP + "bin" + SEP, PathUtils.getRelativeFilePath(Path.of("/usr/local").toString(),
                Path.of("/usr/local/java/bin").toString() + File.separator));
    }

    /**
     * Parent relative to child goes up.
     */
    @Test
    void parentRelativeToChildGoesUp() {
        assertEquals(".." + SEP + ".." + SEP, PathUtils.getRelativeFilePath(Path.of("/usr/local/java/bin").toString(),
                Path.of("/usr/local").toString() + File.separator));
    }

    /**
     * Sibling path produces correct relative path.
     */
    @Test
    void siblingPathProducesCorrectRelativePath() {
        assertEquals(".." + SEP + ".." + SEP + "bin", PathUtils
                .getRelativeFilePath(Path.of("/usr/local").toString() + File.separator, Path.of("/bin").toString()));
    }

    /**
     * Diverging path from other direction.
     */
    @Test
    void divergingPathFromOtherDirection() {
        assertEquals(".." + SEP + "usr" + SEP + "local" + SEP, PathUtils.getRelativeFilePath(Path.of("/bin").toString(),
                Path.of("/usr/local").toString() + File.separator));
    }

    /**
     * File relative to parent directory.
     */
    @Test
    void fileRelativeToParentDirectory() {
        assertEquals("java" + SEP + "bin" + SEP + "java.sh", PathUtils.getRelativeFilePath(
                Path.of("/usr/local").toString() + File.separator, Path.of("/usr/local/java/bin/java.sh").toString()));
    }

    /**
     * File to parent directory with parent reference.
     */
    @Test
    void fileToParentDirectoryWithParentReference() {
        assertEquals(".." + SEP + ".." + SEP + ".." + SEP, PathUtils.getRelativeFilePath(
                Path.of("/usr/local/java/bin/java.sh").toString(), Path.of("/usr/local").toString() + File.separator));
    }

    /**
     * Same path returns empty string.
     */
    @Test
    void samePathReturnsEmptyString() {
        String path = Path.of("/usr/local").toString();
        assertEquals("", PathUtils.getRelativeFilePath(path, path));
    }

    /**
     * New path ending with separator preserves trailing separator in result.
     */
    @Test
    void newPathEndingWithSeparatorPreservesTrailingSeparator() {
        String result = PathUtils.getRelativeFilePath(Path.of("/usr/local").toString(),
                Path.of("/usr/local/java/bin").toString() + File.separator);
        assertEquals("java" + SEP + "bin" + SEP, result,
                "Result should end with separator when newPath ends with separator");
    }
}
