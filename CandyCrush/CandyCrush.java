package CandyCrush;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import CandyCrush.Solve.Axis;

public class CandyCrush {
    private PrintStream pStream;
    private Scanner s;

    public CandyCrush(InputStream in, PrintStream out) {
        this.pStream = out;
        this.s = new Scanner(in);
    }

    protected void finalize() {
        this.s.close();
    }

    public void StartGame() {
        String[][] gameBoard = new String[10][10];
        fillRandom(new String[]{
            ANSIColors.ANSI_RED + "@" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_BLUE + "#" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_YELLOW + "$" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_GREEN + "%" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_PURPLE + "&" + ANSIColors.ANSI_RESET
        }, gameBoard);

        println(getBoardString(gameBoard));
        Solve[] solves = findSolves(gameBoard);
        for (int i = 0; i < solves.length; i++) {
            println(solves[i]);
        }
        int points = removeSolved(gameBoard, solves);

        println(getBoardString(gameBoard));
        println("Points: " + points);
        String[][][] animate = gravityDown(gameBoard);
        println("After gravity: ");
        println(getBoardString(gameBoard));
        
        for(int i = 0; i < animate.length; i++){
            String[][] frame = new String[animate[0].length][animate[0][0].length];
            for(int j = 0; j < animate[i].length; j++){
                for(int k = 0; k < animate[i][j].length; k++){
                    frame[j][k] = animate[i][j][k];
                }
            }
            println("Frame " + i + ":");
            println(getBoardString(frame));
        }
    }

    //moves all pieces down when there is something empty underneath
    //input array is modified
    //returns a 3d matrix containing the animation of gravity
    private String[][][] gravityDown(String[][] board){
        ArrayList<String[][]> frames = new ArrayList<String[][]>();
        while(true){
            //move down 1 spot
            for(int i = (board.length-1); i >= 0; i--) {
                for(int j = 0; j < board[i].length; j++) {
                    if(i+1 >= board.length) continue;
                    if(board[i+1][j].equals(" ")) {
                        board[i+1][j] = board[i][j];
                        board[i][j] = " ";
                    }
                }
            }
            //copy
            String[][] tempboard = new String[board.length][board[0].length];
            for(int i = 0; i < board.length; i++) {
                for(int j = 0; j < board[0].length; j++){
                    tempboard[i][j] = board[i][j];
                }
            }
            frames.add(tempboard);
            //check if move down possible
            boolean reloop = false;
            for(int i = 0; i < board[0].length; i++) {
                boolean start = false;
                for(int j = board.length-1; j >= 0; j--) {
                    if(board[j][i].equals(" ")) {
                        start = true;
                        continue;
                    }
                    if(start) {
                        if(!board[j][i].equals(" ")) {
                            reloop = true;
                        }
                    }
                }
            }
            if(!reloop) break;
        }
        return frames.toArray(new String[frames.size()][board.length][board[0].length]);
    }

    //Returns the amount of points gained from removing solutions
    private int removeSolved(String[][] board, Solve[] solves) {
        int points = 0;
        for(int i = 0; i < solves.length; i++) {
            Solve sol = solves[i];
            if(sol.axis == Axis.Row){
                for(int j = sol.beginIdx; j <= sol.endIdx; j++) {
                    board[sol.axisIdx][j] = " ";
                    points++;
                }
            } else if(solves[i].axis == Axis.Col) {
                for(int j = sol.beginIdx; j <= sol.endIdx; j++) {
                    board[j][sol.axisIdx] = " ";
                    points++;
                }
            }
        }
        return points;
    }

