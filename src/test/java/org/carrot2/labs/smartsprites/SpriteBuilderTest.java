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

import static org.assertj.core.api.Assertions.assertThat;
import static org.carrot2.labs.test.Assertions.assertThat;

import com.google.common.collect.Lists;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.carrot2.labs.smartsprites.SmartSpritesParameters.PngDepth;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

/**
 * Test cases for {@link SpriteBuilder}. The test cases read/ write files to the directories contained in the test/
 * directory.
 */
class SpriteBuilderTest extends TestWithMemoryMessageSink {

    /** Builder under tests, initialized in {@link #buildSprites(SmartSpritesParameters)}. */
    private SpriteBuilder spriteBuilder;

    /**
     * Sets the up headless mode.
     */
    @BeforeEach
    void setUpHeadlessMode() {
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * Test no sprite declarations.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testNoSpriteDeclarations() throws FileNotFoundException, IOException {
        final File testDir = testDir("no-sprite-declarations");

        buildSprites(testDir);

        assertThat(processedCss()).doesNotExist();
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test no sprite references.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testNoSpriteReferences() throws FileNotFoundException, IOException {
        final File testDir = testDir("no-sprite-references");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test target sprite image dir not exists.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testTargetSpriteImageDirNotExists() throws FileNotFoundException, IOException {
        final File testDir = testDir("target-sprite-image-dir-not-exists");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img-sprite/sprite.png")).exists();
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        FileUtils.deleteDirectory(new File(testDir, "img-sprite"));
    }

    /**
     * Test simple horizontal sprite.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testSimpleHorizontalSprite() throws FileNotFoundException, IOException {
        final File testDir = testDir("simple-horizontal-sprite");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(17 + 15 + 48, 47));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test simple horizontal sprite important.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testSimpleHorizontalSpriteImportant() throws FileNotFoundException, IOException {
        final File testDir = testDir("simple-horizontal-sprite-important");
        buildSprites(testDir, true);

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(17 + 15 + 48 + 20, 47));
    }

    /**
     * Test layout properties from sprite image directive.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testLayoutPropertiesFromSpriteImageDirective() throws FileNotFoundException, IOException {
        final File testDir = testDir("layout-properties-from-sprite-image-directive");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(17 + 15 + 48 + 3 * (2 + 3), 47 + 5 + 7));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test multiple css files.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testMultipleCssFiles() throws FileNotFoundException, IOException {
        final File testDir = testDir("multiple-css-files");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(css("css/style2-sprite.css")).hasSameTextualContentAs(css("css/style2-expected.css"));
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(17 + 15 + 48, 47));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test large repeat.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testLargeRepeat() throws FileNotFoundException, IOException {
        final File testDir = testDir("large-repeat");
        buildSprites(testDir);

        final String spriteHorizontalPath = "img/sprite-horizontal.png";
        final String spriteVerticalPath = "img/sprite-vertical.png";

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, spriteHorizontalPath)).exists();
        assertThat(new File(testDir, spriteVerticalPath)).exists();
        assertThat(sprite(testDir, spriteHorizontalPath)).hasSize(new Dimension(17 + 15, 16 * 17 /* lcm(16, 17) */));
        assertThat(sprite(testDir, spriteVerticalPath)).hasSize(new Dimension(15 * 17 /* lcm(15, 17) */, 17 + 16));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test missing images.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testMissingImages() throws FileNotFoundException, IOException {
        final File testDir = testDir("missing-images");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(18, 17 + 6 + 5));

        // The unsatisfied sprite references are not removed from the output
        // file, hence we have two warnings
        assertThat(messages).contains(
                new Message(Message.MessageLevel.WARN, Message.MessageType.CANNOT_NOT_LOAD_IMAGE,
                        new File(testDir, "css/style.css").getPath(), 51, new File(testDir, "img/logo.png").getPath(),
                        "Can't read input file!"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.CANNOT_NOT_LOAD_IMAGE,
                        new File(testDir, "css/style-expected.css").getPath(), 51,
                        new File(testDir, "img/logo.png").getPath(), "Can't read input file!"));
    }

