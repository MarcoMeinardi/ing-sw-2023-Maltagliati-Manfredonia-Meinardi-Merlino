package network;

import network.parameters.Message;
import model.ScoreBoard;
import network.parameters.GameInfo;
import network.parameters.Update;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to represent an event sent by the server to the clients.
 * @param <T> the type of the data sent by the event.
 */
public class ServerEvent <T extends Serializable> implements Serializable{
    /**
     * Enum to represent the type of the event.
     */
    public enum Type{
        Join,
        Leave,
        Start,
        End,
        Error,
        Update,
        NewMessage,
        LobbyUpdate,
        ExitGame,
        ServerDisconnect
    }

    /**
     * Constructor for the class.
     * @param type the type of the event.
     * @param data the data of the event.
     */
    public ServerEvent(Type type, T data) {
        this.type = type;
        this.data = data;
    }

    private Type type;

    private T data;

    /**
     * Getter for the type of the event.
     * @return the type of the event.
     */
    public Type getType() {
        return type;
    }

    /**
     * Getter for the data of the event.
     * @return the data of the event.
     */
    public T getData() {
        return data;
    }

    /**
     * Method to create a new Join event.
     * @param player the player that joined the lobby.
     * @return the new event.
     */
    public static ServerEvent Join(String player) {
        return new ServerEvent(Type.Join, player);
    }

    /**
     * Method to create a new Leave event.
     * @param player the player that left the lobby.
     * @return the new event.
     */
    public static ServerEvent Leave(String player) {
        return new ServerEvent(Type.Leave, player);
    }
    /**
     * Method to create a new Start event.
     * @return the new event.
     */
    public static ServerEvent Start(){
        return new ServerEvent(Type.Start, null);
    }

    /**
     * Method to create a new Start event of a previously started game.
     * @param data info about the game.
     * @return the new event.
     */
    public static ServerEvent Start(GameInfo data){
        return new ServerEvent(Type.Start, data);
    }

    /**
     * Method to create a new End event.
     * @param scoreBoard the scoreboard of the game.
     * @return the new event.
     */
    public static ServerEvent End(ScoreBoard scoreBoard){
        return new ServerEvent(Type.End, scoreBoard);
    }

    /**
     * Method to create a new Error event.
     * @param data the exception that caused the error.
     * @return the new event.
     */
    public static ServerEvent Error(Exception data){
        return new ServerEvent(Type.Error, data);
    }

    /**
     * Method to create a new Update event.
     * @param update the update of the game.
     * @return the new event.
     */
    public static ServerEvent Update(Update update){
        return new ServerEvent(Type.Update, update);
    }

    /**
     * Method to create a new NewMessage event.
     * @param message the message sent by a player.
     * @return the new event.
     */
    public static ServerEvent NewMessage(Message message){
        return new ServerEvent(Type.NewMessage, message);
    }

    /**
     * Method to create a new LobbyUpdate event.
     * @param players the list of players in the lobby.
     * @return the new event.
     */
    public static ServerEvent LobbyUpdate(ArrayList<String> players){
        return new ServerEvent(Type.LobbyUpdate, players);
    }

    /**
     * Method to create a new ExitGame event.
     * @return the new event.
     */
    public static ServerEvent ExitGame() {
        return new ServerEvent(Type.ExitGame, null);
    }

    /**
     * Method to create a new ServerDisconnect event.
     * @return the new event.
     */
    public static ServerEvent ServerDisconnect() {
        return new ServerEvent(Type.ServerDisconnect, null);
    }
}
