package Lesson_6.Server;

import Lesson_6.Client.AlreadyConnectedClient;
import Lesson_6.Client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import static java.lang.Thread.sleep;

public class ServerTest {
    private Vector<ClientHandler> clients;

    public ServerTest() throws SQLException {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            AuthService.connect();
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");

            while (true) {
                socket = server.accept();
                ClientHandler ch = new ClientHandler(this,socket);
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Клиент " + ch.getNick() + " подключился");
				//new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler o: clients) {
            if(o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

     public void subscribe(ClientHandler client){ // throws AlreadyConnectedClient {
//         String connectedUser = client.getNick();
//
//         if (clients.size() == 0) {
//             clients.add(client);
//         } else {
//             for (int i = clients.size()-1; i >= 0; i--) {
//                 if (clients.elementAt(i).getNick().equals(connectedUser)) {
//                     throw new AlreadyConnectedClient();
//                 } else clients.add(client);
//
//             }
//         }
		clients.add(client);
        broadcastClientList();
     }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
		broadcastClientList();
    }

    public void broadcastMsg (ClientHandler from, String msg) { //(String msg, String name) {
        for (ClientHandler o: clients) {
			if(!o.checkBlackList(from.getNick())) {
											   
                o.sendMsg(msg);
            }
//            if (name != null) {
//                if (o.getNick().equals(name)) {
//                    o.sendMsg(msg);
//                    break;
//                }
//                continue;
//            }
//            else {
//                String[] myMsg = msg.split(" : ");
//                if (myMsg[0].equals(o.getNick())) o.sendMsg("Я : " + myMsg[1]);
//                else o.sendMsg(msg);
//            }
        }
    }

public void sendPersonalMsg(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o: clients) {
            if(o.getNick().equals(nickTo)) {
                o.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("to " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден!");
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");

        for (ClientHandler o : clients) {
            sb.append(o.getNick() + " ");
        }

        String out = sb.toString();
        for (ClientHandler o : clients) {
            o.sendMsg(out);
        }
    }
}
