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
 * Test cases for {@link LevelCounterMessageSink}.
 */
class LevelCounterMessageSinkTest {

    /**
     * Counts info messages correctly.
     */
    @Test
    void countsInfoMessages() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "info1"));
        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "info2"));
        assertEquals(2, sink.getInfoCount());
        assertEquals(0, sink.getWarnCount());
    }

    /**
     * Counts warn messages correctly.
     */
    @Test
    void countsWarnMessages() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.WARN, MessageType.GENERIC, null, 0, "warn1"));
        assertEquals(0, sink.getInfoCount());
        assertEquals(1, sink.getWarnCount());
    }

    /**
     * Does not count error messages in info or warn.
     */
    @Test
    void doesNotCountErrorMessages() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.ERROR, MessageType.GENERIC, null, 0, "error1"));
        assertEquals(0, sink.getInfoCount());
        assertEquals(0, sink.getWarnCount());
    }

    /**
     * Mixed messages counted separately.
     */
    @Test
    void mixedMessagesCounted() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "i"));
        sink.add(new Message(MessageLevel.WARN, MessageType.GENERIC, null, 0, "w"));
        sink.add(new Message(MessageLevel.ERROR, MessageType.GENERIC, null, 0, "e"));
        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "i2"));
        assertEquals(2, sink.getInfoCount());
        assertEquals(1, sink.getWarnCount());
    }

    /**
     * Initial counts are zero.
     */
    @Test
    void initialCountsAreZero() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        assertEquals(0, sink.getInfoCount());
        assertEquals(0, sink.getWarnCount());
    }

    /**
     * Has warnings returns true when there are warn messages.
     */
    @Test
    void hasWarningsWhenWarnMessagesPresent() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.WARN, MessageType.GENERIC, null, 0, "w"));
        assertTrue(sink.getWarnCount() > 0);
    }
}
