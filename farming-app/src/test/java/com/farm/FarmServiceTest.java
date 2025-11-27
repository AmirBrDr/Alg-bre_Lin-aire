package com.farm;

import com.farm.model.*;
import com.farm.service.FarmService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Farming Management Application.
 */
public class FarmServiceTest {
    
    private FarmService farmService;
    
    @Before
    public void setUp() {
        farmService = new FarmService();
    }
    
    // ==================== FARM TESTS ====================
    
    @Test
    public void testSetAndGetFarm() {
        Farm farm = new Farm("Green Valley Farm", "John Doe", 100.0);
        farmService.setFarm(farm);
        
        Farm retrieved = farmService.getFarm();
        assertNotNull(retrieved);
        assertEquals("Green Valley Farm", retrieved.getName());
        assertEquals("John Doe", retrieved.getOwnerName());
        assertEquals(100.0, retrieved.getTotalAcreage(), 0.01);
    }
    
    // ==================== CROP TESTS ====================
    
    @Test
    public void testAddCrop() {
        Crop crop = new Crop("Wheat", "Winter Wheat", 10.0);
        Crop added = farmService.addCrop(crop);
        
        assertNotNull(added);
        assertEquals("Wheat", added.getName());
        assertEquals("Winter Wheat", added.getVariety());
        assertEquals(10.0, added.getAreaInAcres(), 0.01);
        assertEquals(Crop.CropStatus.PLANTED, added.getStatus());
    }
    
    @Test
    public void testGetAllCrops() {
        farmService.addCrop(new Crop("Wheat", "Winter Wheat", 10.0));
        farmService.addCrop(new Crop("Corn", "Sweet Corn", 15.0));
        
        assertEquals(2, farmService.getAllCrops().size());
    }
    
    @Test
    public void testUpdateCropStatus() {
        Crop crop = farmService.addCrop(new Crop("Wheat", "Winter Wheat", 10.0));
        
        farmService.updateCropStatus(crop.getId(), Crop.CropStatus.GROWING);
        Crop updated = farmService.getCrop(crop.getId());
        
        assertEquals(Crop.CropStatus.GROWING, updated.getStatus());
    }
    
    @Test
    public void testRemoveCrop() {
        Crop crop = farmService.addCrop(new Crop("Wheat", "Winter Wheat", 10.0));
        assertEquals(1, farmService.getAllCrops().size());
        
        boolean removed = farmService.removeCrop(crop.getId());
        assertTrue(removed);
        assertEquals(0, farmService.getAllCrops().size());
    }
    
    @Test
    public void testGetCropsByStatus() {
        Crop crop1 = farmService.addCrop(new Crop("Wheat", "Winter Wheat", 10.0));
        Crop crop2 = farmService.addCrop(new Crop("Corn", "Sweet Corn", 15.0));
        
        farmService.updateCropStatus(crop1.getId(), Crop.CropStatus.GROWING);
        
        assertEquals(1, farmService.getCropsByStatus(Crop.CropStatus.PLANTED).size());
        assertEquals(1, farmService.getCropsByStatus(Crop.CropStatus.GROWING).size());
    }
    
    // ==================== LIVESTOCK TESTS ====================
    
    @Test
    public void testAddLivestock() {
        Livestock animal = new Livestock("Bessie", Livestock.AnimalType.CATTLE, "Holstein", Livestock.Gender.FEMALE);
        Livestock added = farmService.addLivestock(animal);
        
        assertNotNull(added);
        assertEquals("Bessie", added.getName());
        assertEquals(Livestock.AnimalType.CATTLE, added.getType());
        assertEquals(Livestock.HealthStatus.HEALTHY, added.getHealthStatus());
    }
    
    @Test
    public void testGetLivestockByType() {
        farmService.addLivestock(new Livestock("Bessie", Livestock.AnimalType.CATTLE, "Holstein", Livestock.Gender.FEMALE));
        farmService.addLivestock(new Livestock("Daisy", Livestock.AnimalType.CATTLE, "Jersey", Livestock.Gender.FEMALE));
        farmService.addLivestock(new Livestock("Porky", Livestock.AnimalType.PIG, "Yorkshire", Livestock.Gender.MALE));
        
        assertEquals(2, farmService.getLivestockByType(Livestock.AnimalType.CATTLE).size());
        assertEquals(1, farmService.getLivestockByType(Livestock.AnimalType.PIG).size());
    }
    
    @Test
    public void testUpdateLivestockHealth() {
        Livestock animal = farmService.addLivestock(new Livestock("Bessie", Livestock.AnimalType.CATTLE, "Holstein", Livestock.Gender.FEMALE));
        
        farmService.updateLivestockHealth(animal.getId(), Livestock.HealthStatus.SICK);
        Livestock updated = farmService.getLivestock(animal.getId());
        
        assertEquals(Livestock.HealthStatus.SICK, updated.getHealthStatus());
    }
    
