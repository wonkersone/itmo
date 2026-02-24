package ru.itmo.exeptions;

public class InvalidLocationException extends Exception {
    public InvalidLocationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Ошибка локации: " + super.getMessage();
    }
}
