/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.css;

/**
 * Represents a single CSS property and its value, e.g. <code>background-image: url(img.png)</code>.
 */
public class CssProperty {

    /** The rule. */
    public final String rule;

    /** The value. */
    public final String value;

    /** The important. */
    public final boolean important;

    /**
     * Instantiates a new css property.
     *
     * @param rule
     *            the rule
     * @param value
     *            the value
     */
    public CssProperty(String rule, String value) {
        this(rule, value, false);
    }

    /**
     * Instantiates a new css property.
     *
     * @param rule
     *            the rule
     * @param value
     *            the value
     * @param important
     *            the important
     */
    public CssProperty(String rule, String value, boolean important) {
        this.rule = rule;
        this.value = value;
        this.important = important;
    }
}
