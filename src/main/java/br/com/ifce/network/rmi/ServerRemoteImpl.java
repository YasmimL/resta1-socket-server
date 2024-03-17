package br.com.ifce.network.rmi;

import br.com.ifce.model.Message;
import br.com.ifce.model.Player;
import br.com.ifce.model.enums.MessageType;
import br.com.ifce.service.GameService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServerRemoteImpl extends UnicastRemoteObject implements ServerRemote {

    private final List<ClientRemote> clients = new ArrayList<>();

    private final RMIRegister register;

    public ServerRemoteImpl(RMIRegister register) throws RemoteException {
        super();
        this.register = register;
    }

    @Override
    public int getTotalClients() {
        return this.clients.size();
    }

    @Override
    public void registerClient(ClientRemote client) throws RemoteException {
        this.clients.add(client);
        var playerKey = String.format("PLAYER %d", this.getTotalClients());

        new Thread(() -> {
            try {
                this.register.addPlayer(playerKey);
                client.onMessage(new Message<>(MessageType.PLAYER_REGISTERED, playerKey));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (this.clients.size() == GameService.TOTAL_PLAYERS) {
                GameService.getInstance().initGame(
                        register.getPlayers().stream()
                                .map(it -> new Player(it, 0))
                                .toList()
                );
            }
        }).start();
    }

    @Override
    public void onMessage(String playerKey, Message<?> message) throws RemoteException {
        this.register.onMessage(playerKey, message);
    }

    @Override
    public void send(Message<?> message) {
        this.clients.forEach(client -> {
            try {
                client.onMessage(message);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
