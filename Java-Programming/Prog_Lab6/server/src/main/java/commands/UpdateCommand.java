package commands;

import mainClasses.Worker;
import managers.CollectionManager;

/**
 * Команда 'update'
 * Обновляет значение элемента коллекции, id которого равен заданному
 */
public class UpdateCommand extends Command {

    /**
     * Создает команду update
     */
    public UpdateCommand() {
        super("update", "обновить значение элемента коллекции, id которого равен заданному",
                CommandType.WITH_WORKER_DATA, true);
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (id элемента для обновления)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager) {
        return "";
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        try {
            if (args.length < 1) throw new IllegalArgumentException();
            int id = Integer.parseInt(args[0]);
            worker.setId(id);
            collectionManager.updateElement(id, worker);
            return "Значение элемента обновлено";
        } catch (IllegalArgumentException e) {
            return "Использование: update [id]";
        }
    }
}