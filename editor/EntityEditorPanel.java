/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.model.*;
import com.rpgmaker.util.ImageCache;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Panel para crear, editar y gestionar entidades (NPCs, enemigos, objetos).
 */
public class EntityEditorPanel extends JPanel {

    private MainFrame mainFrame;
    private DefaultListModel<Entity> listModel;
    private JList<Entity> entityList;

    // Campos de edición
    private JTextField nameField;
    private JComboBox<EntityType> typeCombo;
    private JSpinner xSpinner, ySpinner;
    private JSpinner hpSpinner, atkSpinner, defSpinner, spdSpinner;
    private JSpinner expSpinner, goldSpinner;
    private JTextField eventIdField;
    private JTextArea dialogueArea;
    private JPanel editPanel;

    // Campo de imagen
    private JLabel imagePreviewLabel;
    private JTextField imagePathField;

    private boolean updating = false;

    public EntityEditorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Lista de entidades
        listModel = new DefaultListModel<>();
        entityList = new JList<>(listModel);
        entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entityList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                showEntityDetails(entityList.getSelectedValue());
        });

        JScrollPane listScroll = new JScrollPane(entityList);
        listScroll.setPreferredSize(new Dimension(0, 150));
        listScroll.setBorder(BorderFactory.createTitledBorder("Entidades en el Mapa"));

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton addBtn = new JButton("+ Añadir");
        addBtn.addActionListener(e -> addEntity());
        JButton removeBtn = new JButton("- Eliminar");
        removeBtn.addActionListener(e -> removeEntity());
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(listScroll, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Panel de edición
        editPanel = buildEditPanel();
        add(editPanel, BorderLayout.CENTER);
    }

    private JPanel buildEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Propiedades de Entidad"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Nombre:"), gbc);
        nameField = new JTextField(12);
        nameField.addActionListener(e -> applyChanges());
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(nameField, gbc);

        // Tipo
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Tipo:"), gbc);
        typeCombo = new JComboBox<>(EntityType.values());
        typeCombo.addActionListener(e -> applyChanges());
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(typeCombo, gbc);

        // Posición
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Posición (X, Y):"), gbc);
        JPanel posPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        xSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        ySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        xSpinner.setPreferredSize(new Dimension(60, 24));
        ySpinner.setPreferredSize(new Dimension(60, 24));
        posPanel.add(xSpinner);
        posPanel.add(ySpinner);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(posPanel, gbc);

        // Encabezado de estadísticas
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Estadísticas (para enemigos):"), gbc);
        gbc.gridwidth = 1;

        // HP
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("HP:"), gbc);
        hpSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 99999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(hpSpinner, gbc);

        // Ataque
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Ataque:"), gbc);
        atkSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 9999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(atkSpinner, gbc);

        // Defensa
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Defensa:"), gbc);
        defSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 9999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(defSpinner, gbc);

        // Velocidad
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Velocidad:"), gbc);
        spdSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 9999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(spdSpinner, gbc);

        // Recompensas
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Recomp. EXP:"), gbc);
        expSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 99999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(expSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Recomp. Oro:"), gbc);
        goldSpinner = new JSpinner(new SpinnerNumberModel(3, 0, 99999, 1));
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(goldSpinner, gbc);

        // ID de Evento
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("ID del Evento:"), gbc);
        eventIdField = new JTextField(12);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(eventIdField, gbc);

        // Diálogos
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Diálogos (una línea por entrada):"), gbc);
        gbc.gridwidth = 1;

        dialogueArea = new JTextArea(4, 20);
        dialogueArea.setLineWrap(true);
        dialogueArea.setWrapStyleWord(true);
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        panel.add(new JScrollPane(dialogueArea), gbc);
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Botón aplicar
        JButton applyBtn = new JButton("Aplicar Cambios");
        applyBtn.addActionListener(e -> applyChanges());
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(applyBtn, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        gbc.gridy = row++;
        panel.add(new JLabel("Imagen / Sprite:"), gbc);
        gbc.gridwidth = 1;

        // Campo de ruta
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel imgPanel = new JPanel(new BorderLayout(4, 0));
        imagePathField = new JTextField(12);
        imagePathField.setEditable(false);
        JButton browseImgBtn = new JButton("Seleccionar...");
        browseImgBtn.addActionListener(e -> selectEntityImage());
        JButton clearImgBtn = new JButton("Quitar");
        clearImgBtn.addActionListener(e -> clearEntityImage());
        JPanel imgBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        imgBtns.add(browseImgBtn);
        imgBtns.add(clearImgBtn);
        imgPanel.add(imagePathField, BorderLayout.CENTER);
        imgPanel.add(imgBtns, BorderLayout.EAST);
        panel.add(imgPanel, gbc);
        row++;

        // Preview de imagen
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        imagePreviewLabel = new JLabel("Sin imagen", SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(64, 64));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(imagePreviewLabel, gbc);

        return panel;
    }

    private void addEntity() {
        GameMap map = mainFrame.getCurrentMap();
        if (map == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un mapa primero.");
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Nombre de la entidad:");
        if (name != null && !name.isBlank()) {
            Entity entity = new Entity(name, EntityType.NPC, 0, 0);
            map.addEntity(entity);
            refresh();
            entityList.setSelectedValue(entity, true);
            mainFrame.getMapEditorPanel().repaint();
        }
    }

    private void removeEntity() {
        Entity entity = entityList.getSelectedValue();
        GameMap map = mainFrame.getCurrentMap();
        if (entity != null && map != null) {
            map.removeEntity(entity);
            refresh();
            mainFrame.getMapEditorPanel().repaint();
        }
    }

    private void showEntityDetails(Entity entity) {
        if (entity == null)
            return;
        updating = true;
        nameField.setText(entity.getName());
        typeCombo.setSelectedItem(entity.getType());
        xSpinner.setValue(entity.getX());
        ySpinner.setValue(entity.getY());
        hpSpinner.setValue(entity.getMaxHp());
        atkSpinner.setValue(entity.getAttack());
        defSpinner.setValue(entity.getDefense());
        spdSpinner.setValue(entity.getSpeed());
        expSpinner.setValue(entity.getExpReward());
        goldSpinner.setValue(entity.getGoldReward());
        eventIdField.setText(entity.getEventId() != null ? entity.getEventId() : "");
        dialogueArea.setText(String.join("\n", entity.getDialogueLines()));
        // Imagen
        if (entity.getImagePath() != null && !entity.getImagePath().isBlank()) {
            imagePathField.setText(entity.getImagePath());
            Image img = ImageCache.getImage(entity.getImagePath());
            if (img != null) {
                imagePreviewLabel.setIcon(new ImageIcon(img.getScaledInstance(56, 56, Image.SCALE_SMOOTH)));
                imagePreviewLabel.setText("");
            }
        } else {
            imagePathField.setText("");
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("Sin imagen");
        }
        updating = false;
    }

    private void applyChanges() {
        if (updating)
            return;
        Entity entity = entityList.getSelectedValue();
        if (entity == null)
            return;

        entity.setName(nameField.getText());
        entity.setType((EntityType) typeCombo.getSelectedItem());
        entity.setX((int) xSpinner.getValue());
        entity.setY((int) ySpinner.getValue());
        entity.setMaxHp((int) hpSpinner.getValue());
        entity.setHp((int) hpSpinner.getValue());
        entity.setAttack((int) atkSpinner.getValue());
        entity.setDefense((int) defSpinner.getValue());
        entity.setSpeed((int) spdSpinner.getValue());
        entity.setExpReward((int) expSpinner.getValue());
        entity.setGoldReward((int) goldSpinner.getValue());
        entity.setEventId(eventIdField.getText().trim().isBlank() ? null : eventIdField.getText().trim());

        // Imagen
        String imgPath = imagePathField.getText();
        entity.setImagePath(imgPath.isBlank() ? null : imgPath);

        String[] lines = dialogueArea.getText().split("\n");
        entity.getDialogueLines().clear();
        for (String line : lines) {
            if (!line.isBlank())
                entity.getDialogueLines().add(line.trim());
        }

        refresh();
        entityList.setSelectedValue(entity, true);
        mainFrame.getMapEditorPanel().repaint();
        mainFrame.setStatus("Entidad actualizada: " + entity.getName());
    }

    public void refresh() {
        listModel.clear();
        GameMap map = mainFrame.getCurrentMap();
        if (map != null) {
            for (Entity e : map.getEntities()) {
                listModel.addElement(e);
            }
        }
    }

    private void selectEntityImage() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar Imagen de Entidad (PNG/GIF)");
        fc.setFileFilter(new FileNameExtensionFilter("Imágenes (PNG, GIF)", "png", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            imagePathField.setText(file.getAbsolutePath());
            Image img = ImageCache.getImage(file.getAbsolutePath());
            if (img != null) {
                imagePreviewLabel.setIcon(new ImageIcon(img.getScaledInstance(56, 56, Image.SCALE_SMOOTH)));
                imagePreviewLabel.setText("");
            }
            applyChanges();
        }
    }

    private void clearEntityImage() {
        imagePathField.setText("");
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("Sin imagen");
        applyChanges();
    }

    public void setSelectedEntity(Entity entity) {
        entityList.setSelectedValue(entity, true);
    }
}
