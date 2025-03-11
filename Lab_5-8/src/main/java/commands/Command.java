package commands;

import interfaces.Executable;
import managers.CollectionManager;

import java.util.Objects;

/**
 * Абстрактный базовый класс для всех команд
 * Реализует интерфейс Executable и определяет основные характеристики команды
 */
public abstract class Command implements Executable {
    private final String name;
    private final String description;

    /**
     * Конструктор команды
     * @param name название команды
     * @param description описание команды
     */
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Получает название команды
     * @return название команды
     */
    public String getName(){
        return name;
    }

    /**
     * Получает описание команды
     * @return описание команды
     */
    public String getDescription(){
        return description;
    }

    /**
     * Сравнивает текущий объект с другим объектом
     * @param o объект для сравнения
     * @return true, если объекты равны, false в противном случае
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name) && Objects.equals(description, command.description);
    }

    /**
     * Вычисляет хеш-код объекта
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    /**
     * Преобразует объект в строковое представление
     * @return строковое представление команды
     */
    @Override
    public String toString() {
        return "Command {" +
                "\nname = " + name + "\n" +
                "description = " + description +
                "\n}";
    }

    /**
     * Выполняет команду с заданными аргументами
     * @param args аргументы команды
     * @param collectionManager менеджер коллекции, над которой выполняется команда
     */
    public abstract void execute(String[] args, CollectionManager collectionManager);
}

