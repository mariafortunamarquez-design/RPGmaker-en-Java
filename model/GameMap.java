/** @author María Fortuna */

package com.rpgmaker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa un mapa del juego con una cuadrícula de casillas y entidades
 * colocadas.
 */
public class GameMap {
    private String id;
    private String name;
    private int width;
    private int height;
    private Tile[][] tiles;
    private List<Entity> entities;

    // Conexiones de mapa
    private String northMapId;
    private String southMapId;
    private String eastMapId;
    private String westMapId;

    // Música de fondo
    private String bgmName;
    private String bgmPath; // Ruta al archivo WAV de música de fondo

    // Tasa de encuentros aleatorios (0 = sin encuentros)
    private int encounterRate;
    private List<String> encounterEnemyIds;

    public GameMap() {
        this("Mapa Sin Título", 20, 15);
    }

    public GameMap(String name, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.width = width;
        this.height = height;
        this.entities = new ArrayList<>();
        this.encounterEnemyIds = new ArrayList<>();
        this.encounterRate = 0;
        initTiles();
    }

    /** Inicializar todas las casillas como hierba. */
    private void initTiles() {
        tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile(TileType.HIERBA);
            }
        }
    }

    /** Redimensionar el mapa, preservando casillas existentes donde sea posible. */
    public void resize(int newWidth, int newHeight) {
        Tile[][] newTiles = new Tile[newHeight][newWidth];
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                if (y < height && x < width) {
                    newTiles[y][x] = tiles[y][x];
                } else {
                    newTiles[y][x] = new Tile(TileType.HIERBA);
                }
            }
        }
        this.tiles = newTiles;
        this.width = newWidth;
        this.height = newHeight;
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[y][x];
        }
        return null;
    }

    public void setTile(int x, int y, TileType type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[y][x].setType(type);
        }
    }

    /** Rellenar una región con un tipo de casilla (herramienta de relleno). */
    public void floodFill(int startX, int startY, TileType fillType) {
        if (startX < 0 || startX >= width || startY < 0 || startY >= height)
            return;
        TileType targetType = tiles[startY][startX].getType();
        if (targetType == fillType)
            return;
        floodFillRecursive(startX, startY, targetType, fillType);
    }

    private void floodFillRecursive(int x, int y, TileType target, TileType fill) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return;
        if (tiles[y][x].getType() != target)
            return;
        tiles[y][x].setType(fill);
        floodFillRecursive(x + 1, y, target, fill);
        floodFillRecursive(x - 1, y, target, fill);
        floodFillRecursive(x, y + 1, target, fill);
        floodFillRecursive(x, y - 1, target, fill);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public Entity getEntityAt(int x, int y) {
        for (Entity e : entities) {
            if (e.getX() == x && e.getY() == y)
                return e;
        }
        return null;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public String getNorthMapId() {
        return northMapId;
    }

    public void setNorthMapId(String northMapId) {
        this.northMapId = northMapId;
    }

    public String getSouthMapId() {
        return southMapId;
    }

    public void setSouthMapId(String southMapId) {
        this.southMapId = southMapId;
    }

    public String getEastMapId() {
        return eastMapId;
    }

    public void setEastMapId(String eastMapId) {
        this.eastMapId = eastMapId;
    }

    public String getWestMapId() {
        return westMapId;
    }

    public void setWestMapId(String westMapId) {
        this.westMapId = westMapId;
    }

    public String getBgmName() {
        return bgmName;
    }

    public void setBgmName(String bgmName) {
        this.bgmName = bgmName;
    }

    public String getBgmPath() {
        return bgmPath;
    }

    public void setBgmPath(String bgmPath) {
        this.bgmPath = bgmPath;
    }

    public int getEncounterRate() {
        return encounterRate;
    }

    public void setEncounterRate(int encounterRate) {
        this.encounterRate = encounterRate;
    }

    public List<String> getEncounterEnemyIds() {
        return encounterEnemyIds;
    }

    public void setEncounterEnemyIds(List<String> encounterEnemyIds) {
        this.encounterEnemyIds = encounterEnemyIds;
    }

    /**
     * Crea una copia profunda del mapa para previsualización sin afectar al
     * original.
     */
    public GameMap deepCopy() {
        GameMap copy = new GameMap(this.name, this.width, this.height);
        copy.id = this.id;
        copy.northMapId = this.northMapId;
        copy.southMapId = this.southMapId;
        copy.eastMapId = this.eastMapId;
        copy.westMapId = this.westMapId;
        copy.bgmName = this.bgmName;
        copy.bgmPath = this.bgmPath;
        copy.encounterRate = this.encounterRate;
        copy.encounterEnemyIds = new ArrayList<>(this.encounterEnemyIds);

        // Copiar casillas
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = this.tiles[y][x];
                Tile ct = copy.tiles[y][x];
                ct.setType(t.getType());
                ct.setWalkable(t.isWalkable());
                ct.setEventId(t.getEventId());
                ct.setImagePath(t.getImagePath());
            }
        }

        // Copiar entidades
        copy.entities.clear();
        for (Entity e : this.entities) {
            copy.entities.add(e.copy());
        }

        return copy;
    }

    @Override
    public String toString() {
        return name;
    }
}
