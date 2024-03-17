package br.com.ifce;

import br.com.ifce.model.Player;
import br.com.ifce.network.rmi.RMIRegister;
import br.com.ifce.network.socket.SocketRegister;
import br.com.ifce.service.GameService;

public class Main {
    public static void main(String[] args) {
//        var register = new SocketRegister();
        var register = new RMIRegister();
//        var gameService = GameService.getInstance();
//        gameService.setRegister(register);
//        gameService.initGame(
//                register.getPlayers().stream()
//                        .map(it -> new Player(it, 0))
//                        .toList()
//        );
    }
}