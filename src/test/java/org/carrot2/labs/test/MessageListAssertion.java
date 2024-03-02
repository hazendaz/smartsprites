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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Fail;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageLevel;

/**
 * Assertions on lists of {@link Message}s.
 */
public class MessageListAssertion {
    /** The actual message list */
    private List<Message> actual;

    /**
     * Creates a {@link Message} list assertion object.
     */
    public MessageListAssertion(List<Message> actual) {
        this.actual = actual;
    }

    /**
     * Asserts that the current message list contains (at least) the specified messages.
     */
    public MessageListAssertion contains(Message... messages) {
        final Set<Message> toCheck = Sets.newHashSet(messages);
        for (int i = 0; i < actual.size(); i++) {
            for (Iterator<Message> it = toCheck.iterator(); it.hasNext();) {
                final Message message = it.next();
                try {
                    org.carrot2.labs.test.Assertions.assertThat(actual.get(i)).as("message[" + i + "]")
                            .isEquivalentTo(message);
                    it.remove();

                } catch (AssertionError e) {
                    // This means the message wasn't equivalent, ignore
                }
            }
        }

        if (!toCheck.isEmpty()) {
            Fail.fail("Message list did not contain " + toCheck.size() + " of the required messages");
        }

        return this;
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected message list.
     */
    public MessageListAssertion isEquivalentTo(List<Message> expected) {
        Assertions.assertThat(actual).hasSize(expected.size());
        for (int i = 0; i < actual.size(); i++) {
            org.carrot2.labs.test.Assertions.assertThat(actual.get(i)).as("message[" + i + "]")
                    .isEquivalentTo(expected.get(i));
        }
        return this;
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected message list.
     */
    public MessageListAssertion isEquivalentTo(MessageLevel onlyLevel, List<Message> expected) {
        final List<Message> filtered = new ArrayList<>();
        for (Message message : actual) {
            if (message.level == onlyLevel) {
                filtered.add(message);
            }
        }

        final List<Message> actualBackup = actual;
        actual = filtered;
        isEquivalentTo(expected);
        actual = actualBackup;

        return this;
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected message list.
     */
    public MessageListAssertion isEquivalentTo(Message... messages) {
        return isEquivalentTo(Lists.newArrayList(messages));
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected message list.
     */
    public MessageListAssertion isEquivalentTo(MessageLevel onlyLevel, Message... messages) {
        return isEquivalentTo(onlyLevel, Lists.newArrayList(messages));
    }

    public MessageListAssertion doesNotHaveMessagesOfLevel(MessageLevel level) {
        int levelCount = 0;
        final StringBuilder messages = new StringBuilder();
        for (Message message : actual) {
            // Ignore status messages
            if (message.level.equals(MessageLevel.STATUS)) {
                continue;
            }

            if (MessageLevel.COMPARATOR.compare(message.level, level) >= 0) {
                levelCount++;
                messages.append(message.toString());
                messages.append(", ");
            }
        }

        if (levelCount > 0) {
            Fail.fail("Found " + levelCount + " " + level.name() + "+ messages: "
                    + messages.substring(0, messages.length() - 2));
        }

        return this;
    }

    public MessageListAssertion isEmpty() {
        Assertions.assertThat(actual).isEmpty();
        return this;
    }
}
