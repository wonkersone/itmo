package network;

import commands.Command;
import exceptions.ScriptRecursionException;
import mainClasses.Worker;
import managers.CollectionManager;
import managers.CommandManager;
import shit.Request;
import shit.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Selector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс, реализующий TCP сервер для обработки клиентских запросов
 * Обеспечивает многопоточную обработку подключений и выполнение команд
 */
public class TCPServer {
    /** Логгер для записи событий сервера */
    private static final Logger logger = LogManager.getLogger(TCPServer.class);
    /** Селектор для неблокирующего ввода/вывода */
    private Selector selector;
    /** Серверный сокет для приема подключений */
    private ServerSocket serverSocket;
    /** Флаг работы сервера */
    private volatile boolean isRunning = true;
    /** Менеджер коллекции работников */
    private final CollectionManager collectionManager;
    /** Менеджер команд */
    private final CommandManager commandManager;
    /** Сканер для чтения команд администратора */
    private final Scanner scanner = new Scanner(System.in);
    /** Список активных подключений */
    private final List<Socket> activeSockets = new CopyOnWriteArrayList<>();
    /** Счетчик подключенных клиентов */
    public int clientCount = 0;
    /** Множество активных скриптов для предотвращения рекурсии */
    private final Set<String> activeScripts = new HashSet<>();

    /**
     * Конструктор сервера
     * @param collectionManager менеджер коллекции
     * @param commandManager менеджер команд
     */
    public TCPServer(CollectionManager collectionManager, CommandManager commandManager) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    /**
     * Запускает сервер на указанном порту
     * @param port порт для прослушивания
     * @throws IOException если возникла ошибка при запуске сервера
     */
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        logger.info("Сервер запущен на порту {}", port);

