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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.carrot2.labs.smartsprites.css.CssProperty;
import org.carrot2.labs.smartsprites.css.CssSyntaxUtils;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.labs.smartsprites.resource.ResourceHandler;

/**
 * Methods for collecting SmartSprites directives from CSS files.
 */
public class SpriteDirectiveOccurrenceCollector {

    /** A regular expression for extracting sprite image directives. */
    private static final Pattern SPRITE_IMAGE_DIRECTIVE = Pattern.compile("/\\*+\\s+(sprite:[^*]*)\\*+/");

    /** A regular expression for extracting sprite reference directives. */
    private static final Pattern SPRITE_REFERENCE_DIRECTIVE = Pattern.compile("/\\*+\\s+(sprite-ref:[^*]*)\\*+/");

    /** This builder's message log. */
    private final MessageLog messageLog;

    /** The resource handler. */
    private final ResourceHandler resourceHandler;

    /**
     * Creates a {@link SpriteDirectiveOccurrenceCollector} with the provided parameters and log.
     *
     * @param messageLog
     *            the message log
     * @param resourceHandler
     *            the resource handler
     */
    SpriteDirectiveOccurrenceCollector(MessageLog messageLog, ResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
        this.messageLog = messageLog;
    }

    /**
     * Collects {@link SpriteImageOccurrence}s from a single CSS file.
     *
     * @param cssFile
     *            the css file
     *
     * @return the collection
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Collection<SpriteImageOccurrence> collectSpriteImageOccurrences(String cssFile) throws IOException {
        final Collection<SpriteImageOccurrence> occurrences = new ArrayList<>();
        messageLog.setCssFile(null);
        messageLog.info(MessageType.READING_SPRITE_IMAGE_DIRECTIVES, cssFile);
        messageLog.setCssFile(cssFile);

        int lineNumber = -1;
        String line;

        try (BufferedReader reader = new BufferedReader(resourceHandler.getResourceAsReader(cssFile))) {
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                messageLog.setLine(lineNumber);

                final String spriteImageDirectiveString = extractSpriteImageDirectiveString(line);
                if (spriteImageDirectiveString == null) {
                    continue;
                }

                final SpriteImageDirective directive = SpriteImageDirective.parse(spriteImageDirectiveString,
                        messageLog);
                if (directive == null) {
                    continue;
                }

                occurrences.add(new SpriteImageOccurrence(directive, cssFile, lineNumber));
            }
        }

        return occurrences;
    }

    /**
     * Collects {@link SpriteReferenceOccurrence}s from a single CSS file.
     *
     * @param cssFile
     *            the css file
     * @param spriteImageDirectives
     *            the sprite image directives
     *
     * @return the collection
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Collection<SpriteReferenceOccurrence> collectSpriteReferenceOccurrences(String cssFile,
            Map<String, SpriteImageDirective> spriteImageDirectives) throws IOException {
        final Collection<SpriteReferenceOccurrence> directives = new ArrayList<>();

        messageLog.setCssFile(null);
        messageLog.info(MessageType.READING_SPRITE_REFERENCE_DIRECTIVES, cssFile);
        messageLog.setCssFile(cssFile);

        int lineNumber = -1;
        String line;

        try (BufferedReader reader = new BufferedReader(resourceHandler.getResourceAsReader(cssFile))) {
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                messageLog.setLine(lineNumber);

                final String directiveString = extractSpriteReferenceDirectiveString(line);
                if (directiveString == null) {
                    continue;
                }

                final CssProperty backgroundProperty = extractSpriteReferenceCssProperty(line);
                final String imageUrl = CssSyntaxUtils.unpackUrl(backgroundProperty.value, messageLog);
                if (imageUrl == null) {
                    continue;
                }

                final SpriteReferenceDirective directive = SpriteReferenceDirective.parse(directiveString,
                        spriteImageDirectives, messageLog);
                if (directive == null) {
                    continue;
                }

                directives.add(new SpriteReferenceOccurrence(directive, imageUrl, cssFile, lineNumber,
                        backgroundProperty.important));
            }
        }

        return directives;
    }

    /**
     * Collects {@link SpriteImageOccurrence}s from the provided CSS files.
     *
     * @param filePaths
     *            the file paths
     *
     * @return the multimap
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Multimap<String, SpriteImageOccurrence> collectSpriteImageOccurrences(Collection<String> filePaths)
            throws IOException {
        final Multimap<String, SpriteImageOccurrence> spriteImageOccurrencesByFile = LinkedListMultimap.create();
        for (final String cssFile : filePaths) {
            messageLog.setCssFile(cssFile);

            final Collection<SpriteImageOccurrence> spriteImageOccurrences = collectSpriteImageOccurrences(cssFile);

            spriteImageOccurrencesByFile.putAll(cssFile, spriteImageOccurrences);
        }
        return spriteImageOccurrencesByFile;
    }

    /**
     * Collects {@link SpriteReferenceOccurrence}s from the provided CSS files.
     *
     * @param files
     *            the files
     * @param spriteImageDirectivesBySpriteId
     *            the sprite image directives by sprite id
     *
     * @return the multimap
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Multimap<String, SpriteReferenceOccurrence> collectSpriteReferenceOccurrences(Collection<String> files,
            final Map<String, SpriteImageDirective> spriteImageDirectivesBySpriteId) throws IOException {
        final Multimap<String, SpriteReferenceOccurrence> spriteEntriesByFile = LinkedListMultimap.create();
        for (final String cssFile : files) {
            messageLog.setCssFile(cssFile);

            final Collection<SpriteReferenceOccurrence> spriteReferenceOccurrences = collectSpriteReferenceOccurrences(
                    cssFile, spriteImageDirectivesBySpriteId);

            spriteEntriesByFile.putAll(cssFile, spriteReferenceOccurrences);
        }
        return spriteEntriesByFile;
    }

    /**
     * Groups {@link SpriteImageDirective}s by sprite id.
     *
     * @param spriteImageOccurrencesByFile
     *            the sprite image occurrences by file
     *
     * @return the map
     */
    Map<String, SpriteImageOccurrence> mergeSpriteImageOccurrences(
            final Multimap<String, SpriteImageOccurrence> spriteImageOccurrencesByFile) {
        final Map<String, SpriteImageOccurrence> spriteImageDirectivesBySpriteId = new LinkedHashMap<>();
        for (final Map.Entry<String, SpriteImageOccurrence> entry : spriteImageOccurrencesByFile.entries()) {
            final String cssFile = entry.getKey();
            final SpriteImageOccurrence spriteImageOccurrence = entry.getValue();

            messageLog.setCssFile(cssFile);

            // Add to the global map, checking for duplicates
            if (spriteImageDirectivesBySpriteId.containsKey(spriteImageOccurrence.spriteImageDirective.spriteId)) {
                messageLog.warning(MessageType.IGNORING_SPRITE_IMAGE_REDEFINITION);
            } else {
                spriteImageDirectivesBySpriteId.put(spriteImageOccurrence.spriteImageDirective.spriteId,
                        spriteImageOccurrence);
            }
        }
        return spriteImageDirectivesBySpriteId;
    }

