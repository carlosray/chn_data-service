package ru.vas.dataservice.exception;

public class UpdateNotFoundException extends Exception{
    private static final long serialVersionUID = -7867924516955077947L;

    public UpdateNotFoundException(String message) {
        super(message);
    }
}
