package commands;

import managers.CollectionManager;

/**
 * Команда 'min_by_creation_date'
 * Выводит любой объект из коллекции, значение поля creationDate которого является минимальным
 */
public class MinByCreationDateCommand extends Command {

    /**
     * Создает команду min_by_creation_date
     */
    public MinByCreationDateCommand() {
        super("min_by_creation_date", "вывести любой объект из коллекции, значение поля creationDate которого является минимальным");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        collectionManager.getWorkersCollection().stream()
                .min((w1, w2) -> w1.getCreationDate().compareTo(w2.getCreationDate()))
                .ifPresent(System.out::println);
    }
}