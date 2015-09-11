import java.net.*;
import java.io.*;

public class tttServer 
{
    private ServerSocket listener;
    private Socket connection;
    private int port;
    private boolean hasConnection;

    public tttServer(int p) throws Exception 
    {
        this.port = p;
        this.hasConnection = false;

        System.out.println("Listening on port " + this.port + "...");
        listener = new ServerSocket(port);
        while (!this.hasConnection) 
        {
            connection = listener.accept();
            this.hasConnection = true;
        }
    }

    public boolean hasConnection() 
    {
        return hasConnection;
    }

    public Socket connection() 
    {
        return connection;
    }
}
