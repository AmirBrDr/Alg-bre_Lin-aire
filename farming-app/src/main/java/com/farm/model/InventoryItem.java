package com.farm.model;

import java.util.UUID;

/**
 * Represents inventory items such as seeds, feed, equipment, fertilizers, etc.
 */
public class InventoryItem {
    private final String id;
    private String name;
    private ItemCategory category;
    private double quantity;
    private String unit;
    private double unitCost;
    private double minimumStock;
    private String supplier;
    private String storageLocation;
    private String notes;

    public enum ItemCategory {
        SEEDS, FERTILIZER, PESTICIDE, FEED, EQUIPMENT, FUEL, MEDICINE, OTHER
    }

    public InventoryItem(String name, ItemCategory category, double quantity, String unit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
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

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(double amount) {
        this.quantity += amount;
    }

    public boolean removeQuantity(double amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
            return true;
        }
        return false;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(double minimumStock) {
        this.minimumStock = minimumStock;
    }

    public boolean isLowStock() {
        return quantity <= minimumStock;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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

    public double getTotalValue() {
        return quantity * unitCost;
    }

    @Override
    public String toString() {
        return String.format("InventoryItem{id='%s', name='%s', category=%s, quantity=%.2f %s%s}",
                id.substring(0, 8), name, category, quantity, unit, isLowStock() ? " [LOW STOCK]" : "");
    }
}
