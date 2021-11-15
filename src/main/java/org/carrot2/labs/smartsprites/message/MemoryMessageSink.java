package org.carrot2.labs.smartsprites.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects messages in a {@link List}, see {@link #messages}.
 */
public class MemoryMessageSink implements MessageSink
{
    /**
     * Contains collected messages.
     */
    public final List<Message> messages = new ArrayList<>();

    public void add(Message message)
    {
        messages.add(message);
    }
}
