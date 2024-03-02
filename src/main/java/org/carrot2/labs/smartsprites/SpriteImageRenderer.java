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

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.carrot2.labs.smartsprites.SmartSpritesParameters.PngDepth;
import org.carrot2.labs.smartsprites.SpriteImageDirective.Ie6Mode;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageFormat;
import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.util.BufferedImageUtils;
import org.carrot2.util.ColorQuantizer;
import org.carrot2.util.ColorQuantizer.ColorReductionInfo;

/**
 * Applies color quantization to the merged sprite image if required.
 */
public class SpriteImageRenderer {

    /** This builder's configuration. */
    public final SmartSpritesParameters parameters;

    /** This builder's message log. */
    private final MessageLog messageLog;

    /**
     * Instantiates a new sprite image renderer.
     *
     * @param parameters
     *            the parameters
     * @param messageLog
     *            the message log
     */
    SpriteImageRenderer(SmartSpritesParameters parameters, MessageLog messageLog) {
        this.parameters = parameters;
        this.messageLog = messageLog;
    }

    /**
     * If needed, quantizes the image.
     *
     * @param spriteImage
     *            the sprite image
     *
     * @return the buffered image[]
     */
    BufferedImage[] render(SpriteImage spriteImage) {
        final BufferedImage sprite = spriteImage.sprite;
        final SpriteImageDirective spriteImageDirective = spriteImage.spriteImageOccurrence.spriteImageDirective;
        final boolean isPng = spriteImageDirective.format == SpriteImageFormat.PNG;
        final boolean isJpg = spriteImageDirective.format == SpriteImageFormat.JPG;

        final boolean isPngAuto = isPng && parameters.getSpritePngDepth() == PngDepth.AUTO;
        final boolean isPngDirect = isPng && parameters.getSpritePngDepth() == PngDepth.DIRECT;

        final ColorReductionInfo colorReductionInfo = ColorQuantizer.getColorReductionInfo(sprite);
        final boolean canReduceWithoutQualityLoss = colorReductionInfo.canReduceWithoutQualityLoss();

        final BufferedImage[] result = new BufferedImage[2];

        if (isPngDirect || isPngAuto && !canReduceWithoutQualityLoss || isJpg) {
            result[0] = sprite;

            // If needed, generate a quantized version for IE6. If the image has >255
            // colors but doesn't have any transparency, we don't need an IE6 version,
            // because IE6 can handle PNG24 with no transparency correctly.
            if (parameters.isSpritePngIe6() && isPng && BufferedImageUtils.hasTransparency(sprite)
                    && spriteImageDirective.ie6Mode != Ie6Mode.NONE) {
                result[1] = quantize(sprite, spriteImage, colorReductionInfo, MessageLevel.IE6NOTICE);
                spriteImage.hasReducedForIe6 = true;
            } else if (spriteImageDirective.matteColor != null) {
                // Can't or no need to handle indexed color
                messageLog.warning(MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, spriteImageDirective.spriteId);
            }

            return result;
        }
        if (canReduceWithoutQualityLoss) {
            // Can perform reduction to indexed color without data loss
            if (spriteImageDirective.matteColor != null) {
                messageLog.warning(MessageType.IGNORING_MATTE_COLOR_NO_PARTIAL_TRANSPARENCY,
                        spriteImageDirective.spriteId);
            }
            result[0] = ColorQuantizer.reduce(sprite);
        } else {
            result[0] = quantize(sprite, spriteImage, colorReductionInfo, MessageLevel.WARN);
        }
        return result;
    }

    /**
     * Performs quantization, logs the appropriate messages if needed.
     *
     * @param sprite
     *            the sprite
     * @param spriteImage
     *            the sprite image
     * @param colorReductionInfo
     *            the color reduction info
     * @param logLevel
     *            the log level
     *
     * @return the buffered image
     */
    private BufferedImage quantize(BufferedImage sprite, SpriteImage spriteImage,
            final ColorReductionInfo colorReductionInfo, MessageLevel logLevel) {
        final SpriteImageDirective spriteImageDirective = spriteImage.spriteImageOccurrence.spriteImageDirective;

        // Need to quantize
        if (colorReductionInfo.hasPartialTransparency) {
            messageLog.log(logLevel, MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, spriteImageDirective.spriteId);
        } else {
            messageLog.log(logLevel, MessageType.TOO_MANY_COLORS_FOR_INDEXED_COLOR, spriteImageDirective.spriteId,
                    colorReductionInfo.distinctColors, ColorQuantizer.MAX_INDEXED_COLORS);
        }

        final Color matte;
        if (spriteImageDirective.matteColor != null) {
            matte = spriteImageDirective.matteColor;
        } else {
            if (colorReductionInfo.hasPartialTransparency) {
                messageLog.log(logLevel, MessageType.USING_WHITE_MATTE_COLOR_AS_DEFAULT, spriteImageDirective.spriteId);
            }
            matte = Color.WHITE;
        }

        return ColorQuantizer.quantize(sprite, matte);
    }
}
