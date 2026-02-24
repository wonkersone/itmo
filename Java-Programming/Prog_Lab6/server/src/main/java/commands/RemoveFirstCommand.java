package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'remove_first'
 * Удаляет первый элемент из коллекции
 */
public class RemoveFirstCommand extends Command {

    /**
     * Создает команду remove_first
     */
    public RemoveFirstCommand() {
        super("remove_first", "удалить первый элемент из коллекции",
                CommandType.WITHOUT_WORKER_DATA, false);
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументов!";
        } else {
            collectionManager.removeFirstElement();
            return "Первый элемент удален";
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        return "";
    }
}