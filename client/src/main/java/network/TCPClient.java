package network;

import shit.Request;
import shit.Response;

import java.io.*;
import java.net.Socket;

public class TCPClient {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connect(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Подключено к серверу " + ip + ":" + port);
    }


    public Response sendRequest(Request request) throws IOException {
        try {
            synchronized (out) {  // Синхронизация для потокобезопасности
                out.writeObject(request);
                out.flush();
                return (Response) in.readObject();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Ошибка десериализации", e);
        } catch (IOException e) {
            // При разрыве соединения пробуем переподключиться
            reconnect();
            throw e;
        }
    }

    private void reconnect() throws IOException {
        disconnect();
        connect("localhost", 8000); // Или параметры из конфига
    }

    public void disconnect() throws IOException {
        try {
            out.close();
            in.close();
        } finally {
            clientSocket.close();
        }
    }
}