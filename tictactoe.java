import java.net.*;
import java.io.*;

public class tictactoe {
      public static void main(String[] args) throws Exception {
            BufferedReader localInput = new BufferedReader(new InputStreamReader(System.in));
            boolean isListener = false;

            System.out.print("Enter server ip (leave blank to host): ");
            String host = localInput.readLine();

            if(host.equals("")) {
                  // i am host
                  isListener = true;
            }

            int port = 1337;
            boolean canPlay = false;

            // initialize server and client to null
            Server server = null;
            Client client = null;

            if(isListener) {
                  // is host
                  server = new Server(port);
                  if(server.hasConnection()) {
                        // if a client has connected, can start game
                        canPlay = true;
                  } else {
                        // client connection failed, abort
                        System.out.println("Connection to peer failed!");
                        return;
                  }
            } else {
                  // is client, has connected to server
                  client = new Client(host, port);
                  canPlay = true;
            }

            if(canPlay) {
                  // am i using client or server socket
                  Socket gameSocket = (isListener ? server.connection() : client.connection);

                  // initialize new game with the correct socket and host status
                  tttGame game = new tttGame(gameSocket, isListener);

                  // while game is in progress, and no winner is chosen
                  while(game.isRunning()) {

                        // if my turn, ask for a valid move
                        if(game.myTurn) {
                              boolean res = false;
                              do {
                                    System.out.print("Enter your move (x y): ");
                                    String move[] = localInput.readLine().split(" ");
                                    int x = Integer.parseInt(move[0]);
                                    int y = Integer.parseInt(move[1]);
                                    res = game.performMove(x,y);
                              } while(!res);
                              // a valid move has been performed, wait for response
                              System.out.println("Waiting for opponent...");
                        }

                        // not my turn, waiting for clients move
                        else if(!game.myTurn) {
                              String move[] = game.readMessage().split(" ");
                              int x = Integer.parseInt(move[0]);
                              int y = Integer.parseInt(move[1]);
                              game.performMove(x,y);
                        }

                        // has any player won?
                        game.checkWinner();
                  }
                  game.exit();
            }
      }

      private static class Server {
            private ServerSocket listener;
            private Socket connection;
            private int port;
            private boolean hasConnection;

            public Server(int p) throws Exception {
                  this.port = p;
                  this.hasConnection = false;

                  System.out.println("Listening on port " + this.port + "...");
                  listener = new ServerSocket(port);
                  while (!this.hasConnection) {
                              connection = listener.accept();
                              this.hasConnection = true;
                  }
            }

            public boolean hasConnection() {
                  return hasConnection;
            }

            public Socket connection() {
                  return connection;
            }
      }

      private static class Client {
            private Socket connection;
            private int port;

            public Client(String host, int p) throws Exception {
                  this.port = p;
                  System.out.println("Connecting to " + host + " on port " + this.port);
                  connection = new Socket(host,p);
                  System.out.println("Waiting for opponent...");
            }
      }

      private static class tttGame {
            private boolean running;
            private boolean isHost;
            private boolean myTurn;
            private int availableMoves;
            private Socket gameSocket;
            private BufferedReader response;
            private DataOutputStream client;

            private String grid[][];

            public tttGame(Socket connection, boolean isHost) throws Exception {
                  this.grid = new String[4][4];
                  for(int i = 0; i < 4; i++) {
                        for(int j = 0; j < 4; j++) {
                              this.grid[i][j] = " ";
                        }
                  }

                  this.running = true;
                  this.isHost = isHost;
                  this.myTurn = isHost;
                  this.availableMoves = 3*3;

                  this.gameSocket = connection;
                  this.response = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
                  this.client = new DataOutputStream(gameSocket.getOutputStream());
            }

            public boolean performMove(int x, int y) throws Exception {
                  if(this.moveEmpty(x,y)) {
                        if(myTurn) {
                              this.grid[x][y] = "X";
                        } else {
                              this.grid[x][y] = "O";
                        }
                        this.sendMessage(x + " " + y);
                        this.myTurn = !this.myTurn;
                        this.printBoard();
                        this.availableMoves--;

                        if(this.availableMoves <= 0) {
                              this.draw();
                        }

                        return true;
                  }
                  return false;
            }

            public boolean moveEmpty(int x, int y) {
                  if(this.checkBounds(x,y)) {
                        return this.grid[x][y].equals(" ");
                  }
                  return false;
            }

            public boolean checkBounds(int x, int y) {
                  return x >= 1 && x <= 3 && y >= 1 && y <= 3;
            }

            public boolean isRunning() {
                  return this.running;
            }

            public void checkWinner()  throws Exception {
                  // dfs?
            }

            public void winner() throws Exception {
                  if(this.isHost) {
                        System.out.println("You won!");
                  } else {
                        System.out.println("You lost!");
                  }
                  this.gameOver();
                  this.exit();
            }

            public void draw() throws Exception {
                  System.out.println("Draw - No available moves...");
                  this.gameOver();
                  this.exit();
            }

            public void gameOver() {
                  this.running = false;
            }

            public void printBoard() {
                        for(int i = 1; i <= 3; i++) {
                              for(int j = 1; j <= 3; j++) {
                                    System.out.print("[" + this.grid[j][i] + "]");
                              }
                              System.out.println();
                        }
            }

            public void exit() throws Exception {
                  this.gameSocket.close();
                  System.exit(0);
            }

            public String readMessage() throws Exception {
                  return this.response.readLine();
            }

            public void sendMessage(String s) throws Exception {
                  this.client.writeBytes(s+"\n");
            }

      }
}
