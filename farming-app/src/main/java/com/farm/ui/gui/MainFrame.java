package com.farm.ui.gui;

import com.farm.service.FarmService;
import com.farm.model.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI frame for the Farming Management Application.
 */
public class MainFrame extends JFrame {
    private final FarmService farmService;
    private JTabbedPane tabbedPane;
    private CropPanel cropPanel;
    private LivestockPanel livestockPanel;
    private FieldPanel fieldPanel;
    private InventoryPanel inventoryPanel;
    private HarvestPanel harvestPanel;
    private DashboardPanel dashboardPanel;
    private JPanel statusBar;

    public MainFrame(FarmService farmService) {
        this.farmService = farmService;
        initializeFrame();
        setupFarm();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("🌾 Farming Management Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
    }

    private void setupFarm() {
        if (farmService.getFarm() == null) {
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JTextField farmNameField = new JTextField("My Farm");
            JTextField ownerNameField = new JTextField("Farm Owner");
            JTextField acreageField = new JTextField("100");

            panel.add(new JLabel("Farm Name:"));
            panel.add(farmNameField);
            panel.add(new JLabel("Owner Name:"));
            panel.add(ownerNameField);
            panel.add(new JLabel("Total Acreage:"));
            panel.add(acreageField);

            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Setup Your Farm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String farmName = farmNameField.getText().trim();
                String ownerName = ownerNameField.getText().trim();
                double acreage = 100.0;
                try {
                    acreage = Double.parseDouble(acreageField.getText().trim());
                } catch (NumberFormatException ignored) {}

                if (farmName.isEmpty()) farmName = "My Farm";
                if (ownerName.isEmpty()) ownerName = "Farm Owner";

                Farm farm = new Farm(farmName, ownerName, acreage);
                farmService.setFarm(farm);
            } else {
                Farm farm = new Farm("My Farm", "Farm Owner", 100.0);
                farmService.setFarm(farm);
            }
        }
    }

    private void createComponents() {
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Create panels
        dashboardPanel = new DashboardPanel(farmService);
        cropPanel = new CropPanel(farmService, this::refreshAll);
        livestockPanel = new LivestockPanel(farmService, this::refreshAll);
        fieldPanel = new FieldPanel(farmService, this::refreshAll);
        inventoryPanel = new InventoryPanel(farmService, this::refreshAll);
        harvestPanel = new HarvestPanel(farmService, this::refreshAll);

        // Add tabs with icons represented by text
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);
        tabbedPane.addTab("🌱 Crops", cropPanel);
        tabbedPane.addTab("🐄 Livestock", livestockPanel);
        tabbedPane.addTab("🌾 Fields", fieldPanel);
        tabbedPane.addTab("📦 Inventory", inventoryPanel);
        tabbedPane.addTab("🌻 Harvests", harvestPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Create status bar
        statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(46, 125, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        Farm farm = farmService.getFarm();
        String farmName = farm != null ? farm.getName() : "Farm";
        String ownerName = farm != null ? farm.getOwnerName() : "";

        JLabel titleLabel = new JLabel("🌾 " + farmName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel ownerLabel = new JLabel("Owner: " + ownerName);
        ownerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        ownerLabel.setForeground(new Color(200, 230, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(ownerLabel);

        panel.add(textPanel, BorderLayout.WEST);

        // Add refresh button
        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.addActionListener(e -> refreshAll());
        panel.add(refreshBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        FarmService.FarmStatistics stats = farmService.getStatistics();
        JLabel statusLabel = new JLabel(String.format(
            "Crops: %d | Livestock: %d | Fields: %d | Inventory: %d | Harvests: %d",
            stats.totalCrops, stats.totalLivestock, stats.totalFields,
            stats.totalInventoryItems, stats.totalHarvests
        ));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(statusLabel);
        return panel;
    }

    public void refreshAll() {
        dashboardPanel.refresh();
        cropPanel.refresh();
        livestockPanel.refresh();
        fieldPanel.refresh();
        inventoryPanel.refresh();
        harvestPanel.refresh();

        // Update status bar
        if (statusBar != null) {
            remove(statusBar);
        }
        statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
}
