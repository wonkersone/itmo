package commands;

import mainClasses.Worker;
import managers.CollectionManager;

public class ShowCommand extends Command {
    public ShowCommand() {
        super("show", "вывести все элементы коллекции",
                CommandType.WITHOUT_WORKER_DATA, false);
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager) {
        if (args.length > 0) {
            return "Данная команда не принимает аргументы!";
        } else {
             return collectionManager.showCollectionElements();
        }
    }

    @Override
    public String execute(String[] args, CollectionManager collectionManager, Worker worker) {
        return "";
    }
}