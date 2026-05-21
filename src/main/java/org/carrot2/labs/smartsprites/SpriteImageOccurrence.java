/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites;

/**
 * Describes an occurrence of a {@link SpriteImageDirective} in a specific CSS file.
 */
public class SpriteImageOccurrence extends SpriteDirectiveOccurrence {

    /** The sprite image directive. */
    public final SpriteImageDirective spriteImageDirective;

    /**
     * Instantiates a new sprite image occurrence.
     *
     * @param spriteImageDirective
     *            the sprite image directive
     * @param cssFile
     *            the css file
     * @param line
     *            the line
     */
    public SpriteImageOccurrence(SpriteImageDirective spriteImageDirective, String cssFile, int line) {
        super(cssFile, line);
        this.spriteImageDirective = spriteImageDirective;
    }
}
