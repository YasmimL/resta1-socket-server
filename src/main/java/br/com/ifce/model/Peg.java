package br.com.ifce.model;

import br.com.ifce.model.enums.MoveDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public class Peg {
    @Getter
    private final int row;

    @Getter
    private final int column;

    @Getter
    private int value;

    @Getter
    private final Board board;

    public boolean isInvalidSpot() {
        return this.value == -1;
    }

    public boolean isSpotEmpty() {
        return this.value == 0;
    }

    public boolean isSpotBusy() {
        return this.value == 1;
    }

    public void empty() {
        this.value = 0;
    }

    public void fill() {
        this.value = 1;
    }

    public boolean canMove() {
        var validMoves = new int[][]{
                new int[]{this.row - 2, this.column},
                new int[]{this.row, this.column + 2},
                new int[]{this.row + 2, this.column},
                new int[]{this.row, this.column + 2}
        };

        return Stream.of(validMoves).anyMatch(move -> this.validateMove(move[0], move[1]));
    }

    public boolean validateMove(int[] target) {
        return this.validateMove(target[0], target[1]);
    }

    public boolean validateMove(int targetRow, int targetColumn) {
        if (this.isInvalidSpot() || this.isSpotEmpty()) return false;
        if (targetRow < 0 || targetRow > this.board.getLastRow()) return false;
        if (targetColumn < 0 || targetColumn > this.board.getLastColumn()) return false;

        var targetSpot = this.board.getBoard()[targetRow][targetColumn];
        if (targetSpot.isInvalidSpot() || targetSpot.isSpotBusy()) return false;

        MoveDirection moveDirection = this.getMoveDirection(targetRow, targetColumn);
        if (moveDirection == null) return false;

        return moveDirection == MoveDirection.HORIZONTAL ?
                this.validateHorizontalMove(targetColumn) :
                this.validateVerticalMove(targetRow);
    }

    private boolean validateHorizontalMove(int targetColumn) {
        Peg jumped = this.getHorizontallyJumpedPeg(targetColumn);
        if (jumped != null && !jumped.isInvalidSpot()) {
            return jumped.isSpotBusy();
        }

        return false;
    }

    private boolean validateVerticalMove(int targetRow) {
        Peg jumped = this.getVerticallyJumpedPeg(targetRow);
        if (jumped != null && !jumped.isInvalidSpot()) {
            return jumped.isSpotBusy();
        }

        return false;
    }

    public MoveDirection getMoveDirection(int targetRow, int targetColumn) {
        if (targetRow == this.row && (targetColumn == this.column - 2 || targetColumn == this.column + 2)) {
            return MoveDirection.HORIZONTAL;
        }
        if (targetColumn == this.column && (targetRow == this.row - 2 || targetRow == this.row + 2)) {
            return MoveDirection.VERTICAL;
        }

        return null;
    }

    public Peg getJumpedPeg(int[] target) {
        return this.getJumpedPeg(target[0], target[1]);
    }

    public Peg getJumpedPeg(int targetRow, int targetColumn) {
        var moveDirection = this.getMoveDirection(targetRow, targetColumn);

        if (moveDirection == null) return null;

        return moveDirection == MoveDirection.HORIZONTAL ?
                this.getHorizontallyJumpedPeg(targetColumn) :
                this.getVerticallyJumpedPeg(targetRow);
    }

    public Peg getHorizontallyJumpedPeg(int targetColumn) {
        if (targetColumn < this.column) {
            return this.board.getBoard()[this.row][this.column - 1];
        }
        if (targetColumn > this.column) {
            return this.board.getBoard()[this.row][this.column + 1];
        }

        return null;
    }

    public Peg getVerticallyJumpedPeg(int targetRow) {
        if (targetRow < this.row) {
            return this.board.getBoard()[this.row - 1][this.column];
        }
        if (targetRow > this.row) {
            return this.board.getBoard()[this.row + 1][this.column];
        }

        return null;
    }
}
