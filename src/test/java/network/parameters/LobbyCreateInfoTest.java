package network.parameters;

import junit.framework.TestCase;

public class LobbyCreateInfoTest extends TestCase {

    public void testName() {
        LobbyCreateInfo lobbyCreateInfo = new LobbyCreateInfo("Lobby");
        assertEquals("Lobby", lobbyCreateInfo.name());
    }
}