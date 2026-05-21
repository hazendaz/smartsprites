/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Base class for tests involving {@link BufferedImage}s.
 */
public class BufferedImageTestBase {

    /**
     * Image.
     *
     * @param fileName
     *            the file name
     *
     * @return the buffered image
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected BufferedImage image(String fileName) throws IOException {
        return ImageIO.read(BufferedImageTestBase.class.getResourceAsStream("/images/" + fileName));
    }
}
