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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageFormat;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteImageLayout;
import org.carrot2.labs.smartsprites.SpriteLayoutProperties.SpriteAlignment;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.labs.smartsprites.resource.ResourceHandler;
import org.carrot2.util.BufferedImageUtils;
import org.carrot2.util.FileUtils;

/**
 * Lays out and builds sprite images based on the collected SmartSprites directives.
 */
public class SpriteImageBuilder {

    /** This builder's configuration. */
    public final SmartSpritesParameters parameters;

    /** This builder's message log. */
    private final MessageLog messageLog;

    /** Image merger for this builder. */
    private SpriteImageRenderer spriteImageRenderer;

    /** The resource handler. */
    private ResourceHandler resourceHandler;

    /**
     * A timestamp to use for timestamp-based sprite image UIDs. We need this time stamp as a field to make sure the
     * timestamp is the same for all sprite image replacements.
     */
    private Instant timestamp;

    /**
     * Creates a {@link SpriteImageBuilder} with the provided parameters and log.
     *
     * @param parameters
     *            the parameters
     * @param messageLog
     *            the message log
     * @param resourceHandler
     *            the resource handler
     */
    SpriteImageBuilder(SmartSpritesParameters parameters, MessageLog messageLog, ResourceHandler resourceHandler) {
        this.messageLog = messageLog;
        this.parameters = parameters;
        this.resourceHandler = resourceHandler;
        spriteImageRenderer = new SpriteImageRenderer(parameters, messageLog);
    }

