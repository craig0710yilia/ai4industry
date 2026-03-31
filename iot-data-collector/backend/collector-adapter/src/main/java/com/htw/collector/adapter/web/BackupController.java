package com.htw.collector.adapter.web;

import com.htw.collector.adapter.web.common.Result;
import com.htw.collector.app.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/backups")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    @GetMapping
    public Result<List<String>> listBackups() {
        return Result.success(backupService.listBackups());
    }

    @PostMapping("/trigger")
    public Result<Void> triggerBackup() {
        backupService.triggerManualBackup();
        return Result.success();
    }
}
