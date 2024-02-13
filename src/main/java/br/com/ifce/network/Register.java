package br.com.ifce.network;

import br.com.ifce.model.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Register {

    private final List<PlayerThread> playerThreads = new ArrayList<>();

    public Register() {
        int port = 5000;
        int totalPlayers = 2;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            IntStream.range(0, totalPlayers).forEach(($) -> {
                try {
                    var socket = serverSocket.accept();
                    var thread = new PlayerThread(socket, this);
                    this.playerThreads.add(thread);
                    thread.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMessage(Message<?> message) {
//        this.playerThreads.forEach(t -> t.print("SERVER: " + message));
    }

    public void send(Message<?> message) {
        this.playerThreads.forEach(t -> t.send(message));
    }
}
