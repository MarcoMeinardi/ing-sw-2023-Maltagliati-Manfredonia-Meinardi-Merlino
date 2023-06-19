package network;

import junit.framework.TestCase;

public class ServerTest extends TestCase {

    public void testIp() {
        Server server = new Server("localhost", 8080);
        assertEquals(server.ip(), "localhost");
    }

    public void testPort() {
        Server server = new Server("localhost", 8080);
        assertEquals(server.port(), 8080);
    }

    public void testServerName() {
        assertEquals(Server.SERVER_NAME, "Server");
    }
}