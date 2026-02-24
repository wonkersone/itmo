package ru.itmo.records;

import ru.itmo.enums.Locations;

public record Inanimate(String name, Locations location) {

    public Inanimate {
        System.out.println(name + " находится в замке.");
    }

}