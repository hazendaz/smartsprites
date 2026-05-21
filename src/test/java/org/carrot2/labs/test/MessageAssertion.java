/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
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
