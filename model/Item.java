/** @author María Fortuna */

package com.rpgmaker.model;

import java.util.UUID;

/**
 * Representa un objeto que puede estar en el inventario, equiparse o usarse.
 */
public class Item {
    private String id;
    private String name;
    private String description;
    private ItemType type;

    // Modificadores de estadísticas (para equipo)
    private int attackBonus;
    private int defenseBonus;
    private int hpBonus;
    private int speedBonus;

    // Para consumibles
    private int healAmount;

    // Precio para tiendas
    private int buyPrice;
    private int sellPrice;

    // ¿Es apilable este objeto?
    private boolean stackable;

    public Item() {
        this.id = UUID.randomUUID().toString();
        this.name = "Nuevo Objeto";
        this.description = "";
        this.type = ItemType.CONSUMIBLE;
        this.stackable = true;
        this.buyPrice = 10;
        this.sellPrice = 5;
    }

    public Item(String name, ItemType type) {
        this();
        this.name = name;
        this.type = type;
        this.stackable = (type == ItemType.CONSUMIBLE || type == ItemType.OBJETO_CLAVE);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    public int getHpBonus() {
        return hpBonus;
    }

    public void setHpBonus(int hpBonus) {
        this.hpBonus = hpBonus;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }

    public void setSpeedBonus(int speedBonus) {
        this.speedBonus = speedBonus;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public void setHealAmount(int healAmount) {
        this.healAmount = healAmount;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    @Override
    public String toString() {
        return name + " (" + type.getDisplayName() + ")";
    }
}
