/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link EnumUtils}.
 */
class EnumUtilsTest {

    /**
     * Value of valid name returns matching enum constant.
     */
    @Test
    void valueOfValidNameReturnsMatchingConstant() {
        assertEquals(TimeUnit.SECONDS, EnumUtils.valueOf("SECONDS", TimeUnit.class, TimeUnit.MILLISECONDS));
    }

    /**
     * Value of invalid name returns default value.
     */
    @Test
    void valueOfInvalidNameReturnsDefault() {
        assertEquals(TimeUnit.MILLISECONDS, EnumUtils.valueOf("INVALID", TimeUnit.class, TimeUnit.MILLISECONDS));
    }

    /**
     * Value of null name returns default value.
     */
    @Test
    void valueOfNullNameReturnsDefault() {
        assertEquals(TimeUnit.MILLISECONDS, EnumUtils.valueOf(null, TimeUnit.class, TimeUnit.MILLISECONDS));
    }

    /**
     * Value of blank name returns default value.
     */
    @Test
    void valueOfBlankNameReturnsDefault() {
        assertEquals(TimeUnit.MILLISECONDS, EnumUtils.valueOf("   ", TimeUnit.class, TimeUnit.MILLISECONDS));
    }

    /**
     * Value of empty name returns default value.
     */
    @Test
    void valueOfEmptyNameReturnsDefault() {
        assertEquals(TimeUnit.MILLISECONDS, EnumUtils.valueOf("", TimeUnit.class, TimeUnit.MILLISECONDS));
    }

    /**
     * Value of null name with null default returns null.
     */
    @Test
    void valueOfNullNameWithNullDefaultReturnsNull() {
        assertNull(EnumUtils.valueOf(null, TimeUnit.class, null));
    }
}
