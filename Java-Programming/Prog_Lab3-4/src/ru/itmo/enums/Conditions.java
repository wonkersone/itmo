package ru.itmo.enums;

public enum Conditions {
    SLEEPY("сонный"), RELAXED("на расслабоне, на чилле"), SCARED("в ужасе"), STINKY("вонючий"), CHEERFUL("бодрячком"), AMAZED("ошарашен"), TIRED("уставший"), IS_SLEEPING("спит"), SECRETIVE("скрытный"), AGGRESSIVE("злой"), CALM("спокойный"), HAPPY("счастливый"), SAD("грустный");

    private final String value;

    Conditions(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
