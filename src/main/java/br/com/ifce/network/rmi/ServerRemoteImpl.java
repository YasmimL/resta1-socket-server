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
    public synchronized String registerClient(ClientRemote client) throws RemoteException {
        this.clients.add(client);
        this.addShutdownHook(client);
        var playerKey = String.format("PLAYER %d", this.getTotalClients());
        this.register.addPlayer(playerKey);

        if (this.clients.size() == GameService.TOTAL_PLAYERS) {
            new Thread(() -> GameService.getInstance().initGame(
                    this.register.getPlayers().stream()
                            .map(it -> new Player(it, 0))
                            .toList()
            )).start();
        }

        return playerKey;
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

    private void addShutdownHook(ClientRemote client) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                client.onMessage(new Message<>(MessageType.CLOSE, null));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }));
    }
}
