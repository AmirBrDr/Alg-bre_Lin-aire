package com.farm.ui.gui;

import com.farm.service.FarmService;
import com.farm.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Dashboard panel showing farm overview and statistics.
 */
public class DashboardPanel extends JPanel {
    private final FarmService farmService;
    private JPanel statsPanel;
    private JPanel alertsPanel;

    public DashboardPanel(FarmService farmService) {
        this.farmService = farmService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        createComponents();
    }

    private void createComponents() {
        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 10));

        // Statistics panel
        statsPanel = createStatsPanel();
        contentPanel.add(statsPanel);

        // Alerts and quick info panel
        alertsPanel = createAlertsPanel();
        contentPanel.add(alertsPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Quick actions panel at bottom
        JPanel actionsPanel = createQuickActionsPanel();
        add(actionsPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 15, 15));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 125, 50), 2),
            "📊 Farm Statistics",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            new Color(46, 125, 50)
        ));
        panel.setBackground(Color.WHITE);

        FarmService.FarmStatistics stats = farmService.getStatistics();

        panel.add(createStatCard("🌱 Total Crops", String.valueOf(stats.totalCrops), new Color(76, 175, 80)));
        panel.add(createStatCard("🐄 Livestock", String.valueOf(stats.totalLivestock), new Color(139, 69, 19)));
        panel.add(createStatCard("🌾 Fields", String.format("%d (%.1f acres)", stats.totalFields, stats.totalFieldAcreage), new Color(255, 193, 7)));
        panel.add(createStatCard("📦 Inventory", String.valueOf(stats.totalInventoryItems), new Color(33, 150, 243)));
        panel.add(createStatCard("🌻 Harvests", String.valueOf(stats.totalHarvests), new Color(255, 152, 0)));
        panel.add(createStatCard("🌿 Planted Area", String.format("%.1f acres", stats.activelyPlantedAcreage), new Color(156, 204, 101)));
        
        Farm farm = farmService.getFarm();
        double totalAcreage = farm != null ? farm.getTotalAcreage() : 0;
        panel.add(createStatCard("🏡 Farm Size", String.format("%.1f acres", totalAcreage), new Color(121, 85, 72)));
        panel.add(createStatCard("⚠️ Low Stock Items", String.valueOf(stats.lowStockItemCount), 
            stats.lowStockItemCount > 0 ? new Color(244, 67, 54) : new Color(76, 175, 80)));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 152, 0), 2),
            "🔔 Alerts & Reminders",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            new Color(255, 152, 0)
        ));
        panel.setBackground(Color.WHITE);

        DefaultListModel<String> alertsModel = new DefaultListModel<>();

        // Check for low stock items
        for (InventoryItem item : farmService.getLowStockItems()) {
            alertsModel.addElement("⚠️ Low stock: " + item.getName() + 
                " (" + String.format("%.1f", item.getQuantity()) + " " + item.getUnit() + ")");
        }

        // Check for sick animals
        for (Livestock animal : farmService.getLivestockByHealthStatus(Livestock.HealthStatus.SICK)) {
            alertsModel.addElement("🏥 Sick animal: " + animal.getName() + " (" + animal.getType() + ")");
        }

        // Check for crops ready for harvest
        for (Crop crop : farmService.getCropsByStatus(Crop.CropStatus.READY_FOR_HARVEST)) {
            alertsModel.addElement("🌻 Ready for harvest: " + crop.getName() + " - " + crop.getVariety());
        }

        if (alertsModel.isEmpty()) {
            alertsModel.addElement("✅ No alerts - Everything is running smoothly!");
        }

        JList<String> alertsList = new JList<>(alertsModel);
        alertsList.setFont(new Font("Arial", Font.PLAIN, 14));
        alertsList.setBackground(Color.WHITE);
        alertsList.setCellRenderer(new AlertListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(alertsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Quick Actions"
        ));
        panel.setBackground(new Color(250, 250, 250));

        JButton addCropBtn = createActionButton("➕ Add Crop", new Color(76, 175, 80));
        JButton addAnimalBtn = createActionButton("➕ Add Animal", new Color(139, 69, 19));
        JButton addFieldBtn = createActionButton("➕ Add Field", new Color(255, 193, 7));
        JButton recordHarvestBtn = createActionButton("🌻 Record Harvest", new Color(255, 152, 0));

        addCropBtn.addActionListener(e -> showQuickAddCropDialog());
        addAnimalBtn.addActionListener(e -> showQuickAddAnimalDialog());
        addFieldBtn.addActionListener(e -> showQuickAddFieldDialog());
        recordHarvestBtn.addActionListener(e -> showQuickHarvestDialog());

        panel.add(addCropBtn);
        panel.add(addAnimalBtn);
        panel.add(addFieldBtn);
        panel.add(recordHarvestBtn);

        return panel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showQuickAddCropDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField varietyField = new JTextField();
        JTextField areaField = new JTextField();

        panel.add(new JLabel("Crop Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Variety:"));
        panel.add(varietyField);
        panel.add(new JLabel("Area (acres):"));
        panel.add(areaField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Crop",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String variety = varietyField.getText().trim();
                double area = Double.parseDouble(areaField.getText().trim());

                if (!name.isEmpty()) {
                    Crop crop = new Crop(name, variety, area);
                    farmService.addCrop(crop);
                    JOptionPane.showMessageDialog(this, "Crop added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid area value", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showQuickAddAnimalDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField nameField = new JTextField();
        JComboBox<Livestock.AnimalType> typeCombo = new JComboBox<>(Livestock.AnimalType.values());
        JTextField breedField = new JTextField();
        JComboBox<Livestock.Gender> genderCombo = new JComboBox<>(Livestock.Gender.values());

        panel.add(new JLabel("Animal Name/Tag:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Breed:"));
        panel.add(breedField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Animal",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                Livestock animal = new Livestock(name,
                    (Livestock.AnimalType) typeCombo.getSelectedItem(),
                    breedField.getText().trim(),
                    (Livestock.Gender) genderCombo.getSelectedItem());
                farmService.addLivestock(animal);
                JOptionPane.showMessageDialog(this, "Animal added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            }
        }
    }

    private void showQuickAddFieldDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField sizeField = new JTextField();
        JComboBox<Field.SoilType> soilCombo = new JComboBox<>(Field.SoilType.values());

        panel.add(new JLabel("Field Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Size (acres):"));
        panel.add(sizeField);
        panel.add(new JLabel("Soil Type:"));
        panel.add(soilCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Field",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double size = Double.parseDouble(sizeField.getText().trim());

                if (!name.isEmpty()) {
                    Field field = new Field(name, size, (Field.SoilType) soilCombo.getSelectedItem());
                    farmService.addField(field);
                    JOptionPane.showMessageDialog(this, "Field added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid size value", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showQuickHarvestDialog() {
        java.util.List<Crop> crops = farmService.getCropsByStatus(Crop.CropStatus.READY_FOR_HARVEST);
        crops.addAll(farmService.getCropsByStatus(Crop.CropStatus.GROWING));

        if (crops.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No crops available for harvest.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JComboBox<Crop> cropCombo = new JComboBox<>(crops.toArray(new Crop[0]));
        JTextField quantityField = new JTextField();
        JTextField unitField = new JTextField("kg");
        JComboBox<Harvest.QualityGrade> gradeCombo = new JComboBox<>(Harvest.QualityGrade.values());

        panel.add(new JLabel("Select Crop:"));
        panel.add(cropCombo);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);
        panel.add(new JLabel("Quality Grade:"));
        panel.add(gradeCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Record Harvest",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Crop selectedCrop = (Crop) cropCombo.getSelectedItem();
                double quantity = Double.parseDouble(quantityField.getText().trim());
                String unit = unitField.getText().trim();

                if (selectedCrop != null) {
                    Harvest harvest = new Harvest(selectedCrop.getId(), quantity, unit);
                    harvest.setQuality((Harvest.QualityGrade) gradeCombo.getSelectedItem());
                    farmService.addHarvest(harvest);
                    JOptionPane.showMessageDialog(this, "Harvest recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity value", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refresh() {
        removeAll();
        createComponents();
        revalidate();
        repaint();
    }

    // Custom cell renderer for alerts list
    private static class AlertListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            
            String text = value.toString();
            if (text.startsWith("⚠️") || text.startsWith("🏥")) {
                setForeground(new Color(211, 47, 47));
            } else if (text.startsWith("🌻")) {
                setForeground(new Color(255, 152, 0));
            } else {
                setForeground(new Color(76, 175, 80));
            }
            
            return c;
        }
    }
}
