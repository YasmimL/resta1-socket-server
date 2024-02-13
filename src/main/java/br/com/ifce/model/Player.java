package br.com.ifce.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Player {
    @Getter
    private String name;

    @Getter
    private int score;
}
