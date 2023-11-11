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
package org.carrot2.labs.test;

import java.util.List;

import org.carrot2.labs.smartsprites.css.CssProperty;
import org.carrot2.labs.smartsprites.message.Message;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.Lists;

/**
 * Assertions on lists of {@link CssProperty} instances.
 */
public class CssPropertyListAssertion
{
    /** The actual message list */
    private List<CssProperty> actual;

    /**
     * Creates a {@link Message} list assertion object.
     */
    public CssPropertyListAssertion(List<CssProperty> actual)
    {
        this.actual = actual;
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected
     * message list.
     */
    public CssPropertyListAssertion isEquivalentTo(List<CssProperty> properties)
    {
        assertThat(actual).hasSize(properties.size());
        for (int i = 0; i < actual.size(); i++)
        {
            org.carrot2.labs.test.Assertions.assertThat(actual.get(i)).as(
                "property[" + i + "]").isEquivalentTo(properties.get(i));
        }
        return this;
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected
     * message list.
     */
    public CssPropertyListAssertion isEquivalentTo(CssProperty... properties)
    {
        return isEquivalentTo(Lists.newArrayList(properties));
    }

    public CssPropertyListAssertion isEmpty()
    {
        assertTrue(actual.isEmpty());
        return this;
    }

}
