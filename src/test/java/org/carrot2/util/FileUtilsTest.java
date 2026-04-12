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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test cases for {@link FileUtils}.
 */
class FileUtilsTest {

    /**
     * Canonicalize empty.
     */
    @Test
    void canonicalizeEmpty() {
        assertEquals("", FileUtils.canonicalize("", "/"));
    }

    /**
     * Canonicalize one segment.
     */
    @Test
    void canonicalizeOneSegment() {
        assertEquals("file", FileUtils.canonicalize("file", "/"));
    }

    /**
     * Canonicalize two segments canonical.
     */
    @Test
    void canonicalizeTwoSegmentsCanonical() {
        assertEquals("path/file", FileUtils.canonicalize("path/file", "/"));
    }

    /**
     * Canonicalize two segments non canonical.
     */
    @Test
    void canonicalizeTwoSegmentsNonCanonical() {
        assertEquals("", FileUtils.canonicalize("path/..", "/"));
    }

    /**
     * Canonicalize two segments non canonical trailing separator.
     */
    @Test
    void canonicalizeTwoSegmentsNonCanonicalTrailingSeparator() {
        assertEquals("", FileUtils.canonicalize("path/../", "/"));
    }

    /**
     * Canonicalize more segments non canonical.
     */
    @Test
    void canonicalizeMoreSegmentsNonCanonical() {
        assertEquals("/longer/file/actual/file", FileUtils.canonicalize("/longer/file/path/../actual/file", "/"));
    }

    /**
     * Canonicalize more parents.
     */
    @Test
    void canonicalizeMoreParents() {
        assertEquals("/longer/actual/file", FileUtils.canonicalize("/longer/file/path/../../actual/file", "/"));
    }

    /**
     * Canonicalize canonical starting with parent.
     */
    @Test
    void canonicalizeCanonicalStartingWithParent() {
        assertEquals("../../img/sprite.png", FileUtils.canonicalize("../../img/sprite.png", "/"));
    }

    /**
     * Get canonical or absolute file returns a non-null file for a valid path.
     *
     * @param tempDir
     *            the temp dir
     */
    @Test
    void getCanonicalOrAbsoluteFileReturnsFile(@TempDir File tempDir) {
        File result = FileUtils.getCanonicalOrAbsoluteFile(new File(tempDir, "test.txt").getPath());
        assertNotNull(result);
    }

    /**
     * Change root swaps the root directory of a file path.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void changeRootSwapsRootDirectory(@TempDir File tempDir) throws IOException {
        File oldRoot = new File(tempDir, "old");
        File newRoot = new File(tempDir, "new");
        oldRoot.mkdirs();
        newRoot.mkdirs();

        File file = new File(oldRoot, "subdir/file.css");
        file.getParentFile().mkdirs();
        assertTrue(file.createNewFile(), "File should have been created");

        String result = FileUtils.changeRoot(file.getPath(), oldRoot.getPath(), newRoot.getPath());
        assertNotNull(result);
        assertTrue(result.contains("file.css"), "Should contain filename: " + result);
        assertTrue(result.contains(newRoot.getPath()), "Should contain new root: " + result);
    }

    /**
     * Delete throwing exceptions with null array does nothing.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void deleteThrowingExceptionsWithNullArrayDoesNothing() throws IOException {
        FileUtils.deleteThrowingExceptions((File[]) null);
    }

    /**
     * Delete throwing exceptions with null element skips it.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void deleteThrowingExceptionsWithNullElementSkipsIt() throws IOException {
        FileUtils.deleteThrowingExceptions((File) null);
    }

    /**
     * Delete throwing exceptions deletes existing file.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void deleteThrowingExceptionsDeletesExistingFile(@TempDir File tempDir) throws IOException {
        File file = new File(tempDir, "todelete.txt");
        assertTrue(file.createNewFile(), "File should have been created");
        assertTrue(file.exists());

        FileUtils.deleteThrowingExceptions(file);

        assertFalse(file.exists(), "File should have been deleted");
    }

    /**
     * Mkdirs throwing exceptions creates directories.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void mkdirsThrowingExceptionsCreatesDirectories(@TempDir File tempDir) throws IOException {
        File newDir = new File(tempDir, "a/b/c");
        assertFalse(newDir.exists());

        FileUtils.mkdirsThrowingExceptions(newDir);

        assertTrue(newDir.exists(), "Directory should have been created");
    }

    /**
     * Mkdirs throwing exceptions does nothing when directory already exists.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void mkdirsThrowingExceptionsDoesNothingWhenAlreadyExists(@TempDir File tempDir) throws IOException {
        // tempDir already exists - should not throw
        FileUtils.mkdirsThrowingExceptions(tempDir);
    }

    /**
     * Is file in parent returns true for direct child.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void isFileInParentReturnsTrueForDirectChild(@TempDir File tempDir) throws IOException {
        File child = new File(tempDir, "child.txt");
        assertTrue(child.createNewFile(), "File should have been created");

        assertTrue(FileUtils.isFileInParent(child, tempDir));
    }

    /**
     * Is file in parent returns true for nested child.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void isFileInParentReturnsTrueForNestedChild(@TempDir File tempDir) throws IOException {
        File subdir = new File(tempDir, "sub");
        subdir.mkdirs();
        File child = new File(subdir, "child.txt");
        assertTrue(child.createNewFile(), "File should have been created");

        assertTrue(FileUtils.isFileInParent(child, tempDir));
    }

    /**
     * Is file in parent returns false for unrelated file.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void isFileInParentReturnsFalseForUnrelatedFile(@TempDir File tempDir) throws IOException {
        File otherDir = new File(tempDir, "other");
        File parent = new File(tempDir, "parent");
        File child = new File(otherDir, "child.txt");

        assertFalse(FileUtils.isFileInParent(child, parent));
    }

    /**
     * Is file in parent returns false when file has no parent.
     */
    @Test
    void isFileInParentReturnsFalseWhenFileHasNoParent() {
        File file = new File("file.txt");
        File parent = new File("/some/parent");
        // A file with no parent (relative path with single segment) returns false
        assertFalse(FileUtils.isFileInParent(file, parent));
    }
}