    /**
     * Builds all sprite images based on the collected directives.
     *
     * @param spriteImageOccurrencesBySpriteId
     *            the sprite image occurrences by sprite id
     * @param spriteReferenceOccurrencesBySpriteId
     *            the sprite reference occurrences by sprite id
     *
     * @return the multimap
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Multimap<String, SpriteReferenceReplacement> buildSpriteImages(
            Map<String, SpriteImageOccurrence> spriteImageOccurrencesBySpriteId,
            Multimap<String, SpriteReferenceOccurrence> spriteReferenceOccurrencesBySpriteId) throws IOException {
        timestamp = Instant.now();

        final Multimap<String, SpriteReferenceReplacement> spriteReplacementsByFile = LinkedListMultimap.create();
        for (final Map.Entry<String, Collection<SpriteReferenceOccurrence>> spriteReferenceOccurrences : spriteReferenceOccurrencesBySpriteId
                .asMap().entrySet()) {
            final Map<SpriteReferenceOccurrence, SpriteReferenceReplacement> spriteReferenceReplacements = buildSpriteReplacements(
                    spriteImageOccurrencesBySpriteId.get(spriteReferenceOccurrences.getKey()),
                    spriteReferenceOccurrences.getValue());

            for (final SpriteReferenceReplacement spriteReferenceReplacement : spriteReferenceReplacements.values()) {
                spriteReplacementsByFile.put(spriteReferenceReplacement.spriteReferenceOccurrence.cssFile,
                        spriteReferenceReplacement);
            }
        }

        return spriteReplacementsByFile;
    }

    /**
     * Builds sprite image for a single sprite image directive.
     *
     * @param spriteImageOccurrence
     *            the sprite image occurrence
     * @param spriteReferenceOccurrences
     *            the sprite reference occurrences
     *
     * @return the map
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    Map<SpriteReferenceOccurrence, SpriteReferenceReplacement> buildSpriteReplacements(
            SpriteImageOccurrence spriteImageOccurrence,
            Collection<SpriteReferenceOccurrence> spriteReferenceOccurrences) throws IOException {
        // Load images into memory. TODO: impose some limit here?
        final Map<SpriteReferenceOccurrence, BufferedImage> images = new LinkedHashMap<>();
        for (final SpriteReferenceOccurrence spriteReferenceOccurrence : spriteReferenceOccurrences) {
            messageLog.setCssFile(spriteReferenceOccurrence.cssFile);
            messageLog.setLine(spriteReferenceOccurrence.line);

            final String realImagePath = resourceHandler.getResourcePath(spriteReferenceOccurrence.cssFile,
                    spriteReferenceOccurrence.imagePath);

            try (InputStream is = resourceHandler.getResourceAsInputStream(realImagePath)) {

                // Load image
                if (is == null) {
                    messageLog.warning(MessageType.CANNOT_NOT_LOAD_IMAGE, realImagePath, "Can't read input file!");
                    continue;
                }
                messageLog.info(MessageType.READING_IMAGE, realImagePath);
                final BufferedImage image = ImageIO.read(is);
                if (image != null) {
                    images.put(spriteReferenceOccurrence, image);
                } else {
                    messageLog.warning(MessageType.UNSUPPORTED_INDIVIDUAL_IMAGE_FORMAT, realImagePath);
                }
            } catch (final IOException e) {
                messageLog.warning(MessageType.CANNOT_NOT_LOAD_IMAGE, realImagePath, "Can't read input file!");
                continue;
            }

            messageLog.setCssFile(null);
        }

        // Build the sprite image bitmap
        final SpriteImage spriteImage = SpriteImageBuilder.buildSpriteImage(spriteImageOccurrence, images, messageLog);
        if (spriteImage == null) {
            return Collections.<SpriteReferenceOccurrence, SpriteReferenceReplacement> emptyMap();
        }

        // Render the sprite into the required formats, perform quantization if needed
        final BufferedImage[] mergedImages = spriteImageRenderer.render(spriteImage);

        writeSprite(spriteImage, mergedImages[0]);

        return spriteImage.spriteReferenceReplacements;
    }

    /**
     * Writes sprite image to the disk.
     *
     * @param spriteImage
     *            the sprite image
     * @param mergedImage
     *            the merged image
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void writeSprite(SpriteImage spriteImage, final BufferedImage mergedImage) throws IOException {
        final SpriteImageOccurrence spriteImageOccurrence = spriteImage.spriteImageOccurrence;
        final SpriteImageDirective spriteImageDirective = spriteImageOccurrence.spriteImageDirective;

        // Write the image to a byte array first. We need the data to compute an sha512 hash.
        final ByteArrayOutputStream spriteImageByteArrayOutputStream = new ByteArrayOutputStream();

        // If writing to a JPEG, we need to make a 3-byte-encoded image
        final BufferedImage imageToWrite;
        if (SpriteImageFormat.JPG.equals(spriteImageDirective.format)) {
            imageToWrite = new BufferedImage(mergedImage.getWidth(), mergedImage.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            BufferedImageUtils.drawImage(mergedImage, imageToWrite, 0, 0);
        } else {
            imageToWrite = mergedImage;
        }

        try {
            ImageIO.write(imageToWrite, spriteImageDirective.format.toString(), spriteImageByteArrayOutputStream);
        } catch (IOException e) {
            // Unlikely to happen.
            messageLog.warning(MessageType.CANNOT_WRITE_SPRITE_IMAGE, spriteImageDirective.imagePath, e.getMessage());
        }

        // Build file name
        byte[] spriteImageBytes = spriteImageByteArrayOutputStream.toByteArray();
        String resolvedImagePath = spriteImage.resolveImagePath(spriteImageBytes, timestamp.toString());
        if (resolvedImagePath.indexOf('?') >= 0) {
            resolvedImagePath = resolvedImagePath.substring(0, resolvedImagePath.indexOf('?'));
        }

        // Save the image to the disk
        final String mergedImageFile = getImageFile(spriteImageOccurrence.cssFile, resolvedImagePath);

        try (OutputStream spriteImageOutputStream = resourceHandler.getResourceAsOutputStream(mergedImageFile)) {
            messageLog.info(MessageType.WRITING_SPRITE_IMAGE, mergedImage.getWidth(), mergedImage.getHeight(),
                    spriteImageDirective.spriteId, mergedImageFile);

            spriteImageOutputStream.write(spriteImageBytes);
        } catch (final IOException e) {
            messageLog.warning(MessageType.CANNOT_WRITE_SPRITE_IMAGE, mergedImageFile, e.getMessage());
        }
    }

    /**
     * Computes the image path. If the imagePath is relative, it's taken relative to the cssFile. If imagePath is
     * absolute (starts with '/') and documentRootDir is not null, it's taken relative to documentRootDir.
     *
     * @param cssFile
     *            the css file
     * @param imagePath
     *            the image path
     *
     * @return the image file
     */
    String getImageFile(String cssFile, String imagePath) {
        // Absolute path resolution is done by resourceHandler
        final String path = resourceHandler.getResourcePath(cssFile, imagePath);

        // Just handle the root directory changing
        if (!imagePath.startsWith("/") && parameters.hasOutputDir()) {
            return FileUtils.changeRoot(path, parameters.getRootDir(), parameters.getOutputDir());
        }
        return path;
    }

