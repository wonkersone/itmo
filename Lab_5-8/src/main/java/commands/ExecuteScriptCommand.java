package commands;

import managers.CollectionManager;
import managers.CommandManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Команда 'execute_script'
 * Считывает и исполняет скрипт из указанного файла
 */
public class ExecuteScriptCommand extends Command {
    private final CommandManager commandManager;
    private static final Set<String> executedScripts = new HashSet<>();

    /**
     * Создает команду execute_script
     * @param commandManager менеджер команд для выполнения команд из скрипта
     */
    public ExecuteScriptCommand(CommandManager commandManager) {
        super("execute_script", "считать и исполнить скрипт из указанного файла");
        this.commandManager = commandManager;
    }

    /**
     * Исполняет команду
     * @param args аргументы команды (путь к файлу скрипта)
     * @param collectionManager менеджер коллекции
     */
    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        try {
            if (args.length != 1) throw new IllegalArgumentException();
            String fileName = args[0];
            
            if (!executedScripts.add(fileName)) {
                System.out.println("Обнаружена рекурсия в скрипте");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] commandArgs = line.trim().split("\\s+");
                    if (commandArgs.length > 0) {
                        String commandName = commandArgs[0];
                        String[] commandArguments = new String[commandArgs.length - 1];
                        System.arraycopy(commandArgs, 1, commandArguments, 0, commandArgs.length - 1);
                        commandManager.executeCommand(commandName, commandArguments, collectionManager);
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
            } finally {
                executedScripts.remove(fileName);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Использование: execute_script file_name");
        }
    }
}