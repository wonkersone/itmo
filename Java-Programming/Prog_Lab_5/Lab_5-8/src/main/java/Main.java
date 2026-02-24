import managers.CollectionManager;
import managers.CommandManager;
import commands.*;
import managers.UserInputScanner;


public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: не указан путь к файлу с данными.");
            return;
        }

        String fileName = args[0];

        // Инициализация менеджера коллекции
        CollectionManager collectionManager = new CollectionManager();

        // Загрузка коллекции из файла
        collectionManager.loadCollectionFromFile(fileName);

        // Инициализация менеджера команд
        CommandManager commandManager = new CommandManager();

        UserInputScanner userInputScanner = new UserInputScanner(commandManager, collectionManager);

        // Регистрация команд
        commandManager.registerCommand(new HelpCommand(commandManager));
        commandManager.registerCommand(new InfoCommand());
        commandManager.registerCommand(new ShowCommand());
        commandManager.registerCommand(new AddCommand());
        commandManager.registerCommand(new UpdateCommand());
        commandManager.registerCommand(new RemoveByIdCommand());
        commandManager.registerCommand(new ClearCommand());
        commandManager.registerCommand(new SaveCommand());
        commandManager.registerCommand(new ExecuteScriptCommand(commandManager));
        commandManager.registerCommand(new ExitCommand());
        commandManager.registerCommand(new RemoveFirstCommand());
        commandManager.registerCommand(new AddIfMinCommand());
        commandManager.registerCommand(new RemoveLowerCommand());
        commandManager.registerCommand(new SumOfSalaryCommand());
        commandManager.registerCommand(new MinByCreationDateCommand());
        commandManager.registerCommand(new PrintFieldAscendingSalaryCommand());

        userInputScanner.startInteractiveMode();
    }
}