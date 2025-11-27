package com.farm;

import com.farm.service.FarmService;
import com.farm.ui.ConsoleUI;
import com.farm.ui.gui.MainFrame;

import javax.swing.*;

/**
 * Main entry point for the Farming Management Application.
 * Supports both GUI and console modes.
 */
public class FarmingApp {

    public static void main(String[] args) {
        FarmService farmService = new FarmService();
        
        // Check if console mode is requested via command line argument
        boolean consoleMode = false;
        for (String arg : args) {
            if ("--console".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                consoleMode = true;
                break;
            }
        }
        
        if (consoleMode) {
            // Run in console mode
            ConsoleUI ui = new ConsoleUI(farmService);
            ui.start();
        } else {
            // Run in GUI mode (default)
            SwingUtilities.invokeLater(() -> {
                try {
                    // Set system look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // Use default look and feel
                }
                
                MainFrame mainFrame = new MainFrame(farmService);
                mainFrame.setVisible(true);
            });
        }
    }
}
