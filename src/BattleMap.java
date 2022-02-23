package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BattleMap {
    public char [][] map;
    public char [][] mapOpponent;

    public BattleMap() {
        map = new char[10][10];
        mapOpponent = new char[10][10];
        prepareOpponentMap();
    }

    private void unlockSpaceNextToDownedShip(int row, int column) {
        boolean [][] visited = new boolean[10][10];
        prepareVisitedPlacesArray(visited);
        unlockSpaceDownedShip(row, column, visited);
    }

    private void unlockSpaceDownedShip(int row, int column, boolean [][] visited) {
        visited[row][column] = true;
        // up
        if(row > 0) {
            if(mapOpponent[row-1][column] == '#') {
                if (!visited[row - 1][column]) unlockSpaceDownedShip(row - 1, column, visited);
            }
            else mapOpponent[row-1] [column] = '.';
        }

        // down
        if(row < 9) {
            if(mapOpponent[row+1][column] == '#') {
                if (!visited[row + 1][column]) unlockSpaceDownedShip(row + 1, column, visited);
            }
            else mapOpponent[row+1] [column] = '.';
        }

        // left
        if(column > 0) {
            if(mapOpponent[row][column-1] == '#') {
                if (!visited[row][column - 1]) unlockSpaceDownedShip(row, column - 1, visited);
            }
            else mapOpponent[row] [column-1] = '.';
        }

        // right
        if(column < 9) {
            if(mapOpponent[row][column+1] == '#') {
                if (!visited[row][column + 1]) unlockSpaceDownedShip(row, column + 1, visited);
            }
            else mapOpponent[row] [column+1] = '.';
        }

    }

    public void updateOpponentMap(String command, String attackingPole) {
        int row = getRow(attackingPole);
        int column = getColumn(attackingPole);

        if(command.equals("missed")) {
            mapOpponent[row][column] = '.';
        } else if (command.equals("hit")) {
            mapOpponent[row][column] = '#';
        } else if (command.equals("downed")) {
            mapOpponent[row][column] = '#';
            unlockSpaceNextToDownedShip(row, column);
        }
    }

    public void updateMyMap(String command, String attackingPole) {
        int row = getRow(attackingPole);
        int column = getColumn(attackingPole);

        if(command.equals("missed")) {
            map[row][column] = '~';
        } else if (command.equals("hit")) {
            map[row][column] = '@';
        } else if (command.equals("downed")) {
            map[row][column] = '@';
        }
    }

    public String getCommandFromShot(String attackingPole) {
        int row = getRow(attackingPole);
        int column = getColumn(attackingPole);

        if(map[row][column] == '.') {
            return "missed";
        } else if (map[row][column] == '#' || map[row][column] == '@') {
            if(lastShipPart(row, column)) {
                if (wasLastShipOnMap()) return "last downed";
                else return "downed";
            }
            else return "hit";
        } else if (map[row][column] == '~') {
            return "missed";
        } else return "errorGetCommandFromShot";

    }

    private boolean wasLastShipOnMap() {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(map[i][j] == '#') return false;
            }
        }
        return true;
    }

    private boolean lastShipPart(int row, int column) {
        map[row][column] = '@';
        boolean [][] visitedPlaces = new boolean[10][10];
        prepareVisitedPlacesArray(visitedPlaces);
        return isLastShipPart(row, column, visitedPlaces);
    }

    private boolean isLastShipPart(int row, int column, boolean [][] visited) {
        visited[row][column] = true;
        // up
        boolean up = true;
        if(row > 0) {
            if(map[row-1][column] == '#') return false;
            if(map[row-1][column] == '@' && !visited[row-1][column]) up = isLastShipPart(row-1, column, visited);
        }

        // down
        boolean down = true;
        if(row < 9) {
            if(map[row+1][column] == '#') return false;
            if(map[row+1][column] == '@' && !visited[row+1][column]) down = isLastShipPart(row+1, column, visited);
        }

        // left
        boolean left = true;
        if(column > 0) {
            if(map[row][column-1] == '#') return false;
            if(map[row][column-1] == '@' && !visited[row][column-1]) left = isLastShipPart(row, column-1, visited);
        }

        // right
        boolean right = true;
        if(column < 9) {
            if(map[row][column+1] == '#') return false;
            if(map[row][column+1] == '@' && !visited[row][column+1]) right = isLastShipPart(row, column+1, visited);
        }

        return up&&down&&left&&right;

    }

    private void prepareVisitedPlacesArray(boolean [][] array) {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                array[i][j] = false;
            }
        }
    }

    private int getColumn(String attackingPole) {
        return attackingPole.charAt(1)-48;
    }

    private int getRow(String attackingPole) {
        return attackingPole.charAt(0)-65;
    }

    private void prepareOpponentMap() {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                mapOpponent[i][j] = '?';
            }
        }
    }

    public void displayMyMap() {
        System.out.println("Your map:");
        displayMap(map);
    }

    public void displayOpponentMap() {
        System.out.println("Opponent map:");
        displayMap(mapOpponent);
    }

    private void displayMap(char[][] mapDispl) {
        System.out.println("  0123456789");
        char rowLetter = 65;
        for(int i = 0; i < 10; i++) {
            System.out.print(rowLetter + " ");
            for(int j = 0; j < 10; j++) {
                System.out.print(mapDispl[i][j]);
            }
            System.out.println();
            rowLetter++;
        }
    }

    public void uploadMap(String fileName) {
        Path mapPath = Paths.get(fileName);
        List<String> lines = null;
        try {
            lines = Files.readAllLines(mapPath);
        } catch (IOException e) {
            System.err.println("Error while opening map file: " + fileName);
            e.printStackTrace();
        }

        assert lines != null;
        for(int i = 0; i < 10; i++) {
            String line = lines.get(i);
            for(int j = 0; j < 10; j++) {
                map[i][j] = line.charAt(j);
            }
        }
    }
}
