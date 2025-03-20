package me.gamordstrimer.utils;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.GZIPOutputStream;

public class LogsManager {

    private static LogsManager instance;
    private static final String LOGS_FOLDER = "logs"; // Folder for logs
    private static final String ARCHIVE_FOLDER = LOGS_FOLDER + "/archive"; // Archive folder
    private final String LOG_FILE_PATH; // Path to log file

    private LogsManager() {
        this.LOG_FILE_PATH = generateLogFileName();
        ensureLogFileExists();
    }

    public static LogsManager getInstance() {
        if (instance == null) {
            instance = new LogsManager();
        }
        return instance;
    }

    private String generateLogFileName() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("HH-mm-ss-dd-MM-yyyy")); // Format: HH-date
        return LOGS_FOLDER + "/log-" + timestamp + ".log";
    }

    private void ensureLogFileExists() {
        File folder = new File(LOGS_FOLDER);
        if (!folder.exists()) {
            folder.mkdir(); // Create logs folder if it doesn't exist
        }

        File logFile = new File(LOG_FILE_PATH);
        try {
            if (!logFile.exists()) {
                logFile.createNewFile(); // Create log file if it doesn't exist
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeInLogFile(String logLine) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + logLine);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void archiveOldLog() {
        File logFile = new File(LOG_FILE_PATH);
        File archiveFolder = new File(ARCHIVE_FOLDER);

        // Ensure the archive folder exists
        if (!archiveFolder.exists()) {
            archiveFolder.mkdir();
        }

        if (logFile.exists()) {
            String archiveFileName = "log-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss-dd-MM-yyyy")) + ".log.gz";
            File archiveFile = new File(ARCHIVE_FOLDER + "/" + archiveFileName);

            try {
                compressLogFile(logFile, archiveFile);
                Files.delete(logFile.toPath()); // Delete original log file after compression
                System.out.println("Log file archived as: " + archiveFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to archive log file.");
            }
        }
    }

    private void compressLogFile(File sourceFile, File destinationFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destinationFile);
             GZIPOutputStream gzipOS = new GZIPOutputStream(fos)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
        }
    }

    public void appClosing() {
        writeInLogFile("============ APPLICATION CLOSING ============\n");
        archiveOldLog();
    }

    public void appStarting() {
        writeInLogFile("============ APPLICATION STARTING ============\n");
    }

    public void openLogsFolder() {
        File logsFolder = new File(LOGS_FOLDER);

        if (!logsFolder.exists()) {
            System.out.println("Logs folder does not exist.");
            return;
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(logsFolder);
            } else {
                System.out.println("Desktop is not supported. Open the folder manually: " + logsFolder.getAbsolutePath());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Failed to open logs Folder");
        }
    }
}
