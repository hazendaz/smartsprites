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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashSet;
import java.util.Set;

/**
 * Various utility methods for working with {@link BufferedImage}s.
 */
public class BufferedImageUtils
{
    
    /**
     * Returns <code>true</code> if the provided image has partially transparent areas
     * (alpha channel).
     *
     * @param image the image
     * @return true, if successful
     */
    public static boolean hasPartialTransparency(BufferedImage image)
    {
        final Raster alphaRaster = image.getAlphaRaster();
        if (image.getTransparency() != Transparency.TRANSLUCENT || alphaRaster == null)
        {
            return false;
        }

        int [] pixels = alphaRaster.getPixels(0, 0, alphaRaster.getWidth(), alphaRaster
            .getHeight(), (int []) null);
        for (int i : pixels)
        {
            if (i != 0 && i != 255)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns <code>true</code> if the provided image has any kind of transparent areas.
     *
     * @param image the image
     * @return true, if successful
     */
    public static boolean hasTransparency(BufferedImage image)
    {
        final Raster alphaRaster = image.getAlphaRaster();
        if (image.getTransparency() != Transparency.TRANSLUCENT || alphaRaster == null)
        {
            return false;
        }

        int [] pixels = alphaRaster.getPixels(0, 0, alphaRaster.getWidth(), alphaRaster
            .getHeight(), (int []) null);
        for (int i : pixels)
        {
            if (i != 255)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the number of distinct colors (excluding transparency) in the
     * <code>image</code>.
     *
     * @param image the image
     * @return the int
     */
    public static int countDistinctColors(BufferedImage image)
    {
        return getDistinctColors(image).length;
    }

    /**
     * Returns the <code>image</code>'s distinct colors in an RGB format, discarding
     * transparency information.
     *
     * @param image the image
     * @return the distinct colors
     */
    public static int [] getDistinctColors(BufferedImage image)
    {
        return getDistinctColors(image, 0);
    }

    /**
     * Returns the <code>image</code>'s distinct colors in an RGB format, discarding
     * transparency information. Adds <code>padding</code> empty slots at the beginning of
     * the returned array.
     *
     * @param image the image
     * @param padding the padding
     * @return the distinct colors
     */
    public static int [] getDistinctColors(BufferedImage image, int padding)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final Set<Integer> colors = new HashSet<>();

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                final int pixel = image.getRGB(x, y);

                // Count only colors for which alpha is not fully transparent
                if ((pixel & 0xff000000) != 0x00000000)
                {
                    colors.add(Integer.valueOf(pixel & 0x00ffffff));
                }
            }
        }

        final int [] colorMap = new int [colors.size() + padding];
        int index = padding;
        for (Integer color : colors)
        {
            colorMap[index++] = color;
        }

        return colorMap;
    }

    /**
     * Returns a two dimensional array of the <code>image</code>'s RGB values, including
     * transparency.
     *
     * @param image the image
     * @return the rgb
     */
    public static int [][] getRgb(BufferedImage image)
    {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final int [][] rgb = new int [width] [height];

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                rgb[x][y] = image.getRGB(x, y);
            }
        }

        return rgb;
    }

    /**
     * Performs matting of the <code>source</code> image using <code>matteColor</code>.
     * Matting is rendering partial transparencies using solid color as if the original
     * image was put on top of a bitmap filled with <code>matteColor</code>.
     *
     * @param source the source
     * @param matteColor the matte color
     * @return the buffered image
     */
    public static BufferedImage matte(BufferedImage source, Color matteColor)
    {
        final int width = source.getWidth();
        final int height = source.getHeight();

        // A workaround for possibly different custom image types we can get:
        // draw a copy of the image
        final BufferedImage sourceConverted = new BufferedImage(width, height,
            BufferedImage.TYPE_4BYTE_ABGR);
        sourceConverted.getGraphics().drawImage(source, 0, 0, null);

        final BufferedImage matted = new BufferedImage(width, height,
            BufferedImage.TYPE_4BYTE_ABGR);

        final BufferedImage matte = new BufferedImage(width, height,
            BufferedImage.TYPE_4BYTE_ABGR);
        final int matteRgb = matteColor.getRGB();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                matte.setRGB(x, y, matteRgb);
            }
        }

        CompositeContext context = AlphaComposite.DstOver.createContext(matte
            .getColorModel(), sourceConverted.getColorModel(), null);
        context.compose(matte.getRaster(), sourceConverted.getRaster(), matted
            .getRaster());

        return matted;
    }

    /**
     * Draws <code>image</code> on the <code>canvas</code> placing the top left corner of
     * <code>image</code> at <code>x</code> / <code>y</code> offset from the top left
     * corner of <code>canvas</code>.
     *
     * @param image the image
     * @param canvas the canvas
     * @param x the x
     * @param y the y
     */
    public static void drawImage(BufferedImage image, BufferedImage canvas, int x, int y)
    {
        final int [] imgRGB = image.getRGB(0, 0, image.getWidth(), image.getHeight(),
            null, 0, image.getWidth());
        canvas.setRGB(x, y, image.getWidth(), image.getHeight(), imgRGB, 0, image
            .getWidth());
    }

    /**
     * Instantiates a new buffered image utils.
     */
    private BufferedImageUtils()
    {
    }
}
