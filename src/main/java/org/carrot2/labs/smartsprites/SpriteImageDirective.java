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

import java.awt.Color;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.carrot2.labs.smartsprites.css.CssProperty;
import org.carrot2.labs.smartsprites.css.CssSyntaxUtils;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.util.CollectionUtils;
import org.carrot2.util.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Represents a directive that declares an individual sprite image.
 */
public class SpriteImageDirective
{
    public static final String PROPERTY_SPRITE_ID = "sprite";
    public static final String PROPERTY_SPRITE_IMAGE_LAYOUT = "sprite-layout";
    public static final String PROPERTY_SPRITE_IMAGE_URL = "sprite-image";
    public static final String PROPERTY_SPRITE_IMAGE_UID_SUFFIX = "sprite-image-uid";
    public static final String PROPERTY_SPRITE_MATTE_COLOR = "sprite-matte-color";
    public static final String PROPERTY_SPRITE_IE6_MODE = "sprite-ie6-mode";
    public static final String PROPERTY_SPRITE_SCALE = "sprite-scale";

    /** A set of allowed properties */
    private static final Set<String> ALLOWED_PROPERTIES = ImmutableSet.of(
        PROPERTY_SPRITE_ID, PROPERTY_SPRITE_IMAGE_LAYOUT, PROPERTY_SPRITE_IMAGE_URL,
        PROPERTY_SPRITE_MATTE_COLOR, PROPERTY_SPRITE_IE6_MODE,
        PROPERTY_SPRITE_SCALE, PROPERTY_SPRITE_IMAGE_UID_SUFFIX);

    /**
     * Defines the layout of this sprite.
     */
    public enum SpriteImageLayout
    {
        /**
         * Vertical layout, images stacked on each other.
         */
        VERTICAL,

        /**
         * Horizontal layout, images next to each other.
         */
        HORIZONTAL;

        private String value;

        private SpriteImageLayout()
        {
            this.value = name().toLowerCase();
        }

        @Override
        public String toString()
        {
            return value;
        }

        public static String valuesAsString()
        {
            final String list = Lists.newArrayList(values()).toString();
            return list.substring(1, list.length() - 1);
        }
    }

    /**
     * Defines the UID Generation Mode of this sprite.
     */
    public enum SpriteUidType
    {
        /**
         * No UID extension.
         */
        NONE,

        /**
         * Append current timestamp as long.
         */
        DATE,

        /**
         * Append sha512 of the sprites file.
         */
        SHA512;

        private String value;

        public final Pattern pattern;

        private SpriteUidType()
        {
            this.value = name().toLowerCase();
            this.pattern = Pattern.compile("${" + value + "}", Pattern.LITERAL);
        }

        @Override
        public String toString()
        {
            return value;
        }

        public static String valuesAsString()
        {
            final String list = Lists.newArrayList(values()).toString();
            return list.substring(1, list.length() - 1);
        }
    }

    /**
     * Defines supported image file formats.
     */
    public enum SpriteImageFormat
    {
        PNG, GIF, JPG;

        private String value;

        private SpriteImageFormat()
        {
            this.value = name().toLowerCase();
        }

        @Override
        public String toString()
        {
            return value;
        }

        public static SpriteImageFormat getValue(String value)
        {
            return valueOf(value.toUpperCase());
        }

        public static String valuesAsString()
        {
            final String list = Lists.newArrayList(values()).toString();
            return list.substring(1, list.length() - 1);
        }
    }

    /**
     * Defines supported IE6 support options.
     */
    public enum Ie6Mode
    {
        /** No IE6-friendly image will be created for this sprite, even if needed */
        NONE,

        /** IE6-friendly image will be generated for this sprite if needed */
        AUTO;

        private String value;

        private Ie6Mode()
        {
            this.value = name().toLowerCase();
        }

        @Override
        public String toString()
        {
            return value;
        }

        public static String valuesAsString()
        {
            final String list = Lists.newArrayList(values()).toString();
            return list.substring(1, list.length() - 1);
        }
    }

    /**
     * Unique identified of this sprite.
     */
    public final String spriteId;

    /**
     * CSS file relative path for this sprite image.
     */
    public final String imagePath;

    /**
     * Non-file-name extension after the sprite image path to force a cache update on
     * change, prefixed by '?'.
     */
    public final SpriteUidType uidType;

