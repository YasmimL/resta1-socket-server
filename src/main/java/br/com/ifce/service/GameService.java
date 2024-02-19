package br.com.ifce.service;

import br.com.ifce.model.Board;
import br.com.ifce.model.GameState;
import br.com.ifce.model.Message;
import br.com.ifce.model.Movement;
import br.com.ifce.model.Player;
import br.com.ifce.model.enums.MessageType;
import br.com.ifce.network.Register;
import br.com.ifce.repository.GameRepository;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class GameService {

    private GameRepository repository;

    @Setter
    private Register register;

    private static GameService instance = GameService.getInstance();

    private GameService() {
    }

    public static GameService getInstance() {
        if (instance == null) instance = new GameService();

        return instance;
    }

    public void initGame(List<Player> players) {
        if (players.size() != 2) throw new IllegalArgumentException("Two players are necessary to start a new match");

        this.repository = new GameRepository(
                new Board(),
                players.stream().collect(Collectors.toMap(Player::getName, player -> player)),
                players.get((int) Math.round(Math.random())).getName()
        );

        var message = new Message<>(MessageType.START_GAME, new GameState(
                this.repository.getBoard().getNumericBoard(),
                this.repository.getCurrentPlayer()
        ));
        register.send(message);
    }

    public void handleMovement(Movement movement) {
        var board = this.repository.getBoard();
        var validMove = board.validateMove(movement);

        if (!validMove) {
            this.register.send(new Message<>(MessageType.INVALID_MOVEMENT, movement));
            return;
        }

        var source = board.get(movement.getSource()[0], movement.getSource()[1]);
        source.empty();

        var jumped = source.getJumpedPeg(movement.getTarget());
        jumped.empty();

        var target = board.get(movement.getTarget()[0], movement.getTarget()[1]);
        target.fill();

        var message = new Message<>(MessageType.HIT, new GameState(
                this.repository.getBoard().getNumericBoard(),
                this.repository.getCurrentPlayer()
        ));
        register.send(message);
    }
}
