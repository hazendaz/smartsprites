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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.carrot2.labs.smartsprites.message.LevelCounterMessageSink;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.labs.smartsprites.resource.FileSystemResourceHandler;
import org.carrot2.labs.smartsprites.resource.ResourceHandler;
import org.carrot2.util.FileUtils;
import org.carrot2.util.PathUtils;
import org.carrot2.util.StringUtils;

/**
 * Performs all stages of sprite building. This class is not thread-safe.
 */
public class SpriteBuilder {
    /** Properties we need to watch for in terms of overriding the generated ones. */
    private static final HashSet<String> OVERRIDING_PROPERTIES = Sets.newHashSet("background-position",
            "background-image");

    /** This builder's configuration. */
    public final SmartSpritesParameters parameters;

    /** This builder's message log. */
    private final MessageLog messageLog;

    /** Directive occurrence collector for this builder. */
    private final SpriteDirectiveOccurrenceCollector spriteDirectiveOccurrenceCollector;

    /** SpriteImageBuilder for this builder. */
    private final SpriteImageBuilder spriteImageBuilder;

    /** Resource handler. */
    private ResourceHandler resourceHandler;

    /**
     * Creates a {@link SpriteBuilder} with the provided parameters and log.
     *
     * @param parameters
     *            the parameters
     * @param messageLog
     *            the message log
     */
    public SpriteBuilder(SmartSpritesParameters parameters, MessageLog messageLog) {
        this(parameters, messageLog, new FileSystemResourceHandler(parameters.getDocumentRootDir(),
                parameters.getCssFileEncoding(), messageLog));
    }

    /**
     * Creates a {@link SpriteBuilder} with the provided parameters and log.
     *
     * @param parameters
     *            the parameters
     * @param messageLog
     *            the message log
     * @param resourceHandler
     *            the resource handler
     */
    public SpriteBuilder(SmartSpritesParameters parameters, MessageLog messageLog, ResourceHandler resourceHandler) {
        this.messageLog = messageLog;
        this.parameters = parameters;
        this.resourceHandler = resourceHandler;
        spriteDirectiveOccurrenceCollector = new SpriteDirectiveOccurrenceCollector(messageLog, resourceHandler);
        spriteImageBuilder = new SpriteImageBuilder(parameters, messageLog, resourceHandler);
    }

