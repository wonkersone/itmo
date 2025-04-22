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

public class UserInputScanner {
    private final TCPClient client;
    private final Scanner scanner;
    private final WorkerInputHelper helper;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final Set<String> executedScripts = new HashSet<>();

    public UserInputScanner(TCPClient client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.helper = new WorkerInputHelper();
    }

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

//    private Request createRequest(String input) throws IOException, ScriptRecursionException {
//        String[] parts = input.split(" ", 2);
//        String commandName = parts[0];
//        String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];
//
//        if (commandName.equalsIgnoreCase("execute_script")) {
//            if (args.length == 0) throw new IllegalArgumentException("Не указан путь к скрипту");
//
//            String scriptPath = args[0];
//            if (executedScripts.contains(scriptPath)) {
//                throw new ScriptRecursionException("Рекурсия! Скрипт " + scriptPath + " уже выполняется");
//            }
//            executedScripts.add(scriptPath);
//
//            String scriptContent = new String(Files.readAllBytes(Paths.get(scriptPath)));
//            return new Request(
//                    commandName,
//                    args,
//                    scriptContent,
//                    Request.RequestType.SCRIPT_TRANSFER
//            );
//        } else {
//            return new Request(commandName, args, null, Request.RequestType.INITIAL_COMMAND);
//        }
//    }

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

    private void checkRecursion(String scriptPath) throws ScriptRecursionException {
        if (executedScripts.contains(scriptPath)) {
            throw new ScriptRecursionException("Рекурсия! Скрипт " + scriptPath + " уже выполняется");
        }
        executedScripts.add(scriptPath);
    }

    private String readScriptContent(String scriptPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(scriptPath)));
    }

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