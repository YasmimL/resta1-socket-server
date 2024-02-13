package br.com.ifce.repository;

import br.com.ifce.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public class GameRepository {

    @Getter
    private int[][] board;

    @Getter
    private Map<String, Player> players;

    @Getter
    private String currentPlayer;
}
