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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.util.FileUtils;
import org.carrot2.util.StringUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Contains invocation parameters for SmartSprites, provides methods for validating the parameters.
 */
public final class SmartSpritesParameters {
    /**
     * Path to the directory that contains the css files to be processed. Directories containing CSS and image files
     * must be writable, unless output.dir.path is provided. The root.dir.path can be either absolute, e.g.
     * c:/myproject/web or relative to the directory in which this script is run.
     */
    @Option(name = "--root-dir-path", required = false, metaVar = "DIR")
    private String rootDir;

    /**
     * Paths to individual CSS files to process. Either {@link #rootDir} or {@link #cssFiles} must be not empty. If only
     * {@link #cssFiles} is not empty, {@link #outputDir} must be blank. If both {@link #rootDir} and {@link #cssFiles}
     * are not empty, {@link #outputDir} is supported but only {@link #cssFiles} from {@link #rootDir} are processed.
     */
    @Argument(metaVar = "CSS-FILES")
    @Option(name = "--css-files", required = false, metaVar = "FILES")
    private List<String> cssFiles;

    /**
     * Output directory for processed CSS files and CSS-relative sprite images. The directory structure relative to
     * root.dir.path will be preserved in the output directory. E.g. if CSS files are contained in the css/base
     * directory of root.dir.path, the processed results will be written to output.dir.path/css/base. Also, CSS-relative
     * sprite images will be written to the output directory. Sprite images with document-root-relative URLs will be
     * written relative to the document.root.dir.path.
     * <p>
     * If the output.dir.path directory does not exist, it will be created.
     * <p>
     * You can leave this property empty, in which case the CSS files will be written next to the original CSS files
     * with css.file.suffix, and sprite images will be written relative to CSS files.
     * <p>
     * If you are using a non-empty output.dir.path, you might want to use an empty css.file.suffix.
     */
    @Option(name = "--output-dir-path", metaVar = "DIR")
    private String outputDir;

    /**
     * Document root path for document-root-relative (starting with '/') image urls in CSS. All such image URLs will be
     * taken relative to document.root.dir.path. Also document-root-relative sprite URLs will be written relative to
     * document.root.dir.path. You can leave this property empty if your CSS uses only CSS-relative image URLs. *
     */
    @Option(name = "--document-root-dir-path", metaVar = "DIR")
    private String documentRootDir;

    /**
     * Message logging level. If you're getting lots of INFO messages and want to see only warnings, set this option to
     * WARN.
     */
    @Option(name = "--log-level")
    private MessageLevel logLevel;

    /**
     * The encoding to assume for input and output CSS files.
     */
    @Option(name = "--css-file-encoding")
    private String cssFileEncoding;

    /**
     * Suffix to be appended to the processed CSS file name.
     */
    @Option(name = "--css-file-suffix")
    private String cssFileSuffix;

    /**
     * The required depth of the generated PNG sprites.
     */
    @Option(name = "--sprite-png-depth")
    private PngDepth spritePngDepth;

    /**
     * If <code>true</code>, SmartSprites will generate the sprite directive indicating that the image is a sprite
     * image.
     */
    @Option(name = "--mark-sprite-images")
    private boolean markSpriteImages;

    /** The default suffix to be added to the generated CSS files. */
    public static final String DEFAULT_CSS_FILE_SUFFIX = "-sprite";

    /** By default, we use full color only when necessary. */
    public static final PngDepth DEFAULT_SPRITE_PNG_DEPTH = PngDepth.AUTO;

    /** By default, we'll assume CSS files are UTF-8 encoded. */
    public static final String DEFAULT_CSS_FILE_ENCODING = StandardCharsets.UTF_8.name();

    /** The default logging level. */
    public static final MessageLevel DEFAULT_LOGGING_LEVEL = MessageLevel.INFO;

    /** By default, we don't generate sprite directive in output css. */
    public static final boolean DEFAULT_MARK_SPRITE_IMAGES = false;

    /**
     * The Enum PngDepth.
     */
    public enum PngDepth {

