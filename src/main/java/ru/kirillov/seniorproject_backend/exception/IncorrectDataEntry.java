package ru.kirillov.seniorproject_backend.exception;

public class IncorrectDataEntry extends RuntimeException {

    private final long id;

    public IncorrectDataEntry(String msg, long id) {
        super(msg);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
