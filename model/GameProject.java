/** @author María Fortuna */

package com.rpgmaker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo raíz: contiene todos los datos del proyecto del juego.
 */
public class GameProject {
    private String name;
    private String author;
    private String description;
    private String version;

    private List<GameMap> maps;
    private List<Item> items;
    private List<GameEvent> events;
    private PlayerConfig playerConfig;

    // Flags / variables globales
    private Map<String, Boolean> flags;
    private Map<String, Integer> variables;

    // Ruta del archivo donde se guarda este proyecto
    private transient String filePath;

    public GameProject() {
        this.name = "Mi Juego RPG";
        this.author = "";
        this.description = "";
        this.version = "1.0";
        this.maps = new ArrayList<>();
        this.items = new ArrayList<>();
        this.events = new ArrayList<>();
        this.playerConfig = new PlayerConfig();
        this.flags = new HashMap<>();
        this.variables = new HashMap<>();
    }

    /** Crear un nuevo proyecto con un mapa inicial por defecto. */
    public static GameProject createDefault() {
        GameProject project = new GameProject();
        GameMap startMap = new GameMap("Aldea Inicial", 25, 20);
        project.maps.add(startMap);
        project.playerConfig.setStartingMapId(startMap.getId());
        return project;
    }


    public void addMap(GameMap map) {
        maps.add(map);
    }

    public void removeMap(GameMap map) {
        maps.remove(map);
    }

    public GameMap getMapById(String id) {
        for (GameMap m : maps) {
            if (m.getId().equals(id))
                return m;
        }
        return null;
    }


    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public Item getItemById(String id) {
        for (Item i : items) {
            if (i.getId().equals(id))
                return i;
        }
        return null;
    }


    public void addEvent(GameEvent event) {
        events.add(event);
    }

    public void removeEvent(GameEvent event) {
        events.remove(event);
    }

    public GameEvent getEventById(String id) {
        for (GameEvent e : events) {
            if (e.getId().equals(id))
                return e;
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<GameMap> getMaps() {
        return maps;
    }

    public void setMaps(List<GameMap> maps) {
        this.maps = maps;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<GameEvent> getEvents() {
        return events;
    }

    public void setEvents(List<GameEvent> events) {
        this.events = events;
    }

    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public void setPlayerConfig(PlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public void setFlags(Map<String, Boolean> flags) {
        this.flags = flags;
    }

    public Map<String, Integer> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Integer> variables) {
        this.variables = variables;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
