package network;

import shit.Request;
import shit.Response;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

//    public Response sendRequest(Request request) throws IOException {
//        try {
//            synchronized (out) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ObjectOutputStream oos = new ObjectOutputStream(baos);
//                oos.writeObject(request);
//                byte[] data = baos.toByteArray();
//
//                DataOutputStream dos = new DataOutputStream(out);
//                dos.writeInt(data.length);
//                dos.write(data);
//                dos.flush();
//
//                DataInputStream dis = new DataInputStream(in);
//                int length = dis.readInt();
//                byte[] responseData = new byte[length];
//                dis.readFully(responseData);
//
//                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(responseData));
//                return (Response) ois.readObject();
//            }
//        } catch (ClassNotFoundException e) {
//            throw new IOException("Ошибка десериализации", e);
//        } catch (IOException e) {
//            reconnect();
//            throw e;
//        }
//    }


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