    /**
     * Test unsupported individual image format.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testUnsupportedIndividualImageFormat() throws FileNotFoundException, IOException {
        final File testDir = testDir("unsupported-image-format");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).doesNotExist();

        assertThat(messages).contains(
                new Message(Message.MessageLevel.WARN, Message.MessageType.UNSUPPORTED_INDIVIDUAL_IMAGE_FORMAT,
                        new File(testDir, "css/style.css").getPath(), 44, new File(testDir, "img/web.iff").getPath()));
    }

    /**
     * Test unsupported sprite properties.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testUnsupportedSpriteProperties() throws FileNotFoundException, IOException {
        final File testDir = testDir("unsupported-sprite-properties");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(48, 16 + 17 + 47));

        final String styleCssPath = new File(testDir, "css/style.css").getPath();
        assertThat(messages).isEquivalentTo(Message.MessageLevel.WARN,
                new Message(Message.MessageLevel.WARN, Message.MessageType.UNSUPPORTED_PROPERTIES_FOUND, styleCssPath,
                        40, "sprites-layout"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.UNSUPPORTED_PROPERTIES_FOUND, styleCssPath,
                        50, "sprites-margin-top"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.UNSUPPORTED_PROPERTIES_FOUND, styleCssPath,
                        54, "sprites-alignment"));
    }

    /**
     * Test overriding css properties.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testOverridingCssProperties() throws FileNotFoundException, IOException {
        final File testDir = testDir("overriding-css-properties");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(17 + 15 + 48, 47));

        final String styleCssPath = new File(testDir, "css/style.css").getPath();
        assertThat(messages).isEquivalentTo(Message.MessageLevel.WARN,
                new Message(Message.MessageLevel.WARN, Message.MessageType.OVERRIDING_PROPERTY_FOUND, styleCssPath, 46,
                        "background-image", 45),
                new Message(Message.MessageLevel.WARN, Message.MessageType.OVERRIDING_PROPERTY_FOUND, styleCssPath, 57,
                        "background-position", 56));
    }

    /**
     * Test absolute image url.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testAbsoluteImageUrl() throws FileNotFoundException, IOException {
        final File testDir = testDir("absolute-image-url");
        final File documentRootDir = testDir("absolute-image-url/absolute-path");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, documentRootDir, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        final File spriteFile = new File(documentRootDir, "img/sprite.png");
        assertThat(spriteFile).exists();
        assertThat(ImageIO.read(spriteFile)).hasSize(new Dimension(17, 17));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        org.carrot2.util.FileUtils.deleteThrowingExceptions(spriteFile);
    }

    /**
     * Test non default output dir.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testNonDefaultOutputDir() throws FileNotFoundException, IOException {
        final File testDir = testDir("non-default-output-dir");
        final File documentRootDir = testDir("non-default-output-dir");
        final File outputDir = testDir("non-default-output-dir/output-dir");
        org.carrot2.util.FileUtils.mkdirsThrowingExceptions(outputDir);
        buildSprites(filesystemSmartSpritesParameters(testDir, outputDir, documentRootDir, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());

        final File absoluteSpriteFile = new File(documentRootDir, "img/absolute.png");
        assertThat(absoluteSpriteFile).exists();
        assertThat(ImageIO.read(absoluteSpriteFile)).hasSize(new Dimension(17, 17));

        final File relativeSpriteFile = new File(outputDir, "img/relative.png");
        assertThat(relativeSpriteFile).exists();
        assertThat(ImageIO.read(relativeSpriteFile)).hasSize(new Dimension(15, 16));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        FileUtils.deleteDirectory(outputDir);
        org.carrot2.util.FileUtils.deleteThrowingExceptions(absoluteSpriteFile);
    }

    /**
     * Test scaled sprite image.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testScaledSpriteImage() throws FileNotFoundException, IOException {
        final File testDir = testDir("scaled-sprite");
        final File documentRootDir = testDir("scaled-sprite");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, documentRootDir, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());

        final File absoluteSpriteFile = new File(documentRootDir, "img/absolute.png");
        assertThat(absoluteSpriteFile).exists();
        assertThat(ImageIO.read(absoluteSpriteFile)).hasSize(new Dimension(17, 17));

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        org.carrot2.util.FileUtils.deleteThrowingExceptions(absoluteSpriteFile);
    }

    /**
     * Test fractional scaled sprite image.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testFractionalScaledSpriteImage() throws FileNotFoundException, IOException {
        final File testDir = testDir("scaled-sprite-fractional");
        final File documentRootDir = testDir("scaled-sprite-fractional");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, documentRootDir, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());

        final File absoluteSpriteFile = new File(documentRootDir, "img/absolute.png");
        assertThat(absoluteSpriteFile).exists();
        assertThat(ImageIO.read(absoluteSpriteFile)).hasSize(new Dimension(17, 17));

        // assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        assertThat(messages).isEquivalentTo(Message.MessageLevel.WARN,
                new Message(Message.MessageLevel.WARN, Message.MessageType.IMAGE_FRACTIONAL_SCALE_VALUE, null, 44,
                        "../img/web.gif", 8.5f, 8.5f),
                new Message(Message.MessageLevel.WARN, Message.MessageType.FRACTIONAL_SCALE_VALUE, null, 44, "absolute",
                        8.5f, 8.5f));

        org.carrot2.util.FileUtils.deleteThrowingExceptions(absoluteSpriteFile);
    }

    /**
     * Test css output dir.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCssOutputDir() throws FileNotFoundException, IOException {
        final File testDir = testDir("css-output-dir");
        final File rootDir = new File(testDir, "css/sprite").getCanonicalFile();
        final File outputDir = testDir("css-output-dir/output-dir/css");
        org.carrot2.util.FileUtils.mkdirsThrowingExceptions(outputDir);
        buildSprites(filesystemSmartSpritesParameters(rootDir, outputDir, null, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss(new File(rootDir, "style.css")))
                .hasSameTextualContentAs(new File(rootDir, "style-expected.css"));

        final File relativeSpriteFile = new File(outputDir, "../img/relative.png");
        assertThat(relativeSpriteFile).exists();
        assertThat(ImageIO.read(relativeSpriteFile)).hasSize(new Dimension(17 + 15, 17));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        FileUtils.deleteDirectory(outputDir.getParentFile());
    }

    /**
     * Test repeated image references.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testRepeatedImageReferences() throws FileNotFoundException, IOException {
        final File testDir = testDir("repeated-image-references");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(17 + 19, 19));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test indexed color.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testIndexedColor() throws FileNotFoundException, IOException {
        final File testDir = testDir("indexed-color");
        buildSprites(testDir);

        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.gif")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha.png")).isDirectColor()
                .hasTrueAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors.png")).isDirectColor()
                .doesNotHaveAlpha();

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test indexed forced direct color.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testIndexedForcedDirectColor() throws FileNotFoundException, IOException {
        final File testDir = testDir("indexed-color");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, PngDepth.DIRECT,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.gif")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isDirectColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha.png")).isDirectColor()
                .hasTrueAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors.png")).isDirectColor()
                .doesNotHaveAlpha();

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test indexed forced indexed color.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testIndexedForcedIndexedColor() throws FileNotFoundException, IOException {
        final File testDir = testDir("indexed-color");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, PngDepth.INDEXED,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.gif")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha.png")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors.png")).isIndexedColor()
                .doesNotHaveAlpha();

        assertThat(messages).isEquivalentTo(Message.MessageLevel.WARN,
                new Message(Message.MessageLevel.WARN, Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null,
                        61, "full-alpha"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.USING_WHITE_MATTE_COLOR_AS_DEFAULT, null, 61,
                        "full-alpha"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.TOO_MANY_COLORS_FOR_INDEXED_COLOR, null, 68,
                        "many-colors", 293, 255));
    }

    /**
     * Test matte color.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testMatteColor() throws FileNotFoundException, IOException {
        final File testDir = testDir("matte-color");
        buildSprites(testDir);

        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha-m1.png")).isDirectColor()
                .hasTrueAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha-m2.png")).isDirectColor()
                .hasTrueAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha-m3.png")).isDirectColor()
                .hasTrueAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors.png")).isDirectColor()
                .doesNotHaveAlpha();

        assertThat(messages).isEquivalentTo(Message.MessageLevel.WARN,
                new Message(Message.MessageLevel.WARN, Message.MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, null, 48,
                        "full-alpha-m1"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, null, 55,
                        "full-alpha-m2"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, null, 62,
                        "full-alpha-m3"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.IGNORING_MATTE_COLOR_NO_PARTIAL_TRANSPARENCY,
                        null, 69, "bit-alpha"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, null, 76,
                        "many-colors"));
    }

    /**
     * Test matte color forced index.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testMatteColorForcedIndex() throws FileNotFoundException, IOException {
        final File testDir = testDir("matte-color");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, PngDepth.INDEXED,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha-m1.png")).isIndexedColor()
                .hasBitAlpha().isEqualTo(sprite(testDir, "img/sprite-full-alpha-m2.png"))
                .isNotEqualTo(sprite(testDir, "img/sprite-full-alpha-m3.png"));
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha-m2.png")).isIndexedColor()
                .hasBitAlpha().isEqualTo(sprite(testDir, "img/sprite-full-alpha-m1.png"))
                .isNotEqualTo(sprite(testDir, "img/sprite-full-alpha-m3.png"));
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha-m3.png")).isIndexedColor()
                .hasBitAlpha().isNotEqualTo(sprite(testDir, "img/sprite-full-alpha-m1.png"));
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors.png")).isIndexedColor()
                .doesNotHaveAlpha();

        assertThat(messages).isEquivalentTo(Message.MessageLevel.WARN,
                new Message(Message.MessageLevel.WARN, Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null,
                        48, "full-alpha-m1"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null,
                        55, "full-alpha-m2"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null,
                        62, "full-alpha-m3"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.IGNORING_MATTE_COLOR_NO_PARTIAL_TRANSPARENCY,
                        null, 69, "bit-alpha"),
                new Message(Message.MessageLevel.WARN, Message.MessageType.TOO_MANY_COLORS_FOR_INDEXED_COLOR, null, 76,
                        "many-colors", 293, 255));
    }

    /**
     * Test ie 6 indexed color.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testIe6IndexedColor() throws FileNotFoundException, IOException {
        final File testDir = testDir("indexed-color-ie6");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, PngDepth.AUTO, true,
                SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.gif")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha.png")).isDirectColor()
                .hasTrueAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-full-alpha-ie6.png")).isIndexedColor()
                .hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors.png")).isDirectColor()
                .doesNotHaveAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors-bit-alpha-ie6.png"))
                .isIndexedColor().hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors-bit-alpha-no-ie6.png"))
                .isDirectColor().hasBitAlpha();
        org.carrot2.labs.test.Assertions.assertThat(sprite(testDir, "img/sprite-many-colors-bit-alpha.png"))
                .isDirectColor().hasBitAlpha();

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        assertThat(messages).isEquivalentTo(Message.MessageLevel.IE6NOTICE,
                new Message(Message.MessageLevel.IE6NOTICE, Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR,
                        null, 63, "full-alpha"),
                new Message(Message.MessageLevel.IE6NOTICE, Message.MessageType.USING_WHITE_MATTE_COLOR_AS_DEFAULT,
                        null, 63, "full-alpha"),
                new Message(Message.MessageLevel.IE6NOTICE, Message.MessageType.TOO_MANY_COLORS_FOR_INDEXED_COLOR, null,
                        77, "many-colors-bit-alpha", 293, 255));
    }

    /**
     * Test sprite image uid sha 512.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testSpriteImageUidSha512() throws FileNotFoundException, IOException {
        final File testDir = testDir("sprite-image-uid-sha512");
        buildSprites(testDir);

        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(17 + 15, 17));
        assertThat(sprite(testDir, "img/sprite2.png")).hasSize(new Dimension(48, 47));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        // TODO: the generated digest depends on the content of the generated image, which
        // in turn depends on the Java version used.
        //
        // assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
    }

    /**
     * Test sprite image uid sha 512 ie 6.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testSpriteImageUidSha512Ie6() throws FileNotFoundException, IOException {
        final File testDir = testDir("sprite-image-uid-sha512-ie6");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null, MessageLevel.INFO,
                SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX, PngDepth.AUTO, true,
                SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(new File(testDir, "img/sprite-ie6.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(20, 20));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Variable substitution in sprite image path.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void variableSubstitutionInSpriteImagePath() throws FileNotFoundException, IOException {
        final File testDir = testDir("variable-substitution-in-sprite-image-path");
        final String sprite1 = "img/sprite-a1bc9b4fea190b40426b1a294fd1b1271822d59085a1143b2f313f70772d10c32c5c9bca305ebaceeaf135124eec70a5df2d745604916654d90a0a7939334978.png";
        final String sprite2 = "img/7afeb4044f634a1859b08d79aff8802291d911f72dc32e89361d218766072a178084d363549ff83c546f0fb03436aca05f7719812a6c6f8117f253b948399e0f/sprite2.png";

        try {
            buildSprites(testDir);

            assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
            assertThat(new File(testDir, sprite1)).exists();
            assertThat(new File(testDir, sprite2)).exists();
            assertThat(sprite(testDir, sprite1)).hasSize(new Dimension(17 + 15, 17));
            assertThat(sprite(testDir, sprite2)).hasSize(new Dimension(48, 47));
            assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        } finally {
            FileUtils.deleteDirectory(new File(testDir,
                    "img/7afeb4044f634a1859b08d79aff8802291d911f72dc32e89361d218766072a178084d363549ff83c546f0fb03436aca05f7719812a6c6f8117f253b948399e0f"));
        }
    }

    /**
     * Test individual css file does not exist.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testIndividualCssFileDoesNotExist() throws IOException {
        final String path = testDir("does-not-exist").getPath();
        buildSprites(Lists.newArrayList(path));
        assertThat(messages).contains(Message.warn(MessageType.CSS_FILE_DOES_NOT_EXIST, path));
    }

    /**
     * Test directory provided as individual css file.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testDirectoryProvidedAsIndividualCssFile() throws IOException {
        final String path = testDir(".").getPath();
        buildSprites(Lists.newArrayList(path));
        assertThat(messages).contains(Message.warn(MessageType.CSS_PATH_IS_NOT_A_FILE, path));
    }

    /**
     * Test individual css files without output dir.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testIndividualCssFilesWithoutOutputDir() throws IOException {
        final File testDir = testDir("individual-css-files-without-output-dir");
        final File css = new File(testDir, "css/style-sprite.css");
        final File customCss = new File(testDir, "css/custom/style-sprite.css");
        final File otherCss = new File(testDir, "css-other/style-sprite.css");
        final File sprite = new File(testDir, "img/sprite.png");
        try {
            buildSprites(Lists.newArrayList(new File(testDir, "/css/style.css").getPath(),
                    new File(testDir, "css/custom/style.css").getPath(),
                    new File(testDir, "css-other/style.css").getPath()));
            assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
            assertThat(css).hasSameTextualContentAs(new File(testDir, "css/style-expected.css"));
            assertThat(customCss).hasSameTextualContentAs(new File(testDir, "css/custom/style-expected.css"));
            assertThat(otherCss).hasSameTextualContentAs(new File(testDir, "css-other/style-expected.css"));
            assertThat(sprite).exists();
            assertThat(sprite(testDir)).hasSize(new Dimension(17, 17));
        } finally {
            org.carrot2.util.FileUtils.deleteThrowingExceptions(css, otherCss, customCss, sprite);
        }
    }

    /**
     * Test individual css files with output dir.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testIndividualCssFilesWithOutputDir() throws IOException {
        final File testDir = testDir("individual-css-files-with-output-dir");
        final File outputDir = new File(testDir, "output");
        final File css = new File(outputDir, "style-sprite.css");
        final File customCss = new File(outputDir, "custom/style-sprite.css");
        final File otherCss = new File(testDir, "css-other/style-sprite.css");
        final File sprite = new File(testDir, "img/sprite.png");
        try {
            final String otherCssPath = new File(testDir, "css-other/style.css").getPath();
            buildSprites(
                    Lists.newArrayList(new File(testDir, "css/style.css").getPath(),
                            new File(testDir, "css/custom/style.css").getPath(), otherCssPath),
                    new File(testDir, "css").getPath(), outputDir.getPath());
            assertThat(css).hasSameTextualContentAs(new File(testDir, "css/style-expected.css"));
            assertThat(customCss).hasSameTextualContentAs(new File(testDir, "css/custom/style-expected.css"));
            assertThat(otherCss).doesNotExist();
            assertThat(sprite).exists();
            assertThat(sprite(testDir)).hasSize(new Dimension(17, 17));
            assertThat(messages)
                    .contains(Message.warn(MessageType.IGNORING_CSS_FILE_OUTSIDE_OF_ROOT_DIR, otherCssPath));
        } finally {
            FileUtils.deleteDirectory(outputDir);
            org.carrot2.util.FileUtils.deleteThrowingExceptions(sprite);
        }
    }

    /**
     * Test sprite margins.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testSpriteMargins() throws FileNotFoundException, IOException {
        final File testDir = testDir("sprite-margins");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        final String horizontalSpritePath = "img/sprite-horizontal.png";
        assertThat(new File(testDir, horizontalSpritePath)).exists();
        assertThat(sprite(testDir, horizontalSpritePath)).hasSize(new Dimension(48 + 100 + 100 + 48 + 48, 47 * 6));

        final String verticalSpritePath = "img/sprite-vertical.png";
        assertThat(sprite(testDir, verticalSpritePath)).hasSize(new Dimension(48 * 6, 47 + 100 + 100 + 47 + 47));

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test sprite centering.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testSpriteCentering() throws FileNotFoundException, IOException {
        final File testDir = testDir("sprite-centering");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        final String horizontalSpritePath = "img/sprite-horizontal.png";
        assertThat(new File(testDir, horizontalSpritePath)).exists();
        assertThat(sprite(testDir, horizontalSpritePath)).hasSize(new Dimension(48, 47));

        final String verticalSpritePath = "img/sprite-vertical.png";
        assertThat(sprite(testDir, verticalSpritePath)).hasSize(new Dimension(48, 47));

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test case for bug SMARTSPRITES-69.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCssSubfolders() throws FileNotFoundException, IOException {
        final File testDir = testDir("css-in-subfolders");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(css("css/library/common-sprite.css"))
                .hasSameTextualContentAs(css("css/library/common-expected.css"));
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(new Dimension(17, 17 + 16));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        org.carrot2.util.FileUtils.deleteThrowingExceptions(css("css/library/common-sprite.css"));
    }

    /**
     * Test case for bug SMARTSPRITES-78. The bug was caused by the lack of clear contract on the format of paths in
     * {@link SpriteBuilder#buildSprites(java.util.Collection)}.
     *
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void testCssFileApiInvocation() throws FileNotFoundException, IOException {
        final File testDir = testDir("simple-horizontal-sprite");
        final File outputCss = new File(testDir, "css/style-sprite.css");
        final File sprite = new File(testDir, "img/sprite.png");

        try {
            buildSprites(
                    Lists.newArrayList(new File(testDir, "css/style.css").getPath().replace(File.separatorChar, '/')));

            assertThat(outputCss).hasSameTextualContentAs(new File(testDir, "css/style-expected.css"));
            assertThat(sprite).exists();
            assertThat(sprite(testDir)).hasSize(new Dimension(17 + 15 + 48, 47));
            assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        } finally {
            org.carrot2.util.FileUtils.deleteThrowingExceptions(outputCss, sprite);
        }
    }

    /**
     * Clean up.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @AfterEach
    public void cleanUp() throws IOException {
        if (spriteBuilder == null) {
            return;
        }

        // Delete sprite CSS
        final String rootDir = spriteBuilder.parameters.getRootDir();
        if (rootDir == null) {
            return;
        }

        org.carrot2.util.FileUtils.deleteThrowingExceptions(new File(rootDir, "css").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("-sprite");
            }
        }));

        // Delete sprites
        org.carrot2.util.FileUtils.deleteThrowingExceptions(new File(rootDir, "img").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("sprite");
            }
        }));
    }

    /**
     * Test dir.
     *
     * @param test
     *            the test
     *
     * @return the file
     */
    private File testDir(String test) {
        return new File("test/" + test);
    }

