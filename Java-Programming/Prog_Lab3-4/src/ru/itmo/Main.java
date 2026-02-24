package ru.itmo;

import ru.itmo.enums.Conditions;
import ru.itmo.enums.Locations;
import ru.itmo.enums.Smells;
import ru.itmo.exeptions.InvalidLocationException;
import ru.itmo.objects.Buildings;
import ru.itmo.objects.Entity;
import ru.itmo.objects.Environment;
import ru.itmo.records.Inanimate;

import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        // Создаем основные объекты
        System.out.println("Описание героев и локации:");
        Entity prince = new Entity("Принц", Locations.CASTLE, Conditions.IS_SLEEPING);
        Buildings castle = new Buildings("замок", Locations.MOUNTAIN, Smells.PLEASANT, 1.0);
        Inanimate castleDoor = new Inanimate("дверь замка", Locations.CASTLE_DOOR);
        Inanimate banner = new Inanimate("знамя", Locations.BANNER_ON_TOWER);
        Inanimate gates = new Inanimate("ворота", Locations.GATES);
        Inanimate swimmingPool = null;
        Buildings manure = new Buildings("навозная куча", Locations.FIELD, Smells.STINKY, 0.5);
        Buildings treehouse = new Buildings("домик на дереве", Locations.FOREST, Smells.WOOD, 0.2);
        Buildings bunker = new Buildings("бункер", Locations.FOREST, Smells.DAMP, 0.95);
        Environment forest = new Environment("лес", Locations.FOREST);
        Environment field = new Environment("поле", Locations.FIELD);
        //.

        //Создаем комнаты замка с помощью Inanimate и добавляем их в ArrayList
        ArrayList<Inanimate> rooms = new ArrayList<>();

        Inanimate kitchen = new Inanimate("кухня", Locations.KITCHEN);
        Inanimate bathroom = new Inanimate("ванная комната", Locations.BATHROOM);
        Inanimate hall = new Inanimate("гостиная", Locations.HALL);
        Inanimate cellar = new Inanimate("подвал", Locations.CELLAR);
        Inanimate storage = new Inanimate("кладовка", Locations.STORAGE);
        Inanimate balcony = new Inanimate("балкон", Locations.BALCONY);

        rooms.add(kitchen);
        rooms.add(bathroom);
        rooms.add(hall);
        rooms.add(cellar);
        rooms.add(storage);
        rooms.add(balcony);
        rooms.add(gates);
        //.

        //Основной сюжет
        System.out.println();
        System.out.println("Начало истории:");

        System.out.println();
        prince.wakeUp();
        prince.wander();

        try {
            prince.go(swimmingPool);
        } catch (InvalidLocationException e) {
            System.err.println(e.getMessage());
        }

        try {
            prince.go(castleDoor);
        } catch (InvalidLocationException e) {
            System.err.println(e.getMessage());
        }

        prince.breathe();
        prince.see(banner);
        System.out.println("Он осознал, что восставшие жители королевства скоро придут за ним.");
        prince.closeEyes();

        prince.escapeChoice(rooms);

        prince.run(forest, Conditions.SCARED);
        prince.findCoin();
        prince.run(forest, prince.getCondition());

        prince.hideChoice(manure, treehouse, bunker);

    }
}
