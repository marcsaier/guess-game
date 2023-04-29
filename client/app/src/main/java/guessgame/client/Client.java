/*
 * This is a client that connects to a server via tcp
 * The client has 3 Attempts to guess a number the server picked
 * 
 */
package guessgame.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static final int START_GAME = 0;
    public static final int SERVER_READY = 0;
    public static final int CORRECT_GUESS = 1;
    public static final int INCORRECT_GUESS = 2;
    public static final int GAME_OVER = 9;


    public static void main(String[] args) {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Scanner scanner = new Scanner(System.in);

        try (Socket socket = new Socket(hostname, port))        {
            OutputStream toServer = socket.getOutputStream();
            InputStream fromServer = socket.getInputStream();

            toServer.write(START_GAME);

            int serverStatus = fromServer.read();

            if (serverStatus==SERVER_READY) {
                System.out.println("CONNECTED TO SERVER "+hostname+" VIA PORT: "+port+"\n GAME STARTED");
            }
            else {
                System.out.println("Server and Client are not connected correctly");
                throw new IllegalStateException();
            }
            GAME_LOOP:
            while (true) {
                System.out.println("Please enter your guess (number in between 1-9) :");
                toServer.write(scanner.nextInt());
                serverStatus = fromServer.read();
                switch (serverStatus) {
                    case CORRECT_GUESS:
                        System.out.println("Correct, you win!");
                        break GAME_LOOP;
                    case INCORRECT_GUESS:
                        System.out.println("Wrong guess! Try again...");
                        break;
                    case GAME_OVER:
                        System.out.println("You lost, too many wrong guesses!");
                        break GAME_LOOP;
                    default:
                        throw new IOException("Invalid Server Code");
                }
            }
        }
        catch (UnknownHostException e) {
            System.out.println("Der Hostname konnte nicht gefunden werden");
        }
        catch (IOException e) {
            System.out.println("Error: IO Exception");
        }
        scanner.close();
    }
}
