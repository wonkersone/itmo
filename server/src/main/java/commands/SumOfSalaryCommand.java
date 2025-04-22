package commands;

import mainClasses.Worker;
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
        super("sum_of_salary", "вывести сумму значений поля salary для всех элементов коллекции",
                CommandType.WITHOUT_WORKER_DATA, false);
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (не используются)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public String execute(String[] args, CollectionManager collectionManager) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументы!";
        } else {
            long sum = collectionManager.getWorkersCollection().stream()
                    .mapToLong(Worker::getSalary)
                    .sum();
            return "Сумма зарплат: " + sum;
        }

    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        return "";
    }
}