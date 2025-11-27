package com.farm.ui.gui;

import com.farm.model.InventoryItem;
import com.farm.service.FarmService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing inventory.
 */
public class InventoryPanel extends JPanel {
    private final FarmService farmService;
    private final Runnable refreshCallback;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public InventoryPanel(FarmService farmService, Runnable refreshCallback) {
        this.farmService = farmService;
        this.refreshCallback = refreshCallback;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        createComponents();
    }

    private void createComponents() {
        // Title and buttons panel
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("📦 Inventory Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 150, 243));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("➕ Add Item");
        JButton editBtn = new JButton("✏️ Edit");
        JButton adjustBtn = new JButton("📊 Adjust Quantity");
        JButton deleteBtn = new JButton("🗑️ Delete");

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        adjustBtn.addActionListener(e -> adjustQuantity());
        deleteBtn.addActionListener(e -> deleteItem());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(adjustBtn);
        buttonPanel.add(deleteBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Category", "Quantity", "Unit", "Min Stock", "Status", "Supplier"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 13));
        inventoryTable.setRowHeight(30);
        inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom renderer for status column
        inventoryTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(new Color(227, 242, 253));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(33, 150, 243)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        List<InventoryItem> items = farmService.getAllInventory();
        long seeds = items.stream().filter(i -> i.getCategory() == InventoryItem.ItemCategory.SEEDS).count();
        long fertilizer = items.stream().filter(i -> i.getCategory() == InventoryItem.ItemCategory.FERTILIZER).count();
        long feed = items.stream().filter(i -> i.getCategory() == InventoryItem.ItemCategory.FEED).count();
        long lowStock = farmService.getLowStockItems().size();

        panel.add(new JLabel(String.format("📊 Total Items: %d | ", items.size())));
        panel.add(new JLabel(String.format("🌱 Seeds: %d | ", seeds)));
        panel.add(new JLabel(String.format("🧪 Fertilizer: %d | ", fertilizer)));
        panel.add(new JLabel(String.format("🍽️ Feed: %d | ", feed)));
        
        if (lowStock > 0) {
            JLabel lowStockLabel = new JLabel(String.format("⚠️ Low Stock: %d", lowStock));
            lowStockLabel.setForeground(Color.RED);
            lowStockLabel.setFont(lowStockLabel.getFont().deriveFont(Font.BOLD));
            panel.add(lowStockLabel);
        } else {
            panel.add(new JLabel("✅ All items well stocked"));
        }

        return panel;
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JComboBox<InventoryItem.ItemCategory> categoryCombo = new JComboBox<>(InventoryItem.ItemCategory.values());
        JTextField quantityField = new JTextField();
        JTextField unitField = new JTextField();
        JTextField minStockField = new JTextField("0");
        JTextField supplierField = new JTextField();

        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit (e.g., kg, bags):"));
        panel.add(unitField);
        panel.add(new JLabel("Minimum Stock:"));
        panel.add(minStockField);
        panel.add(new JLabel("Supplier:"));
        panel.add(supplierField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Inventory Item",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double quantity = Double.parseDouble(quantityField.getText().trim());
                String unit = unitField.getText().trim();

                if (!name.isEmpty() && !unit.isEmpty()) {
                    InventoryItem item = new InventoryItem(name, 
                        (InventoryItem.ItemCategory) categoryCombo.getSelectedItem(),
                        quantity, unit);
                    item.setMinimumStock(Double.parseDouble(minStockField.getText().trim()));
                    item.setSupplier(supplierField.getText().trim());
                    farmService.addInventoryItem(item);
                    refresh();
                    refreshCallback.run();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        InventoryItem item = farmService.getInventoryItem(id);
        if (item == null) return;

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField(item.getName());
        JTextField unitField = new JTextField(item.getUnit());
        JTextField minStockField = new JTextField(String.valueOf(item.getMinimumStock()));
        JTextField supplierField = new JTextField(item.getSupplier() != null ? item.getSupplier() : "");
        JTextField storageField = new JTextField(item.getStorageLocation() != null ? item.getStorageLocation() : "");

        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitField);
        panel.add(new JLabel("Minimum Stock:"));
        panel.add(minStockField);
        panel.add(new JLabel("Supplier:"));
        panel.add(supplierField);
        panel.add(new JLabel("Storage Location:"));
        panel.add(storageField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Item",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                item.setName(nameField.getText().trim());
                item.setUnit(unitField.getText().trim());
                item.setMinimumStock(Double.parseDouble(minStockField.getText().trim()));
                item.setSupplier(supplierField.getText().trim());
                item.setStorageLocation(storageField.getText().trim());
                refresh();
                refreshCallback.run();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void adjustQuantity() {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        InventoryItem item = farmService.getInventoryItem(id);
        if (item == null) return;

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Current Quantity:"));
        panel.add(new JLabel(String.format("%.2f %s", item.getQuantity(), item.getUnit())));
        panel.add(new JLabel("Adjustment (+/-):"));
        JTextField adjustField = new JTextField();
        panel.add(adjustField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Adjust Quantity",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double adjustment = Double.parseDouble(adjustField.getText().trim());
                item.addQuantity(adjustment);
                refresh();
                refreshCallback.run();
                JOptionPane.showMessageDialog(this, 
                    String.format("New quantity: %.2f %s", item.getQuantity(), item.getUnit()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteItem() {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this item?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(row, 0);
            farmService.removeInventoryItem(id);
            refresh();
            refreshCallback.run();
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (InventoryItem item : farmService.getAllInventory()) {
            String status = item.isLowStock() ? "⚠️ LOW STOCK" : "✅ OK";
            tableModel.addRow(new Object[]{
                item.getId(),
                item.getName(),
                item.getCategory().toString(),
                String.format("%.2f", item.getQuantity()),
                item.getUnit(),
                String.format("%.2f", item.getMinimumStock()),
                status,
                item.getSupplier() != null ? item.getSupplier() : ""
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

    // Custom cell renderer for status column
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value.toString();
            if (status.contains("LOW")) {
                c.setForeground(Color.RED);
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else {
                c.setForeground(new Color(46, 125, 50));
            }
            return c;
        }
    }
}
