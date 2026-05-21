/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link Message}.
 */
class MessageTest {

    /**
     * Warn creates message with warn level and no css path.
     */
    @Test
    void warnCreatesMessageWithWarnLevel() {
        Message msg = Message.warn(MessageType.GENERIC, "test warning");
        assertEquals(MessageLevel.WARN, msg.level);
        assertEquals(MessageType.GENERIC, msg.type);
        assertEquals(1, msg.arguments.length);
        assertEquals("test warning", msg.arguments[0]);
    }

    /**
     * Error creates message with error level.
     */
    @Test
    void errorCreatesMessageWithErrorLevel() {
        Message msg = Message.error(MessageType.GENERIC, "test error");
        assertEquals(MessageLevel.ERROR, msg.level);
        assertEquals(MessageType.GENERIC, msg.type);
        assertEquals(1, msg.arguments.length);
        assertEquals("test error", msg.arguments[0]);
    }

    /**
     * To string with no css path contains level and message.
     */
    @Test
    void toStringWithNoCssPath() {
        Message msg = Message.warn(MessageType.GENERIC, "hello");
        String result = msg.toString();
        assertTrue(result.startsWith("WARN: "), "Should start with level: " + result);
        assertTrue(result.contains("hello"), "Should contain formatted message: " + result);
    }

    /**
     * To string with css path includes file and line info.
     */
    @Test
    void toStringWithCssPathIncludesFileInfo() {
        Message msg = new Message(MessageLevel.ERROR, MessageType.GENERIC, "test.css", 5, "msg");
        String result = msg.toString();
        assertTrue(result.contains("test.css"), "Should contain css path: " + result);
        // line is stored 0-based, displayed 1-based
        assertTrue(result.contains("line: 6"), "Should contain line number (1-based): " + result);
    }

    /**
     * Get formatted message formats arguments.
     */
    @Test
    void getFormattedMessageWithArguments() {
        Message msg = Message.warn(MessageType.CANNOT_NOT_LOAD_IMAGE, "file.png", "IOException");
        String formatted = msg.getFormattedMessage();
        assertTrue(formatted.contains("file.png"), "Should contain file name: " + formatted);
        assertTrue(formatted.contains("IOException"), "Should contain exception: " + formatted);
    }

    /**
     * Message level comparator orders levels correctly.
     */
    @Test
    void messageLevelComparatorOrdersLevelsCorrectly() {
        assertTrue(MessageLevel.COMPARATOR.compare(MessageLevel.INFO, MessageLevel.WARN) < 0);
        assertTrue(MessageLevel.COMPARATOR.compare(MessageLevel.WARN, MessageLevel.INFO) > 0);
        assertEquals(0, MessageLevel.COMPARATOR.compare(MessageLevel.INFO, MessageLevel.INFO));
    }

    /**
     * Message level comparator orders error above warn.
     */
    @Test
    void messageLevelComparatorOrdersErrorAboveWarn() {
        assertTrue(MessageLevel.COMPARATOR.compare(MessageLevel.WARN, MessageLevel.ERROR) < 0);
        assertTrue(MessageLevel.COMPARATOR.compare(MessageLevel.ERROR, MessageLevel.STATUS) < 0);
    }

    /**
     * Message constructor copies arguments array defensively.
     */
    @Test
    void messageConstructorCopiesArguments() {
        Object[] args = { "arg1", "arg2" };
        Message msg = new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, args);
        // Modify original array - should not affect message
        args[0] = "changed";
        assertEquals("arg1", msg.arguments[0], "Arguments should be copied defensively");
    }

    /**
     * To string with info level and no css path.
     */
    @Test
    void toStringInfoLevelNoCssPath() {
        Message msg = new Message(MessageLevel.INFO, MessageType.PROCESSING_COMPLETED, null, 0, 100L);
        String result = msg.toString();
        assertTrue(result.startsWith("INFO: "), "Should start with INFO: " + result);
    }

    /**
     * Warn with no arguments produces valid message.
     */
    @Test
    void warnWithNoArguments() {
        Message msg = Message.warn(MessageType.SPRITE_ID_NOT_FOUND);
        assertEquals(MessageLevel.WARN, msg.level);
        assertEquals(0, msg.arguments.length);
    }

    /**
     * Error with no arguments produces valid message.
     */
    @Test
    void errorWithNoArguments() {
        Message msg = Message.error(MessageType.EITHER_ROOT_DIR_OR_CSS_FILES_IS_REQUIRED);
        assertEquals(MessageLevel.ERROR, msg.level);
        assertEquals(0, msg.arguments.length);
    }
}
