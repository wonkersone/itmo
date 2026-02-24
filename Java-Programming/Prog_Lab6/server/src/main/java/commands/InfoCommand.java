package commands;

import mainClasses.Worker;
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
        super("info", "вывести информацию о коллекции",
                CommandType.WITHOUT_WORKER_DATA, false);
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции, информацию о которой нужно вывести
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументы!";
        } else {
            return collectionManager.getCollectionInfo();
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        return "";
    }
}