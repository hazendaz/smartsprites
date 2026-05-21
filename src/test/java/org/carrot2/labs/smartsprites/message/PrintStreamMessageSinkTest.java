/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.message;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link PrintStreamMessageSink}.
 */
class PrintStreamMessageSinkTest {

    /**
     * Prints info message when level is info.
     */
    @Test
    void printsInfoMessageWhenLevelIsInfo() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintStreamMessageSink sink = new PrintStreamMessageSink(ps);

        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "hello"));

        String output = baos.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("hello"), "Should print info message: " + output);
    }

    /**
     * Suppresses message below threshold level.
     */
    @Test
    void suppressesMessageBelowThresholdLevel() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintStreamMessageSink sink = new PrintStreamMessageSink(ps, MessageLevel.WARN);

        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "ignored"));

        String output = baos.toString(StandardCharsets.UTF_8);
        assertTrue(output.isEmpty(), "Should not print info message below WARN threshold: " + output);
    }

    /**
     * Prints warn message at warn threshold.
     */
    @Test
    void printsWarnMessageAtWarnThreshold() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintStreamMessageSink sink = new PrintStreamMessageSink(ps, MessageLevel.WARN);

        sink.add(new Message(MessageLevel.WARN, MessageType.GENERIC, null, 0, "warning"));

        String output = baos.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("warning"), "Should print warn message at WARN threshold: " + output);
    }

    /**
     * Prints error message when level is higher than threshold.
     */
    @Test
    void printsErrorMessageAboveThreshold() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintStreamMessageSink sink = new PrintStreamMessageSink(ps, MessageLevel.WARN);

        sink.add(new Message(MessageLevel.ERROR, MessageType.GENERIC, null, 0, "error msg"));

        String output = baos.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("error msg"), "Should print error message above WARN threshold: " + output);
    }

    /**
     * Default constructor uses info level.
     */
    @Test
    void defaultConstructorUsesInfoLevel() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintStreamMessageSink sink = new PrintStreamMessageSink(ps);

        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "info msg"));

        String output = baos.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("info msg"), "Default constructor should print INFO messages: " + output);
    }
}
