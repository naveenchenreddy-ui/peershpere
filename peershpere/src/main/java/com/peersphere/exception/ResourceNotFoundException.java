package com.peersphere.exception;

/**
 * Thrown when a requested resource (user, group, note...) doesn't exist.
 * This is a RuntimeException so we don't have to declare it with 'throws'.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}