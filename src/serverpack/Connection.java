package serverpack;

/**
 * Created by IvanOP on 04.05.2017.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Connection {
    int portNumber = 4020;
    boolean isConnected = false;
    String message = "";
    private Executor executor;
    PrintWriter toClient;
    BufferedReader fromClient;
    ServerSocket serverSocket;
    Socket server;
    private Thread runConnectionThread;


    private Runnable runConnection = () -> {
        while (true) {
            executor.connect();
            try {
                while (isConnected) {
                    String line = fromClient.readLine();
                    if (!line.equals("")) {
                        System.out.println("Server received: " + line);
                    }
                    executor.executeMessageFromClient(line);
                    executor.executeMessageFromClient(message);
                    toClient.println("");
                    toClient.flush();
                    sleep(50);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    Connection() {
        executor = new Executor(this);
        runConnectionThread = new Thread(runConnection);
        runConnectionThread.start();
    }

    void setMessage(String messageFromAdmin) {
        this.message = messageFromAdmin;
    }


}
