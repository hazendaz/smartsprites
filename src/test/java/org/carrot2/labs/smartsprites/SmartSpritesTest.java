/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.smartsprites;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link SmartSprites}.
 */
class SmartSpritesTest {

    /** The original standard out. */
    private PrintStream originalOut;

    /** Captured output stream. */
    private ByteArrayOutputStream capturedOut;

    /**
     * Sets up output capture.
     */
    @BeforeEach
    void setUp() {
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    /**
     * Restores standard out after each test.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Main with no args prints usage.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void mainWithNoArgsPrintsUsage() throws IOException {
        SmartSprites.main(new String[0]);
        String output = capturedOut.toString(StandardCharsets.UTF_8);
        // Usage should be printed when no args provided
        org.junit.jupiter.api.Assertions.assertTrue(output.contains("Usage") || output.contains("smartsprites"),
                "Should print usage: " + output);
    }

    /**
     * Main with invalid arg prints usage and error.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    void mainWithInvalidArgPrintsUsageAndError() throws IOException {
        SmartSprites.main(new String[] { "--invalid-argument-xyz" });
        String output = capturedOut.toString(StandardCharsets.UTF_8);
        // Should print usage + error message without throwing
        org.junit.jupiter.api.Assertions.assertFalse(output.isEmpty(), "Should produce output: " + output);
    }

    /**
     * Main does not throw with no args.
     */
    @Test
    void mainDoesNotThrowWithNoArgs() {
        assertDoesNotThrow(() -> SmartSprites.main(new String[0]));
    }

    /**
     * Main does not throw with invalid args.
     */
    @Test
    void mainDoesNotThrowWithInvalidArgs() {
        assertDoesNotThrow(() -> SmartSprites.main(new String[] { "--nonexistent-flag" }));
    }
}