        /** The auto. */
        AUTO,
        /** The indexed. */
        INDEXED,
        /** The direct. */
        DIRECT;
    }

    /**
     * Creates the parameters with default options and null root dir, before root dir is set, the parameters are
     * invalid.
     */
    public SmartSpritesParameters() {
        this(null);
    }

    /**
     * Creates the parameters with most default values.
     *
     * @param rootDir
     *            the root dir
     */
    public SmartSpritesParameters(String rootDir) {
        this(rootDir, null, null, null, MessageLevel.INFO, DEFAULT_CSS_FILE_SUFFIX, DEFAULT_SPRITE_PNG_DEPTH,
                DEFAULT_CSS_FILE_ENCODING, DEFAULT_MARK_SPRITE_IMAGES);
    }

    /**
     * Creates the parameters.
     *
     * @param rootDir
     *            the root dir
     * @param cssFiles
     *            the css files
     * @param outputDir
     *            the output dir
     * @param documentRootDir
     *            the document root dir
     * @param logLevel
     *            the log level
     * @param cssFileSuffix
     *            the css file suffix
     * @param spritePngDepth
     *            the sprite png depth
     * @param cssEncoding
     *            the css encoding
     */
    public SmartSpritesParameters(String rootDir, List<String> cssFiles, String outputDir, String documentRootDir,
            MessageLevel logLevel, String cssFileSuffix, PngDepth spritePngDepth, String cssEncoding) {
        this(rootDir, cssFiles, outputDir, documentRootDir, logLevel, cssFileSuffix, spritePngDepth, cssEncoding,
                DEFAULT_MARK_SPRITE_IMAGES);
    }

    /**
     * Creates the parameters.
     *
     * @param rootDir
     *            the root dir
     * @param cssFiles
     *            the css files
     * @param outputDir
     *            the output dir
     * @param documentRootDir
     *            the document root dir
     * @param logLevel
     *            the log level
     * @param cssFileSuffix
     *            the css file suffix
     * @param spritePngDepth
     *            the sprite png depth
     * @param cssEncoding
     *            the css encoding
     * @param markSpriteImages
     *            the mark sprite images
     */
    public SmartSpritesParameters(String rootDir, List<String> cssFiles, String outputDir, String documentRootDir,
            MessageLevel logLevel, String cssFileSuffix, PngDepth spritePngDepth, String cssEncoding,
            boolean markSpriteImages) {
        this.rootDir = rootDir;
        this.cssFiles = cssFiles;
        this.outputDir = outputDir;
        this.documentRootDir = documentRootDir;
        this.logLevel = logLevel;
        this.cssFileEncoding = cssEncoding;
        this.cssFileSuffix = getCssFileSuffix(cssFileSuffix);
        this.spritePngDepth = spritePngDepth;
        this.markSpriteImages = markSpriteImages;
    }

