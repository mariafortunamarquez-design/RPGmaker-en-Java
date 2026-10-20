/** @author María Fortuna */

package com.rpgmaker.model;

import java.awt.Color;

/**
 * Enum que representa los diferentes tipos de casillas disponibles en el editor
 * de mapas.
 * Cada tipo tiene un nombre visible, color y transitabilidad por defecto.
 */
public enum TileType {
    HIERBA("Hierba", new Color(34, 139, 34), true),
    AGUA("Agua", new Color(30, 144, 255), false),
    MURO("Muro", new Color(105, 105, 105), false),
    TIERRA("Tierra", new Color(139, 119, 101), true),
    PIEDRA("Piedra", new Color(169, 169, 169), true),
    ARENA("Arena", new Color(238, 214, 175), true),
    BOSQUE("Bosque", new Color(0, 100, 0), false),
    PUERTA("Puerta", new Color(139, 90, 43), true),
    PUENTE("Puente", new Color(160, 120, 60), true),
    LAVA("Lava", new Color(207, 16, 32), false),
    HIELO("Hielo", new Color(176, 224, 230), true),
    SUELO_CUEVA("Suelo Cueva", new Color(85, 85, 85), true);

    private final String nombreVisible;
    private final Color color;
    private final boolean transitable;

    TileType(String nombreVisible, Color color, boolean transitable) {
        this.nombreVisible = nombreVisible;
        this.color = color;
        this.transitable = transitable;
    }

    public String getDisplayName() {
        return nombreVisible;
    }

    public Color getColor() {
        return color;
    }

    public boolean isDefaultWalkable() {
        return transitable;
    }
}
