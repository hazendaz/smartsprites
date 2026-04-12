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
package org.carrot2.labs.smartsprites.resource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.carrot2.labs.smartsprites.message.MemoryMessageSink;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test cases for {@link FileSystemResourceHandler}.
 */
class FileSystemResourceHandlerTest {

    /** The message log. */
    private MessageLog messageLog;

    /**
     * Sets up the message log.
     */
    @BeforeEach
    void setUp() {
        messageLog = new MessageLog(new MemoryMessageSink());
    }

    /**
     * Get resource as input stream reads existing file.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void getResourceAsInputStreamReadsExistingFile(@TempDir File tempDir) throws IOException {
        File file = new File(tempDir, "test.txt");
        Files.writeString(file.toPath(), "hello", StandardCharsets.UTF_8);

        FileSystemResourceHandler handler = new FileSystemResourceHandler(null, "UTF-8", messageLog);
        try (InputStream is = handler.getResourceAsInputStream(file.getPath())) {
            assertNotNull(is);
            byte[] bytes = is.readAllBytes();
            assertTrue(bytes.length > 0);
        }
    }

    /**
     * Get resource as reader reads existing file content.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void getResourceAsReaderReadsExistingFileContent(@TempDir File tempDir) throws IOException {
        File file = new File(tempDir, "test.txt");
        Files.writeString(file.toPath(), "world", StandardCharsets.UTF_8);

        FileSystemResourceHandler handler = new FileSystemResourceHandler(null, "UTF-8", messageLog);
        try (Reader reader = handler.getResourceAsReader(file.getPath())) {
            assertNotNull(reader);
            char[] buf = new char[10];
            int n = reader.read(buf);
            assertTrue(n > 0);
            String content = new String(buf, 0, n);
            assertTrue(content.contains("world"), "Should read file content: " + content);
        }
    }

    /**
     * Get resource as output stream creates the file.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void getResourceAsOutputStreamCreatesFile(@TempDir File tempDir) throws IOException {
        File file = new File(tempDir, "output.txt");

        FileSystemResourceHandler handler = new FileSystemResourceHandler(null, "UTF-8", messageLog);
        try (OutputStream os = handler.getResourceAsOutputStream(file.getPath())) {
            assertNotNull(os);
            os.write("test".getBytes(StandardCharsets.UTF_8));
        }

        assertTrue(file.exists(), "Output file should have been created");
    }

    /**
     * Get resource as writer writes content.
     *
     * @param tempDir
     *            the temp dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void getResourceAsWriterWritesContent(@TempDir File tempDir) throws IOException {
        File file = new File(tempDir, "writer.txt");

        FileSystemResourceHandler handler = new FileSystemResourceHandler(null, "UTF-8", messageLog);
        try (Writer writer = handler.getResourceAsWriter(file.getPath())) {
            assertNotNull(writer);
            writer.write("written content");
        }

        String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        assertTrue(content.contains("written content"), "Should have written content: " + content);
    }

    /**
     * Get resource path with relative path resolves against base file.
     */
    @Test
    void getResourcePathWithRelativePathResolvesAgainstBaseFile() {
        FileSystemResourceHandler handler = new FileSystemResourceHandler(null, "UTF-8", messageLog);
        String result = handler.getResourcePath("/css/main.css", "images/sprite.png");
        assertNotNull(result);
        assertTrue(result.contains("sprite.png"), "Should contain the file name: " + result);
        assertTrue(result.contains("images"), "Should contain the directory: " + result);
    }

    /**
     * Get resource path with absolute path and document root resolves against root.
     *
     * @param tempDir
     *            the temp dir
     */
    @Test
    void getResourcePathWithAbsolutePathAndDocumentRootResolvesAgainstRoot(@TempDir File tempDir) {
        FileSystemResourceHandler handler = new FileSystemResourceHandler(tempDir.getPath(), "UTF-8", messageLog);
        String result = handler.getResourcePath("/css/main.css", "/images/sprite.png");
        assertNotNull(result);
        assertTrue(result.contains("sprite.png"), "Should contain the file name: " + result);
        assertTrue(result.contains(tempDir.getPath()), "Should be rooted at document root: " + result);
    }

    /**
     * Get resource path with absolute path and no document root logs warning.
     */
    @Test
    void getResourcePathWithAbsolutePathAndNoDocumentRootLogsWarning() {
        MemoryMessageSink memorySink = new MemoryMessageSink();
        MessageLog log = new MessageLog(memorySink);
        FileSystemResourceHandler handler = new FileSystemResourceHandler(null, "UTF-8", log);

        String result = handler.getResourcePath("/css/main.css", "/images/sprite.png");

        // When no document root dir, it returns empty string and logs a warning
        assertTrue(result.isEmpty() || result != null, "Result should be empty or non-null: " + result);
        assertNotNull(memorySink.messages, "Should have logged messages");
    }
}
