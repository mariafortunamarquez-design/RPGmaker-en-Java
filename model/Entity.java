/** @author María Fortuna */

package com.rpgmaker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa una entidad (NPC, enemigo, objeto, etc.) que se puede colocar en
 * un mapa.
 */
public class Entity {
    private String id;
    private String name;
    private EntityType type;
    private int x;
    private int y;

    // Estadísticas (principalmente para enemigos)
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;
    private int speed;
    private int expReward;
    private int goldReward;

    // Diálogos (para NPCs)
    private List<String> dialogueLines;

    // Evento activado al interactuar
    private String eventId;

    // Tabla de botín (IDs de objetos que puede soltar el enemigo)
    private List<String> dropItemIds;

    // Ruta a una imagen personalizada para el sprite (PNG/GIF)
    private String imagePath;

    public Entity() {
        this.id = UUID.randomUUID().toString();
        this.name = "Nueva Entidad";
        this.type = EntityType.NPC;
        this.hp = 10;
        this.maxHp = 10;
        this.attack = 3;
        this.defense = 2;
        this.speed = 5;
        this.expReward = 5;
        this.goldReward = 3;
        this.dialogueLines = new ArrayList<>();
        this.dropItemIds = new ArrayList<>();
    }

    public Entity(String name, EntityType type, int x, int y) {
        this();
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
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

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getExpReward() {
        return expReward;
    }

    public void setExpReward(int expReward) {
        this.expReward = expReward;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public void setGoldReward(int goldReward) {
        this.goldReward = goldReward;
    }

    public List<String> getDialogueLines() {
        return dialogueLines;
    }

    public void setDialogueLines(List<String> dialogueLines) {
        this.dialogueLines = dialogueLines;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public List<String> getDropItemIds() {
        return dropItemIds;
    }

    public void setDropItemIds(List<String> dropItemIds) {
        this.dropItemIds = dropItemIds;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Entity copy() {
        Entity e = new Entity();
        e.id = this.id;
        e.name = this.name;
        e.type = this.type;
        e.x = this.x;
        e.y = this.y;
        e.hp = this.hp;
        e.maxHp = this.maxHp;
        e.attack = this.attack;
        e.defense = this.defense;
        e.speed = this.speed;
        e.expReward = this.expReward;
        e.goldReward = this.goldReward;
        e.dialogueLines = new ArrayList<>(this.dialogueLines);
        e.eventId = this.eventId;
        e.dropItemIds = new ArrayList<>(this.dropItemIds);
        e.imagePath = this.imagePath;
        return e;
    }

    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ")";
    }
}
