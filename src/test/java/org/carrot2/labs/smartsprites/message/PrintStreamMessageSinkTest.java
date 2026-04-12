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
