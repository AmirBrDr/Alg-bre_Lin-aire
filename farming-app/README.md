# 🌾 Farming Management Application

A comprehensive Java application for managing farm operations including crops, livestock, fields, inventory, and harvest records.

## Features

### 📊 Dashboard
- Overview of all farm statistics
- Real-time alerts for low stock items and sick animals
- Quick action buttons for common tasks
- Summary of crops ready for harvest

### 🌱 Crop Management
- Add, edit, and delete crops
- Track crop status (Planted, Growing, Ready for Harvest, Harvested, Failed)
- Record planting dates and expected yields
- Assign crops to specific fields

### 🐄 Livestock Management
- Track all farm animals
- Support for multiple animal types (Cattle, Pigs, Sheep, Goats, Chickens, etc.)
- Health status monitoring (Healthy, Sick, Recovering, Quarantined)
- Record weight and location information

### 🌾 Field Management
- Define and manage farm fields
- Track field size, soil type, and status
- Plant crops in specific fields
- Monitor field availability

### 📦 Inventory Management
- Track seeds, fertilizers, pesticides, feed, equipment, and more
- Low stock alerts
- Quantity adjustment tracking
- Supplier information

### 🌻 Harvest Records
- Record harvest quantities and quality grades
- Link harvests to specific crops
- Track storage locations
- View detailed harvest reports

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building the Application

```bash
cd farming-app
mvn clean package
```

## Running the Application

### GUI Mode (Default)
```bash
java -jar target/farming-app-1.0.0.jar
```

Or with Maven:
```bash
mvn exec:java -Dexec.mainClass="com.farm.FarmingApp"
```

### Console Mode
```bash
java -jar target/farming-app-1.0.0.jar --console
```

Or:
```bash
java -jar target/farming-app-1.0.0.jar -c
```

## Running Tests

```bash
mvn test
```

## Project Structure

```
farming-app/
├── pom.xml
├── src/
│   ├── main/java/com/farm/
│   │   ├── FarmingApp.java          # Main entry point
│   │   ├── model/                   # Domain models
│   │   │   ├── Farm.java
│   │   │   ├── Crop.java
│   │   │   ├── Livestock.java
│   │   │   ├── Field.java
│   │   │   ├── Harvest.java
│   │   │   └── InventoryItem.java
│   │   ├── service/                 # Business logic
│   │   │   └── FarmService.java
│   │   └── ui/                      # User interfaces
│   │       ├── ConsoleUI.java       # Command-line interface
│   │       └── gui/                 # Graphical interface
│   │           ├── MainFrame.java
│   │           ├── DashboardPanel.java
│   │           ├── CropPanel.java
│   │           ├── LivestockPanel.java
│   │           ├── FieldPanel.java
│   │           ├── InventoryPanel.java
│   │           └── HarvestPanel.java
│   └── test/java/com/farm/
│       └── FarmServiceTest.java     # Unit tests
└── README.md
```

## Screenshots

The application features a modern, intuitive GUI with:
- Tabbed navigation for different management areas
- Color-coded status indicators
- Quick action buttons
- Real-time statistics and alerts

## License

This project is open source and available for educational purposes.

## Contributing

Feel free to submit issues and pull requests to improve the application.
