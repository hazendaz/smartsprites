/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites.message;

/**
 * Collects {@link Message}s for further processing/ retrieval.
 */
public interface MessageSink {

    /**
     * Adds a {@link Message} for further processing/ retrieval.
     *
     * @param message
     *            the message
     */
    void add(Message message);
}
