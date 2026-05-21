/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites;

import static org.carrot2.labs.test.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.carrot2.labs.smartsprites.css.CssSyntaxUtils;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.resource.FileSystemResourceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link SpriteDirectiveOccurrenceCollector}.
 */
class SpriteDirectiveOccurrenceCollectorTest extends TestWithMemoryMessageSink {

    /** The sprite directive occurrence collector. */
    SpriteDirectiveOccurrenceCollector spriteDirectiveOccurrenceCollector;

    /**
     * Prepare.
     */
    @BeforeEach
    void prepare() {
        spriteDirectiveOccurrenceCollector = new SpriteDirectiveOccurrenceCollector(messageLog,
                new FileSystemResourceHandler(null, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING, messageLog));
    }

    /**
     * Test sprite image directive extraction one directive complex.
     */
    @Test
    void testSpriteImageDirectiveExtractionOneDirectiveComplex() {
        final String spriteDirective = "sprite: sprite; sprite-image-url: url('../sprite.png'); sprite-image-layout: vertical";
        final String css = ".test { margin-top: 10px }\n/* some comment */\n" + "/* " + spriteDirective + " */";

        assertEquals(spriteDirective, SpriteDirectiveOccurrenceCollector.extractSpriteImageDirectiveString(css));
    }

    /**
     * Test sprite image directive extraction one directive simple.
     */
    @Test
    void testSpriteImageDirectiveExtractionOneDirectiveSimple() {
        final String spriteDirective = "sprite: sprite";
        final String css = "/* " + spriteDirective + " */";

        assertEquals(spriteDirective, SpriteDirectiveOccurrenceCollector.extractSpriteImageDirectiveString(css));
    }

    /**
     * Test sprite image directive extraction more directives.
     */
    @Test
    void testSpriteImageDirectiveExtractionMoreDirectives() {
        final String spriteDirective1 = "sprite: sprite; sprite-image-url: url('../sprite.png'); sprite-image-layout: vertical";
        final String spriteDirective2 = "sprite: sprite2; sprite-image-url: url('../sprite2.png'); sprite-image-layout: horizontal";
        final String css = ".test { margin-top: 10px }\n/* some comment */\n" + "/* " + spriteDirective1 + " */\n"
                + ".rule { float: left }\n" + "/*** \t" + spriteDirective2 + " \t **/";

        assertEquals(spriteDirective1, SpriteDirectiveOccurrenceCollector.extractSpriteImageDirectiveString(css));
    }

    /**
     * Test sprite reference directive extraction.
     */
    @Test
    void testSpriteReferenceDirectiveExtraction() {
        final String spriteDirective = "sprite-ref: sprite; sprite-alignment: repeat";
        final String css = "background-image: url('../img/img.png'); /** " + spriteDirective + " */";

        assertEquals(spriteDirective, SpriteDirectiveOccurrenceCollector.extractSpriteReferenceDirectiveString(css));
    }

    /**
     * Test sprite reference image url extraction.
     */
    @Test
    void testSpriteReferenceImageUrlExtraction() {
        final String spriteDirective = "sprite-ref: sprite; sprite-alignment: repeat";
        final String css = "background-image: url('../img/img.png'); /** " + spriteDirective + " */";

        assertEquals("../img/img.png", CssSyntaxUtils
                .unpackUrl(spriteDirectiveOccurrenceCollector.extractSpriteReferenceCssProperty(css).value, null));
    }

    /**
     * Test sprite reference image url extraction no background image.
     */
    @Test
    void testSpriteReferenceImageUrlExtractionNoBackgroundImage() {
        final String spriteDirective = "sprite-ref: sprite; sprite-alignment: repeat";
        final String css = "background-imagez: url('../img/img.png'); /** " + spriteDirective + " */";

        assertEquals(null, spriteDirectiveOccurrenceCollector.extractSpriteReferenceCssProperty(css));

        assertThat(messages).isEquivalentTo(new Message(Message.MessageLevel.WARN,
                Message.MessageType.NO_BACKGROUND_IMAGE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE, null, 0, css));
    }

    /**
     * Test sprite reference image url extraction more rules.
     */
    @Test
    void testSpriteReferenceImageUrlExtractionMoreRules() {
        final String spriteDirective = "sprite-ref: sprite; sprite-alignment: repeat";
        final String css = "color: red; background-image: url('../img/img.png'); /** " + spriteDirective + " */";

        assertEquals(null, spriteDirectiveOccurrenceCollector.extractSpriteReferenceCssProperty(css));

        assertThat(messages).isEquivalentTo(new Message(Message.MessageLevel.WARN,
                Message.MessageType.MORE_THAN_ONE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE, null, 0, css));
    }
}
