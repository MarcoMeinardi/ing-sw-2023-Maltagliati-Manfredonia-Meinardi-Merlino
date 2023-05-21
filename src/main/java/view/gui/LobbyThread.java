package view.gui;

import network.ClientStatus;
import network.ServerEvent;

import java.util.Optional;

public class LobbyThread implements Runnable {
    public void run() {
        while (HostLobbyController.state != ClientStatus.Disconnected || HostLobbyController.gameStarted) {
            handleEvent();
        }
    }

    private void handleEvent() {
        Optional<ServerEvent> event = HostLobbyController.networkManager.getEvent();
        if (event.isEmpty()) {
            return; // No event
        }
        switch (event.get().getType()) {
            case Join -> {
                String joinedPlayer = (String)event.get().getData();
                try {
                    HostLobbyController.lobby.addPlayer(joinedPlayer);
                    HostLobbyController.setIsLobbyChanged(true);
                } catch (Exception e) {}  // Cannot happen
                if (!joinedPlayer.equals(HostLobbyController.username)) {
                    System.out.println(joinedPlayer + " joined the lobby");
                }
            }
            case Leave -> {
                String leftPlayer = (String)event.get().getData();
                try {
                    HostLobbyController.lobby.removePlayer(leftPlayer);
                    HostLobbyController.setIsLobbyChanged(true);
                } catch (Exception e) {}  // Cannot happen
                System.out.format("%s left the %s%n", leftPlayer, HostLobbyController.state == ClientStatus.InLobby ? "lobby" : "game");
            }
            case Start -> {
                //TODO Fare il game che inizia
            }
            case Update -> {
                //TODO update
            }
            case End -> {
                //TODO end
            }
            case NewMessage -> {
                //TODO new message
            } case Pause -> {
                //TODO pause
            } case Resume -> {
                //TODO resume
            }
            default -> throw new RuntimeException("Unhandled event");
        }
    }

}
