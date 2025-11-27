package com.farm.ui.gui;

import com.farm.model.Livestock;
import com.farm.service.FarmService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing livestock.
 */
public class LivestockPanel extends JPanel {
    private final FarmService farmService;
    private final Runnable refreshCallback;
    private JTable livestockTable;
    private DefaultTableModel tableModel;
    private JPanel summaryPanel;

    public LivestockPanel(FarmService farmService, Runnable refreshCallback) {
        this.farmService = farmService;
        this.refreshCallback = refreshCallback;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        createComponents();
    }

    private void createComponents() {
        // Title and buttons panel
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("🐄 Livestock Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(139, 69, 19));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("➕ Add Animal");
        JButton editBtn = new JButton("✏️ Edit");
        JButton healthBtn = new JButton("🏥 Update Health");
        JButton deleteBtn = new JButton("🗑️ Delete");

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        healthBtn.addActionListener(e -> updateHealth());
        deleteBtn.addActionListener(e -> deleteAnimal());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(healthBtn);
        buttonPanel.add(deleteBtn);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name/Tag", "Type", "Breed", "Gender", "Health Status", "Weight (kg)", "Acquisition Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        livestockTable = new JTable(tableModel);
        livestockTable.setFont(new Font("Arial", Font.PLAIN, 13));
        livestockTable.setRowHeight(30);
        livestockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        livestockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(livestockTable);
        add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBackground(new Color(255, 243, 224));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(139, 69, 19)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        List<Livestock> animals = farmService.getAllLivestock();
        long cattle = animals.stream().filter(a -> a.getType() == Livestock.AnimalType.CATTLE).count();
        long pigs = animals.stream().filter(a -> a.getType() == Livestock.AnimalType.PIG).count();
        long sheep = animals.stream().filter(a -> a.getType() == Livestock.AnimalType.SHEEP).count();
        long chickens = animals.stream().filter(a -> a.getType() == Livestock.AnimalType.CHICKEN).count();
        long sick = animals.stream().filter(a -> a.getHealthStatus() == Livestock.HealthStatus.SICK).count();

        panel.add(new JLabel(String.format("📊 Total: %d animals | ", animals.size())));
        panel.add(new JLabel(String.format("🐄 Cattle: %d | ", cattle)));
        panel.add(new JLabel(String.format("🐷 Pigs: %d | ", pigs)));
        panel.add(new JLabel(String.format("🐑 Sheep: %d | ", sheep)));
        panel.add(new JLabel(String.format("🐔 Chickens: %d | ", chickens)));
        if (sick > 0) {
            JLabel sickLabel = new JLabel(String.format("🏥 Sick: %d", sick));
            sickLabel.setForeground(Color.RED);
            panel.add(sickLabel);
        }

        return panel;
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JComboBox<Livestock.AnimalType> typeCombo = new JComboBox<>(Livestock.AnimalType.values());
        JTextField breedField = new JTextField();
        JComboBox<Livestock.Gender> genderCombo = new JComboBox<>(Livestock.Gender.values());
        JTextField weightField = new JTextField();

        panel.add(new JLabel("Name/Tag:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Breed:"));
        panel.add(breedField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderCombo);
        panel.add(new JLabel("Weight (kg):"));
        panel.add(weightField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Animal",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                Livestock animal = new Livestock(name,
                    (Livestock.AnimalType) typeCombo.getSelectedItem(),
                    breedField.getText().trim(),
                    (Livestock.Gender) genderCombo.getSelectedItem());
                
                if (!weightField.getText().trim().isEmpty()) {
                    try {
                        animal.setWeight(Double.parseDouble(weightField.getText().trim()));
                    } catch (NumberFormatException ignored) {}
                }
                
                farmService.addLivestock(animal);
                refresh();
                refreshCallback.run();
            }
        }
    }

    private void showEditDialog() {
        int row = livestockTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an animal to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Livestock animal = farmService.getLivestock(id);
        if (animal == null) return;

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField(animal.getName());
        JTextField breedField = new JTextField(animal.getBreed());
        JTextField weightField = new JTextField(String.valueOf(animal.getWeight()));
        JTextField locationField = new JTextField(animal.getLocation() != null ? animal.getLocation() : "");

        panel.add(new JLabel("Name/Tag:"));
        panel.add(nameField);
        panel.add(new JLabel("Breed:"));
        panel.add(breedField);
        panel.add(new JLabel("Weight (kg):"));
        panel.add(weightField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Animal",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            animal.setName(nameField.getText().trim());
            animal.setBreed(breedField.getText().trim());
            animal.setLocation(locationField.getText().trim());
            try {
                animal.setWeight(Double.parseDouble(weightField.getText().trim()));
            } catch (NumberFormatException ignored) {}
            refresh();
            refreshCallback.run();
        }
    }

    private void updateHealth() {
        int row = livestockTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an animal", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Livestock.HealthStatus[] statuses = Livestock.HealthStatus.values();
        Livestock.HealthStatus selected = (Livestock.HealthStatus) JOptionPane.showInputDialog(this,
            "Select health status:", "Update Health Status",
            JOptionPane.PLAIN_MESSAGE, null, statuses, statuses[0]);

        if (selected != null) {
            farmService.updateLivestockHealth(id, selected);
            refresh();
            refreshCallback.run();
        }
    }

    private void deleteAnimal() {
        int row = livestockTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an animal to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove this animal?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(row, 0);
            farmService.removeLivestock(id);
            refresh();
            refreshCallback.run();
        }
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Livestock animal : farmService.getAllLivestock()) {
            tableModel.addRow(new Object[]{
                animal.getId(),
                animal.getName(),
                animal.getType().toString(),
                animal.getBreed(),
                animal.getGender().toString(),
                animal.getHealthStatus().toString(),
                String.format("%.1f", animal.getWeight()),
                animal.getAcquisitionDate().toString()
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
