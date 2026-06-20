package com.enviro.assessment.junior.tadii.exception;

// ── Resource Not Found ───────────────────────────────────────────────────────
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
