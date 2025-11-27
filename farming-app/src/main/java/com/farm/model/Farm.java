package com.farm.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents the main farm entity containing all farm information.
 */
public class Farm {
    private final String id;
    private String name;
    private String ownerName;
    private String address;
    private double totalAcreage;
    private LocalDate establishedDate;
    private String contactPhone;
    private String contactEmail;
    private String notes;

    public Farm(String name, String ownerName, double totalAcreage) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.ownerName = ownerName;
        this.totalAcreage = totalAcreage;
        this.establishedDate = LocalDate.now();
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalAcreage() {
        return totalAcreage;
    }

    public void setTotalAcreage(double totalAcreage) {
        this.totalAcreage = totalAcreage;
    }

    public LocalDate getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(LocalDate establishedDate) {
        this.establishedDate = establishedDate;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("Farm{id='%s', name='%s', owner='%s', acreage=%.2f, established=%s}",
                id.substring(0, 8), name, ownerName, totalAcreage, establishedDate);
    }
}
