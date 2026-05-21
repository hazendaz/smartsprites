/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites;

/**
 * Represents the replacement that will be made for an individual {@link SpriteReferenceOccurrence}.
 */
public class SpriteReferenceReplacement {

    /** Properties of the sprite image this replacement refers to. */
    public SpriteImage spriteImage;

    /** The {@link SpriteReferenceOccurrence} this instance refers to. */
    public final SpriteReferenceOccurrence spriteReferenceOccurrence;

    /** String representation of the horizontal position of this sprite replacement. */
    public final String horizontalPositionString;

    /** Numeric representation of the horizontal position of this sprite replacement. */
    public final int horizontalPosition;

    /** String representation of the vertical position of this sprite replacement. */
    public final String verticalPositionString;

    /** Numeric representation of the vertical position of this sprite replacement. */
    public final int verticalPosition;

    /**
     * Instantiates a new sprite reference replacement.
     *
     * @param spriteReferenceOccurrence
     *            the sprite reference occurrence
     * @param verticalPosition
     *            the vertical position
     * @param horizontalPosition
     *            the horizontal position
     */
    public SpriteReferenceReplacement(SpriteReferenceOccurrence spriteReferenceOccurrence, int verticalPosition,
            String horizontalPosition) {
        this.spriteReferenceOccurrence = spriteReferenceOccurrence;
        this.horizontalPosition = -1;
        this.horizontalPositionString = horizontalPosition;
        this.verticalPosition = verticalPosition;
        this.verticalPositionString = "-" + verticalPosition + "px";
    }

    /**
     * Instantiates a new sprite reference replacement.
     *
     * @param spriteReferenceOccurrence
     *            the sprite reference occurrence
     * @param verticalPosition
     *            the vertical position
     * @param horizontalPosition
     *            the horizontal position
     */
    public SpriteReferenceReplacement(SpriteReferenceOccurrence spriteReferenceOccurrence, String verticalPosition,
            int horizontalPosition) {
        this.spriteReferenceOccurrence = spriteReferenceOccurrence;
        this.horizontalPosition = horizontalPosition;
        this.horizontalPositionString = "-" + horizontalPosition + "px";
        this.verticalPosition = -1;
        this.verticalPositionString = verticalPosition;
    }
}
