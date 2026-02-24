package ru.itmo.enums;

public enum HideLevels {
    VISIBLE("видимый"), SECRETIVE("скрытный"), INVISIBLE("невидимый");

    private final String value;

    HideLevels(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
