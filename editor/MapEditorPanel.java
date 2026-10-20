/** @author María Fortuna */

package com.rpgmaker.editor;

import com.rpgmaker.model.*;
import com.rpgmaker.util.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Canvas principal de edición de mapas. Renderiza la cuadrícula de casillas y
 * entidades,
 * maneja pintar, borrar, rellenar y colocación de entidades.
 */
public class MapEditorPanel extends JPanel {

    public enum Tool {
        PAINT, ERASE, FILL, ENTITY, SELECT
    }

    private MainFrame mainFrame;
    private GameMap currentMap;
    private Tool currentTool = Tool.PAINT;
    private int tileSize = 32;
    private boolean showGrid = true;
    private boolean painting = false;

    // Para colocación de entidades
    private Entity selectedEntity;
    private Entity draggingEntity;
    private int dragOriginX, dragOriginY;
    private boolean isDragging = false;

    // Posición del cursor
    private int hoverX = -1, hoverY = -1;

    public MapEditorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(new Color(40, 40, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePress(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDragging) {
                    handleMouseRelease(e);
                }
                painting = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDrag(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateHover(e);
            }
        });

        // Zoom con Ctrl + Rueda del ratón
        addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
                e.consume();
            }
        });

        // Atajos de teclado
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1 -> setCurrentTool(Tool.PAINT);
                    case KeyEvent.VK_2 -> setCurrentTool(Tool.ERASE);
                    case KeyEvent.VK_3 -> setCurrentTool(Tool.FILL);
                    case KeyEvent.VK_4 -> setCurrentTool(Tool.ENTITY);
                    case KeyEvent.VK_5 -> setCurrentTool(Tool.SELECT);
                    case KeyEvent.VK_G -> {
                        showGrid = !showGrid;
                        repaint();
                    }
                    case KeyEvent.VK_PLUS, KeyEvent.VK_EQUALS -> zoomIn();
                    case KeyEvent.VK_MINUS -> zoomOut();
                }
            }
        });
    }

    private void handleMousePress(MouseEvent e) {
        if (currentMap == null)
            return;
        int tx = e.getX() / tileSize;
        int ty = e.getY() / tileSize;

        if (tx < 0 || tx >= currentMap.getWidth() || ty < 0 || ty >= currentMap.getHeight())
            return;

        if (SwingUtilities.isRightMouseButton(e)) {
            showTileContextMenu(e, tx, ty);
            return;
        }

        switch (currentTool) {
            case PAINT -> {
                painting = true;
                paintTile(tx, ty);
            }
            case ERASE -> {
                painting = true;
                currentMap.setTile(tx, ty, TileType.HIERBA);
                // También quitar la imagen personalizada
                Tile t = currentMap.getTile(tx, ty);
                if (t != null)
                    t.setImagePath(null);
                repaint();
            }
            case FILL -> {
                currentMap.floodFill(tx, ty, mainFrame.getSelectedTileType());
                repaint();
            }
            case ENTITY -> {
                placeEntity(tx, ty);
            }
            case SELECT -> {
                selectAt(tx, ty);
            }
        }
    }

    private void handleMouseDrag(MouseEvent e) {
        if (currentMap == null)
            return;

        int tx = e.getX() / tileSize;
        int ty = e.getY() / tileSize;

        if (isDragging && draggingEntity != null) {
            if (tx >= 0 && tx < currentMap.getWidth() && ty >= 0 && ty < currentMap.getHeight()) {
                draggingEntity.setX(tx);
                draggingEntity.setY(ty);
                mainFrame.setStatus("Moviendo: " + draggingEntity.getName() + " a (" + tx + ", " + ty + ")");
                repaint();
            }
            return;
        }

        if (!painting)
            return;

        switch (currentTool) {
            case PAINT -> paintTile(tx, ty);
            case ERASE -> {
                currentMap.setTile(tx, ty, TileType.HIERBA);
                Tile t = currentMap.getTile(tx, ty);
                if (t != null)
                    t.setImagePath(null);
                repaint();
            }
            default -> {
                /* FILL, ENTITY, SELECT no soportan arrastre */ }
        }
        updateHover(e);
    }

    private void updateHover(MouseEvent e) {
        int newX = e.getX() / tileSize;
        int newY = e.getY() / tileSize;
        if (newX != hoverX || newY != hoverY) {
            hoverX = newX;
            hoverY = newY;
            if (currentMap != null && hoverX >= 0 && hoverX < currentMap.getWidth()
                    && hoverY >= 0 && hoverY < currentMap.getHeight()) {
                Tile t = currentMap.getTile(hoverX, hoverY);
                mainFrame.setStatus(String.format("(%d, %d) — %s | %s",
                        hoverX, hoverY,
                        t.getType().getDisplayName(),
                        t.isWalkable() ? "Transitable" : "Bloqueado"));
            }
            repaint();
        }
    }

    private void paintTile(int tx, int ty) {
        TileType selected = mainFrame.getSelectedTileType();
        if (selected != null) {
            currentMap.setTile(tx, ty, selected);
            // Aplicar textura personalizada si hay una seleccionada en la paleta
            String texturePath = mainFrame.getSelectedTexturePath();
            if (texturePath != null) {
                Tile t = currentMap.getTile(tx, ty);
                if (t != null)
                    t.setImagePath(texturePath);
            }
            repaint();
        }
    }

    private void placeEntity(int tx, int ty) {
        Entity existing = currentMap.getEntityAt(tx, ty);
        if (existing != null) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "La entidad '" + existing.getName() + "' ya está aquí.\n¿Eliminarla?",
                    "Entidad", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                currentMap.removeEntity(existing);
                repaint();
            }
            return;
        }

        String[] options = { "NPC", "Enemigo", "Cofre", "Cartel", "Objeto", "Punto Guardado" };
        String choice = (String) JOptionPane.showInputDialog(this,
                "Elige tipo de entidad:", "Colocar Entidad",
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice != null) {
            EntityType type = switch (choice) {
                case "Enemigo" -> EntityType.ENEMIGO;
                case "Cofre" -> EntityType.COFRE;
                case "Cartel" -> EntityType.CARTEL;
                case "Objeto" -> EntityType.OBJETO;
                case "Punto Guardado" -> EntityType.PUNTO_GUARDADO;
                default -> EntityType.NPC;
            };

            String defaultName = choice + " " + (currentMap.getEntities().size() + 1);
            String defaultId = choice.toLowerCase() + "_" + (currentMap.getEntities().size() + 1);

            JTextField nameField = new JTextField(defaultName, 15);
            JTextField idField = new JTextField(defaultId, 15);

            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 4, 4));
            inputPanel.add(new JLabel("Nombre:"));
            inputPanel.add(nameField);
            inputPanel.add(new JLabel("ID Único:"));
            inputPanel.add(idField);

            int result = JOptionPane.showConfirmDialog(this, inputPanel,
                    "Configurar Nueva Entidad", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String id = idField.getText().trim();

                if (name.isEmpty())
                    name = defaultName;
                if (id.isEmpty())
                    id = java.util.UUID.randomUUID().toString();

                Entity entity = new Entity(name, type, tx, ty);
                entity.setId(id);
                currentMap.addEntity(entity);
                mainFrame.setStatus("Colocado: " + entity.getName() + " (ID: " + id + ")");
                repaint();
            }
        }
    }

    private void selectAt(int tx, int ty) {
        Entity entity = currentMap.getEntityAt(tx, ty);
        if (entity != null) {
            selectedEntity = entity;
            draggingEntity = entity;
            isDragging = true;
            dragOriginX = tx;
            dragOriginY = ty;
            mainFrame.showEntityTab();
            mainFrame.setStatus("Seleccionado: " + entity + " (Arrastra para mover)");
        } else {
            selectedEntity = null;
            Tile tile = currentMap.getTile(tx, ty);
            if (tile != null) {
                mainFrame.setStatus(String.format("Casilla (%d,%d): %s, transitable=%b",
                        tx, ty, tile.getType().getDisplayName(), tile.isWalkable()));
            }
        }
        repaint();
    }

    private void handleMouseRelease(MouseEvent e) {
        if (isDragging && draggingEntity != null) {
            int tx = e.getX() / tileSize;
            int ty = e.getY() / tileSize;

            if (tx < 0 || tx >= currentMap.getWidth() || ty < 0 || ty >= currentMap.getHeight()) {
                // Devolver a origen si sale del mapa
                draggingEntity.setX(dragOriginX);
                draggingEntity.setY(dragOriginY);
            } else {
                // Verificar si hay otra entidad en el destino (opcional, dejamos que se solapen
                // por ahora o evitamos)
                Entity other = currentMap.getEntityAt(tx, ty);
                if (other != null && other != draggingEntity) {
                    // Si hay otra, devolvemos a origen o permitimos? El usuario pide simplificar,
                    // mejor evitar colisiones si es posible
                    // Pero por ahora permitimos o simplemente solapamos.
                }
                draggingEntity.setX(tx);
                draggingEntity.setY(ty);
                mainFrame.setStatus("Entidad movida a (" + tx + ", " + ty + ")");
            }
            mainFrame.getEntityEditorPanel().refresh(); // Actualizar spinners en el panel lateral
            mainFrame.getEntityEditorPanel().setSelectedEntity(draggingEntity); // Asegurar que sigue seleccionada
        }
        isDragging = false;
        draggingEntity = null;
        repaint();
    }

    private void showTileContextMenu(MouseEvent e, int tx, int ty) {
        JPopupMenu popup = new JPopupMenu();
        Tile tile = currentMap.getTile(tx, ty);

        JCheckBoxMenuItem walkableItem = new JCheckBoxMenuItem("Transitable", tile.isWalkable());
        walkableItem.addActionListener(ev -> {
            tile.setWalkable(!tile.isWalkable());
            repaint();
        });

        JMenuItem setEventItem = new JMenuItem("Asignar Evento...");
        setEventItem.addActionListener(ev -> {
            String eventId = JOptionPane.showInputDialog(this, "ID del Evento:", tile.getEventId());
            if (eventId != null) {
                tile.setEventId(eventId.isBlank() ? null : eventId);
            }
        });

        // Opción para asignar imagen a la casilla
        JMenuItem setImageItem = new JMenuItem("Asignar Imagen...");
        setImageItem.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imágenes (PNG, GIF)", "png", "gif"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tile.setImagePath(fc.getSelectedFile().getAbsolutePath());
                repaint();
            }
        });

        JMenuItem clearImageItem = new JMenuItem("Quitar Imagen");
        clearImageItem.setEnabled(tile.getImagePath() != null);
        clearImageItem.addActionListener(ev -> {
            tile.setImagePath(null);
            repaint();
        });

        Entity entityHere = currentMap.getEntityAt(tx, ty);
        JMenuItem entityItem;
        if (entityHere != null) {
            entityItem = new JMenuItem("Eliminar Entidad: " + entityHere.getName());
            entityItem.addActionListener(ev -> {
                currentMap.removeEntity(entityHere);
                repaint();
            });
        } else {
            entityItem = new JMenuItem("Colocar Entidad Aquí...");
            entityItem.addActionListener(ev -> placeEntity(tx, ty));
        }

        popup.add(walkableItem);
        popup.add(setEventItem);
        popup.addSeparator();
        popup.add(setImageItem);
        popup.add(clearImageItem);
        popup.addSeparator();
        popup.add(entityItem);
        popup.show(this, e.getX(), e.getY());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentMap == null) {
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.drawString("No hay mapa seleccionado. Crea o selecciona un mapa de la lista.", 50, 50);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int mapW = currentMap.getWidth();
        int mapH = currentMap.getHeight();

        // Dibujar casillas
        for (int y = 0; y < mapH; y++) {
            for (int x = 0; x < mapW; x++) {
                Tile tile = currentMap.getTile(x, y);
                int px = x * tileSize;
                int py = y * tileSize;

                // Intentar dibujar imagen personalizada
                Image img = null;
                if (tile.getImagePath() != null) {
                    img = ImageCache.getImage(tile.getImagePath());
                }

                if (img != null) {
                    g2.drawImage(img, px, py, tileSize, tileSize, this);
                } else {
                    // Rellenar color de casilla
                    g2.setColor(tile.getType().getColor());
                    g2.fillRect(px, py, tileSize, tileSize);
                }

                // Superposición no-transitable
                if (!tile.isWalkable() && tile.getType().isDefaultWalkable()) {
                    g2.setColor(new Color(255, 0, 0, 40));
                    g2.fillRect(px, py, tileSize, tileSize);
                }

                // Indicador de evento
                if (tile.getEventId() != null) {
                    g2.setColor(new Color(255, 255, 0, 120));
                    g2.fillOval(px + tileSize - 10, py + 2, 8, 8);
                }
            }
        }

        // Dibujar cuadrícula
        if (showGrid) {
            g2.setColor(new Color(0, 0, 0, 60));
            for (int x = 0; x <= mapW; x++) {
                g2.drawLine(x * tileSize, 0, x * tileSize, mapH * tileSize);
            }
            for (int y = 0; y <= mapH; y++) {
                g2.drawLine(0, y * tileSize, mapW * tileSize, y * tileSize);
            }
        }

        // Dibujar entidades
        for (Entity entity : currentMap.getEntities()) {
            int ex = entity.getX() * tileSize;
            int ey = entity.getY() * tileSize;
            int padding = 4;

            // Intentar dibujar imagen personalizada del entity
            Image entityImg = null;
            if (entity.getImagePath() != null) {
                entityImg = ImageCache.getImage(entity.getImagePath());
            }

            if (entityImg != null) {
                g2.drawImage(entityImg, ex + 2, ey + 2, tileSize - 4, tileSize - 4, this);
            } else {
                g2.setColor(entity.getType().getColor());
                g2.fillRoundRect(ex + padding, ey + padding,
                        tileSize - padding * 2, tileSize - padding * 2, 6, 6);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                String label = entity.getName().length() > 4
                        ? entity.getName().substring(0, 4)
                        : entity.getName();
                FontMetrics fm = g2.getFontMetrics();
                int textX = ex + (tileSize - fm.stringWidth(label)) / 2;
                int textY = ey + tileSize / 2 + fm.getAscent() / 2 - 1;
                g2.drawString(label, textX, textY);
            }

            // Resaltado de selección
            if (entity == selectedEntity) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(ex + 2, ey + 2, tileSize - 4, tileSize - 4, 6, 6);
                g2.setStroke(new BasicStroke(1));
            }
        }

        // Dibujar resaltado del cursor
        if (hoverX >= 0 && hoverX < mapW && hoverY >= 0 && hoverY < mapH) {
            g2.setColor(new Color(255, 255, 255, 80));
            g2.drawRect(hoverX * tileSize, hoverY * tileSize, tileSize - 1, tileSize - 1);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (currentMap != null) {
            return new Dimension(currentMap.getWidth() * tileSize + 1,
                    currentMap.getHeight() * tileSize + 1);
        }
        return new Dimension(800, 600);
    }

    public void setCurrentMap(GameMap map) {
        this.currentMap = map;
        this.selectedEntity = null;
        revalidate();
        repaint();
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentTool(Tool tool) {
        this.currentTool = tool;
        mainFrame.setStatus("Herramienta: " + tool.name());
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Entity entity) {
        this.selectedEntity = entity;
        repaint();
    }

    public void zoomIn() {
        if (tileSize < 96) {
            tileSize += 8;
            revalidate();
            repaint();
        }
    }

    public void zoomOut() {
        if (tileSize > 16) {
            tileSize -= 8;
            revalidate();
            repaint();
        }
    }
}
