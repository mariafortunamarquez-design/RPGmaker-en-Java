/** @author María Fortuna */

package com.rpgmaker.model;

import java.awt.Color;

/**
 * Tipos de entidades que se pueden colocar en un mapa.
 */
public enum EntityType {
    NPC("NPC", new Color(0, 120, 215)),
    ENEMIGO("Enemigo", new Color(220, 20, 60)),
    OBJETO("Objeto", new Color(255, 165, 0)),
    COFRE("Cofre", new Color(218, 165, 32)),
    CARTEL("Cartel", new Color(139, 119, 101)),
    PUNTO_GUARDADO("Punto de Guardado", new Color(148, 103, 189));

    private final String nombreVisible;
    private final Color color;

    EntityType(String nombreVisible, Color color) {
        this.nombreVisible = nombreVisible;
        this.color = color;
    }

    public String getDisplayName() {
        return nombreVisible;
    }

    public Color getColor() {
        return color;
    }
}
