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
