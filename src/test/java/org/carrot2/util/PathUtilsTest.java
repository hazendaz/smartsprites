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

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link PathUtils}.
 */
class PathUtilsTest {

    /**
     * Returns empty string when old path is null.
     */
    @Test
    void returnsEmptyWhenOldPathIsNull() {
        assertEquals("", PathUtils.getRelativeFilePath(null, "/usr/local/java"));
    }

    /**
     * Returns empty string when new path is null.
     */
    @Test
    void returnsEmptyWhenNewPathIsNull() {
        assertEquals("", PathUtils.getRelativeFilePath("/usr/local", null));
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
        assertEquals("", PathUtils.getRelativeFilePath("", "/usr/local"));
    }

    /**
     * Returns empty string when new path is empty.
     */
    @Test
    void returnsEmptyWhenNewPathIsEmpty() {
        assertEquals("", PathUtils.getRelativeFilePath("/usr/local", ""));
    }

    /**
     * Child path relative to parent.
     */
    @Test
    void childPathRelativeToParent() {
        assertEquals("java/bin", PathUtils.getRelativeFilePath("/usr/local", "/usr/local/java/bin"));
    }

    /**
     * Child path with trailing slash relative to parent.
     */
    @Test
    void childPathWithTrailingSlashRelativeToParent() {
        // newPath ends with '/', so result also ends with separator
        assertEquals("java/bin/", PathUtils.getRelativeFilePath("/usr/local", "/usr/local/java/bin/"));
    }

    /**
     * Parent relative to child goes up.
     */
    @Test
    void parentRelativeToChildGoesUp() {
        // newPath "/usr/local/" ends with separator, result also ends with separator
        assertEquals("../../", PathUtils.getRelativeFilePath("/usr/local/java/bin", "/usr/local/"));
    }

    /**
     * Sibling path produces correct relative path.
     */
    @Test
    void siblingPathProducesCorrectRelativePath() {
        assertEquals("../../bin", PathUtils.getRelativeFilePath("/usr/local/", "/bin"));
    }

    /**
     * Diverging path from other direction.
     */
    @Test
    void divergingPathFromOtherDirection() {
        // newPath "/usr/local/" ends with separator, result also ends with separator
        assertEquals("../usr/local/", PathUtils.getRelativeFilePath("/bin", "/usr/local/"));
    }

    /**
     * File relative to parent directory.
     */
    @Test
    void fileRelativeToParentDirectory() {
        assertEquals("java/bin/java.sh", PathUtils.getRelativeFilePath("/usr/local/", "/usr/local/java/bin/java.sh"));
    }

    /**
     * File to parent directory with parent reference.
     */
    @Test
    void fileToParentDirectoryWithParentReference() {
        // newPath "/usr/local/" ends with separator, result also ends with separator
        assertEquals("../../../", PathUtils.getRelativeFilePath("/usr/local/java/bin/java.sh", "/usr/local/"));
    }

    /**
     * Same path returns empty string.
     */
    @Test
    void samePathReturnsEmptyString() {
        assertEquals("", PathUtils.getRelativeFilePath("/usr/local", "/usr/local"));
    }

    /**
     * New path ending with separator preserves trailing separator in result.
     */
    @Test
    void newPathEndingWithSeparatorPreservesTrailingSeparator() {
        // When newPath ends with separator, the result also ends with separator
        String result = PathUtils.getRelativeFilePath("/usr/local", "/usr/local/java/bin/");
        assertEquals("java/bin/", result, "Result should end with separator when newPath ends with separator");
    }
}