    /**
     * Layout of this sprite image.
     */
    public final SpriteImageLayout layout;

    /**
     * Format of this sprite image.
     */
    public final SpriteImageFormat format;

    /**
     * How IE6 sprites should be handled.
     */
    public final Ie6Mode ie6Mode;

    /**
     * Matte color to be used when reducing true alpha channel.
     */
    public final Color matteColor;

    /**
     * Scaling ratio to apply to background; default is 1.
     */
    public final float scaleRatio;

    /**
     * Sprite layout properties defined at the sprite image directive level. The defaults
     * provided here can be overridden at the sprite reference directive level.
     */
    public final SpriteLayoutProperties spriteLayoutProperties;

    /**
     * Pattern for a simple syntactic check of the image path.
     */
    private static final Pattern IMAGE_PATH_PATTERN = Pattern
        .compile("([^${}]*|\\$\\{[^}]*\\})*");

    /**
     * Pattern for extracting variables from image path.
     */
    private static final Pattern IMAGE_PATH_VARIABLE_PATTERN = Pattern
        .compile("\\$\\{([a-z]*)\\}");

    /**
     * Variable names allowed in image path.
     */
    private static final Set<String> ALLOWED_VARIABLES = ImmutableSet.of(PROPERTY_SPRITE_ID,
        SpriteUidType.DATE.toString(), SpriteUidType.SHA512.toString());

    public SpriteImageDirective(String id, String imageUrl, SpriteImageLayout layout,
        SpriteImageFormat format, Ie6Mode ie6Mode, Color matteColor, SpriteUidType uidType, float scale)
    {
        this(id, imageUrl, layout, format, ie6Mode, matteColor, uidType, scale,
            new SpriteLayoutProperties(layout));
    }

    public SpriteImageDirective(String id, String imageUrl, SpriteImageLayout layout,
        SpriteImageFormat format, Ie6Mode ie6Mode, Color matteColor,
        SpriteUidType uidType, float scale, SpriteLayoutProperties spriteLayoutProperties)
    {
        this.spriteId = id;
        this.imagePath = imageUrl;
        this.layout = layout;
        this.format = format;
        this.ie6Mode = ie6Mode;
        this.matteColor = matteColor;
        this.uidType = uidType;
        this.scaleRatio = scale;
        this.spriteLayoutProperties = spriteLayoutProperties;
    }

