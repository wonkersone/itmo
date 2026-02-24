package commands;

import managers.CollectionManager;

/**
 * Команда 'clear'
 * Очищает коллекцию
 */
public class ClearCommand extends Command {

    /**
     * Создает команду clear
     */
    public ClearCommand() {
        super("clear", "очистить коллекцию");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции, которую нужно очистить
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        collectionManager.clearCollection();
        System.out.println("Коллекция очищена");
    }
}