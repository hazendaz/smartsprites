/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
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
