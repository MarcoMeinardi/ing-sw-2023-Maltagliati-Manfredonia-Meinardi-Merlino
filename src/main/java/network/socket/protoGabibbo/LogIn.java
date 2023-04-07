package network.socket.protoGabibbo;

public class LogIn extends Message{
    private String username;

    public LogIn(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
