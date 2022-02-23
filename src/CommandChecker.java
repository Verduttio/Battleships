package com.company;

public class CommandChecker {
    public static boolean checkMessage(String message) {
        return message.matches("((start|missed|hit|downed);[A-J][0-9])|(last downed)");
    }

    public static String getCommand(String message) {
        if(message.contains(";"))
            return message.substring(0, message.indexOf(';'));
        else return message;
    }

    public static String getAttackingPole(String message) {
        return message.substring(message.indexOf(';')+1);
    }


}
