package com.farm.ui.gui;

import com.farm.model.Crop;
import com.farm.model.Field;
import com.farm.service.FarmService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing fields.
 */
public class FieldPanel extends JPanel {
    private final FarmService farmService;
    private final Runnable refreshCallback;
    private JTable fieldTable;
    private DefaultTableModel tableModel;
    private JPanel summaryPanel;

    public FieldPanel(FarmService farmService, Runnable refreshCallback) {
        this.farmService = farmService;
        this.refreshCallback = refreshCallback;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        createComponents();
    }

    private void createComponents() {
        // Title and buttons panel
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("🌾 Field Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(255, 152, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("➕ Add Field");
        JButton editBtn = new JButton("✏️ Edit");
        JButton plantBtn = new JButton("🌱 Plant Crop");
        JButton deleteBtn = new JButton("🗑️ Delete");

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        plantBtn.addActionListener(e -> plantCropInField());
        deleteBtn.addActionListener(e -> deleteField());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(plantBtn);
        buttonPanel.add(deleteBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Size (acres)", "Soil Type", "Status", "Location", "Crops Planted"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        fieldTable = new JTable(tableModel);
        fieldTable.setFont(new Font("Arial", Font.PLAIN, 13));
        fieldTable.setRowHeight(30);
        fieldTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        fieldTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(fieldTable);
        add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(new Color(255, 249, 196));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 152, 0)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        List<Field> fields = farmService.getAllFields();
        double totalAcreage = fields.stream().mapToDouble(Field::getSizeInAcres).sum();
        long available = fields.stream().filter(f -> f.getStatus() == Field.FieldStatus.AVAILABLE).count();
        long planted = fields.stream().filter(f -> f.getStatus() == Field.FieldStatus.PLANTED).count();

        panel.add(new JLabel(String.format("📊 Total: %d fields | ", fields.size())));
        panel.add(new JLabel(String.format("📐 Total Area: %.2f acres | ", totalAcreage)));
        panel.add(new JLabel(String.format("✅ Available: %d | ", available)));
        panel.add(new JLabel(String.format("🌱 Planted: %d", planted)));

        return panel;
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField sizeField = new JTextField();
        JComboBox<Field.SoilType> soilCombo = new JComboBox<>(Field.SoilType.values());
        JTextField locationField = new JTextField();

        panel.add(new JLabel("Field Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Size (acres):"));
        panel.add(sizeField);
        panel.add(new JLabel("Soil Type:"));
        panel.add(soilCombo);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Field",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double size = Double.parseDouble(sizeField.getText().trim());

                if (!name.isEmpty()) {
                    Field field = new Field(name, size, (Field.SoilType) soilCombo.getSelectedItem());
                    field.setLocation(locationField.getText().trim());
                    farmService.addField(field);
                    refresh();
                    refreshCallback.run();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid size value", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = fieldTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a field to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Field field = farmService.getField(id);
        if (field == null) return;

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField(field.getName());
        JTextField sizeField = new JTextField(String.valueOf(field.getSizeInAcres()));
        JComboBox<Field.SoilType> soilCombo = new JComboBox<>(Field.SoilType.values());
        soilCombo.setSelectedItem(field.getSoilType());
        JTextField locationField = new JTextField(field.getLocation() != null ? field.getLocation() : "");

        panel.add(new JLabel("Field Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Size (acres):"));
        panel.add(sizeField);
        panel.add(new JLabel("Soil Type:"));
        panel.add(soilCombo);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Field",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                field.setName(nameField.getText().trim());
                field.setSizeInAcres(Double.parseDouble(sizeField.getText().trim()));
                field.setSoilType((Field.SoilType) soilCombo.getSelectedItem());
                field.setLocation(locationField.getText().trim());
                refresh();
                refreshCallback.run();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid size value", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void plantCropInField() {
        int row = fieldTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a field", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String fieldId = (String) tableModel.getValueAt(row, 0);
        Field field = farmService.getField(fieldId);
        
        if (field.getStatus() == Field.FieldStatus.PLANTED) {
            JOptionPane.showMessageDialog(this, "This field already has crops planted. Choose a different field.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Crop> crops = farmService.getCropsByStatus(Crop.CropStatus.PLANTED);
        if (crops.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No crops available for planting. Add a crop first.", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Crop[] cropArray = crops.toArray(new Crop[0]);
        Crop selected = (Crop) JOptionPane.showInputDialog(this,
            "Select crop to plant in " + field.getName() + ":",
            "Plant Crop",
            JOptionPane.PLAIN_MESSAGE, null, cropArray, cropArray[0]);

        if (selected != null) {
            farmService.plantCropInField(fieldId, selected.getId());
            refresh();
            refreshCallback.run();
            JOptionPane.showMessageDialog(this, 
                selected.getName() + " planted in " + field.getName() + " successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteField() {
        int row = fieldTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a field to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this field?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(row, 0);
            farmService.removeField(id);
            refresh();
            refreshCallback.run();
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Field field : farmService.getAllFields()) {
            tableModel.addRow(new Object[]{
                field.getId(),
                field.getName(),
                String.format("%.2f", field.getSizeInAcres()),
                field.getSoilType().toString(),
                field.getStatus().toString(),
                field.getLocation() != null ? field.getLocation() : "",
                field.getCropIds().size()
            });
        }

        // Update summary panel
        if (summaryPanel != null) {
            remove(summaryPanel);
        }
        summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
}
