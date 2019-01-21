package Lesson_6.Client;

import java.io.PrintStream;

public class AlreadyConnectedClient extends Exception{
    @Override
    public void printStackTrace(PrintStream s) {
        System.err.println("Попытка подключиться в один аккуант дважды!");
    }
}
