/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.carrot2.labs.smartsprites.css.CssProperty;
import org.carrot2.labs.smartsprites.message.Message;

/**
 * Assertions on lists of {@link CssProperty} instances.
 */
public class CssPropertyListAssertion {

    /** The actual message list. */
    private List<CssProperty> actual;

    /**
     * Creates a {@link Message} list assertion object.
     *
     * @param actual
     *            the actual
     */
    public CssPropertyListAssertion(List<CssProperty> actual) {
        this.actual = actual;
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected message list.
     *
     * @param properties
     *            the properties
     *
     * @return the css property list assertion
     */
    public CssPropertyListAssertion isEquivalentTo(List<CssProperty> properties) {
        assertThat(actual).hasSize(properties.size());
        for (int i = 0; i < actual.size(); i++) {
            org.carrot2.labs.test.Assertions.assertThat(actual.get(i)).as("property[" + i + "]")
                    .isEquivalentTo(properties.get(i));
        }
        return this;
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected message list.
     *
     * @param properties
     *            the properties
     *
     * @return the css property list assertion
     */
    public CssPropertyListAssertion isEquivalentTo(CssProperty... properties) {
        return isEquivalentTo(new ArrayList<>(Arrays.asList(properties)));
    }

    /**
     * Checks if is empty.
     *
     * @return the css property list assertion
     */
    public CssPropertyListAssertion isEmpty() {
        assertTrue(actual.isEmpty());
        return this;
    }

}
