package com.farm.service;

import com.farm.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing all farm operations.
 * Acts as an in-memory data store and business logic handler.
 */
public class FarmService {
    private Farm farm;
    private final Map<String, Crop> crops;
    private final Map<String, Livestock> livestock;
    private final Map<String, Field> fields;
    private final Map<String, Harvest> harvests;
    private final Map<String, InventoryItem> inventory;

    public FarmService() {
        this.crops = new HashMap<>();
        this.livestock = new HashMap<>();
        this.fields = new HashMap<>();
        this.harvests = new HashMap<>();
        this.inventory = new HashMap<>();
    }

    // Farm Management
    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public Farm getFarm() {
        return farm;
    }

    // Crop Management
    public Crop addCrop(Crop crop) {
        crops.put(crop.getId(), crop);
        return crop;
    }

    public Crop getCrop(String id) {
        return crops.get(id);
    }

    public List<Crop> getAllCrops() {
        return new ArrayList<>(crops.values());
    }

    public List<Crop> getCropsByStatus(Crop.CropStatus status) {
        return crops.values().stream()
                .filter(c -> c.getStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean removeCrop(String id) {
        return crops.remove(id) != null;
    }

    public Crop updateCropStatus(String id, Crop.CropStatus status) {
        Crop crop = crops.get(id);
        if (crop != null) {
            crop.setStatus(status);
        }
        return crop;
    }

    // Livestock Management
    public Livestock addLivestock(Livestock animal) {
        livestock.put(animal.getId(), animal);
        return animal;
    }

    public Livestock getLivestock(String id) {
        return livestock.get(id);
    }

    public List<Livestock> getAllLivestock() {
        return new ArrayList<>(livestock.values());
    }

    public List<Livestock> getLivestockByType(Livestock.AnimalType type) {
        return livestock.values().stream()
                .filter(l -> l.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Livestock> getLivestockByHealthStatus(Livestock.HealthStatus status) {
        return livestock.values().stream()
                .filter(l -> l.getHealthStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean removeLivestock(String id) {
        return livestock.remove(id) != null;
    }

    public Livestock updateLivestockHealth(String id, Livestock.HealthStatus status) {
        Livestock animal = livestock.get(id);
        if (animal != null) {
            animal.setHealthStatus(status);
        }
        return animal;
    }

    // Field Management
    public Field addField(Field field) {
        fields.put(field.getId(), field);
        return field;
    }

    public Field getField(String id) {
        return fields.get(id);
    }

    public List<Field> getAllFields() {
        return new ArrayList<>(fields.values());
    }

    public List<Field> getAvailableFields() {
        return fields.values().stream()
                .filter(f -> f.getStatus() == Field.FieldStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    public boolean removeField(String id) {
        return fields.remove(id) != null;
    }

    public void plantCropInField(String fieldId, String cropId) {
        Field field = fields.get(fieldId);
        Crop crop = crops.get(cropId);
        if (field != null && crop != null) {
            field.addCrop(cropId);
            crop.setFieldId(fieldId);
        }
    }

    // Harvest Management
    public Harvest addHarvest(Harvest harvest) {
        harvests.put(harvest.getId(), harvest);
        // Update crop status
        Crop crop = crops.get(harvest.getCropId());
        if (crop != null) {
            crop.setStatus(Crop.CropStatus.HARVESTED);
        }
        return harvest;
    }

    public Harvest getHarvest(String id) {
        return harvests.get(id);
    }

    public List<Harvest> getAllHarvests() {
        return new ArrayList<>(harvests.values());
    }

    public List<Harvest> getHarvestsByCrop(String cropId) {
        return harvests.values().stream()
                .filter(h -> h.getCropId().equals(cropId))
                .collect(Collectors.toList());
    }

    // Inventory Management
    public InventoryItem addInventoryItem(InventoryItem item) {
        inventory.put(item.getId(), item);
        return item;
    }

    public InventoryItem getInventoryItem(String id) {
        return inventory.get(id);
    }

    public List<InventoryItem> getAllInventory() {
        return new ArrayList<>(inventory.values());
    }

    public List<InventoryItem> getInventoryByCategory(InventoryItem.ItemCategory category) {
        return inventory.values().stream()
                .filter(i -> i.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<InventoryItem> getLowStockItems() {
        return inventory.values().stream()
                .filter(InventoryItem::isLowStock)
                .collect(Collectors.toList());
    }

    public boolean removeInventoryItem(String id) {
        return inventory.remove(id) != null;
    }

    // Statistics
    public FarmStatistics getStatistics() {
        return new FarmStatistics(
                crops.size(),
                livestock.size(),
                fields.size(),
                harvests.size(),
                inventory.size(),
                fields.values().stream().mapToDouble(Field::getSizeInAcres).sum(),
                crops.values().stream()
                        .filter(c -> c.getStatus() == Crop.CropStatus.GROWING || c.getStatus() == Crop.CropStatus.PLANTED)
                        .mapToDouble(Crop::getAreaInAcres).sum(),
                getLowStockItems().size()
        );
    }

    /**
     * Data class for farm statistics.
     */
    public static class FarmStatistics {
        public final int totalCrops;
        public final int totalLivestock;
        public final int totalFields;
        public final int totalHarvests;
        public final int totalInventoryItems;
        public final double totalFieldAcreage;
        public final double activelyPlantedAcreage;
        public final int lowStockItemCount;

        public FarmStatistics(int totalCrops, int totalLivestock, int totalFields,
                              int totalHarvests, int totalInventoryItems,
                              double totalFieldAcreage, double activelyPlantedAcreage,
                              int lowStockItemCount) {
            this.totalCrops = totalCrops;
            this.totalLivestock = totalLivestock;
            this.totalFields = totalFields;
            this.totalHarvests = totalHarvests;
            this.totalInventoryItems = totalInventoryItems;
            this.totalFieldAcreage = totalFieldAcreage;
            this.activelyPlantedAcreage = activelyPlantedAcreage;
            this.lowStockItemCount = lowStockItemCount;
        }

        @Override
        public String toString() {
            return String.format(
                    "Farm Statistics:\n" +
                    "  Total Crops: %d\n" +
                    "  Total Livestock: %d\n" +
                    "  Total Fields: %d (%.2f acres)\n" +
                    "  Actively Planted: %.2f acres\n" +
                    "  Total Harvests: %d\n" +
                    "  Inventory Items: %d (%d low stock)",
                    totalCrops, totalLivestock, totalFields, totalFieldAcreage,
                    activelyPlantedAcreage, totalHarvests, totalInventoryItems, lowStockItemCount
            );
        }
    }
}
