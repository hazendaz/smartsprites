/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
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
public class SmartSprites {

    /**
     * Entry point to SmartSprites. All parameters are passed as JVM properties.
     *
     * @param args
     *            the arguments
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws IOException {
        final SmartSpritesParameters parameters = new SmartSpritesParameters();

        final ParserProperties parserProperties = ParserProperties.defaults();
        parserProperties.withUsageWidth(80);

        final CmdLineParser parser = new CmdLineParser(parameters, parserProperties);

        if (args.length == 0) {
            printUsage(parser);
            return;
        }

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            printUsage(parser);
            System.out.println("\n" + e.getMessage());
            return;
        }

        // Get parameters form system properties
        final MessageLog messageLog = new MessageLog(new PrintStreamMessageSink(System.out, parameters.getLogLevel()));
        new SpriteBuilder(parameters, messageLog).buildSprites();
    }

    /**
     * Prints the usage.
     *
     * @param parser
     *            the parser
     */
    private static void printUsage(final CmdLineParser parser) {
        System.out.print("Usage: smartsprites");
        parser.printSingleLineUsage(System.out);
        System.out.println();
        System.out.println(
                "\nPlease see https://www.w3schools.com/css/css_image_sprites.asp for detailed option descriptions.");
    }
}