    /**
     * Performs processing for this builder's parameters. This method resolves all paths against the local file system.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void buildSprites() throws IOException {
        if (!parameters.validate(messageLog)) {
            return;
        }

        final Collection<String> filePaths;
        if (parameters.getCssFiles() != null && !parameters.getCssFiles().isEmpty()) {
            // Take directly provided css fle paths
            filePaths = parameters.getCssFiles();

            // If root dir is provided, filter out those files that are outside root dir
            if (StringUtils.isNotBlank(parameters.getOutputDir())) {
                filterFilesOutsideRootDir(filePaths);
            }

            // Make sure the files exist and are really files
            for (Iterator<String> it = filePaths.iterator(); it.hasNext();) {
                final String path = it.next();
                final File file = new File(path);
                if (file.exists()) {
                    if (!file.isFile()) {
                        messageLog.warning(MessageType.CSS_PATH_IS_NOT_A_FILE, path);
                        it.remove();
                    }
                } else {
                    messageLog.warning(MessageType.CSS_FILE_DOES_NOT_EXIST, path);
                    it.remove();
                }
            }
        } else {
            // Take all css files from the root dir
            final List<File> files = Lists.newArrayList(org.apache.commons.io.FileUtils
                    .listFiles(parameters.getRootDirFile(), new String[] { "css" }, true));
            Collections.sort(files, Comparator.comparing(File::getAbsolutePath));

            filePaths = new ArrayList<>();
            for (File file : files) {
                filePaths.add(file.getPath());
            }
        }

        buildSprites(filePaths);
    }

    /**
     * Filter files outside root dir.
     *
     * @param filePaths
     *            the file paths
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void filterFilesOutsideRootDir(Collection<String> filePaths) throws IOException {
        for (Iterator<String> it = filePaths.iterator(); it.hasNext();) {
            final String filePath = it.next();
            if (!FileUtils.isFileInParent(new File(filePath), parameters.getRootDirFile())) {
                it.remove();
                messageLog.warning(MessageType.IGNORING_CSS_FILE_OUTSIDE_OF_ROOT_DIR, filePath);
            }
        }
    }

    /**
     * Performs processing from the list of file paths for this builder's parameters.
     *
     * @param filePaths
     *            paths of CSS files to process. Non-absolute paths will be taken relative to the current working
     *            directory. Both platform-specific and '/' as the file separator are supported.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void buildSprites(Collection<String> filePaths) throws IOException {
        final long start = System.currentTimeMillis();

        final LevelCounterMessageSink levelCounter = new LevelCounterMessageSink();
        messageLog.addMessageSink(levelCounter);

        // Collect sprite declarations from all css files
        final Multimap<String, SpriteImageOccurrence> spriteImageOccurrencesByFile = spriteDirectiveOccurrenceCollector
                .collectSpriteImageOccurrences(filePaths);

        // Merge them, checking for duplicates
        final Map<String, SpriteImageOccurrence> spriteImageOccurrencesBySpriteId = spriteDirectiveOccurrenceCollector
                .mergeSpriteImageOccurrences(spriteImageOccurrencesByFile);
        final Map<String, SpriteImageDirective> spriteImageDirectivesBySpriteId = new LinkedHashMap<>();
        for (Map.Entry<String, SpriteImageOccurrence> entry : spriteImageOccurrencesBySpriteId.entrySet()) {
            spriteImageDirectivesBySpriteId.put(entry.getKey(), entry.getValue().spriteImageDirective);
        }

        // Collect sprite references from all css files
        final Multimap<String, SpriteReferenceOccurrence> spriteEntriesByFile = spriteDirectiveOccurrenceCollector
                .collectSpriteReferenceOccurrences(filePaths, spriteImageDirectivesBySpriteId);

        // Now merge and regroup all files by sprite-id
        final Multimap<String, SpriteReferenceOccurrence> spriteReferenceOccurrencesBySpriteId = SpriteDirectiveOccurrenceCollector
                .mergeSpriteReferenceOccurrences(spriteEntriesByFile);

        // Build the sprite images
        messageLog.setCssFile(null);
        final Multimap<String, SpriteReferenceReplacement> spriteReplacementsByFile = spriteImageBuilder
                .buildSpriteImages(spriteImageOccurrencesBySpriteId, spriteReferenceOccurrencesBySpriteId);

        // Rewrite the CSS
        rewriteCssFiles(spriteImageOccurrencesByFile, spriteReplacementsByFile);

        final long stop = System.currentTimeMillis();

        if (levelCounter.getWarnCount() > 0) {
            messageLog.status(MessageType.PROCESSING_COMPLETED_WITH_WARNINGS, stop - start,
                    levelCounter.getWarnCount());
        } else {
            messageLog.status(MessageType.PROCESSING_COMPLETED, stop - start);
        }
    }

    /**
     * Rewrites the original files to refer to the generated sprite images.
     *
     * @param spriteImageOccurrencesByFile
     *            the sprite image occurrences by file
     * @param spriteReplacementsByFile
     *            the sprite replacements by file
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void rewriteCssFiles(final Multimap<String, SpriteImageOccurrence> spriteImageOccurrencesByFile,
            final Multimap<String, SpriteReferenceReplacement> spriteReplacementsByFile) throws IOException {
        if (spriteReplacementsByFile.isEmpty()) {
            // If nothing to replace, still, copy the original file, so that there
            // is some output file.
            for (final Map.Entry<String, Collection<SpriteImageOccurrence>> entry : spriteImageOccurrencesByFile.asMap()
                    .entrySet()) {
                final String cssFile = entry.getKey();

                createProcessedCss(cssFile, SpriteImageBuilder.getSpriteImageOccurrencesByLineNumber(
                        spriteImageOccurrencesByFile.get(cssFile)), new HashMap<>());
            }
        } else {
            for (final Map.Entry<String, Collection<SpriteReferenceReplacement>> entry : spriteReplacementsByFile
                    .asMap().entrySet()) {
                final String cssFile = entry.getKey();
                final Map<Integer, SpriteReferenceReplacement> spriteReplacementsByLineNumber = SpriteImageBuilder
                        .getSpriteReplacementsByLineNumber(entry.getValue());

                createProcessedCss(cssFile, SpriteImageBuilder.getSpriteImageOccurrencesByLineNumber(
                        spriteImageOccurrencesByFile.get(cssFile)), spriteReplacementsByLineNumber);
            }
        }
    }

    /**
     * Rewrites one CSS file to refer to the generated sprite images.
     *
     * @param originalCssFile
     *            the original css file
     * @param spriteImageOccurrencesByLineNumber
     *            the sprite image occurrences by line number
     * @param spriteReplacementsByLineNumber
     *            the sprite replacements by line number
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void createProcessedCss(String originalCssFile,
            Map<Integer, SpriteImageOccurrence> spriteImageOccurrencesByLineNumber,
            Map<Integer, SpriteReferenceReplacement> spriteReplacementsByLineNumber) throws IOException {
        final String processedCssFile = getProcessedCssFile(originalCssFile);
        final BufferedReader originalCssReader = new BufferedReader(
                resourceHandler.getResourceAsReader(originalCssFile));
        messageLog.setCssFile(null);
        messageLog.info(MessageType.CREATING_CSS_STYLE_SHEET, processedCssFile);
        messageLog.info(MessageType.READING_CSS, originalCssFile);
        final BufferedWriter processedCssWriter = new BufferedWriter(
                resourceHandler.getResourceAsWriter(processedCssFile));
        messageLog.info(MessageType.WRITING_CSS, processedCssFile);

        String originalCssLine;
        int originalCssLineNumber = -1;
        int lastReferenceReplacementLine = -1;

        boolean markSpriteImages = parameters.isMarkSpriteImages();

        // Generate UID for sprite file
        try {
            messageLog.setCssFile(originalCssFile);

            originalCssFile = originalCssFile.replace(File.separatorChar, '/');

            while ((originalCssLine = originalCssReader.readLine()) != null) {
                originalCssLineNumber++;
                messageLog.setLine(originalCssLineNumber);

                if (originalCssLine.contains("}")) {
                    lastReferenceReplacementLine = -1;
                }

                final SpriteImageOccurrence spriteImageOccurrence = spriteImageOccurrencesByLineNumber
                        .get(originalCssLineNumber);
                final SpriteReferenceReplacement spriteReferenceReplacement = spriteReplacementsByLineNumber
                        .get(originalCssLineNumber);

                if (spriteImageOccurrence != null) {
                    // Ignore line with directive
                    continue;
                }

                if (spriteReferenceReplacement != null) {
                    final boolean important = spriteReferenceReplacement.spriteReferenceOccurrence.important;
                    lastReferenceReplacementLine = originalCssLineNumber;

                    processedCssWriter.write("  background-image: url('"
                            + getRelativeToReplacementLocation(spriteReferenceReplacement.spriteImage.resolvedPath,
                                    originalCssFile, spriteReferenceReplacement)
                            + "')" + (important ? " !important" : "") + ";"
                            + (markSpriteImages ? " /** sprite:sprite */" : "") + "\n");

