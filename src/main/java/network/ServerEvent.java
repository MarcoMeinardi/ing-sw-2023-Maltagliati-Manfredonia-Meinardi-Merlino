package network;

import network.parameters.Message;
import model.ScoreBoard;
import network.parameters.GameInfo;
import network.parameters.Update;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerEvent <T extends Serializable> implements Serializable{
    public enum Type{
        Join,
        Leave,
        Start,
        Pause,
        Resume,
        End,
        Error,
        Update,
        NewMessage,
        LobbyUpdate,
        ExitGame,
        ServerDisconnect
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

    public static ServerEvent Join(String player) {
        return new ServerEvent(Type.Join, player);
    }

    public static ServerEvent Leave(String player) {
        return new ServerEvent(Type.Leave, player);
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
    public static ServerEvent Start(GameInfo data){
        return new ServerEvent(Type.Start, data);
    }

    public static ServerEvent End(ScoreBoard scoreBoard){
        return new ServerEvent(Type.End, scoreBoard);
    }

    public static ServerEvent Error(Exception data){
        return new ServerEvent(Type.Error, data);
    }

    public static ServerEvent Update(Update update){
        return new ServerEvent(Type.Update, update);
    }

    public static ServerEvent NewMessage(Message message){
        return new ServerEvent(Type.NewMessage, message);
    }

    public static ServerEvent LobbyUpdate(ArrayList<String> players){
        return new ServerEvent(Type.LobbyUpdate, players);
    }
    public static ServerEvent ExitGame() {
        return new ServerEvent(Type.ExitGame, null);
    }
    public static ServerEvent ServerDisconnect() {
        return new ServerEvent(Type.ServerDisconnect, null);
    }
}
