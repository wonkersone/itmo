package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'add_if_min'
 * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
 */
public class AddIfMinCommand extends Command {

    /**
     * Создает команду add_if_min
     */
    public AddIfMinCommand() {
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, " +
                "чем у наименьшего элемента этой коллекции", CommandType.WITH_WORKER_DATA, false);
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
        if (collectionManager.addElementIfMin(worker)) {
            return "Работник добавлен в коллекцию";
        } else {
            return "Работник не добавлен, так как его значение не минимально";
        }
    }
}