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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.common.collect.ImmutableMap;

import java.awt.Color;
import java.util.Map;

import org.carrot2.labs.smartsprites.SpriteImageDirective.Ie6Mode;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageFormat;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageLayout;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteUidType;
import org.carrot2.labs.smartsprites.SpriteLayoutProperties.SpriteAlignment;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link SpriteReferenceDirective}.
 */
class SpriteReferenceDirectiveTest extends TestWithMemoryMessageSink {

    /** The Constant VERTICAL_SPRITE_IMAGE_DIRECTIVE. */
    private static final SpriteImageDirective VERTICAL_SPRITE_IMAGE_DIRECTIVE = new SpriteImageDirective("vsprite",
            "sprite.png", SpriteImageLayout.VERTICAL, SpriteImageFormat.PNG, Ie6Mode.AUTO, Color.WHITE,
            SpriteUidType.NONE, 1);

    /** The Constant HORIZONTAL_SPRITE_IMAGE_DIRECTIVE. */
    private static final SpriteImageDirective HORIZONTAL_SPRITE_IMAGE_DIRECTIVE = new SpriteImageDirective("hsprite",
            "hsprite.png", SpriteImageLayout.HORIZONTAL, SpriteImageFormat.PNG, Ie6Mode.AUTO, Color.WHITE,
            SpriteUidType.NONE, 1);

    /** The Constant VERTICAL_SPRITE_IMAGE_DIRECTIVE_WITH_LAYOUT. */
    private static final SpriteImageDirective VERTICAL_SPRITE_IMAGE_DIRECTIVE_WITH_LAYOUT = new SpriteImageDirective(
            "vsprite", "sprite.png", SpriteImageLayout.VERTICAL, SpriteImageFormat.PNG, Ie6Mode.AUTO, Color.WHITE,
            SpriteUidType.NONE, 1, new SpriteLayoutProperties(SpriteAlignment.REPEAT, 1, 2, 3, 4));

    /** The Constant SPRITE_IMAGE_DIRECTIVES. */
    private static final Map<String, SpriteImageDirective> SPRITE_IMAGE_DIRECTIVES = ImmutableMap.of("vsprite",
            VERTICAL_SPRITE_IMAGE_DIRECTIVE, "hsprite", HORIZONTAL_SPRITE_IMAGE_DIRECTIVE, "vlsprite",
            VERTICAL_SPRITE_IMAGE_DIRECTIVE_WITH_LAYOUT);

    /**
     * Test empty.
     */
    @Test
    void testEmpty() {
        final MessageLog messageLog = new MessageLog();
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse("", SPRITE_IMAGE_DIRECTIVES,
                messageLog);
        assertNull(directive);
    }

