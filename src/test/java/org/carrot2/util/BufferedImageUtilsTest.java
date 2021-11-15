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
