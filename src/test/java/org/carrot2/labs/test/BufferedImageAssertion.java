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
package org.carrot2.labs.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.carrot2.labs.smartsprites.css.CssProperty;
import org.carrot2.util.BufferedImageUtils;

/**
 * Assertions on instances of {@link CssProperty}.
 */
public class BufferedImageAssertion {

    /** The actual property. */
    private final BufferedImage actual;

    /** Assertion description. */
    private String description = "image";

    /**
     * Creates a {@link BufferedImage} assertion.
     *
     * @param actual
     *            the actual
     */
    public BufferedImageAssertion(BufferedImage actual) {
        this.actual = actual;
    }

    /**
     * Asserts that the image is an indexed color image.
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion isIndexedColor() {
        assertThat(isIndexed()).as(description + ".indexed").isTrue();
        return this;
    }

    /**
     * Asserts that the image is a direct color image.
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion isDirectColor() {
        assertThat(!isIndexed()).as(description + ".direct").isTrue();
        return this;
    }

    /**
     * Checks if is indexed.
     *
     * @return true, if is indexed
     */
    private boolean isIndexed() {
        return actual.getType() == BufferedImage.TYPE_BYTE_INDEXED
                || actual.getType() == BufferedImage.TYPE_BYTE_BINARY;
    }

    /**
     * Asserts that the image has bit (0/1) alpha areas.
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion hasBitAlpha() {
        final int[][] rgb = BufferedImageUtils.getRgb(actual);
        int width = actual.getWidth();
        int height = actual.getHeight();
        boolean hasBitAlpha = false;

        exit: for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int alpha = (rgb[x][y] & 0xff000000) >> 24;
                if (alpha == 0) {
                    hasBitAlpha = true;
                }

                if (alpha > 0 && alpha != 255) {
                    hasBitAlpha = false;
                    break exit;
                }
            }
        }

        assertThat(hasBitAlpha).as(description + ".hasBitAlpha").isTrue();
        return this;
    }

    /**
     * Asserts that the image has true (0..1) alpha areas.
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion hasTrueAlpha() {
        final int[][] rgb = BufferedImageUtils.getRgb(actual);
        int width = actual.getWidth();
        int height = actual.getHeight();
        boolean hasTrueAlpha = false;

        exit: for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int alpha = (rgb[x][y] & 0xff000000) >> 24;
                if (alpha > 0 && alpha < 255) {
                    hasTrueAlpha = true;
                    break exit;
                }
            }
        }

        assertThat(hasTrueAlpha).as(description + ".hasTrueAlpha").isTrue();
        return this;
    }

    /**
     * Asserts that the image has or doesn't have any transparent areas.
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion doesNotHaveAlpha() {
        final int[][] rgb = BufferedImageUtils.getRgb(actual);
        int width = actual.getWidth();
        int height = actual.getHeight();
        boolean hasAlpha = false;

        exit: for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((rgb[x][y] & 0xff000000) != 0xff000000) {
                    hasAlpha = true;
                    break exit;
                }
            }
        }

        assertThat(hasAlpha).as(description + ".hasAlpha").isFalse();
        return this;
    }

    /**
     * Asserts that the image has the specified number of colors, fully transparent pixels are not counted.
     *
     * @param colors
     *            the colors
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion hasNumberOfColorsEqualTo(int colors) {
        assertThat(BufferedImageUtils.countDistinctColors(actual)).as(description + ".colors").isEqualTo(colors);
        return this;
    }

    /**
     * Checks if is equal to.
     *
     * @param expected
     *            the expected
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion isEqualTo(BufferedImage expected) {
        assertThat(compareImage(expected)).isTrue();
        return this;
    }

    /**
     * Checks if is not equal to.
     *
     * @param expected
     *            the expected
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion isNotEqualTo(BufferedImage expected) {
        assertThat(actual).isNotEqualTo(expected);
        return this;
    }

    /**
     * As.
     *
     * @param description
     *            the description
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion as(String description) {
        this.description = description;
        return this;
    }

    /**
     * Checks for size.
     *
     * @param dimension
     *            the dimension
     *
     * @return the buffered image assertion
     */
    public BufferedImageAssertion hasSize(Dimension dimension) {
        assertThat(new Dimension(actual.getWidth(), actual.getHeight())).isEqualTo(dimension);
        return this;
    }

    /**
     * Compare image.
     *
     * @param expected
     *            the expected
     *
     * @return true, if successful
     */
    private boolean compareImage(BufferedImage expected) {
        if (actual.getWidth() == expected.getWidth() && actual.getHeight() == expected.getHeight()) {
            for (int x = 0; x < actual.getWidth(); x++) {
                for (int y = 0; y < actual.getHeight(); y++) {
                    if (actual.getRGB(x, y) != expected.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
