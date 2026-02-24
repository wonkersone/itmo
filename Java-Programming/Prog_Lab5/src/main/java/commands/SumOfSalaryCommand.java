package commands;

import managers.CollectionManager;

/**
 * Команда 'sum_of_salary'
 * Выводит сумму значений поля salary для всех элементов коллекции
 */
public class SumOfSalaryCommand extends Command {

    /**
     * Создает команду sum_of_salary
     */
    public SumOfSalaryCommand() {
        super("sum_of_salary", "вывести сумму значений поля salary для всех элементов коллекции");
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        long sum = collectionManager.getWorkersCollection().stream()
                .mapToLong(worker -> worker.getSalary())
                .sum();
        System.out.println("Сумма зарплат: " + sum);
    }
}