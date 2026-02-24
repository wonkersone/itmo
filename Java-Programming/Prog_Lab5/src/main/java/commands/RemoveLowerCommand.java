package commands;

import mainClasses.Worker;
import managers.CollectionManager;
import managers.WorkerInputHelper;

/**
 * Команда 'remove_lower'
 * Удаляет из коллекции все элементы, меньшие, чем заданный
 */
public class RemoveLowerCommand extends Command {

    /**
     * Создает команду remove_lower
     */
    public RemoveLowerCommand() {
        super("remove_lower", "удалить из коллекции все элементы, меньшие, чем заданный");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        Worker worker = WorkerInputHelper.inputWorker();
        collectionManager.getWorkersCollection().removeIf(w -> w.getSalary() < worker.getSalary());
        System.out.println("Элементы удалены");
    }
}