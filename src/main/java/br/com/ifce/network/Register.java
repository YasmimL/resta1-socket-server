package br.com.ifce.network;

import br.com.ifce.model.Message;
import br.com.ifce.model.Movement;
import br.com.ifce.service.GameService;

import java.util.List;

public interface Register {
    default void onMessage(String playerKey, Message<?> message) {
        var service = GameService.getInstance();
        switch (message.getType()) {
            case MOVEMENT -> service.handleMovement(playerKey, (Movement) message.getPayload());
            case CHAT -> service.handleChat(playerKey, (String) message.getPayload());
            case PASS_TURN -> service.handlePassTurn(playerKey);
            case GIVE_UP -> service.handleGiveUp(playerKey);
            case RESTART_GAME -> service.restartGame();
        }
    }

    void send(Message<?> message);

    List<String> getPlayers();
}
