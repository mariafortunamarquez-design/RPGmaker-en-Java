/** @author María Fortuna */

package com.rpgmaker.export;

import com.rpgmaker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Exporta un GameProject como un proyecto Java jugable independiente.
 * Genera archivos .java que se pueden compilar con javac y ejecutar
 * directamente.
 */
public class JavaCodeExporter {

    /**
     * Exportar el proyecto al directorio especificado.
     * Genera archivos planos Java que se compilan con javac.
     */
    public static void export(GameProject project, File outputDir) throws IOException {
        outputDir.mkdirs();

        writeFile(outputDir, "Main.java", generateMainClass(project));
        writeFile(outputDir, "GameData.java", generateGameData(project));
        writeFile(outputDir, "GameEngine.java", generateGameEngine());
        writeFile(outputDir, "compilar.bat", generateCompileScript());
        writeFile(outputDir, "ejecutar.bat", generateRunScript());
    }

    private static void writeFile(File dir, String filename, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(new File(dir, filename)), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }

    private static String generateMainClass(GameProject project) {
        return """
                import javax.swing.*;
                import java.awt.*;

                /**
                 * Punto de entrada del juego exportado.
                 * @author María Fortuna
                 */
                public class Main {
                    public static void main(String[] args) {
                        try {
                            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                    UIManager.setLookAndFeel(info.getClassName());
                                    UIManager.put("control", new Color(50, 50, 50));
                                    UIManager.put("nimbusBase", new Color(30, 30, 30));
                                    UIManager.put("nimbusLightBackground", new Color(45, 45, 45));
                                    UIManager.put("text", new Color(220, 220, 220));
                                    UIManager.put("nimbusSelectionBackground", new Color(70, 130, 180));
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error al establecer tema: " + e.getMessage());
                        }

                        SwingUtilities.invokeLater(() -> {
                            GameEngine engine = new GameEngine();
                            engine.setVisible(true);
                        });
                    }
                }
                """;
    }

    private static String generateGameData(GameProject project) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**\n");
        sb.append(" * Datos del juego generados automáticamente.\n");
        sb.append(" * @author María Fortuna\n");
        sb.append(" * Proyecto: ").append(project.getName()).append("\n");
        sb.append(" * Autor: ").append(project.getAuthor()).append("\n");
        sb.append(" */\n");
        sb.append("public class GameData {\n\n");

        sb.append("    public static final String GAME_NAME = \"").append(escape(project.getName())).append("\";\n");
        sb.append("    public static final String GAME_AUTHOR = \"").append(escape(project.getAuthor()))
                .append("\";\n");
        sb.append("    public static final String GAME_VERSION = \"").append(escape(project.getVersion()))
                .append("\";\n\n");

        // Configuración del jugador
        PlayerConfig pc = project.getPlayerConfig();
        sb.append("    // Configuración del jugador\n");
        sb.append("    public static final String PLAYER_NAME = \"").append(escape(pc.getName())).append("\";\n");
        sb.append("    public static final int PLAYER_MAX_HP = ").append(pc.getMaxHp()).append(";\n");
        sb.append("    public static final int PLAYER_ATTACK = ").append(pc.getAttack()).append(";\n");
        sb.append("    public static final int PLAYER_DEFENSE = ").append(pc.getDefense()).append(";\n");
        sb.append("    public static final int PLAYER_SPEED = ").append(pc.getSpeed()).append(";\n");
        sb.append("    public static final int PLAYER_GOLD = ").append(pc.getStartingGold()).append(";\n");
        sb.append("    public static final int PLAYER_START_X = ").append(pc.getStartX()).append(";\n");
        sb.append("    public static final int PLAYER_START_Y = ").append(pc.getStartY()).append(";\n\n");

        // Datos de mapas — tipo de casilla como enteros
        sb.append("    // Datos de mapas (índice ordinal de TileType)\n");
        sb.append("    public static final int MAP_COUNT = ").append(project.getMaps().size()).append(";\n\n");

