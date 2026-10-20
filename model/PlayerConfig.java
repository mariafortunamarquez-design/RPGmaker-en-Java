/** @author María Fortuna */

package com.rpgmaker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuración del personaje jugador.
 */
public class PlayerConfig {
    private String name;
    private int maxHp;
    private int attack;
    private int defense;
    private int speed;
    private int startingGold;
    private int startingLevel;

    // Posición inicial
    private String startingMapId;
    private int startX;
    private int startY;

    // Inventario inicial (IDs de objetos)
    private List<String> startingItemIds;

    public PlayerConfig() {
        this.name = "Héroe";
        this.maxHp = 100;
        this.attack = 10;
        this.defense = 5;
        this.speed = 8;
        this.startingGold = 50;
        this.startingLevel = 1;
        this.startX = 1;
        this.startY = 1;
        this.startingItemIds = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getStartingGold() {
        return startingGold;
    }

    public void setStartingGold(int startingGold) {
        this.startingGold = startingGold;
    }

    public int getStartingLevel() {
        return startingLevel;
    }

    public void setStartingLevel(int startingLevel) {
        this.startingLevel = startingLevel;
    }

    public String getStartingMapId() {
        return startingMapId;
    }

    public void setStartingMapId(String startingMapId) {
        this.startingMapId = startingMapId;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public List<String> getStartingItemIds() {
        return startingItemIds;
    }

    public void setStartingItemIds(List<String> startingItemIds) {
        this.startingItemIds = startingItemIds;
    }
}
