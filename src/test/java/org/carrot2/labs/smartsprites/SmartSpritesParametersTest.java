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
package org.carrot2.labs.smartsprites;

import static org.carrot2.labs.test.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.util.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link SmartSpritesParameters}.
 */
class SmartSpritesParametersTest extends TestWithMemoryMessageSink {

    /** The existing root dir. */
    private File existingRootDir;

    /** The existing root dir path. */
    private String existingRootDirPath;

    /** The existing output dir. */
    private File existingOutputDir;

    /** The existing output dir path. */
    private String existingOutputDirPath;

    /** The existing file. */
    private File existingFile;

    /** The existing file path. */
    private String existingFilePath;

    /**
     * Prepare files.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @BeforeEach
    void prepareFiles() throws IOException {
        existingFile = File.createTempFile("smartsprites", null);
        existingFilePath = existingFile.getPath();
        existingRootDir = mkdirInTemp("rootdir");
        existingRootDirPath = existingRootDir.getPath();
        existingOutputDir = mkdirInTemp("outputdir");
        existingOutputDirPath = existingOutputDir.getPath();
    }

    /**
     * Clean up files.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @AfterEach
    void cleanUpFiles() throws IOException {
        FileUtils.deleteThrowingExceptions(existingFile, existingOutputDir, existingRootDir);
    }

    /**
     * Test validate no root dir no css files.
     */
    @Test
    void testValidateNoRootDirNoCssFiles() {
        checkInvalid(parameters(null, null), Message.error(MessageType.EITHER_ROOT_DIR_OR_CSS_FILES_IS_REQUIRED));
    }

    /**
     * Test validate root dir does not exist.
     */
    @Test
    void testValidateRootDirDoesNotExist() {
        final String dir = "nonexisting-dir";
        checkInvalid(parameters(dir, null),
                Message.error(MessageType.ROOT_DIR_DOES_NOT_EXIST_OR_IS_NOT_DIRECTORY, dir));
    }

    /**
     * Test validate output dir no root dir.
     */
    @Test
    void testValidateOutputDirNoRootDir() {
        checkInvalid(parameters(null, Lists.newArrayList("css/file.css"), existingOutputDirPath),
                Message.error(MessageType.ROOT_DIR_IS_REQUIRED_FOR_OUTPUT_DIR));
    }

    /**
     * Test validate output dir is not A directory.
     */
    @Test
    void testValidateOutputDirIsNotADirectory() {
        checkInvalid(parameters(existingRootDirPath, Lists.newArrayList("css/file.css"), existingFilePath),
                Message.error(MessageType.OUTPUT_DIR_IS_NOT_DIRECTORY, existingFilePath));
    }

    /**
     * Test validate document root dir does not exist.
     */
    @Test
    void testValidateDocumentRootDirDoesNotExist() {
        final String nonexistingDir = "nonexisting-dir";
        checkInvalid(parameters(existingRootDirPath, (String) null, nonexistingDir),
                Message.error(MessageType.DOCUMENT_ROOT_DIR_DOES_NOT_EXIST_OR_IS_NOT_DIRECTORY, nonexistingDir));
    }

    /**
     * Test validate document root dir is not A directory.
     */
    @Test
    void testValidateDocumentRootDirIsNotADirectory() {
        checkInvalid(parameters(existingRootDirPath, (String) null, existingFilePath),
                Message.error(MessageType.DOCUMENT_ROOT_DIR_DOES_NOT_EXIST_OR_IS_NOT_DIRECTORY, existingFilePath));
    }

    /**
     * Test validate no output dir and empty css file suffix.
     */
    @Test
    void testValidateNoOutputDirAndEmptyCssFileSuffix() {
        checkInvalid(
                new SmartSpritesParameters(null, Lists.newArrayList("css/file.css"), null, null, null, "", null, null),
                Message.error(MessageType.CSS_FILE_SUFFIX_IS_REQUIRED_IF_NO_OUTPUT_DIR));
    }

    /**
     * Test validate root dir and css files without output dir.
     */
    @Test
    void testValidateRootDirAndCssFilesWithoutOutputDir() {
        checkInvalid(parameters(existingRootDirPath, Lists.newArrayList("css/file.css"), null),
                Message.error(MessageType.ROOT_DIR_AND_CSS_FILES_CANNOT_BE_BOTH_SPECIFIED_UNLESS_WITH_OUTPUT_DIR));
    }

    /**
     * Test validate valid all dirs.
     */
    @Test
    void testValidateValidAllDirs() {
        checkValid(parameters(existingRootDirPath, existingOutputDirPath, existingOutputDirPath));

    }

    /**
     * Test validate valid only root dir.
     */
    @Test
    void testValidateValidOnlyRootDir() {
        checkValid(parameters(".", null));
    }

    /**
     * Check valid.
     *
     * @param parameters
     *            the parameters
     */
    private void checkValid(final SmartSpritesParameters parameters) {
        assertTrue(parameters.validate(messageLog));
    }

    /**
     * Check invalid.
     *
     * @param parameters
     *            the parameters
     * @param messages
     *            the messages
     */
    private void checkInvalid(SmartSpritesParameters parameters, Message... messages) {
        assertFalse(parameters.validate(messageLog));
        assertThat(this.messages).contains(messages);
    }

    /**
     * Parameters.
     *
     * @param rootDir
     *            the root dir
     * @param outputDir
     *            the output dir
     *
     * @return the smart sprites parameters
     */
    private static SmartSpritesParameters parameters(String rootDir, String outputDir) {
        return parameters(rootDir, outputDir, null);
    }

    /**
     * Parameters.
     *
     * @param rootDir
     *            the root dir
     * @param outputDir
     *            the output dir
     * @param documentRootDir
     *            the document root dir
     *
     * @return the smart sprites parameters
     */
    private static SmartSpritesParameters parameters(String rootDir, String outputDir, String documentRootDir) {
        return new SmartSpritesParameters(rootDir, null, outputDir, documentRootDir, null, null, null, null);
    }

    /**
     * Parameters.
     *
     * @param rootDir
     *            the root dir
     * @param cssFiles
     *            the css files
     * @param outputDir
     *            the output dir
     *
     * @return the smart sprites parameters
     */
    private static SmartSpritesParameters parameters(String rootDir, List<String> cssFiles, String outputDir) {
        return new SmartSpritesParameters(rootDir, cssFiles, outputDir, null, null, null, null, null);
    }

    /**
     * Mkdir in temp.
     *
     * @param name
     *            the name
     *
     * @return the file
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private File mkdirInTemp(final String name) throws IOException {
        final File file = new File(existingFile.getParent(), existingFile.getName() + name);
        FileUtils.mkdirsThrowingExceptions(file);
        return file;
    }
}
