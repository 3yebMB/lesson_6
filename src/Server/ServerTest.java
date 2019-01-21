package Lesson_6.Server;

import Lesson_6.Client.ClientHandler;
import Lesson_6.Client.Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

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
                System.out.println("Клиент " + ch.getNick() + " подключился");
//                new ClientHandler(this,socket);
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

     public void subscribe(ClientHandler client) {
//         for (ClientHandler c: clients) {
//             if (!c.getNick().equals(client.getNick())){
                 clients.add(client);
//             }
//         }

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
                o.sendMsg(msg);
            }

        }
    }
}
