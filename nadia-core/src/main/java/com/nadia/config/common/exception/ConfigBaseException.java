package com.nadia.config.common.exception;

public abstract class ConfigBaseException extends RuntimeException {

    protected ConfigBaseException(String message) {
        super(message);
    }
}
