/** @author María Fortuna */

package com.rpgmaker.preview;

import com.rpgmaker.model.*;
import com.rpgmaker.util.ImageCache;
import com.rpgmaker.util.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel de vista previa que permite probar el mapa del juego en tiempo real.
 * El jugador puede moverse con las flechas o WASD e interactuar con
 * NPC/entidades.
 * Soporta zoom con Ctrl+Rueda, imágenes/texturas, inventario (I/Tab) y música.
 */
public class GamePreviewPanel extends JPanel {

    private int tileSize = 32;

    private GameProject project;
    private GameMap currentMap;
    private int playerX, playerY;
    private int playerHp, playerMaxHp;
    private int playerGold;
    private JLabel statusLabel;
    private JPanel renderPanel;

    // Menú de inventario
    private InventoryMenuPanel inventoryMenu;
    private boolean inventoryOpen = false;

    // Estado en tiempo real del juego
    private Map<String, Boolean> eventFlags = new HashMap<>();
    private List<String> inventoryItems = new ArrayList<>();

    // Reproductor de música
    private MusicPlayer musicPlayer;

    public GamePreviewPanel(GameProject project, GameMap startMap) {
        this.project = project;
        this.currentMap = startMap.deepCopy();
        this.playerX = project.getPlayerConfig().getStartX();
        this.playerY = project.getPlayerConfig().getStartY();
        this.playerMaxHp = project.getPlayerConfig().getMaxHp();
        this.playerHp = playerMaxHp;
        this.playerGold = project.getPlayerConfig().getStartingGold();
        this.musicPlayer = new MusicPlayer();
        if (project.getPlayerConfig().getStartingItemIds() != null) {
            this.inventoryItems = new ArrayList<>(project.getPlayerConfig().getStartingItemIds());
        }

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Panel de renderizado del juego con JLayeredPane para overlay de inventario
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBackground(Color.BLACK);
        layeredPane.setOpaque(true);

        renderPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderGame((Graphics2D) g);
            }
        };
        renderPanel.setBackground(Color.BLACK);
        renderPanel.setFocusable(true);
        renderPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                // Toggle inventario con I o Tab
                if (code == KeyEvent.VK_I || code == KeyEvent.VK_TAB) {
                    toggleInventory();
                    return;
                }
                if (!inventoryOpen && !combatOpen) {
                    handleInput(code);
                    renderPanel.repaint();
                }
            }
        });

        // Zoom con Ctrl + Rueda del ratón
        renderPanel.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    if (tileSize < 96) {
                        tileSize += 4;
                        renderPanel.repaint();
                    }
                } else {
                    if (tileSize > 12) {
                        tileSize -= 4;
                        renderPanel.repaint();
                    }
                }
                e.consume();
            }
        });

        // Manejar redimensionado del layeredPane
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = layeredPane.getSize();
                renderPanel.setBounds(0, 0, size.width, size.height);
                if (inventoryMenu != null) {
                    inventoryMenu.setBounds(0, 0, size.width, size.height);
                }
                if (combatPanel != null) {
                    combatPanel.setBounds(0, 0, size.width, size.height);
                }
            }
        });

        layeredPane.add(renderPanel, JLayeredPane.DEFAULT_LAYER);

        add(layeredPane, BorderLayout.CENTER);

        // Barra de estado
        statusLabel = new JLabel(getStatusText());
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(new Color(30, 30, 30));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(statusLabel, BorderLayout.SOUTH);

        // Panel de controles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        controlPanel.setBackground(new Color(30, 30, 30));

        JLabel controlsHelp = new JLabel(
                "[WASD/Flechas] Mover  |  [E] Interactuar  |  [I/Tab] Inventario  |  [Ctrl+Rueda] Zoom  |  [ESC] Cerrar");
        controlsHelp.setForeground(new Color(180, 180, 180));
        controlsHelp.setFont(new Font("SansSerif", Font.PLAIN, 11));
        controlPanel.add(controlsHelp);
        add(controlPanel, BorderLayout.NORTH);

        // Iniciar música si hay configurada
        startMusic();

        // Enfocar el panel de renderizado para el input
        SwingUtilities.invokeLater(renderPanel::requestFocusInWindow);
    }

    private void startMusic() {
        if (currentMap != null && currentMap.getBgmPath() != null
                && !currentMap.getBgmPath().isBlank()) {
            musicPlayer.play(currentMap.getBgmPath());
        }
    }

    /** Detener la música al cerrar la preview. */
    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    private void toggleInventory() {
        inventoryOpen = !inventoryOpen;
        JLayeredPane layered = (JLayeredPane) renderPanel.getParent();

        if (inventoryOpen) {
            inventoryMenu = new InventoryMenuPanel(project,
                    () -> toggleInventory(), // cerrar menú
                    () -> { // salir del juego
                        stopMusic();
                        Window w = SwingUtilities.getWindowAncestor(this);
                        if (w != null)
                            w.dispose();
                    });
            inventoryMenu.setMusicVolume(musicPlayer.getVolume());
            inventoryMenu.setOnVolumeChange(() -> {
                musicPlayer.setVolume(inventoryMenu.getMusicVolume());
            });
            inventoryMenu.setBounds(0, 0, layered.getWidth(), layered.getHeight());
            layered.add(inventoryMenu, JLayeredPane.POPUP_LAYER);
            layered.revalidate();
            layered.repaint();
        } else {
            if (inventoryMenu != null) {
                layered.remove(inventoryMenu);
                inventoryMenu = null;
                layered.revalidate();
                layered.repaint();
                renderPanel.requestFocusInWindow();
            }
        }
    }

    private void handleInput(int keyCode) {
        int newX = playerX, newY = playerY;

        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> newY--;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> newY++;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> newX--;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> newX++;
            case KeyEvent.VK_E -> {
                interactAt(playerX, playerY);
                return;
            }
            case KeyEvent.VK_ESCAPE -> {
                stopMusic();
                Window w = SwingUtilities.getWindowAncestor(this);
                if (w != null)
                    w.dispose();
                return;
            }
        }

        // Verificar límites y transitabilidad
        if (newX >= 0 && newX < currentMap.getWidth() &&
                newY >= 0 && newY < currentMap.getHeight()) {
            Tile tile = currentMap.getTile(newX, newY);
            if (tile != null && tile.isWalkable()) {
                playerX = newX;
                playerY = newY;

                // Verificar si hay entidad enemiga en la casilla
                Entity entity = currentMap.getEntityAt(playerX, playerY);
                if (entity != null && entity.getType() == EntityType.ENEMIGO) {
                    handleCombat(entity);
                }

                // Disparar eventos "Al Pisar"
                if (tile.getEventId() != null) {
                    executeEvent(tile.getEventId(), GameEvent.TriggerType.AL_PISAR);
                }
            }
        }

        statusLabel.setText(getStatusText());
    }

    private void interactAt(int x, int y) {
        // Verificar casillas adyacentes para interacción
        int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 }, { 0, 0 } };
        for (int[] d : dirs) {
            int cx = x + d[0], cy = y + d[1];
            Entity entity = currentMap.getEntityAt(cx, cy);
            if (entity != null) {
                if (entity.getEventId() != null) {
                    executeEvent(entity.getEventId(), GameEvent.TriggerType.AL_INTERACTUAR);
                } else {
                    // Lógica por defecto según tipo si no hay evento asignado
                    handleDefaultInteraction(entity);
                }
                statusLabel.setText(getStatusText());
                repaint();
                return;
            }

            // Si no hay entidad, verificar si la propia casilla tiene evento
            Tile t = currentMap.getTile(cx, cy);
            if (t != null && t.getEventId() != null) {
                executeEvent(t.getEventId(), GameEvent.TriggerType.AL_INTERACTUAR);
                statusLabel.setText(getStatusText());
                repaint();
                return;
            }
        }
        statusLabel.setText("No hay nada con que interactuar aquí.");
    }

    private void handleDefaultInteraction(Entity entity) {
        switch (entity.getType()) {
            case NPC -> {
                if (!entity.getDialogueLines().isEmpty()) {
                    String dialogue = String.join("\n\n", entity.getDialogueLines());
                    JOptionPane.showMessageDialog(this,
                            dialogue, entity.getName(), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            entity.getName() + " no tiene nada que decir.",
                            "Diálogo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            case COFRE -> {
                playerGold += entity.getGoldReward();
                JOptionPane.showMessageDialog(this,
                        "¡Abriste un cofre!\n+" + entity.getGoldReward() + " oro",
                        "Cofre", JOptionPane.INFORMATION_MESSAGE);
                currentMap.removeEntity(entity);
            }
            case CARTEL -> {
                String text = entity.getDialogueLines().isEmpty()
                        ? "El cartel está vacío."
                        : String.join("\n", entity.getDialogueLines());
                JOptionPane.showMessageDialog(this,
                        text, "Cartel", JOptionPane.INFORMATION_MESSAGE);
            }
            case PUNTO_GUARDADO -> {
                playerHp = playerMaxHp;
                JOptionPane.showMessageDialog(this,
                        "¡HP restaurado! Partida guardada.",
                        "Punto de Guardado", JOptionPane.INFORMATION_MESSAGE);
            }
            case OBJETO -> {
                JOptionPane.showMessageDialog(this,
                        "Encontraste: " + entity.getName(),
                        "Objeto", JOptionPane.INFORMATION_MESSAGE);
                currentMap.removeEntity(entity);
            }
            default -> {
            }
        }
    }

    private void executeEvent(String eventId, GameEvent.TriggerType trigger) {
        GameEvent event = project.getEvents().stream()
                .filter(e -> e.getId().equals(eventId))
                .findFirst().orElse(null);

        if (event == null)
            return;
        if (event.getTriggerType() != trigger)
            return;

        com.rpgmaker.model.event.EventGraph graph = event.getEventGraph();
        if (graph != null && graph.getStartNodeId() != null) {
            runEventGraph(graph, graph.getStartNodeId());
        }
    }

    private void runEventGraph(com.rpgmaker.model.event.EventGraph graph, String startNodeId) {
        String currentNodeId = startNodeId;
        while (currentNodeId != null) {
            com.rpgmaker.model.event.EventNode node = graph.getNode(currentNodeId);
            if (node == null)
                break;

            if ("Start".equals(node.getNodeType())) {
                currentNodeId = ((com.rpgmaker.model.event.StartNode) node).getNextNodeId();
            } else if (node instanceof com.rpgmaker.model.event.DialogNode dn) {
                JOptionPane.showMessageDialog(this, dn.getText(), "Evento", JOptionPane.INFORMATION_MESSAGE);
                currentNodeId = dn.getNextNodeId();
            } else if (node instanceof com.rpgmaker.model.event.ConditionNode cn) {
                boolean result = false;
                if (cn.getConditionType() == com.rpgmaker.model.event.ConditionNode.ConditionType.FLAG) {
                    boolean val = eventFlags.getOrDefault(cn.getTargetId(), false);
                    result = (val == cn.isExpectedValue());
                } else if (cn.getConditionType() == com.rpgmaker.model.event.ConditionNode.ConditionType.ITEM) {
                    boolean hasItem = inventoryItems.contains(cn.getTargetId());
                    result = (hasItem == cn.isExpectedValue());
                }
                currentNodeId = result ? cn.getTrueNextNodeId() : cn.getFalseNextNodeId();

            } else if (node instanceof com.rpgmaker.model.event.ActionNode an) {
                handleActionParams(an.getActionType(), an.getParameters());
                currentNodeId = an.getNextNodeId();
            } else {
                break;
            }
        }
    }

    private void handleActionParams(com.rpgmaker.model.ActionType type, java.util.Map<String, String> parameters) {
        switch (type) {
            case ESTABLECER_FLAG -> {
                String flagId = parameters.get("flagId");
                boolean val = Boolean.parseBoolean(parameters.getOrDefault("value", "true"));
                if (flagId != null)
                    eventFlags.put(flagId, val);
            }
            case DAR_OBJETO -> {
                String itemId = parameters.get("itemId");
                if (itemId != null) {
                    inventoryItems.add(itemId);
                    statusLabel.setText("Recibido objeto: " + itemId);
                }
            }
            case QUITAR_OBJETO -> {
                String itemId = parameters.get("itemId");
                if (itemId != null) {
                    inventoryItems.remove(itemId);
                }
            }
            case TELETRANSPORTAR -> {
                String mapId = parameters.get("mapId");
                int tx = Integer.parseInt(parameters.getOrDefault("x", "0"));
                int ty = Integer.parseInt(parameters.getOrDefault("y", "0"));

                GameMap nextMap = project.getMaps().stream()
                        .filter(m -> m.getId().equals(mapId))
                        .findFirst().orElse(null);

                if (nextMap != null) {
                    this.currentMap = nextMap.deepCopy();
                    this.playerX = tx;
                    this.playerY = ty;
                    startMusic();
                    repaint();
                    statusLabel.setText("Teletransportado a: " + nextMap.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Mapa destino '" + mapId + "' no encontrado.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            case DAR_ORO -> {
                int amount = Integer.parseInt(parameters.getOrDefault("amount", "0"));
                playerGold += amount;
                statusLabel.setText("Recibido: " + amount + " oro.");
            }
            case QUITAR_ORO -> {
                int amount = Integer.parseInt(parameters.getOrDefault("amount", "0"));
                playerGold = Math.max(0, playerGold - amount);
            }
            case CURAR_JUGADOR -> {
                int amount = Integer.parseInt(parameters.getOrDefault("amount", "0"));
                playerHp = Math.min(playerMaxHp, playerHp + amount);
                statusLabel.setText("Curado: " + amount + " HP");
            }
            case DAÑAR_JUGADOR -> {
                int amount = Integer.parseInt(parameters.getOrDefault("amount", "0"));
                playerHp = Math.max(0, playerHp - amount);
                if (playerHp == 0) {
                    JOptionPane.showMessageDialog(this, "¡Has muerto!", "Fin", JOptionPane.ERROR_MESSAGE);
                }
            }
            case INICIAR_BATALLA -> {
                String enemyIdAttr = parameters.get("enemyId");
                if (enemyIdAttr != null) {
                    Entity dummyEnemy = new Entity();
                    dummyEnemy.setName(enemyIdAttr);
                    dummyEnemy.setType(com.rpgmaker.model.EntityType.ENEMIGO);
                    dummyEnemy.setMaxHp(20);
                    dummyEnemy.setHp(20);
                    dummyEnemy.setAttack(5);
                    dummyEnemy.setDefense(2);
                    dummyEnemy.setExpReward(10);
                    dummyEnemy.setGoldReward(5);
                    handleCombat(dummyEnemy);
                }
            }
            default -> {
            }
        }
    }

    private CombatPanel combatPanel;
    private boolean combatOpen = false;

    private void handleCombat(Entity enemy) {
        if (enemy == null)
            return;
        combatOpen = true;
        JLayeredPane layered = (JLayeredPane) renderPanel.getParent();

        int pAtk = project.getPlayerConfig().getAttack();
        int pDef = project.getPlayerConfig().getDefense();
        int pSpd = project.getPlayerConfig().getSpeed();

        combatPanel = new CombatPanel(this, enemy, playerHp, playerMaxHp, pAtk, pDef, pSpd);
        combatPanel.setBounds(0, 0, layered.getWidth(), layered.getHeight());
        layered.add(combatPanel, JLayeredPane.MODAL_LAYER);
        layered.revalidate();
        layered.repaint();
        combatPanel.requestFocusInWindow();
    }

    public void onCombatEnd(boolean won, Entity enemy, int remainingHp) {
        combatOpen = false;
        this.playerHp = remainingHp;

        JLayeredPane layered = (JLayeredPane) renderPanel.getParent();
        if (combatPanel != null) {
            layered.remove(combatPanel);
            combatPanel = null;
        }
        layered.revalidate();
        layered.repaint();
        renderPanel.requestFocusInWindow();

        if (won) {
            if (enemy != null) { // Si enemy != null, lo derrotamos (no fue huida)
                JOptionPane.showMessageDialog(this,
                        "¡Victoria!\n+" + enemy.getExpReward() + " EXP\n+" + enemy.getGoldReward() + " oro");
                playerGold += enemy.getGoldReward();
                currentMap.removeEntity(enemy);
            }
        } else {
            JOptionPane.showMessageDialog(this, "¡Has sido derrotado!\n...Reviviendo con salud completa...", "Derrota",
                    JOptionPane.WARNING_MESSAGE);
            playerHp = playerMaxHp; // revivir básico
        }
        statusLabel.setText(getStatusText());
    }

    private void renderGame(Graphics2D g2) {
        if (currentMap == null)
            return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int panelW = renderPanel.getWidth();
        int panelH = renderPanel.getHeight();

        // Centrar vista en el jugador
        int offsetX = panelW / 2 - playerX * tileSize - tileSize / 2;
        int offsetY = panelH / 2 - playerY * tileSize - tileSize / 2;

        int mapW = currentMap.getWidth();
        int mapH = currentMap.getHeight();

        // Renderizar casillas
        for (int y = 0; y < mapH; y++) {
            for (int x = 0; x < mapW; x++) {
                Tile tile = currentMap.getTile(x, y);
                int px = offsetX + x * tileSize;
                int py = offsetY + y * tileSize;

                // Intentar dibujar imagen personalizada
                Image img = null;
                if (tile.getImagePath() != null) {
                    img = ImageCache.getImage(tile.getImagePath());
                }

                if (img != null) {
                    g2.drawImage(img, px, py, tileSize, tileSize, renderPanel);
                } else {
                    g2.setColor(tile.getType().getColor());
                    g2.fillRect(px, py, tileSize, tileSize);
                }

                // Overlay no-transitable
                if (!tile.isWalkable()) {
                    g2.setColor(new Color(0, 0, 0, 50));
                    g2.fillRect(px, py, tileSize, tileSize);
                }
            }
        }

        // Cuadrícula
        g2.setColor(new Color(0, 0, 0, 30));
        for (int x = 0; x <= mapW; x++)
            g2.drawLine(offsetX + x * tileSize, offsetY, offsetX + x * tileSize, offsetY + mapH * tileSize);
        for (int y = 0; y <= mapH; y++)
            g2.drawLine(offsetX, offsetY + y * tileSize, offsetX + mapW * tileSize, offsetY + y * tileSize);

        // Renderizar entidades
        for (Entity entity : currentMap.getEntities()) {
            int ex = offsetX + entity.getX() * tileSize;
            int ey = offsetY + entity.getY() * tileSize;
            int pad = 4;

            // Intentar dibujar imagen personalizada
            Image entityImg = null;
            if (entity.getImagePath() != null) {
                entityImg = ImageCache.getImage(entity.getImagePath());
            }

            if (entityImg != null) {
                g2.drawImage(entityImg, ex + 2, ey + 2, tileSize - 4, tileSize - 4, renderPanel);
            } else {
                g2.setColor(entity.getType().getColor());
                g2.fillRoundRect(ex + pad, ey + pad, tileSize - pad * 2, tileSize - pad * 2, 6, 6);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                String label = entity.getName().length() > 3
                        ? entity.getName().substring(0, 3)
                        : entity.getName();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, ex + (tileSize - fm.stringWidth(label)) / 2,
                        ey + tileSize / 2 + fm.getAscent() / 2 - 1);
            }
        }

        // Renderizar jugador
        int px = offsetX + playerX * tileSize;
        int py = offsetY + playerY * tileSize;
        g2.setColor(new Color(255, 215, 0));
        g2.fillOval(px + 4, py + 4, tileSize - 8, tileSize - 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("J", px + tileSize / 2 - 4, py + tileSize / 2 + 5);

        // Barra de vida del jugador
        int barW = 60, barH = 6;
        int barX = px + tileSize / 2 - barW / 2;
        int barY = py - 8;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barW, barH);
        float hpRatio = (float) playerHp / playerMaxHp;
        g2.setColor(hpRatio > 0.5f ? Color.GREEN : hpRatio > 0.25f ? Color.YELLOW : Color.RED);
        g2.fillRect(barX, barY, (int) (barW * hpRatio), barH);
    }

    private String getStatusText() {
        return String.format("HP: %d/%d | Oro: %d | Pos: (%d, %d) | Mapa: %s | Zoom: %d%%",
                playerHp, playerMaxHp, playerGold, playerX, playerY,
                currentMap != null ? currentMap.getName() : "-",
                tileSize * 100 / 32);
    }
}
