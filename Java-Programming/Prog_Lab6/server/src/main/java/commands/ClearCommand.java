package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'clear'
 * Очищает коллекцию
 */
public class ClearCommand extends Command {

    /**
     * Создает команду clear
     */
    public ClearCommand() {
        super("clear", "очистить коллекцию",
                CommandType.WITHOUT_WORKER_DATA, false);
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции, которую нужно очистить
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументов!";
        } else {
            collectionManager.clearCollection();
            return "Коллекция очищена";
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        return "";
    }
}