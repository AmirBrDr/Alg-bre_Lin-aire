package com.farm.ui.gui;

import com.farm.model.Crop;
import com.farm.model.Harvest;
import com.farm.service.FarmService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing harvest records.
 */
public class HarvestPanel extends JPanel {
    private final FarmService farmService;
    private final Runnable refreshCallback;
    private JTable harvestTable;
    private DefaultTableModel tableModel;

    public HarvestPanel(FarmService farmService, Runnable refreshCallback) {
        this.farmService = farmService;
        this.refreshCallback = refreshCallback;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        createComponents();
    }

    private void createComponents() {
        // Title and buttons panel
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("🌻 Harvest Records");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(255, 152, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("➕ Record Harvest");
        JButton viewBtn = new JButton("👁️ View Details");

        addBtn.addActionListener(e -> showAddDialog());
        viewBtn.addActionListener(e -> viewDetails());

        buttonPanel.add(addBtn);
        buttonPanel.add(viewBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Crop", "Harvest Date", "Quantity", "Unit", "Quality Grade", "Storage Location"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        harvestTable = new JTable(tableModel);
        harvestTable.setFont(new Font("Arial", Font.PLAIN, 13));
        harvestTable.setRowHeight(30);
        harvestTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        harvestTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(harvestTable);
        add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(new Color(255, 243, 224));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 152, 0)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        List<Harvest> harvests = farmService.getAllHarvests();
        long premium = harvests.stream().filter(h -> h.getQuality() == Harvest.QualityGrade.PREMIUM).count();
        long gradeA = harvests.stream().filter(h -> h.getQuality() == Harvest.QualityGrade.GRADE_A).count();
        
        // Count crops ready for harvest
        long readyForHarvest = farmService.getCropsByStatus(Crop.CropStatus.READY_FOR_HARVEST).size();

        panel.add(new JLabel(String.format("📊 Total Harvests: %d | ", harvests.size())));
        panel.add(new JLabel(String.format("⭐ Premium: %d | ", premium)));
        panel.add(new JLabel(String.format("🅰️ Grade A: %d | ", gradeA)));
        
        if (readyForHarvest > 0) {
            JLabel readyLabel = new JLabel(String.format("🌻 Ready for harvest: %d crops", readyForHarvest));
            readyLabel.setForeground(new Color(255, 152, 0));
            readyLabel.setFont(readyLabel.getFont().deriveFont(Font.BOLD));
            panel.add(readyLabel);
        }

        return panel;
    }

    private void showAddDialog() {
        List<Crop> crops = farmService.getCropsByStatus(Crop.CropStatus.READY_FOR_HARVEST);
        crops.addAll(farmService.getCropsByStatus(Crop.CropStatus.GROWING));

        if (crops.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No crops available for harvest.\nAdd crops and mark them as 'Growing' or 'Ready for Harvest' first.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<CropItem> cropCombo = new JComboBox<>();
        for (Crop crop : crops) {
            cropCombo.addItem(new CropItem(crop));
        }
        
        JTextField quantityField = new JTextField();
        JTextField unitField = new JTextField("kg");
        JComboBox<Harvest.QualityGrade> gradeCombo = new JComboBox<>(Harvest.QualityGrade.values());
        JTextField storageField = new JTextField();

        panel.add(new JLabel("Select Crop:"));
        panel.add(cropCombo);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);
        panel.add(new JLabel("Quality Grade:"));
        panel.add(gradeCombo);
        panel.add(new JLabel("Storage Location:"));
        panel.add(storageField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Record Harvest",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                CropItem selectedItem = (CropItem) cropCombo.getSelectedItem();
                if (selectedItem == null) return;
                
                double quantity = Double.parseDouble(quantityField.getText().trim());
                String unit = unitField.getText().trim();

                if (!unit.isEmpty()) {
                    Harvest harvest = new Harvest(selectedItem.crop.getId(), quantity, unit);
                    harvest.setQuality((Harvest.QualityGrade) gradeCombo.getSelectedItem());
                    harvest.setStorageLocation(storageField.getText().trim());
                    farmService.addHarvest(harvest);
                    refresh();
                    refreshCallback.run();
                    
                    JOptionPane.showMessageDialog(this,
                        String.format("Harvest recorded successfully!\n%.2f %s of %s harvested.",
                            quantity, unit, selectedItem.crop.getName()),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity value", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewDetails() {
        int row = harvestTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a harvest record to view", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Harvest harvest = farmService.getHarvest(id);
        if (harvest == null) return;

        Crop crop = farmService.getCrop(harvest.getCropId());
        String cropName = crop != null ? crop.getName() + " (" + crop.getVariety() + ")" : "Unknown";

        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════════\n");
        details.append("              HARVEST DETAILS              \n");
        details.append("═══════════════════════════════════════\n\n");
        details.append("Crop: ").append(cropName).append("\n");
        details.append("Harvest Date: ").append(harvest.getHarvestDate()).append("\n");
        details.append("Quantity: ").append(String.format("%.2f", harvest.getQuantity())).append(" ").append(harvest.getUnit()).append("\n");
        details.append("Quality Grade: ").append(harvest.getQuality()).append("\n");
        details.append("Storage Location: ").append(harvest.getStorageLocation() != null ? harvest.getStorageLocation() : "Not specified").append("\n");
        
        if (harvest.getMarketValue() > 0) {
            details.append("Market Value: $").append(String.format("%.2f", harvest.getMarketValue())).append("\n");
        }
        
        if (harvest.getNotes() != null && !harvest.getNotes().isEmpty()) {
            details.append("Notes: ").append(harvest.getNotes()).append("\n");
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Harvest Details", JOptionPane.PLAIN_MESSAGE);
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Harvest harvest : farmService.getAllHarvests()) {
            Crop crop = farmService.getCrop(harvest.getCropId());
            String cropName = crop != null ? crop.getName() : "Unknown";
            
            tableModel.addRow(new Object[]{
                harvest.getId(),
                cropName,
                harvest.getHarvestDate().toString(),
                String.format("%.2f", harvest.getQuantity()),
                harvest.getUnit(),
                harvest.getQuality().toString(),
                harvest.getStorageLocation() != null ? harvest.getStorageLocation() : ""
            });
        }

        // Update summary
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JPanel && i == components.length - 1) {
                remove(components[i]);
                break;
            }
        }
        add(createSummaryPanel(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    // Helper class for crop combo box display
    private static class CropItem {
        final Crop crop;

        CropItem(Crop crop) {
            this.crop = crop;
        }

        @Override
        public String toString() {
            return crop.getName() + " - " + crop.getVariety() + " (" + crop.getStatus() + ")";
        }
    }
}
