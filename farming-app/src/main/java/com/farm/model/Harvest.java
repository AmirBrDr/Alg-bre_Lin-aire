package com.farm.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a harvest record from crops.
 */
public class Harvest {
    private final String id;
    private String cropId;
    private LocalDate harvestDate;
    private double quantity;
    private String unit;
    private QualityGrade quality;
    private double marketValue;
    private String storageLocation;
    private String notes;

    public enum QualityGrade {
        PREMIUM, GRADE_A, GRADE_B, GRADE_C, REJECTED
    }

    public Harvest(String cropId, double quantity, String unit) {
        this.id = UUID.randomUUID().toString();
        this.cropId = cropId;
        this.quantity = quantity;
        this.unit = unit;
        this.harvestDate = LocalDate.now();
        this.quality = QualityGrade.GRADE_A;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public LocalDate getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(LocalDate harvestDate) {
        this.harvestDate = harvestDate;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public QualityGrade getQuality() {
        return quality;
    }

    public void setQuality(QualityGrade quality) {
        this.quality = quality;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("Harvest{id='%s', cropId='%s', quantity=%.2f %s, quality=%s, date=%s}",
                id.substring(0, 8), cropId.substring(0, 8), quantity, unit, quality, harvestDate);
    }
}
