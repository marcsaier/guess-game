package guessgame.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

public class ServerThread extends Thread {
    
    private final Socket serverSocket;
    
    private static int MAX_ATTEMPTS = 3;

    public static final int START_GAME = 0;
    public static final int SERVER_READY = 0;
    public static final int CORRECT_GUESS = 1;
    public static final int INCORRECT_GUESS = 2;
    public static final int GAME_OVER = 9;

    
    public ServerThread(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try(serverSocket) {
        InputStream fromClient = serverSocket.getInputStream();
        OutputStream toClient = serverSocket.getOutputStream();
        int clientStatus = fromClient.read();

        if (clientStatus==START_GAME) {
            System.out.println("NEW GAME STARTS...");
            
            int number = getRandomNumber(9);
            int attempt = 0;

            toClient.write(SERVER_READY);

            while (true) {
                int guess = fromClient.read();

                if (guess==number) {
                    toClient.write(CORRECT_GUESS);
                    break;
                }
                else {
                    attempt++;
                    if (attempt < MAX_ATTEMPTS)
                        toClient.write(INCORRECT_GUESS);
                    else {
                        toClient.write(GAME_OVER);
                        break;
                    }
                }
            }
        }
        else {
            System.err.println("Unexpected Client Status");
        }
        serverSocket.close();
        System.out.println("GAME TERMINATED!");
        }
        catch(IOException e) {
            System.err.println("IOException, Server can't connect to client");
        }
    }

    public int getRandomNumber(int x) {
        Random random = new Random();
        return random.nextInt(x)+1;
    }
}
