package com.kneelawk.krender.model.loading.impl;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KRLog {
    public static final Logger LOGGER = LoggerFactory.getLogger(KRConstants.MOD_ID);
    
    public static Consumer<Exception> error(String message) {
        return e -> LOGGER.error("[Common Events] {}", message, e);
    }
}
