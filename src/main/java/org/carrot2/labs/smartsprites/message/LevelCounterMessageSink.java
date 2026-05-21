/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.message;

import org.carrot2.labs.smartsprites.message.Message.MessageLevel;

/**
 * Counts the number of messages logged with different levels.
 */
public class LevelCounterMessageSink implements MessageSink {

    /** Number of info messages. */
    private int infoCount = 0;

    /** Number of warning messages. */
    private int warnCount = 0;

    @Override
    public void add(Message message) {
        if (MessageLevel.INFO.equals(message.level)) {
            infoCount++;
        }

        if (MessageLevel.WARN.equals(message.level)) {
            warnCount++;
        }
    }

    /**
     * Gets the info count.
     *
     * @return the info count
     */
    public int getInfoCount() {
        return infoCount;
    }

    /**
     * Gets the warn count.
     *
     * @return the warn count
     */
    public int getWarnCount() {
        return warnCount;
    }
}
