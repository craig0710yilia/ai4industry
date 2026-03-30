package com.htw.collector.adapter.rest;

import com.htw.collector.app.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerBackup() {
        backupService.triggerManualBackup();
        return ResponseEntity.ok("Backup triggered");
    }
}
