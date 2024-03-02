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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

import org.apache.commons.io.FilenameUtils;
import org.carrot2.labs.smartsprites.SmartSpritesParameters;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.util.FileUtils;
import org.carrot2.util.StringUtils;

/**
 * This class defines the resource handler which manage resources from the file system.
 * 
 * @author Ibrahim Chaehoi
 * @author Stanislaw Osinski
 */
public class FileSystemResourceHandler implements ResourceHandler
{
    
    /**  The message log. */
    private final MessageLog messageLog;

    /**  The root directory. */
    private final String documentRootDir;

    /** The charset to assume in the {@link #getResourceAsReader(String)} method. */
    private final Charset charset;

    /**
     * Creates a new {@link FileSystemResourceHandler}.
     * 
     * @param documentRootDirPath the document root directory path, can be <code>null</code>
     * @param charset the charset to assume in the {@link #getResourceAsReader(String)}
     *            method
     * @param messageLog the message log
     */
    public FileSystemResourceHandler(String documentRootDirPath, String charset,
        MessageLog messageLog)
    {
        this.documentRootDir = documentRootDirPath;
        this.messageLog = messageLog;
        this.charset = Charset.forName(charset);
    }

    public InputStream getResourceAsInputStream(String path) throws IOException
    {
        return new FileInputStream(FileUtils.getCanonicalOrAbsoluteFile(path));
    }

    public Reader getResourceAsReader(String path) throws IOException
    {
        try
        {
            return new InputStreamReader(getResourceAsInputStream(path), charset);
        }
        catch (UnsupportedEncodingException e)
        {
            // Should not happen as we're checking the charset in constructor
            throw new RuntimeException(e);
        }
    }

    public OutputStream getResourceAsOutputStream(String path) throws IOException
    {
        // Create directories if needed
        final File parentFile = new File(path).getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs())
        {
            messageLog.warning(Message.MessageType.CANNOT_CREATE_DIRECTORIES,
                parentFile.getPath());
        }
        return new FileOutputStream(FileUtils.getCanonicalOrAbsoluteFile(path));
    }

    public Writer getResourceAsWriter(String path) throws IOException
    {
        try
        {
            return new OutputStreamWriter(getResourceAsOutputStream(path), charset);
        }
        catch (UnsupportedEncodingException e)
        {
            // Should not happen as we're checking the charset in constructor
            throw new RuntimeException(e);
        }
    }

    /**
     * This implementation detects if the resource path starts with a "/" and resolves
     * such resources against the provided
     * {@link SmartSpritesParameters#getDocumentRootDir()} directory.
     */
    public String getResourcePath(String baseFile, String filePath)
    {
        if (filePath.startsWith("/"))
        {
            if (StringUtils.isNotBlank(documentRootDir))
            {
                return FilenameUtils.concat(documentRootDir, filePath.substring(1));
            }
            else
            {
                messageLog.warning(MessageType.ABSOLUTE_PATH_AND_NO_DOCUMENT_ROOT,
                    filePath);
                return "";
            }
        }
        else
        {
            return FilenameUtils.concat(FilenameUtils.getFullPath(baseFile), filePath);
        }
    }
}
