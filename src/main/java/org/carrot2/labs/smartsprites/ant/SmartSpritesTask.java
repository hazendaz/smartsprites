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
package org.carrot2.labs.smartsprites.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.carrot2.labs.smartsprites.SmartSpritesParameters;
import org.carrot2.labs.smartsprites.SmartSpritesParameters.PngDepth;
import org.carrot2.labs.smartsprites.SpriteBuilder;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.labs.smartsprites.message.MessageSink;
import org.carrot2.util.EnumUtils;

/**
 * Ant task for calling SmartSprites processing.
 */
public class SmartSpritesTask extends Task {

    /** The root dir. */
    private String rootDir;

    /** The output dir. */
    private String outputDir;

    /** The document root dir. */
    private String documentRootDir;

    /** The log level. */
    private MessageLevel logLevel;

    /** The fail on level. */
    private MessageLevel failOnLevel = MessageLevel.ERROR;

    /** The css file suffix. */
    private String cssFileSuffix = SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX;

    /** The css file encoding. */
    private String cssFileEncoding = SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING;

    /** The sprite png depth. */
    private PngDepth spritePngDepth = SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH;

    /** The sprite png ie 6. */
    private boolean spritePngIe6 = SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6;

    /** The mark sprite images. */
    private boolean markSpriteImages = SmartSpritesParameters.DEFAULT_MARK_SPRITE_IMAGES;

    /** The css files. */
    private List<String> cssFiles = new ArrayList<>();

    /**
     * Sets the root dir.
     *
     * @param dir
     *            the new root dir
     */
    public void setRootDir(File dir) {
        this.rootDir = dir.getPath();
    }

    /**
     * Sets the output dir.
     *
     * @param outputDir
     *            the new output dir
     */
    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir.getPath();
    }

    /**
     * Sets the document root dir.
     *
     * @param documentRootDir
     *            the new document root dir
     */
    public void setDocumentRootDir(File documentRootDir) {
        this.documentRootDir = documentRootDir.getPath();
    }

    /**
     * Sets the log level.
     *
     * @param logLevel
     *            the new log level
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = getLogLevelFromString(logLevel, MessageLevel.INFO);
    }

    /**
     * Sets the fail on level.
     *
     * @param failOnLevel
     *            the new fail on level
     */
    public void setFailOnLevel(String failOnLevel) {
        this.failOnLevel = getLogLevelFromString(failOnLevel, MessageLevel.ERROR);
    }

    /**
     * Gets the log level from string.
     *
     * @param logLevel
     *            the log level
     * @param defaultLevel
     *            the default level
     *
     * @return the log level from string
     */
    private MessageLevel getLogLevelFromString(String logLevel, MessageLevel defaultLevel) {
        try {
            return MessageLevel.valueOf(logLevel);
        } catch (Exception e) {
            return defaultLevel;
        }
    }

    /**
     * Sets the css file encoding.
     *
     * @param cssFileEncoding
     *            the new css file encoding
     */
    public void setCssFileEncoding(String cssFileEncoding) {
        this.cssFileEncoding = cssFileEncoding;
    }

    /**
     * Sets the css file suffix.
     *
     * @param cssFileSuffix
     *            the new css file suffix
     */
    public void setCssFileSuffix(String cssFileSuffix) {
        this.cssFileSuffix = cssFileSuffix;
    }

    /**
     * Sets the sprite png depth.
     *
     * @param spritePngDepthString
     *            the new sprite png depth
     */
    public void setSpritePngDepth(String spritePngDepthString) {
        this.spritePngDepth = EnumUtils.valueOf(spritePngDepthString, PngDepth.class,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH);
    }

    /**
     * Sets the sprite png ie 6.
     *
     * @param spritePngIe6
     *            the new sprite png ie 6
     */
    public void setSpritePngIe6(boolean spritePngIe6) {
        this.spritePngIe6 = spritePngIe6;
    }

    /**
     * Sets the mark sprite images.
     *
     * @param markSpriteImages
     *            the new mark sprite images
     */
    public void setMarkSpriteImages(boolean markSpriteImages) {
        this.markSpriteImages = markSpriteImages;
    }

    @Override
    public void execute() {
        final SmartSpritesParameters parameters = new SmartSpritesParameters(rootDir, cssFiles, outputDir,
                documentRootDir, logLevel, cssFileSuffix, spritePngDepth, spritePngIe6, cssFileEncoding,
                markSpriteImages);

        final FailureDetectorMessageSink failureDetectorMessageSink = new FailureDetectorMessageSink();
        MessageLog log = new MessageLog(new AntLogMessageSink(), failureDetectorMessageSink);

        if (parameters.validate(log)) {
            try {
                new SpriteBuilder(parameters, log).buildSprites();
            } catch (IOException e) {
                throw new BuildException(e);
            }
        }

        if (failureDetectorMessageSink.shouldFail) {
            throw new BuildException(failureDetectorMessageSink.failureLevel.name() + " messages found");
        }
    }

    /**
     * The Class AntLogMessageSink.
     */
    private class AntLogMessageSink implements MessageSink {
        public void add(Message message) {
            if (MessageLevel.COMPARATOR.compare(message.level, logLevel) >= 0) {
                log(message.toString());
            }
        }
    }

    /**
     * The Class FailureDetectorMessageSink.
     */
    private class FailureDetectorMessageSink implements MessageSink {

        /** The should fail. */
        boolean shouldFail = false;

        /** The failure level. */
        MessageLevel failureLevel = null;

        public void add(Message message) {
            if (failOnLevel != null && MessageLevel.COMPARATOR.compare(message.level, failOnLevel) >= 0
                    && message.level != MessageLevel.STATUS) {
                failureLevel = message.level;
                shouldFail = true;
            }
        }
    }

    /**
     * Adds the configured fileset.
     *
     * @param fileset
     *            the fileset
     */
    public void addConfiguredFileset(FileSet fileset) {
        for (Iterator<Resource> it = fileset.iterator(); it.hasNext();) {
            cssFiles.add(it.next().getName());
        }
    }
}
