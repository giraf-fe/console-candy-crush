package CandyCrush;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

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
        String[][] gameBoard = new String[7][7];
        fillRandom("&$#%@", gameBoard);

        println(getBoardString(gameBoard));
        Solve[] solves = findSolves(gameBoard);
        for (int i = 0; i < solves.length; i++) {
            println(solves[i]);
        }
    }

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
            }
        }
        return solves.toArray(new Solve[solves.size()]);
    }

    private void fillRandom(String availableCharacters, String[][] out) {
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[i].length; j++) {
                out[i][j] = "" + availableCharacters.charAt((int) (Math.random() * availableCharacters.length()));
            }
        }
    }

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

    private <T> void print(T a) {
        this.pStream.print(a);
    }

    private <T> void println(T a) {
        this.pStream.println(a);
    }
}
