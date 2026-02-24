package managers;

import commands.Command;

import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер команд
 * Управляет регистрацией и выполнением команд
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Регистрирует новую команду
     * @param command команда для регистрации
     */
    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    /**
     * Выполняет команду по её имени
     * @param commandName имя команды
     * @param args аргументы команды
     * @param collectionManager менеджер коллекции
     */
    public void executeCommand(String commandName, String[] args, CollectionManager collectionManager) {
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(args, collectionManager);
        } else {
            System.out.println("Неизвестная команда. Введите help для справки.");
        }
    }

    /**
     * Получает все зарегистрированные команды
     * @return карта команд, где ключ - имя команды
     */
    public Map<String, Command> getCommands() {
        return commands;
    }
}


