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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.Map;
import java.util.Set;

import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageLayout;
import org.carrot2.labs.smartsprites.css.CssProperty;
import org.carrot2.labs.smartsprites.css.CssSyntaxUtils;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;

/**
 * Represents common sprite layout properties that can be used both in {@link SpriteImageDirective} and
 * {@link SpriteReferenceDirective}.
 */
public class SpriteLayoutProperties {

    /** The Constant PROPERTY_SPRITE_ALIGNMENT. */
    public static final String PROPERTY_SPRITE_ALIGNMENT = "sprite-alignment";

    /** The Constant PROPERTY_SPRITE_MARGIN_BOTTOM. */
    public static final String PROPERTY_SPRITE_MARGIN_BOTTOM = "sprite-margin-bottom";

    /** The Constant PROPERTY_SPRITE_MARGIN_TOP. */
    public static final String PROPERTY_SPRITE_MARGIN_TOP = "sprite-margin-top";

    /** The Constant PROPERTY_SPRITE_MARGIN_RIGHT. */
    public static final String PROPERTY_SPRITE_MARGIN_RIGHT = "sprite-margin-right";

    /** The Constant PROPERTY_SPRITE_MARGIN_LEFT. */
    public static final String PROPERTY_SPRITE_MARGIN_LEFT = "sprite-margin-left";

    /** Allowed properties of this directive. */
    static final Set<String> ALLOWED_PROPERTIES = ImmutableSet.of(PROPERTY_SPRITE_ALIGNMENT,
            PROPERTY_SPRITE_MARGIN_LEFT, PROPERTY_SPRITE_MARGIN_RIGHT, PROPERTY_SPRITE_MARGIN_TOP,
            PROPERTY_SPRITE_MARGIN_BOTTOM);

    /**
     * Alignment of the individual image within the sprite image.
     */
    public enum SpriteAlignment {
        /**
         * To the left edge of a vertical sprite.
         */
        LEFT,

        /**
         * To the right edge of a vertical sprite.
         */
        RIGHT,

        /**
         * To the top edge of a horizontal sprite.
         */
        TOP,

        /**
         * To the bottom edge of a horizontal sprite.
         */
        BOTTOM,

        /**
         * Repeated across the full width/ height of the sprite image.
         */
        REPEAT,

        /** To the center of a vertical or horizontal sprite. */
        CENTER;

        /** The value. */
        private String value;

        /**
         * Instantiates a new sprite alignment.
         */
        private SpriteAlignment() {
            this.value = name().toLowerCase();
        }

        @Override
        public String toString() {
            return value;
        }

        /**
         * Gets the value.
         *
         * @param value
         *            the value
         *
         * @return the value
         */
        public static SpriteAlignment getValue(String value) {
            return valueOf(value.toUpperCase());
        }

        /**
         * Values as string.
         *
         * @return the string
         */
        public static String valuesAsString() {
            final String list = Lists.newArrayList(values()).toString();
            return list.substring(1, list.length() - 1);
        }
    }

    /** Alignment of this individual image. */
    public final SpriteAlignment alignment;

    /** Left margin of the individual image. */
    public final int marginLeft;

    /** Right margin of the individual image. */
    public final int marginRight;

    /** Top margin of the individual image. */
    public final int marginTop;

    /** Bottom margin of the individual image. */
    public final int marginBottom;

