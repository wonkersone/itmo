package userManagers;

import exceptions.ScriptRecursionException;
import mainClasses.Worker;
import network.TCPClient;
import shit.Request;
import shit.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Класс для обработки пользовательского ввода и взаимодействия с сервером
 * Поддерживает интерактивный режим работы и выполнение скриптов
 */
public class UserInputScanner {
    /** Клиент для взаимодействия с сервером */
    private final TCPClient client;
    /** Сканер для чтения пользовательского ввода */
    private final Scanner scanner;
    /** Помощник для ввода данных работника */
    private final WorkerInputHelper helper;
    /** Форматтер для дат */
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    /** Множество выполненных скриптов для предотвращения рекурсии */
    private final Set<String> executedScripts = new HashSet<>();

    /**
     * Создает новый экземпляр UserInputScanner
     * @param client TCP клиент для взаимодействия с сервером
     */
    public UserInputScanner(TCPClient client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.helper = new WorkerInputHelper();
    }

    /**
     * Запускает интерактивный режим работы с пользователем
     * Обрабатывает команды пользователя, отправляет запросы на сервер
     * и выводит результаты выполнения команд
     */
    public void startInteractiveMode() {
        System.out.println("Клиент запущен в интерактивном режиме!");
        Scanner scanner = new Scanner(System.in).useDelimiter("\n"); // Исправляем работу с переводом строк

        while (true) {
            try {
                System.out.print("> ");
                if (!scanner.hasNext()) {  // Проверяем доступность ввода
                    System.out.println("Нет данных для чтения");
                    break;
                }

                String input = scanner.next().trim();
                if (input.isEmpty()) continue;

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Завершение работы клиента");
                    break;
                } else if ("save".equalsIgnoreCase(input)) {
                    System.out.println("Ошибка: Введена некорректная команда!");
                    continue;
                }

                // Создаем запрос
                Request request = createRequest(input);

                // Отправляем запрос и получаем ответ
                Response response = client.sendRequest(request);
                executedScripts.clear();
                if (response.getType() == Response.ResponseType.NEED_WORKER) {
                    System.out.println("Сервер запрашивает данные работника!");
                    Worker newWorker = helper.inputWorker();
                    Request newRequest = new Request(newWorker);
                    Response newResponse = client.sendRequest(newRequest);
                    System.out.println("\n" + newResponse.getMessage());
                } else if (response.getType() == Response.ResponseType.ERROR) {
                    System.out.println("Ошибка: " + response.getMessage());
                } else if (response.getType() == Response.ResponseType.ONE_MORE_SCRIPT) {
                    handleNestedScript(response);
                }
                else {
                    System.out.println("\n" + response.getMessage());
                }

            } catch (ScriptRecursionException exception) {
                System.out.println(exception.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка подключения к серверу: " + e.getMessage());
                // Восстанавливаем соединение при необходимости
                try {
                    System.out.println("Пытаемся восстановить подключение...");
                    client.disconnect();
                    client.connect("localhost", 8000);
                } catch (IOException ex) {
                    System.out.println("Не удалось восстановить соединение: " + ex.getMessage());
                    break;
                }
            }
        }
        scanner.close();
    }

    /**
     * Создает запрос на основе пользовательского ввода
     * @param input строка с командой и аргументами
     * @return объект запроса
     * @throws IOException если произошла ошибка при чтении файла скрипта
     * @throws ScriptRecursionException если обнаружена рекурсия в скриптах
     */
    private Request createRequest(String input) throws IOException, ScriptRecursionException {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0];
        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

        if (commandName.equalsIgnoreCase("execute_script")) {
            String scriptPath = args[0];
            checkRecursion(scriptPath);
            String scriptContent = readScriptContent(scriptPath);
            return new Request(
                    commandName,
                    args,
                    scriptContent,
                    Request.RequestType.SCRIPT_TRANSFER
            );
        }
        return new Request(commandName, args);
    }

    /**
     * Проверяет наличие рекурсии в скриптах
     * @param scriptPath путь к скрипту
     * @throws ScriptRecursionException если скрипт уже выполняется
     */
    private void checkRecursion(String scriptPath) throws ScriptRecursionException {
        if (executedScripts.contains(scriptPath)) {
            throw new ScriptRecursionException("Рекурсия! Скрипт " + scriptPath + " уже выполняется");
        }
        executedScripts.add(scriptPath);
    }

    /**
     * Читает содержимое скрипта из файла
     * @param scriptPath путь к файлу скрипта
     * @return содержимое скрипта
     * @throws IOException если произошла ошибка при чтении файла
     */
    private String readScriptContent(String scriptPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(scriptPath)));
    }

    /**
     * Обрабатывает вложенный скрипт, полученный от сервера
     * @param response ответ сервера с информацией о вложенном скрипте
     */
    private void handleNestedScript(Response response) {
        String scriptPath = response.getMessage().split(": ")[1];
        try {
            Request scriptRequest = createRequest("execute_script " + scriptPath);
            Response scriptResponse = client.sendRequest(scriptRequest);
            System.out.println(scriptResponse.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка выполнения вложенного скрипта: " + e.getMessage());
        }
    }
}