    /**
     * Sprite.
     *
     * @param testDir
     *            the test dir
     *
     * @return the buffered image
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private BufferedImage sprite(final File testDir) throws IOException {
        return sprite(testDir, "img/sprite.png");
    }

    /**
     * Sprite.
     *
     * @param testDir
     *            the test dir
     * @param imagePath
     *            the image path
     *
     * @return the buffered image
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private BufferedImage sprite(final File testDir, String imagePath) throws IOException {
        return ImageIO.read(new File(testDir, imagePath));
    }

    /**
     * Expected css.
     *
     * @return the file
     */
    private File expectedCss() {
        return css("css/style-expected.css");
    }

    /**
     * Source css.
     *
     * @return the file
     */
    private File sourceCss() {
        return css("css/style.css");
    }

    /**
     * Processed css.
     *
     * @return the file
     */
    private File processedCss() {
        return processedCss(sourceCss());
    }

    /**
     * Css.
     *
     * @param cssPath
     *            the css path
     *
     * @return the file
     */
    private File css(String cssPath) {
        return new File(spriteBuilder.parameters.getRootDir(), cssPath);
    }

    /**
     * Processed css.
     *
     * @param sourceCss
     *            the source css
     *
     * @return the file
     */
    private File processedCss(File sourceCss) {
        return new File(spriteBuilder.getProcessedCssFile(sourceCss.getPath()));
    }

