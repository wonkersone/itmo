package commands;

import managers.CollectionManager;

public class ShowCommand extends Command {
    public ShowCommand() {
        super("show", "вывести все элементы коллекции");
    }

    @Override
    public void execute(String[] args, CollectionManager collectionManager) {
        collectionManager.showCollectionElements();
    }
}