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
package org.carrot2.labs.smartsprites.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.carrot2.labs.smartsprites.message.Message.MessageLevel;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link LevelCounterMessageSink}.
 */
class LevelCounterMessageSinkTest {

    /**
     * Counts info messages correctly.
     */
    @Test
    void countsInfoMessages() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "info1"));
        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "info2"));
        assertEquals(2, sink.getInfoCount());
        assertEquals(0, sink.getWarnCount());
    }

    /**
     * Counts warn messages correctly.
     */
    @Test
    void countsWarnMessages() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.WARN, MessageType.GENERIC, null, 0, "warn1"));
        assertEquals(0, sink.getInfoCount());
        assertEquals(1, sink.getWarnCount());
    }

    /**
     * Does not count error messages in info or warn.
     */
    @Test
    void doesNotCountErrorMessages() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.ERROR, MessageType.GENERIC, null, 0, "error1"));
        assertEquals(0, sink.getInfoCount());
        assertEquals(0, sink.getWarnCount());
    }

    /**
     * Mixed messages counted separately.
     */
    @Test
    void mixedMessagesCounted() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "i"));
        sink.add(new Message(MessageLevel.WARN, MessageType.GENERIC, null, 0, "w"));
        sink.add(new Message(MessageLevel.ERROR, MessageType.GENERIC, null, 0, "e"));
        sink.add(new Message(MessageLevel.INFO, MessageType.GENERIC, null, 0, "i2"));
        assertEquals(2, sink.getInfoCount());
        assertEquals(1, sink.getWarnCount());
    }

    /**
     * Initial counts are zero.
     */
    @Test
    void initialCountsAreZero() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        assertEquals(0, sink.getInfoCount());
        assertEquals(0, sink.getWarnCount());
    }

    /**
     * Has warnings returns true when there are warn messages.
     */
    @Test
    void hasWarningsWhenWarnMessagesPresent() {
        LevelCounterMessageSink sink = new LevelCounterMessageSink();
        sink.add(new Message(MessageLevel.WARN, MessageType.GENERIC, null, 0, "w"));
        assertTrue(sink.getWarnCount() > 0);
    }
}
