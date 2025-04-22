import commands.Command;
import exceptions.ScriptRecursionException;
import mainClasses.Worker;
import managers.CollectionManager;
import managers.CommandManager;
import network.TCPServer;
import shit.Request;
import shit.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class NewTCPServer {
    private static final Logger logger = LogManager.getLogger(TCPServer.class);
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private volatile boolean isRunning = true;
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;
    private final Scanner scanner = new Scanner(System.in);
    private final ExecutorService workerPool = Executors.newFixedThreadPool(4);
    private final Map<SocketChannel, ByteBuffer> buffers = new ConcurrentHashMap<>();
    private final Set<String> activeScripts = new HashSet<>();
    private int clientCount = 0;

    public NewTCPServer(CollectionManager collectionManager, CommandManager commandManager) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }

    public void start(int port) throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));

        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("Сервер запущен на порту {}", port);

        new Thread(this::adminInput).start();

        while (isRunning) {
            try {
                selector.select(500);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();

                for (SelectionKey key : selectedKeys) {
                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        acceptClient(key);
                    } else if (key.isReadable()) {
                        readData(key);
                    }
                }
                selectedKeys.clear();
            } catch (IOException e) {
                if (!isRunning) break;
                logger.error("Ошибка селектора: {}", e.getMessage());
            }
        }
    }

    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        buffers.put(clientChannel, ByteBuffer.allocate(8192));
        logger.info("Подключение от клиента №{}", ++clientCount);
    }

    private void readData(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = buffers.get(clientChannel);

        try {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                closeConnection(clientChannel);
                return;
            }

            if (bytesRead > 0) {
                buffer.flip();

                while (buffer.remaining() >= 4) {
                    buffer.mark();
                    int objectSize = buffer.getInt();

                    if (objectSize <= 0 || objectSize > 10_000_000) {
                        logger.error("Invalid object size: {}", objectSize);
                        closeConnection(clientChannel);
                        return;
                    }

                    if (buffer.remaining() >= objectSize) {
                        byte[] objectData = new byte[objectSize];
                        buffer.get(objectData);
                        processRequest(clientChannel, objectData);
                        buffer.compact();
                    } else {
                        buffer.reset();
                        buffer.compact();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            closeConnection(clientChannel);
            logger.error("Ошибка чтения: {}", e.getMessage());
        }
    }

    private void processRequest(SocketChannel channel, byte[] data) {
        workerPool.submit(() -> {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {

                Request request = (Request) ois.readObject();
                logger.info("Получен запрос: {}", request);

                Response response = handleRequest(request, channel);
                sendResponse(channel, response);

            } catch (Exception e) {
                logger.error("Ошибка обработки запроса: {}", e.getMessage());
                closeConnection(channel);
            }
        });
    }

    private Response handleRequest(Request request, SocketChannel channel) throws IOException, ClassNotFoundException {
        if (request.getType() == Request.RequestType.INITIAL_COMMAND) {
            return processInitialCommand(request, channel);
        } else if (request.getType() == Request.RequestType.SCRIPT_TRANSFER) {
            return processScriptRequest(request);
        }
        return new Response(Response.ResponseType.ERROR, "Неизвестный тип запроса");
    }

    private Response processInitialCommand(Request request, SocketChannel channel) throws IOException, ClassNotFoundException {
        Command command = commandManager.getCommands().get(request.getCommandName());
        if (command == null) {
            return new Response(Response.ResponseType.ERROR, "Введена некорректная команда!");
        }

        if (command.needArgs && request.getArgs().length == 0) {
            return new Response(Response.ResponseType.ERROR, "У данной команды должен быть аргумент");
        }

        if (command.getCommandType() == Command.CommandType.WITHOUT_WORKER_DATA) {
            return commandManager.executeCommand(request.getCommandName(), request.getArgs(), collectionManager);
        } else {
            sendResponse(channel, new Response(Response.ResponseType.NEED_WORKER, "Требуется работник"));
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int bytesRead = channel.read(buffer);
            if (bytesRead <= 0) throw new IOException("Не удалось прочитать работника");

            buffer.flip();
            byte[] workerData = new byte[buffer.remaining()];
            buffer.get(workerData);

            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(workerData))) {
                Worker worker = (Worker) ois.readObject();
                return commandManager.executeCommand(request.getCommandName(), request.getArgs(), collectionManager, worker);
            }
        }
    }

    private Response processScriptRequest(Request request) {
        String scriptPath = request.getArgs()[0];
        StringBuilder result = new StringBuilder();

        try {
            if (activeScripts.contains(scriptPath)) {
                throw new ScriptRecursionException("Рекурсия! Скрипт " + scriptPath + " уже выполняется");
            }
            activeScripts.add(scriptPath);

            result.append("=== Начало выполнения скрипта ").append(scriptPath).append(" ===\n");
            result.append(processScriptContent(request.getScriptContent(), scriptPath));
            result.append("=== Завершение скрипта ").append(scriptPath).append(" ===\n");

        } catch (ScriptRecursionException e) {
            result.append("ОШИБКА: ").append(e.getMessage()).append("\n");
        } finally {
            activeScripts.remove(scriptPath);
        }
        return new Response(Response.ResponseType.INFO, result.toString());
    }

    // Остальные методы (processScriptContent, handleNestedScript, adminInput) остаются без изменений

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

    private void sendResponse(SocketChannel channel, Response response) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        byte[] data = baos.toByteArray();

        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();

        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    public synchronized void disconnect() {
        logger.info("Начало отключения сервера");
        if (!isRunning) return;
        isRunning = false;

        collectionManager.saveCollectionToFile();

        buffers.keySet().forEach(this::closeConnection);
        buffers.clear();

        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(5, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
        }

        try {
            if (selector != null) selector.close();
            if (serverChannel != null) serverChannel.close();
        } catch (IOException e) {
            logger.error("Ошибка остановки сервера: {}", e.getMessage());
        }

        System.out.println("Сервер остановлен корректно");
        System.exit(0);
    }

    private void closeConnection(SocketChannel channel) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
                buffers.remove(channel);
                logger.info("Соединение закрыто");
            }
        } catch (IOException e) {
            logger.error("Ошибка закрытия соединения: {}", e.getMessage());
        }
    }

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

    // Остальные методы (adminInput, closeSocket) остаются без изменений
}