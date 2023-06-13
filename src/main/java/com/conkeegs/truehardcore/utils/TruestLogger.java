package com.conkeegs.truehardcore.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TruestLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("TRUEHARDCORE");

    public static Logger getLogger() {
        return LOGGER;
    }
}
