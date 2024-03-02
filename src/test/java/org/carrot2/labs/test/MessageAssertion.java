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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.carrot2.labs.smartsprites.message.Message;

/**
 * Assertions on {@link Message}s.
 */
public class MessageAssertion {

    /** The actual message. */
    private Message actual;

    /** Assertion description. */
    private String description = "message";

    /**
     * Creates a {@link Message} assertion object.
     *
     * @param actual
     *            the actual
     */
    public MessageAssertion(Message actual) {
        this.actual = actual;
    }

    /**
     * Asserts that the current message is equivalent to the provided expected message.
     *
     * @param expected
     *            the expected
     *
     * @return the message assertion
     */
    public MessageAssertion isEquivalentTo(Message expected) {
        assertNotNull(actual);
        assertThat(actual.cssPath).as(description + ".cssPath").isEqualTo(expected.cssPath);
        assertThat(actual.line).as(description + ".line").isEqualTo(expected.line);
        assertThat(actual.level).as(description + ".level").isEqualTo(expected.level);
        assertThat(actual.type).as(description + ".type").isEqualTo(expected.type);
        assertThat(actual.arguments).as(description + ".arguments").isEqualTo(expected.arguments);

        return this;
    }

    /**
     * As.
     *
     * @param description
     *            the description
     *
     * @return the message assertion
     */
    public MessageAssertion as(String description) {
        this.description = description;
        return this;
    }
}
