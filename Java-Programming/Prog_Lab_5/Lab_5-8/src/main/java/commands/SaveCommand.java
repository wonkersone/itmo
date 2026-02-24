package commands;

import managers.CollectionManager;

/**
 * Команда 'save'
 * Сохраняет коллекцию в файл
 */
public class SaveCommand extends Command {

    /**
     * Создает команду save
     */
    public SaveCommand() {
        super("save", "сохранить коллекцию в файл");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        try {
            collectionManager.saveCollectionToFile();
            System.out.println("Коллекция успешно сохранена.");
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении коллекции: " + e.getMessage());
        }
    }
}