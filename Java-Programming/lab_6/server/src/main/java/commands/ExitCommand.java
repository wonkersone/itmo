package commands;

import mainClasses.Worker;
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
        super("exit", "завершить программу", CommandType.WITHOUT_WORKER_DATA, false);
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции (не используется)
     */
    @Override
    public String  execute(String[] args, CollectionManager collectionManager) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументов!";
        } else {
            System.exit(0);
        }
        return "";
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        return "";
    }
}