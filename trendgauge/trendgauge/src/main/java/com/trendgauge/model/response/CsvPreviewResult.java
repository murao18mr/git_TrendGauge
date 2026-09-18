package com.trendgauge.model.response;

import java.util.List;

public class CsvPreviewResult {
    private List<CsvPreviewResponse> rows;
    private boolean mappingRequired;
    private int columnCount;

    public CsvPreviewResult(List<CsvPreviewResponse> rows, boolean mappingRequired, int columnCount) {
        this.rows = rows;
        this.mappingRequired = mappingRequired;
        this.columnCount = columnCount;
    }

    public List<CsvPreviewResponse> getRows() {
        return rows;
    }

    public void setRows(List<CsvPreviewResponse> rows) {
        this.rows = rows;
    }

    public boolean isMappingRequired() {
        return mappingRequired;
    }

    public void setMappingRequired(boolean mappingRequired) {
        this.mappingRequired = mappingRequired;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
}
