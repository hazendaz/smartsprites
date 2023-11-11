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

import java.io.IOException;

import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.labs.smartsprites.message.PrintStreamMessageSink;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

/**
 * The entry class for SmartSprites.
 */
public class SmartSprites
{
    /**
     * Entry point to SmartSprites. All parameters are passed as JVM properties.
     */
    public static void main(String [] args) throws IOException
    {
        final SmartSpritesParameters parameters = new SmartSpritesParameters();

        final ParserProperties parserProperties = ParserProperties.defaults();
        parserProperties.withUsageWidth(80);

        final CmdLineParser parser = new CmdLineParser(parameters, parserProperties);

        if (args.length == 0)
        {
            printUsage(parser);
            return;
        }
        
        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            printUsage(parser);
            System.out.println("\n" + e.getMessage());
            return;
        }
        
        // Get parameters form system properties
        final MessageLog messageLog = new MessageLog(new PrintStreamMessageSink(
            System.out, parameters.getLogLevel()));
        new SpriteBuilder(parameters, messageLog).buildSprites();
    }

    private static void printUsage(final CmdLineParser parser)
    {
        System.out.print("Usage: smartsprites");
        parser.printSingleLineUsage(System.out);
        System.out.println();
        System.out.println("\nPlease see http://csssprites.org for detailed option descriptions.");
    }
}
