package org.carrot2.labs.smartsprites;

import static org.carrot2.labs.test.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;
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

import com.google.common.collect.Lists;

/**
 * Test cases for {@link SpriteBuilder}. The test cases read/ write files to the
 * directories contained in the test/ directory.
 */
class SpriteBuilderTest extends TestWithMemoryMessageSink
{
    /** Builder under tests, initialized in {@link #buildSprites(SmartSpritesParameters)} */
    private SpriteBuilder spriteBuilder;

    @BeforeEach
    void setUpHeadlessMode()
    {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void testNoSpriteDeclarations() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("no-sprite-declarations");

        buildSprites(testDir);

        assertThat(processedCss()).doesNotExist();
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testNoSpriteReferences() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("no-sprite-references");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testTargetSpriteImageDirNotExists() throws FileNotFoundException,
        IOException
    {
        final File testDir = testDir("target-sprite-image-dir-not-exists");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img-sprite/sprite.png")).exists();
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        FileUtils.deleteDirectory(new File(testDir, "img-sprite"));
    }

    @Test
    void testSimpleHorizontalSprite() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("simple-horizontal-sprite");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(17 + 15 + 48, 47));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testSimpleHorizontalSpriteImportant() throws FileNotFoundException,
        IOException
    {
        final File testDir = testDir("simple-horizontal-sprite-important");
        buildSprites(testDir, true);

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(17 + 15 + 48 + 20, 47));
    }

    @Test
    void testLayoutPropertiesFromSpriteImageDirective()
        throws FileNotFoundException, IOException
    {
        final File testDir = testDir("layout-properties-from-sprite-image-directive");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(17 + 15 + 48 + 3 * (2 + 3), 47 + 5 + 7));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testMultipleCssFiles() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("multiple-css-files");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(css("css/style2-sprite.css")).hasSameTextualContentAs(
            css("css/style2-expected.css"));
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(17 + 15 + 48, 47));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testLargeRepeat() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("large-repeat");
        buildSprites(testDir);

        final String spriteHorizontalPath = "img/sprite-horizontal.png";
        final String spriteVerticalPath = "img/sprite-vertical.png";

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, spriteHorizontalPath)).exists();
        assertThat(new File(testDir, spriteVerticalPath)).exists();
        assertThat(sprite(testDir, spriteHorizontalPath))
            .hasSize(new Dimension(17 + 15, 16 * 17 /* lcm(16, 17) */));
        assertThat(sprite(testDir, spriteVerticalPath))
            .hasSize(new Dimension(15 * 17 /* lcm(15, 17) */, 17 + 16));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testMissingImages() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("missing-images");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(18, 17 + 6 + 5));

        // The unsatisfied sprite references are not removed from the output
        // file, hence we have two warnings
        assertThat(messages).contains(
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.CANNOT_NOT_LOAD_IMAGE, new File(testDir,
            "css/style.css").getPath(), 15,
            new File(testDir, "img/logo.png").getPath(), "Can't read input file!"),
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.CANNOT_NOT_LOAD_IMAGE, new File(testDir,
            "css/style-expected.css").getPath(), 15, new File(testDir,
            "img/logo.png").getPath(), "Can't read input file!"));
    }

    @Test
    void testUnsupportedIndividualImageFormat() throws FileNotFoundException,
        IOException
    {
        final File testDir = testDir("unsupported-image-format");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).doesNotExist();

        assertThat(messages).contains(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.UNSUPPORTED_INDIVIDUAL_IMAGE_FORMAT, new File(
                    testDir, "css/style.css").getPath(), 8, new File(testDir,
                    "img/web.iff").getPath()));
    }

    @Test
    void testUnsupportedSpriteProperties() throws FileNotFoundException,
        IOException
    {
        final File testDir = testDir("unsupported-sprite-properties");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(48, 16 + 17 + 47));

        final String styleCssPath = new File(testDir, "css/style.css").getPath();
        assertThat(messages).isEquivalentTo(
          Message.MessageLevel.WARN,
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.UNSUPPORTED_PROPERTIES_FOUND, styleCssPath, 4,
            "sprites-layout"),
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.UNSUPPORTED_PROPERTIES_FOUND, styleCssPath, 14,
            "sprites-margin-top"),
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.UNSUPPORTED_PROPERTIES_FOUND, styleCssPath, 18,
            "sprites-alignment"));
    }

    @Test
    void testOverridingCssProperties() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("overriding-css-properties");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(17 + 15 + 48, 47));

        final String styleCssPath = new File(testDir, "css/style.css").getPath();
        assertThat(messages).isEquivalentTo(
            Message.MessageLevel.WARN,
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.OVERRIDING_PROPERTY_FOUND, styleCssPath, 10,
                "background-image", 9),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.OVERRIDING_PROPERTY_FOUND, styleCssPath, 21,
                "background-position", 20));
    }

    @Test
    void testAbsoluteImageUrl() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("absolute-image-url");
        final File documentRootDir = testDir("absolute-image-url/absolute-path");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, documentRootDir,
            MessageLevel.INFO, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        final File spriteFile = new File(documentRootDir, "img/sprite.png");
        assertThat(spriteFile).exists();
        assertThat(ImageIO.read(spriteFile)).hasSize(
            new Dimension(17, 17));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        org.carrot2.util.FileUtils.deleteThrowingExceptions(spriteFile);
    }

    @Test
    void testNonDefaultOutputDir() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("non-default-output-dir");
        final File documentRootDir = testDir("non-default-output-dir");
        final File outputDir = testDir("non-default-output-dir/output-dir");
        org.carrot2.util.FileUtils.mkdirsThrowingExceptions(outputDir);
        buildSprites(filesystemSmartSpritesParameters(testDir, outputDir,
            documentRootDir, MessageLevel.INFO,
            SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());

        final File absoluteSpriteFile = new File(documentRootDir, "img/absolute.png");
        assertThat(absoluteSpriteFile).exists();
        assertThat(ImageIO.read(absoluteSpriteFile))
            .hasSize(new Dimension(17, 17));

        final File relativeSpriteFile = new File(outputDir, "img/relative.png");
        assertThat(relativeSpriteFile).exists();
        assertThat(ImageIO.read(relativeSpriteFile))
            .hasSize(new Dimension(15, 16));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        FileUtils.deleteDirectory(outputDir);
        org.carrot2.util.FileUtils.deleteThrowingExceptions(absoluteSpriteFile);
    }

    @Test
    void testScaledSpriteImage() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("scaled-sprite");
        final File documentRootDir = testDir("scaled-sprite");
        buildSprites(filesystemSmartSpritesParameters(testDir, null,
            documentRootDir, MessageLevel.INFO,
            SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());

        final File absoluteSpriteFile = new File(documentRootDir, "img/absolute.png");
        assertThat(absoluteSpriteFile).exists();
        assertThat(ImageIO.read(absoluteSpriteFile))
            .hasSize(new Dimension(17, 17));

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        org.carrot2.util.FileUtils.deleteThrowingExceptions(absoluteSpriteFile);
    }

    @Test
    void testFractionalScaledSpriteImage() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("scaled-sprite-fractional");
        final File documentRootDir = testDir("scaled-sprite-fractional");
        buildSprites(filesystemSmartSpritesParameters(testDir, null,
            documentRootDir, MessageLevel.INFO,
            SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());

        final File absoluteSpriteFile = new File(documentRootDir, "img/absolute.png");
        assertThat(absoluteSpriteFile).exists();
        assertThat(ImageIO.read(absoluteSpriteFile))
            .hasSize(new Dimension(17, 17));

//        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        assertThat(messages).isEquivalentTo(
          Message.MessageLevel.WARN,
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.IMAGE_FRACTIONAL_SCALE_VALUE, null, 8, "../img/web.gif",
            8.5f, 8.5f),
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.FRACTIONAL_SCALE_VALUE, null, 8, "absolute",
            8.5f, 8.5f));

        org.carrot2.util.FileUtils.deleteThrowingExceptions(absoluteSpriteFile);
    }

    @Test
    void testCssOutputDir() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("css-output-dir");
        final File rootDir = new File(testDir, "css/sprite").getCanonicalFile();
        final File outputDir = testDir("css-output-dir/output-dir/css");
        org.carrot2.util.FileUtils.mkdirsThrowingExceptions(outputDir);
        buildSprites(filesystemSmartSpritesParameters(rootDir, outputDir, null,
            MessageLevel.INFO, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss(new File(rootDir, "style.css"))).hasSameTextualContentAs(
            new File(rootDir, "style-expected.css"));

        final File relativeSpriteFile = new File(outputDir, "../img/relative.png");
        assertThat(relativeSpriteFile).exists();
        assertThat(ImageIO.read(relativeSpriteFile))
            .hasSize(new Dimension(17 + 15, 17));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        FileUtils.deleteDirectory(outputDir.getParentFile());
    }

    @Test
    void testRepeatedImageReferences() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("repeated-image-references");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(17 + 19, 19));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testIndexedColor() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("indexed-color");
        buildSprites(testDir);

        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.gif")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha.png")).isDirectColor()
            .hasTrueAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors.png")).isDirectColor()
            .doesNotHaveAlpha();

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testIndexedForcedDirectColor() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("indexed-color");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null,
            MessageLevel.INFO, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            PngDepth.DIRECT, SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.gif")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isDirectColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha.png")).isDirectColor()
            .hasTrueAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors.png")).isDirectColor()
            .doesNotHaveAlpha();

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testIndexedForcedIndexedColor() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("indexed-color");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null,
            MessageLevel.INFO, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            PngDepth.INDEXED, SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.gif")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha.png")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors.png")).isIndexedColor()
            .doesNotHaveAlpha();

        assertThat(messages).isEquivalentTo(
          Message.MessageLevel.WARN,
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null, 25,
            "full-alpha"),
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.USING_WHITE_MATTE_COLOR_AS_DEFAULT, null, 25,
            "full-alpha"),
          new Message(Message.MessageLevel.WARN,
            Message.MessageType.TOO_MANY_COLORS_FOR_INDEXED_COLOR, null, 32,
            "many-colors", 293, 255));
    }

    @Test
    void testMatteColor() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("matte-color");
        buildSprites(testDir);

        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha-m1.png")).isDirectColor()
            .hasTrueAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha-m2.png")).isDirectColor()
            .hasTrueAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha-m3.png")).isDirectColor()
            .hasTrueAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors.png")).isDirectColor()
            .doesNotHaveAlpha();

        assertThat(messages).isEquivalentTo(
            Message.MessageLevel.WARN,
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, null, 12,
                "full-alpha-m1"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, null, 19,
                "full-alpha-m2"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, null, 26,
                "full-alpha-m3"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.IGNORING_MATTE_COLOR_NO_PARTIAL_TRANSPARENCY, null,
                33, "bit-alpha"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.IGNORING_MATTE_COLOR_NO_SUPPORT, null, 40,
                "many-colors"));
    }

    @Test
    void testMatteColorForcedIndex() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("matte-color");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null,
            MessageLevel.INFO, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            PngDepth.INDEXED, SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha-m1.png")).isIndexedColor()
            .hasBitAlpha().isEqualTo(sprite(testDir, "img/sprite-full-alpha-m2.png"))
            .isNotEqualTo(sprite(testDir, "img/sprite-full-alpha-m3.png"));
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha-m2.png")).isIndexedColor()
            .hasBitAlpha().isEqualTo(sprite(testDir, "img/sprite-full-alpha-m1.png"))
            .isNotEqualTo(sprite(testDir, "img/sprite-full-alpha-m3.png"));
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha-m3.png")).isIndexedColor()
            .hasBitAlpha().isNotEqualTo(sprite(testDir, "img/sprite-full-alpha-m1.png"));
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors.png")).isIndexedColor()
            .doesNotHaveAlpha();

        assertThat(messages).isEquivalentTo(
            Message.MessageLevel.WARN,
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null, 12,
                "full-alpha-m1"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null, 19,
                "full-alpha-m2"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null, 26,
                "full-alpha-m3"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.IGNORING_MATTE_COLOR_NO_PARTIAL_TRANSPARENCY, null,
                33, "bit-alpha"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.TOO_MANY_COLORS_FOR_INDEXED_COLOR, null, 40,
                "many-colors", 293, 255));
    }

    @Test
    void testIe6IndexedColor() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("indexed-color-ie6");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null,
            MessageLevel.INFO, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            PngDepth.AUTO, true, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.gif")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-bit-alpha.png")).isIndexedColor()
            .hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha.png")).isDirectColor()
            .hasTrueAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-full-alpha-ie6.png"))
            .isIndexedColor().hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors.png")).isDirectColor()
            .doesNotHaveAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors-bit-alpha-ie6.png"))
            .isIndexedColor().hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors-bit-alpha-no-ie6.png"))
            .isDirectColor().hasBitAlpha();
        org.carrot2.labs.test.Assertions
            .assertThat(sprite(testDir, "img/sprite-many-colors-bit-alpha.png"))
            .isDirectColor().hasBitAlpha();

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        assertThat(messages).isEquivalentTo(
            Message.MessageLevel.IE6NOTICE,
            new Message(Message.MessageLevel.IE6NOTICE,
                Message.MessageType.ALPHA_CHANNEL_LOSS_IN_INDEXED_COLOR, null, 27,
                "full-alpha"),
            new Message(Message.MessageLevel.IE6NOTICE,
                Message.MessageType.USING_WHITE_MATTE_COLOR_AS_DEFAULT, null, 27,
                "full-alpha"),
            new Message(Message.MessageLevel.IE6NOTICE,
                Message.MessageType.TOO_MANY_COLORS_FOR_INDEXED_COLOR, null, 41,
                "many-colors-bit-alpha", 293, 255));
    }

    @Test
    void testSpriteImageUidMd5() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("sprite-image-uid-md5");
        buildSprites(testDir);

        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(17 + 15, 17));
        assertThat(sprite(testDir, "img/sprite2.png"))
            .hasSize(new Dimension(48, 47));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        // TODO: the generated digest depends on the content of the generated image, which
        // in turn depends on the Java version used.
        //
        // assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testSpriteImageUidMd5Ie6() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("sprite-image-uid-md5-ie6");
        buildSprites(filesystemSmartSpritesParameters(testDir, null, null,
            MessageLevel.INFO, SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            PngDepth.AUTO, true, SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(new File(testDir, "img/sprite-ie6.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(20, 20));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void variableSubstitutionInSpriteImagePath() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("variable-substitution-in-sprite-image-path");
        final String sprite1 = "img/sprite-10eb4d8ef5d4b17c7e1173b2213ec6d1.png";
        final String sprite2 = "img/01cbb5bd4c5577f487e1ca434009967c/sprite2.png";

        try
        {
            buildSprites(testDir);

            assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
            assertThat(new File(testDir, sprite1)).exists();
            assertThat(new File(testDir, sprite2)).exists();
            assertThat(sprite(testDir, sprite1)).hasSize(
                new Dimension(17 + 15, 17));
            assertThat(sprite(testDir, sprite2)).hasSize(
                new Dimension(48, 47));
            assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        }
        finally
        {
            FileUtils
                .deleteDirectory(new File(testDir, "img/01cbb5bd4c5577f487e1ca434009967c"));
        }
    }

    @Test
    void testIndividualCssFileDoesNotExist() throws IOException
    {
        final String path = testDir("does-not-exist").getPath();
        buildSprites(Lists.newArrayList(path));
        assertThat(messages).contains(
            Message.warn(MessageType.CSS_FILE_DOES_NOT_EXIST, path));
    }

    @Test
    void testDirectoryProvidedAsIndividualCssFile() throws IOException
    {
        final String path = testDir(".").getPath();
        buildSprites(Lists.newArrayList(path));
        assertThat(messages).contains(
            Message.warn(MessageType.CSS_PATH_IS_NOT_A_FILE, path));
    }

    @Test
    void testIndividualCssFilesWithoutOutputDir() throws IOException
    {
        final File testDir = testDir("individual-css-files-without-output-dir");
        final File css = new File(testDir, "css/style-sprite.css");
        final File customCss = new File(testDir, "css/custom/style-sprite.css");
        final File otherCss = new File(testDir, "css-other/style-sprite.css");
        final File sprite = new File(testDir, "img/sprite.png");
        try
        {
            buildSprites(Lists.newArrayList(
                new File(testDir, "/css/style.css").getPath(), new File(testDir,
                    "css/custom/style.css").getPath(), new File(testDir,
                    "css-other/style.css").getPath()));
            assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
            assertThat(css).hasSameTextualContentAs(new File(testDir, "css/style-expected.css"));
            assertThat(customCss).hasSameTextualContentAs(
                new File(testDir, "css/custom/style-expected.css"));
            assertThat(otherCss).hasSameTextualContentAs(
                new File(testDir, "css-other/style-expected.css"));
            assertThat(sprite).exists();
            assertThat(sprite(testDir)).hasSize(
                new Dimension(17, 17));
        }
        finally
        {
            org.carrot2.util.FileUtils.deleteThrowingExceptions(css, otherCss, customCss,
                sprite);
        }
    }

    @Test
    void testIndividualCssFilesWithOutputDir() throws IOException
    {
        final File testDir = testDir("individual-css-files-with-output-dir");
        final File outputDir = new File(testDir, "output");
        final File css = new File(outputDir, "style-sprite.css");
        final File customCss = new File(outputDir, "custom/style-sprite.css");
        final File otherCss = new File(testDir, "css-other/style-sprite.css");
        final File sprite = new File(testDir, "img/sprite.png");
        try
        {
            final String otherCssPath = new File(testDir, "css-other/style.css")
                .getPath();
            buildSprites(Lists.newArrayList(new File(testDir, "css/style.css").getPath(),
                new File(testDir, "css/custom/style.css").getPath(), otherCssPath),
                new File(testDir, "css").getPath(), outputDir.getPath());
            assertThat(css).hasSameTextualContentAs(new File(testDir, "css/style-expected.css"));
            assertThat(customCss).hasSameTextualContentAs(
                new File(testDir, "css/custom/style-expected.css"));
            assertThat(otherCss).doesNotExist();
            assertThat(sprite).exists();
            assertThat(sprite(testDir)).hasSize(
                new Dimension(17, 17));
            assertThat(messages).contains(
              Message.warn(
                MessageType.IGNORING_CSS_FILE_OUTSIDE_OF_ROOT_DIR, otherCssPath));
        }
        finally
        {
            FileUtils.deleteDirectory(outputDir);
            org.carrot2.util.FileUtils.deleteThrowingExceptions(sprite);
        }
    }

    @Test
    void testSpriteMargins() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("sprite-margins");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        final String horizontalSpritePath = "img/sprite-horizontal.png";
        assertThat(new File(testDir, horizontalSpritePath)).exists();
        assertThat(sprite(testDir, horizontalSpritePath))
            .hasSize(new Dimension(48 + 100 + 100 + 48 + 48, 47 * 6));

        final String verticalSpritePath = "img/sprite-vertical.png";
        assertThat(sprite(testDir, verticalSpritePath))
            .hasSize(new Dimension(48 * 6, 47 + 100 + 100 + 47 + 47));

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testSpriteCentering() throws FileNotFoundException, IOException {
        final File testDir = testDir("sprite-centering");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        final String horizontalSpritePath = "img/sprite-horizontal.png";
        assertThat(new File(testDir, horizontalSpritePath)).exists();
        assertThat(
                sprite(testDir, horizontalSpritePath)).hasSize(
          new Dimension(48, 47));

        final String verticalSpritePath = "img/sprite-vertical.png";
        assertThat(
                sprite(testDir, verticalSpritePath)).hasSize(
          new Dimension(48, 47));

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    /**
     * Test case for bug SMARTSPRITES-69.
     */
    @Test
    void testCssSubfolders() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("css-in-subfolders");
        buildSprites(testDir);

        assertThat(processedCss()).hasSameTextualContentAs(expectedCss());
        assertThat(css("css/library/common-sprite.css")).hasSameTextualContentAs(
            css("css/library/common-expected.css"));
        assertThat(new File(testDir, "img/sprite.png")).exists();
        assertThat(sprite(testDir)).hasSize(
            new Dimension(17, 17 + 16));
        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);

        org.carrot2.util.FileUtils
            .deleteThrowingExceptions(css("css/library/common-sprite.css"));
    }

    /**
     * Test case for bug SMARTSPRITES-78. The bug was caused by the lack of clear contract
     * on the format of paths in {@link SpriteBuilder#buildSprites(java.util.Collection)}.
     */
    @Test
    void testCssFileApiInvocation() throws FileNotFoundException, IOException
    {
        final File testDir = testDir("simple-horizontal-sprite");
        final File outputCss = new File(testDir, "css/style-sprite.css");
        final File sprite = new File(testDir, "img/sprite.png");

        try
        {
            buildSprites(Lists.newArrayList(new File(testDir, "css/style.css").getPath()
                .replace(File.separatorChar, '/')));

            assertThat(outputCss).hasSameTextualContentAs(
                new File(testDir, "css/style-expected.css"));
            assertThat(sprite).exists();
            assertThat(sprite(testDir)).hasSize(
                new Dimension(17 + 15 + 48, 47));
            assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        }
        finally
        {
            org.carrot2.util.FileUtils.deleteThrowingExceptions(outputCss, sprite);
        }
    }

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

        org.carrot2.util.FileUtils.deleteThrowingExceptions(new File(rootDir, "css")
          .listFiles(new FilenameFilter() {
              public boolean accept(File dir, String name) {
                  return name.contains("-sprite");
              }
          }));

        // Delete sprites
        org.carrot2.util.FileUtils.deleteThrowingExceptions(new File(rootDir, "img")
          .listFiles(new FilenameFilter() {
              public boolean accept(File dir, String name) {
                  return name.startsWith("sprite");
              }
          }));
    }

    private File testDir(String test)
    {
        return new File("test/" + test);
    }

    private BufferedImage sprite(final File testDir) throws IOException
    {
        return sprite(testDir, "img/sprite.png");
    }

    private BufferedImage sprite(final File testDir, String imagePath) throws IOException
    {
        return ImageIO.read(new File(testDir, imagePath));
    }

    private File expectedCss()
    {
        return css("css/style-expected.css");
    }

    private File sourceCss()
    {
        return css("css/style.css");
    }

    private File processedCss()
    {
        return processedCss(sourceCss());
    }

    private File css(String cssPath)
    {
        return new File(spriteBuilder.parameters.getRootDir(), cssPath);
    }

    private File processedCss(File sourceCss)
    {
        return new File(spriteBuilder.getProcessedCssFile(sourceCss.getPath()));
    }

    private void buildSprites(File dir) throws IOException
    {
        buildSprites(dir, false);
    }

    private void buildSprites(File dir, boolean ie6) throws IOException
    {
        buildSprites(new SmartSpritesParameters(dir.getPath(), null, null, null,
            SmartSpritesParameters.DEFAULT_LOGGING_LEVEL,
            SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH, ie6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));
    }

    private void buildSprites(List<String> cssFiles) throws IOException
    {
        buildSprites(cssFiles, null, null);
    }

    private void buildSprites(List<String> cssFiles, String rootDir, String outputDir)
        throws IOException
    {
        buildSprites(new SmartSpritesParameters(rootDir, cssFiles, outputDir, null,
            SmartSpritesParameters.DEFAULT_LOGGING_LEVEL,
            SmartSpritesParameters.DEFAULT_CSS_FILE_SUFFIX,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_DEPTH,
            SmartSpritesParameters.DEFAULT_SPRITE_PNG_IE6,
            SmartSpritesParameters.DEFAULT_CSS_FILE_ENCODING));
    }

    private void buildSprites(SmartSpritesParameters parameters) throws IOException
    {
        spriteBuilder = new SpriteBuilder(parameters, messageLog);
        spriteBuilder.buildSprites();
    }

    private static SmartSpritesParameters filesystemSmartSpritesParameters(File rootDir,
        File outputDir, File documentRootDir, MessageLevel logLevel,
        String cssFileSuffix, PngDepth spritePngDepth, boolean spritePngIe6,
        String cssEncoding)
    {
        return new SmartSpritesParameters(rootDir.getPath(), null,
            outputDir != null ? outputDir.getPath() : null,
            documentRootDir != null ? documentRootDir.getPath() : null, logLevel,
            cssFileSuffix, spritePngDepth, spritePngIe6, cssEncoding);
    }

}
