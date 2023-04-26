package network.rpc;

import model.ScoreBoard;
import network.rpc.client.Server;
import network.rpc.parameters.Update;

import java.io.Serializable;

public class ServerEvent <T extends Serializable> implements Serializable{
    protected enum Type{
        Pause,
        Resume,
        Start,
        End,
        Error,

        UPDATE,
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

    public static ServerEvent Start(){
        return new ServerEvent(Type.Start, null);
    }

    public static ServerEvent End(ScoreBoard scoreBoard){
        return new ServerEvent(Type.End, scoreBoard);
    }

    public static ServerEvent Error(Exception data){
        return new ServerEvent(Type.Error, data);
    }

    public static ServerEvent Update(Update update){ return new ServerEvent(Type.UPDATE, update);}
}
