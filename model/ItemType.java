/** @author María Fortuna */

package com.rpgmaker.model;

/**
 * Categorías de objetos disponibles en el RPG.
 */
public enum ItemType {
    ARMA("Arma"),
    ARMADURA("Armadura"),
    CASCO("Casco"),
    ACCESORIO("Accesorio"),
    CONSUMIBLE("Consumible"),
    OBJETO_CLAVE("Objeto Clave");

    private final String nombreVisible;

    ItemType(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getDisplayName() {
        return nombreVisible;
    }
}
