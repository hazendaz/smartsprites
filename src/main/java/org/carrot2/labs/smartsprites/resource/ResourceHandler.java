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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.carrot2.labs.smartsprites.SmartSpritesParameters;

/**
 * Defines an abstraction layer for resource (CSS, images) management. Resources are defined by means of abstract
 * implementation-dependent paths represented by plain {@link String}s. Responsibility for closing of all resources
 * acquired from this interface rests with the caller.
 *
 * @author Ibrahim Chaehoi
 * @author Stanislaw Osinski
 */
public interface ResourceHandler {

    /**
     * Returns the resource input stream for the provided path.
     *
     * @param path
     *            the resource path
     *
     * @return the resource stream or <code>null</code> if the resource could not be opened.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    InputStream getResourceAsInputStream(String path) throws IOException;

    /**
     * Returns the reader for the provided path. Implementations are responsible for creating the reader with the right
     * charset.
     *
     * @param path
     *            the resource path
     *
     * @return the reader or <code>null</code> if the resource could not be opened.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Reader getResourceAsReader(String path) throws IOException;

    /**
     * Returns the resource output stream for the provided path. If the resource already exists, its content should be
     * overwritten.
     *
     * @param path
     *            the resource path
     *
     * @return the resource stream or <code>null</code> if the resource could not be opened.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    OutputStream getResourceAsOutputStream(String path) throws IOException;

    /**
     * Returns the writer for the provided path. If the resource already exists, its content should be overwritten.
     * Implementations are responsible for creating the writer with the right charset.
     *
     * @param path
     *            the resource path
     *
     * @return the writer or <code>null</code> if the resource could not be opened.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Writer getResourceAsWriter(String path) throws IOException;

    /**
     * Builds a resource path relative to a CSS file resource path.
     *
     * @param cssFilePath
     *            the CSS file path (base)
     * @param cssRelativePath
     *            the relative path to be resolved against the cssFilePath. If the resource path starts with the '/'
     *            character, it is an absolute resource and should be resolved against the
     *            {@link SmartSpritesParameters#getDocumentRootDir()} path instead. All other resource paths should be
     *            assumed to be relative to the cssFilePath.
     *
     * @return the combined resource path
     */
    String getResourcePath(String cssFilePath, String cssRelativePath);
}
