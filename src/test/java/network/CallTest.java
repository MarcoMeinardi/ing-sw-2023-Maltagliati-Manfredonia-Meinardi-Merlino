package network;

import junit.framework.TestCase;

import java.util.UUID;

public class CallTest extends TestCase {

    public void testParams() {
        Call<String> call = new Call<>("params", Service.Login, java.util.UUID.randomUUID());
        assertEquals("params", call.params());
    }

    public void testService() {
        Call<String> call = new Call<>("params", Service.Login, java.util.UUID.randomUUID());
        assertEquals(Service.Login, call.service());
    }

    public void testId() {
        UUID uuid = java.util.UUID.randomUUID();
        Call<String> call = new Call<>("params", Service.Login, uuid);
        assertEquals(uuid, call.id());
    }
}