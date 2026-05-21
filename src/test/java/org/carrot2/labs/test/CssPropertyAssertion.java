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

import org.carrot2.labs.smartsprites.css.CssProperty;

/**
 * Assertions on instances of {@link CssProperty}.
 */
public class CssPropertyAssertion {

    /** The actual property. */
    private CssProperty actual;

    /** Assertion description. */
    private String description = "cssProperty";

    /**
     * Creates a {@link CssProperty} assertion object.
     *
     * @param actual
     *            the actual
     */
    public CssPropertyAssertion(CssProperty actual) {
        this.actual = actual;
    }

    /**
     * Asserts that the current property is equivalent to the provided expected property.
     *
     * @param expected
     *            the expected
     *
     * @return the css property assertion
     */
    public CssPropertyAssertion isEquivalentTo(CssProperty expected) {
        assertNotNull(actual);
        assertThat(actual.rule).as(description + ".rule").isEqualTo(actual.rule);
        assertThat(actual.value).as(description + ".value").isEqualTo(actual.value);

        return this;
    }

    /**
     * As.
     *
     * @param description
     *            the description
     *
     * @return the css property assertion
     */
    public CssPropertyAssertion as(String description) {
        this.description = description;
        return this;
    }
}
