/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

import org.carrot2.labs.smartsprites.css.CssProperty;
import org.carrot2.labs.smartsprites.css.CssSyntaxUtils;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.util.CollectionUtils;

/**
 * Represents a directive that adds an individual image to a sprite image.
 */
public class SpriteReferenceDirective {

    /** The Constant PROPERTY_SPRITE_REF. */
    public static final String PROPERTY_SPRITE_REF = "sprite-ref";

    /** Allowed properties of this directive. */
    private static final Set<String> ALLOWED_PROPERTIES = ImmutableSet.of(PROPERTY_SPRITE_REF);

    /** Sprite id for this individual image. */
    public final String spriteRef;

    /**
     * Sprite layout properties defined at sprite reference level.
     */
    public final SpriteLayoutProperties spriteLayoutProperties;

    /**
     * Instantiates a new sprite reference directive.
     *
     * @param spriteImageId
     *            the sprite image id
     * @param spriteLayoutProperties
     *            the sprite layout properties
     */
    public SpriteReferenceDirective(String spriteImageId, SpriteLayoutProperties spriteLayoutProperties) {
        this.spriteRef = spriteImageId;
        this.spriteLayoutProperties = spriteLayoutProperties;
    }

    /**
     * Parses a {@link SpriteReferenceDirective} from the provided {@link String}, logging messages to the provided
     * {@link MessageLog}.
     *
     * @param directiveString
     *            the directive string
     * @param spriteImages
     *            the sprite images
     * @param messageCollector
     *            the message collector
     *
     * @return the sprite reference directive
     */
    public static SpriteReferenceDirective parse(String directiveString, Map<String, SpriteImageDirective> spriteImages,
            MessageLog messageCollector) {
        final Map<String, CssProperty> rules = CssSyntaxUtils
                .propertiesAsMap(CssSyntaxUtils.extractRules(directiveString, messageCollector));

        final Set<String> properties = Sets.newLinkedHashSet(rules.keySet());
        properties.removeAll(ALLOWED_PROPERTIES);
        properties.removeAll(SpriteLayoutProperties.ALLOWED_PROPERTIES);
        if (!properties.isEmpty()) {
            messageCollector.warning(MessageType.UNSUPPORTED_PROPERTIES_FOUND, CollectionUtils.toString(properties));
        }

        // Sprite-ref is required
        if (!CssSyntaxUtils.hasNonBlankValue(rules, PROPERTY_SPRITE_REF)) {
            messageCollector.warning(MessageType.SPRITE_REF_NOT_FOUND);
            return null;
        }

        final String spriteRef = rules.get(PROPERTY_SPRITE_REF).value;

        // Check if referred sprite exists
        final SpriteImageDirective spriteImageDirective = spriteImages.get(spriteRef);

        // Referenced sprite not found
        if (spriteImageDirective == null) {
            messageCollector.warning(MessageType.REFERENCED_SPRITE_NOT_FOUND, spriteRef);
            return null;
        }

        // Parse sprite layout properties
        return new SpriteReferenceDirective(spriteRef, SpriteLayoutProperties.parse(directiveString,
                spriteImageDirective.layout, spriteImageDirective.spriteLayoutProperties, messageCollector));
    }
}
