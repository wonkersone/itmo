package ru.itmo.enums;

public enum Locations {
    MOUNTAIN("на горе"), CASTLE("в замке"), FIELD("в поле"), FOREST("в лесу"), TOWER("на башне"), KITCHEN("кухня"), BATHROOM("ванная"), HALL("гостиная"), CELLAR("подвал"), STORAGE("кладовка"), BALCONY("балкон"), GATES("ворота"), BANNER_ON_TOWER("знамя на башне"), CASTLE_DOOR("дверь замка");

    private final String value;

    Locations(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
