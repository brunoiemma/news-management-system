package com.example.news.management.system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para indicar que un recurso no fue encontrado.
 * Al ser lanzada, Spring Boot responderá con un estado HTTP 404 (Not Found).
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}