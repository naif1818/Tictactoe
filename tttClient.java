import java.net.*;
import java.io.*;

public class tttClient {
      private Socket connection;
      private int port;

      public tttClient(String host, int p) {
            this.port = p;
            System.out.println("Connecting to " + host + " on port " + this.port);
            try {
                  connection = new Socket(host,p);
            } catch(Exception ex) {
                  System.out.println("Connection to " + host + " failed!");
                  System.exit(0);
            }
            System.out.println("Connected...");
            System.out.println("Waiting for opponent...");
      }

      public Socket connection() {
            return connection;
      }
}
