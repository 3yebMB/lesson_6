package Lesson_6.Server;

import Lesson_6.Client.AlreadyConnectedClient;
import Lesson_6.Client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

import static java.lang.Thread.sleep;

public class ServerTest {
    private Vector<ClientHandler> clients;

    public ServerTest(){
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

     public void subscribe(ClientHandler client) throws AlreadyConnectedClient {
         String connectedUser = client.getNick();

         if (clients.size() == 0) {
             clients.add(client);
         } else {
             for (int i = clients.size()-1; i >= 0; i--) {
                 if (clients.elementAt(i).getNick().equals(connectedUser)) {
                     throw new AlreadyConnectedClient();
                 } else clients.add(client);

             }
         }
     }


    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    public void broadcastMsg(String msg, String name) {
        for (ClientHandler o: clients) {
            if (name != null) {
                if (o.getNick().equals(name)) {
                    o.sendMsg(msg);
                    break;
                }
                continue;
            }
            else {
                String[] myMsg = msg.split(" : ");
                if (myMsg[0].equals(o.getNick())) o.sendMsg("Я : " + myMsg[1]);
                else o.sendMsg(msg);
            }
        }
    }
}
