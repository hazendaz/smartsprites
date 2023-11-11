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
class ColorQuantizerTest extends BufferedImageTestBase
{
    @Test
    void testOneColor() throws IOException
    {
        final String fileName = "one-color.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).doesNotHaveAlpha()
            .hasNumberOfColorsEqualTo(1).isIndexedColor();
    }

    @Test
    void testNoAlpha() throws IOException
    {
        final String fileName = "no-alpha.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).doesNotHaveAlpha()
            .hasNumberOfColorsEqualTo(4).isIndexedColor();
    }

    @Test
    void testBitAlpha() throws IOException
    {
        final String fileName = "bit-alpha.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).hasBitAlpha()
            .hasNumberOfColorsEqualTo(1).isIndexedColor();
    }

    @Test
    void testFullAlpha() throws IOException
    {
        final String fileName = "full-alpha.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).hasBitAlpha()
            .hasNumberOfColorsEqualTo(3).isIndexedColor();
    }

    @Test
    void testExactColorsQuantize() throws IOException
    {
        final String fileName = "exact-colors.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).hasBitAlpha()
            .hasNumberOfColorsEqualTo(16).isIndexedColor();
    }

    @Test
    void testExactColorsReduce() throws IOException
    {
        final String fileName = "exact-colors.png";
        final BufferedImage quantized = ColorQuantizer.reduce(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).hasBitAlpha()
            .hasNumberOfColorsEqualTo(255).isIndexedColor();
    }

    @Test
    void testManyColorsQuantize() throws IOException
    {
        final String fileName = "many-colors.png";
        final BufferedImage quantized = ColorQuantizer.quantize(image(fileName));
        org.carrot2.labs.test.Assertions.assertThat(quantized).doesNotHaveAlpha()
            .hasNumberOfColorsEqualTo(61).isIndexedColor();
        // Current quantizer is far from perfect
    }

    @Test
    void testManyColorsReduce() throws IOException
    {
        final String fileName = "many-colors.png";
        assertThrows(IllegalArgumentException.class, () -> {
            ColorQuantizer.reduce(image(fileName));
        });
    }

    @Test
    void testCanReduceWithoutDataLoss() throws IOException
    {
        checkDataLoss("bit-alpha.png", true);
        checkDataLoss("exact-colors.png", true);
        checkDataLoss("full-alpha.png", false);
        checkDataLoss("many-colors.png", false);
        checkDataLoss("no-alpha.png", true);
        checkDataLoss("one-color.png", true);
    }

    private void checkDataLoss(String path, boolean expectedCanReduce) throws IOException
    {
        assertEquals(expectedCanReduce,
            ColorQuantizer.getColorReductionInfo(image(path))
                .canReduceWithoutQualityLoss());
    }
}
