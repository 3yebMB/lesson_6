package Lesson_6.Client;

import Lesson_6.Server.AuthService;
import Lesson_6.Server.ServerTest;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ServerTest server;
    private String nick;
    private String[] pMsg;

    public ClientHandler(ServerTest server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();

                            if (str.startsWith("/auth")) {
                                String[] tokens = str.split(" ");
                                String newNick = AuthService.getNickLoginAndPass(tokens[1], tokens[2]);
                                if (newNick != null) {
                                    sendMsg("/authok");
                                    nick = newNick;
                                    server.subscribe(ClientHandler.this);
                                    break;
                                } else {
                                    sendMsg("Неверный логин/пароль!");
                                }
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            if(str.equals("/end")) {
                                out.writeUTF("/serverClosed");
                                break;
                            }
                            if (str.contains("/w")){
                                pMsg = str.split(" ", 3);
                                //if (pMsg[1].equals(nick))
                                    server.broadcastMsg(pMsg[2], pMsg[1]);
                            }
                            else
                                server.broadcastMsg(str, null);
                        }
                    } catch (SocketException se) {
//                        se.printStackTrace();
                        System.out.println("Клиент " + nick + " отключился.");
                    } catch (IOException e) {
                        e.printStackTrace();
//                        System.out.println("2");
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.unsubscribe(ClientHandler.this);
                    }

                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseMsg(String str) {
    }

    public String getNick() {
        return nick;
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
