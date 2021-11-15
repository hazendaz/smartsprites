package org.carrot2.labs.smartsprites;

import static org.carrot2.labs.test.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;

import org.carrot2.labs.smartsprites.SpriteImageDirective.Ie6Mode;
import org.carrot2.labs.smartsprites.SpriteImageDirective.SpriteUidType;
import org.carrot2.labs.smartsprites.SpriteLayoutProperties.SpriteAlignment;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link SpriteImageDirective}.
 */
class SpriteImageDirectiveTest extends TestWithMemoryMessageSink
{
    @Test
    void testEmpty()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse("", messageLog);
        assertNull(directive);
    }

    @Test
    void testIdUrlLayoutProvidedIe6Mode()
    {
        final SpriteImageDirective directive = SpriteImageDirective
            .parse(
                "sprite: sprite; sprite-image: url('../sprite.png'); sprite-layout: horizontal; sprite-ie6-mode: none",
                messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.PNG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.HORIZONTAL, directive.layout);
        assertEquals(Ie6Mode.NONE, directive.ie6Mode);
        assertThat(messages).isEmpty();
    }

    @Test
    void testIdUrlProvided()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url('../sprite.png')", messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.PNG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.VERTICAL, directive.layout);
        assertThat(messages).isEmpty();
    }

    @Test
    void variablesCorrectSyntax()
    {
        checkImagePathVariableCorrect("../${date}/${sprite}-${md5}.png");
    }

    @Test
    void variablesAndQueryStringCorrectSyntax()
    {
        checkImagePathVariableCorrect("../${sprite}-${md5}.png?${date}");
    }

    @Test
    void variablesUnbalancedBrackets()
    {
        checkImagePathVariableIncorrect("../$sprite}-${md5}.png?${date}");
    }

    @Test
    void variablesMissingDollar()
    {
        checkImagePathVariableIncorrect("../{sprite}-${md5}.png?${date}");
    }

    @Test
    void variablesUnsupportedVariable()
    {
        checkImagePathUnsupportedVariable("abc");
    }

    @Test
    void variablesEmptyVariable()
    {
        checkImagePathUnsupportedVariable("");
    }

    private void checkImagePathVariableCorrect(String path)
    {
        assertNotNull(SpriteImageDirective.parse("sprite: sprite; sprite-image: url('"
            + path + "')", messageLog));
        assertThat(messages).isEmpty();
    }

    private void checkImagePathVariableIncorrect(String path)
    {
        assertNotNull(SpriteImageDirective.parse("sprite: sprite; sprite-image: url('"
            + path + "')", messageLog));
        assertThat(messages).contains(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.MALFORMED_SPRITE_IMAGE_PATH, null, 0, path));
    }

    private void checkImagePathUnsupportedVariable(String variable)
    {
        assertNotNull(SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url('../img/${" + variable + "}.png')",
            messageLog));
        assertThat(messages).contains(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.UNSUPPORTED_VARIABLE_IN_SPRITE_IMAGE_PATH, null, 0,
                variable));
    }

    @Test
    void testMatteColor()
    {
        final SpriteImageDirective directive = SpriteImageDirective
            .parse(
                "sprite: sprite; sprite-image: url('../sprite.png'); sprite-matte-color: #f08231",
                messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.PNG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.VERTICAL, directive.layout);
        assertEquals(directive.matteColor, new Color(0x00f08231));
        assertThat(messages).isEmpty();
    }

    @Test
    void testUidNone()
    {
        checkUidType("sprite-image-uid: none", SpriteUidType.NONE);
        assertThat(messages).isEmpty();
    }

    @Test
    void testUidDate()
    {
        checkUidType("sprite-image-uid: date", SpriteUidType.DATE);
        assertThat(messages).contains(
            new Message(MessageLevel.DEPRECATION,
                MessageType.DEPRECATED_SPRITE_IMAGE_UID, null, 0, "date"));
    }

    @Test
    void testUidMd5()
    {
        checkUidType("sprite-image-uid: md5", SpriteUidType.MD5);
        assertThat(messages).contains(
            new Message(MessageLevel.DEPRECATION,
                MessageType.DEPRECATED_SPRITE_IMAGE_UID, null, 0, "md5"));
    }

    @Test
    void testUidUnknown()
    {
        checkUidType("sprite-image-uid: unknown", SpriteUidType.NONE);
        assertThat(messages).contains(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.UNSUPPORTED_UID_TYPE, null, 0, "unknown"));
    }

    @Test
    void testNoId()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite-image: url('../sprite.png')", messageLog);

        assertNull(directive);

        assertThat(messages).isEquivalentTo(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.SPRITE_ID_NOT_FOUND, null, 0));
    }

    @Test
    void testNoUrl()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite;", messageLog);

        assertNull(directive);

        assertThat(messages).isEquivalentTo(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.SPRITE_IMAGE_URL_NOT_FOUND, null, 0));
    }

    @Test
    void testUnrecognizedFormat()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url('../sprite.')", messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.PNG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.VERTICAL, directive.layout);

        assertThat(messages)
            .isEquivalentTo(
                new Message(Message.MessageLevel.WARN,
                    Message.MessageType.CANNOT_DETERMINE_IMAGE_FORMAT, null, 0,
                    "../sprite."));
    }

    @Test
    void testUnsupportedSpriteImageFormat()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url('../sprite.jpgx')", messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.jpgx", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.PNG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.VERTICAL, directive.layout);

        assertThat(messages).isEquivalentTo(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.UNSUPPORTED_SPRITE_IMAGE_FORMAT, null, 0, "jpgx"));
    }

    @Test
    void testLeadingSpaceInUrl()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url(../sprite.png )", messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.PNG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.VERTICAL, directive.layout);

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
    }

    @Test
    void testUnsupportedLayout()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url('../sprite.jpg'); sprite-layout: other",
            messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.jpg", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.JPG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.VERTICAL, directive.layout);

        assertThat(messages).isEquivalentTo(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.UNSUPPORTED_LAYOUT, null, 0, "other"));
    }

    @Test
    void testUnsupportedIe6Mode()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url('../sprite.png'); sprite-ie6-mode: other",
            messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);

        assertThat(messages).isEquivalentTo(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.UNSUPPORTED_IE6_MODE, null, 0, "other"));
    }

    @Test
    void testIgnoredIe6Mode()
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url('../sprite.gif'); sprite-ie6-mode: auto",
            messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.gif", directive.imagePath);

        assertThat(messages).isEquivalentTo(
            new Message(Message.MessageLevel.IE6NOTICE,
                Message.MessageType.IGNORING_IE6_MODE, null, 0, "GIF"));
    }

    @Test
    void testUnsupportedProperties()
    {
        final SpriteImageDirective directive = SpriteImageDirective
            .parse(
                "sprite: sprite; sprites-image: url('../sprite.png'); sprites-layout: horizontal",
                messageLog);

        assertNull(directive);
        assertThat(messages).isEquivalentTo(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.UNSUPPORTED_PROPERTIES_FOUND, null, 0,
                "sprites-image, sprites-layout"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.SPRITE_IMAGE_URL_NOT_FOUND, null, 0));
    }

    @Test
    void testSpriteLayoutProperties()
    {
        final SpriteImageDirective directive = SpriteImageDirective
            .parse(
                "sprite: sprite; sprite-image: url('../sprite.png'); sprite-layout: horizontal; "
                    + "sprite-alignment: bottom; sprite-margin-left: 10px; sprite-margin-right: 20; sprite-margin-top: 30px; sprite-margin-bottom: 40;",
                messageLog);

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.PNG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.HORIZONTAL, directive.layout);

        assertEquals(SpriteAlignment.BOTTOM, directive.spriteLayoutProperties.alignment);
        assertEquals(10, directive.spriteLayoutProperties.marginLeft);
        assertEquals(20, directive.spriteLayoutProperties.marginRight);
        assertEquals(30, directive.spriteLayoutProperties.marginTop);
        assertEquals(40, directive.spriteLayoutProperties.marginBottom);
    }

    @Test
    void testNegativeMarginValues()
    {
        final SpriteImageDirective directive = SpriteImageDirective
            .parse(
                "sprite: sprite; sprite-image: url('../sprite.png'); sprite-layout: horizontal; "
                    + "sprite-alignment: bottom; sprite-margin-left: -5px; sprite-margin-right: 20; sprite-margin-top: 30px; sprite-margin-bottom: -40;",
                messageLog);

        assertThat(messages).contains(
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.IGNORING_NEGATIVE_MARGIN_VALUE, null, 0,
                "sprite-margin-left"),
            new Message(Message.MessageLevel.WARN,
                Message.MessageType.IGNORING_NEGATIVE_MARGIN_VALUE, null, 0,
                "sprite-margin-bottom"));
        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);
        assertEquals(SpriteImageDirective.SpriteImageFormat.PNG, directive.format);
        assertEquals(SpriteImageDirective.SpriteImageLayout.HORIZONTAL, directive.layout);

        assertEquals(SpriteAlignment.BOTTOM, directive.spriteLayoutProperties.alignment);
        assertEquals(0, directive.spriteLayoutProperties.marginLeft);
        assertEquals(20, directive.spriteLayoutProperties.marginRight);
        assertEquals(30, directive.spriteLayoutProperties.marginTop);
        assertEquals(0, directive.spriteLayoutProperties.marginBottom);
    }

    @Test
    void testSpriteScalingProperty()
    {
        final SpriteImageDirective directive = SpriteImageDirective
            .parse(
                "sprite: sprite; sprite-image: url('../sprite.png'); sprite-layout: horizontal; "
                    + "sprite-scale: 2;",
                messageLog);

        assertThat(messages).doesNotHaveMessagesOfLevel(MessageLevel.WARN);
        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);
        assertEquals(2f, directive.scaleRatio, .5);
    }

    private void checkUidType(String uidDeclaration, SpriteUidType expectedUidType)
    {
        final SpriteImageDirective directive = SpriteImageDirective.parse(
            "sprite: sprite; sprite-image: url('../sprite.png'); " + uidDeclaration,
            messageLog);

        assertNotNull(directive);
        assertEquals("sprite", directive.spriteId);
        assertEquals("../sprite.png", directive.imagePath);
        assertEquals(directive.uidType, expectedUidType);
    }
}
