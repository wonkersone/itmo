package commands;

import mainClasses.Worker;
import managers.CollectionManager;

import java.util.Comparator;

/**
 * Команда 'min_by_creation_date'
 * Выводит любой объект из коллекции, значение поля creationDate которого является минимальным
 */
public class MinByCreationDateCommand extends Command {

    /**
     * Создает команду min_by_creation_date
     */
    public MinByCreationDateCommand() {
        super("min_by_creation_date", "вывести объект из коллекции, значение поля creationDate которого " +
                "является минимальным", CommandType.WITHOUT_WORKER_DATA ,false);
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager) {
        if (args.length > 0 ) {
            return "Данная команда не принимает аргументы!";
        } else {
            String res = collectionManager.getWorkersCollection().stream()
                            .min(Comparator.comparing(Worker::getCreationDate)).toString();
            return res;
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        return "";
    }
}