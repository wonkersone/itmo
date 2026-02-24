package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'remove_lower'
 * Удаляет из коллекции все элементы, меньшие, чем заданный
 */
public class RemoveLowerCommand extends Command {

    /**
     * Создает команду remove_lower
     */
    public RemoveLowerCommand() {
        super("remove_lower", "удалить из коллекции все элементы, меньшие, чем заданный",
                CommandType.WITH_WORKER_DATA, false);
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager) {
        return "";
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументы!";
        } else {
            collectionManager.getWorkersCollection().removeIf(w -> w.getSalary() < worker.getSalary());
            return "Элементы удалены";
        }
    }
}