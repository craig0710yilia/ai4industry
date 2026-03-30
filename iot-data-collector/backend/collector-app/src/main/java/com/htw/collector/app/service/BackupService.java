package com.htw.collector.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Monthly backup of PostgreSQL data using pg_dump.
 * Runs at 02:00 on the 1st day of every month.
 */
@Slf4j
@Service
public class BackupService {

    @Value("${collector.backup.dir:/data/backups}")
    private String backupDir;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Scheduled(cron = "0 0 2 1 * *")
    public void monthlyBackup() {
        String month = YearMonth.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        log.info("Starting monthly backup for {}", month);
        try {
            Path dir = Path.of(backupDir);
            Files.createDirectories(dir);
            String filename = String.format("collector_backup_%s.sql.gz", month);
            Path outFile = dir.resolve(filename);

            // Parse jdbc url to extract db name and host
            // jdbc:postgresql://host:port/dbname
            String[] parts = datasourceUrl.replace("jdbc:postgresql://", "").split("/");
            String hostPort = parts[0];
            String dbName = parts[1].split("\\?")[0];

            ProcessBuilder pb = new ProcessBuilder(
                    "sh", "-c",
                    String.format(
                            "PGPASSWORD='%s' pg_dump -h %s -U %s %s | gzip > %s",
                            dbPassword.replace("'", "\\'"),
                            hostPort.split(":")[0],
                            dbUsername,
                            dbName,
                            outFile.toAbsolutePath()
                    )
            );
            pb.inheritIO();
            Process proc = pb.start();
            int exitCode = proc.waitFor();
            if (exitCode == 0) {
                log.info("Backup completed: {}", outFile);
            } else {
                log.error("Backup failed with exit code {}", exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Backup error", e);
            Thread.currentThread().interrupt();
        }
    }

    public void triggerManualBackup() {
        monthlyBackup();
    }
}
