package network.socket.serverSide;

import java.net.*;

public class Server extends Thread{

    private ServerSocket Server;

    public Server() throws Exception{
        Server = new ServerSocket(4000);
        System.out.println("Server is running...");
        this.start();
    }

    @Override
    public void run() {
        super.run();
        while(true){
            try {
                System.out.println("Waiting for client...");
                Socket socket = Server.accept();
                System.out.println("Client connected");
                Connect c = new Connect(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
