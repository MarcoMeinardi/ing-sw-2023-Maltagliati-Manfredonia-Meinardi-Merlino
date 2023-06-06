package network.rpc.client;

import com.sun.jdi.connect.spi.Connection;
import network.Function;
import network.Result;
import network.Server;
import network.Service;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import static org.junit.Assert.*;

import org.junit.Test;

public class NetworkManagerTest {

    /**
     * Should connect to the server
     */
    @Test
    public void testConnectionSuccessful() {
        Server server = new Server("localhost", 8080);
        NetworkManager networkManager = NetworkManager.getInstance();

        try {
            Socket socket = new Socket(server.ip(), server.port());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Function<>(null, Service.Ping).getCall());
            outputStream.flush();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Result<?> result = (Result<?>) inputStream.readObject();
            assertTrue(result.isOk());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Should create a new instance of NetworkManager when no instance exists
     */
    @Test
    public void getInstanceCreatesNewInstanceWhenNoneExists() {
        NetworkManager instance = NetworkManager.getInstance();
        assertNotNull(instance);
        assertEquals(NetworkManager.class, instance.getClass());
    }

    /**
     * Should return the same instance of NetworkManager when called multiple times
     */
    @Test
    public void getInstanceReturnsSameInstance() {
        NetworkManager instance1 = NetworkManager.getInstance();
        NetworkManager instance2 = NetworkManager.getInstance();

        assertSame(instance1, instance2);
    }

}