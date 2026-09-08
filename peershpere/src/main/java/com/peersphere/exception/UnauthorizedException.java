package com.peersphere.exception;

/**
 * Thrown when a user tries to perform an action they don't have
 * permission for — like deleting a group they don't own.
 * This maps to HTTP 403 Forbidden.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}