    /**
     * Validates the provided parameters. All resource paths are resolved against the local file system.
     *
     * @param log
     *            the log
     *
     * @return <code>true</code> if the parameters are valid
     */
    public boolean validate(MessageLog log) {
        boolean valid = true;

        // Either root dir or css files are required
        if (!hasRootDir() && !hasCssFiles()) {
            log.error(MessageType.EITHER_ROOT_DIR_OR_CSS_FILES_IS_REQUIRED);
            return false;
        }

        // If there is no output dir, we can't have both root dir or css files
        if (!hasOutputDir() && hasRootDir() && hasCssFiles()) {
            log.error(MessageType.ROOT_DIR_AND_CSS_FILES_CANNOT_BE_BOTH_SPECIFIED_UNLESS_WITH_OUTPUT_DIR);
            return false;
        }

        // Check root dir if provided
        if (hasRootDir()) {
            final File rootDir = FileUtils.getCanonicalOrAbsoluteFile(this.rootDir);
            if (!rootDir.exists() || !rootDir.isDirectory()) {
                log.error(MessageType.ROOT_DIR_DOES_NOT_EXIST_OR_IS_NOT_DIRECTORY, this.rootDir);
                valid = false;
            }
        }

        // Check output dir if provided
        if (hasOutputDir()) {
            // For output dir, we need root dir
            if (!hasRootDir()) {
                log.error(MessageType.ROOT_DIR_IS_REQUIRED_FOR_OUTPUT_DIR);
                return false;
            }

            final File outputDir = FileUtils.getCanonicalOrAbsoluteFile(this.outputDir);
            if (outputDir.exists() && !outputDir.isDirectory()) {
                log.error(MessageType.OUTPUT_DIR_IS_NOT_DIRECTORY, this.outputDir);
                valid = false;
            }
        }

        if (!hasOutputDir() && StringUtils.isBlank(cssFileSuffix)) {
            log.error(MessageType.CSS_FILE_SUFFIX_IS_REQUIRED_IF_NO_OUTPUT_DIR);
            valid = false;
        }

        if (hasDocumentRootDir()) {
            final File documentRootDir = FileUtils.getCanonicalOrAbsoluteFile(this.documentRootDir);
            if (!documentRootDir.exists() || !documentRootDir.isDirectory()) {
                log.error(MessageType.DOCUMENT_ROOT_DIR_DOES_NOT_EXIST_OR_IS_NOT_DIRECTORY, this.documentRootDir);
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Gets the css file suffix.
     *
     * @param suffix
     *            the suffix
     *
     * @return the css file suffix
     */
    private String getCssFileSuffix(String suffix) {
        if (suffix != null) {
            return suffix;
        }
        if (!hasOutputDir()) {
            // If there is no output dir, we must have some suffix
            return DEFAULT_CSS_FILE_SUFFIX;
        }
        // If we have an output dir, we can have an empty suffix
        return "";
    }

    /**
     * Gets the root dir.
     *
     * @return the root dir
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * Gets the root dir file.
     *
     * @return the root dir file
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public File getRootDirFile() throws IOException {
        return rootDir.startsWith("..") ? new File(rootDir).getCanonicalFile() : new File(rootDir);
    }

    /**
     * Checks for root dir.
     *
     * @return true, if successful
     */
    public boolean hasRootDir() {
        return StringUtils.isNotBlank(rootDir);
    }

    /**
     * Gets the css files.
     *
     * @return the css files
     */
    public List<String> getCssFiles() {
        return cssFiles;
    }

    /**
     * Checks for css files.
     *
     * @return true, if successful
     */
    public boolean hasCssFiles() {
        return cssFiles != null && !cssFiles.isEmpty();
    }

    /**
     * Gets the output dir.
     *
     * @return the output dir
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Checks for output dir.
     *
     * @return true, if successful
     */
    public boolean hasOutputDir() {
        return StringUtils.isNotBlank(outputDir);
    }

    /**
     * Gets the document root dir.
     *
     * @return the document root dir
     */
    public String getDocumentRootDir() {
        return documentRootDir;
    }

    /**
     * Checks for document root dir.
     *
     * @return true, if successful
     */
    public boolean hasDocumentRootDir() {
        return StringUtils.isNotBlank(documentRootDir);
    }

    /**
     * Gets the log level.
     *
     * @return the log level
     */
    public MessageLevel getLogLevel() {
        return logLevel;
    }

    /**
     * Gets the css file suffix.
     *
     * @return the css file suffix
     */
    public String getCssFileSuffix() {
        return cssFileSuffix;
    }

    /**
     * Gets the sprite png depth.
     *
     * @return the sprite png depth
     */
    public PngDepth getSpritePngDepth() {
        return spritePngDepth;
    }

    /**
     * Checks if is mark sprite images.
     *
     * @return true, if is mark sprite images
     */
    public boolean isMarkSpriteImages() {
        return markSpriteImages;
    }

    /**
     * Gets the css file encoding.
     *
     * @return the css file encoding
     */
    public String getCssFileEncoding() {
        return cssFileEncoding;
    }
}
