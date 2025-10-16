package com.xw.api.exception;

/**
 * Exception thrown when attempting to delete a category that contains items.
 * This enforces the business rule that categories with existing items cannot be deleted.
 */
public class CategoryNotEmptyException extends RuntimeException {
    
    public CategoryNotEmptyException(String message) {
        super(message);
    }
    
    public CategoryNotEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}