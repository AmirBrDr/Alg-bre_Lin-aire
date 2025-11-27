package com.farm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a field or plot of land on the farm.
 */
public class Field {
    private final String id;
    private String name;
    private double sizeInAcres;
    private SoilType soilType;
    private FieldStatus status;
    private String location;
    private List<String> cropIds;
    private String notes;

    public enum SoilType {
        CLAY, SANDY, LOAMY, SILT, PEAT, CHALK
    }

    public enum FieldStatus {
        AVAILABLE, PLANTED, FALLOW, UNDER_PREPARATION
    }

    public Field(String name, double sizeInAcres, SoilType soilType) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.sizeInAcres = sizeInAcres;
        this.soilType = soilType;
        this.status = FieldStatus.AVAILABLE;
        this.cropIds = new ArrayList<>();
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

    public double getSizeInAcres() {
        return sizeInAcres;
    }

    public void setSizeInAcres(double sizeInAcres) {
        this.sizeInAcres = sizeInAcres;
    }

    public SoilType getSoilType() {
        return soilType;
    }

    public void setSoilType(SoilType soilType) {
        this.soilType = soilType;
    }

    public FieldStatus getStatus() {
        return status;
    }

    public void setStatus(FieldStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getCropIds() {
        return new ArrayList<>(cropIds);
    }

    public void addCrop(String cropId) {
        this.cropIds.add(cropId);
        this.status = FieldStatus.PLANTED;
    }

    public void removeCrop(String cropId) {
        this.cropIds.remove(cropId);
        if (cropIds.isEmpty()) {
            this.status = FieldStatus.AVAILABLE;
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("Field{id='%s', name='%s', size=%.2f acres, soil=%s, status=%s}",
                id.substring(0, 8), name, sizeInAcres, soilType, status);
    }
}
