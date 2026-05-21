/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link BufferedImageUtils}.
 */
class BufferedImageUtilsTest extends BufferedImageTestBase {

    /**
     * Test has alpha transparency partial.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testHasAlphaTransparencyPartial() throws IOException {
        assertTrue(BufferedImageUtils.hasTransparency(image("full-alpha.png")));
    }

    /**
     * Test has alpha transparency bitmask.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testHasAlphaTransparencyBitmask() throws IOException {
        assertTrue(BufferedImageUtils.hasTransparency(image("bit-alpha.png")));
    }

    /**
     * Test has alpha transparency no transparency.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testHasAlphaTransparencyNoTransparency() throws IOException {
        assertFalse(BufferedImageUtils.hasTransparency(image("no-alpha.png")));
    }

    /**
     * Test has partial alpha transparency partial.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testHasPartialAlphaTransparencyPartial() throws IOException {
        assertTrue(BufferedImageUtils.hasPartialTransparency(image("full-alpha.png")));
    }

    /**
     * Test has partial alpha transparency bitmask.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testHasPartialAlphaTransparencyBitmask() throws IOException {
        assertFalse(BufferedImageUtils.hasPartialTransparency(image("bit-alpha.png")));
    }

    /**
     * Test has partial alpha transparency no transparency.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testHasPartialAlphaTransparencyNoTransparency() throws IOException {
        assertFalse(BufferedImageUtils.hasPartialTransparency(image("no-alpha.png")));
    }

    /**
     * Test count distinct colors transparency.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCountDistinctColorsTransparency() throws IOException {
        assertEquals(1, BufferedImageUtils.countDistinctColors(image("full-alpha.png")));
    }

    /**
     * Test count distinct colors transparency matted.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCountDistinctColorsTransparencyMatted() throws IOException {
        assertEquals(4,
                BufferedImageUtils.countDistinctColors(BufferedImageUtils.matte(image("full-alpha.png"), Color.WHITE)));
    }

    /**
     * Test count distinct colors no transparency.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCountDistinctColorsNoTransparency() throws IOException {
        assertEquals(4, BufferedImageUtils.countDistinctColors(image("no-alpha.png")));
    }

    /**
     * Test count distinct colors gradient.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCountDistinctColorsGradient() throws IOException {
        // black is the same in all bands
        assertEquals(1021, BufferedImageUtils.countDistinctColors(image("many-colors.png")));
    }
}
