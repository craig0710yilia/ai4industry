package com.htw.collector.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BackupService {

    @Value("${collector.backup.directory:./backups}")
    private String backupDir;

    @Value("${collector.backup.pg-dump-path:pg_dump}")
    private String pgDumpPath;

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/iot_collector}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:postgres}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:postgres}")
    private String datasourcePassword;

    @Scheduled(cron = "${collector.backup.cron:0 0 2 1 * ?}")
    public void performMonthlyBackup() {
        log.info("Starting scheduled database backup...");
        try {
            Path backupPath = Paths.get(backupDir);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
                log.info("Created backup directory: {}", backupDir);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "backup_" + timestamp + ".sql";
            Path outputFile = backupPath.resolve(filename);

            // Parse connection info from datasource URL
            // Expected format: jdbc:postgresql://host:port/dbname
            String dbInfo = datasourceUrl.replace("jdbc:postgresql://", "");
            String[] parts = dbInfo.split("/");
            String hostPort = parts[0];
            String dbName = parts.length > 1 ? parts[1].split("\\?")[0] : "iot_collector";
            String host = hostPort.split(":")[0];
            String port = hostPort.contains(":") ? hostPort.split(":")[1] : "5432";

            ProcessBuilder pb = new ProcessBuilder(
                    pgDumpPath,
                    "-h", host,
                    "-p", port,
                    "-U", datasourceUsername,
                    "-d", dbName,
                    "-f", outputFile.toString()
            );
            pb.environment().put("PGPASSWORD", datasourcePassword);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Database backup completed successfully: {}", outputFile);
            } else {
                log.error("Database backup failed with exit code: {}", exitCode);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Database backup failed", e);
            Thread.currentThread().interrupt();
        }
    }

    public List<String> listBackups() {
        File dir = new File(backupDir);
        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .map(File::getName)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }

    public void triggerManualBackup() {
        performMonthlyBackup();
    }
}
