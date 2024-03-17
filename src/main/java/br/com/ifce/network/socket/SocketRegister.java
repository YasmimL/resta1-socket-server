package br.com.ifce.network.socket;

import br.com.ifce.model.Message;
import br.com.ifce.model.enums.MessageType;
import br.com.ifce.network.Register;
import br.com.ifce.service.GameService;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class SocketRegister implements Register {

    private final List<PlayerThread> playerThreads = new ArrayList<>();

    public SocketRegister() {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            IntStream.range(0, GameService.TOTAL_PLAYERS).forEach((i) -> {
                try {
                    var socket = serverSocket.accept();
                    var thread = new PlayerThread(socket, this, String.format("PLAYER %d", i + 1));
                    this.playerThreads.add(thread);
                    thread.start();
                    thread.send(new Message<>(MessageType.PLAYER_REGISTERED, thread.getPlayerKey()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(Message<?> message) {
        this.playerThreads.forEach(t -> t.send(message));
    }

    public List<String> getPlayers() {
        return this.playerThreads.stream().map(PlayerThread::getPlayerKey).toList();
    }
}
