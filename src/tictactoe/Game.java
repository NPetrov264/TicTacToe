package tictactoe;

import java.util.*;

public class Game {
    final Scanner sc = new Scanner(System.in);
    private final char[][] board;
    private GameState gameState;
    private int turn;
    private boolean xTurn;
    private enum GameState {
        O_WIN(-1),
        DRAW(0),
        X_WIN(1),
        RUNNING(2);

        private final int value;
        GameState(final int newValue) {
            value = newValue;
        }
        public int getValue() { return value; }
    }
    private enum Difficulty { EASY, MEDIUM, HARD }

    public Game() {
        this.board = new char[3][3];
    }
    public void menu() {
        String command;

        while (true) {
            System.out.print("Input command: ");
            command = sc.nextLine();
            if ("exit".equals(command)){
                break;
            }
            String[] parts = command.split(" ");
            if (parts.length == 3 && "start".equals(parts[0])) {
                playGame(parts[1], parts[2]);
            } else {
                System.out.println("Bad parameters!");
            }
        }
    }
    public void playGame(String player1, String player2) {

        gameState = GameState.RUNNING;
        xTurn = true;
        turn = 1;
        Arrays.fill(this.board[0], ' ');
        Arrays.fill(this.board[1], ' ');
        Arrays.fill(this.board[2], ' ');

        while (gameState == GameState.RUNNING) {
            printBoard();
            if (xTurn && "user".equals(player1)) {
                makeMove();
            } else if (xTurn) {
                switch (player1) {
                    case "easy":
                        aiMove(Difficulty.EASY);
                        break;
                    case "medium":
                        aiMove(Difficulty.MEDIUM);
                        break;
                    case "hard":
                        aiMove(Difficulty.HARD);
                        break;
                    default:
                        aiMove(Difficulty.EASY);
                        break;
                }
            } else if ("user".equals(player2)) {
                makeMove();
            } else {
                switch (player2) {
                    case "easy":
                        aiMove(Difficulty.EASY);
                        break;
                    case "medium":
                        aiMove(Difficulty.MEDIUM);
                        break;
                    case "hard":
                        aiMove(Difficulty.HARD);
                        break;
                    default:
                        aiMove(Difficulty.EASY);
                        break;
                }
            }
            gameState = checkResult(board, turn);
            turn++;
        }
        printBoard();
        displayResults();
    }
    private void printBoard() {
        for (int i = 0; i < 3; i++) {
            if ( i == 0) {
                System.out.println("---------");
            }
            System.out.print("|");
            for (int j = 0; j < 3; j++) {
                System.out.print(" " + board[i][j]);
            }
            System.out.println(" |");
            if (i == 2) {
                System.out.println("---------");
            }
        }
    }
    private void makeMove() {
        int x = -1, y = -1;
        do {
            try {
                System.out.print("Enter the coordinates: ");
                x = sc.nextInt() - 1;
                y = sc.nextInt() - 1;
                sc.nextLine();
                if (board[x][y] != ' ') {
                    System.out.println("This cell is occupied! Choose another one! ");
                }

            } catch (InputMismatchException e) {
                System.out.println("You should enter numbers!");
                sc.nextLine();
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Coordinates should be from 1 to 3!");
            }
        } while (x < 0 || x > 2 || y < 0 || y > 2 || board[x][y] != ' ');
        board[x][y] = xTurn ? 'X' : 'O';
        xTurn = !xTurn;
    }
    private void aiMove(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                System.out.println("Making move level \"easy\"");
                easyMove();
                break;
            case MEDIUM:
                System.out.println("Making move level \"medium\"");
                mediumMove();
                break;
            case HARD:
                System.out.println("Making move level \"hard\"");
                hardMove();
                break;
            default:
                System.out.println("Error: Difficulty not selected!");
                break;
        }
        xTurn = !xTurn;
    }
    //make a random move
    private void easyMove() {
        Random rand = new Random();
        int x;
        int y;
        do {
            x = rand.nextInt(3);
            y = rand.nextInt(3);
        } while (board[x][y] != ' ');
        board[x][y] = xTurn ? 'X' : 'O';
    }

    //Find if there is next move that can win the game or stop opponent from winning
    //and if there is such move do it, and if not do a random move
    private void mediumMove() {
        int x = -1;
        int y = -1;

        if (xTurn) {
            for (int i = 0; i < 3 ; i++) {
                for (int j = 0; j < 3 ; j++) {
                    if(board[i][j] == ' ') {
                        board[i][j] = 'X';
                        if (checkResult(board, turn) == GameState.X_WIN) {
                            return;
                        }
                        board[i][j] = 'O';
                        if (checkResult(board, turn) == GameState.O_WIN) {
                            x = i;
                            y = j;
                        }
                        board[i][j] = ' ';
                    }
                }
            }
            if(x != -1) {
                board[x][y] = 'X';
                return;
            }
        } else {
            for (int i = 0; i < 3 ; i++) {
                for (int j = 0; j < 3 ; j++) {
                    if(board[i][j] == ' ') {
                        board[i][j] = 'O';
                        if (checkResult(board, turn) == GameState.O_WIN) {
                            return;
                        }
                        board[i][j] = 'X';
                        if (checkResult(board, turn) == GameState.X_WIN) {
                            x = i;
                            y = j;
                        }
                        board[i][j] = ' ';
                    }
                }
            }
            if(x != -1) {
                board[x][y] = 'O';
                return;
            }
        }
        easyMove();
    }

    //play the optimal move using the minimax algorithm
    private void hardMove() {
        int x = 0, y = 0;
        if (xTurn) {
            int bestMove = -100;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if(board[i][j] == ' ') {
                        board[i][j] = 'X';
                        int move = minimax(board, turn + 1, false);
                        board[i][j] = ' ';
                        if (move > bestMove) {
                            bestMove = move;
                            x = i;
                            y = j;
                        }
                    }
                }
            }
        } else {
            int bestMove = 100;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'O';
                        int move = minimax(board, turn + 1, true);
                        board[i][j] = ' ';
                        if (move < bestMove) {
                            bestMove = move;
                            x = i;
                            y = j;
                        }
                    }
                }
            }
        }
        board[x][y] = xTurn ? 'X' : 'O';
    }
    //minimax algorithm
    private int minimax(char[][] board, int turn, boolean xTurn) {
        GameState state = checkResult(board, turn);
        if(state != GameState.RUNNING) {
            return state.getValue() * (11 - turn);
        }
        if (xTurn) {
            int bestScore = -100;
            for (int i = 0; i < 3 ; i++) {
                for (int j = 0; j < 3 ; j++) {
                    if(board[i][j] == ' ') {
                        board[i][j] = 'X';
                        int score = minimax(board, turn + 1, false);
                        bestScore = Math.max(bestScore, score);
                        board[i][j] = ' ';
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = 100;
            for (int i = 0; i < 3 ; i++) {
                for (int j = 0; j < 3 ; j++) {
                    if(board[i][j] == ' ') {
                        board[i][j] = 'O';
                        int score = minimax(board, turn + 1, true);
                        bestScore = Math.min(bestScore, score);
                        board[i][j] = ' ';
                    }
                }
            }
            return bestScore;
        }
    }

    private GameState checkResult(char[][] board, int turn) {
        if(turn >= 9) {
            return GameState.DRAW;
        }
        for (int i = 0; i < 3 ; i++) {
            if(board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0] == 'X'? GameState.X_WIN : GameState.O_WIN;
            }
            if(board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return board[0][i] == 'X'? GameState.X_WIN : GameState.O_WIN;
            }
        }
        if(board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[1][1] == 'X'? GameState.X_WIN : GameState.O_WIN;
        }
        if(board[2][0] != ' ' && board[2][0] == board[1][1] && board[1][1] == board[0][2]) {
            return board[1][1] == 'X'? GameState.X_WIN : GameState.O_WIN;
        }
        return GameState.RUNNING;
    }
    private void displayResults(){
        switch (gameState) {
            case X_WIN:
                System.out.println("X wins");
                break;
            case O_WIN:
                System.out.println("O wins");
                break;
            case DRAW:
                System.out.println("Draw");
                break;
            default:
                System.out.println("Game not finished");
                break;
        }
    }

}

class Coordinates {
    int x;
    int y;
    Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

}