/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites;

/**
 * A base class for SmartSprites directives.
 */
public class SpriteDirectiveOccurrence {

    /** CSS file in which this directive was found. */
    public final String cssFile;

    /** Line number on which the directive occurred. */
    public final int line;

    /**
     * Instantiates a new sprite directive occurrence.
     *
     * @param cssFile
     *            the css file
     * @param line
     *            the line
     */
    public SpriteDirectiveOccurrence(String cssFile, int line) {
        this.cssFile = cssFile;
        this.line = line;
    }
}
