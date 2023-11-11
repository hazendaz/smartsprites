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

import java.io.PrintStream;

import org.carrot2.labs.smartsprites.message.Message.MessageLevel;

/**
 * Outputs logged {@link Message}s to the provided {@link PrintStream}. The
 * {@link Message#toString()} method will be used to format the messages.
 */
public class PrintStreamMessageSink implements MessageSink
{
    /** The stream to print messages to. */
    private final PrintStream printStream;

    /** Logging level */
    private final MessageLevel level;

    /**
     * Creates a {@link PrintStreamMessageSink} with the provided <code>printStream</code>
     * and {@link MessageLevel#INFO} logging level.
     */
    public PrintStreamMessageSink(PrintStream printStream)
    {
        this(printStream, MessageLevel.INFO);
    }

    /**
     * Creates a {@link PrintStreamMessageSink} with the provided <code>printStream</code>
     * and logging <code>level</code>.
     */
    public PrintStreamMessageSink(PrintStream printStream, MessageLevel level)
    {
        this.printStream = printStream;
        this.level = level;
    }

    public void add(Message message)
    {
        if (MessageLevel.COMPARATOR.compare(message.level, level) >= 0)
        {
            printStream.println(message.toString());
        }
    }
}