    /**
     * Calculates total dimensions and lays out a single sprite image.
     *
     * @param spriteImageOccurrence
     *            the sprite image occurrence
     * @param images
     *            the images
     * @param messageLog
     *            the message log
     *
     * @return the sprite image
     */
    static SpriteImage buildSpriteImage(SpriteImageOccurrence spriteImageOccurrence,
            Map<SpriteReferenceOccurrence, BufferedImage> images, MessageLog messageLog) {
        // First find the least common multiple of the images with 'repeat' alignment
        final SpriteImageLayout layout = spriteImageOccurrence.spriteImageDirective.layout;
        final float spriteScale = spriteImageOccurrence.spriteImageDirective.scaleRatio;
        final int leastCommonMultiple = SpriteImageBuilder.calculateLeastCommonMultiple(images, layout);

        // Compute sprite dimension (width for vertical, height for horizontal sprites)
        final boolean vertical = layout.equals(SpriteImageLayout.VERTICAL);
        int dimension = leastCommonMultiple;
        for (final Map.Entry<SpriteReferenceOccurrence, BufferedImage> entry : images.entrySet()) {
            final BufferedImage image = entry.getValue();
            final SpriteReferenceOccurrence spriteReferenceOccurrence = entry.getKey();

            // Compute dimensions
            dimension = Math.max(dimension, vertical ? spriteReferenceOccurrence.getRequiredWidth(image, layout)
                    : spriteReferenceOccurrence.getRequiredHeight(image, layout));
        }

        // Correct for least common multiple
        if (dimension % leastCommonMultiple != 0) {
            dimension += leastCommonMultiple - dimension % leastCommonMultiple;
        }

        // Compute the other sprite dimension.
        int currentOffset = 0;
        final Map<SpriteReferenceOccurrence, SpriteReferenceReplacement> spriteReplacements = new LinkedHashMap<>();
        final Map<BufferedImageEqualsWrapper, Integer> renderedImageToOffset = new LinkedHashMap<>();
        for (final Map.Entry<SpriteReferenceOccurrence, BufferedImage> entry : images.entrySet()) {
            final SpriteReferenceOccurrence spriteReferenceOccurrence = entry.getKey();
            final BufferedImage image = entry.getValue();

            final BufferedImage rendered = spriteReferenceOccurrence.render(image, layout, dimension);
            final BufferedImageEqualsWrapper imageWrapper = new BufferedImageEqualsWrapper(rendered);
            Integer imageOffset = renderedImageToOffset.get(imageWrapper);
            if (imageOffset == null) {
                // Draw a new image
                imageOffset = currentOffset;
                renderedImageToOffset.put(imageWrapper, imageOffset);
                currentOffset += vertical ? rendered.getHeight() : rendered.getWidth();
            }

            final float scaledImageWidth = spriteReferenceOccurrence.getRequiredWidth(image, layout) / spriteScale;
            final float scaledImageHeight = spriteReferenceOccurrence.getRequiredHeight(image, layout) / spriteScale;
            if (Math.round(scaledImageWidth) != scaledImageWidth
                    || Math.round(scaledImageHeight) != scaledImageHeight) {
                messageLog.warning(MessageType.IMAGE_FRACTIONAL_SCALE_VALUE, spriteReferenceOccurrence.imagePath,
                        scaledImageWidth, scaledImageHeight);
            }

            final int adjustedImageOffset = Math.round(imageOffset / spriteScale);
            spriteReplacements.put(spriteReferenceOccurrence,
                    spriteReferenceOccurrence.buildReplacement(layout, adjustedImageOffset));
        }

        // Render the sprite image and build sprite reference replacements
        final int spriteWidth = vertical ? dimension : currentOffset;
        final int spriteHeight = vertical ? currentOffset : dimension;
        if (spriteWidth == 0 || spriteHeight == 0) {
            return null;
        }

        final float scaledWidth = spriteWidth / spriteScale;
        final float scaledHeight = spriteHeight / spriteScale;
        if (Math.round(scaledWidth) != scaledWidth || Math.round(scaledHeight) != scaledHeight) {
            messageLog.warning(MessageType.FRACTIONAL_SCALE_VALUE, spriteImageOccurrence.spriteImageDirective.spriteId,
                    scaledWidth, scaledHeight);
        }

        final BufferedImage sprite = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_4BYTE_ABGR);

        for (final Map.Entry<BufferedImageEqualsWrapper, Integer> entry : renderedImageToOffset.entrySet()) {

            BufferedImageUtils.drawImage(entry.getKey().image, sprite, vertical ? 0 : entry.getValue(),
                    vertical ? entry.getValue() : 0);
        }

