package ru.itmo.interfaces;

import ru.itmo.exeptions.InvalidLocationException;
import ru.itmo.objects.Buildings;
import ru.itmo.records.Inanimate;

public interface Movable {
    void go(Inanimate loc) throws InvalidLocationException;

    void run(Inanimate obj);

    void hide(Buildings building);

    void wander();
}