        new Thread(this::adminInput).start();

        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                activeSockets.add(clientSocket);
                logger.info("Подключение от клиента №{}", ++clientCount);
                new Thread(() -> handleConnection(clientSocket)).start();
            } catch (IOException e) {
                if (!isRunning) {
                    logger.info("Сервер остановлен!");
                    break;
                }
            }
        }
    }

    /**
     * Обрабатывает подключение клиента
     * @param socket сокет клиента
     */
    private void handleConnection(Socket socket) {
        try {

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                Request request = (Request) in.readObject();
                logger.info("Получен запрос: {}", request.toString());

                if (request.getType() == Request.RequestType.INITIAL_COMMAND) {
                    Command command = commandManager.getCommands().get(request.getCommandName());
                    if (command != null) {
                        if (command.needArgs && request.getArgs().length == 0) {
                            Response response = new Response(Response.ResponseType.ERROR, false,
                                    "У данной команды обязательно должен быть указан ее аргумент.");
                            logger.info("Сформирован ответ клиенту: {}", response.toString());
                            sendResponse(response, out);
                            logger.warn("Команда была введена некорректно!");
                        } else {
                            logger.info("Запрос на выполнение команды: {}", command.toString());
                            if (command.getCommandType() == Command.CommandType.WITHOUT_WORKER_DATA) {
                                Response response = commandManager.executeCommand(request.getCommandName(), request.getArgs(), collectionManager);
                                logger.info("Сформирован ответ клиенту: {}", response.toString());
                                sendResponse(response, out);
                            } else {
                                Response response = new Response(Response.ResponseType.NEED_WORKER, "Требуется ввести данные о работнике");
                                logger.info("Сформирован ответ клиенту: {}", response.toString());
                                out.writeObject(response);
                                Request newRequest = (Request) in.readObject();
                                Worker worker = newRequest.getWorker();
                                logger.info("Получены данные о работнике: {}", newRequest.getWorker().toString());
                                Response newResponse = commandManager.executeCommand(request.getCommandName(), request.getArgs(), collectionManager, worker);
                                logger.info("Сформирован ответ клиенту: {}", newResponse.toString());
                                sendResponse(newResponse, out);
                            }
                        }
                    } else {
                        Response response = new Response(Response.ResponseType.ERROR, "Введена некорректная команда!");
                        logger.warn("Сформирован ответ клиенту: {}", response.toString());
                        sendResponse(response, out);
                    }
                } else if (request.getType() == Request.RequestType.SCRIPT_TRANSFER) {
                    processScriptRequest(request, out);
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка при обработке соединения: {}", (Object) e.getStackTrace());
        } finally {
            closeSocket(socket);
            activeSockets.remove(socket);
        }
    }

    /**
     * Обрабатывает запрос на выполнение скрипта
     * @param request запрос клиента
     * @param out поток для отправки ответа
     * @throws IOException если возникла ошибка при отправке ответа
     */
    private void processScriptRequest(Request request, ObjectOutputStream out) throws IOException {
        String scriptPath = request.getArgs()[0];
        String scriptContent = request.getScriptContent();
        StringBuilder result = new StringBuilder();

        try {
            if (activeScripts.contains(scriptPath)) {
                throw new ScriptRecursionException("Рекурсия! Скрипт " + scriptPath + " уже выполняется");
            }
            activeScripts.add(scriptPath);
            logger.info("Начало выполнения скрипта: {}", scriptPath);

            result.append("=== Начало выполнения скрипта ").append(scriptPath).append(" ===\n");
            result.append(processScriptContent(scriptContent, scriptPath));
            result.append("=== Завершение скрипта ").append(scriptPath).append(" ===\n");

        } catch (ScriptRecursionException e) {
            logger.error("Ошибка при выполнении скрипта: {}", e.getMessage());
            result.append("ОШИБКА: ").append(e.getMessage()).append("\n");
        } finally {
            activeScripts.remove(scriptPath);
            Response newResponse = new Response(Response.ResponseType.INFO, result.toString());
            logger.info("Сформирован ответ клиенту: {}", newResponse.toString());
            sendResponse(newResponse, out);
        }
    }

    /**
     * Обрабатывает содержимое скрипта
     * @param content содержимое скрипта
     * @param currentScriptPath путь к текущему скрипту
     * @return результат выполнения скрипта
     */
    private String processScriptContent(String content, String currentScriptPath) {
        StringBuilder output = new StringBuilder();
        List<String> lines = Arrays.asList(content.split("\n"));

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            try {
                String[] parts = line.split("\\s+", 2);
                String commandName = parts[0];
                String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

                if (commandName.equalsIgnoreCase("execute_script")) {
                    String nestedScriptPath = args[0];
                    output.append(handleNestedScript(nestedScriptPath, currentScriptPath));
                } else {
                    Response response = commandManager.executeCommand(commandName, args, collectionManager);
                    output.append(response.getMessage()).append("\n");
                }
            } catch (Exception e) {
                output.append("ОШИБКА: ").append(e.getMessage()).append("\n");
            }
        }
        return output.toString();
    }

    /**
     * Обрабатывает вложенный скрипт
     * @param scriptPath путь к вложенному скрипту
     * @param parentScript путь к родительскому скрипту
     * @return результат выполнения вложенного скрипта
     * @throws Exception если возникла ошибка при выполнении скрипта
     */
    private String handleNestedScript(String scriptPath, String parentScript) throws Exception {
        if (activeScripts.contains(scriptPath)) {
            throw new ScriptRecursionException("Рекурсивный вызов из " + parentScript + " в " + scriptPath);
        }

        activeScripts.add(scriptPath);
        StringBuilder output = new StringBuilder();

        try {
            Path path = Paths.get(scriptPath);
            String content = new String(Files.readAllBytes(path));
            output.append("=== Начало вложенного скрипта ").append(scriptPath).append(" ===\n");
            output.append(processScriptContent(content, scriptPath));
            output.append("=== Конец вложенного скрипта ").append(scriptPath).append(" ===\n");
        } finally {
            activeScripts.remove(scriptPath);
        }
        return output.toString();
    }

    /**
     * Отправляет ответ клиенту
     * @param response ответ для отправки
     * @param out поток для отправки
     * @throws IOException если возникла ошибка при отправке
     */
    public void sendResponse(Response response, ObjectOutputStream out) throws IOException {
        out.writeObject(response);
        out.flush();
    }

    /**
     * Запускает режим обработки команд администратора
     */
    public void adminInput() {
        logger.info("Доступен интерактивный режим админа");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) { continue; }

            String[] parts = input.split(" ", 2);
            String commandName = parts[0];
            String[] commandArgs = parts.length > 1 ? parts[1].split(" ") : new String[0];

            if (commandName.equalsIgnoreCase("exit")) {
                logger.info("Получена команда на завершение работы сервера");
                disconnect();
            } else {
                logger.info("Выполнение команды администратора: {}", commandName);
                System.out.println((commandManager.executeCommand(commandName, commandArgs, collectionManager)).getMessage());
            }
        }
    }

    /**
     * Останавливает сервер и освобождает ресурсы
     */
    public synchronized void disconnect() {
        logger.info("Начало отключения сервера");
        if (!isRunning) return;
        isRunning = false;
        System.out.println("Завершение работы сервера...");

        collectionManager.saveCollectionToFile();

        activeSockets.forEach(this::closeSocket);

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            logger.info("Сервер успешно остановлен");
        } catch (IOException e) {
            logger.error("Ошибка при остановке сервера: {}", e.getMessage());
        }

        System.out.println("Коллекция сохранена в файл. \nСервер остановлен корректно. Всем пока!");
        System.exit(0);
    }

    /**
     * Закрывает сокет клиента
     * @param socket сокет для закрытия
     */
    private void closeSocket(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logger.info("Соединение с клиентом закрыто!");
            }
        } catch (IOException e) {
            logger.error("Ошибка закрытия сокета: {}", e.getMessage());
        }
    }

