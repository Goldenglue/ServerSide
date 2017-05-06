package serverpack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IvanOP on 04.05.2017.
 */
public class Executor {
    private Map<String, Call> stringMethodMap;
    private String[] possibleCommands;
    private Connection connection;
    private String message;
    String string = "";
    Vector<Object> someVector = new Vector<>();

    interface Call {
        void execute();
    }

    private Call[] calls = new Call[]{
            this::disconnect,
            this::receiveSerializedObject,
            this::sendSerializedObject,
            this::clearVectorOnServer,
            this::clearVectorOnClient,
            this::sizeOnServer,
            this::sizeOnClient,
            this::requestObject,
            this::sendObject,
            this::getObject
    };

    Executor(Connection conn) {
        this.connection = conn;
        try {
            setStringMethodMap();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void setStringMethodMap() throws NoSuchMethodException {
        possibleCommands = new String[]{"-dc", "-sobjc", "-sobjs", "-clrc", "-clrs", "-vecsc", "-vecss", "-gobjs"
                , "-gobjc", "-robj"};
        stringMethodMap = new HashMap<>();
        for (int i = 0; i < possibleCommands.length; i++) {
            stringMethodMap.put(possibleCommands[i], calls[i]);
        }
    }

    void executeMessageFromClient(String message) {
        this.message = message;
        message = message.replaceAll("\\d", "");
        if (stringMethodMap.containsKey(message)) {
            for (Map.Entry<String, Call> temp : stringMethodMap.entrySet()) {
                if (temp.getKey().equals(message)) {
                    temp.getValue().execute();
                }
            }
        }
    }

    void connect() {
        try {
            connection.isConnected = true;
            connection.serverSocket = new ServerSocket(connection.portNumber);
            System.out.println("Waiting for client on port " + connection.serverSocket.getLocalPort() + "...");
            connection.server = connection.serverSocket.accept();
            connection.toClient =
                    new PrintWriter(connection.server.getOutputStream(), true);
            connection.fromClient =
                    new BufferedReader(
                            new InputStreamReader(connection.server.getInputStream()));
            System.out.println("Connected to " + connection.server.getLocalSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {

        connection.isConnected = false;
    }

    private void sendSerializedObject() {
        connection.toClient.println("-sobjs");
        connection.toClient.flush();
        Gson gson = new Gson();
        String temp = gson.toJson(someVector);
        connection.toClient.println(temp);
        connection.toClient.flush();
    }

    private void receiveSerializedObject() {
        Gson gson = new Gson();
        try {
            string = connection.fromClient.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(string).getAsJsonArray();
        Type heh = new TypeToken<Object>() {
        }.getType();
        for (int i = 0; i < jsonArray.size(); i++) {
            System.out.println(jsonArray);
            someVector.add(gson.fromJson(string, heh));
        }
    }

    private void clearVectorOnServer() {
        someVector.removeAllElements();
        System.out.println("Vector cleared");
    }

    private void clearVectorOnClient() {
        connection.toClient.println(message);
        connection.toClient.flush();
    }

    private void sizeOnServer() {
        connection.toClient.println(someVector.size());
        connection.toClient.flush();
    }

    private void sizeOnClient() {
        connection.toClient.println(message);
        connection.toClient.flush();
        try {
            System.out.println("size on client: " + connection.fromClient.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestObject() {
        connection.toClient.println(message);
        connection.toClient.flush();
    }

    private void sendObject() {
        Gson gson = new Gson();
        message = message.replaceAll("[^0-9]", "");
        String object = gson.toJson(someVector.get(Integer.valueOf(message)));
        String type;
        if (object.contains("png")) {
            type = "Images";
        } else {
            type = "Strings";
        }
        connection.toClient.println("-robj");
        connection.toClient.flush();
        System.out.println(type);
        connection.toClient.println(type);
        connection.toClient.flush();
        System.out.println(object);
        connection.toClient.println(object);
        connection.toClient.flush();
    }

    private void getObject() {
        Gson gson = new Gson();
        try {
            String object = connection.fromClient.readLine();
            System.out.println(object);
            Type heh = new TypeToken<Object>() {
            }.getType();
            someVector.add(gson.fromJson(object, heh));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
