package me.bedwarshurts.mmextension.utils.exceptions;

public class DependencyNotFoundException extends RuntimeException {
    public DependencyNotFoundException(String message) {
        super(message);
    }
}
