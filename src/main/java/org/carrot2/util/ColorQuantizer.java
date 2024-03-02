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

import java.awt.Color;
import java.awt.image.*;

import amd.Quantize;

/**
 * A simple utility wrapping the {@link Quantize} class to work on {@link BufferedImage}s
 * and handle transparency.
 */
public class ColorQuantizer
{
    
    /**  Maximum number of colors in an indexed image, leaving one for transparency. */
    public static final int MAX_INDEXED_COLORS = 255;

    /**
     * Instantiates a new color quantizer.
     */
    private ColorQuantizer()
    {
        // Prevent Instantiation
    }

    /**
     * Quantizes the image to {@link #MAX_INDEXED_COLORS} with white matte for areas with
     * partial transparency (full transparency will be preserved).
     *
     * @param source the source
     * @return {@link BufferedImage} with type {@link BufferedImage#TYPE_BYTE_INDEXED} and
     *         quantized colors
     */
    public static BufferedImage quantize(BufferedImage source)
    {
        return quantize(source, Color.WHITE);
    }

    /**
     * Quantizes the image to {@link #MAX_INDEXED_COLORS} with the provided matte
     * {@link Color} for areas with partial transparency (full transparency will be
     * preserved).
     *
     * @param source the source
     * @param matteColor the matte color
     * @return {@link BufferedImage} with type {@link BufferedImage#TYPE_BYTE_INDEXED} and
     *         quantized colors
     */
    public static BufferedImage quantize(BufferedImage source, Color matteColor)
    {
        return quantize(source, matteColor, MAX_INDEXED_COLORS);
    }

    /**
     * Quantizes the image to the provided number of colors with the provided matte
     * {@link Color} for areas with partial transparency (full transparency will be
     * preserved).
     *
     * @param source the source
     * @param matteColor the matte color
     * @param maxColors the max colors
     * @return {@link BufferedImage} with type {@link BufferedImage#TYPE_BYTE_INDEXED} and
     *         quantized colors
     */
    public static BufferedImage quantize(BufferedImage source, Color matteColor,
        int maxColors)
    {
        final int width = source.getWidth();
        final int height = source.getHeight();

        // First put the matte color so that we have a sensible result
        // for images with full alpha transparencies
        final BufferedImage mattedSource = BufferedImageUtils.matte(source, matteColor);

        // Get two copies of RGB data (quantization will overwrite one)
        final int [][] bitmap = BufferedImageUtils.getRgb(mattedSource);

        // Quantize colors and shift palette by one for transparency color
        // We'll keep transparency color black for now.
        final int [] colors = Quantize.quantizeImage(bitmap, maxColors);
        final int [] colorsWithAlpha = new int [colors.length + 1];
        System.arraycopy(colors, 0, colorsWithAlpha, 1, colors.length);
        colorsWithAlpha[0] = matteColor.getRGB();
        final IndexColorModel colorModel = new IndexColorModel(8, colorsWithAlpha.length,
            colorsWithAlpha, 0, false, 0, DataBuffer.TYPE_BYTE);

        // Write the results to an indexed image, skipping the fully transparent bits
        final BufferedImage quantized = new BufferedImage(width, height,
            BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        final WritableRaster raster = quantized.getRaster();
        final int [][] rgb = BufferedImageUtils.getRgb(source);
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                final int value = (rgb[x][y] & 0xff000000) != 0x00000000 ? bitmap[x][y] + 1
                    : 0;
                raster.setPixel(x, y, new int []
                {
                    value
                });
            }
        }

        return quantized;
    }

    /**
     * Reduces a direct color buffered image to an indexed color one without quality loss.
     * To make sure no quality loss will occur, check the results of the
     * {@link #getColorReductionInfo(BufferedImage)} method call.
     *
     * @param source the source
     * @return the buffered image
     * @throws IllegalArgumentException if the application of this method would result in
     *             image quality loss
     */
    public static BufferedImage reduce(BufferedImage source)
    {
        final int width = source.getWidth();
        final int height = source.getHeight();

        if (BufferedImageUtils.hasPartialTransparency(source))
        {
            throw new IllegalArgumentException(
                "The source image cannot contain translucent areas");
        }

        final int [] colorsWithAlpha = BufferedImageUtils.getDistinctColors(source, 1);
        if (colorsWithAlpha.length - 1 > MAX_INDEXED_COLORS)
        {
            throw new IllegalArgumentException(
                "The source image cannot contain more than " + MAX_INDEXED_COLORS
                    + " colors");
        }

        final IndexColorModel colorModel = new IndexColorModel(8, colorsWithAlpha.length,
            colorsWithAlpha, 0, false, 0, DataBuffer.TYPE_BYTE);

        // Write the results to an indexed image, skipping the fully transparent bits
        final BufferedImage quantized = new BufferedImage(width, height,
            BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        final int [][] rgb = BufferedImageUtils.getRgb(source);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if ((rgb[x][y] & 0xff000000) != 0x00000000)
                {
                    quantized.setRGB(x, y, source.getRGB(x, y));
                }
            }
        }

        return quantized;
    }

    /**
     * Returns a {@link ColorReductionInfo} for the provided image.
     *
     * @param source the source
     * @return the color reduction info
     */
    public static ColorReductionInfo getColorReductionInfo(BufferedImage source)
    {
        return new ColorReductionInfo(BufferedImageUtils.hasPartialTransparency(source),
            BufferedImageUtils.countDistinctColors(source));
    }

    /**
     * Indicates how many distinct colors an image has, whether it has partial transparency
     * (alpha channel).
     */
    public static class ColorReductionInfo
    {
        
        /**  Number of distinct colors in the image. */
        public int distinctColors;

        /**  True if the image has partially transparent areas (alpha channel). */
        public boolean hasPartialTransparency;

        /**
         * Instantiates a new color reduction info.
         *
         * @param hasPartialTransparency the has partial transparency
         * @param distinctColors the distinct colors
         */
        public ColorReductionInfo(boolean hasPartialTransparency, int distinctColors)
        {
            this.hasPartialTransparency = hasPartialTransparency;
            this.distinctColors = distinctColors;
        }

        /**
         * Returns true if the image can be saved in a 8-bit indexed color format with
         * 1-bit transparency without quality loss.
         *
         * @return true, if successful
         */
        public boolean canReduceWithoutQualityLoss()
        {
            return !hasPartialTransparency && distinctColors <= MAX_INDEXED_COLORS;
        }
    }
}
