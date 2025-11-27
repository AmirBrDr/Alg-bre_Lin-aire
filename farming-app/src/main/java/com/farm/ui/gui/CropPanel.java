package com.farm.ui.gui;

import com.farm.model.Crop;
import com.farm.service.FarmService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing crops.
 */
public class CropPanel extends JPanel {
    private final FarmService farmService;
    private final Runnable refreshCallback;
    private JTable cropTable;
    private DefaultTableModel tableModel;
    private JPanel summaryPanel;

    public CropPanel(FarmService farmService, Runnable refreshCallback) {
        this.farmService = farmService;
        this.refreshCallback = refreshCallback;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        createComponents();
    }

    private void createComponents() {
        // Title and buttons panel
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("🌱 Crop Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(46, 125, 50));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("➕ Add Crop");
        JButton editBtn = new JButton("✏️ Edit");
        JButton deleteBtn = new JButton("🗑️ Delete");
        JButton statusBtn = new JButton("📋 Update Status");

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteCrop());
        statusBtn.addActionListener(e -> updateStatus());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(statusBtn);
        buttonPanel.add(deleteBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Variety", "Area (acres)", "Status", "Planting Date", "Expected Yield/Acre"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cropTable = new JTable(tableModel);
        cropTable.setFont(new Font("Arial", Font.PLAIN, 13));
        cropTable.setRowHeight(30);
        cropTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        cropTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(cropTable);
        add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(new Color(232, 245, 233));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(46, 125, 50)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        List<Crop> crops = farmService.getAllCrops();
        long planted = crops.stream().filter(c -> c.getStatus() == Crop.CropStatus.PLANTED).count();
        long growing = crops.stream().filter(c -> c.getStatus() == Crop.CropStatus.GROWING).count();
        long ready = crops.stream().filter(c -> c.getStatus() == Crop.CropStatus.READY_FOR_HARVEST).count();
        double totalArea = crops.stream().mapToDouble(Crop::getAreaInAcres).sum();

        panel.add(new JLabel(String.format("📊 Total: %d crops | ", crops.size())));
        panel.add(new JLabel(String.format("🌱 Planted: %d | ", planted)));
        panel.add(new JLabel(String.format("🌿 Growing: %d | ", growing)));
        panel.add(new JLabel(String.format("🌻 Ready: %d | ", ready)));
        panel.add(new JLabel(String.format("📐 Total Area: %.2f acres", totalArea)));

        return panel;
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField varietyField = new JTextField();
        JTextField areaField = new JTextField();
        JTextField yieldField = new JTextField();
        JTextArea notesArea = new JTextArea(3, 20);

        panel.add(new JLabel("Crop Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Variety:"));
        panel.add(varietyField);
        panel.add(new JLabel("Area (acres):"));
        panel.add(areaField);
        panel.add(new JLabel("Expected Yield/Acre:"));
        panel.add(yieldField);
        panel.add(new JLabel("Notes:"));
        panel.add(new JScrollPane(notesArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Crop",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String variety = varietyField.getText().trim();
                double area = Double.parseDouble(areaField.getText().trim());

                if (!name.isEmpty()) {
                    Crop crop = new Crop(name, variety, area);
                    if (!yieldField.getText().trim().isEmpty()) {
                        crop.setExpectedYieldPerAcre(Double.parseDouble(yieldField.getText().trim()));
                    }
                    crop.setNotes(notesArea.getText().trim());
                    farmService.addCrop(crop);
                    refresh();
                    refreshCallback.run();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = cropTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a crop to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Crop crop = farmService.getCrop(id);
        if (crop == null) return;

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField(crop.getName());
        JTextField varietyField = new JTextField(crop.getVariety());
        JTextField areaField = new JTextField(String.valueOf(crop.getAreaInAcres()));
        JTextField yieldField = new JTextField(String.valueOf(crop.getExpectedYieldPerAcre()));

        panel.add(new JLabel("Crop Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Variety:"));
        panel.add(varietyField);
        panel.add(new JLabel("Area (acres):"));
        panel.add(areaField);
        panel.add(new JLabel("Expected Yield/Acre:"));
        panel.add(yieldField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Crop",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                crop.setName(nameField.getText().trim());
                crop.setVariety(varietyField.getText().trim());
                crop.setAreaInAcres(Double.parseDouble(areaField.getText().trim()));
                if (!yieldField.getText().trim().isEmpty()) {
                    crop.setExpectedYieldPerAcre(Double.parseDouble(yieldField.getText().trim()));
                }
                refresh();
                refreshCallback.run();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStatus() {
        int row = cropTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a crop", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Crop.CropStatus[] statuses = Crop.CropStatus.values();
        Crop.CropStatus selected = (Crop.CropStatus) JOptionPane.showInputDialog(this,
            "Select new status:", "Update Status",
            JOptionPane.PLAIN_MESSAGE, null, statuses, statuses[0]);

        if (selected != null) {
            farmService.updateCropStatus(id, selected);
            refresh();
            refreshCallback.run();
        }
    }

    private void deleteCrop() {
        int row = cropTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a crop to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this crop?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(row, 0);
            farmService.removeCrop(id);
            refresh();
            refreshCallback.run();
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Crop crop : farmService.getAllCrops()) {
            tableModel.addRow(new Object[]{
                crop.getId(),
                crop.getName(),
                crop.getVariety(),
                String.format("%.2f", crop.getAreaInAcres()),
                crop.getStatus().toString(),
                crop.getPlantingDate().toString(),
                String.format("%.2f", crop.getExpectedYieldPerAcre())
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
