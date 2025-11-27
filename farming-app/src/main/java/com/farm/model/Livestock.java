package com.farm.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents livestock (animals) on the farm.
 */
public class Livestock {
    private final String id;
    private String name;
    private AnimalType type;
    private String breed;
    private LocalDate birthDate;
    private LocalDate acquisitionDate;
    private Gender gender;
    private double weight;
    private HealthStatus healthStatus;
    private String location;
    private String notes;

    public enum AnimalType {
        CATTLE, PIG, SHEEP, GOAT, CHICKEN, DUCK, TURKEY, HORSE, OTHER
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum HealthStatus {
        HEALTHY, SICK, RECOVERING, QUARANTINED
    }

    public Livestock(String name, AnimalType type, String breed, Gender gender) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.gender = gender;
        this.healthStatus = HealthStatus.HEALTHY;
        this.acquisitionDate = LocalDate.now();
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

    public AnimalType getType() {
        return type;
    }

    public void setType(AnimalType type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return String.format("Livestock{id='%s', name='%s', type=%s, breed='%s', gender=%s, health=%s}",
                id.substring(0, 8), name, type, breed, gender, healthStatus);
    }
}
