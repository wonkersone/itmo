package managers;

import commands.Command;
import mainClasses.Worker;
import shit.Response;

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
    public Response executeCommand(String commandName, String[] args, CollectionManager collectionManager) {
        Command command = commands.get(commandName);
        if (command != null) {
            String message = command.execute(args, collectionManager);
            return new Response(Response.ResponseType.INFO, true, message);
        } else {
            return new Response(Response.ResponseType.INFO, false, "Неизвестная команда");
        }
    }

    public Response executeCommand(String commandName, String[] args, CollectionManager collectionManager, Worker worker) {
        Command command = commands.get(commandName);
        if (command != null) {
            String message = command.execute(args, collectionManager, worker);
            return new Response(true, message);
        } else {
            return new Response(false, "Неизвестная команда");
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