    // ==================== FIELD TESTS ====================
    
    @Test
    public void testAddField() {
        Field field = new Field("North Field", 25.0, Field.SoilType.LOAMY);
        Field added = farmService.addField(field);
        
        assertNotNull(added);
        assertEquals("North Field", added.getName());
        assertEquals(25.0, added.getSizeInAcres(), 0.01);
        assertEquals(Field.FieldStatus.AVAILABLE, added.getStatus());
    }
    
    @Test
    public void testGetAvailableFields() {
        Field field1 = farmService.addField(new Field("North Field", 25.0, Field.SoilType.LOAMY));
        Field field2 = farmService.addField(new Field("South Field", 30.0, Field.SoilType.CLAY));
        
        Crop crop = farmService.addCrop(new Crop("Wheat", "Winter Wheat", 10.0));
        farmService.plantCropInField(field1.getId(), crop.getId());
        
        assertEquals(1, farmService.getAvailableFields().size());
        assertEquals("South Field", farmService.getAvailableFields().get(0).getName());
    }
    
    @Test
    public void testPlantCropInField() {
        Field field = farmService.addField(new Field("North Field", 25.0, Field.SoilType.LOAMY));
        Crop crop = farmService.addCrop(new Crop("Wheat", "Winter Wheat", 10.0));
        
        farmService.plantCropInField(field.getId(), crop.getId());
        
        Field updatedField = farmService.getField(field.getId());
        Crop updatedCrop = farmService.getCrop(crop.getId());
        
        assertEquals(Field.FieldStatus.PLANTED, updatedField.getStatus());
        assertEquals(field.getId(), updatedCrop.getFieldId());
    }
    
    // ==================== INVENTORY TESTS ====================
    
    @Test
    public void testAddInventoryItem() {
        InventoryItem item = new InventoryItem("Wheat Seeds", InventoryItem.ItemCategory.SEEDS, 100.0, "kg");
        InventoryItem added = farmService.addInventoryItem(item);
        
        assertNotNull(added);
        assertEquals("Wheat Seeds", added.getName());
        assertEquals(100.0, added.getQuantity(), 0.01);
    }
    
    @Test
    public void testLowStockDetection() {
        InventoryItem item = new InventoryItem("Fertilizer", InventoryItem.ItemCategory.FERTILIZER, 10.0, "bags");
        item.setMinimumStock(20.0);
        farmService.addInventoryItem(item);
        
        assertEquals(1, farmService.getLowStockItems().size());
    }
    
    @Test
    public void testInventoryQuantityUpdate() {
        InventoryItem item = new InventoryItem("Feed", InventoryItem.ItemCategory.FEED, 50.0, "kg");
        farmService.addInventoryItem(item);
        
        item.addQuantity(25.0);
        assertEquals(75.0, item.getQuantity(), 0.01);
        
        boolean removed = item.removeQuantity(30.0);
        assertTrue(removed);
        assertEquals(45.0, item.getQuantity(), 0.01);
        
        // Try to remove more than available
        boolean failedRemove = item.removeQuantity(100.0);
        assertFalse(failedRemove);
        assertEquals(45.0, item.getQuantity(), 0.01);
    }
    
    // ==================== HARVEST TESTS ====================
    
    @Test
    public void testRecordHarvest() {
        Crop crop = farmService.addCrop(new Crop("Wheat", "Winter Wheat", 10.0));
        crop.setStatus(Crop.CropStatus.READY_FOR_HARVEST);
        
        Harvest harvest = new Harvest(crop.getId(), 500.0, "bushels");
        farmService.addHarvest(harvest);
        
        assertEquals(1, farmService.getAllHarvests().size());
        
        // Check crop status was updated
        Crop updatedCrop = farmService.getCrop(crop.getId());
        assertEquals(Crop.CropStatus.HARVESTED, updatedCrop.getStatus());
    }
    
    // ==================== STATISTICS TESTS ====================
    
    @Test
    public void testStatistics() {
        farmService.addCrop(new Crop("Wheat", "Winter Wheat", 10.0));
        farmService.addLivestock(new Livestock("Bessie", Livestock.AnimalType.CATTLE, "Holstein", Livestock.Gender.FEMALE));
        farmService.addField(new Field("North Field", 25.0, Field.SoilType.LOAMY));
        
        FarmService.FarmStatistics stats = farmService.getStatistics();
        
        assertEquals(1, stats.totalCrops);
        assertEquals(1, stats.totalLivestock);
        assertEquals(1, stats.totalFields);
        assertEquals(25.0, stats.totalFieldAcreage, 0.01);
    }
}
