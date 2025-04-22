package shit;

import mainClasses.Worker;

import java.io.Serializable;
import java.util.Arrays;

public class Request implements Serializable {

    public enum RequestType {
        SCRIPT_TRANSFER, INITIAL_COMMAND, WORKER_DATA;
    }

    private RequestType type;
    private String commandName;
    private String[] args;
    private String scriptContent;
    private Worker worker;

    // Для первоначальных команд
    public Request(String commandName, String[] args) {
        this.type = RequestType.INITIAL_COMMAND;
        this.commandName = commandName;
        this.args = args;
    }

    // Для передачи данных работника
    public Request(Worker worker) {
        this.type = RequestType.WORKER_DATA;
        this.worker = worker;
    }

    //Для передачи файла со скриптом
    public Request(String commandName, String[] args, String scriptContent, RequestType type) {
        this.commandName = commandName;
        this.args = args;
        this.scriptContent = scriptContent;
        this.type = type;
    }


    @Override
    public String toString() {
        return "(type = " + type + ", commandName = " + commandName
                + ", arguments = " + Arrays.toString(args) + ", worker = " + worker + ")";
    }

    public String getScriptContent() { return scriptContent; }
    public RequestType getType() { return type; }
    public String getCommandName() { return commandName; }
    public String[] getArgs() { return args; }
    public Worker getWorker() { return worker; }
}
