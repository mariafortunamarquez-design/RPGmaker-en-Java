/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.model.*;

import javax.swing.*;
import java.awt.*;

/**
 * Panel para crear y editar objetos (armas, armaduras, consumibles, etc.).
 */
public class ItemEditorPanel extends JPanel {

    private MainFrame mainFrame;
    private DefaultListModel<Item> listModel;
    private JList<Item> itemList;

    // Campos de edición
    private JTextField nameField, descField;
    private JComboBox<ItemType> typeCombo;
    private JSpinner atkSpinner, defSpinner, hpSpinner, spdSpinner;
    private JSpinner healSpinner, buySpinner, sellSpinner;
    private JCheckBox stackableCheck;

    private boolean updating = false;

    public ItemEditorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Lista de objetos
        listModel = new DefaultListModel<>();
        itemList = new JList<>(listModel);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showItemDetails(itemList.getSelectedValue());
        });

        JScrollPane listScroll = new JScrollPane(itemList);
        listScroll.setPreferredSize(new Dimension(0, 130));
        listScroll.setBorder(BorderFactory.createTitledBorder("Objetos"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton addBtn = new JButton("+ Añadir");
        addBtn.addActionListener(e -> addItem());
        JButton removeBtn = new JButton("- Eliminar");
        removeBtn.addActionListener(e -> removeItem());
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(listScroll, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Panel de edición
        add(buildEditPanel(), BorderLayout.CENTER);
    }

    private JPanel buildEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Propiedades del Objeto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Nombre:"), gbc);
        nameField = new JTextField(12);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Tipo:"), gbc);
        typeCombo = new JComboBox<>(ItemType.values());
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Descripción:"), gbc);
        descField = new JTextField(12);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(descField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Bonificaciones de estadísticas
        String[] labels = { "Bono ATQ:", "Bono DEF:", "Bono HP:", "Bono VEL:", "Curación:" };
        JSpinner[] spinners = new JSpinner[5];
        atkSpinner = spinners[0] = new JSpinner(new SpinnerNumberModel(0, -999, 999, 1));
        defSpinner = spinners[1] = new JSpinner(new SpinnerNumberModel(0, -999, 999, 1));
        hpSpinner = spinners[2] = new JSpinner(new SpinnerNumberModel(0, -999, 999, 1));
        spdSpinner = spinners[3] = new JSpinner(new SpinnerNumberModel(0, -999, 999, 1));
        healSpinner = spinners[4] = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            gbc.gridy = row++;
            panel.add(spinners[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Precios
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Precio Compra:"), gbc);
        buySpinner = new JSpinner(new SpinnerNumberModel(10, 0, 99999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(buySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Precio Venta:"), gbc);
        sellSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 99999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(sellSpinner, gbc);

        stackableCheck = new JCheckBox("Apilable");
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(stackableCheck, gbc);
        gbc.gridwidth = 1;

        JButton applyBtn = new JButton("Aplicar Cambios");
        applyBtn.addActionListener(e -> applyChanges());
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(applyBtn, gbc);

        gbc.gridy = row + 1;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private void addItem() {
        String name = JOptionPane.showInputDialog(this, "Nombre del objeto:");
        if (name != null && !name.isBlank()) {
            Item item = new Item(name, ItemType.CONSUMIBLE);
            mainFrame.getProject().addItem(item);
            refresh();
            itemList.setSelectedValue(item, true);
        }
    }

    private void removeItem() {
        Item item = itemList.getSelectedValue();
        if (item != null) {
            mainFrame.getProject().removeItem(item);
            refresh();
        }
    }

    private void showItemDetails(Item item) {
        if (item == null)
            return;
        updating = true;
        nameField.setText(item.getName());
        typeCombo.setSelectedItem(item.getType());
        descField.setText(item.getDescription());
        atkSpinner.setValue(item.getAttackBonus());
        defSpinner.setValue(item.getDefenseBonus());
        hpSpinner.setValue(item.getHpBonus());
        spdSpinner.setValue(item.getSpeedBonus());
        healSpinner.setValue(item.getHealAmount());
        buySpinner.setValue(item.getBuyPrice());
        sellSpinner.setValue(item.getSellPrice());
        stackableCheck.setSelected(item.isStackable());
        updating = false;
    }

    private void applyChanges() {
        if (updating)
            return;
        Item item = itemList.getSelectedValue();
        if (item == null)
            return;

        item.setName(nameField.getText());
        item.setType((ItemType) typeCombo.getSelectedItem());
        item.setDescription(descField.getText());
        item.setAttackBonus((int) atkSpinner.getValue());
        item.setDefenseBonus((int) defSpinner.getValue());
        item.setHpBonus((int) hpSpinner.getValue());
        item.setSpeedBonus((int) spdSpinner.getValue());
        item.setHealAmount((int) healSpinner.getValue());
        item.setBuyPrice((int) buySpinner.getValue());
        item.setSellPrice((int) sellSpinner.getValue());
        item.setStackable(stackableCheck.isSelected());

        refresh();
        itemList.setSelectedValue(item, true);
        mainFrame.setStatus("Objeto actualizado: " + item.getName());
    }

    public void refresh() {
        listModel.clear();
        for (Item item : mainFrame.getProject().getItems()) {
            listModel.addElement(item);
        }
    }
}
