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
package org.carrot2.labs.smartsprites.css;

import static org.carrot2.labs.test.Assertions.assertThat;
import static org.carrot2.labs.test.Assertions.assertThatCssPropertyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.util.List;

import org.carrot2.labs.smartsprites.TestWithMemoryMessageSink;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link CssSyntaxUtils}.
 */
class CssSyntaxUtilsTest extends TestWithMemoryMessageSink {
    @Test
    void testEmpty() {
        final List<CssProperty> actualRules = CssSyntaxUtils.extractProperties("   ");
        assertThatCssPropertyList(actualRules).isEmpty();
    }

    @Test
    void testSingleRule() {
        final List<CssProperty> actualRules = CssSyntaxUtils.extractProperties("test-rule: test-value");

        assertThatCssPropertyList(actualRules).isEquivalentTo(new CssProperty("test-rule", "test-value"));
    }

    @Test
    void testImportantRules() {
        final List<CssProperty> actualRules = CssSyntaxUtils
                .extractProperties("background-image: url(test.png) !important; color: red!   important");

        assertThatCssPropertyList(actualRules).isEquivalentTo(
                new CssProperty("background-image", "url(test.png)", true), new CssProperty("color", "red", true));
    }

    @Test
    void testUpperCaseRule() {
        final List<CssProperty> actualRules = CssSyntaxUtils.extractProperties("TEST-rule: test-value");

        assertThatCssPropertyList(actualRules).isEquivalentTo(new CssProperty("test-rule", "test-value"));
    }

    @Test
    void testMoreRules() {
        final List<CssProperty> actualRules = CssSyntaxUtils.extractProperties("rule-1: value1; rule-2: value2;");

        assertThatCssPropertyList(actualRules).isEquivalentTo(new CssProperty("rule-1", "value1"),
                new CssProperty("rule-2", "value2"));
    }

    @Test
    void testWhiteSpace() {
        final List<CssProperty> actualRules = CssSyntaxUtils
                .extractProperties("\trule-1  : value1  ; \trule-2  : value2\t;");

        assertThatCssPropertyList(actualRules).isEquivalentTo(new CssProperty("rule-1", "value1"),
                new CssProperty("rule-2", "value2"));
    }

    @Test
    void testColonsInPropertyValue() {
        final List<CssProperty> actualRules = CssSyntaxUtils
                .extractRules("rule-1: value1 : v2; rule-2: url(jar:/test.png);", messageLog);

        // This isn't really valid in CSS, but until we switch to full
        // parsing, this should be enough.
        assertThatCssPropertyList(actualRules).isEquivalentTo(new CssProperty("rule-1", "value2 : v2"),
                new CssProperty("rule-2", "url(jar:/test.png)"));

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testUnpackUrlNoQuotes() {
        assertEquals("test/img/t.png", CssSyntaxUtils.unpackUrl("url(test/img/t.png)"));
    }

    @Test
    void testUnpackUrlSingleQuotes() {
        assertEquals("test/img/t.png", CssSyntaxUtils.unpackUrl("url('test/img/t.png')"));
    }

    @Test
    void testUnpackUrlDoubleQuotes() {
        assertEquals("test/img/t.png", CssSyntaxUtils.unpackUrl("url(\"test/img/t.png\")"));
    }

    @Test
    void testUnbalancedQuotes() {
        assertEquals(null, CssSyntaxUtils.unpackUrl("url('test/img/t.png\")", messageLog));

        assertThat(messages).isEquivalentTo(new Message(Message.MessageLevel.WARN, Message.MessageType.MALFORMED_URL,
                null, 0, "url('test/img/t.png\")"));
    }

    @Test
    void testMalformedPrefix() {
        assertEquals(null, CssSyntaxUtils.unpackUrl("urlx('test/img/t.png')", messageLog));

        assertThat(messages).isEquivalentTo(new Message(Message.MessageLevel.WARN, Message.MessageType.MALFORMED_URL,
                null, 0, "urlx('test/img/t.png')"));
    }

    @Test
    void testLongCssColor() {
        assertEquals("cafe01", parseCssColor("#cafe01"));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testInvalidCssColor() {
        assertEquals(null, parseCssColor("cafe01"));
        assertThat(messages)
                .isEquivalentTo(new Message(MessageLevel.WARN, MessageType.MALFORMED_COLOR, null, 0, "cafe01"));
    }

    @Test
    void testShortCssColor() {
        // Currently unsupported
        assertEquals(null, parseCssColor("#fff"));
        assertThat(messages)
                .isEquivalentTo(new Message(MessageLevel.WARN, MessageType.MALFORMED_COLOR, null, 0, "#fff"));
    }

    private String parseCssColor(String cssColor) {
        final Color color = CssSyntaxUtils.parseColor(cssColor, messageLog, null);
        if (color != null) {
            return Integer.toString(color.getRGB() & 0x00ffffff, 16);
        } else {
            return null;
        }
    }
}
