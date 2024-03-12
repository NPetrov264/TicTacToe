package tictactoe;

import java.util.*;

public class Game {
    final Scanner sc = new Scanner(System.in);
    private final char[][] board;
    private GameState gameState;
    private int turn;
    private boolean xTurn;
    private List<Coordinates> coordinates;
    private enum GameState { RUNNING, DRAW, X_WIN, O_WIN }
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
        turn = 0;
        Arrays.fill(this.board[0], ' ');
        Arrays.fill(this.board[1], ' ');
        Arrays.fill(this.board[2], ' ');
        coordinates = new ArrayList<>(9);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                coordinates.add(new Coordinates(i, j));
            }
        }
        Collections.shuffle(coordinates);

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
                    default:
                        aiMove(Difficulty.EASY);
                        break;
                }
            }
            checkResult();
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
        Coordinates temp;
        switch (difficulty) {
            case EASY:
                System.out.println("Making move level \"easy\"");
                temp = easyMove();
                board[temp.x][temp.y] = xTurn ? 'X' : 'O';
                break;
            case MEDIUM:
                System.out.println("Making move level \"medium\"");
                temp = mediumMove();
                board[temp.x][temp.y] = xTurn ? 'X' : 'O';
                break;
            case HARD:
                break;
            default:
                System.out.println("Error: Difficulty not selected!");
                break;
        }
        xTurn = !xTurn;
    }
    private Coordinates easyMove() {
        Coordinates temp;
        do {
            temp = coordinates.remove(0);
        } while (board[temp.x][temp.y] != ' ');
        return temp;
    }

    private Coordinates mediumMove() {
        int x = 0;
        int y = 0;
        int sumO;
        int sumX;

        for (int i = 0; i < 3 ; i++) {
            sumX = 0;
            sumO = 0;
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 'O') {
                    sumO++;
                } else if (board[i][j] == 'X') {
                    sumX++;
                } else {
                    x = i;
                    y = j;
                }
            }
            if(sumX == 2 && sumO == 0 || sumX == 0 && sumO == 2) {
                return new Coordinates(x, y);
            }
        }
        for (int i = 0; i < 3 ; i++) {
            sumX = 0;
            sumO = 0;
            for (int j = 0; j < 3; j++) {
                if (board[j][i] == 'O') {
                    sumO++;
                } else if (board[j][i] == 'X') {
                    sumX++;
                } else {
                    x = j;
                    y = i;
                }
            }
            if(sumX == 2 && sumO == 0 || sumX == 0 && sumO == 2) {
                return new Coordinates(x, y);
            }
        }
        sumX = 0;
        sumO = 0;
        for (int i = 0; i < 3 ; i++) {
            if (board[i][i] == 'O') {
                sumO++;
            } else if (board[i][i] == 'X') {
                sumX++;
            } else {
                x = i;
                y = i;
            }
        }
        if(sumX == 2 && sumO == 0 || sumX == 0 && sumO == 2) {
            return new Coordinates(x, y);
        }
        sumX = 0;
        sumO = 0;
        for (int i = 0; i < 3 ; i++) {
            if (board[i][2 - i] == 'O') {
                sumO++;
            } else if (board[i][2 - i] == 'X') {
                sumX++;
            } else {
                x = i;
                y = 2 - i;
            }
        }
        if(sumX == 2 && sumO == 0 || sumX == 0 && sumO == 2) {
            return new Coordinates(x, y);
        }
        return easyMove();
    }
    private void checkResult() {
        for (int i = 0; i < 3 ; i++) {
            if(board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                gameState = board[i][0] == 'X'? GameState.X_WIN : GameState.O_WIN;
                return;
            }
            if(board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                gameState = board[0][i] == 'X'? GameState.X_WIN : GameState.O_WIN;
                return;
            }
        }
        if(board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            gameState = board[1][1] == 'X'? GameState.X_WIN : GameState.O_WIN;
            return;
        }
        if(board[2][0] != ' ' && board[2][0] == board[1][1] && board[1][1] == board[0][2]) {
            gameState = board[1][1] == 'X'? GameState.X_WIN : GameState.O_WIN;
            return;
        }
        if(++turn >= 9) {
            gameState = GameState.DRAW;
        }
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