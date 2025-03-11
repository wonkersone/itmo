package commands;

import mainClasses.Worker;
import managers.CollectionManager;
import managers.WorkerInputHelper;

/**
 * Команда 'update'
 * Обновляет значение элемента коллекции, id которого равен заданному
 */
public class UpdateCommand extends Command {

    /**
     * Создает команду update
     */
    public UpdateCommand() {
        super("update", "обновить значение элемента коллекции, id которого равен заданному");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (id элемента для обновления)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        try {
            if (args.length != 1) throw new IllegalArgumentException();
            int id = Integer.parseInt(args[0]);
            Worker worker = WorkerInputHelper.inputWorker();
            worker.setId(id);
            collectionManager.updateElement(id, worker);
            System.out.println("Значение элемента обновлено");
        } catch (IllegalArgumentException e) {
            System.out.println("Использование: update id");
        }
    }
}