/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.util;

import java.util.Collection;

/**
 * Various utility methods for working with {@link Collection}s.
 */
public class CollectionUtils {

    /**
     * Instantiates a new collection utils.
     */
    private CollectionUtils() {
        // Prevent Instantiation
    }

    /**
     * Converts a {@link Collection} to a {@link String} separating entries by <code>, </code>.
     *
     * @param collection
     *            the collection
     *
     * @return the string
     */
    public static String toString(Collection<?> collection) {
        final StringBuilder string = new StringBuilder();
        for (final Object object : collection) {
            string.append(object.toString());
            string.append(", ");
        }

        string.delete(string.length() - 2, string.length());

        return string.toString();
    }
}
