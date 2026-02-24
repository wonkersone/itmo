package managers;

import java.util.Scanner;

/**
 * Класс для обработки пользовательского ввода
 * Обеспечивает интерактивный режим работы с командами
 */
public class UserInputScanner {
    private final CommandManager commandManager;
    private final CollectionManager collectionManager;
    private final Scanner scanner;

    /**
     * Создает новый сканер пользовательского ввода
     * @param commandManager менеджер команд для их выполнения
     * @param collectionManager менеджер коллекции для работы с данными
     */
    public UserInputScanner(CommandManager commandManager, CollectionManager collectionManager) {
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Запускает интерактивный режим ввода команд
     * Читает команды из консоли и выполняет их до получения команды exit
     */
    public void startInteractiveMode() {
        System.out.println("Программа запущена в интерактивном режиме. Введите команду (help - список команд):");

        while (true) {
            try {
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                // Разделение ввода на команду и аргументы
                String[] parts = input.split(" ", 2);
                String commandName = parts[0];
                String[] commandArgs = parts.length > 1 ? parts[1].split(" ") : new String[0];

                // Проверка на команду выхода
                if (commandName.equalsIgnoreCase("exit")) {
                    System.out.println("Завершение программы.");
                    break;
                }

                // Выполнение команды
                commandManager.executeCommand(commandName, commandArgs, collectionManager);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

}