/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.carrot2.labs.smartsprites.SmartSpritesParameters;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.util.FileUtils;
import org.carrot2.util.StringUtils;

/**
 * This class defines the resource handler which manage resources from the file system.
 *
 * @author Ibrahim Chaehoi
 * @author Stanislaw Osinski
 */
public class FileSystemResourceHandler implements ResourceHandler {

    /** The message log. */
    private final MessageLog messageLog;

    /** The root directory. */
    private final String documentRootDir;

    /** The charset to assume in the {@link #getResourceAsReader(String)} method. */
    private final Charset charset;

    /**
     * Creates a new {@link FileSystemResourceHandler}.
     *
     * @param documentRootDirPath
     *            the document root directory path, can be <code>null</code>
     * @param charset
     *            the charset to assume in the {@link #getResourceAsReader(String)} method
     * @param messageLog
     *            the message log
     */
    public FileSystemResourceHandler(String documentRootDirPath, String charset, MessageLog messageLog) {
        this.documentRootDir = documentRootDirPath;
        this.messageLog = messageLog;
        this.charset = Charset.forName(charset);
    }

    @Override
    public InputStream getResourceAsInputStream(String path) throws IOException {
        return Files.newInputStream(FileUtils.getCanonicalOrAbsoluteFile(path).toPath());
    }

    @Override
    public Reader getResourceAsReader(String path) throws IOException {
        try {
            return new InputStreamReader(getResourceAsInputStream(path), charset);
        } catch (UnsupportedEncodingException e) {
            // Should not happen as we're checking the charset in constructor
            throw new RuntimeException(e);
        }
    }

    @Override
    public OutputStream getResourceAsOutputStream(String path) throws IOException {
        // Create directories if needed
        final File parentFile = Path.of(path).toFile().getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            messageLog.warning(Message.MessageType.CANNOT_CREATE_DIRECTORIES, parentFile.getPath());
        }
        return Files.newOutputStream(FileUtils.getCanonicalOrAbsoluteFile(path).toPath());
    }

    @Override
    public Writer getResourceAsWriter(String path) throws IOException {
        try {
            return new OutputStreamWriter(getResourceAsOutputStream(path), charset);
        } catch (UnsupportedEncodingException e) {
            // Should not happen as we're checking the charset in constructor
            throw new RuntimeException(e);
        }
    }

    /**
     * This implementation detects if the resource path starts with a "/" and resolves such resources against the
     * provided {@link SmartSpritesParameters#getDocumentRootDir()} directory.
     */
    @Override
    public String getResourcePath(String baseFile, String filePath) {
        if (!filePath.startsWith("/")) {
            return FilenameUtils.concat(FilenameUtils.getFullPath(baseFile), filePath);
        }
        if (StringUtils.isNotBlank(documentRootDir)) {
            return FilenameUtils.concat(documentRootDir, filePath.substring(1));
        }
        messageLog.warning(MessageType.ABSOLUTE_PATH_AND_NO_DOCUMENT_ROOT, filePath);
        return "";
    }
}
