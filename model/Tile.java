/** @author María Fortuna */

package com.rpgmaker.model;

/**
 * Representa una casilla individual en un mapa del juego.
 */
public class Tile {
    private TileType type;
    private boolean walkable;
    private String eventId; // ID de un evento activado en esta casilla (puede ser null)
    private String imagePath; // Ruta a una imagen PNG/GIF personalizada (puede ser null)

    public Tile() {
        this(TileType.HIERBA);
    }

    public Tile(TileType type) {
        this.type = type;
        this.walkable = type.isDefaultWalkable();
        this.eventId = null;
    }


    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
        this.walkable = type.isDefaultWalkable();
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /** Copia profunda */
    public Tile copy() {
        Tile t = new Tile(this.type);
        t.walkable = this.walkable;
        t.eventId = this.eventId;
        t.imagePath = this.imagePath;
        return t;
    }
}
