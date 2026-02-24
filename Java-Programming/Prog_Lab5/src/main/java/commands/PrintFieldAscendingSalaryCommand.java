package commands;

import managers.CollectionManager;

/**
 * Команда 'print_field_ascending_salary'
 * Выводит значения поля salary всех элементов в порядке возрастания
 */
public class PrintFieldAscendingSalaryCommand extends Command {

    /**
     * Создает команду print_field_ascending_salary
     */
    public PrintFieldAscendingSalaryCommand() {
        super("print_field_ascending_salary", "вывести значения поля salary всех элементов в порядке возрастания");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        collectionManager.getWorkersCollection().stream()
                .mapToLong(worker -> worker.getSalary())
                .sorted()
                .forEach(salary -> System.out.println(salary));
    }
}