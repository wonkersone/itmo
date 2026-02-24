package commands;

import mainClasses.Worker;
import managers.CollectionManager;
import managers.WorkerInputHelper;

/**
 * Команда 'add'
 * Добавляет новый элемент в коллекцию
 */
public class AddCommand extends Command {

    /**
     * Создает команду add
     */
    public AddCommand() {
        super("add", "добавить новый элемент в коллекцию");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        Worker worker = WorkerInputHelper.inputWorker();
        collectionManager.addElement(worker);
        System.out.println("Работник добавлен в коллекцию");
    }
}