    /**
     * Instantiates a new sprite layout properties.
     *
     * @param alignment
     *            the alignment
     * @param marginLeft
     *            the margin left
     * @param marginRight
     *            the margin right
     * @param marginTop
     *            the margin top
     * @param marginBottom
     *            the margin bottom
     */
    public SpriteLayoutProperties(SpriteAlignment alignment, int marginLeft, int marginRight, int marginTop,
            int marginBottom) {
        this.alignment = alignment;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    /**
     * Creates an instance with default values.
     *
     * @param layout
     *            the layout
     */
    SpriteLayoutProperties(SpriteImageLayout layout) {
        this(getDefaultAlignment(layout), 0, 0, 0, 0);
    }

    /**
     * Parses a {@link SpriteLayoutProperties} from the provided {@link String} logging messages to the provided
     * {@link MessageLog}.
     *
     * @param directiveString
     *            the directive string
     * @param spriteImageLayout
     *            the sprite image layout
     * @param messageCollector
     *            the message collector
     *
     * @return the sprite layout properties
     */
    public static SpriteLayoutProperties parse(String directiveString, SpriteImageLayout spriteImageLayout,
            MessageLog messageCollector) {
        return parse(directiveString, spriteImageLayout, new SpriteLayoutProperties(spriteImageLayout),
                messageCollector);
    }

    /**
     * Parses a {@link SpriteLayoutProperties} from the provided {@link String}, using the provided defaults and logging
     * messages to the provided {@link MessageLog}.
     *
     * @param directiveString
     *            the directive string
     * @param spriteImageLayout
     *            the sprite image layout
     * @param defaults
     *            the defaults
     * @param messageCollector
     *            the message collector
     *
     * @return the sprite layout properties
     */
    public static SpriteLayoutProperties parse(String directiveString, SpriteImageLayout spriteImageLayout,
            SpriteLayoutProperties defaults, MessageLog messageCollector) {
        final Map<String, CssProperty> rules = CssSyntaxUtils
                .propertiesAsMap(CssSyntaxUtils.extractRules(directiveString, messageCollector));

        // We don't check for allowed properties here. The check, including
        // sprite layout properties will be done when parsing the directive
        // that embeds sprite layout properties.

        // Alignment is optional
        SpriteAlignment alignment;
        if (CssSyntaxUtils.hasNonBlankValue(rules, PROPERTY_SPRITE_ALIGNMENT)) {
            final String alignmentValue = rules.get(PROPERTY_SPRITE_ALIGNMENT).value;
            try {
                alignment = correctAlignment(spriteImageLayout, SpriteAlignment.getValue(alignmentValue),
                        messageCollector);
            } catch (final IllegalArgumentException e) {
                messageCollector.warning(MessageType.UNSUPPORTED_ALIGNMENT, alignmentValue);
                alignment = getDefaultAlignment(spriteImageLayout);
            }
        } else {
            alignment = defaults.alignment;
        }

        // Parse margins
        final int marginLeft = getMargin(PROPERTY_SPRITE_MARGIN_LEFT, rules, defaults.marginLeft, messageCollector);
        final int marginRight = getMargin(PROPERTY_SPRITE_MARGIN_RIGHT, rules, defaults.marginRight, messageCollector);
        final int marginTop = getMargin(PROPERTY_SPRITE_MARGIN_TOP, rules, defaults.marginTop, messageCollector);
        final int marginBottom = getMargin(PROPERTY_SPRITE_MARGIN_BOTTOM, rules, defaults.marginBottom,
                messageCollector);

        return new SpriteLayoutProperties(alignment, marginLeft, marginRight, marginTop, marginBottom);
    }

    /**
     * Corrects sprite alignment if necessary based on the layout of the enclosing sprite image.
     *
     * @param spriteImageLayout
     *            the sprite image layout
     * @param alignment
     *            the alignment
     * @param messageCollector
     *            the message collector
     *
     * @return the sprite alignment
     */
    private static SpriteAlignment correctAlignment(SpriteImageLayout spriteImageLayout, SpriteAlignment alignment,
            MessageLog messageCollector) {
        if (spriteImageLayout.equals(SpriteImageLayout.HORIZONTAL)) {
            if (alignment.equals(SpriteAlignment.LEFT) || alignment.equals(SpriteAlignment.RIGHT)) {
                messageCollector.warning(MessageType.ONLY_TOP_OR_BOTTOM_ALIGNMENT_ALLOWED, alignment.value);
                return SpriteAlignment.TOP;
            }
        } else {
            if (alignment.equals(SpriteAlignment.TOP) || alignment.equals(SpriteAlignment.BOTTOM)) {
                messageCollector.warning(MessageType.ONLY_LEFT_OR_RIGHT_ALIGNMENT_ALLOWED, alignment.value);
                return SpriteAlignment.LEFT;
            }
        }

        return alignment;
    }

    /**
     * Returns default alignment for given sprite image directive.
     *
     * @param spriteImageLayout
     *            the sprite image layout
     *
     * @return the default alignment
     */
    private static SpriteAlignment getDefaultAlignment(SpriteImageLayout spriteImageLayout) {
        if (spriteImageLayout.equals(SpriteImageLayout.HORIZONTAL)) {
            return SpriteAlignment.TOP;
        } else {
            return SpriteAlignment.LEFT;
        }
    }

    /**
     * Parses margin value.
     *
     * @param marginRule
     *            the margin rule
     * @param rules
     *            the rules
     * @param defaultMargin
     *            the default margin
     * @param messageLog
     *            the message log
     *
     * @return the margin
     */
    private static int getMargin(String marginRule, Map<String, CssProperty> rules, int defaultMargin,
            MessageLog messageLog) {
        if (CssSyntaxUtils.hasNonBlankValue(rules, marginRule)) {
            final String rawMarginValue = rules.get(marginRule).value;
            String marginValue = rawMarginValue;
            if (marginValue.toLowerCase().endsWith("px")) {
                marginValue = marginValue.substring(0, marginValue.length() - 2);
            }
            try {
                int marginIntValue = Integer.parseInt(marginValue);
                if (marginIntValue < 0) {
                    messageLog.warning(MessageType.IGNORING_NEGATIVE_MARGIN_VALUE, marginRule);
                    marginIntValue = 0;
                }

                return marginIntValue;
            } catch (final NumberFormatException e) {
                messageLog.warning(MessageType.CANNOT_PARSE_MARGIN_VALUE, rawMarginValue);
                return 0;
            }
        } else {
            return defaultMargin;
        }
    }
}
