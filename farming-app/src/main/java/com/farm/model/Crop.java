package com.farm.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a crop planted in the farm.
 */
public class Crop {
    private final String id;
    private String name;
    private String variety;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private double areaInAcres;
    private CropStatus status;
    private String fieldId;
    private double expectedYieldPerAcre;
    private String notes;

    public enum CropStatus {
        PLANTED, GROWING, READY_FOR_HARVEST, HARVESTED, FAILED
    }

    public Crop(String name, String variety, double areaInAcres) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.variety = variety;
        this.areaInAcres = areaInAcres;
        this.status = CropStatus.PLANTED;
        this.plantingDate = LocalDate.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }

    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public LocalDate getExpectedHarvestDate() {
        return expectedHarvestDate;
    }

    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) {
        this.expectedHarvestDate = expectedHarvestDate;
    }

    public double getAreaInAcres() {
        return areaInAcres;
    }

    public void setAreaInAcres(double areaInAcres) {
        this.areaInAcres = areaInAcres;
    }

    public CropStatus getStatus() {
        return status;
    }

    public void setStatus(CropStatus status) {
        this.status = status;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public double getExpectedYieldPerAcre() {
        return expectedYieldPerAcre;
    }

    public void setExpectedYieldPerAcre(double expectedYieldPerAcre) {
        this.expectedYieldPerAcre = expectedYieldPerAcre;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getExpectedTotalYield() {
        return expectedYieldPerAcre * areaInAcres;
    }

    @Override
    public String toString() {
        return String.format("Crop{id='%s', name='%s', variety='%s', area=%.2f acres, status=%s, planted=%s}",
                id.substring(0, 8), name, variety, areaInAcres, status, plantingDate);
    }
}
