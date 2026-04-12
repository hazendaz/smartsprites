/*
 * SmartSprites Project
 *
 * Copyright (C) 2007-2009, Stanisław Osiński.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of  source code must  retain the above  copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following  disclaimer in  the documentation  and/or
 *   other materials provided with the distribution.
 *
 * - Neither the name of the SmartSprites Project nor the names of its contributors
 *   may  be used  to endorse  or  promote  products derived   from  this  software
 *   without specific prior written permission.
 *
 * - We kindly request that you include in the end-user documentation provided with
 *   the redistribution and/or in the software itself an acknowledgement equivalent
 *   to  the  following: "This product includes software developed by the SmartSprites
 *   Project."
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  AND
 * ANY EXPRESS OR  IMPLIED WARRANTIES, INCLUDING,  BUT NOT LIMITED  TO, THE IMPLIED
 * WARRANTIES  OF  MERCHANTABILITY  AND  FITNESS  FOR  A  PARTICULAR  PURPOSE   ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE  FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL,  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL  DAMAGES
 * (INCLUDING, BUT  NOT LIMITED  TO, PROCUREMENT  OF SUBSTITUTE  GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS;  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  ON
 * ANY  THEORY  OF  LIABILITY,  WHETHER  IN  CONTRACT,  STRICT  LIABILITY,  OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE)  ARISING IN ANY WAY  OUT OF THE USE  OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