        return new SpriteImage(sprite, spriteImageOccurrence, spriteReplacements, spriteWidth, spriteHeight,
                spriteScale);
    }

    /**
     * Calculates the width/ height of "repeated" sprites.
     *
     * @param images
     *            the images
     * @param layout
     *            the layout
     *
     * @return the int
     */
    static int calculateLeastCommonMultiple(Map<SpriteReferenceOccurrence, BufferedImage> images,
            SpriteImageLayout layout) {
        int leastCommonMultiple = 1;
        for (final Map.Entry<SpriteReferenceOccurrence, BufferedImage> entry : images.entrySet()) {
            final BufferedImage image = entry.getValue();
            final SpriteReferenceOccurrence spriteReferenceOccurrence = entry.getKey();
            if (image != null && SpriteAlignment.REPEAT
                    .equals(spriteReferenceOccurrence.spriteReferenceDirective.spriteLayoutProperties.alignment)) {
                if (SpriteImageLayout.VERTICAL.equals(layout)) {
                    leastCommonMultiple = ArithmeticUtils.lcm(leastCommonMultiple,
                            spriteReferenceOccurrence.getRequiredWidth(image, layout));
                } else {
                    leastCommonMultiple = ArithmeticUtils.lcm(leastCommonMultiple,
                            spriteReferenceOccurrence.getRequiredHeight(image, layout));
                }
            }
        }
        return leastCommonMultiple;
    }

    /**
     * Groups {@link SpriteReferenceReplacement}s by the line number of their corresponding directives.
     *
     * @param spriteReferenceReplacements
     *            the sprite reference replacements
     *
     * @return the sprite replacements by line number
     */
    static Map<Integer, SpriteReferenceReplacement> getSpriteReplacementsByLineNumber(
            Collection<SpriteReferenceReplacement> spriteReferenceReplacements) {
        final Map<Integer, SpriteReferenceReplacement> result = new HashMap<>();

        for (final SpriteReferenceReplacement spriteReferenceReplacement : spriteReferenceReplacements) {
            result.put(spriteReferenceReplacement.spriteReferenceOccurrence.line, spriteReferenceReplacement);
        }

        return result;
    }

    /**
     * Groups {@link SpriteImageOccurrence}s by the line number of their corresponding directives.
     *
     * @param spriteImageOccurrences
     *            the sprite image occurrences
     *
     * @return the sprite image occurrences by line number
     */
    static Map<Integer, SpriteImageOccurrence> getSpriteImageOccurrencesByLineNumber(
            Collection<SpriteImageOccurrence> spriteImageOccurrences) {
        final Map<Integer, SpriteImageOccurrence> result = new HashMap<>();

        for (final SpriteImageOccurrence spriteImageOccurrence : spriteImageOccurrences) {
            result.put(spriteImageOccurrence.line, spriteImageOccurrence);
        }

        return result;
    }

    /**
     * A wrapper that implements content-aware {@link Object#equals(Object)} and {@link Object#hashCode()} on
     * {@link BufferedImage}s.
     */
    static final class BufferedImageEqualsWrapper {

        /** The image. */
        BufferedImage image;

        /**
         * Instantiates a new buffered image equals wrapper.
         *
         * @param image
         *            the image
         */
        BufferedImageEqualsWrapper(BufferedImage image) {
            this.image = image;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BufferedImageEqualsWrapper)) {
                return false;
            }

            if (obj == this) {
                return true;
            }

            final BufferedImage other = ((BufferedImageEqualsWrapper) obj).image;

            boolean equal = other.getWidth() == image.getWidth() && other.getHeight() == image.getHeight()
                    && other.getType() == image.getType();

            if (equal) {
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        if (ignoreFullTransparency(image.getRGB(x, y)) != ignoreFullTransparency(other.getRGB(x, y))) {
                            return false;
                        }
                    }
                }
            }

            return equal;
        }

        @Override
        public int hashCode() {
            if (image == null) {
                return 0;
            }

            int hash = image.getWidth() ^ image.getHeight() << 16;

            // Computes the hashCode based on an 4 x 4 to 7 x 7 grid of image's pixels
            final int xIncrement = image.getWidth() > 7 ? image.getWidth() >> 2 : 1;
            final int yIncrement = image.getHeight() > 7 ? image.getHeight() >> 2 : 1;

            for (int y = 0; y < image.getHeight(); y += yIncrement) {
                for (int x = 0; x < image.getWidth(); x += xIncrement) {
                    hash ^= ignoreFullTransparency(image.getRGB(x, y));
                }
            }

            return hash;
        }

        /**
         * If the pixel is fully transparent, returns 0. Otherwise, returns the pixel. This is useful in
         * {@link #equals(Object)} and {@link #hashCode()} to ignore pixels that have different colors but are invisible
         * anyway because of full transparency.
         *
         * @param pixel
         *            the pixel
         *
         * @return the int
         */
        private static int ignoreFullTransparency(int pixel) {
            if ((pixel & 0xff000000) == 0x00000000) {
                return 0;
            }
            return pixel;
        }
    }
}
