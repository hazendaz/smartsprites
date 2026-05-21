/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites;

import java.util.List;

import org.carrot2.labs.smartsprites.message.MemoryMessageSink;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.junit.jupiter.api.BeforeEach;

/**
 * A base class for tests checking logged messages.
 */
public abstract class TestWithMemoryMessageSink {

    /** The message log. */
    protected MessageLog messageLog;

    /** The messages. */
    protected List<Message> messages;

    /**
     * Sets the up message log with memory message sink.
     */
    @BeforeEach
    public void setUpMessageLogWithMemoryMessageSink() {
        final MemoryMessageSink messageSink = new MemoryMessageSink();

        messageLog = new MessageLog(messageSink);
        messages = messageSink.messages;
    }
}
