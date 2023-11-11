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

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Gathers {@link Message}s during the runtime of the applications.
 */
public class MessageLog
{
    /** Current line in the CSS file */
    private int line = 0;

    /** CSS file currently processed */
    private String cssPath = null;

    /** Sinks for messages */
    private final List<MessageSink> sinks;

    /**
     * Creates a {@link MessageLog} with the provided {@link MessageSink}s.
     */
    public MessageLog(MessageSink... sinks)
    {
        this.sinks = Lists.newArrayList(sinks);
    }

    /**
     * Logs a message to this log.
     */
    public void log(Message.MessageLevel level, Message.MessageType type,
        Object... arguments)
    {
        for (final MessageSink sink : sinks)
        {
            sink.add(new Message(level, type, cssPath, line, arguments));
        }
    }

    /**
     * Logs an information message to this log.
     */
    public void info(Message.MessageType type, Object... arguments)
    {
        log(Message.MessageLevel.INFO, type, arguments);
    }

    /**
     * Logs a warning message to this log.
     */
    public void notice(Message.MessageType type, Object... arguments)
    {
        log(Message.MessageLevel.IE6NOTICE, type, arguments);
    }

    /**
     * Logs a warning message to this log.
     */
    public void deprecation(Message.MessageType type, Object... arguments)
    {
        log(Message.MessageLevel.DEPRECATION, type, arguments);
    }
    
    /**
     * Logs a warning message to this log.
     */
    public void warning(Message.MessageType type, Object... arguments)
    {
        log(Message.MessageLevel.WARN, type, arguments);
    }
    
    /**
     * Logs an error message to this log.
     */
    public void error(Message.MessageType type, Object... arguments)
    {
        log(Message.MessageLevel.ERROR, type, arguments);
    }

    /**
     * Logs a status message to this log.
     */
    public void status(Message.MessageType type, Object... arguments)
    {
        log(Message.MessageLevel.STATUS, type, arguments);
    }

    /**
     * Sets current CSS line for this log.
     */
    public void setLine(int line)
    {
        this.line = line;
    }
    
    public void setCssFile(String cssFilePath)
    {
        this.cssPath = cssFilePath;
    }
    
    /**
     * Adds a {@link MessageSink} to this log.
     */
    public void addMessageSink(MessageSink sink)
    {
        this.sinks.add(sink);
    }
}
