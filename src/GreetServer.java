package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GreetServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final String mapFile;

    public GreetServer(String map) {mapFile = map;}

    public void start(int port) {
        try {
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            serverSocket = new ServerSocket(port, 50, addr);
            System.out.println("Server started...");
            System.out.println("Waiting for client to join...");
            clientSocket = serverSocket.accept();
            System.out.println("Client has joined...");
            out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));

            BattleMap battleMap = new BattleMap();
            battleMap.uploadMap(mapFile);
            battleMap.displayMyMap();

            Scanner inConsole = new Scanner(System.in);
            String attackingPole = "A0";  // just for correct pole
            String command = "";
            while(true) {
                System.out.println("Waiting for opponent move...");
                String opponentMessage = in.readLine();
                if (CommandChecker.checkMessage(opponentMessage)) {
                    System.out.println("Opponent message: " + opponentMessage);
                    String opponentCommand = CommandChecker.getCommand(opponentMessage);
                    if(opponentCommand.equals("last downed")) {
                        System.out.println("You won!!!");
                        System.out.println("Game ended.");
                        TimeUnit.SECONDS.sleep(1);
                        stop();
                    } else {

                        String opponentAttackingPole = CommandChecker.getAttackingPole(opponentMessage);

                        command = battleMap.getCommandFromShot(opponentAttackingPole);

                        battleMap.updateOpponentMap(opponentCommand, attackingPole);
                        battleMap.updateMyMap(command, opponentAttackingPole);

                        battleMap.displayMyMap();
                        battleMap.displayOpponentMap();

                        if(command.equals("last downed")) {
                            System.out.println("You lost!!!");
                            System.out.println("Game ended.");
                            out.println(command);
                            TimeUnit.SECONDS.sleep(1);
                            stop();
                        } else {
                            System.out.print(command + ";");
                            attackingPole = inConsole.nextLine();
                            while (!attackingPole.matches("[A-J][0-9]")) {
                                System.out.println("Incorrect pole! Try again.");
                                System.out.print(command + ";");
                                attackingPole = inConsole.nextLine();
                            }

                            String sendMessage = command + ';' + attackingPole;
                            out.println(sendMessage);
                        }
                    }
                } else {
                    System.err.println("An unexpected error occurred. Please contact with the application team.");
                    System.exit(3);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void stop() {
        try{
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}