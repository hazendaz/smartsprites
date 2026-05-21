/*
 * SPDX-License-Identifier: BSD-3-Clause
 * See LICENSE file for details.
 *
 * Copyright 2021-2026 Hazendaz
 * Copyright (C) 2007-2009, Stanisław Osiński.
 */
package org.carrot2.labs.test;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Fail;
import org.carrot2.labs.smartsprites.message.Message;
import org.carrot2.labs.smartsprites.message.Message.MessageLevel;

/**
 * Assertions on lists of {@link Message}s.
 */
public class MessageListAssertion {

    /** The actual message list. */
    private List<Message> actual;

    /**
     * Creates a {@link Message} list assertion object.
     *
     * @param actual
     *            the actual
     */
    public MessageListAssertion(List<Message> actual) {
        this.actual = actual;
    }

    /**
     * Asserts that the current message list contains (at least) the specified messages.
     *
     * @param messages
     *            the messages
     *
     * @return the message list assertion
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
     *
     * @param expected
     *            the expected
     *
     * @return the message list assertion
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
     *
     * @param onlyLevel
     *            the only level
     * @param expected
     *            the expected
     *
     * @return the message list assertion
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
     *
     * @param messages
     *            the messages
     *
     * @return the message list assertion
     */
    public MessageListAssertion isEquivalentTo(Message... messages) {
        return isEquivalentTo(new ArrayList<>(Arrays.asList(messages)));
    }

    /**
     * Asserts that the current message list is equivalent to the provided expected message list.
     *
     * @param onlyLevel
     *            the only level
     * @param messages
     *            the messages
     *
     * @return the message list assertion
     */
    public MessageListAssertion isEquivalentTo(MessageLevel onlyLevel, Message... messages) {
        return isEquivalentTo(onlyLevel, new ArrayList<>(Arrays.asList(messages)));
    }

    /**
     * Does not have messages of level.
     *
     * @param level
     *            the level
     *
     * @return the message list assertion
     */
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

    /**
     * Checks if is empty.
     *
     * @return the message list assertion
     */
    public MessageListAssertion isEmpty() {
        Assertions.assertThat(actual).isEmpty();
        return this;
    }
}
