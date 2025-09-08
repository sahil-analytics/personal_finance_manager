package com.financemanager.webapp.dto;

import java.util.List;
import java.math.BigDecimal;

// Used to return data formatted for the category spending pie chart
public class ChartDataDTO {

    // Option 1: Parallel lists (Common for libraries like Chart.js)
    private List<String> labels; // Category names
    private List<BigDecimal> values; // Corresponding spending amounts

    // Option 2: Map (Less common for direct chart lib consumption, but possible)
    // private Map<String, BigDecimal> categorySpending;

    // Constructors (using Option 1)
    public ChartDataDTO() {
    }

    public ChartDataDTO(List<String> labels, List<BigDecimal> values) {
        this.labels = labels;
        this.values = values;
    }

    // Getters and Setters (for Option 1)
    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<BigDecimal> getValues() {
        return values;
    }

    public void setValues(List<BigDecimal> values) {
        this.values = values;
    }

    // toString() - Optional
    @Override
    public String toString() {
        return "ChartDataDTO{" +
                "labels=" + labels +
                ", values=" + values +
                '}';
    }
}