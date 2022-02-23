package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final String mapFile;

    public GreetClient(String map) {mapFile = map;}

    public void startConnection(String ip, int port) {
        try{
            clientSocket = new Socket(ip, port);
            System.out.println("Successfully connected with server...");
            out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));

            BattleMap battleMap = new BattleMap();
            battleMap.uploadMap(mapFile);
            battleMap.displayMyMap();


            Scanner inConsole = new Scanner(System.in);
            String command = "start";
            battleMap.displayOpponentMap();

            while(true) {
                System.out.print(command + ";");
                String attackingPole = inConsole.nextLine();
                while(!attackingPole.matches("[A-J][0-9]")) {
                    System.out.println("Incorrect pole! Try again.");
                    System.out.print(command + ";");
                    attackingPole = inConsole.nextLine();
                }
                String sendMessage = command + ';' + attackingPole;
                System.out.println("Waiting for opponent move...");
                String opponentResponse = sendMessage(sendMessage);
                if (CommandChecker.checkMessage(opponentResponse)) {
                    System.out.println("Opponent message: " + opponentResponse);
                    String opponentCommand = CommandChecker.getCommand(opponentResponse);
                    if(opponentCommand.equals("last downed")) {
                        System.out.println("You won!!!");
                        System.out.println("Game ended.");
                        TimeUnit.SECONDS.sleep(1);
                        stopConnection();
                        System.exit(0);
                    } else {
                        String opponentAttackingPole = CommandChecker.getAttackingPole(opponentResponse);

                        command = battleMap.getCommandFromShot(opponentAttackingPole);

                        battleMap.updateMyMap(command, opponentAttackingPole);
                        battleMap.updateOpponentMap(opponentCommand, attackingPole);

                        battleMap.displayMyMap();
                        battleMap.displayOpponentMap();

                        if (command.equals("last downed")) {
                            System.out.println("You lost!!!");
                            System.out.println("Game ended.");
                            sendMessage(command);
                            TimeUnit.SECONDS.sleep(1);
                            stopConnection();
                            System.exit(0);
                        }
                    }
                } else {
                    System.err.println("An unexpected error occurred. Please contact with the application team.");
                    System.exit(4);
                }
            }



        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String sendMessage(String msg) {
        try{
            out.println(msg);
            String resp = in.readLine();
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public void stopConnection() {
        try{
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}