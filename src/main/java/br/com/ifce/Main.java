package br.com.ifce;

import br.com.ifce.model.Player;
import br.com.ifce.network.Register;
import br.com.ifce.service.GameService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var register = new Register();
        var gameService = GameService.getInstance();
        gameService.setRegister(register);
        gameService.initGame(
                List.of(
                        new Player("PLAYER 1", 0),
                        new Player("PLAYER 2", 0)
                )
        );
    }
}