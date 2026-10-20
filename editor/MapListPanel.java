/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.model.GameMap;

import javax.swing.*;
import java.awt.*;

/**
 * Panel lateral que lista todos los mapas del proyecto con controles de
 * añadir/eliminar/renombrar.
 */
public class MapListPanel extends JPanel {

    private MainFrame mainFrame;
    private DefaultListModel<GameMap> listModel;
    private JList<GameMap> mapList;

    public MapListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        listModel = new DefaultListModel<>();
        mapList = new JList<>(listModel);
        mapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mapList.setCellRenderer(new MapCellRenderer());
        mapList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                GameMap selected = mapList.getSelectedValue();
                if (selected != null) {
                    mainFrame.setCurrentMap(selected);
                }
            }
        });

        add(new JScrollPane(mapList), BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 4, 0));
        JButton addBtn = new JButton("+ Añadir");
        addBtn.addActionListener(e -> addNewMap());
        JButton removeBtn = new JButton("- Elim");
        removeBtn.addActionListener(e -> removeSelectedMap());
        JButton renameBtn = new JButton("Renombrar");
        renameBtn.addActionListener(e -> renameSelectedMap());

        addBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        removeBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        renameBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(renameBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addNewMap() {
        String name = JOptionPane.showInputDialog(this, "Nombre del mapa:", "Nuevo Mapa");
        if (name != null && !name.isBlank()) {
            String defaultId = name.toLowerCase().replace(" ", "_");
            JTextField idField = new JTextField(defaultId, 15);
            SpinnerNumberModel widthModel = new SpinnerNumberModel(20, 5, 200, 1);
            SpinnerNumberModel heightModel = new SpinnerNumberModel(15, 5, 200, 1);
            JSpinner widthSpinner = new JSpinner(widthModel);
            JSpinner heightSpinner = new JSpinner(heightModel);

            JPanel inputPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridx = 0;
            gbc.gridy = 0;
            inputPanel.add(new JLabel("ID Único:"), gbc);
            gbc.gridx = 1;
            inputPanel.add(idField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            inputPanel.add(new JLabel("Ancho:"), gbc);
            gbc.gridx = 1;
            inputPanel.add(widthSpinner, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            inputPanel.add(new JLabel("Alto:"), gbc);
            gbc.gridx = 1;
            inputPanel.add(heightSpinner, gbc);

            int result = JOptionPane.showConfirmDialog(this, inputPanel,
                    "Configuración del Nuevo Mapa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String id = idField.getText().trim();
                if (id.isEmpty())
                    id = java.util.UUID.randomUUID().toString();

                GameMap map = new GameMap(name, (int) widthSpinner.getValue(), (int) heightSpinner.getValue());
                map.setId(id);
                mainFrame.getProject().addMap(map);
                refresh();
                mapList.setSelectedValue(map, true);
            }
        }
    }

    private void removeSelectedMap() {
        GameMap selected = mapList.getSelectedValue();
        if (selected == null)
            return;
        int choice = JOptionPane.showConfirmDialog(this,
                "¿Eliminar mapa '" + selected.getName() + "'?",
                "Eliminar Mapa", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            mainFrame.getProject().removeMap(selected);
            refresh();
            if (!mainFrame.getProject().getMaps().isEmpty()) {
                mapList.setSelectedIndex(0);
            }
        }
    }

    private void renameSelectedMap() {
        GameMap selected = mapList.getSelectedValue();
        if (selected == null)
            return;
        String newName = JOptionPane.showInputDialog(this, "Nuevo nombre:", selected.getName());
        if (newName != null && !newName.isBlank()) {
            selected.setName(newName);
            refresh();
        }
    }

    public void refresh() {
        listModel.clear();
        for (GameMap map : mainFrame.getProject().getMaps()) {
            listModel.addElement(map);
        }
    }

    /**
     * Renderizador personalizado para mostrar nombre del mapa + dimensiones.
     */
    private static class MapCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof GameMap map) {
                setText(String.format("%s (%dx%d)", map.getName(), map.getWidth(), map.getHeight()));
            }
            return this;
        }
    }
}
