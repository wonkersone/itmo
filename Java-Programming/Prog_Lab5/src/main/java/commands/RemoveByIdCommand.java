package commands;

import managers.CollectionManager;

/**
 * Команда 'remove_by_id'
 * Удаляет элемент из коллекции по его id
 */
public class RemoveByIdCommand extends Command {

    /**
     * Создает команду remove_by_id
     */
    public RemoveByIdCommand() {
        super("remove_by_id", "удалить элемент из коллекции по его id");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (id элемента для удаления)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        try {
            if (args.length != 1) throw new IllegalArgumentException();
            int id = Integer.parseInt(args[0]);
            collectionManager.removeElement(id);
            System.out.println("Элемент успешно удален");
        } catch (IllegalArgumentException e) {
            System.out.println("Использование: remove_by_id id");
        }
    }
}