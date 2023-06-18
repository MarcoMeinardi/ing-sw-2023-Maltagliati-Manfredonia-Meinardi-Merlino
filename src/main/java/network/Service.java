package network;

/**
 * Services that can be called on the server
 */
public enum Service {
    Ping,
    Login,
    LobbyList,
    LobbyCreate,
    LobbyJoin,
    LobbyLeave,
    LobbyUpdate,
    GameStart,
    GameLoad,
    CardSelect,
    GameChatSend,
    ExitGame
}
