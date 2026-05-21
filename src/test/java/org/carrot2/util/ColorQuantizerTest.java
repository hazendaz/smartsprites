/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import amd.Quantize;

/**
 * Test cases for {@link Quantize}.
 */
class ColorQuantizerTest extends BufferedImageTestBase {

    /**
     * Test one color.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testOneColor() throws IOException {
        final String fileName = "one-color.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).doesNotHaveAlpha().hasNumberOfColorsEqualTo(1)
                .isIndexedColor();
    }

    /**
     * Test no alpha.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testNoAlpha() throws IOException {
        final String fileName = "no-alpha.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).doesNotHaveAlpha().hasNumberOfColorsEqualTo(4)
                .isIndexedColor();
    }

    /**
     * Test bit alpha.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testBitAlpha() throws IOException {
        final String fileName = "bit-alpha.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).hasBitAlpha().hasNumberOfColorsEqualTo(1)
                .isIndexedColor();
    }

    /**
     * Test full alpha.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testFullAlpha() throws IOException {
        final String fileName = "full-alpha.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).hasBitAlpha().hasNumberOfColorsEqualTo(3)
                .isIndexedColor();
    }

    /**
     * Test exact colors quantize.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testExactColorsQuantize() throws IOException {
        final String fileName = "exact-colors.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).hasBitAlpha().hasNumberOfColorsEqualTo(16)
                .isIndexedColor();
    }

    /**
     * Test exact colors reduce.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testExactColorsReduce() throws IOException {
        final String fileName = "exact-colors.png";
        final BufferedImage quantized = ColorQuantizer.reduce(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).hasBitAlpha().hasNumberOfColorsEqualTo(255)
                .isIndexedColor();
    }

    /**
     * Test many colors quantize.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testManyColorsQuantize() throws IOException {
        final String fileName = "many-colors.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).doesNotHaveAlpha().hasNumberOfColorsEqualTo(61)
                .isIndexedColor();
        // Current quantizer is far from perfect
    }

    /**
     * Test many colors reduce.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testManyColorsReduce() throws IOException {
        final String fileName = "many-colors.png";
        assertThrows(IllegalArgumentException.class, () -> {
            ColorQuantizer.reduce(image(fileName));
        });
    }

    /**
     * Test can reduce without data loss.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCanReduceWithoutDataLoss() throws IOException {
        checkDataLoss("bit-alpha.png", true);
        checkDataLoss("exact-colors.png", true);
        checkDataLoss("full-alpha.png", false);
        checkDataLoss("many-colors.png", false);
        checkDataLoss("no-alpha.png", true);
        checkDataLoss("one-color.png", true);
    }

    /**
     * Check data loss.
     *
     * @param path
     *            the path
     * @param expectedCanReduce
     *            the expected can reduce
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void checkDataLoss(String path, boolean expectedCanReduce) throws IOException {
        assertEquals(expectedCanReduce,
                ColorQuantizer.getColorReductionInfo(image(path)).canReduceWithoutQualityLoss());
    }
}
