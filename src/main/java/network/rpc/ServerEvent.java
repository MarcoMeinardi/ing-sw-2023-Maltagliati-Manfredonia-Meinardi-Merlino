package network.rpc;

import java.io.Serializable;

public class ServerEvent <T extends Serializable> implements Serializable{
    protected enum Type{
        Pause,
        Resume,
        Ping,
    }

    public ServerEvent(Type type, T data) {
        this.type = type;
        this.data = data;
    }

    private Type type;

    private T data;

    public Type getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    public static ServerEvent Pause(String data){
        return new ServerEvent(Type.Pause, data);
    }

    public static ServerEvent Resume(String data){
        return new ServerEvent(Type.Resume, data);
    }

    public static ServerEvent Ping(){
        return new ServerEvent(Type.Ping, null);
    }

}