        for (int m = 0; m < project.getMaps().size(); m++) {
            GameMap map = project.getMaps().get(m);
            sb.append("    // Mapa ").append(m).append(": ").append(map.getName()).append("\n");
            sb.append("    public static final String MAP_").append(m).append("_NAME = \"")
                    .append(escape(map.getName())).append("\";\n");
            sb.append("    public static final int MAP_").append(m).append("_W = ").append(map.getWidth())
                    .append(";\n");
            sb.append("    public static final int MAP_").append(m).append("_H = ").append(map.getHeight())
                    .append(";\n");
            sb.append("    public static final int[] MAP_").append(m).append("_TILES = {");

            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    if (x > 0 || y > 0)
                        sb.append(",");
                    sb.append(map.getTile(x, y).getType().ordinal());
                }
            }
            sb.append("};\n");

            // Datos de entidades
            sb.append("    public static final int MAP_").append(m).append("_ENTITY_COUNT = ")
                    .append(map.getEntities().size()).append(";\n");
            for (int e = 0; e < map.getEntities().size(); e++) {
                Entity entity = map.getEntities().get(e);
                String ePrefix = "MAP_" + m + "_ENTITY_" + e;
                sb.append("    public static final String ").append(ePrefix).append("_NAME = \"")
                        .append(escape(entity.getName())).append("\";\n");
                sb.append("    public static final int ").append(ePrefix).append("_TYPE = ")
                        .append(entity.getType().ordinal()).append(";\n");
                sb.append("    public static final int ").append(ePrefix).append("_X = ").append(entity.getX())
                        .append(";\n");
                sb.append("    public static final int ").append(ePrefix).append("_Y = ").append(entity.getY())
                        .append(";\n");
                sb.append("    public static final int ").append(ePrefix).append("_HP = ").append(entity.getMaxHp())
                        .append(";\n");
                sb.append("    public static final int ").append(ePrefix).append("_ATK = ").append(entity.getAttack())
                        .append(";\n");
                sb.append("    public static final int ").append(ePrefix).append("_DEF = ").append(entity.getDefense())
                        .append(";\n");
            }
            sb.append("\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    private static String generateGameEngine() {
        return """
                import javax.swing.*;
                import java.awt.*;
                import java.awt.event.*;

                /**
                 * Motor de juego simple que lee datos de GameData y permite al jugador moverse por el mapa.
                 * @author María Fortuna
                 */
                public class GameEngine extends JFrame {

                    private static final int TILE_SIZE = 32;

                    // Colores de tipos de casilla (mismo orden que TileType en el editor)
                    private static final Color[] TILE_COLORS = {
                        new Color(34, 139, 34),   // Hierba
                        new Color(30, 144, 255),  // Agua
                        new Color(105, 105, 105), // Muro
                        new Color(139, 119, 101), // Tierra
                        new Color(169, 169, 169), // Piedra
                        new Color(238, 214, 175), // Arena
                        new Color(0, 100, 0),     // Bosque
                        new Color(139, 90, 43),   // Puerta
                        new Color(160, 120, 60),  // Puente
                        new Color(207, 16, 32),   // Lava
                        new Color(176, 224, 230), // Hielo
                        new Color(85, 85, 85)     // Suelo Cueva
                    };

                    // Transitabilidad por defecto de cada tipo de casilla
                    private static final boolean[] TILE_WALKABLE = {
                        true, false, false, true, true, true, false, true, true, false, true, true
                    };

                    private int playerX, playerY;
                    private int currentMap = 0;
                    private int playerHp;
                    private int playerGold;

                    private JPanel gamePanel;
                    private JLabel statusLabel;

                    public GameEngine() {
                        super(GameData.GAME_NAME);
                        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        playerX = GameData.PLAYER_START_X;
                        playerY = GameData.PLAYER_START_Y;
                        playerHp = GameData.PLAYER_MAX_HP;
                        playerGold = GameData.PLAYER_GOLD;

                        gamePanel = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                drawMap(g);
                            }
                        };
                        gamePanel.setBackground(Color.BLACK);
                        gamePanel.setFocusable(true);
                        gamePanel.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyPressed(KeyEvent e) {
                                handleKey(e.getKeyCode());
                            }
                        });
                        add(gamePanel, BorderLayout.CENTER);

                        statusLabel = new JLabel("HP: " + playerHp + " | Oro: " + playerGold + " | " + GameData.PLAYER_NAME);
                        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                        add(statusLabel, BorderLayout.SOUTH);

                        pack();
                        setSize(800, 640);
                        setLocationRelativeTo(null);
                        gamePanel.requestFocusInWindow();
                    }

                    private void handleKey(int key) {
                        int newX = playerX, newY = playerY;
                        switch (key) {
                            case KeyEvent.VK_W, KeyEvent.VK_UP -> newY--;
                            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> newY++;
                            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> newX--;
                            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> newX++;
                        }

                        int[] tiles = getMapTiles(currentMap);
                        int mapW = getMapWidth(currentMap);
                        int mapH = getMapHeight(currentMap);

                        if (newX >= 0 && newX < mapW && newY >= 0 && newY < mapH) {
                            int tileType = tiles[newY * mapW + newX];
                            if (tileType >= 0 && tileType < TILE_WALKABLE.length && TILE_WALKABLE[tileType]) {
                                playerX = newX;
                                playerY = newY;
                            }
                        }

                        statusLabel.setText("HP: " + playerHp + " | Oro: " + playerGold +
                            " | Pos: (" + playerX + "," + playerY + ") | " + getMapName(currentMap));
                        gamePanel.repaint();
                    }

                    private void drawMap(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        int[] tiles = getMapTiles(currentMap);
                        int mapW = getMapWidth(currentMap);
                        int mapH = getMapHeight(currentMap);

                        // Calcular desplazamiento para centrar al jugador
                        int offsetX = gamePanel.getWidth() / 2 - playerX * TILE_SIZE - TILE_SIZE / 2;
                        int offsetY = gamePanel.getHeight() / 2 - playerY * TILE_SIZE - TILE_SIZE / 2;

                        // Dibujar casillas
                        for (int y = 0; y < mapH; y++) {
                            for (int x = 0; x < mapW; x++) {
                                int type = tiles[y * mapW + x];
                                Color c = (type >= 0 && type < TILE_COLORS.length) ? TILE_COLORS[type] : Color.MAGENTA;
                                g2.setColor(c);
                                g2.fillRect(offsetX + x * TILE_SIZE, offsetY + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                            }
                        }

                        // Dibujar cuadrícula
                        g2.setColor(new Color(0, 0, 0, 40));
                        for (int x = 0; x <= mapW; x++)
                            g2.drawLine(offsetX + x * TILE_SIZE, offsetY, offsetX + x * TILE_SIZE, offsetY + mapH * TILE_SIZE);
                        for (int y = 0; y <= mapH; y++)
                            g2.drawLine(offsetX, offsetY + y * TILE_SIZE, offsetX + mapW * TILE_SIZE, offsetY + y * TILE_SIZE);

                        // Dibujar jugador
                        g2.setColor(new Color(255, 215, 0));
                        g2.fillOval(offsetX + playerX * TILE_SIZE + 4, offsetY + playerY * TILE_SIZE + 4,
                            TILE_SIZE - 8, TILE_SIZE - 8);
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                        g2.drawString("P", offsetX + playerX * TILE_SIZE + 12, offsetY + playerY * TILE_SIZE + 20);
                    }

                    private int[] getMapTiles(int idx) {
                        switch (idx) {
                            case 0: return GameData.MAP_0_TILES;
                            default: return GameData.MAP_0_TILES;
                        }
                    }

                    private int getMapWidth(int idx) {
                        switch (idx) {
                            case 0: return GameData.MAP_0_W;
                            default: return GameData.MAP_0_W;
                        }
                    }

                    private int getMapHeight(int idx) {
                        switch (idx) {
                            case 0: return GameData.MAP_0_H;
                            default: return GameData.MAP_0_H;
                        }
                    }

                    private String getMapName(int idx) {
                        switch (idx) {
                            case 0: return GameData.MAP_0_NAME;
                            default: return "Mapa " + idx;
                        }
                    }
                }
                """;
    }

    private static String generateCompileScript() {
        return "@echo off\r\nrem Creado con RPG Maker por María Fortuna\r\nif not exist classes mkdir classes\r\necho Compilando juego...\r\njavac -encoding UTF-8 -d classes *.java\r\nif %ERRORLEVEL%==0 (\r\n    echo Compilacion exitosa!\r\n) else (\r\n    echo Error en la compilacion.\r\n)\r\npause\r\n";
    }

    private static String generateRunScript() {
        return "@echo off\r\nrem Creado con RPG Maker por María Fortuna\r\njava -cp classes Main\r\npause\r\n";
    }

    private static String escape(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
