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
class SpriteDirectiveOccurrenceCollectorTest extends TestWithMemoryMessageSink
{
    SpriteDirectiveOccurrenceCollector spriteDirectiveOccurrenceCollector;

    @BeforeEach
    void prepare()
    {
        spriteDirectiveOccurrenceCollector = new SpriteDirectiveOccurrenceCollector(
            messageLog, new FileSystemResourceHandler(null, 
                SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING, messageLog));
    }

    @Test
    void testSpriteImageDirectiveExtractionOneDirectiveComplex()
    {
        final String spriteDirective = "sprite: sprite; sprite-image-url: url('../sprite.png'); sprite-image-layout: vertical";
        final String css = ".test { margin-top: 10px }\n/* some comment */\n" + "/* "
            + spriteDirective + " */";

        assertEquals(spriteDirective, SpriteDirectiveOccurrenceCollector
            .extractSpriteImageDirectiveString(css));
    }

    @Test
    void testSpriteImageDirectiveExtractionOneDirectiveSimple()
    {
        final String spriteDirective = "sprite: sprite";
        final String css = "/* " + spriteDirective + " */";

        assertEquals(spriteDirective, SpriteDirectiveOccurrenceCollector
            .extractSpriteImageDirectiveString(css));
    }

    @Test
    void testSpriteImageDirectiveExtractionMoreDirectives()
    {
        final String spriteDirective1 = "sprite: sprite; sprite-image-url: url('../sprite.png'); sprite-image-layout: vertical";
        final String spriteDirective2 = "sprite: sprite2; sprite-image-url: url('../sprite2.png'); sprite-image-layout: horizontal";
        final String css = ".test { margin-top: 10px }\n/* some comment */\n" + "/* "
            + spriteDirective1 + " */\n" + ".rule { float: left }\n" + "/*** \t"
            + spriteDirective2 + " \t **/";

        assertEquals(spriteDirective1, SpriteDirectiveOccurrenceCollector
            .extractSpriteImageDirectiveString(css));
    }

    @Test
    void testSpriteReferenceDirectiveExtraction()
    {
        final String spriteDirective = "sprite-ref: sprite; sprite-alignment: repeat";
        final String css = "background-image: url('../img/img.png'); /** "
            + spriteDirective + " */";

        assertEquals(spriteDirective, SpriteDirectiveOccurrenceCollector
            .extractSpriteReferenceDirectiveString(css));
    }

    @Test
    void testSpriteReferenceImageUrlExtraction()
    {
        final String spriteDirective = "sprite-ref: sprite; sprite-alignment: repeat";
        final String css = "background-image: url('../img/img.png'); /** "
            + spriteDirective + " */";

        assertEquals("../img/img.png", CssSyntaxUtils.unpackUrl(spriteDirectiveOccurrenceCollector
            .extractSpriteReferenceCssProperty(css).value, null));
    }

    @Test
    void testSpriteReferenceImageUrlExtractionNoBackgroundImage()
    {
        final String spriteDirective = "sprite-ref: sprite; sprite-alignment: repeat";
        final String css = "background-imagez: url('../img/img.png'); /** "
            + spriteDirective + " */";

        assertEquals(null, spriteDirectiveOccurrenceCollector
            .extractSpriteReferenceCssProperty(css));

        assertThat(messages)
            .isEquivalentTo(
                new Message(
                    Message.MessageLevel.WARN,
                    Message.MessageType.NO_BACKGROUND_IMAGE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE,
                    null, 0, css));
    }

    @Test
    void testSpriteReferenceImageUrlExtractionMoreRules()
    {
        final String spriteDirective = "sprite-ref: sprite; sprite-alignment: repeat";
        final String css = "color: red; background-image: url('../img/img.png'); /** "
            + spriteDirective + " */";

        assertEquals(null, spriteDirectiveOccurrenceCollector
            .extractSpriteReferenceCssProperty(css));

        assertThat(messages)
            .isEquivalentTo(
                new Message(
                    Message.MessageLevel.WARN,
                    Message.MessageType.MORE_THAN_ONE_RULE_NEXT_TO_SPRITE_REFERENCE_DIRECTIVE,
                    null, 0, css));
    }
}
