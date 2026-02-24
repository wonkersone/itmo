package ru.itmo.interfaces;

import ru.itmo.objects.Buildings;
import ru.itmo.records.Inanimate;

import java.util.ArrayList;

public interface Scenario {
    void wakeUp();

    void breathe();

    void see(Inanimate object);

    void closeEyes();

    void escapeChoice(ArrayList<Inanimate> rooms);

    void hideChoice(Buildings build1, Buildings build2, Buildings build3);

    void findCoin();
}