//    /**
//     * Обрабатывает начальную команду от клиента
//     * @param request запрос клиента
//     * @param out поток для отправки ответа
//     * @param in поток для чтения данных
//     * @throws IOException если возникла ошибка при обмене данными
//     * @throws ClassNotFoundException если возникла ошибка при десериализации
//     */
//    private void handleInitialCommand(Request request, ObjectOutputStream out, ObjectInputStream in) throws IOException, ClassNotFoundException {
//        Command command = commandManager.getCommands().get(request.getCommandName());
//        if (command != null) {
//            if (command.needArgs && request.getArgs().length == 0) {
//                Response response = new Response(Response.ResponseType.ERROR, false,
//                        "У данной команды обязательно должен быть указан ее аргумент.");
//                logger.info("Сформирован ответ клиенту: {}", response.toString());
//                sendResponse(response, out);
//                logger.warn("Команда была введена некорректно!");
//            } else {
//                logger.info("Запрос на выполнение команды: {}", command.toString());
//                if (command.getCommandType() == Command.CommandType.WITHOUT_WORKER_DATA) {
//                    Response response = commandManager.executeCommand(request.getCommandName(), request.getArgs(), collectionManager);
//                    logger.info("Сформирован ответ клиенту: {}", response.toString());
//                    sendResponse(response, out);
//                } else {
//                    Response response = new Response(Response.ResponseType.NEED_WORKER, "Требуется ввести данные о работнике");
//                    logger.info("Сформирован ответ клиенту: {}", response.toString());
//                    out.writeObject(response);
//                    Request newRequest = (Request) in.readObject();
//                    Worker worker = newRequest.getWorker();
//                    logger.info("Получены данные о работнике: {}", newRequest.getWorker().toString());
//                    Response newResponse = commandManager.executeCommand(request.getCommandName(), request.getArgs(), collectionManager, worker);
//                    logger.info("Сформирован ответ клиенту: {}", newResponse.toString());
//                    sendResponse(newResponse, out);
//                }
//            }
//        } else {
//            Response response = new Response(Response.ResponseType.ERROR, "Введена некорректная команда!");
//            logger.warn("Сформирован ответ клиенту: {}", response.toString());
//            sendResponse(response, out);
//        }
//    }
}