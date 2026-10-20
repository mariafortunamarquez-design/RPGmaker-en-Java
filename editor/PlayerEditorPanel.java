/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.model.*;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para configurar las estadísticas iniciales del personaje jugador,
 * posición e inventario.
 */
public class PlayerEditorPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField nameField;
    private JSpinner hpSpinner, atkSpinner, defSpinner, spdSpinner;
    private JSpinner goldSpinner, levelSpinner;
    private JSpinner startXSpinner, startYSpinner;
    private JComboBox<String> startMapCombo;

    private boolean updating = false;

    public PlayerEditorPanel(MainFrame mainFrame) {
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

        // Encabezado
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel header = new JLabel("Configuración del Jugador");
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(header, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Nombre:"), gbc);
        nameField = new JTextField(12);
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(nameField, gbc);

        // Estadísticas
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("HP Máx:"), gbc);
        hpSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 99999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(hpSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Ataque:"), gbc);
        atkSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 9999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(atkSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Defensa:"), gbc);
        defSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 9999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(defSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Velocidad:"), gbc);
        spdSpinner = new JSpinner(new SpinnerNumberModel(8, 0, 9999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(spdSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Oro Inicial:"), gbc);
        goldSpinner = new JSpinner(new SpinnerNumberModel(50, 0, 99999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(goldSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Nivel Inicial:"), gbc);
        levelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(levelSpinner, gbc);

        // Separador
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Posición inicial
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        add(new JLabel("Posición Inicial:"), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Mapa:"), gbc);
        startMapCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(startMapCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Inicio X:"), gbc);
        startXSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(startXSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel("Inicio Y:"), gbc);
        startYSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        add(startYSpinner, gbc);

        // Botón aplicar
        JButton applyBtn = new JButton("Aplicar Cambios");
        applyBtn.addActionListener(e -> applyChanges());
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        add(applyBtn, gbc);

        gbc.gridy = row + 1;
        gbc.weighty = 1;
        add(Box.createVerticalGlue(), gbc);
    }

    private void applyChanges() {
        if (updating)
            return;
        PlayerConfig config = mainFrame.getProject().getPlayerConfig();

        config.setName(nameField.getText());
        config.setMaxHp((int) hpSpinner.getValue());
        config.setAttack((int) atkSpinner.getValue());
        config.setDefense((int) defSpinner.getValue());
        config.setSpeed((int) spdSpinner.getValue());
        config.setStartingGold((int) goldSpinner.getValue());
        config.setStartingLevel((int) levelSpinner.getValue());
        config.setStartX((int) startXSpinner.getValue());
        config.setStartY((int) startYSpinner.getValue());

        // Establecer mapa inicial
        int mapIdx = startMapCombo.getSelectedIndex();
        if (mapIdx >= 0 && mapIdx < mainFrame.getProject().getMaps().size()) {
            config.setStartingMapId(mainFrame.getProject().getMaps().get(mapIdx).getId());
        }

        mainFrame.setStatus("Configuración del jugador actualizada");
    }

    public void refresh() {
        updating = true;
        PlayerConfig config = mainFrame.getProject().getPlayerConfig();
        nameField.setText(config.getName());
        hpSpinner.setValue(config.getMaxHp());
        atkSpinner.setValue(config.getAttack());
        defSpinner.setValue(config.getDefense());
        spdSpinner.setValue(config.getSpeed());
        goldSpinner.setValue(config.getStartingGold());
        levelSpinner.setValue(config.getStartingLevel());
        startXSpinner.setValue(config.getStartX());
        startYSpinner.setValue(config.getStartY());

        // Rellenar combo de mapa inicial
        DefaultComboBoxModel<String> mapModel = new DefaultComboBoxModel<>();
        int selectedIdx = 0;
        for (int i = 0; i < mainFrame.getProject().getMaps().size(); i++) {
            GameMap m = mainFrame.getProject().getMaps().get(i);
            mapModel.addElement(m.getName());
            if (m.getId().equals(config.getStartingMapId())) {
                selectedIdx = i;
            }
        }
        startMapCombo.setModel(mapModel);
        if (mapModel.getSize() > 0) {
            startMapCombo.setSelectedIndex(selectedIdx);
        }

        updating = false;
    }
}