    /**
     * Parses a string into a {@link SpriteImageDirective}, logging messages to the
     * provided {@link MessageLog}s.
     */
    public static SpriteImageDirective parse(String directiveString,
        MessageLog messageCollector)
    {
        final Map<String, CssProperty> rules = CssSyntaxUtils
            .propertiesAsMap(CssSyntaxUtils.extractRules(directiveString,
                messageCollector));

        final Set<String> properties = Sets.newLinkedHashSet(rules.keySet());
        properties.removeAll(ALLOWED_PROPERTIES);
        properties.removeAll(SpriteLayoutProperties.ALLOWED_PROPERTIES);
        if (!properties.isEmpty())
        {
            messageCollector.warning(MessageType.UNSUPPORTED_PROPERTIES_FOUND,
                CollectionUtils.toString(properties));
        }

        if (!CssSyntaxUtils.hasNonBlankValue(rules, PROPERTY_SPRITE_ID))
        {
            messageCollector.warning(MessageType.SPRITE_ID_NOT_FOUND);
            return null;
        }

        if (!CssSyntaxUtils.hasNonBlankValue(rules, PROPERTY_SPRITE_IMAGE_URL))
        {
            messageCollector.warning(MessageType.SPRITE_IMAGE_URL_NOT_FOUND);
            return null;
        }

        final String id = rules.get(PROPERTY_SPRITE_ID).value;

        final SpriteUidType uidGenerator = valueOf(
            CssSyntaxUtils.getValue(rules, PROPERTY_SPRITE_IMAGE_UID_SUFFIX),
            SpriteUidType.class, SpriteUidType.NONE, messageCollector,
            MessageType.UNSUPPORTED_UID_TYPE);
        if (uidGenerator != SpriteUidType.NONE)
        {
            messageCollector.deprecation(
                MessageType.DEPRECATED_SPRITE_IMAGE_UID,
                uidGenerator.toString());
        }

        // Image path. If the path does not match a regular expression, issue a warning.
        final String imagePath = CssSyntaxUtils.unpackUrl(rules
            .get(PROPERTY_SPRITE_IMAGE_URL).value);
        if (IMAGE_PATH_PATTERN.matcher(imagePath).matches())
        {
            // Check variable names
            final Matcher variableMatcher = IMAGE_PATH_VARIABLE_PATTERN
                .matcher(imagePath);
            while (variableMatcher.find())
            {
                if (variableMatcher.groupCount() == 1
                    && !ALLOWED_VARIABLES.contains(variableMatcher.group(1)))
                {
                    messageCollector.warning(
                        MessageType.UNSUPPORTED_VARIABLE_IN_SPRITE_IMAGE_PATH,
                        variableMatcher.group(1));
                }
            }
        }
        else
        {
            // Just issue a warning
            messageCollector.warning(MessageType.MALFORMED_SPRITE_IMAGE_PATH, imagePath);
        }

        // Layout is optional
        final SpriteImageLayout layout = valueOf(
            CssSyntaxUtils.getValue(rules, PROPERTY_SPRITE_IMAGE_LAYOUT),
            SpriteImageLayout.class, SpriteImageLayout.VERTICAL, messageCollector,
            MessageType.UNSUPPORTED_LAYOUT);

        // Infer format from image path
        SpriteImageFormat format;
        final int lastDotIndex = imagePath.lastIndexOf('.');
        if ((lastDotIndex < 0) || (lastDotIndex == imagePath.length() - 1))
        {
            messageCollector
                .warning(MessageType.CANNOT_DETERMINE_IMAGE_FORMAT, imagePath);
            format = SpriteImageFormat.PNG;
        }
        else
        {
            final int questionMarkIndex = imagePath.indexOf('?', lastDotIndex);
            final String formatValue = questionMarkIndex >= 0 ? imagePath.substring(
                lastDotIndex + 1, questionMarkIndex) : imagePath
                .substring(lastDotIndex + 1);
            try
            {
                format = SpriteImageFormat.getValue(formatValue);
            }
            catch (final IllegalArgumentException e)
            {
                messageCollector.warning(MessageType.UNSUPPORTED_SPRITE_IMAGE_FORMAT,
                    formatValue);
                format = SpriteImageFormat.PNG;
            }
        }

        // Layout is optional
        final String ie6ModeString = CssSyntaxUtils.getValue(rules,
            PROPERTY_SPRITE_IE6_MODE);
        final Ie6Mode ie6Mode = valueOf(ie6ModeString, Ie6Mode.class, Ie6Mode.AUTO,
            messageCollector, MessageType.UNSUPPORTED_IE6_MODE);
        if (StringUtils.isNotBlank(ie6ModeString) && format != SpriteImageFormat.PNG)
        {
            messageCollector.notice(MessageType.IGNORING_IE6_MODE, format.name());
        }

        // Matte color
        final Color matteColor;
        if (CssSyntaxUtils.hasNonBlankValue(rules, PROPERTY_SPRITE_MATTE_COLOR))
        {
            matteColor = CssSyntaxUtils.parseColor(
                rules.get(PROPERTY_SPRITE_MATTE_COLOR).value, messageCollector, null);
        }
        else
        {
            matteColor = null;
        }

        final float scale;
        if (CssSyntaxUtils.hasNonBlankValue(rules, PROPERTY_SPRITE_SCALE))
        {
            scale = Float.parseFloat(rules.get(PROPERTY_SPRITE_SCALE).value);
        }
        else
        {
            scale = 1.0f;
        }

        return new SpriteImageDirective(id, imagePath, layout, format, ie6Mode,
            matteColor, uidGenerator, scale, SpriteLayoutProperties.parse(directiveString,
                layout, messageCollector));
    }

    private static <T extends Enum<T>> T valueOf(String stringValue, Class<T> enumClass,
        T defaultValue, MessageLog messageCollector, MessageType messageType)
    {
        if (StringUtils.isNotBlank(stringValue))
        {
            try
            {
                return Enum.valueOf(enumClass, stringValue.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                messageCollector.warning(messageType, stringValue);
                return defaultValue;
            }
        }
        else
        {
            return defaultValue;
        }
    }
}
