/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

/**
 * The Class StringUtils.
 */
public class StringUtils {

    /**
     * Instantiates a new string utils.
     */
    private StringUtils() {
        // Prevent Instantiation
    }

    /**
     * Checks if is blank.
     *
     * @param string
     *            the string
     *
     * @return true, if is blank
     */
    public static boolean isBlank(final String string) {
        return Strings.isNullOrEmpty(string) || CharMatcher.whitespace().matchesAllOf(string);
    }

    /**
     * Checks if is not blank.
     *
     * @param string
     *            the string
     *
     * @return true, if is not blank
     */
    public static boolean isNotBlank(final String string) {
        return !StringUtils.isBlank(string);
    }

}
