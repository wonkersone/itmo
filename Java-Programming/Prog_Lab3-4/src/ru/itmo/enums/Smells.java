package ru.itmo.enums;

public enum Smells {
    PLEASANT("приятный"), STINKY("вонючий"), WOOD("деревянный"), DAMP("сырой");

    private final String value;

    Smells(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
