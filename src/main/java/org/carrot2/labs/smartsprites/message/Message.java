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
package org.carrot2.labs.smartsprites.message;

import java.io.Serializable;
import java.util.Comparator;

import org.carrot2.labs.smartsprites.SpriteImageDirective;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageFormat;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageLayout;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteUidType;
import org.carrot2.labs.smartsprites.SpriteLayoutProperties.SpriteAlignment;
import org.carrot2.labs.smartsprites.SpriteReferenceDirective;

/**
 * Represents a processing message, can be an information message or a warning.
 */
public class Message implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * The importance of the message.
     */
    public enum MessageLevel {
        /**
         * Information message, can be ignored.
         */
        INFO(1),

        /**
         * Notice messages related to deprecated features.
         */
        DEPRECATION(2),

        /**
         * Warning messages, ignoring can lead to the converted designs looking broken.
         */
        WARN(4),

        /**
         * Error messages, SmartSprites cannot perform processing.
         */
        ERROR(5),

        /**
         * Status messages displayed at the end of processing.
         */
        STATUS(6);

        /** Numeric level for comparisons. */
        private final int level;

        /**
         * Instantiates a new message level.
         *
         * @param level
         *            the level
         */
        MessageLevel(int level) {
            this.level = level;
        }

        /** The Constant COMPARATOR. */
        public static final Comparator<MessageLevel> COMPARATOR = new Comparator<MessageLevel>() {
            @Override
            public int compare(MessageLevel levelA, MessageLevel levelB) {
                return levelA.level - levelB.level;
            }
        };

    }

    /**
     * Defines all the possible information and warning messages.
     */
    public enum MessageType {

        /** The cannot determine image format. */
        CANNOT_DETERMINE_IMAGE_FORMAT("Cannot determine image format from file name: %s"),

        /** The cannot not load image. */
        CANNOT_NOT_LOAD_IMAGE("Cannot load image: %s due to: %s"),

        /** The cannot parse margin value. */
        CANNOT_PARSE_MARGIN_VALUE(
                "Cannot parse margin value: %s. Only 'px' units are supported."),

        /** The cannot write sprite image. */
        CANNOT_WRITE_SPRITE_IMAGE("Cannot write sprite image: %s due to %s"),

        /** The cannot create directories. */
        CANNOT_CREATE_DIRECTORIES("Cannot create directories: %s"),

        /** The creating css style sheet. */
        CREATING_CSS_STYLE_SHEET("Creating CSS style sheet: %s"),

        /** The writing css. */
        WRITING_CSS("Writing CSS to %s"),

        /** The writing sprite image. */
        WRITING_SPRITE_IMAGE("Writing sprite image of size %s x %s for sprite '%s' to %s"),

        /** The ignoring sprite image redefinition. */
        IGNORING_SPRITE_IMAGE_REDEFINITION("Ignoring sprite image redefinition"),

        /** The malformed css rule. */
        MALFORMED_CSS_RULE("Malformed CSS rule: %s"),

        /** The malformed color. */
        MALFORMED_COLOR("Malformed color: %s"),

        /** The malformed url. */
        MALFORMED_URL("Malformed URL: %s"),

        /** The malformed sprite image path. */
        MALFORMED_SPRITE_IMAGE_PATH("Malformed sprite-image path: %s"),

        /** The unsupported variable in sprite image path. */
        UNSUPPORTED_VARIABLE_IN_SPRITE_IMAGE_PATH(
                "Unsupported variable in sprite-image path: %s"),

        /** The more than one rule next to sprite reference directive. */
        MORE_THAN_ONE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE(
                "Found more than one CSS rule next to sprite reference comment: %s"),

        /** The no background image rule next to sprite reference directive. */
        NO_BACKGROUND_IMAGE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE(
                "No 'background-image' CSS rule next to sprite reference comment: %s"),

        /** The either root dir or css files is required. */
        EITHER_ROOT_DIR_OR_CSS_FILES_IS_REQUIRED(
                "Either root directory or non-empty list of individual CSS files is required"),

        /** The root dir and css files cannot be both specified unless with output dir. */
        ROOT_DIR_AND_CSS_FILES_CANNOT_BE_BOTH_SPECIFIED_UNLESS_WITH_OUTPUT_DIR(
                "Root directory and individual CSS files cannot be both specified, unless output dir is also specified"),

        /** The root dir is required for output dir. */
        ROOT_DIR_IS_REQUIRED_FOR_OUTPUT_DIR(
                "If output directory is specified, root directory must also be provided"),

        /** The root dir does not exist or is not directory. */
        ROOT_DIR_DOES_NOT_EXIST_OR_IS_NOT_DIRECTORY(
                "Root directory must exist and be a directory: %s"),

        /** The css file does not exist. */
        CSS_FILE_DOES_NOT_EXIST("Ignoring CSS file %s, it does not exist"),

        /** The css path is not a file. */
        CSS_PATH_IS_NOT_A_FILE("Ignoring CSS path %s, it is not a file"),

        /** The output dir is not directory. */
        OUTPUT_DIR_IS_NOT_DIRECTORY("Output directory must be a directory: %s"),

        /** The document root dir does not exist or is not directory. */
        DOCUMENT_ROOT_DIR_DOES_NOT_EXIST_OR_IS_NOT_DIRECTORY(
                "Document root directory must exist and be a directory: %s"),

        /** The ignoring css file outside of root dir. */
        IGNORING_CSS_FILE_OUTSIDE_OF_ROOT_DIR(
                "Ignoring a CSS file outside of root directory: %s"),

        /** The css file suffix is required if no output dir. */
        CSS_FILE_SUFFIX_IS_REQUIRED_IF_NO_OUTPUT_DIR(
                "A non-empty CSS file suffix is required when no output directory is specified"),

        /** The only left or right alignment allowed. */
        ONLY_LEFT_OR_RIGHT_ALIGNMENT_ALLOWED(
                "Only 'left' or 'right' alignment allowed on vertical sprites, found: %s. Using 'left'."),

        /** The only top or bottom alignment allowed. */
        ONLY_TOP_OR_BOTTOM_ALIGNMENT_ALLOWED(
                "Only 'top' or 'bottom' alignment allowed on horizontal sprites, found: %s. Using 'top'."),

        /** The reading sprite image directives. */
        READING_SPRITE_IMAGE_DIRECTIVES("Reading sprite image directives from %s"),

        /** The reading sprite reference directives. */
        READING_SPRITE_REFERENCE_DIRECTIVES("Reading sprite reference directives from %s"),

        /** The reading css. */
        READING_CSS("Reading CSS from %s"),

        /** The reading image. */
        READING_IMAGE("Reading image from %s"),

        /** The referenced sprite not found. */
        REFERENCED_SPRITE_NOT_FOUND("Referenced sprite: %s not found"),

        /** The sprite id not found. */
        SPRITE_ID_NOT_FOUND("'" + SpriteImageDirective.PROPERTY_SPRITE_ID + "' rule is required"),

        /** The sprite image url not found. */
        SPRITE_IMAGE_URL_NOT_FOUND("'" + SpriteImageDirective.PROPERTY_SPRITE_IMAGE_URL + "' rule is required"),

        /** The sprite ref not found. */
        SPRITE_REF_NOT_FOUND("'" + SpriteReferenceDirective.PROPERTY_SPRITE_REF + "' rule is required"),

        /** The unsupported alignment. */
        UNSUPPORTED_ALIGNMENT(
                "Unsupported alignment: %s. Supported alignments are: " + SpriteAlignment.valuesAsString() + "."),

        /** The unsupported individual image format. */
        UNSUPPORTED_INDIVIDUAL_IMAGE_FORMAT("Unsupported format of image loaded from: %s"),

        /** The unsupported sprite image format. */
        UNSUPPORTED_SPRITE_IMAGE_FORMAT(
                "Format of image: %s is not supported. Supported formats are: " + SpriteImageFormat.valuesAsString()
                        + "."),

        /** The unsupported layout. */
        UNSUPPORTED_LAYOUT(
                "Unsupported layout: %s. Supported layouts are: " + SpriteImageLayout.valuesAsString() + "."),

        /** The unsupported uid type. */
        UNSUPPORTED_UID_TYPE(
                "Unsupported uid type: %s. Supported uid types are: " + SpriteUidType.valuesAsString() + "."),

        /** The jpg does not support indexed color. */
        JPG_DOES_NOT_SUPPORT_INDEXED_COLOR("JPG format does not support indexed color"),

        /** The too many colors for indexed color. */
        TOO_MANY_COLORS_FOR_INDEXED_COLOR(
                "Sprite '%s' requires %d colors, but the maximum for indexed color mode is %d. Image quality will be degraded."),

        /** The alpha channel loss in indexed color. */
        ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR(
                "Alpha channel of sprite '%s' cannot be encoded in indexed color mode. Image quality will be degraded."),

        /** The using white matte color as default. */
        USING_WHITE_MATTE_COLOR_AS_DEFAULT(
                "Defaulting to white matte color to render partial transparencies of sprite '%s'"),

        /** The ignoring matte color no partial transparency. */
        IGNORING_MATTE_COLOR_NO_PARTIAL_TRANSPARENCY(
                "Ignoring sprite-mate-color on sprite '%s' because the sprite image does not contain partially transparent areas"),

        /** The ignoring matte color no support. */
        IGNORING_MATTE_COLOR_NO_SUPPORT(
                "Ignoring sprite-mate-color on sprite '%s' because its output format does not require matting or does not support transparency"),

        /** The ignoring negative margin value. */
        IGNORING_NEGATIVE_MARGIN_VALUE("Values of %s must not be negative, using 0"),

        /** The processing completed. */
        PROCESSING_COMPLETED("SmartSprites processing completed in %d ms"),

        /** The processing completed with warnings. */
        PROCESSING_COMPLETED_WITH_WARNINGS(
                "SmartSprites processing completed in %d ms with %d warning(s)"),

        /** The unsupported properties found. */
        UNSUPPORTED_PROPERTIES_FOUND("Unsupported properties found: %s"),

        /** The overriding property found. */
        OVERRIDING_PROPERTY_FOUND(
                "Found a '%s' property that overrides the generated one. Move it before the sprite reference directive on line %d."),

        /** The absolute path and no document root. */
        ABSOLUTE_PATH_AND_NO_DOCUMENT_ROOT(
                "Found an absolute image path '%s' and no document.root.dir.path was defined. Taking relative to the CSS file."),

        /** The deprecated sprite image uid. */
        DEPRECATED_SPRITE_IMAGE_UID(
                "The sprite-image-uid property is deprecated and will be removed in version 0.4.0. Please insert the ${%s} variable into the sprite-image property instead."),

        /** The fractional scale value. */
        FRACTIONAL_SCALE_VALUE(
                "The sprite-scale value applied to '%s' results in a scaled sprite with fractional dimensions (%fpx %fpx)."),

        /** The image fractional scale value. */
        IMAGE_FRACTIONAL_SCALE_VALUE(
                "The sprite-scale value applied to '%s' results in a scaled image with fractional dimensions (%fpx %fpx)."),

        /** The generic. */
        GENERIC("%s");

        /**
         * Human readable text of the message.
         */
        private final String text;

        /**
         * Instantiates a new message type.
         *
         * @param text
         *            the text
         */
        MessageType(String text) {
            this.text = text;
        }

        /**
         * Returns a human readable version of this message.
         *
         * @return the text
         */
        public String getText() {
            return text;
        }
    }

    /**
     * Importance of this message.
     */
    public final MessageLevel level;

    /**
     * Semantics of the message.
     */
    public final MessageType type;

    /**
     * CSS file to which this message refers or <code>null</code>.
     */
    public final String cssPath;

    /**
     * Line number to which this message refers, meaningful only if {@link #cssPath} is not <code>null</code>.
     */
    public final int line;

    /**
     * Additional arguments to this message, used to format the human-readable string.
     */
    public final Object[] arguments;

    /**
     * Creates a new message, see field descriptions for details.
     *
     * @param level
     *            the level
     * @param type
     *            the type
     * @param cssPath
     *            the css path
     * @param line
     *            the line
     * @param arguments
     *            the arguments
     */
    public Message(MessageLevel level, MessageType type, String cssPath, int line, Object... arguments) {
        this.level = level;
        this.type = type;
        this.cssPath = cssPath;
        this.line = line;
        this.arguments = new Object[arguments.length];
        System.arraycopy(arguments, 0, this.arguments, 0, arguments.length);
    }

    /**
     * Warn.
     *
     * @param type
     *            the type
     * @param arguments
     *            the arguments
     *
     * @return the message
     */
    public static Message warn(MessageType type, Object... arguments) {
        return new Message(MessageLevel.WARN, type, null, 0, arguments);
    }

    /**
     * Error.
     *
     * @param type
     *            the type
     * @param arguments
     *            the arguments
     *
     * @return the message
     */
    public static Message error(MessageType type, Object... arguments) {
        return new Message(MessageLevel.ERROR, type, null, 0, arguments);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(level);
        stringBuilder.append(": ");
        stringBuilder.append(getFormattedMessage());

        if (cssPath != null) {
            stringBuilder.append(" (");
            stringBuilder.append(cssPath);
            stringBuilder.append(", line: ");
            stringBuilder.append(line + 1);
            stringBuilder.append(")");
        }

        return stringBuilder.toString();
    }

    /**
     * Gets the formatted message.
     *
     * @return the formatted message
     */
    public String getFormattedMessage() {
        return String.format(type.getText(), arguments);
    }
}
