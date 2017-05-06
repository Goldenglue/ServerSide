package serverpack;


import static java.lang.Thread.sleep;

/**
 * Created by IvanOP on 04.05.2017.
 */
public class Server implements Runnable {
    private String temp = "";
    private ServerUI serverUI;
    private Connection connection;
    private Thread runServerThread;
    private Runnable runServer = () -> {
        while (true) {
            this.temp = serverUI.getMessageToClient();
            serverUI.clearMessage();
            connection.setMessage(temp);

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    };

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    @Override
    public void run() {
        serverUI = new ServerUI();
        connection = new Connection();
        runServerThread = new Thread(runServer);
        runServerThread.start();
    }
}
