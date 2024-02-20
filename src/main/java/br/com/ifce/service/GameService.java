package br.com.ifce.service;

import br.com.ifce.model.*;
import br.com.ifce.model.enums.MessageType;
import br.com.ifce.network.Register;
import br.com.ifce.repository.GameRepository;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class GameService {
    public static final int TOTAL_PLAYERS = 2;

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
        if (players.size() != TOTAL_PLAYERS)
            throw new IllegalArgumentException("Two players are necessary to start a new match");

        this.repository = new GameRepository(
                new Board(),
                players.stream().collect(Collectors.toMap(Player::getName, player -> player)),
                players.get((int) Math.round(Math.random())).getName()
        );

        var message = new Message<>(MessageType.START_GAME, new GameState(
                this.repository.getBoard().getNumericBoard(),
                this.repository.getCurrentPlayer(),
                this.getGameScore()
        ));
        register.send(message);
    }

    public void handleMovement(String playerKey, Movement movement) {
        if (!playerKey.equals(this.getCurrentPlayer())) return;

        var board = this.repository.getBoard();
        var validMove = board.validateMove(movement);

        if (!validMove) {
            this.register.send(new Message<>(MessageType.INVALID_MOVEMENT, movement));
            return;
        }

        this.handleHit(movement);
    }

    private void handleHit(Movement movement) {
        var board = this.repository.getBoard();

        var source = board.get(movement.getSource()[0], movement.getSource()[1]);
        source.empty();

        var jumped = source.getJumpedPeg(movement.getTarget());
        jumped.empty();

        var target = board.get(movement.getTarget()[0], movement.getTarget()[1]);
        target.fill();

        this.repository.getPlayers().get(this.getCurrentPlayer()).increaseScore();

        var message = new Message<>(MessageType.HIT, new GameState(
                this.repository.getBoard().getNumericBoard(),
                this.repository.getCurrentPlayer(),
                this.getGameScore()
        ));
        register.send(message);

        var isGameFinished = this.checkGameFinished();
        if (isGameFinished) return;

        this.changeTurn();
    }

    private boolean checkGameFinished() {
        var board = this.repository.getBoard();
        var pegs = board.getPegs();

        if (pegs.size() == 1) {
            this.finishGame(MessageType.GAME_COMPLETE);
            return true;
        }

        var availablePegs = pegs.stream().filter(Peg::canMove).count();
        if (availablePegs == 0) {
            this.finishGame(MessageType.GAME_OVER);
            return true;
        }

        return false;
    }

    void finishGame(MessageType messageType) {
        var finalScore = this.getGameScore();
        var winner = finalScore.stream().findFirst().orElse(new Player(null, 0)).getName();
        var message = new Message<>(messageType, new GameReport(
                winner,
                finalScore
        ));
        register.send(message);
    }

    private void changeTurn() {
        var currentPlayer = this.getCurrentPlayer();
        var allPlayers = this.repository.getPlayers().values().stream().map(Player::getName);
        this.repository.setCurrentPlayer(allPlayers
                .filter(player -> !player.equals(currentPlayer))
                .findFirst()
                .orElse(""));

        var message = new Message<>(
                MessageType.CHANGE_TURN,
                this.repository.getCurrentPlayer()
        );
        register.send(message);
    }

    public String getCurrentPlayer() {
        return this.repository.getCurrentPlayer();
    }

    public List<Player> getGameScore() {
        return this.repository.getPlayers().values().stream()
                .sorted()
                .toList();
    }
}
