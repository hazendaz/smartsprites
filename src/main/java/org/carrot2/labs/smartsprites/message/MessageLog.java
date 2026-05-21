/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gathers {@link Message}s during the runtime of the applications.
 */
public class MessageLog {

    /** Current line in the CSS file. */
    private int line = 0;

    /** CSS file currently processed. */
    private String cssPath = null;

    /** Sinks for messages. */
    private final List<MessageSink> sinks;

    /**
     * Creates a {@link MessageLog} with the provided {@link MessageSink}s.
     *
     * @param sinks
     *            the sinks
     */
    public MessageLog(MessageSink... sinks) {
        this.sinks = new ArrayList<>(Arrays.asList(sinks));
    }

    /**
     * Logs a message to this log.
     *
     * @param level
     *            the level
     * @param type
     *            the type
     * @param arguments
     *            the arguments
     */
    public void log(Message.MessageLevel level, Message.MessageType type, Object... arguments) {
        for (final MessageSink sink : sinks) {
            sink.add(new Message(level, type, cssPath, line, arguments));
        }
    }

    /**
     * Logs an information message to this log.
     *
     * @param type
     *            the type
     * @param arguments
     *            the arguments
     */
    public void info(Message.MessageType type, Object... arguments) {
        log(Message.MessageLevel.INFO, type, arguments);
    }

    /**
     * Logs a warning message to this log.
     *
     * @param type
     *            the type
     * @param arguments
     *            the arguments
     */
    public void deprecation(Message.MessageType type, Object... arguments) {
        log(Message.MessageLevel.DEPRECATION, type, arguments);
    }

    /**
     * Logs a warning message to this log.
     *
     * @param type
     *            the type
     * @param arguments
     *            the arguments
     */
    public void warning(Message.MessageType type, Object... arguments) {
        log(Message.MessageLevel.WARN, type, arguments);
    }

    /**
     * Logs an error message to this log.
     *
     * @param type
     *            the type
     * @param arguments
     *            the arguments
     */
    public void error(Message.MessageType type, Object... arguments) {
        log(Message.MessageLevel.ERROR, type, arguments);
    }

    /**
     * Logs a status message to this log.
     *
     * @param type
     *            the type
     * @param arguments
     *            the arguments
     */
    public void status(Message.MessageType type, Object... arguments) {
        log(Message.MessageLevel.STATUS, type, arguments);
    }

    /**
     * Sets current CSS line for this log.
     *
     * @param line
     *            the new line
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Sets the css file.
     *
     * @param cssFilePath
     *            the new css file
     */
    public void setCssFile(String cssFilePath) {
        this.cssPath = cssFilePath;
    }

    /**
     * Adds a {@link MessageSink} to this log.
     *
     * @param sink
     *            the sink
     */
    public void addMessageSink(MessageSink sink) {
        this.sinks.add(sink);
    }
}
