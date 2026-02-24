package commands;

import managers.CollectionManager;
import managers.CommandManager;

/**
 * Команда 'help'
 * Выводит справку по всем доступным командам
 */
public class HelpCommand extends Command{
    private final CommandManager commandManager;

    /**
     * Создает команду help
     * @param commandManager менеджер команд, содержащий информацию о всех доступных командах
     */
    public HelpCommand(CommandManager commandManager) {
        super("help", "вывести справку по доступным командам");
        this.commandManager = commandManager;
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции (не используется)
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        System.out.println("Доступные команды:");
        commandManager.getCommands().forEach((name, command) -> {
            System.out.printf("%s - %s%n", name, command.getDescription());
        });
    }
}
