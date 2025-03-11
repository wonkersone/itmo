package commands;

import managers.CollectionManager;

/**
 * Команда 'exit'
 * Завершает программу
 */
public class ExitCommand extends Command {

    /**
     * Создает команду exit
     */
    public ExitCommand() {
        super("exit", "завершить программу");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции (не используется)
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        System.out.println("Завершение программы.");
        System.exit(0);
    }
}