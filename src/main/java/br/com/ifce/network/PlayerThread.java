package br.com.ifce.network;

import br.com.ifce.model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerThread extends Thread {

    private final Socket socket;

    private final Register register;

    public PlayerThread(Socket socket, Register register) {
        this.socket = socket;
        this.register = register;
    }

    @Override
    public void run() {
        try {
            while (true) {
                var inputStream = new ObjectInputStream(this.socket.getInputStream());
                this.register.onMessage((Message<?>) inputStream.readObject());
            }
        } catch (Exception e) {
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
}
