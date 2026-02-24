package managers;

import fileManagers.CollectionParser;
import fileManagers.CollectionWriter;
import mainClasses.Worker;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Comparator;

/**
 * Менеджер коллекции работников
 * Управляет коллекцией объектов Worker, обеспечивая операции добавления, удаления и модификации элементов
 */
public class CollectionManager {
    private ArrayDeque<Worker> workersCollection;
    private final LocalDate creationDate;
    private final CollectionWriter writer = new CollectionWriter();
    private final CollectionParser parser = new CollectionParser(this);
    private String filePath;

    /**
     * Создает новый менеджер коллекции
     * Инициализирует пустую коллекцию и устанавливает дату создания
     */
    public CollectionManager() {
        workersCollection = new ArrayDeque<Worker>();
        creationDate = LocalDate.now();
    }

    /**
     * Устанавливает путь к файлу для сохранения/загрузки коллекции
     * @param filePath путь к файлу
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Загружает коллекцию из файла
     * @param filePath путь к файлу
     */
    public void loadCollectionFromFile(String filePath) {
        this.filePath = filePath;
        workersCollection = parser.parseFromFile(filePath);
    }

    /**
     * Сохраняет коллекцию в файл
     * @throws IllegalStateException если путь к файлу не установлен
     */
    public void saveCollectionToFile() {
        if (filePath == null) {
            throw new IllegalStateException("Путь к файлу не установлен");
        }
        writer.writeToFile(filePath, workersCollection);
    }

    /**
     * Возвращает дату создания коллекции
     * @return дата создания
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Возвращает коллекцию работников
     * @return коллекция работников
     */
    public ArrayDeque<Worker> getWorkersCollection() {
        return workersCollection;
    }

    /**
     * Устанавливает новую коллекцию работников
     * @param workersCollection новая коллекция
     */
    public void setWorkersCollection(ArrayDeque<Worker> workersCollection) {
        this.workersCollection = workersCollection;
    }

    /**
     * Возвращает информацию о коллекции
     * @return строка с информацией о типе, дате создания и размере коллекции
     */
    public String getCollectionInfo() {
        return ("Type - " + workersCollection.getClass().getName() +
                "\nCreation date - " + getCreationDate() +
                "\nAmount of elements - " + workersCollection.size());
    }

    /**
     * Выводит все элементы коллекции
     */
    public void showCollectionElements() {
        if (workersCollection.isEmpty()) {
            System.out.println("Коллекция пуста");
        } else {
            for (Worker worker : workersCollection) {
                System.out.println(worker.toString());
            }
        }
    }

    /**
     * Добавляет нового работника в коллекцию
     * @param worker новый работник
     */
    public void addElement(Worker worker) {
        if (worker == null) {
            throw new IllegalArgumentException("Работник не может быть null");
        }
        worker.setId(generateId());
        worker.setCreationDate(LocalDate.now());
        if (worker.getName() == null || worker.getName().isEmpty()) {
            throw new IllegalArgumentException("Имя работника не может быть пустым");
        }
        if (worker.getCoordinates() == null) {
            throw new IllegalArgumentException("Координаты работника не могут быть null");
        }
        if (worker.getStartDate() == null) {
            throw new IllegalArgumentException("Дата начала работы не может быть null");
        }
        if (worker.getPosition() == null) {
            throw new IllegalArgumentException("Должность работника не может быть null");
        }
        if (worker.getSalary() <= 0) {
            throw new IllegalArgumentException("Зарплата должна быть больше 0");
        }
        workersCollection.add(worker);
    }

    /**
     * Обновляет данные работника по его id
     * @param id идентификатор работника
     * @param new_worker новые данные работника
     */
    public void updateElement(int id, Worker new_worker) {
        workersCollection.stream().filter(worker -> worker.getId() == id).findFirst().
                ifPresent(worker -> {
                    worker.setId(new_worker.getId());
                    worker.setName(new_worker.getName());
                    worker.setCoordinates(new_worker.getCoordinates());
                    worker.setSalary(new_worker.getSalary());
                    worker.setStartDate(new_worker.getStartDate());
                    worker.setEndDate(new_worker.getEndDate());
                    worker.setPosition(new_worker.getPosition());
                    worker.setPerson(new_worker.getPerson());
                });
    }

    /**
     * Удаляет работника по его id
     * @param id идентификатор работника
     */
    public void removeElement(int id) {
        workersCollection.removeIf(worker -> worker.getId() == id);
    }

    /**
     * Очищает коллекцию
     */
    public void clearCollection() {
        workersCollection.clear();
    }

    /**
     * Удаляет первый элемент коллекции
     */
    public void removeFirstElement() {
        workersCollection.pollFirst();
    }

    /**
     * Добавляет элемент, если его зарплата меньше минимальной в коллекции
     * @param worker работник для добавления
     * @return true если элемент был добавлен, false если нет
     */
    public boolean addElementIfMin(Worker worker) {
        Worker minWorker = workersCollection.stream()
                .min(Comparator.comparing(Worker::getSalary)).orElse(null);
        if (minWorker == null || worker.getSalary() < minWorker.getSalary()) {
            workersCollection.add(worker);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Удаляет всех работников с зарплатой меньше заданной
     * @param worker работник для сравнения
     * @return true если были удалены элементы, false если нет
     */
    public boolean removeLowerElement(Worker worker) {
        return workersCollection.removeIf(w -> w.getSalary() < worker.getSalary());
    }

    /**
     * Выводит значения поля salary в порядке возрастания
     */
    public void printFieldAscendingSalary() {
        if (workersCollection.isEmpty()) {
            System.out.println("Коллекция пуста");
            return;
        }
        workersCollection.stream()
                .sorted(Comparator.comparing(Worker::getSalary))
                .forEach(w -> System.out.println(w.getSalary()));
    }

    /**
     * Возвращает работника с минимальной датой создания
     * @return строковое представление работника или сообщение о пустой коллекции
     */
    public String minByCreationDate() {
        if (workersCollection.isEmpty()) {
            return "Коллекция пуста";
        }
        Worker minWorker = workersCollection.stream()
                .min(Comparator.comparing(Worker::getCreationDate))
                .orElse(null);
        return minWorker != null ? minWorker.toString() : "Коллекция пуста";
    }

    /**
     * Возвращает сумму зарплат всех работников
     * @return сумма зарплат
     */
    public long sumOfSalary() {
        return workersCollection.stream()
                .mapToLong(Worker::getSalary)
                .sum();
    }

    /**
     * Генерирует уникальный идентификатор для нового работника
     * @return новый уникальный id, на единицу больше максимального в коллекции
     */
    private int generateId() {
        if (workersCollection.isEmpty()) {
            return 1;
        }
        return workersCollection.stream()
                .mapToInt(Worker::getId)
                .max()
                .orElse(0) + 1;
    }
}