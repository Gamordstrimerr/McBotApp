package me.gamordstrimer.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogsManager {

    private static final String LOGS_FOLDER = "logs"; // Folder for logs
    private final String LOG_FILE_PATH; // Path to log file

    public LogsManager() {
        this.LOG_FILE_PATH = generateLogFileName();
    }

    private String generateLogFileName() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("HH-yyyy-MM-dd")); // Format: HH-date
        return LOGS_FOLDER + "/log-" + timestamp + ".log";
    }

    public void writeInLogFile(String logLine) {

    }
}
