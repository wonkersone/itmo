package ru.itmo.objects;

import ru.itmo.enums.Locations;
import ru.itmo.enums.Smells;

public class Buildings extends GeneralObject {
    Smells smell;
    double hideLevel;

    public Buildings(String name, Locations location, Smells smell, double hideLevel) {
        super(name, location);
        this.smell = smell;
        this.hideLevel = hideLevel;
        System.out.println(name + " находится " + location.getValue());
    }

    public String  getSmell() {
        return smell.getValue();
    }

    public double getHideLevel() {
        return hideLevel;
    }
}
