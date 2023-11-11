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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link BufferedImageUtils}.
 */
class BufferedImageUtilsTest extends BufferedImageTestBase
{
    @Test
    void testHasAlphaTransparencyPartial() throws IOException
    {
        assertTrue(
            BufferedImageUtils.hasTransparency(image("full-alpha.png")));
    }

    @Test
    void testHasAlphaTransparencyBitmask() throws IOException
    {
        assertTrue(
            BufferedImageUtils.hasTransparency(image("bit-alpha.png")));
    }

    @Test
    void testHasAlphaTransparencyNoTransparency() throws IOException
    {
        assertFalse(
            BufferedImageUtils.hasTransparency(image("no-alpha.png")));
    }

    @Test
    void testHasPartialAlphaTransparencyPartial() throws IOException
    {
        assertTrue(
            BufferedImageUtils
                .hasPartialTransparency(image("full-alpha.png")));
    }

    @Test
    void testHasPartialAlphaTransparencyBitmask() throws IOException
    {
        assertFalse(
            BufferedImageUtils
                .hasPartialTransparency(image("bit-alpha.png")));
    }

    @Test
    void testHasPartialAlphaTransparencyNoTransparency() throws IOException
    {
        assertFalse(
            BufferedImageUtils
                .hasPartialTransparency(image("no-alpha.png")));
    }

    @Test
    void testCountDistinctColorsTransparency() throws IOException
    {
        assertEquals(1, 
            BufferedImageUtils
                .countDistinctColors(image("full-alpha.png")));
    }

    @Test
    void testCountDistinctColorsTransparencyMatted() throws IOException
    {
        assertEquals(4,
            BufferedImageUtils.countDistinctColors(BufferedImageUtils.matte(
                image("full-alpha.png"), Color.WHITE)));
    }

    @Test
    void testCountDistinctColorsNoTransparency() throws IOException
    {
        assertEquals(4,
            BufferedImageUtils.countDistinctColors(image("no-alpha.png")));
    }

    @Test
    void testCountDistinctColorsGradient() throws IOException
    {
        // black is the same in all bands
        assertEquals(1021,
            BufferedImageUtils
                .countDistinctColors(image("many-colors.png")));
    }
}
