package interfaces;

import managers.CollectionManager;

/**
 * Интерфейс для выполняемых команд
 * Определяет метод execute для выполнения команды
 */
public interface Executable {
    /**
     * Выполняет команду с заданными аргументами
     * @param args аргументы команды
     * @param collectionManager менеджер коллекции, над которой выполняется команда
     */
    void execute(String[] args, CollectionManager collectionManager);
}
