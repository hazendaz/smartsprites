/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects messages in a {@link List}, see {@link #messages}.
 */
public class MemoryMessageSink implements MessageSink {
    /**
     * Contains collected messages.
     */
    public final List<Message> messages = new ArrayList<>();

    @Override
    public void add(Message message) {
        messages.add(message);
    }
}