    //------------------------------IN DESPERATE NEED FOR SIMPLIFICATION-------------------------------
    //returns an array of Solves in the board, see solve class
    private Solve[] findSolves(String[][] board) {
        ArrayList<Solve> solves = new ArrayList<Solve>();
        Solve temp = new Solve();

        // check through rows
        temp.axis = Solve.Axis.Row;
        for (int i = 0; i < board.length; i++) {
            temp.axisIdx = i;
            temp.beginIdx = 0;
            String matchString = board[i][0];
            int matchCount = 0;
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].equals(matchString)) {
                    matchCount++;
                } else {
                    if (matchCount >= 3) {// solve found
                        temp.endIdx = j - 1;
                        solves.add(new Solve(temp));
                    }
                    temp.beginIdx = j;
                    matchCount = 1;
                    matchString = board[i][j];
                }
                if (j == board[i].length-1) {
                    if (matchCount >= 3) {// solve found, identical as above
                        temp.endIdx = j - 1;
                        solves.add(new Solve(temp));
                    }
                    temp.beginIdx = j;
                    matchCount = 1;
                    matchString = board[i][j];
                }
            }

        }

        // check through columns
        temp.axis = Solve.Axis.Col;
        for (int i = 0; i < board[0].length; i++) {
            temp.axisIdx = i;
            temp.beginIdx = 0;
            String matchString = board[0][i];
            int matchCount = 0;
            for (int j = 0; j < board.length; j++) {
                if (board[j][i].equals(matchString)) {
                    matchCount++;
                } else {
                    if (matchCount >= 3) {// solve found
                        temp.endIdx = j - 1;
                        solves.add(new Solve(temp));
                    }
                    temp.beginIdx = j;
                    matchCount = 1;
                    matchString = board[j][i];
                }
                if (j == board.length-1) {
                    if (matchCount >= 3) {// solve found, identical as above
                        temp.endIdx = j - 1;
                        solves.add(new Solve(temp));
                    }
                    temp.beginIdx = j;
                    matchCount = 1;
                    matchString = board[j][i];
                }
            }
        }
        return solves.toArray(new Solve[solves.size()]);
    }
    //^^^^^^^^^^^^^^^^^^^^^^^^IN DESPERATE NEED FOR SIMPLIFICATION^^^^^^^^^^^^^^^^^^^^^^^^

    enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }
    //swap
    private void swapPieces(String[][] board, int row, int col, Direction dir) {
        String swap;
        switch (dir) {
            case UP:
                if(row-1 < 0) throw new RuntimeException("Swap to outside of board detected.");
                swap = board[row][col];
                board[row][col] = board[row-1][col];
                board[row-1][col] = swap;
                break;
            case DOWN:
                if(row+1 >= board.length) throw new RuntimeException("Swap to outside of board detected.");
                swap = board[row][col];
                board[row][col] = board[row+1][col];
                board[row+1][col] = swap;
                break;
            case LEFT:
                if(col-1 < 0) throw new RuntimeException("Swap to outside of board detected.");
                swap = board[row][col];
                board[row][col] = board[row][col-1];
                board[row][col-1] = swap;
                break;
            case RIGHT:
                if(col+1 > board[0].length) throw new RuntimeException("Swap to outside of board detected.");
                swap = board[row][col];
                board[row][col] = board[row][col+1];
                board[row][col+1] = swap;
                break;
            default:
                break;
        }
    }

    //replaces empty spots with new pieces
    private void repopulateEmpty(String[][] board, String[] availableCharacters) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if(board[i][j].equals(" ")) {
                    board[i][j] = availableCharacters[(int) (Math.random() * availableCharacters.length)];
                }
            }
        }
    }

    //fills array with random values
    private void fillRandom(String[] availableCharacters, String[][] out) {
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[i].length; j++) {
                out[i][j] = availableCharacters[(int) (Math.random() * availableCharacters.length)];
            }
        }
    }

    //returns a string form of a board
    private String getBoardString(String[][] board) {
        String s = "";
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                s += board[i][j] + " ";
            }
            s += "\n";
        }
        return s;
    }

    //these are here because i am too lazy to type out system.out.println
    //local print method
    private <T> void print(T a) {
        this.pStream.print(a);
    }

    //local println method
    private <T> void println(T a) {
        this.pStream.println(a);
    }
}
