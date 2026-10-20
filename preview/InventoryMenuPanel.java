/** @author María Fortuna */

package com.rpgmaker.preview;

import com.rpgmaker.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de menú de inventario estilo RPG.
 * Contiene pestañas: Inventario, Equipamiento, Ajustes y Salir.
 */
public class InventoryMenuPanel extends JPanel {

    private GameProject project;
    private Runnable onClose;
    private Runnable onExit;
    private float musicVolume = 0.8f;
    private Runnable onVolumeChange;

    public InventoryMenuPanel(GameProject project, Runnable onClose, Runnable onExit) {
        this.project = project;
        this.onClose = onClose;
        this.onExit = onExit;

        setOpaque(false);
        setLayout(new GridBagLayout());

        // Panel central semi-transparente
        JPanel centerPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 20, 35, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(100, 130, 200, 150));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
            }
        };
        centerPanel.setOpaque(false);
        centerPanel.setPreferredSize(new Dimension(500, 400));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Título
        JLabel titleLabel = new JLabel("MENÚ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(220, 200, 120));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));
        centerPanel.add(titleLabel, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.setBackground(new Color(30, 30, 50));
        tabs.setForeground(Color.WHITE);

        tabs.addTab("Inventario", buildInventoryTab());
        tabs.addTab("Equipamiento", buildEquipmentTab());
        tabs.addTab("Ajustes", buildSettingsTab());
        tabs.addTab("Salir", buildExitTab());

        centerPanel.add(tabs, BorderLayout.CENTER);

        // Instrucciones
        JLabel helpLabel = new JLabel("Pulsa [I] o [Tab] para cerrar", SwingConstants.CENTER);
        helpLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(150, 150, 170));
        helpLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 2, 0));
        centerPanel.add(helpLabel, BorderLayout.SOUTH);

        add(centerPanel);
    }

    private JPanel buildInventoryTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        List<Item> items = project.getItems();

        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No hay objetos definidos en el proyecto.", SwingConstants.CENTER);
            emptyLabel.setForeground(new Color(180, 180, 180));
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
            panel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            DefaultListModel<String> model = new DefaultListModel<>();
            for (Item item : items) {
                String line = item.getName() + "  [" + item.getType().getDisplayName() + "]";
                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    line += "  — " + item.getDescription();
                }
                model.addElement(line);
            }
            JList<String> list = new JList<>(model);
            list.setBackground(new Color(35, 35, 55));
            list.setForeground(Color.WHITE);
            list.setFont(new Font("SansSerif", Font.PLAIN, 12));
            list.setSelectionBackground(new Color(70, 90, 140));
            JScrollPane scroll = new JScrollPane(list);
            scroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120)));
            panel.add(scroll, BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel buildEquipmentTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String[] slots = { "Arma", "Armadura", "Casco", "Accesorio" };
        ItemType[] slotTypes = { ItemType.ARMA, ItemType.ARMADURA, ItemType.CASCO, ItemType.ACCESORIO };

        for (int i = 0; i < slots.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            JLabel slotLabel = new JLabel(slots[i] + ":");
            slotLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            slotLabel.setForeground(new Color(200, 190, 140));
            panel.add(slotLabel, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;

            // Buscar item de ese tipo
            String itemName = "(vacío)";
            for (Item item : project.getItems()) {
                if (item.getType() == slotTypes[i]) {
                    itemName = item.getName();
                    break;
                }
            }

            JLabel itemLabel = new JLabel(itemName);
            itemLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            itemLabel.setForeground(itemName.equals("(vacío)")
                    ? new Color(120, 120, 140)
                    : Color.WHITE);
            panel.add(itemLabel, gbc);
        }

        // Espaciador
        gbc.gridx = 0;
        gbc.gridy = slots.length;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel buildSettingsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Volumen
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel volLabel = new JLabel("Volumen Música:");
        volLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        volLabel.setForeground(Color.WHITE);
        panel.add(volLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JSlider volumeSlider = new JSlider(0, 100, (int) (musicVolume * 100));
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.addChangeListener(e -> {
            musicVolume = volumeSlider.getValue() / 100f;
            if (onVolumeChange != null)
                onVolumeChange.run();
        });
        panel.add(volumeSlider, gbc);

        // Info del proyecto
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        JLabel infoLabel = new JLabel("Proyecto: " + project.getName());
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(170, 170, 190));
        panel.add(infoLabel, gbc);

        gbc.gridy = 2;
        JLabel versionLabel = new JLabel("Versión: " + project.getVersion());
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(170, 170, 190));
        panel.add(versionLabel, gbc);

        // Espaciador
        gbc.gridy = 3;
        gbc.weighty = 1;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel buildExitTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        btnPanel.setOpaque(false);

        JButton closeMenuBtn = new JButton("Cerrar Menú");
        closeMenuBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeMenuBtn.setBackground(new Color(60, 80, 130));
        closeMenuBtn.setForeground(Color.WHITE);
        closeMenuBtn.setFocusPainted(false);
        closeMenuBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeMenuBtn.addActionListener(e -> {
            if (onClose != null)
                onClose.run();
        });

        JButton exitGameBtn = new JButton("Salir del Juego");
        exitGameBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        exitGameBtn.setBackground(new Color(150, 40, 40));
        exitGameBtn.setForeground(Color.WHITE);
        exitGameBtn.setFocusPainted(false);
        exitGameBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitGameBtn.addActionListener(e -> {
            if (onExit != null)
                onExit.run();
        });

        btnPanel.add(closeMenuBtn);
        btnPanel.add(exitGameBtn);
        panel.add(btnPanel);

        return panel;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float vol) {
        this.musicVolume = vol;
    }

    public void setOnVolumeChange(Runnable callback) {
        this.onVolumeChange = callback;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Fondo semi-transparente oscuro
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}
