package com.farm.ui;

import com.farm.model.*;
import com.farm.service.FarmService;

import java.util.List;
import java.util.Scanner;

/**
 * Command-line interface for the farming management application.
 */
public class ConsoleUI {
    private final FarmService farmService;
    private final Scanner scanner;
    private boolean running;

    public ConsoleUI(FarmService farmService) {
        this.farmService = farmService;
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        printWelcome();
        setupFarm();

        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");
            handleMainMenuChoice(choice);
        }

        System.out.println("\nThank you for using the Farming Management App. Goodbye!");
        scanner.close();
    }

    private void printWelcome() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         FARMING MANAGEMENT APPLICATION v1.0                ║");
        System.out.println("║         Manage your farm efficiently!                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void setupFarm() {
        if (farmService.getFarm() == null) {
            System.out.println("Let's set up your farm first!\n");
            String farmName = readString("Enter farm name: ");
            String ownerName = readString("Enter owner name: ");
            double acreage = readDouble("Enter total farm acreage: ");

            Farm farm = new Farm(farmName, ownerName, acreage);
            farmService.setFarm(farm);
            System.out.println("\nFarm created successfully!");
            System.out.println(farm);
            System.out.println();
        }
    }

    private void printMainMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║            MAIN MENU                  ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  1. Crop Management                   ║");
        System.out.println("║  2. Livestock Management              ║");
        System.out.println("║  3. Field Management                  ║");
        System.out.println("║  4. Inventory Management              ║");
        System.out.println("║  5. Harvest Records                   ║");
        System.out.println("║  6. View Farm Statistics              ║");
        System.out.println("║  7. View Farm Info                    ║");
        System.out.println("║  0. Exit                              ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                cropMenu();
                break;
            case 2:
                livestockMenu();
                break;
            case 3:
                fieldMenu();
                break;
            case 4:
                inventoryMenu();
                break;
            case 5:
                harvestMenu();
                break;
            case 6:
                viewStatistics();
                break;
            case 7:
                viewFarmInfo();
                break;
            case 0:
                running = false;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // ==================== CROP MANAGEMENT ====================
    private void cropMenu() {
        while (true) {
            System.out.println("\n--- CROP MANAGEMENT ---");
            System.out.println("1. Add New Crop");
            System.out.println("2. View All Crops");
            System.out.println("3. Update Crop Status");
            System.out.println("4. Remove Crop");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    addCrop();
                    break;
                case 2:
                    viewAllCrops();
                    break;
                case 3:
                    updateCropStatus();
                    break;
                case 4:
                    removeCrop();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addCrop() {
        System.out.println("\n--- ADD NEW CROP ---");
        String name = readString("Crop name: ");
        String variety = readString("Variety: ");
        double area = readDouble("Area (acres): ");

        Crop crop = new Crop(name, variety, area);

        System.out.println("Expected yield per acre (optional, press Enter to skip): ");
        String yieldInput = scanner.nextLine();
        if (!yieldInput.isEmpty()) {
            try {
                crop.setExpectedYieldPerAcre(Double.parseDouble(yieldInput));
            } catch (NumberFormatException ignored) {
            }
        }

        farmService.addCrop(crop);
        System.out.println("Crop added successfully!");
        System.out.println(crop);
    }

    private void viewAllCrops() {
        System.out.println("\n--- ALL CROPS ---");
        List<Crop> crops = farmService.getAllCrops();
        if (crops.isEmpty()) {
            System.out.println("No crops registered.");
        } else {
            for (int i = 0; i < crops.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, crops.get(i));
            }
        }
    }

    private void updateCropStatus() {
        viewAllCrops();
        List<Crop> crops = farmService.getAllCrops();
        if (crops.isEmpty()) return;

        int index = readInt("Select crop number to update: ") - 1;
        if (index < 0 || index >= crops.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.println("Status options: PLANTED, GROWING, READY_FOR_HARVEST, HARVESTED, FAILED");
        String statusStr = readString("Enter new status: ").toUpperCase();
        try {
            Crop.CropStatus status = Crop.CropStatus.valueOf(statusStr);
            farmService.updateCropStatus(crops.get(index).getId(), status);
            System.out.println("Status updated successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status.");
        }
    }

    private void removeCrop() {
        viewAllCrops();
        List<Crop> crops = farmService.getAllCrops();
        if (crops.isEmpty()) return;

        int index = readInt("Select crop number to remove: ") - 1;
        if (index < 0 || index >= crops.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        farmService.removeCrop(crops.get(index).getId());
        System.out.println("Crop removed successfully!");
    }

    // ==================== LIVESTOCK MANAGEMENT ====================
    private void livestockMenu() {
        while (true) {
            System.out.println("\n--- LIVESTOCK MANAGEMENT ---");
            System.out.println("1. Add New Animal");
            System.out.println("2. View All Livestock");
            System.out.println("3. View by Type");
            System.out.println("4. Update Health Status");
            System.out.println("5. Remove Animal");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    addLivestock();
                    break;
                case 2:
                    viewAllLivestock();
                    break;
                case 3:
                    viewLivestockByType();
                    break;
                case 4:
                    updateLivestockHealth();
                    break;
                case 5:
                    removeLivestock();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addLivestock() {
        System.out.println("\n--- ADD NEW ANIMAL ---");
        String name = readString("Animal name/tag: ");

        System.out.println("Animal types: CATTLE, PIG, SHEEP, GOAT, CHICKEN, DUCK, TURKEY, HORSE, OTHER");
        String typeStr = readString("Animal type: ").toUpperCase();
        Livestock.AnimalType type;
        try {
            type = Livestock.AnimalType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid type, defaulting to OTHER.");
            type = Livestock.AnimalType.OTHER;
        }

        String breed = readString("Breed: ");

        System.out.println("Gender: MALE, FEMALE");
        String genderStr = readString("Gender: ").toUpperCase();
        Livestock.Gender gender;
        try {
            gender = Livestock.Gender.valueOf(genderStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid gender, defaulting to FEMALE.");
            gender = Livestock.Gender.FEMALE;
        }

        Livestock animal = new Livestock(name, type, breed, gender);
        farmService.addLivestock(animal);
        System.out.println("Animal added successfully!");
        System.out.println(animal);
    }

    private void viewAllLivestock() {
        System.out.println("\n--- ALL LIVESTOCK ---");
        List<Livestock> animals = farmService.getAllLivestock();
        if (animals.isEmpty()) {
            System.out.println("No livestock registered.");
        } else {
            for (int i = 0; i < animals.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, animals.get(i));
            }
        }
    }

    private void viewLivestockByType() {
        System.out.println("Animal types: CATTLE, PIG, SHEEP, GOAT, CHICKEN, DUCK, TURKEY, HORSE, OTHER");
        String typeStr = readString("Enter type to filter: ").toUpperCase();
        try {
            Livestock.AnimalType type = Livestock.AnimalType.valueOf(typeStr);
            List<Livestock> filtered = farmService.getLivestockByType(type);
            System.out.printf("\n--- %s ---\n", type);
            if (filtered.isEmpty()) {
                System.out.println("No animals of this type.");
            } else {
                filtered.forEach(System.out::println);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid type.");
        }
    }

    private void updateLivestockHealth() {
        viewAllLivestock();
        List<Livestock> animals = farmService.getAllLivestock();
        if (animals.isEmpty()) return;

        int index = readInt("Select animal number to update: ") - 1;
        if (index < 0 || index >= animals.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        System.out.println("Health status: HEALTHY, SICK, RECOVERING, QUARANTINED");
        String statusStr = readString("Enter new health status: ").toUpperCase();
        try {
            Livestock.HealthStatus status = Livestock.HealthStatus.valueOf(statusStr);
            farmService.updateLivestockHealth(animals.get(index).getId(), status);
            System.out.println("Health status updated successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status.");
        }
    }

    private void removeLivestock() {
        viewAllLivestock();
        List<Livestock> animals = farmService.getAllLivestock();
        if (animals.isEmpty()) return;

        int index = readInt("Select animal number to remove: ") - 1;
        if (index < 0 || index >= animals.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        farmService.removeLivestock(animals.get(index).getId());
        System.out.println("Animal removed successfully!");
    }

    // ==================== FIELD MANAGEMENT ====================
    private void fieldMenu() {
        while (true) {
            System.out.println("\n--- FIELD MANAGEMENT ---");
            System.out.println("1. Add New Field");
            System.out.println("2. View All Fields");
            System.out.println("3. View Available Fields");
            System.out.println("4. Plant Crop in Field");
            System.out.println("5. Remove Field");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    addField();
                    break;
                case 2:
                    viewAllFields();
                    break;
                case 3:
                    viewAvailableFields();
                    break;
                case 4:
                    plantCropInField();
                    break;
                case 5:
                    removeField();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addField() {
        System.out.println("\n--- ADD NEW FIELD ---");
        String name = readString("Field name: ");
        double size = readDouble("Size (acres): ");

        System.out.println("Soil types: CLAY, SANDY, LOAMY, SILT, PEAT, CHALK");
        String soilStr = readString("Soil type: ").toUpperCase();
        Field.SoilType soilType;
        try {
            soilType = Field.SoilType.valueOf(soilStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid soil type, defaulting to LOAMY.");
            soilType = Field.SoilType.LOAMY;
        }

        Field field = new Field(name, size, soilType);
        farmService.addField(field);
        System.out.println("Field added successfully!");
        System.out.println(field);
    }

    private void viewAllFields() {
        System.out.println("\n--- ALL FIELDS ---");
        List<Field> fields = farmService.getAllFields();
        if (fields.isEmpty()) {
            System.out.println("No fields registered.");
        } else {
            for (int i = 0; i < fields.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, fields.get(i));
            }
        }
    }

    private void viewAvailableFields() {
        System.out.println("\n--- AVAILABLE FIELDS ---");
        List<Field> fields = farmService.getAvailableFields();
        if (fields.isEmpty()) {
            System.out.println("No available fields.");
        } else {
            fields.forEach(System.out::println);
        }
    }

    private void plantCropInField() {
        System.out.println("\n--- PLANT CROP IN FIELD ---");
        List<Field> fields = farmService.getAvailableFields();
        List<Crop> crops = farmService.getCropsByStatus(Crop.CropStatus.PLANTED);

        if (fields.isEmpty()) {
            System.out.println("No available fields.");
            return;
        }
        if (crops.isEmpty()) {
            System.out.println("No crops available for planting.");
            return;
        }

        System.out.println("Available Fields:");
        for (int i = 0; i < fields.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, fields.get(i));
        }
        int fieldIndex = readInt("Select field: ") - 1;

        System.out.println("Available Crops:");
        for (int i = 0; i < crops.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, crops.get(i));
        }
        int cropIndex = readInt("Select crop: ") - 1;

        if (fieldIndex >= 0 && fieldIndex < fields.size() &&
            cropIndex >= 0 && cropIndex < crops.size()) {
            farmService.plantCropInField(fields.get(fieldIndex).getId(), crops.get(cropIndex).getId());
            System.out.println("Crop planted in field successfully!");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private void removeField() {
        viewAllFields();
        List<Field> fields = farmService.getAllFields();
        if (fields.isEmpty()) return;

        int index = readInt("Select field number to remove: ") - 1;
        if (index < 0 || index >= fields.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        farmService.removeField(fields.get(index).getId());
        System.out.println("Field removed successfully!");
    }

    // ==================== INVENTORY MANAGEMENT ====================
    private void inventoryMenu() {
        while (true) {
            System.out.println("\n--- INVENTORY MANAGEMENT ---");
            System.out.println("1. Add New Item");
            System.out.println("2. View All Inventory");
            System.out.println("3. View Low Stock Items");
            System.out.println("4. Update Item Quantity");
            System.out.println("5. Remove Item");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    addInventoryItem();
                    break;
                case 2:
                    viewAllInventory();
                    break;
                case 3:
                    viewLowStockItems();
                    break;
                case 4:
                    updateItemQuantity();
                    break;
                case 5:
                    removeInventoryItem();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addInventoryItem() {
        System.out.println("\n--- ADD INVENTORY ITEM ---");
        String name = readString("Item name: ");

        System.out.println("Categories: SEEDS, FERTILIZER, PESTICIDE, FEED, EQUIPMENT, FUEL, MEDICINE, OTHER");
        String catStr = readString("Category: ").toUpperCase();
        InventoryItem.ItemCategory category;
        try {
            category = InventoryItem.ItemCategory.valueOf(catStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid category, defaulting to OTHER.");
            category = InventoryItem.ItemCategory.OTHER;
        }

        double quantity = readDouble("Quantity: ");
        String unit = readString("Unit (e.g., kg, liters, bags): ");

        InventoryItem item = new InventoryItem(name, category, quantity, unit);

        double minStock = readDouble("Minimum stock level (for low stock alert): ");
        item.setMinimumStock(minStock);

        farmService.addInventoryItem(item);
        System.out.println("Item added successfully!");
        System.out.println(item);
    }

    private void viewAllInventory() {
        System.out.println("\n--- ALL INVENTORY ---");
        List<InventoryItem> items = farmService.getAllInventory();
        if (items.isEmpty()) {
            System.out.println("No inventory items.");
        } else {
            for (int i = 0; i < items.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, items.get(i));
            }
        }
    }

    private void viewLowStockItems() {
        System.out.println("\n--- LOW STOCK ITEMS ---");
        List<InventoryItem> items = farmService.getLowStockItems();
        if (items.isEmpty()) {
            System.out.println("No low stock items. Everything is well stocked!");
        } else {
            items.forEach(System.out::println);
        }
    }

    private void updateItemQuantity() {
        viewAllInventory();
        List<InventoryItem> items = farmService.getAllInventory();
        if (items.isEmpty()) return;

        int index = readInt("Select item number to update: ") - 1;
        if (index < 0 || index >= items.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        InventoryItem item = items.get(index);
        System.out.printf("Current quantity: %.2f %s%n", item.getQuantity(), item.getUnit());
        double change = readDouble("Enter quantity to add (negative to remove): ");
        item.addQuantity(change);
        System.out.printf("New quantity: %.2f %s%n", item.getQuantity(), item.getUnit());
    }

    private void removeInventoryItem() {
        viewAllInventory();
        List<InventoryItem> items = farmService.getAllInventory();
        if (items.isEmpty()) return;

        int index = readInt("Select item number to remove: ") - 1;
        if (index < 0 || index >= items.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        farmService.removeInventoryItem(items.get(index).getId());
        System.out.println("Item removed successfully!");
    }

    // ==================== HARVEST MANAGEMENT ====================
    private void harvestMenu() {
        while (true) {
            System.out.println("\n--- HARVEST RECORDS ---");
            System.out.println("1. Record New Harvest");
            System.out.println("2. View All Harvests");
            System.out.println("0. Back to Main Menu");

            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    recordHarvest();
                    break;
                case 2:
                    viewAllHarvests();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void recordHarvest() {
        System.out.println("\n--- RECORD HARVEST ---");
        List<Crop> crops = farmService.getCropsByStatus(Crop.CropStatus.READY_FOR_HARVEST);
        crops.addAll(farmService.getCropsByStatus(Crop.CropStatus.GROWING));

        if (crops.isEmpty()) {
            System.out.println("No crops ready for harvest.");
            return;
        }

        System.out.println("Crops available for harvest:");
        for (int i = 0; i < crops.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, crops.get(i));
        }

        int index = readInt("Select crop: ") - 1;
        if (index < 0 || index >= crops.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        double quantity = readDouble("Harvest quantity: ");
        String unit = readString("Unit (e.g., kg, bushels, tons): ");

        Harvest harvest = new Harvest(crops.get(index).getId(), quantity, unit);

        System.out.println("Quality grades: PREMIUM, GRADE_A, GRADE_B, GRADE_C, REJECTED");
        String qualityStr = readString("Quality grade: ").toUpperCase();
        try {
            harvest.setQuality(Harvest.QualityGrade.valueOf(qualityStr));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid grade, defaulting to GRADE_A.");
        }

        farmService.addHarvest(harvest);
        System.out.println("Harvest recorded successfully!");
        System.out.println(harvest);
    }

    private void viewAllHarvests() {
        System.out.println("\n--- ALL HARVESTS ---");
        List<Harvest> harvests = farmService.getAllHarvests();
        if (harvests.isEmpty()) {
            System.out.println("No harvest records.");
        } else {
            harvests.forEach(System.out::println);
        }
    }

    // ==================== STATISTICS & INFO ====================
    private void viewStatistics() {
        System.out.println("\n" + farmService.getStatistics());
    }

    private void viewFarmInfo() {
        Farm farm = farmService.getFarm();
        if (farm != null) {
            System.out.println("\n--- FARM INFORMATION ---");
            System.out.println(farm);
        }
    }

    // ==================== HELPER METHODS ====================
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int value = Integer.parseInt(scanner.nextLine().trim());
            return value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
