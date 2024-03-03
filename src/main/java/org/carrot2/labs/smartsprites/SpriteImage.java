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

import com.google.common.hash.Hashing;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteUidType;

/**
 * A merged sprite image consisting of a number of individual images.
 */
public class SpriteImage {

    /** The rendered sprite image bitmap. */
    public final BufferedImage sprite;

    /**
     * All {@link SpriteReferenceReplacement}s corresponding to the individual images this sprite image consists of.
     */
    public final Map<SpriteReferenceOccurrence, SpriteReferenceReplacement> spriteReferenceReplacements;

    /**
     * {@link SpriteImageOccurrence} for which this {@link SpriteImage} has been built.
     */
    public final SpriteImageOccurrence spriteImageOccurrence;

    /**
     * The {@link SpriteImageDirective#imagePath} with variables resolved.
     */
    public String resolvedPath;

    /**
     * The width of the final sprite.
     */
    public int spriteWidth;

    /**
     * The height of the final sprite.
     */
    public int spriteHeight;

    /**
     * The scale to apply to the final sprite's background-size.
     */
    public float scaleRatio;

    /** The Constant SPRITE_VARIABLE. */
    private static final Pattern SPRITE_VARIABLE = Pattern.compile("${sprite}", Pattern.LITERAL);

    /**
     * Instantiates a new sprite image.
     *
     * @param sprite
     *            the sprite
     * @param spriteImageOccurrence
     *            the sprite image occurrence
     * @param spriteReplacements
     *            the sprite replacements
     * @param width
     *            the width
     * @param height
     *            the height
     * @param scale
     *            the scale
     */
    public SpriteImage(BufferedImage sprite, SpriteImageOccurrence spriteImageOccurrence,
            Map<SpriteReferenceOccurrence, SpriteReferenceReplacement> spriteReplacements, int width, int height,
            float scale) {
        this.sprite = sprite;
        this.spriteReferenceReplacements = spriteReplacements;
        this.spriteImageOccurrence = spriteImageOccurrence;
        this.spriteWidth = width;
        this.spriteHeight = height;
        this.scaleRatio = scale;

        for (SpriteReferenceReplacement replacement : spriteReplacements.values()) {
            replacement.spriteImage = this;
        }
    }

    /**
     * Resolve image path.
     *
     * @param image
     *            the image
     * @param timestamp
     *            the timestamp
     *
     * @return the string
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    String resolveImagePath(byte[] image, String timestamp) throws IOException {
        String imagePath = spriteImageOccurrence.spriteImageDirective.imagePath;

        // Backwards compatibility: if there are no place holders in the path
        // and the UID type is defined, append the UID as a query string just like
        // the previous versions did. To be removed in 0.4.0.
        if (spriteImageOccurrence.spriteImageDirective.uidType != SpriteUidType.NONE
                && !SpriteUidType.SHA512.pattern.matcher(imagePath).find()
                && !SpriteUidType.DATE.pattern.matcher(imagePath).find()) {
            imagePath += "?${" + spriteImageOccurrence.spriteImageDirective.uidType.toString() + "}";
        }

        // Resolve SHA512 hash
        Matcher sha512Matcher = SpriteUidType.SHA512.pattern.matcher(imagePath);
        if (sha512Matcher.find()) {
            // Compute SHA512 only when necessary
            imagePath = sha512Matcher.replaceAll(computeSha512(image));
        }

        // Resolve timestamp
        imagePath = SpriteUidType.DATE.pattern.matcher(imagePath).replaceAll(timestamp);

        // Resolve sprite name
        imagePath = SPRITE_VARIABLE.matcher(imagePath).replaceAll(spriteImageOccurrence.spriteImageDirective.spriteId);

        this.resolvedPath = imagePath;
        return this.resolvedPath;
    }

    /**
     * Computes Sha512 using guava.
     *
     * @param image
     *            the image
     *
     * @return the string
     */
    private static String computeSha512(byte[] image) {
        return Hashing.sha512().hashBytes(image).toString();
    }
}
