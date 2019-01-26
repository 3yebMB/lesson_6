package Lesson_6.Client;

import Lesson_6.Server.AuthService;
import Lesson_6.Server.ServerTest;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class ClientHandler {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ServerTest server;
    private String nick;
    private String[] pMsg;
    private boolean newClient = true;
    private long timeDoNothing;
    ArrayList<String> blackList;

    public ClientHandler(ServerTest server, Socket socket) {
        timeDoNothing = System.currentTimeMillis();

        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
			this.blackList = new ArrayList<>();

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
									if(!server.isNickBusy(newNick)) {								 
                                    sendMsg("/authok");
                                    sendMsg("Приветствую тебя, "+newNick);
                                    nick = newNick;
//                                    try {
                                        server.subscribe(ClientHandler.this);
//                                    }
//                                    catch (AlreadyConnectedClient acc) {
//                                        acc.printStackTrace();
//                                        return;
//                                    }
                                    break;
									}
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
                                server.sendPersonalMsg(ClientHandler.this, pMsg[1], pMsg[2]);    //server.broadcastMsg(pMsg[2], nick + " : " + pMsg[1]);
                            }
							if(str.startsWith("/blacklist ")) {
                                    String[] tokens = str.split(" ");
                                    blackList.add(tokens[1]);
                                    sendMsg("Вы добавили пользователя " + tokens[1] + " в черный список");
                                }
                            else server.broadcastMsg(ClientHandler.this,nick + ": " + str);
                                //server.broadcastMsg(nick + " : " + str, null);
                        }
                    } catch (SocketException se) {
                        System.out.println("Клиент " + nick + " отключился.");
                    } catch (IOException e) {
                        e.printStackTrace();
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

    public boolean checkBlackList(String nick) {
        return blackList.contains(nick);
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
