package br.com.ifce.network;

import br.com.ifce.model.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@RequiredArgsConstructor
public class PlayerThread extends Thread {

    private final Socket socket;

    private final Register register;

    @Getter
    private final String playerKey;

    @Setter
    private boolean interrupted;

    @Override
    public void run() {
        try {
            while (!this.interrupted) {
                var inputStream = new ObjectInputStream(this.socket.getInputStream());
                this.register.onMessage(this.playerKey, (Message<?>) inputStream.readObject());
            }
        } catch (Exception e) {
            this.close();
            e.printStackTrace();
        }
    }

    public void send(Message<?> message) {
        try {
            var outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            outputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        this.interrupted = true;
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
