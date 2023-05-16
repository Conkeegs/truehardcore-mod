package com.conkeegs.truehardcore.utils;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TrueHardcoreLogger {
    private static final String PREFIX = "[TRUEHARDCORE]";

    public static Logger getLogger() {
        Logger logger = Logger.getLogger("");
        Handler[] handlers = logger.getHandlers();

        for (Handler handler : handlers) {
            handler.setFormatter(new CustomLogFormatter());
            handler.setLevel(Level.ALL); // Adjust the log level as needed
        }

        return logger;
    }

    private static class CustomLogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return PREFIX + " " + record.getMessage() + "\n";
        }
    }
}
