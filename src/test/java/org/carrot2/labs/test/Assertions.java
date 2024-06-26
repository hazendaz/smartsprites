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
package org.carrot2.labs.test;

import java.awt.image.BufferedImage;
import java.util.List;

import org.carrot2.labs.smartsprites.css.CssProperty;
import org.carrot2.labs.smartsprites.message.Message;

/**
 * FEST-style assertions for SmartSprites-specific data types.
 */
public class Assertions {

    /**
     * Creates a {@link MessageAssertion}.
     *
     * @param actual
     *            the actual
     *
     * @return the message assertion
     */
    public static MessageAssertion assertThat(Message actual) {
        return new MessageAssertion(actual);
    }

    /**
     * Creates a {@link MessageListAssertion}.
     *
     * @param actual
     *            the actual
     *
     * @return the message list assertion
     */
    public static MessageListAssertion assertThat(List<Message> actual) {
        return new MessageListAssertion(actual);
    }

    /**
     * Creates a {@link CssPropertyAssertion}.
     *
     * @param actual
     *            the actual
     *
     * @return the css property assertion
     */
    public static CssPropertyAssertion assertThat(CssProperty actual) {
        return new CssPropertyAssertion(actual);
    }

    /**
     * Creates a {@link CssPropertyListAssertion}.
     *
     * @param actual
     *            the actual
     *
     * @return the css property list assertion
     */
    public static CssPropertyListAssertion assertThatCssPropertyList(List<CssProperty> actual) {
        return new CssPropertyListAssertion(actual);
    }

    /**
     * Creates a {@link BufferedImageAssertion}.
     *
     * @param actual
     *            the actual
     *
     * @return the buffered image assertion
     */
    public static BufferedImageAssertion assertThat(BufferedImage actual) {
        return new BufferedImageAssertion(actual);
    }
}
