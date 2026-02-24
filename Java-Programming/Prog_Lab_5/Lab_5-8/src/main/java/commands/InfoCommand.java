package commands;

import managers.CollectionManager;

/**
 * Команда 'info'
 * Выводит информацию о коллекции
 */
public class InfoCommand extends Command {

    /**
     * Создает команду info
     */
    public InfoCommand() {
        super("info", "вывести информацию о коллекции");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции, информацию о которой нужно вывести
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        System.out.println(collectionManager.getCollectionInfo());
    }
}