    /**
     * Test sprite ref only.
     */
    @Test
    void testSpriteRefOnly() {
        final MessageLog messageLog = new MessageLog();
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse("sprite-ref: vsprite",
                SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("vsprite", directive.spriteRef);
        assertEquals(SpriteAlignment.LEFT, directive.spriteLayoutProperties.alignment);
        assertEquals(0, directive.spriteLayoutProperties.marginLeft);
        assertEquals(0, directive.spriteLayoutProperties.marginRight);
        assertEquals(0, directive.spriteLayoutProperties.marginTop);
        assertEquals(0, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEmpty();
    }

    /**
     * Test sprite ref only horizontal image.
     */
    @Test
    void testSpriteRefOnlyHorizontalImage() {
        final MessageLog messageLog = new MessageLog();
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse("sprite-ref: hsprite",
                SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("hsprite", directive.spriteRef);
        assertEquals(SpriteAlignment.TOP, directive.spriteLayoutProperties.alignment);
        assertEquals(0, directive.spriteLayoutProperties.marginLeft);
        assertEquals(0, directive.spriteLayoutProperties.marginRight);
        assertEquals(0, directive.spriteLayoutProperties.marginTop);
        assertEquals(0, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEmpty();
    }

    /**
     * Test sprite ref not found.
     */
    @Test
    void testSpriteRefNotFound() {
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse("sprite-ref: spritex",
                SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNull(directive);

        assertThat(messages).isEquivalentTo(new Message(Message.MessageLevel.WARN,
                Message.MessageType.REFERENCED_SPRITE_NOT_FOUND, null, 0, "spritex"));
    }

    /**
     * Test sprite ref alignment.
     */
    @Test
    void testSpriteRefAlignment() {
        final MessageLog messageLog = new MessageLog();
        final SpriteReferenceDirective directive = SpriteReferenceDirective
                .parse("sprite-ref: vsprite; sprite-alignment: repeat", SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("vsprite", directive.spriteRef);
        assertEquals(SpriteAlignment.REPEAT, directive.spriteLayoutProperties.alignment);
        assertEquals(0, directive.spriteLayoutProperties.marginLeft);
        assertEquals(0, directive.spriteLayoutProperties.marginRight);
        assertEquals(0, directive.spriteLayoutProperties.marginTop);
        assertEquals(0, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEmpty();
    }

    /**
     * Test unsupported alignment.
     */
    @Test
    void testUnsupportedAlignment() {
        final SpriteReferenceDirective directive = SpriteReferenceDirective
                .parse("sprite-ref: vsprite; sprite-alignment: repeat-x", SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("vsprite", directive.spriteRef);
        assertEquals(SpriteAlignment.LEFT, directive.spriteLayoutProperties.alignment);
        assertEquals(0, directive.spriteLayoutProperties.marginLeft);
        assertEquals(0, directive.spriteLayoutProperties.marginRight);
        assertEquals(0, directive.spriteLayoutProperties.marginTop);
        assertEquals(0, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEquivalentTo(
                new Message(Message.MessageLevel.WARN, Message.MessageType.UNSUPPORTED_ALIGNMENT, null, 0, "repeat-x"));
    }

    /**
     * Test mismatched top alignment.
     */
    @Test
    void testMismatchedTopAlignment() {
        checkMismatchedAlignment("vsprite", "top", SpriteAlignment.LEFT,
                Message.MessageType.ONLY_LEFT_OR_RIGHT_ALIGNMENT_ALLOWED);
    }

    /**
     * Test mismatched bottom alignment.
     */
    @Test
    void testMismatchedBottomAlignment() {
        checkMismatchedAlignment("vsprite", "bottom", SpriteAlignment.LEFT,
                Message.MessageType.ONLY_LEFT_OR_RIGHT_ALIGNMENT_ALLOWED);
    }

    /**
     * Test mismatched left alignment.
     */
    @Test
    void testMismatchedLeftAlignment() {
        checkMismatchedAlignment("hsprite", "left", SpriteAlignment.TOP,
                Message.MessageType.ONLY_TOP_OR_BOTTOM_ALIGNMENT_ALLOWED);
    }

    /**
     * Test mismatched right alignment.
     */
    @Test
    void testMismatchedRightAlignment() {
        checkMismatchedAlignment("hsprite", "right", SpriteAlignment.TOP,
                Message.MessageType.ONLY_TOP_OR_BOTTOM_ALIGNMENT_ALLOWED);
    }

    /**
     * Check mismatched alignment.
     *
     * @param sprite
     *            the sprite
     * @param alignment
     *            the alignment
     * @param correctedAlignment
     *            the corrected alignment
     * @param message
     *            the message
     */
    private void checkMismatchedAlignment(String sprite, final String alignment, SpriteAlignment correctedAlignment,
            MessageType message) {
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse(
                "sprite-ref: " + sprite + "; sprite-alignment: " + alignment, SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals(sprite, directive.spriteRef);
        assertEquals(correctedAlignment, directive.spriteLayoutProperties.alignment);
        assertEquals(0, directive.spriteLayoutProperties.marginLeft);
        assertEquals(0, directive.spriteLayoutProperties.marginRight);
        assertEquals(0, directive.spriteLayoutProperties.marginTop);
        assertEquals(0, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEquivalentTo(new Message(Message.MessageLevel.WARN, message, null, 0, alignment));
    }

    /**
     * Test sprite margins.
     */
    @Test
    void testSpriteMargins() {
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse(
                "sprite-ref: vsprite; sprite-margin-left: 10px; sprite-margin-right: 20; sprite-margin-top: 30px; sprite-margin-bottom: 40;",
                SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("vsprite", directive.spriteRef);
        assertEquals(SpriteAlignment.LEFT, directive.spriteLayoutProperties.alignment);
        assertEquals(10, directive.spriteLayoutProperties.marginLeft);
        assertEquals(20, directive.spriteLayoutProperties.marginRight);
        assertEquals(30, directive.spriteLayoutProperties.marginTop);
        assertEquals(40, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEmpty();
    }

    /**
     * Test cannot parse margin.
     */
    @Test
    void testCannotParseMargin() {
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse(
                "sprite-ref: vsprite; sprite-margin-left: 10zpx; sprite-margin-right: 20; sprite-margin-top: 30; sprite-margin-bottom: 40;",
                SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("vsprite", directive.spriteRef);
        assertEquals(SpriteAlignment.LEFT, directive.spriteLayoutProperties.alignment);
        assertEquals(0, directive.spriteLayoutProperties.marginLeft);
        assertEquals(20, directive.spriteLayoutProperties.marginRight);
        assertEquals(30, directive.spriteLayoutProperties.marginTop);
        assertEquals(40, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEquivalentTo(new Message(Message.MessageLevel.WARN,
                Message.MessageType.CANNOT_PARSE_MARGIN_VALUE, null, 0, "10zpx"));
    }

    /**
     * Test unsupported properties.
     */
    @Test
    void testUnsupportedProperties() {
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse(
                "sprite-ref: vsprite; sprites-alignment: repeat; sprites-margin-left: 10px", SPRITE_IMAGE_DIRECTIVES,
                messageLog);

        assertNotNull(directive);
        assertEquals("vsprite", directive.spriteRef);

        assertThat(messages).isEquivalentTo(new Message(Message.MessageLevel.WARN,
                Message.MessageType.UNSUPPORTED_PROPERTIES_FOUND, null, 0, "sprites-alignment, sprites-margin-left"));
    }

    /**
     * Test sprite layout from sprite image directive.
     */
    @Test
    void testSpriteLayoutFromSpriteImageDirective() {
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse("sprite-ref: vlsprite",
                SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("vlsprite", directive.spriteRef);
        assertEquals(VERTICAL_SPRITE_IMAGE_DIRECTIVE_WITH_LAYOUT.spriteLayoutProperties.alignment,
                directive.spriteLayoutProperties.alignment);
        assertEquals(VERTICAL_SPRITE_IMAGE_DIRECTIVE_WITH_LAYOUT.spriteLayoutProperties.marginLeft,
                directive.spriteLayoutProperties.marginLeft);
        assertEquals(VERTICAL_SPRITE_IMAGE_DIRECTIVE_WITH_LAYOUT.spriteLayoutProperties.marginRight,
                directive.spriteLayoutProperties.marginRight);
        assertEquals(VERTICAL_SPRITE_IMAGE_DIRECTIVE_WITH_LAYOUT.spriteLayoutProperties.marginTop,
                directive.spriteLayoutProperties.marginTop);
        assertEquals(VERTICAL_SPRITE_IMAGE_DIRECTIVE_WITH_LAYOUT.spriteLayoutProperties.marginBottom,
                directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEmpty();
    }

    /**
     * Test overridden sprite layout from sprite image directive.
     */
    @Test
    void testOverriddenSpriteLayoutFromSpriteImageDirective() {
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse(
                "sprite-ref: vlsprite; sprite-alignment: right; sprite-margin-left: 10px; sprite-margin-right: 20; sprite-margin-top: 30px; sprite-margin-bottom: 40;",
                SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("vlsprite", directive.spriteRef);
        assertEquals(SpriteAlignment.RIGHT, directive.spriteLayoutProperties.alignment);
        assertEquals(10, directive.spriteLayoutProperties.marginLeft);
        assertEquals(20, directive.spriteLayoutProperties.marginRight);
        assertEquals(30, directive.spriteLayoutProperties.marginTop);
        assertEquals(40, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).isEmpty();
    }

    /**
     * Test negative margin values.
     */
    @Test
    void testNegativeMarginValues() {
        final SpriteReferenceDirective directive = SpriteReferenceDirective.parse(
                "sprite-ref: vlsprite; sprite-alignment: right; sprite-margin-left: -10px; sprite-margin-right: 20; sprite-margin-top: 30px; sprite-margin-bottom: -40;",
                SPRITE_IMAGE_DIRECTIVES, messageLog);

        assertNotNull(directive);
        assertEquals("vlsprite", directive.spriteRef);
        assertEquals(SpriteAlignment.RIGHT, directive.spriteLayoutProperties.alignment);
        assertEquals(0, directive.spriteLayoutProperties.marginLeft);
        assertEquals(20, directive.spriteLayoutProperties.marginRight);
        assertEquals(30, directive.spriteLayoutProperties.marginTop);
        assertEquals(0, directive.spriteLayoutProperties.marginBottom);

        assertThat(messages).contains(
                new Message(Message.MessageLevel.WARN, Message.MessageType.IGNORING_NEGATIVE_MARGIN_VALUE, null, 0,
                        "sprite-margin-left"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.IGNORING_NEGATIVE_MARGIN_VALUE, null, 0,
                        "sprite-margin-bottom"));
    }
}
