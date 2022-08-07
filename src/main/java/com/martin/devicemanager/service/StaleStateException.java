package com.martin.devicemanager.service;

public class StaleStateException extends RuntimeException {
    public StaleStateException() {
        super("Entity has stale state");
    }
}
