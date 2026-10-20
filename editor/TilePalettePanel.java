/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.model.TileType;
import com.rpgmaker.util.ImageCache;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Panel de paleta que muestra todos los tipos de casillas disponibles como
 * muestras de color.
 * Los usuarios hacen clic en una muestra para seleccionarla para pintar.
 * Permite importar texturas personalizadas (PNG/GIF).
 */
public class TilePalettePanel extends JPanel {

    private MainFrame mainFrame;
    private TileType selectedTileType = TileType.HIERBA;
    private String selectedTexturePath = null; // Ruta de la textura personalizada
    private JLabel texturePreview;
    private JLabel textureNameLabel;

    public TilePalettePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Panel de casillas
        JPanel tilesPanel = new JPanel(new GridLayout(0, 2, 6, 6));
        buildPalette(tilesPanel);
        JScrollPane scrollPane = new JScrollPane(tilesPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tipos de Casilla"));
        add(scrollPane, BorderLayout.CENTER);

        // Panel de textura personalizada
        JPanel texturePanel = new JPanel(new BorderLayout(4, 4));
        texturePanel.setBorder(BorderFactory.createTitledBorder("Textura Personalizada"));

        texturePreview = new JLabel("Sin textura", SwingConstants.CENTER);
        texturePreview.setPreferredSize(new Dimension(80, 80));
        texturePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        texturePanel.add(texturePreview, BorderLayout.CENTER);

        textureNameLabel = new JLabel("Ninguna", SwingConstants.CENTER);
        textureNameLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        texturePanel.add(textureNameLabel, BorderLayout.NORTH);

        JPanel textureBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
        JButton importBtn = new JButton("Importar...");
        importBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        importBtn.addActionListener(e -> importTexture());
        JButton clearBtn = new JButton("Quitar");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clearBtn.addActionListener(e -> clearTexture());
        textureBtnPanel.add(importBtn);
        textureBtnPanel.add(clearBtn);
        texturePanel.add(textureBtnPanel, BorderLayout.SOUTH);

        add(texturePanel, BorderLayout.SOUTH);
    }

    private void buildPalette(JPanel container) {
        container.removeAll();
        for (TileType type : TileType.values()) {
            JPanel swatch = createSwatch(type);
            container.add(swatch);
        }
        container.revalidate();
        container.repaint();
    }

    private JPanel createSwatch(TileType type) {
        JPanel swatch = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Rellenar con color de casilla
                g2.setColor(type.getColor());
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 30, 8, 8);

                // Indicador de transitabilidad
                g2.setColor(type.isDefaultWalkable() ? new Color(0, 200, 0) : new Color(200, 0, 0));
                g2.fillOval(getWidth() - 16, 6, 10, 10);

                // Borde de selección
                if (type == selectedTileType) {
                    g2.setColor(new Color(80, 180, 255));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 30, 8, 8);
                }
            }
        };

        swatch.setPreferredSize(new Dimension(100, 70));
        swatch.setToolTipText(type.getDisplayName() +
                (type.isDefaultWalkable() ? " (transitable)" : " (bloqueado)"));

        JLabel label = new JLabel(type.getDisplayName(), SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        swatch.add(label, BorderLayout.SOUTH);

        swatch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        swatch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedTileType = type;
                mainFrame.setStatus("Casilla seleccionada: " + type.getDisplayName());
                TilePalettePanel.this.repaint();
            }
        });

        return swatch;
    }

    private void importTexture() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar Textura (PNG/GIF)");
        fc.setFileFilter(new FileNameExtensionFilter("Imágenes (PNG, GIF)", "png", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            selectedTexturePath = file.getAbsolutePath();

            // Mostrar miniatura
            Image img = ImageCache.getImage(selectedTexturePath);
            if (img != null) {
                ImageIcon icon = new ImageIcon(img.getScaledInstance(70, 70, Image.SCALE_SMOOTH));
                texturePreview.setIcon(icon);
                texturePreview.setText("");
            }
            textureNameLabel.setText(file.getName());
            mainFrame.setStatus("Textura seleccionada: " + file.getName());
        }
    }

    private void clearTexture() {
        selectedTexturePath = null;
        texturePreview.setIcon(null);
        texturePreview.setText("Sin textura");
        textureNameLabel.setText("Ninguna");
        mainFrame.setStatus("Textura personalizada eliminada.");
    }

    public TileType getSelectedTileType() {
        return selectedTileType;
    }

    public void setSelectedTileType(TileType type) {
        this.selectedTileType = type;
        repaint();
    }

    /**
     * Devuelve la ruta de la textura personalizada seleccionada, o null si no hay.
     */
    public String getSelectedTexturePath() {
        return selectedTexturePath;
    }
}
