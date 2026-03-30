package com.htw.collector.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private long total;
    private int pages;
    private int current;
    private int size;
    private List<T> records;

    public static <T> PageResult<T> of(long total, int current, int size, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setCurrent(current);
        result.setSize(size);
        result.setRecords(records);
        result.setPages(size > 0 ? (int) Math.ceil((double) total / size) : 0);
        return result;
    }
}
