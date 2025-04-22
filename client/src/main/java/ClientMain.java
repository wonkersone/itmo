import network.TCPClient;
import userManagers.UserInputScanner;

import java.io.IOException;

public class ClientMain {
    public static void main( String[] args ) throws Exception {

        TCPClient client = new TCPClient();
        try {
            client.connect("localhost", 5555);

            UserInputScanner scanner = new UserInputScanner(client);
            scanner.startInteractiveMode();

        } catch (IOException e) {
            System.err.println("Ошибка2222: " + e.getMessage());
            System.exit(1);
        } catch (Exception e1) {
            System.out.println("Какая то ошибка" + e1.getMessage());
        }
    }
}
