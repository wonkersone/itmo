package commands;

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
        super("remove_first", "удалить первый элемент из коллекции");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        collectionManager.removeFirstElement();
        System.out.println("Первый элемент удален");
    }
}