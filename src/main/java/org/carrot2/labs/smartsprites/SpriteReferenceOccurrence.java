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
package org.carrot2.labs.smartsprites;

import java.awt.image.BufferedImage;

import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageLayout;
import org.carrot2.labs.smartsprites.SpriteLayoutProperties.SpriteAlignment;
import org.carrot2.util.BufferedImageUtils;

/**
 * Represents an occurrence of a {@link SpriteReferenceDirective} in a specific CSS file.
 */
public class SpriteReferenceOccurrence extends SpriteDirectiveOccurrence
{
    /** The directive */
    public final SpriteReferenceDirective spriteReferenceDirective;

    /** CSS file relative path to the individual image to be added to a sprite. */
    public final String imagePath;

    /** Indicates whether the original css property has been marked as important */
    public final boolean important;

    public SpriteReferenceOccurrence(SpriteReferenceDirective spriteReferenceDirective,
        String imageFile, String cssFile, int line, boolean important)
    {
        super(cssFile, line);
        this.spriteReferenceDirective = spriteReferenceDirective;
        this.imagePath = imageFile;
        this.important = important;
    }

    /**
     * Computes the minimum width the individual image will need when rendering.
     */
    public int getRequiredWidth(BufferedImage image, SpriteImageLayout layout)
    {
        if (SpriteAlignment.REPEAT
            .equals(spriteReferenceDirective.spriteLayoutProperties.alignment)
            && SpriteImageLayout.VERTICAL.equals(layout))
        {
            // Ignoring left/right margins on repeated
            // images in vertically stacked sprites
            return image.getWidth();
        }
        else
        {
            return image.getWidth()
                + spriteReferenceDirective.spriteLayoutProperties.marginLeft
                + spriteReferenceDirective.spriteLayoutProperties.marginRight;
        }
    }

    /**
     * Computes the minimum height the individual image will need when rendering.
     */
    public int getRequiredHeight(BufferedImage image, SpriteImageLayout layout)
    {
        if (SpriteAlignment.REPEAT
            .equals(spriteReferenceDirective.spriteLayoutProperties.alignment)
            && SpriteImageLayout.HORIZONTAL.equals(layout))
        {
            // Ignoring top/bottom margins on repeated
            // images in horizontally lined sprites
            return image.getHeight();
        }
        else
        {
            return image.getHeight()
                + spriteReferenceDirective.spriteLayoutProperties.marginTop
                + spriteReferenceDirective.spriteLayoutProperties.marginBottom;
        }
    }

    /**
     * Renders the individual image, including margins and repeats if any.
     * 
     * @param image the individual image as read from the file
     * @param layout the layout the enclosing sprite
     * @param dimension height/width of a horizontal/vertical sprite
     * @return the rendered individual image
     */
    public BufferedImage render(BufferedImage image, SpriteImageLayout layout,
        int dimension)
    {
        final BufferedImage rendered;
        if (SpriteImageLayout.VERTICAL.equals(layout))
        {
            rendered = new BufferedImage(dimension, getRequiredHeight(image, layout),
                BufferedImage.TYPE_4BYTE_ABGR);

            if (SpriteAlignment.LEFT
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment))
            {
                BufferedImageUtils.drawImage(image, rendered,
                    spriteReferenceDirective.spriteLayoutProperties.marginLeft,
                    spriteReferenceDirective.spriteLayoutProperties.marginTop);
            }
            else if (SpriteAlignment.RIGHT
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment))
            {
                BufferedImageUtils.drawImage(image, rendered,
                    dimension
                        - spriteReferenceDirective.spriteLayoutProperties.marginRight
                        - image.getWidth(),
                    spriteReferenceDirective.spriteLayoutProperties.marginTop);
            }
            else if (SpriteAlignment.CENTER
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment))
            {
                BufferedImageUtils.drawImage(image, rendered,
                    (rendered.getWidth() - image.getWidth()) / 2,
                    spriteReferenceDirective.spriteLayoutProperties.marginTop);
            }
            else
            {
                // Repeat, ignoring margin-left and margin-right
                for (int x = 0; x < dimension; x += image.getWidth())
                {
                    BufferedImageUtils.drawImage(image, rendered, x,
                        spriteReferenceDirective.spriteLayoutProperties.marginTop);
                }
            }
        }
        else
        {
            rendered = new BufferedImage(getRequiredWidth(image, layout), dimension,
                BufferedImage.TYPE_4BYTE_ABGR);

            if (SpriteAlignment.TOP
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment))
            {
                BufferedImageUtils.drawImage(image, rendered,
                    spriteReferenceDirective.spriteLayoutProperties.marginLeft,
                    spriteReferenceDirective.spriteLayoutProperties.marginTop);
            }
            else if (SpriteAlignment.BOTTOM
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment))
            {
                BufferedImageUtils.drawImage(image, rendered,
                    spriteReferenceDirective.spriteLayoutProperties.marginLeft, dimension
                        - spriteReferenceDirective.spriteLayoutProperties.marginBottom
                        - image.getHeight());
            }
            else if (SpriteAlignment.CENTER
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment))
            {
                BufferedImageUtils.drawImage(image, rendered,
                    spriteReferenceDirective.spriteLayoutProperties.marginLeft, 
                    (rendered.getHeight() - image.getHeight()) / 2);
            }
            else
            {
                // Repeat, ignoring margin-top and margin-bottom
                for (int y = 0; y < dimension; y += image.getHeight())
                {
                    BufferedImageUtils.drawImage(image, rendered,
                        spriteReferenceDirective.spriteLayoutProperties.marginLeft, y);
                }
            }
        }
        return rendered;
    }

    /**
     * Returns the {@link SpriteReferenceReplacement} corresponding to the occurrence,
     * taking into account the layout the the enclosing sprite and the offset at which the
     * individual image was rendered.
     */
    public SpriteReferenceReplacement buildReplacement(SpriteImageLayout layout,
        int offset)
    {
        if (SpriteImageLayout.VERTICAL.equals(layout))
        {
            String horizontalPosition;
            if (SpriteAlignment.RIGHT.equals(spriteReferenceDirective.spriteLayoutProperties.alignment)) 
            {
                horizontalPosition = "right";
            }
            else if (SpriteAlignment.CENTER
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment)) 
            {
                horizontalPosition = "center";
            }
            else
            {
                horizontalPosition = "left";
            }
            
            return new SpriteReferenceReplacement(
                this,
                offset,
                horizontalPosition);
        }
        else
        {
            String verticalPosition;
            if (SpriteAlignment.BOTTOM
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment)) 
            {
                verticalPosition = "bottom";
            }
            else if (SpriteAlignment.CENTER
                .equals(spriteReferenceDirective.spriteLayoutProperties.alignment))
            {
                verticalPosition = "center";
            }
            else
            {
                verticalPosition = "top";
            }
            return new SpriteReferenceReplacement(
                this,
                verticalPosition,
                offset);
        }
    }
}
