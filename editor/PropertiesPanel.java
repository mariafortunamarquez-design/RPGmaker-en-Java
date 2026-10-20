/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.model.GameMap;

import javax.swing.*;
import java.awt.*;

/**
 * Muestra y edita las propiedades del mapa seleccionado actualmente.
 */
public class PropertiesPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField nameField;
    private JLabel sizeLabel;
    private JSpinner encounterRateSpinner;
    private JComboBox<String> northMapCombo, southMapCombo, eastMapCombo, westMapCombo;
    private GameMap currentMap;
    private boolean updating = false;

    public PropertiesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        buildUI();
    }

    private void buildUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Nombre del mapa
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Nombre:"), gbc);
        nameField = new JTextField(15);
        nameField.addActionListener(e -> {
            if (currentMap != null && !updating) {
                currentMap.setName(nameField.getText());
                mainFrame.setStatus("Mapa renombrado a: " + currentMap.getName());
            }
        });
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(nameField, gbc);

        // Tamaño
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Tamaño:"), gbc);
        sizeLabel = new JLabel("-");
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(sizeLabel, gbc);

        // Separador
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Tasa de encuentros
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Tasa Encuentros:"), gbc);
        encounterRateSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        encounterRateSpinner.addChangeListener(e -> {
            if (currentMap != null && !updating) {
                currentMap.setEncounterRate((int) encounterRateSpinner.getValue());
            }
        });
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(encounterRateSpinner, gbc);

        // Conexiones de mapa
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(new JLabel("Conexiones de Mapa:"), gbc);
        gbc.gridwidth = 1;

        northMapCombo = new JComboBox<>();
        southMapCombo = new JComboBox<>();
        eastMapCombo = new JComboBox<>();
        westMapCombo = new JComboBox<>();

        String[] dirs = { "Norte:", "Sur:", "Este:", "Oeste:" };
        @SuppressWarnings("unchecked")
        JComboBox<String>[] combos = new JComboBox[] { northMapCombo, southMapCombo, eastMapCombo, westMapCombo };
        for (int i = 0; i < 4; i++) {
            gbc.gridx = 0;
            gbc.gridy = row;
            add(new JLabel(dirs[i]), gbc);
            gbc.gridx = 1;
            gbc.gridy = row++;
            add(combos[i], gbc);
        }

        // Rellenar espacio restante
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }

    @SuppressWarnings("unchecked")
    public void showMapProperties(GameMap map) {
        this.currentMap = map;
        updating = true;
        if (map != null) {
            nameField.setText(map.getName());
            sizeLabel.setText(map.getWidth() + " x " + map.getHeight());
            encounterRateSpinner.setValue(map.getEncounterRate());

            // Rellenar dropdowns de conexiones de mapa
            DefaultComboBoxModel<String> model;
            for (JComboBox<String> combo : new JComboBox[] { northMapCombo, southMapCombo, eastMapCombo,
                    westMapCombo }) {
                model = new DefaultComboBoxModel<>();
                model.addElement("(Ninguno)");
                for (GameMap m : mainFrame.getProject().getMaps()) {
                    if (!m.getId().equals(map.getId())) {
                        model.addElement(m.getName());
                    }
                }
                combo.setModel(model);
            }
        } else {
            nameField.setText("");
            sizeLabel.setText("-");
            encounterRateSpinner.setValue(0);
        }
        updating = false;
    }
}
