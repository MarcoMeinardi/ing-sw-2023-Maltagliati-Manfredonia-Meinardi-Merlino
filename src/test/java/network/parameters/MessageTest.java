package network.parameters;

import junit.framework.TestCase;

import java.util.Optional;

public class MessageTest extends TestCase {

    public void testIdSender() {
        Message message = new Message("idSender", "message", "idReceiver");
        assertEquals("idSender", message.idSender());
    }

    public void testMessage() {
        Message message = new Message("idSender", "message", "idReceiver");
        assertEquals("message", message.message());
    }

    public void testIdReceiver() {
        Message globalMessage = new Message("idSender", "message");
        Message specificMessage = new Message("idSender", "message", "idReceiver");
        Optional<String> emptyOptional = Optional.empty();
        Optional<String> idReceiver = Optional.of("idReceiver");
        assertEquals(emptyOptional, globalMessage.idReceiver());
        assertEquals(idReceiver, specificMessage.idReceiver());
    }
}