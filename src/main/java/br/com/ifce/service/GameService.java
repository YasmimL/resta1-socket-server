package br.com.ifce.service;

import br.com.ifce.model.GameState;
import br.com.ifce.model.Message;
import br.com.ifce.model.Player;
import br.com.ifce.model.enums.MessageType;
import br.com.ifce.network.Register;
import br.com.ifce.repository.GameRepository;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private int[][] getBoard() {
        final int rows = 7;
        final int columns = 7;
        final int[][] board = new int[rows][columns];
        final var middleSpot = 3;
        final var invalidSpots = new int[]{0, 1, 5, 6};

        IntStream.range(0, rows).forEach(row ->
                IntStream.range(0, columns).forEach(column -> board[row][column] = 1)
        );

        board[middleSpot][middleSpot] = 0;

        for (int row : invalidSpots) {
            for (int column : invalidSpots) {
                board[row][column] = -1;
            }
        }

        return board;
    }

    public void initGame(List<Player> players) {
        if (players.size() != 2) throw new IllegalArgumentException("Two players are necessary to start a new match");

        this.repository = new GameRepository(
                this.getBoard(),
                players.stream().collect(Collectors.toMap(Player::getName, player -> player)),
                players.get((int) Math.round(Math.random())).getName()
        );

        var message = new Message<>(MessageType.START_GAME, new GameState(
                this.repository.getBoard(),
                this.repository.getCurrentPlayer()
        ));
        register.send(message);
    }
}
