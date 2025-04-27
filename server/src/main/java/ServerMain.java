import commands.*;
import managers.CollectionManager;
import managers.CommandManager;
import network.TCPServer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        String filePath = args[0];

        CollectionManager collectionManager = new CollectionManager();
        CommandManager commandManager = new CommandManager();
        TCPServer server = new TCPServer(collectionManager, commandManager);
        collectionManager.loadCollectionFromFile(filePath);

        commandManager.registerCommand(new HelpCommand(commandManager));
        commandManager.registerCommand(new InfoCommand());
        commandManager.registerCommand(new ShowCommand());
        commandManager.registerCommand(new AddCommand());
        commandManager.registerCommand(new UpdateCommand());
        commandManager.registerCommand(new RemoveByIdCommand());
        commandManager.registerCommand(new ClearCommand());
        commandManager.registerCommand(new ExecuteScriptCommand(commandManager));
        commandManager.registerCommand(new RemoveFirstCommand());
        commandManager.registerCommand(new AddIfMinCommand());
        commandManager.registerCommand(new RemoveLowerCommand());
        commandManager.registerCommand(new SumOfSalaryCommand());
        commandManager.registerCommand(new MinByCreationDateCommand());
        commandManager.registerCommand(new PrintFieldAscendingSalaryCommand());
        commandManager.registerCommand(new ExitCommand());
        commandManager.registerCommand(new SaveCommand());


        try {
            server.start(5555);
        } catch (IOException e) {
            System.err.println("Не получилось запустить сервер: " + e.getMessage());
            System.exit(1);
        }

    }
}
