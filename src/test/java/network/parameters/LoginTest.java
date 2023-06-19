package network.parameters;

import junit.framework.TestCase;

public class LoginTest extends TestCase {

    public void testUsername() {
        Login login = new Login("username");
        assertEquals("username", login.username());
    }
}