    /**
     * Groups {@link SpriteReferenceOccurrence}s by sprite id.
     *
     * @param spriteEntriesByFile
     *            the sprite entries by file
     *
     * @return the multimap
     */
    static Multimap<String, SpriteReferenceOccurrence> mergeSpriteReferenceOccurrences(
            final Multimap<String, SpriteReferenceOccurrence> spriteEntriesByFile) {
        final Multimap<String, SpriteReferenceOccurrence> spriteReferenceOccurrencesBySpriteId = LinkedListMultimap
                .create();
        for (final SpriteReferenceOccurrence spriteReferenceOccurrence : spriteEntriesByFile.values()) {
            spriteReferenceOccurrencesBySpriteId.put(spriteReferenceOccurrence.spriteReferenceDirective.spriteRef,
                    spriteReferenceOccurrence);
        }
        return spriteReferenceOccurrencesBySpriteId;
    }

    /**
     * Extract the sprite image directive string to be parsed.
     *
     * @param cssLine
     *            the css line
     *
     * @return the string
     */
    static String extractSpriteImageDirectiveString(String cssLine) {
        final Matcher matcher = SPRITE_IMAGE_DIRECTIVE.matcher(cssLine);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * Extract the sprite reference directive string to be parsed.
     *
     * @param css
     *            the css
     *
     * @return the string
     */
    static String extractSpriteReferenceDirectiveString(String css) {
        final Matcher matcher = SPRITE_REFERENCE_DIRECTIVE.matcher(css);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * Extract the url to the image to be added to a sprite.
     *
     * @param css
     *            the css
     *
     * @return the css property
     */
    CssProperty extractSpriteReferenceCssProperty(String css) {
        final Matcher matcher = SPRITE_REFERENCE_DIRECTIVE.matcher(css);

        // Remove the directive
        final String noDirective = matcher.replaceAll("").trim();

        final Collection<CssProperty> rules = CssSyntaxUtils.extractProperties(noDirective);
        if (rules.isEmpty()) {
            messageLog.warning(MessageType.NO_BACKGROUND_IMAGE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE, css);
            return null;
        }

        if (rules.size() > 1) {
            messageLog.warning(MessageType.MORE_THAN_ONE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE, css);
            return null;
        }

        final CssProperty backgroundImageRule = rules.iterator().next();
        if (!backgroundImageRule.rule.equals("background-image")) {
            messageLog.warning(MessageType.NO_BACKGROUND_IMAGE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE, css);
            return null;
        }

        return backgroundImageRule;
    }
}