                    if (spriteReferenceReplacement.spriteImage.hasReducedForIe6) {
                        processedCssWriter.write("  -background-image: url('"
                                + getRelativeToReplacementLocation(
                                        spriteReferenceReplacement.spriteImage.resolvedPathIe6, originalCssFile,
                                        spriteReferenceReplacement)
                                + "')" + (important ? " !important" : "") + ";"
                                + (markSpriteImages ? " /** sprite:sprite */" : "") + "\n");
                    }

                    processedCssWriter
                            .write("  background-position: " + spriteReferenceReplacement.horizontalPositionString + " "
                                    + spriteReferenceReplacement.verticalPositionString
                                    + (important ? " !important" : "") + ";\n");

                    // If the sprite scale is not 1, write out a background-size directive
                    final float scale = spriteReferenceReplacement.spriteImage.scaleRatio;
                    if (scale != 1.0f) {
                        processedCssWriter.write("  background-size: "
                                + Math.round(spriteReferenceReplacement.spriteImage.spriteWidth / scale) + "px "
                                + Math.round(spriteReferenceReplacement.spriteImage.spriteHeight / scale) + "px;\n");
                    }

                    continue;
                }

                if (lastReferenceReplacementLine >= 0) {
                    for (final String property : OVERRIDING_PROPERTIES) {
                        if (originalCssLine.contains(property)) {
                            messageLog.warning(MessageType.OVERRIDING_PROPERTY_FOUND, property,
                                    lastReferenceReplacementLine);
                        }
                    }
                }

                // Just write the original line
                processedCssWriter.write(originalCssLine + "\n");
            }

            messageLog.setCssFile(null);
        } finally {
            Closeables.close(originalCssReader, true);
            processedCssWriter.close();
        }
    }

    /**
     * Returns the sprite image's imagePath relative to the CSS in which we're making replacements. The imagePath is
     * relative to the CSS which declared the sprite image. As it may happen that the image is referenced in another CSS
     * file, we must make sure the paths are correctly translated.
     *
     * @param imagePath
     *            the image path
     * @param originalCssFile
     *            the original css file
     * @param spriteReferenceReplacement
     *            the sprite reference replacement
     *
     * @return the relative to replacement location
     */
    private String getRelativeToReplacementLocation(String imagePath, String originalCssFile,
            final SpriteReferenceReplacement spriteReferenceReplacement) {
        final String declaringCssPath = spriteReferenceReplacement.spriteImage.spriteImageOccurrence.cssFile
                .replace(File.separatorChar, '/');
        final String declarationReplacementRelativePath = PathUtils
                .getRelativeFilePath(originalCssFile.substring(0, originalCssFile.lastIndexOf('/')),
                        declaringCssPath.substring(0, declaringCssPath.lastIndexOf('/')))
                .replace(File.separatorChar, '/');
        return FileUtils.canonicalize(
                (Strings.isNullOrEmpty(declarationReplacementRelativePath) || originalCssFile.equals(declaringCssPath)
                        ? "" : declarationReplacementRelativePath + '/') + imagePath,
                "/");
    }

    /**
     * Gets the name of the processed CSS file.
     *
     * @param originalCssFile
     *            the original css file
     *
     * @return the processed css file
     */
    String getProcessedCssFile(String originalCssFile) {
        final int lastDotIndex = originalCssFile.lastIndexOf('.');
        final String processedCssFile;
        if (lastDotIndex >= 0) {
            processedCssFile = originalCssFile.substring(0, lastDotIndex) + parameters.getCssFileSuffix()
                    + originalCssFile.substring(lastDotIndex);
        } else {
            processedCssFile = originalCssFile + parameters.getCssFileSuffix();
        }

        if (parameters.hasOutputDir()) {
            return FileUtils.changeRoot(processedCssFile, parameters.getRootDir(), parameters.getOutputDir());
        }
        return processedCssFile;
    }
}
