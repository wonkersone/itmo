package shit;

import mainClasses.Worker;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Класс, представляющий запрос от клиента к серверу
 * Содержит информацию о типе запроса, команде и её параметрах
 */
public class Request implements Serializable {

    /**
     * Перечисление типов запросов
     */
    public enum RequestType {
        SCRIPT_TRANSFER, INITIAL_COMMAND, WORKER_DATA;
    }

    private RequestType type; // Тип запроса
    private String commandName; // Имя команды
    private String[] args; // Аргументы команды
    private String scriptContent; // Содержимое скрипта
    private Worker worker; // Данные работника

    /**
     * Конструктор для создания запроса с начальной командой
     * @param commandName имя команды
     * @param args аргументы команды
     */
    public Request(String commandName, String[] args) {
        this.type = RequestType.INITIAL_COMMAND;
        this.commandName = commandName;
        this.args = args;
    }

    /**
     * Конструктор для создания запроса с данными работника
     * @param worker объект работника
     */
    public Request(Worker worker) {
        this.type = RequestType.WORKER_DATA;
        this.worker = worker;
    }

    /**
     * Конструктор для создания запроса со скриптом
     * @param commandName имя команды
     * @param args аргументы команды
     * @param scriptContent содержимое скрипта
     * @param type тип запроса
     */
    public Request(String commandName, String[] args, String scriptContent, RequestType type) {
        this.commandName = commandName;
        this.args = args;
        this.scriptContent = scriptContent;
        this.type = type;
    }

    /**
     * Преобразует объект в строковое представление
     * @return строковое представление запроса
     */
    @Override
    public String toString() {
        return "(type = " + type + ", commandName = " + commandName
                + ", arguments = " + Arrays.toString(args) + ", worker = " + worker + ")";
    }

    /**
     * Получает содержимое скрипта
     * @return содержимое скрипта
     */
    public String getScriptContent() { return scriptContent; }

    /**
     * Получает тип запроса
     * @return тип запроса
     */
    public RequestType getType() { return type; }

    /**
     * Получает имя команды
     * @return имя команды
     */
    public String getCommandName() { return commandName; }

    /**
     * Получает аргументы команды
     * @return массив аргументов
     */
    public String[] getArgs() { return args; }

    /**
     * Получает данные работника
     * @return объект работника
     */
    public Worker getWorker() { return worker; }
}
