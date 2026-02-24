package commands;

import managers.CollectionManager;
import mainClasses.Worker;
import managers.WorkerInputHelper;

/**
 * Команда 'add_if_min'
 * Добавляет новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
 */
public class AddIfMinCommand extends Command {

    /**
     * Создает команду add_if_min
     */
    public AddIfMinCommand() {
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        Worker worker = WorkerInputHelper.inputWorker();
        if (collectionManager.addElementIfMin(worker)) {
            System.out.println("Работник добавлен в коллекцию");
        } else {
            System.out.println("Работник не добавлен, так как его значение не минимально");
        }
    }
}