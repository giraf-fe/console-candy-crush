package CandyCrush;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
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
    
    public void StartGame(int boardsize) {
        
        while(true){
            //input loop here
            String q;
            while(true){
                println("Welcome to Candy Crush!\n"+
                        "1: Start Game\n"+
                        "2: Rules\n"+
                        "Anything else: Exit");

                try {
                    q = this.s.nextLine();
                } catch (NoSuchElementException e) {
                    println("Input is invalid, please try again.");
                    continue;
                }
                break;
            }//nah
            
            if(q.equals("1")){
                InternalStartGame(boardsize);
                s.nextLine();
            } else if(q.equals("2")){
                println("The goal of Candy Crush is simple: you need to match 3+ candies in a row (vertically or horizontally) to gain points."+
                        " Your goal is to make as many points as you can. Points go by how many candies in a row you match. Ex: 3 in a row = 3 points.");
            } else{
                break;
            }
        }
    }


    private void InternalStartGame(int boardsize){
        String[][] gameBoard = new String[boardsize][boardsize];
        //keep generating gameBoard until it is not solved
        String[] pieces =  new String[] { 
            ANSIColors.ANSI_RED + "@" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_BLUE + "#" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_YELLOW + "$" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_GREEN + "%" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_PURPLE + "&" + ANSIColors.ANSI_RESET
        };
        fillRandom(pieces, gameBoard);
        while(true){
            Solve[] solves = findSolves(gameBoard);
            if(solves.length == 0) break;

            removeSolved(gameBoard, solves);
            repopulateEmpty(gameBoard, pieces);
        } 
        
        int score = 0;
        while(true){
            ClearConsole();
            println(getBoardString(gameBoard, true));
            println("Current score: " + score);
            print("Select a square (row col) or 0 0 for exit: ");
            int row, col;
            try {
                row = s.nextInt(); 
                col = s.nextInt();  
            } catch (InputMismatchException e) {
                println("Bad input. Try again with integers.");
                Sleep(1500);
                s.nextInt();
                continue;
            }

            if(row == 0 && col == 0) {
                println("You reached a score of " + score + ".");
                break;
            }

            if(row > boardsize || row < 1 || col > boardsize || col < 1) {
                println("Input is out of range. Select a number on the board.");
                continue;
            }
            
            int direction;
            while(true) {
                print("In which direction would you like to swap?\n"+
                         "1: up\n"+
                         "2: down\n"+
                         "3: left\n"+
                         "4: right\n"+
                         "direction: ");
                try {
                    direction = s.nextInt();
                } catch (InputMismatchException e) {
                    println("Bad input. Try again with integers.");
                    Sleep(2000);
                    s.nextLine();
                    continue;
                }
                if(direction > 4 || direction < 1) {
                    println("Input is out of range. Pick a valid direction.");
                }
                break;
            }

            Direction[] dirlut = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

            try {
                swapPieces(gameBoard, row-1, col-1, dirlut[direction-1]);
            } catch (RuntimeException e) {
                println("You are swapping to outside of the board.");
                Sleep(1500);
                continue;
            }

            ClearConsole();
            println(getBoardString(gameBoard, false));
            Sleep(800);

            Solve[] solves = findSolves(gameBoard);

            if(solves.length == 0){
                swapPieces(gameBoard, row-1, col-1, dirlut[direction-1]);
                println("Swap does not create a match!");
                Sleep(800);
                continue;
            }

            score += removeSolved(gameBoard, solves);

            ClearConsole();
            println(getBoardString(gameBoard, false));
            Sleep(800);

            //animate gravity lol
            String[][][] animate = gravityDown(gameBoard);
            for(int i = 0; i < animate.length; i++){
                String[][] frame = new String[animate[0].length][animate[0][0].length];
                for(int j = 0; j < animate[i].length; j++){
                    for(int k = 0; k < animate[i][j].length; k++){
                        frame[j][k] = animate[i][j][k];
                    }
                }
                Sleep(800);
                ClearConsole();
                println(getBoardString(frame, false));
            }
            

            println("Repopulating empty spots...\n");
            Sleep(800);

            repopulateEmpty(gameBoard, pieces);
            ClearConsole();
            println(getBoardString(gameBoard, false));
            Sleep(800);

            //check for combo
            int combonum = 1;
            while(true) {
                solves = findSolves(gameBoard);
                if(solves.length == 0) break;

                println("Combo! x" + combonum + "\n");
                combonum++;

                score += removeSolved(gameBoard, solves);

                //animate gravity lol
                animate = gravityDown(gameBoard);
                for(int i = 0; i < animate.length; i++){
                    String[][] frame = new String[animate[0].length][animate[0][0].length];
                    for(int j = 0; j < animate[i].length; j++){
                        for(int k = 0; k < animate[i][j].length; k++){
                            frame[j][k] = animate[i][j][k];
                        }
                    }
                    Sleep(800);
                    ClearConsole();
                    println(getBoardString(frame, false));
                }
                Sleep(800);

                println("Repopulating empty spots...");

                repopulateEmpty(gameBoard, pieces);

                ClearConsole();
                println(getBoardString(gameBoard, false));
                Sleep(800);
            }
            //combo end

        }
    }

    public void Demo() {
        String[][] gameBoard = new String[6][6]; 
        fillRandom(new String[]{
            ANSIColors.ANSI_RED + "@" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_BLUE + "#" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_YELLOW + "$" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_GREEN + "%" + ANSIColors.ANSI_RESET,
            ANSIColors.ANSI_PURPLE + "&" + ANSIColors.ANSI_RESET
        }, gameBoard);

        println(getBoardString(gameBoard, false));
        Solve[] solves = findSolves(gameBoard);
        for (int i = 0; i < solves.length; i++) {
            println(solves[i]);
        }
        int points = removeSolved(gameBoard, solves);

        println(getBoardString(gameBoard, false));
        println("Points: " + points);
        String[][][] animate = gravityDown(gameBoard);
        println("After gravity: ");
        println(getBoardString(gameBoard, false));
        
        for(int i = 0; i < animate.length; i++){
            String[][] frame = new String[animate[0].length][animate[0][0].length];
            for(int j = 0; j < animate[i].length; j++){
                for(int k = 0; k < animate[i][j].length; k++){
                    frame[j][k] = animate[i][j][k];
                }
            }
            println("Frame " + i + ":");
            println(getBoardString(frame, false));
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
                        temp.endIdx = j;
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
                        temp.endIdx = j-1;
                        solves.add(new Solve(temp));
                    }
                    temp.beginIdx = j;
                    matchCount = 1;
                    matchString = board[j][i];
                }
                if (j == board.length-1) {
                    if (matchCount >= 3) {// solve found, identical as above
                        temp.endIdx = j;
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
    private String getBoardString(String[][] board, boolean numbers) {
        if(numbers) {
            String s = "";
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    s += board[i][j] + " ";
                }
                s += "  " + (i+1) + "\n";
            }
            s += "\n";
            for(int i = 0; i < board.length; i++) {
                s += (i+1) + " ";
            }
            return s + "\n";
        }
        else
        {
            String s = "";
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    s += board[i][j] + " ";
                }
                s += "\n";
            }
            return s; 
        }
        
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

    //sleep method with proper error check
    private void Sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            println("Thread interrupted! Leaving sleep state...");
        }
    }

    //clear
    private void ClearConsole() {
        print("\033[H\033[2J");
        this.pStream.flush();
    }
}
