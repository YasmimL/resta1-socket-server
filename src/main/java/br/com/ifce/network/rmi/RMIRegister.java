package br.com.ifce.network.rmi;

import br.com.ifce.model.Message;
import br.com.ifce.network.Register;
import br.com.ifce.service.GameService;
import lombok.Getter;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIRegister implements Register {

    private final ServerRemote server;

    @Getter
    private final List<String> players = new ArrayList<>();

    public RMIRegister() {
        try {
            this.server = new ServerRemoteImpl(this);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("Server", this.server);
            GameService.getInstance().setRegister(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(Message<?> message) {
        try {
            this.server.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void addPlayer(String player) {
        this.players.add(player);
    }
}
