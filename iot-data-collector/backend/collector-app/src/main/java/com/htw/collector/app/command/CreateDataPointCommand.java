package com.htw.collector.app.command;

import com.htw.collector.domain.enums.DataType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDataPointCommand {
    @NotNull
    private Long dataSourceId;
    @NotBlank
    private String tagName;
    @NotNull
    private DataType dataType;
    @NotBlank
    private String address;
    @Min(1000)
    private int intervalMs = 1000;
    private String unit;
    private String description;
    private boolean enabled = true;
}
