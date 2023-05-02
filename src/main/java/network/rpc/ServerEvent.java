package network.rpc;

import network.rpc.parameters.Message;
import model.ScoreBoard;
import network.rpc.parameters.Update;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerEvent <T extends Serializable> implements Serializable{
    public enum Type{
        Pause,
        Resume,
        Start,
        End,
        Error,
        Update,
        NextTurn,
        NewMessage,
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

    public static ServerEvent Start(ArrayList<String> players){
        return new ServerEvent(Type.Start, players);
    }

    public static ServerEvent End(ScoreBoard scoreBoard){
        return new ServerEvent(Type.End, scoreBoard);
    }

    public static ServerEvent Error(Exception data){
        return new ServerEvent(Type.Error, data);
    }

    public static ServerEvent Update(Update update){ return new ServerEvent(Type.Update, update);}

    public static ServerEvent NextTurn(String idPlayer){ return new ServerEvent(Type.NextTurn, idPlayer);}

    public static ServerEvent NewMessage(Message message){ return new ServerEvent(Type.NewMessage, message);}
}
