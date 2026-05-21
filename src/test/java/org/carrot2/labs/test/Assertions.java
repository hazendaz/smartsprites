/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
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
