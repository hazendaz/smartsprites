/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.message;

import java.io.PrintStream;

import org.carrot2.labs.smartsprites.message.Message.MessageLevel;

/**
 * Outputs logged {@link Message}s to the provided {@link PrintStream}. The {@link Message#toString()} method will be
 * used to format the messages.
 */
public class PrintStreamMessageSink implements MessageSink {
    /** The stream to print messages to. */
    private final PrintStream printStream;

    /** Logging level. */
    private final MessageLevel level;

    /**
     * Creates a {@link PrintStreamMessageSink} with the provided <code>printStream</code> and {@link MessageLevel#INFO}
     * logging level.
     *
     * @param printStream
     *            the print stream
     */
    public PrintStreamMessageSink(PrintStream printStream) {
        this(printStream, MessageLevel.INFO);
    }

    /**
     * Creates a {@link PrintStreamMessageSink} with the provided <code>printStream</code> and logging
     * <code>level</code>.
     *
     * @param printStream
     *            the print stream
     * @param level
     *            the level
     */
    public PrintStreamMessageSink(PrintStream printStream, MessageLevel level) {
        this.printStream = printStream;
        this.level = level;
    }

    @Override
    public void add(Message message) {
        if (MessageLevel.COMPARATOR.compare(message.level, level) >= 0) {
            printStream.println(message.toString());
        }
    }
}
