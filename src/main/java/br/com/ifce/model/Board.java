package br.com.ifce.model;

import lombok.Getter;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board {

    @Getter
    private final int totalRows = 7;

    @Getter
    private final int totalColumns = 7;

    @Getter
    private final int middleSpot = 3;

    @Getter
    private final Peg[][] board;

    public Board() {
        this.board = new Peg[this.totalRows][this.totalColumns];
        IntStream.range(0, this.totalRows).forEach(row ->
                IntStream.range(0, this.totalColumns).forEach(column -> board[row][column] = new Peg(
                        row,
                        column,
                        1,
                        this
                ))
        );

        this.board[middleSpot][middleSpot] = new Peg(
                middleSpot,
                middleSpot,
                0,
                this
        );

        final int[] invalidSpots = new int[]{0, 1, 5, 6};
        for (int row : invalidSpots) {
            for (int column : invalidSpots) {
                this.board[row][column] = new Peg(
                        row,
                        column,
                        -1,
                        this
                );
            }
        }
    }

    public List<Peg> getPegs() {
        return Stream.of(this.board).flatMap(Stream::of).toList();
    }

    public Peg get(int row, int column) {
        return this.board[row][column];
    }

    public int[][] getNumericBoard() {
        var board = new int[this.totalRows][this.totalColumns];

        IntStream.range(0, this.totalRows).forEach(row ->
                IntStream.range(0, this.totalColumns).forEach(column ->
                        board[row][column] = this.board[row][column].getValue()
                )
        );

        return board;
    }

    public int getLastRow() {
        return this.totalRows - 1;
    }

    public int getLastColumn() {
        return this.totalColumns - 1;
    }

    public boolean validateMove(Movement movement) {
        if (movement.getSource().length != 2) return false;
        if (movement.getTarget().length != 2) return false;

        var sourceRow = movement.getSource()[0];
        var sourceColumn = movement.getSource()[1];
        var targetRow = movement.getTarget()[0];
        var targetColumn = movement.getTarget()[1];

        if (sourceRow < 0 || sourceRow > this.getLastRow()) return false;
        if (sourceColumn < 0 || sourceColumn > this.getLastColumn()) return false;
        if (targetRow < 0 || targetRow > this.getLastRow()) return false;
        if (targetColumn < 0 || targetColumn > this.getLastColumn()) return false;

        var source = this.board[sourceRow][sourceColumn];

        return source.validateMove(movement.getTarget());
    }
}
