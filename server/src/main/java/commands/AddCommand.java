package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'add'
 * Добавляет новый элемент в коллекцию
 */
public class AddCommand extends Command {

    /**
     * Создает команду add
     */
    public AddCommand() {
        super("add", "добавить новый элемент в коллекцию",
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
        collectionManager.addElement(worker);
        return "Работник добавлен в коллекцию";
    }


}