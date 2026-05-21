/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.util;

/**
 * Various utility methods for working with Java 5 enum types.
 */
public class EnumUtils {

    /**
     * Instantiates a new enum utils.
     */
    private EnumUtils() {
        // Prevent Instantiation
    }

    /**
     * Returns the enum instance corresponding to the provided <code>name</code> or <code>defaultValue</code> if no enum
     * value corresponds to <code>name</code>.
     *
     * @param <T>
     *            the generic type
     * @param name
     *            the name
     * @param enumClass
     *            the enum class
     * @param defaultValue
     *            the default value
     *
     * @return the t
     */
    public static <T extends Enum<T>> T valueOf(String name, Class<T> enumClass, T defaultValue) {
        if (!StringUtils.isNotBlank(name)) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

}
