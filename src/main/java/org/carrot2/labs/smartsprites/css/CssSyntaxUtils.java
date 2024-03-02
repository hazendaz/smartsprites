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
package org.carrot2.labs.smartsprites.css;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageType;
import org.carrot2.labs.smartsprites.message.MessageLog;
import org.carrot2.util.StringUtils;

/**
 * A few utility methods for processing CSS syntax.
 */
public class CssSyntaxUtils {

    /** The Constant URL_PATTERN. */
    private static final Pattern URL_PATTERN = Pattern.compile("[uU][rR][lL]\\((['\"]?)([^'\"]*)\\1\\)");

    /** The Constant COLOR_PATTERN. */
    private static final Pattern COLOR_PATTERN = Pattern.compile("#([0-9a-f]{6})");

    /** The Constant IMPORTANT_PATTERN. */
    private static final Pattern IMPORTANT_PATTERN = Pattern.compile("!\\s*important");

    /**
     * Instantiates a new css syntax utils.
     */
    private CssSyntaxUtils() {
        // Prevent Instantiation
    }

    /**
     * Extracts CSS properties from the provided {@link String}.
     *
     * @param text
     *            the text
     *
     * @return the list
     */
    public static List<CssProperty> extractProperties(String text) {
        return extractRules(text, null);
    }

    /**
     * Extracts CSS properties from the provided {@link String} and logs warnings to the provided {@link MessageLog}.
     *
     * @param text
     *            the text
     * @param messageLog
     *            the message log
     *
     * @return the list
     */
    public static List<CssProperty> extractRules(String text, MessageLog messageLog) {
        final List<CssProperty> rules = new ArrayList<>();

        final String[] chunks = text.split(";");
        for (final String chunk : chunks) {
            final String[] parts = chunk.split(":", 2);

            if (parts.length == 2) {
                String value = parts[1].trim();
                final Matcher matcher = IMPORTANT_PATTERN.matcher(value);
                boolean important = false;
                if (matcher.find()) {
                    important = true;
                    value = matcher.replaceAll("");
                }

                rules.add(new CssProperty(parts[0].trim().toLowerCase(Locale.ENGLISH), value.trim(), important));
            } else if (messageLog != null) {
                messageLog.warning(Message.MessageType.MALFORMED_CSS_RULE, chunk.trim());
            }
        }

        return rules;
    }

    /**
     * Converts the provided collection of CSS properties to a {@link Map} with keys being property names and values
     * being {@link CssProperty} objects.
     *
     * @param rules
     *            the rules
     *
     * @return the map
     */
    public static Map<String, CssProperty> propertiesAsMap(Collection<CssProperty> rules) {
        final Map<String, CssProperty> result = new LinkedHashMap<>();
        for (final CssProperty cssProperty : rules) {
            result.put(cssProperty.rule, cssProperty);
        }
        return result;
    }

    /**
     * Returns the value of a CSS property if it exists, <code>null</code> otherwise.
     *
     * @param rules
     *            the rules
     * @param property
     *            the property
     *
     * @return the value
     */
    public static String getValue(Map<String, CssProperty> rules, String property) {
        final CssProperty cssProperty = rules.get(property);
        if (cssProperty != null) {
            return cssProperty.value;
        }
        return null;
    }

    /**
     * Returns <code>true</code> if the the provided map contains a property with the specified name that has a
     * non-blank value.
     *
     * @param properties
     *            the properties
     * @param propertyName
     *            the property name
     *
     * @return true, if successful
     */
    public static boolean hasNonBlankValue(Map<String, CssProperty> properties, String propertyName) {
        return properties.containsKey(propertyName) && StringUtils.isNotBlank(properties.get(propertyName).value);
    }

    /**
     * Extracts the actual url from the CSS url expression like <code>url('actua_url')</code>.
     *
     * @param urlValue
     *            the url value
     *
     * @return the string
     */
    public static String unpackUrl(String urlValue) {
        return unpackUrl(urlValue, null);
    }

    /**
     * Extracts the actual url from the CSS url expression like <code>url('actua_url')</code> and logs warnings to the
     * provided {@link MessageLog}.
     *
     * @param urlValue
     *            the url value
     * @param messageLog
     *            the message log
     *
     * @return the string
     */
    public static String unpackUrl(String urlValue, MessageLog messageLog) {
        final Matcher matcher = URL_PATTERN.matcher(urlValue);
        if (!matcher.matches()) {
            if (messageLog != null) {
                messageLog.warning(MessageType.MALFORMED_URL, urlValue);
            }
            return null;
        }
        final String url = matcher.group(2);
        return url != null ? url.trim() : url;
    }

    /**
     * Parses a hexadecimal format (#fff or #ffffff) of CSS color into a {@link Color} object. The RGB format (rgb(100,
     * 0, 0)) is currently not supported. In case of parse errors, the default is returned
     *
     * @param colorValue
     *            the color value
     * @param messageLog
     *            the message log
     * @param defaultColor
     *            the default color
     *
     * @return the color
     */
    public static Color parseColor(String colorValue, MessageLog messageLog, Color defaultColor) {
        final Matcher matcher = COLOR_PATTERN.matcher(colorValue);
        if (!matcher.matches()) {
            if (messageLog != null) {
                messageLog.warning(MessageType.MALFORMED_COLOR, colorValue);
            }
            return defaultColor;
        }

        return new Color(Integer.parseInt(matcher.group(1), 16));
    }
}
