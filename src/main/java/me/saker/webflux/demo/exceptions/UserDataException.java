package me.saker.webflux.demo.exceptions;

public class UserDataException extends RuntimeException {
    public UserDataException(String message) {
        super(message);
    }
}
