package br.com.ifce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
public class Player implements Comparable<Player>, Serializable {
    @Getter
    private final String name;

    @Getter
    private int score;

    public void increaseScore() {
        this.score++;
    }

    @Override
    public int compareTo(Player other) {
        return other.score - this.score;
    }
}
