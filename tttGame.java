import java.net.*;
import java.io.*;

public class tttGame 
{
    private boolean running;
    private boolean isHost;
    private boolean myTurn;
    private int availableMoves;
    private Socket gameSocket;
    private BufferedReader response;
    private DataOutputStream client;

    private String grid[][];

    public tttGame(Socket connection, boolean isHost) throws Exception 
    {
        this.grid = new String[4][4];
        for(int i = 0; i < 4; i++) 
        {
            for(int j = 0; j < 4; j++) 
            {
                this.grid[i][j] = " ";
            }
        }

        this.running = true;
        this.isHost = isHost;
        this.myTurn = isHost;
        this.availableMoves = 3*3;

        this.gameSocket = connection;
        this.response = new BufferedReader(new InputStreamReader(this.gameSocket.getInputStream()));
        this.client = new DataOutputStream(this.gameSocket.getOutputStream());
    }

    public boolean performMove(int x, int y) throws Exception 
    {
        if(this.moveEmpty(x,y)) 
        {
            this.grid[x][y] = this.myTurn ? "X" : "O";
            this.sendMessage(x + " " + y);
            this.myTurn = !this.myTurn;
            this.printBoard();
            this.availableMoves--;

            if(this.availableMoves <= 0)
                this.draw();

            return true;
        }
        return false;
    }

    public boolean moveEmpty(int x, int y) 
    {
        if(this.checkBounds(x,y))
            return this.grid[x][y].equals(" ");

        return false;
    }

    public boolean checkBounds(int x, int y) 
    {
        return x >= 1 && x <= 3 && y >= 1 && y <= 3;
    }

    public boolean isRunning() 
    {
        return this.running;
    }

    public void checkWinner()  throws Exception 
    {
    }

    public void winner() throws Exception 
    {
        if(this.isHost)
            System.out.println("You won!");
        else
            System.out.println("You lost!");

        this.gameOver();
        this.exit();
    }

    public boolean myTurn() 
    {
        return myTurn;
    }

    public void draw() throws Exception 
    {
        System.out.println("Draw - No available moves...");
        this.gameOver();
        this.exit();
    }

    public void gameOver() 
    {
        this.running = false;
    }

    public void printBoard() 
    {
        for(int i = 1; i <= 3; i++) 
        {
            for(int j = 1; j <= 3; j++) 
            {
                System.out.print("[" + this.grid[j][i] + "]");
            }
            System.out.println();
        }
    }

    public void exit() throws Exception 
    {
        this.gameSocket.close();
        System.exit(0);
    }

    public String readMessage() throws Exception 
    {
        return this.response.readLine();
    }

    public void sendMessage(String s) throws Exception 
    {
        this.client.writeBytes(s+"\n");
    }
}