    /**
     * Builds the sprites.
     *
     * @param dir
     *            the dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void buildSprites(File dir) throws IOException {
        buildSprites(dir, false);
    }

    /**
     * Builds the sprites.
     *
     * @param dir
     *            the dir
     * @param ie6
     *            the ie 6
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void buildSprites(File dir, boolean ie6) throws IOException {
        buildSprites(new SmartSpritesParameters(dir.getPath(), null, null, null,
                SmartSpritesParameters.DEFAULT_LOGGING_LEVEL, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH, ie6,
                SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));
    }

    /**
     * Builds the sprites.
     *
     * @param cssFiles
     *            the css files
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void buildSprites(List<String> cssFiles) throws IOException {
        buildSprites(cssFiles, null, null);
    }

    /**
     * Builds the sprites.
     *
     * @param cssFiles
     *            the css files
     * @param rootDir
     *            the root dir
     * @param outputDir
     *            the output dir
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void buildSprites(List<String> cssFiles, String rootDir, String outputDir) throws IOException {
        buildSprites(new SmartSpritesParameters(rootDir, cssFiles, outputDir, null,
                SmartSpritesParameters.DEFAULT_LOGGING_LEVEL, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
                SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH, SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
                SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));
    }

    /**
     * Builds the sprites.
     *
     * @param parameters
     *            the parameters
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void buildSprites(SmartSpritesParameters parameters) throws IOException {
        spriteBuilder = new SpriteBuilder(parameters, messageLog);
        spriteBuilder.buildSprites();
    }

    /**
     * Filesystem smart sprites parameters.
     *
     * @param rootDir
     *            the root dir
     * @param outputDir
     *            the output dir
     * @param documentRootDir
     *            the document root dir
     * @param logLevel
     *            the log level
     * @param cssFileSuffix
     *            the css file suffix
     * @param spritePngDepth
     *            the sprite png depth
     * @param spritePngIe6
     *            the sprite png ie 6
     * @param cssEncoding
     *            the css encoding
     *
     * @return the smart sprites parameters
     */
    private static SmartSpritesParameters filesystemSmartSpritesParameters(File rootDir, File outputDir,
            File documentRootDir, MessageLevel logLevel, String cssFileSuffix, PngDepth spritePngDepth,
            boolean spritePngIe6, String cssEncoding) {
        return new SmartSpritesParameters(rootDir.getPath(), null, outputDir != null ? outputDir.getPath() : null,
                documentRootDir != null ? documentRootDir.getPath() : null, logLevel, cssFileSuffix, spritePngDepth,
                spritePngIe6, cssEncoding);
    }

}
